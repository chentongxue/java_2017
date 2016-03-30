package com.game.draco.app.camp.war;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.map.data.MapConfig;
import sacred.alliance.magic.base.ActiveType;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.MapLogicType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.ParasConstant;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.StringUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.camp.war.config.BattleRewardConfig;
import com.game.draco.app.camp.war.config.ConsequentKilledReward;
import com.game.draco.app.camp.war.config.LeaderBattle;
import com.game.draco.app.camp.war.config.LeaderBattleEffect;
import com.game.draco.app.camp.war.config.LeaderConfig;
import com.game.draco.app.camp.war.config.RoleBattleConfig;
import com.game.draco.app.camp.war.vo.ApplyGroup;
import com.game.draco.app.camp.war.vo.ApplyInfo;
import com.game.draco.app.camp.war.vo.CampMatchGroup;
import com.game.draco.app.camp.war.vo.LeaderBattleType;
import com.game.draco.app.camp.war.vo.MatchInfo;
import com.game.draco.app.camp.war.vo.RolePkResult;
import com.game.draco.app.camp.war.vo.RoleRewardResult;
import com.game.draco.app.camp.war.vo.RoleScore;
import com.game.draco.base.CampType;
import com.game.draco.message.item.CampWarLeaderInfoItem;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.response.C0352_CampWarPanelRespMessage;
import com.game.draco.message.response.C0354_CampWarLeaderBattleRespMessage;
import com.game.draco.message.response.C0357_CampWarLastResultRespMessage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;


public @Data class CampWarAppImpl implements CampWarApp,Service{
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final String wildcard_roleName = "${roleName}" ; //角色名
	private final String wildcard_campName = "${campName}" ; //阵营名
	private final String wildcard_targetRoleName = "${targetRoleName}" ; //目标角色名
	private final String wildcard_killedNum = "${killedNum}" ;//击杀数
	private final String wildcard_cureValue = "${cureValue}" ;//治疗值
	private final String wildcard_leaderName = "${leaderName}" ;//阵营领袖名
	private final String wildcard_targetLeaderName = "${targetLeaderName}" ;//目标领袖名
	private final int leader_pk_cycle = 2000 ; //首领pk周期
	/**
	 * 存放角色英雄的血百分比
	 */
	private Map<String,Map<Integer,Float>> roleHeroHpMap = Maps.newConcurrentMap();
	/**
	 * 玩家报名信息
	 */
	private Map<String,ApplyInfo> applyInfoMap = Maps.newConcurrentMap() ;
	/**
	 * 玩家成绩
	 */
	private Map<String,RoleScore> roleScoreMap = Maps.newConcurrentMap() ;
	
	/**
	 * 阵营报名组(4阵营)
	 */
	private ApplyGroup[] applyGroups = new ApplyGroup[] { new ApplyGroup(),
			new ApplyGroup(), new ApplyGroup(), new ApplyGroup() };
	/**
	 * 阵营匹配关系
	 */
	private CampMatchGroup campMatchGroup = new CampMatchGroup();
	
	private AtomicBoolean matchRunning = new AtomicBoolean(false);
	
	private Map<Byte,LeaderConfig> leaderConfigMap = null ;
	private Map<Byte,LeaderBattle> leaderBattleMap = null ;
	private Map<String,LeaderBattleEffect> leaderBattleEffectMap = null ;
	private RoleBattleConfig roleBattleConfig = null ;
	private BattleRewardConfig battleRewardConfig = null ;
	private Map<Integer,ConsequentKilledReward> killedRewardMap = null ;
	private Active campWarActive = null ;
	
	private LeaderBattle getLeaderBattle(byte battleType){
		return leaderBattleMap.get(battleType); 
	}
	
	private LeaderBattleEffect getLeaderBattleEffect(byte campId,byte battleType){
		String key = campId + "_" + battleType ;
		return leaderBattleEffectMap.get(key); 
	}
	
	private RoleScore getRoleScore(String roleId){
		RoleScore rc = this.roleScoreMap.get(roleId);
		if(null == rc){
			rc = new RoleScore();
			rc.setRoleId(roleId);
			this.roleScoreMap.put(roleId, rc);
		}
		return rc ;
	}
	
	@Override
	public int getLeaderConfigMaxHp(){
		Date now = new Date();
		Date lastLoginDate = DateUtil.addDayToDate(now, -this.roleBattleConfig.getLeaderDays());
		int roleNum = GameContext.getRoleDAO().countLateLoginRole(lastLoginDate) ;
		long maxHp = (long)roleNum * (long)this.roleBattleConfig.getLeaderHpRate() ;
		if(maxHp<=0 || maxHp > Integer.MAX_VALUE){
			return Integer.MAX_VALUE ;
		}
		return Math.max((int)maxHp, this.roleBattleConfig.getLeaderMinHp());
	}
	
	/**
	 * 判断胜负
	 */
	private void whoWin(){
		try {
			for (CampType ct : CampType.values()) {
				if (!ct.isRealCamp()) {
					continue;
				}
				byte campId = ct.getType();
				// 已经结束
				if (this.campMatchGroup.isEnd(campId)) {
					continue;
				}
				if (this.campMatchGroup.getHp(campId) <= 0) {
					this.battleEndEvent(campId);
					continue;
				}
			}
		}catch(Exception ex){
			logger.error("whoWin error",ex);
		}
	}
	
	private void battleEndEvent(int campId) {
		int hp = this.campMatchGroup.getHp(campId);
		int targetCampId = this.campMatchGroup.getTargetCampId(campId);
		int targetHp = this.getCampMatchGroup().getHp(targetCampId);
		boolean flag = (hp >= targetHp ) ;
		int winCampId = flag ? campId : targetCampId ;
		int failCampId = !flag ? campId : targetCampId ;
		for(RoleScore rs : this.roleScoreMap.values()){
			if(rs.getCampId() != winCampId && rs.getCampId() != failCampId ){
				continue ;
			}
			this.sendReward(rs, winCampId, failCampId);
		}
	}
	
	/**
	 * 启动匹配线程
	 */
	private void startMatchThread(){
		if(this.matchRunning.compareAndSet(false, true)){
			//活动开启
			this.onActiveStart();
			final int sleep = this.roleBattleConfig.getMatchCycle()*1000 ;
			final int max_times = sleep /this.leader_pk_cycle ;
			final AtomicInteger current_times = new AtomicInteger(0);
			Thread t = new Thread(new Runnable(){
				@Override
				public void run() {
					while(matchRunning.get()){
						//判断活动是否结束
						if(null != campWarActive && !campWarActive.isTimeOpen()){
							//停止当前匹配线程
							matchRunning.set(false);
							logger.info("stop thread: campwar-match-thread");
							break ;
						}
						//首领间相互pk
						try {
							leaderPkEvent();
						}catch(Exception ex){
							logger.error("leaderPkEvent error",ex);
						}
						//判断胜负是否已分
						whoWin();
						try {
							if(current_times.incrementAndGet() >= max_times){
								current_times.set(0);
								doApplyMatch();
							}
						}catch(Exception ex){
							logger.error("doApplyMatch error",ex);
						}
						try {
							Thread.sleep(leader_pk_cycle);
						} catch (Exception e) {
						}
					}
					//活动结束
					onActiveStop();
				}
			});
			t.setName("campwar-match-thread");
			t.start();
			logger.info("start thread: campwar-match-thread");
		}
	}
	
	private RoleInstance getAvailableRole(ApplyInfo applyInfo){
		if(applyInfo.isCancel()){
			return null  ;
		}
		return GameContext.getOnlineCenter().getRoleInstanceByRoleId(applyInfo.getRoleId());
	}
	
	/**
	 * 匹配逻辑
	 */
	private void doApplyMatch(){
		byte[] match = campMatchGroup.getMatch();
		Set<String> haveDo = Sets.newHashSet();
		for(byte campId = 0;campId<match.length;campId++){
			try {
				byte targetCampId = match[campId];
				String key = Math.min(campId, targetCampId) + "_"
						+ Math.max(campId, targetCampId);
				if (haveDo.contains(key)) {
					continue;
				}
				haveDo.add(key);
				// 两阵营相互1v1匹配
				ApplyGroup group1 = applyGroups[campId];
				ApplyGroup group2 = applyGroups[targetCampId];
				ApplyInfo apply1 = group1.peek();
				while (null != apply1) {
					RoleInstance role1 = this.getAvailableRole(apply1);
					if (null == role1) {
						// 移除
						group1.pop();
						this.removeApplyInfo(apply1.getRoleId());
						// 取下一个
						apply1 = group1.peek();
						continue;
					}
					// 取对方阵营的对手
					RoleInstance role2 = this.getAvailableRole(group2);
					if (null == role2) {
						break;
					}
					apply1 = group1.pop();
					//apply1,apply2 匹配成功
					MatchInfo matchInfo = MatchInfo.create(role1,apply1, role2,
							GameContext.getCampWarApp().getApplyInfo(role2.getRoleId()));
					//发生消息通知进入地图
					this.sendEnterArenaMap(matchInfo);
				}
				//匹配轮空
				for(ApplyInfo apply : group1.getApplyList()){
					if(!apply.isCancel()){
						this.notMatchEvent(apply.getRoleId());
					}
				}
				for(ApplyInfo apply : group2.getApplyList()){
					if(!apply.isCancel()){
						this.notMatchEvent(apply.getRoleId());
					}
				}
			}catch(Exception ex){
				logger.error("doApplyMatch error",ex);
			}
		}
		haveDo.clear();
		haveDo = null ;
	}
	
	private void sendEnterArenaMap(MatchInfo matchInfo) {
		try {
			// 给客户端发送进入地图消息
			matchInfo.getRole1().getBehavior()
					.changeMap(this.roleBattleConfig.getPoint1());
			matchInfo.getRole2().getBehavior()
					.changeMap(this.roleBattleConfig.getPoint2());
		} catch (Exception ex) {
			logger.error("camp war sendEnterArenaMap error",ex);
		}
	}
	
	private RoleInstance getAvailableRole(ApplyGroup group){
		while(true){
			ApplyInfo apply = group.pop();
			if(null == apply){
				return null ;
			}
			RoleInstance role = this.getAvailableRole(apply);
			if(null != role){
				return role ;
			}
			this.removeApplyInfo(apply.getRoleId());
		}
	}
	
	/**
	 * 清除玩家相关数据
	 */
	private void clearRoleData(){
		this.roleHeroHpMap.clear();
		this.applyInfoMap.clear();
		this.roleScoreMap.clear();
	}
	
	@Override
	public void initCampMatchGroup(){
		//阵营分组
		campMatchGroup.init();
	}
	
	private void onActiveStart(){
		//阵营分组
		campMatchGroup.init();
		this.clearRoleData();
	}
	
	private void onActiveStop(){
		this.whoWin();
		campMatchGroup.reset();
		this.clearRoleData();
	}
	
	private void sendReward(RoleScore score, int winCampId, int failCampId) {
		try {
			String strRoleId = String.valueOf(score.getRoleId());
			boolean sendResultPanel = (null != this.getApplyInfo(strRoleId));
			// 1.所有人都发邮件
			// 邮件
			boolean win = (score.getCampId() == winCampId);
			int goodsId = (win) ? this.battleRewardConfig.getLastWinGiftId()
					: this.battleRewardConfig.getLastFailGiftId();
			List<GoodsOperateBean> goodsList = Lists.newArrayList();
			String content = win ? this
					.getText(TextId.CampWar_Mail_Success_Content) : this
					.getText(TextId.CampWar_Mail_Failure_Content);
			goodsList.add(new GoodsOperateBean(goodsId, 1,
					BindingType.already_binding));
			GameContext.getMailApp()
					.sendMailAsync(score.getRoleId(),
							this.getText(TextId.CampWar_Mail_Title), content,
							this.getText(TextId.CampWar_Mail_Sender),
							OutputConsumeType.camp_war_last_reward.getType(),
							goodsList);
			// 2.只给在报名队列中的人发送活动结束面板
			if (!sendResultPanel) {
				return;
			}
			if (!GameContext.getOnlineCenter().isOnlineByRoleId(strRoleId)) {
				return;
			}
			// 发面板
			C0357_CampWarLastResultRespMessage respMsg = new C0357_CampWarLastResultRespMessage();
			respMsg.setWinCampId((byte) winCampId);
			respMsg.setFailCampId((byte) failCampId);
			respMsg.setCampPrestige(score.getGainPrestige());
			respMsg.setFailTimes((short) score.getTotalFailTimes());
			respMsg.setGameMoney(score.getGainGameMoney());
			respMsg.setMaxContinuousWinTimes((short) score.getMaxWinTimes());
			respMsg.setWinTimes((short) score.getTotalWinTimes());
			respMsg.setWinGift(this.battleRewardConfig.getWinGiftItem());
			respMsg.setFailGift(this.battleRewardConfig.getFailGiftItem());
			GameContext.getMessageCenter().sendByRoleId(null, strRoleId,
					respMsg);
		} catch (Exception ex) {
			logger.error(
					"campwar sendReward error,roleId=" + score.getRoleId(), ex);
		}
	}
	
	private void initCampInfo(){
		int campNum = CampType.getRealCampNum();
		//判断是否是>0 的偶数
		if(campNum <=0 || 0 != (campNum%2)){
			Log4jManager.CHECK.error("real campNum(" + campNum + ")  must greater than 0, and must an even number ");
			Log4jManager.checkFail();
			return ;
		}
		if(campNum != this.applyGroups.length){
			this.applyGroups = new ApplyGroup[campNum];
			for(int i=0;i<campNum;i++){
				applyGroups[i] = new ApplyGroup();
			}
		}
	}
	
	private void loadCampWarActive(){
		for(Active active : GameContext.getActiveApp().getAllActive()){
			if(ActiveType.CampWar.getType() == active.getType()){
				this.campWarActive = active ;
				break ;
			}
		}
		if (null == this.campWarActive) {
			Log4jManager.CHECK
					.error("can't get the campWarActive from activeApp,activeType="
							+ ActiveType.CampWar.getType());
			Log4jManager.checkFail();
		}
	}
	
	private void loadConsequentKilledReward(){
		String fileName = XlsSheetNameType.camp_war_consequent_killed_reward.getXlsName();
		String sheetName = XlsSheetNameType.camp_war_consequent_killed_reward
				.getSheetName();
		String path = GameContext.getPathConfig().getXlsPath();
		String file = path + fileName;
		Map<Integer, ConsequentKilledReward> configMap = XlsPojoUtil.sheetToGenericMap(file,
				sheetName, ConsequentKilledReward.class);
		if (Util.isEmpty(configMap)) {
			Log4jManager.CHECK.error("not config ConsequentKilledReward data,file="
					+ file + " sheet=" + sheetName);
			Log4jManager.checkFail();
			return;
		}
		int size = configMap.size();
		for(int i=0;i< size ;i++){
			//将前面没有配置全的添加上便于后面获取方便
			if(configMap.containsKey(i)){
				continue ;
			}
			ConsequentKilledReward reward = new ConsequentKilledReward();
			reward.setKilledNum(i);
			configMap.put(i, reward);
		}
		this.killedRewardMap = configMap ;
	
	}
	
	private void loadBattleRewardConfig(){
		String fileName = XlsSheetNameType.camp_war_battle_reward_config.getXlsName();
		String sheetName = XlsSheetNameType.camp_war_battle_reward_config
				.getSheetName();
		String path = GameContext.getPathConfig().getXlsPath();
		String file = path + fileName;
		BattleRewardConfig config = XlsPojoUtil.getEntity(file, sheetName, BattleRewardConfig.class);
		if(null == config){
			Log4jManager.CHECK.error("not config BattleRewardConfig data,file="
					+ file + " sheet=" + sheetName);
			Log4jManager.checkFail();
			return;
		}
		//检测物品信息
		GoodsLiteNamedItem winItem = this.getGoodsItem(config.getLastWinGiftId());
		GoodsLiteNamedItem failItem = this.getGoodsItem(config.getLastFailGiftId());
		if(null == winItem || null == failItem){
			Log4jManager.CHECK.error("BattleRewardConfig data lastWinGiftId or lastFailGiftId config error,file="
					+ file + " sheet=" + sheetName);
			Log4jManager.checkFail();
			return;
		}
		config.setWinGiftItem(winItem);
		config.setFailGiftItem(failItem);
		this.battleRewardConfig = config ;
	}
	
	private GoodsLiteNamedItem getGoodsItem(int goodsId){
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsId);
		if(null == gb){
			return null ;
		}
		GoodsLiteNamedItem item = gb.getGoodsLiteNamedItem();
		item.setBindType(BindingType.already_binding.getType());
		return item ;
	}
	
	private void loadRoleBattleConfig(){
		String fileName = XlsSheetNameType.camp_war_role_battle_config.getXlsName();
		String sheetName = XlsSheetNameType.camp_war_role_battle_config
				.getSheetName();
		String path = GameContext.getPathConfig().getXlsPath();
		String file = path + fileName;
		RoleBattleConfig config = XlsPojoUtil.getEntity(file, sheetName, RoleBattleConfig.class);
		if(null == config){
			Log4jManager.CHECK.error("not config RoleBattleConfig data,file="
					+ file + " sheet=" + sheetName);
			Log4jManager.checkFail();
			return;
		}
		//验证阵营地图是否正确
		String mapId = config.getRoleBattleMapId();
		MapConfig mapConfig = GameContext.getMapApp().getMapConfig(mapId);
		if(null == mapConfig){
			Log4jManager.CHECK.error("config RoleBattleConfig data error,mapId={} not exist! file={} sheet={}",mapId,file,sheetName);
			Log4jManager.checkFail();
			return ;
		}
		if(!mapConfig.changeLogicType(MapLogicType.campWar)){
			Log4jManager.CHECK.error("g RoleBattleConfig mapId={} logicType congfig error! file={} sheet={}",mapId,file,sheetName);
			Log4jManager.checkFail();
			return ;
		}
		this.roleBattleConfig = config ;
	}
	
	private void loadLeaderBattleEffect(){
		String fileName = XlsSheetNameType.camp_war_leader_battle_effect.getXlsName();
		String sheetName = XlsSheetNameType.camp_war_leader_battle_effect
				.getSheetName();
		String path = GameContext.getPathConfig().getXlsPath();
		String file = path + fileName;
		Map<String, LeaderBattleEffect> configMap = XlsPojoUtil.sheetToGenericMap(file,
				sheetName, LeaderBattleEffect.class);
		if (Util.isEmpty(configMap)) {
			Log4jManager.CHECK.error("not config leader battle  effect data,file="
					+ file + " sheet=" + sheetName);
			Log4jManager.checkFail();
			return;
		}
		this.leaderBattleEffectMap = configMap ;
	}
	
	private void loadLeaderBattle(){
		String fileName = XlsSheetNameType.camp_war_leader_battle.getXlsName();
		String sheetName = XlsSheetNameType.camp_war_leader_battle
				.getSheetName();
		String path = GameContext.getPathConfig().getXlsPath();
		String file = path + fileName;
		Map<Byte, LeaderBattle> configMap = XlsPojoUtil.sheetToGenericMap(file,
				sheetName, LeaderBattle.class);
		if (Util.isEmpty(configMap)) {
			Log4jManager.CHECK.error("not config leader battle data,file="
					+ file + " sheet=" + sheetName);
			Log4jManager.checkFail();
			return;
		}
		for(LeaderBattleType type : LeaderBattleType.values()){
			if(null == configMap.get(type.getType())){
				Log4jManager.CHECK.error("leader battle data config error,same battleType not config,file="
						+ file + " sheet=" + sheetName + " battleType=" + type.getType());
				Log4jManager.checkFail();
			}
		}
		this.leaderBattleMap = configMap ;
	}
	
	private void loadLeaderConfig() {
		String fileName = XlsSheetNameType.camp_war_leader_config.getXlsName();
		String sheetName = XlsSheetNameType.camp_war_leader_config
				.getSheetName();
		String path = GameContext.getPathConfig().getXlsPath();
		String file = path + fileName;
		Map<Byte, LeaderConfig> configMap = XlsPojoUtil.sheetToGenericMap(file,
				sheetName, LeaderConfig.class);
		if (Util.isEmpty(configMap)) {
			Log4jManager.CHECK.error("not config leader config data,file="
					+ file + " sheet=" + sheetName);
			Log4jManager.checkFail();
			return;
		}
		// 判断相关阵营是否都配置了相关数据
		for (CampType ct : CampType.values()) {
			if (!ct.isRealCamp()) {
				continue;
			}
			if (null == configMap.get(ct.getType())) {
				Log4jManager.CHECK
						.error("not config leader config data for campId= "
								+ ct.getType() + ",file=" + file + " sheet="
								+ sheetName);
				Log4jManager.checkFail();
			}
		}
		this.leaderConfigMap = configMap ;
	}


	@Override
	public Active getCampWarActive() {
		return this.campWarActive ; 
	}

	@Override
	public RoleBattleConfig getRoleBattleConfig() {
		return this.getRoleBattleConfig() ;
	}
	
	@Override
	public ApplyInfo getApplyInfo(String roleId) {
		return this.applyInfoMap.get(roleId);
	}

	@Override
	public Message getCampWarPanelMessage(RoleInstance role,boolean autoApply){
		byte campId = role.getCampId() ;
		C0352_CampWarPanelRespMessage respMsg = new C0352_CampWarPanelRespMessage();
		//TODO:
		//respMsg.setActiveRemainSecondTime(activeRemainSecondTime);
		//respMsg.setActiveStatus(activeStatus);
		ApplyInfo applyInfo = this.getApplyInfo(role.getRoleId());
		if(null != applyInfo){
			respMsg.setApplyStatus((byte)1);
			respMsg.setWaitMatchSecondTime((short)((System.currentTimeMillis()-applyInfo.getCreateDate())/1000));
		}else{
			respMsg.setApplyStatus((byte)0);
		}
		respMsg.setLeader1(this.getCampWarLeaderInfoItem(campId));
		respMsg.setLeader2(this.getCampWarLeaderInfoItem(this.campMatchGroup.getTargetCampId(campId)));
		respMsg.setAutoApply(autoApply?(byte)1:(byte)0);
		return respMsg ;
	}
	
	private CampWarLeaderInfoItem getCampWarLeaderInfoItem(byte campId){
		CampWarLeaderInfoItem item = new CampWarLeaderInfoItem();
		item.setCampId(campId);
		LeaderConfig config = this.leaderConfigMap.get(campId);
		if(null == config){
			return item ;
		}
		item.setCurHp(this.campMatchGroup.getHp(campId));
		item.setMaxHp(this.campMatchGroup.getMaxConfigHp());
		item.setName(config.getLeaderName());
		item.setResId(config.getLeaderResId());
		item.setResRate(config.getLeaderResRate());
		item.setLevel(config.getLeaderLevel());
		return item ;
	}
	
	private boolean isActiveOpen(){
		return null != this.campWarActive && this.campWarActive.isTimeOpen() ;
	}

	@Override
	public void removeApplyInfo(String roleId) {
		ApplyInfo apply = this.applyInfoMap.remove(roleId);
		if(null == apply){
			return ;
		}
		apply.setCancel(true);
	}
	
	private String getText(String textId){
		return GameContext.getI18n().getText(textId);
	}
	
	@Override
	public Result apply(RoleInstance role) {
		Result result = new Result();
		if(!this.isActiveOpen()){
			//时间未到
			result.setInfo(this.getText(TextId.CampWar_Not_Open_Now));
			return result ;
		}
		byte campId = role.getCampId();
		if(this.campMatchGroup.isGroupEnd(campId)){
			//活动已经结束
			result.setInfo(this.getText(TextId.CampWar_End_Now));
			return result ;
		}
		//触发匹配线程
		this.startMatchThread();
		//判断当前阵营是否提前分出了胜负
		if(!this.matchRunning.get()){
			//活动已经结束
			result.setInfo(this.getText(TextId.CampWar_End_Now));
			return result ;
		}
		//判断自己是否已经报名
		ApplyInfo applyInfo = this.getApplyInfo(role.getRoleId());
		if(null != applyInfo){
			result.setInfo(this.getText(TextId.CampWar_Already_Apply));
			return result ;
		}
		ApplyGroup group = this.applyGroups[campId];
		applyInfo = new ApplyInfo(role.getRoleId());
		applyInfo.setCampId(role.getCampId());
		this.applyInfoMap.put(role.getRoleId(), applyInfo);
		group.addApplyInfo(applyInfo);
		result.success();  
		result.setInfo(this.getText(TextId.CampWar_Apply_Success));
		return result;
	}

	@Override
	public Result cancel(RoleInstance role) {
		Result result = new Result();
		// 判断自己是否已经报名
		ApplyInfo applyInfo = this.getApplyInfo(role.getRoleId());
		if (null == applyInfo) {
			result.setInfo(this.getText(TextId.CampWar_Cancel_Not_Apply));
			return result;
		}
		//判断是否已经匹配成功
		if(null != applyInfo.getMatch()){
			result.setInfo(this.getText(TextId.CampWar_Cancel_Have_Match));
			return result;
		}
		applyInfo.setCancel(true);
		result.success(); 
		result.setInfo(this.getText(TextId.CampWar_Cancel_Success));
		return result;
	}

	@Override
	public Float getHeroHpRate(String roleId,int heroId) {
		Map<Integer,Float> hp = this.roleHeroHpMap.get(roleId);
		if(null == hp){
			return null ;
		}
		return hp.get(heroId);
	}

	@Override
	public void addHeroHpRate(String roleId,int heroId,Float rate) {
		Map<Integer,Float> hp = this.roleHeroHpMap.get(roleId);
		if(null == hp){
			hp = Maps.newHashMap();
			this.roleHeroHpMap.put(roleId, hp);
		}
		hp.put(heroId, rate);
	}

	@Override
	public void clearAllHeroHpRate(String roleId) {
		this.roleHeroHpMap.remove(roleId);
	}


	@Override
	public void setArgs(Object arg0) {
		
	}


	@Override
	public void stop() {
		
	}
	
	@Override
	public void start() {
		this.initCampInfo();
		this.loadCampWarActive();
		this.loadLeaderConfig();
		this.loadLeaderBattle();
		this.loadLeaderBattleEffect();
		this.loadRoleBattleConfig();
		this.loadBattleRewardConfig();
		this.loadConsequentKilledReward();
	}
	
	/**
	 * 阵营首领pk
	 */
	private void leaderPkEvent(){
		LeaderBattle battle = this
				.getLeaderBattle(LeaderBattleType.system.getType());
		if(null == battle || 0 >= battle.getHurt()){
			return ;
		}
		for(CampType ct : CampType.values()){
			if(!ct.isRealCamp()){
				continue ;
			}
			byte campId = ct.getType() ;
			//已经结束
			if(this.campMatchGroup.isEnd(campId)){
				continue ;
			}
			LeaderBattleEffect effect = this.getLeaderBattleEffect(campId,
					LeaderBattleType.continuous_Kill.getType());
			if (null == effect) {
				continue ;
			}
			this.campMatchGroup.reduTargetHp(campId, battle.getHurt());
			CampType campType = CampType.get(campId);
			String info = this.replace(battle.getReportInfo(),
					this.wildcard_campName,campType.getName(),
					this.wildcard_leaderName,this.getLeaderName(campId),
					this.wildcard_targetLeaderName,this.getTargetLeaderName(campId)
					) ;
			this.broadcastLeaderBattle(campId, effect, 0, battle.getHurt(), info);
		}
	}
	
	private int killEventWin(AbstractRole attacker, AbstractRole victim) {
		RoleScore winScore = this.getRoleScore(attacker.getRoleId());
		// 胜利者连杀数+1
		// 胜利者总胜利次数+1
		int killNum = winScore.getKillNum() + 1 ;
		winScore.setKillNum(killNum);
		winScore.setMaxWinTimes(Math.max(winScore.getMaxWinTimes(),
				winScore.getKillNum()));
		winScore.setTotalWinTimes(winScore.getTotalWinTimes() + 1);
		byte campId = attacker.getCampId();
		// 连杀事件
		LeaderBattle battle = this
				.getLeaderBattle(LeaderBattleType.continuous_Kill.getType());
		if (null == battle 
				|| 0 <= battle.getParameter()
				|| 0 == winScore.getKillNum()%battle.getParameter()) {
			return killNum ;
		}
		this.campMatchGroup.reduTargetHp(campId, battle.getHurt());
		// 构建消息
		LeaderBattleEffect effect = this.getLeaderBattleEffect(campId,
				LeaderBattleType.continuous_Kill.getType());
		if (null == effect) {
			return killNum ;
		}
		// 替换通配符
		CampType campType = CampType.get(campId);
		String info = this.replace(battle.getReportInfo(),
				this.wildcard_campName,campType.getName(),
				this.wildcard_roleName,attacker.getRoleName(),
				this.wildcard_leaderName,this.getLeaderName(campId),
				this.wildcard_targetLeaderName,this.getTargetLeaderName(campId),
				this.wildcard_killedNum,String.valueOf(winScore.getKillNum())
				) ;
		this.broadcastLeaderBattle(campId, effect, 0, -battle.getHurt(), info);
		return killNum ;
	}
	
	private int killEventFail(AbstractRole attacker, AbstractRole victim) {
		// 失败者最大连杀数=0
		// 失败者总失败次数+1
		RoleScore failScore = this.getRoleScore(victim.getRoleId());
		int killNum = failScore.getKillNum();
		failScore.setKillNum(0);
		failScore.setTotalFailTimes(failScore.getTotalFailTimes() + 1);
		if (killNum < 2) {
			return killNum ;
		}
		LeaderBattle battle = this
				.getLeaderBattle(LeaderBattleType.interrupt_kill.getType());
		if (null == battle || battle.getParameter() <= 0) {
			return killNum ;
		}
		byte campId = attacker.getCampId();
		int cureValue = battle.getParameter() * killNum;
		//+hp
		this.campMatchGroup.addHp(campId, cureValue);
		// 构建消息
		LeaderBattleEffect effect = this.getLeaderBattleEffect(campId,
				LeaderBattleType.interrupt_kill.getType());
		if (null == effect) {
			return killNum ;
		}
		// 替换通配符
		CampType campType = CampType.get(campId);
		String info = this.replace(battle.getReportInfo(),
				this.wildcard_campName,campType.getName(),
				this.wildcard_roleName,attacker.getRoleName(),
				this.wildcard_leaderName,this.getLeaderName(campId),
				this.wildcard_killedNum,String.valueOf(killNum),
				this.wildcard_targetRoleName,victim.getRoleName(),
				this.wildcard_cureValue,String.valueOf(cureValue)
				) ;
		this.broadcastLeaderBattle(campId, effect, cureValue, 0, info);
		return killNum ;
	}
	
	private String replace(String info,String ... args){
		if(null == info || null == args){
			return info ;
		}
		for(int i=0;i<args.length;i=i+2){
			info = StringUtil.replace(info, args[i],args[i+1]);
		}
		return info ;
	}
	
	
	private void campPrestigeEvent(AbstractRole role,int prestige){
		RoleScore score = this.getRoleScore(role.getRoleId());
		int remainPrestige = score.getRemainPrestige().addAndGet(prestige) ;
		LeaderBattle battle = this.getLeaderBattle(LeaderBattleType.camp_prestige.getType());
		if(null == battle){
			return ;
		}
		int parameter = battle.getParameter() ;
		if (parameter <= 0){
			return ;
		}
		//次数
		int times = remainPrestige/parameter ;
		if(times <=0){
			return ;
		}
		int redu = parameter * times ;
		score.getRemainPrestige().addAndGet(-redu);
		int hurt = battle.getHurt() * times ;
		byte campId = role.getCampId() ;
		//减少对方首领的hp
		this.campMatchGroup.reduTargetHp(campId, hurt);
		//广播
		//构建消息
		LeaderBattleEffect effect = this.getLeaderBattleEffect(campId, LeaderBattleType.camp_prestige.getType());
		if(null == effect){
			return ;
		}
		//替换通配符
		CampType campType = CampType.get(campId);
		String info = this.replace(battle.getReportInfo(),
				this.wildcard_campName,campType.getName(),
				this.wildcard_roleName,role.getRoleName(),
				this.wildcard_leaderName,this.getLeaderName(campId),
				this.wildcard_targetLeaderName,this.getTargetLeaderName(campId)
				) ;
		for(int i=0;i<times;i++){
			this.broadcastLeaderBattle(campId, effect, 0, -battle.getHurt(), info);
		}
		return ;
	}
	

	private void broadcastLeaderBattle(byte campId, LeaderBattleEffect effect,
			int attackerHpChange, int defenseHpChange, String info) {
		C0354_CampWarLeaderBattleRespMessage msg = new C0354_CampWarLeaderBattleRespMessage();
		msg.setAttackerCampId(campId);
		msg.setAttackerAnimationId(effect.getAnimationId());
		msg.setAttackerEffectId(effect.getAttackerEffectId());
		msg.setAttackerCurHp(this.campMatchGroup.getHp(campId));
		msg.setAttackerHpChange((short) attackerHpChange);
		msg.setDefenseEffectId(effect.getDefenseEffectId());
		msg.setDefenseHpChannge((short) defenseHpChange);
		msg.setDefenseCurHp(this.campMatchGroup.getTargetHp(campId));
		msg.setInfo(info);
		byte targetCampId = this.campMatchGroup.getTargetCampId(campId);
		for (ApplyInfo apply : applyInfoMap.values()) {
			//只广播和自己阵营相关的
			if(campId == apply.getCampId() 
					|| targetCampId == apply.getCampId()){
				GameContext.getMessageCenter().sendByRoleId("", apply.getRoleId(),
						msg);
			}
		}
	}
	
	private String getLeaderName(byte campId) {
		LeaderConfig lc = this.leaderConfigMap.get(campId);
		if(null == lc){
			return "" ;
		}
		return lc.getLeaderName() ;
	}
	
	private String getTargetLeaderName(byte campId){
		byte targetCampId = this.campMatchGroup.getTargetCampId(campId);
		return this.getLeaderName(targetCampId);
	}
	
	@Override
	public List<RoleRewardResult> roleBattleEnd(MatchInfo match,
			AbstractRole winRole, AbstractRole failRole) {
		List<RoleRewardResult> list = Lists.newArrayList();
		// 获得需要奖励的阵营声望与游戏币
		if (null == winRole || null == failRole) {
			// 轮空,不计算连杀,只给轮空奖励
			list.add(this.roleReward(match.getRole1(), false, 0, 0,RolePkResult.nothing));
			list.add(this.roleReward(match.getRole2(), false, 0, 0,RolePkResult.nothing));
		} else {
			int killNum = this.killEventWin(winRole, failRole);
			int interruptKill = this.killEventFail(winRole, failRole);
			list.add(this.roleReward(winRole, true, killNum, interruptKill,RolePkResult.success));
			list.add(this.roleReward(failRole, false, 0, 0,RolePkResult.fail));
		}
		// 声望事件
		for (RoleRewardResult result : list) {
			this.campPrestigeEvent(
					result.getRoleId().equals(winRole.getRoleId()) ? winRole
							: failRole, result.getAddPrestige());
		}
		return list;
	}
	
	private ConsequentKilledReward getConsequentKilledReward(int killNum){
		int size = this.killedRewardMap.size();
		if(killNum > size){
			killNum = size ;
		}
		return this.killedRewardMap.get(killNum) ;
	}
	
	private void notMatchEvent(String roleId){
		try {
			RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleId);
			if(null == role){
				return ;
			}
			RoleScore score = this.getRoleScore(role.getRoleId());
			RoleRewardResult result = this.roleReward(role, true,
					score.getKillNum(), 0, RolePkResult.nothing);
			this.campPrestigeEvent(role, result.getEffectAddPrestige());
		}catch(Exception ex){
			logger.error("notMathcEvent error",ex);
		}
	}
	
	private RoleRewardResult roleReward(AbstractRole role,boolean win,int killNum,int interruptKill,RolePkResult pkType){
		String roleId = role.getRoleId() ;
		RoleRewardResult result = new RoleRewardResult();
		result.setRoleId(roleId);
		result.setPkStatus(pkType.getType());
		
		RoleScore score = this.getRoleScore(roleId);
		int prestige = 0 ;
		int gameMoney = 0 ;
		if(win){
			prestige = this.battleRewardConfig.getWinCampPrestige();
			gameMoney = this.battleRewardConfig.getWinGameMoney() ;
			//计算额外的奖励
			ConsequentKilledReward killReward = this.getConsequentKilledReward(killNum);
			if(null != killReward){
				prestige += killReward.getCampPrestige() ;
				gameMoney += killReward.getGameMoney() ;
			}
			if (interruptKill >= 2) {
				prestige += (int) ((interruptKill - 1)
						* this.battleRewardConfig.getInterruptPrestigeModulus() / ParasConstant.PERCENT_BASE_VALUE);
				gameMoney += (int) ((interruptKill - 1)
						* this.battleRewardConfig.getInterruptGameMoneyModulus() / ParasConstant.PERCENT_BASE_VALUE);
			}
		}else {
			//失败
			prestige = this.battleRewardConfig.getFailCampPrestige();
			gameMoney = this.battleRewardConfig.getFailGameMoney() ;
		}
		int oldPrestige = score.getGainPrestige() ;
		int newPrestige = Math.max(oldPrestige +  prestige, this.battleRewardConfig.getMaxCampPrestige());
		score.setGainPrestige(newPrestige);
		int effectAddPrestige = newPrestige - oldPrestige ;
		result.setAddPrestige(prestige);
		result.setEffectAddPrestige(effectAddPrestige);
		result.setGameMoney(gameMoney);
		result.setWinTimes(score.getMaxWinTimes());
		try {
			if (GameContext.getOnlineCenter().isOnlineByRoleId(roleId)) {
				GameContext.getUserAttributeApp().changeAttribute(role,
						AttributeType.campPrestige, OperatorType.Add,
						effectAddPrestige, OutputConsumeType.camp_war_reward);
				GameContext.getUserAttributeApp().changeAttribute(role,
						AttributeType.silverMoney, OperatorType.Add, gameMoney,
						OutputConsumeType.camp_war_reward);
				role.getBehavior().notifyAttribute();
			}
		}catch(Exception ex){
			logger.error("",ex);
		}
		return result ;
	}
	
}

