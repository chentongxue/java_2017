package com.game.draco.app.camp.war;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import sacred.alliance.magic.channel.EmptyChannelSession;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.ParasConstant;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.core.channel.ChannelSession;
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
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.base.CampType;
import com.game.draco.message.internal.C0085_CampWarAutoApplyInternalMessage;
import com.game.draco.message.internal.C0087_CampWarMatchInternalMessage;
import com.game.draco.message.item.CampWarLeaderBattleHpItem;
import com.game.draco.message.item.CampWarLeaderInfoItem;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.response.C0352_CampWarPanelRespMessage;
import com.game.draco.message.response.C0354_CampWarLeaderBattleRespMessage;
import com.game.draco.message.response.C0357_CampWarLastResultRespMessage;
import com.game.draco.message.response.C0358_CampWarLeaderTipsRespMessage;
import com.game.draco.message.response.C0359_CampWarRoleTipsRespMessage;
import com.game.draco.message.response.C0360_CampWarLeaderMaxHpRespMessage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public @Data
class CampWarAppImpl implements CampWarApp, Service {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final int FULL_HP_RATE = 10000 ;
	private final static ChannelSession emptyChannelSession = new EmptyChannelSession();

	private final String wildcard_roleName = "${roleName}"; // 角色名
	private final String wildcard_campName = "${campName}"; // 阵营名
	private final String wildcard_targetRoleName = "${targetRoleName}"; // 目标角色名
	private final String wildcard_killedNum = "${killedNum}";// 击杀数
	private final String wildcard_cureValue = "${cureValue}";// 治疗值
	private final String wildcard_leaderName = "${leaderName}";// 阵营领袖名
	// private final String wildcard_targetLeaderName = "${targetLeaderName}"
	// ;//目标领袖名
	private final int leader_pk_cycle = 2000; // 首领pk周期
	/**
	 * 存放角色英雄的血百分比
	 */
	private Map<String, Map<Integer, Float>> roleHeroHpMap = Maps
			.newConcurrentMap();
	/**
	 * 玩家报名信息
	 */
	private Map<String, ApplyInfo> applyInfoMap = Maps.newConcurrentMap();
	/**
	 * 玩家成绩
	 */
	private Map<String, RoleScore> roleScoreMap = Maps.newConcurrentMap();

	private Map<String, Byte> panelRoleMap = Maps.newConcurrentMap();

	/**
	 * 阵营报名组(4阵营)
	 */
	private ApplyGroup[] applyGroups = new ApplyGroup[] { new ApplyGroup(),
			new ApplyGroup(), new ApplyGroup(), new ApplyGroup() };

	/**
	 * 总的匹配组
	 */
	private ApplyGroup totalApplyGroup = new ApplyGroup();
	/**
	 * 阵营匹配关系
	 */
	private CampMatchGroup campMatchGroup = new CampMatchGroup();

	private Map<Byte, LeaderConfig> leaderConfigMap = null;
	private Map<Byte, LeaderBattle> leaderBattleMap = null;
	private Map<String, LeaderBattleEffect> leaderBattleEffectMap = null;
	private RoleBattleConfig roleBattleConfig = null;
	private BattleRewardConfig battleRewardConfig = null;
	private Map<Integer, ConsequentKilledReward> killedRewardMap = null;
	private Active campWarActive = null;

	private void addPanelRole(RoleInstance role) {
		this.panelRoleMap.put(role.getRoleId(), role.getCampId());
	}

	@Override
	public void removePanelRole(RoleInstance role) {
		this.panelRoleMap.remove(role.getRoleId());
	}

	private LeaderBattle getLeaderBattle(byte battleType) {
		return leaderBattleMap.get(battleType);
	}

	private LeaderBattleEffect getLeaderBattleEffect(byte campId,
			byte battleType) {
		String key = campId + "_" + battleType;
		return leaderBattleEffectMap.get(key);
	}

	private RoleScore getRoleScore(String roleId) {
		RoleScore rc = this.roleScoreMap.get(roleId);
		if (null == rc) {
			rc = new RoleScore();
			rc.setRoleId(roleId);
			this.roleScoreMap.put(roleId, rc);
		}
		return rc;
	}

	@Override
	public int getLeaderConfigMaxHp() {
		Date now = new Date();
		Date lastLoginDate = DateUtil.addDayToDate(now,
				-this.roleBattleConfig.getLeaderDays());
		int roleNum = GameContext.getRoleDAO()
				.countLateLoginRole(lastLoginDate);
		long maxHp = (long) roleNum
				* (long) this.roleBattleConfig.getLeaderHpRate();
		if (maxHp <= 0 || maxHp > Integer.MAX_VALUE) {
			return Integer.MAX_VALUE;
		}
		return Math.max((int) maxHp, this.roleBattleConfig.getLeaderMinHp());
	}

	/**
	 * 判断胜负
	 */
	private byte whichCampWin(boolean timeover) {
		return this.campMatchGroup.whichCampWin(timeover);
	}

	private void battleEndEvent(int winCampId) {
		for (RoleScore rs : this.roleScoreMap.values()) {
			this.sendReward(rs, winCampId);
		}
	}

	private void dealCampMatchGroup() {
		if (null == this.campMatchGroup) {
			this.campMatchGroup = new CampMatchGroup();
			return;
		}
		if (this.campMatchGroup.isOver()) {
			// 需要判断是否同一天
			if (DateUtil.sameDay(campMatchGroup.getCreateOn(), new Date())) {
				return;
			}
			// 不是同一天重新创建
			this.campMatchGroup = new CampMatchGroup();
			return;
		}
	}

	/**
	 * 启动匹配线程
	 */
	private void startMatchThread() {
		this.dealCampMatchGroup();
		if (!campMatchGroup.toStart()) {
			return;
		}
		// 活动开启
		this.onActiveStart();
		final int sleep = this.roleBattleConfig.getMatchCycle() * 1000;
		final int max_times = sleep / this.leader_pk_cycle;
		final AtomicInteger current_times = new AtomicInteger(0);
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				while (campMatchGroup.isRun()) {
					// 判断活动是否结束
					if (null != campWarActive && !campWarActive.isTimeOpen()) {
						// 停止当前匹配线程
						campMatchGroup.over();
						logger.info("stop thread(time over): campwar-match-thread");
						break;
					}
					// 首领间相互pk
					
					try {
						leaderPkEvent();
					} catch (Exception ex) {
						logger.error("leaderPkEvent error", ex);
					}
					 
					// 判断胜负是否已分
					if (-1 != whichCampWin(false)) {
						// 胜负已分
						campMatchGroup.over();
						logger.info("stop thread(sameone win): campwar-match-thread");
						break;
					}
					try {
						if (current_times.incrementAndGet() >= max_times) {
							current_times.set(0);
							sendApplyMatchReqMessage();
						}
					} catch (Exception ex) {
						logger.error("doApplyMatch error", ex);
					}
					try {
						Thread.sleep(leader_pk_cycle);
					} catch (Exception e) {
					}
				}
				// 活动结束
				onActiveStop();
			}
		});
		t.setName("campwar-match-thread");
		t.start();
		logger.info("start thread: campwar-match-thread");
	}

	private RoleInstance getAvailableRole(ApplyInfo applyInfo) {
		if (applyInfo.isCancel()) {
			return null;
		}
		return GameContext.getOnlineCenter().getRoleInstanceByRoleId(
				applyInfo.getRoleId());
	}

	private ApplyGroup getMaxApplyGroup(int campId) {
		ApplyGroup max = null;
		for (int i = 0; i < applyGroups.length; i++) {
			if (campId == i) {
				continue;
			}
			if (null == max) {
				max = applyGroups[i];
				continue;
			}
			if (max.size() < applyGroups[i].size()) {
				max = applyGroups[i];
			}
		}
		return max;
	}

	private ApplyInfo getEffectTarget(int campId) {
		for (int i = 0; i < this.applyGroups.length - 1; i++) {
			ApplyGroup maxGroup = this.getMaxApplyGroup(campId);
			if (null == maxGroup) {
				return null;
			}
			ApplyInfo info = maxGroup.popEffect();
			if (null != info) {
				return info;
			}
		}
		return null;
	}

	private void sendApplyMatchReqMessage() {
		C0087_CampWarMatchInternalMessage reqMsg = new C0087_CampWarMatchInternalMessage();
		GameContext.getUserSocketChannelEventPublisher().publish(null, reqMsg,
				emptyChannelSession);
	}

	/**
	 * 匹配逻辑
	 */
	@Override
	public void doApplyMatch() {
		while (true) {
			ApplyInfo applyInfo = this.totalApplyGroup.peek();
			if (null == applyInfo) {
				break;
			}
			if (applyInfo.isCancel()) {
				this.totalApplyGroup.pop();
				continue;
			}
			// 获得其他阵营报名人数最多的
			ApplyInfo target = this.getEffectTarget(applyInfo.getCampId());
			if (null == target) {
				break;
			}
			// 匹配成功
			totalApplyGroup.pop();
			// apply1,apply2 匹配成功
			MatchInfo matchInfo = MatchInfo.create(
					this.getAvailableRole(applyInfo), applyInfo,
					this.getAvailableRole(target), target);
			// 发生消息通知进入地图
			this.sendEnterArenaMap(matchInfo);
		}
		// 未匹配成功的
		for (int i = 0; i < this.applyGroups.length; i++) {
			List<ApplyInfo> list = this.applyGroups[i].getApplyList() ;
			if(Util.isEmpty(list)){
				continue ;
			}
			for (ApplyInfo apply : list ) {
				if (apply.isCancel()) {
					continue;
				}
				this.notMatchEvent(apply.getRoleId());
			}
		}
	}

	private void sendEnterArenaMap(MatchInfo matchInfo) {
		try {
			// 给客户端发送进入地图消息
			matchInfo.getRole1().getBehavior()
					.changeMap(this.roleBattleConfig.getPoint1());
			matchInfo.getRole2().getBehavior()
					.changeMap(this.roleBattleConfig.getPoint2());
		} catch (Exception ex) {
			logger.error("camp war sendEnterArenaMap error", ex);
		}
	}

	/**
	 * 清除玩家相关数据
	 */
	private void clearRoleData() {
		this.roleHeroHpMap.clear();
		this.applyInfoMap.clear();
		this.roleScoreMap.clear();
	}

	@Override
	public void initCampMatchGroup() {
		campMatchGroup.init(this.getLeaderConfigMaxHp());
	}

	private void onActiveStart() {
		int maxHp = this.getLeaderConfigMaxHp() ;
		campMatchGroup.init(maxHp);
		//广播血量变化的消息
		try {
			C0360_CampWarLeaderMaxHpRespMessage notifyMsg = new C0360_CampWarLeaderMaxHpRespMessage();
			notifyMsg.setMaxHp(maxHp);
			for (Iterator<Map.Entry<String, Byte>> it = this.panelRoleMap
					.entrySet().iterator(); it.hasNext();) {
				Map.Entry<String, Byte> entry = it.next();
				GameContext.getMessageCenter().sendByRoleId("", entry.getKey(),
						notifyMsg);
			}
		}catch(Exception ex){
			logger.error("",ex);
		}
		this.clearRoleData();
	}

	private void onActiveStop() {
		byte winCampId = this.whichCampWin(true);
		campMatchGroup.over();
		// 发奖
		this.battleEndEvent(winCampId);
		this.clearRoleData();
	}

	private void sendReward(RoleScore score, int winCampId) {
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

	private void initCampInfo() {
		int campNum = CampType.getRealCampNum();
		if (campNum != this.applyGroups.length) {
			this.applyGroups = new ApplyGroup[campNum];
			for (int i = 0; i < campNum; i++) {
				applyGroups[i] = new ApplyGroup();
			}
		}
	}

	private void loadCampWarActive() {
		for (Active active : GameContext.getActiveApp().getAllActive()) {
			if (ActiveType.CampWar.getType() == active.getType()) {
				this.campWarActive = active;
				break;
			}
		}

		if (null == this.campWarActive) {
			Log4jManager.CHECK
					.error("can't get the campWarActive from activeApp,activeType="
							+ ActiveType.CampWar.getType());
			Log4jManager.checkFail();
			return;
		}
		// 阵营战有且只有一个开启/结束时间段
		List<String> times = this.campWarActive.getTimes();
		if (Util.isEmpty(times) || 1 != times.size()) {
			Log4jManager.CHECK.error("the campwar active:"
					+ this.campWarActive.getId()
					+ " timeRange config error,timeRange size must == 1");
			Log4jManager.checkFail();
		}
	}

	private void loadConsequentKilledReward() {
		String fileName = XlsSheetNameType.camp_war_consequent_killed_reward
				.getXlsName();
		String sheetName = XlsSheetNameType.camp_war_consequent_killed_reward
				.getSheetName();
		String path = GameContext.getPathConfig().getXlsPath();
		String file = path + fileName;
		Map<Integer, ConsequentKilledReward> configMap = XlsPojoUtil
				.sheetToGenericMap(file, sheetName,
						ConsequentKilledReward.class);
		if (Util.isEmpty(configMap)) {
			Log4jManager.CHECK
					.error("not config ConsequentKilledReward data,file="
							+ file + " sheet=" + sheetName);
			Log4jManager.checkFail();
			return;
		}
		int size = configMap.size();
		for (int i = 0; i < size; i++) {
			// 将前面没有配置全的添加上便于后面获取方便
			if (configMap.containsKey(i)) {
				continue;
			}
			ConsequentKilledReward reward = new ConsequentKilledReward();
			reward.setKilledNum(i);
			configMap.put(i, reward);
		}
		this.killedRewardMap = configMap;

	}

	private void loadBattleRewardConfig() {
		String fileName = XlsSheetNameType.camp_war_battle_reward_config
				.getXlsName();
		String sheetName = XlsSheetNameType.camp_war_battle_reward_config
				.getSheetName();
		String path = GameContext.getPathConfig().getXlsPath();
		String file = path + fileName;
		BattleRewardConfig config = XlsPojoUtil.getEntity(file, sheetName,
				BattleRewardConfig.class);
		if (null == config) {
			Log4jManager.CHECK.error("not config BattleRewardConfig data,file="
					+ file + " sheet=" + sheetName);
			Log4jManager.checkFail();
			return;
		}
		// 检测物品信息
		GoodsLiteNamedItem winItem = this.getGoodsItem(config
				.getLastWinGiftId());
		GoodsLiteNamedItem failItem = this.getGoodsItem(config
				.getLastFailGiftId());
		if (null == winItem || null == failItem) {
			Log4jManager.CHECK
					.error("BattleRewardConfig data lastWinGiftId or lastFailGiftId config error,file="
							+ file + " sheet=" + sheetName);
			Log4jManager.checkFail();
			return;
		}
		config.setWinGiftItem(winItem);
		config.setFailGiftItem(failItem);
		this.battleRewardConfig = config;
	}

	private GoodsLiteNamedItem getGoodsItem(int goodsId) {
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsId);
		if (null == gb) {
			return null;
		}
		GoodsLiteNamedItem item = gb.getGoodsLiteNamedItem();
		item.setBindType(BindingType.already_binding.getType());
		return item;
	}

	private void loadRoleBattleConfig() {
		String fileName = XlsSheetNameType.camp_war_role_battle_config
				.getXlsName();
		String sheetName = XlsSheetNameType.camp_war_role_battle_config
				.getSheetName();
		String path = GameContext.getPathConfig().getXlsPath();
		String file = path + fileName;
		RoleBattleConfig config = XlsPojoUtil.getEntity(file, sheetName,
				RoleBattleConfig.class);
		if (null == config) {
			Log4jManager.CHECK.error("not config RoleBattleConfig data,file="
					+ file + " sheet=" + sheetName);
			Log4jManager.checkFail();
			return;
		}
		// 验证阵营地图是否正确
		String mapId = config.getRoleBattleMapId();
		MapConfig mapConfig = GameContext.getMapApp().getMapConfig(mapId);
		if (null == mapConfig) {
			Log4jManager.CHECK
					.error("config RoleBattleConfig data error,mapId={} not exist! file={} sheet={}",
							mapId, file, sheetName);
			Log4jManager.checkFail();
			return;
		}
		if (!mapConfig.changeLogicType(MapLogicType.campWar)) {
			Log4jManager.CHECK
					.error("g RoleBattleConfig mapId={} logicType congfig error! file={} sheet={}",
							mapId, file, sheetName);
			Log4jManager.checkFail();
			return;
		}
		this.roleBattleConfig = config;
	}

	private void loadLeaderBattleEffect() {
		String fileName = XlsSheetNameType.camp_war_leader_battle_effect
				.getXlsName();
		String sheetName = XlsSheetNameType.camp_war_leader_battle_effect
				.getSheetName();
		String path = GameContext.getPathConfig().getXlsPath();
		String file = path + fileName;
		Map<String, LeaderBattleEffect> configMap = XlsPojoUtil
				.sheetToGenericMap(file, sheetName, LeaderBattleEffect.class);
		if (Util.isEmpty(configMap)) {
			Log4jManager.CHECK
					.error("not config leader battle  effect data,file=" + file
							+ " sheet=" + sheetName);
			Log4jManager.checkFail();
			return;
		}
		this.leaderBattleEffectMap = configMap;
	}

	private void loadLeaderBattle() {
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
		for (LeaderBattleType type : LeaderBattleType.values()) {
			if (null == configMap.get(type.getType())) {
				Log4jManager.CHECK
						.error("leader battle data config error,same battleType not config,file="
								+ file
								+ " sheet="
								+ sheetName
								+ " battleType="
								+ type.getType());
				Log4jManager.checkFail();
			}
		}
		this.leaderBattleMap = configMap;
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
		this.leaderConfigMap = configMap;
	}

	@Override
	public Active getCampWarActive() {
		return this.campWarActive;
	}

	@Override
	public RoleBattleConfig getRoleBattleConfig() {
		return this.roleBattleConfig;
	}

	@Override
	public ApplyInfo getApplyInfo(String roleId) {
		return this.applyInfoMap.get(roleId);
	}
	
	private String[] getActiveTimePoint(){
		List<String> times = this.campWarActive.getTimes() ;
		if(Util.isEmpty(times)){
			return null ;
		}
		String time = times.get(0);
		if(Util.isEmpty(time)){
			return null ;
		}
		String[] ss = time.split(Cat.strigula);
		if(null == ss || 2 != ss.length){
			return null ;
		}
		return ss ;
	}

	@Override
	public Message getCampWarPanelMessage(RoleInstance role, boolean autoApply) {
		C0352_CampWarPanelRespMessage respMsg = new C0352_CampWarPanelRespMessage();
		boolean isOpenNow = this.isActiveOpen() ;
		if(isOpenNow && this.campMatchGroup.getLiveNum()<=1){
			//已经提前结束
			respMsg.setActiveStatus((byte)0);
			respMsg.setActiveRemainTimeTips(this.getText(TextId.CampWar_Active_End));
		}else{
			respMsg.setActiveStatus(isOpenNow?(byte)1:(byte)0);
			String[] time = this.getActiveTimePoint();
			if(null != time){
				respMsg.setActiveRemainTimeTips(isOpenNow?
						this.messageFormat(TextId.CampWar_Active_End_At, time[1]):
						this.messageFormat(TextId.CampWar_Active_Start_At, time[0]));
			}
		}
	
		ApplyInfo applyInfo = this.getApplyInfo(role.getRoleId());
		if (null != applyInfo) {
			respMsg.setApplyStatus((byte) 1);
			respMsg.setWaitMatchSecondTime((short) ((System.currentTimeMillis() - applyInfo
					.getCreateDate()) / 1000));
		} else {
			respMsg.setApplyStatus((byte) 0);
		}
		List<CampWarLeaderInfoItem> leaderList = Lists.newArrayList();
		for (byte i = 0; i < this.applyGroups.length; i++) {
			leaderList.add(this.getCampWarLeaderInfoItem(i));
		}
		respMsg.setLeaders(leaderList);
		respMsg.setAutoApply(autoApply ? (byte) 1 : (byte) 0);
		respMsg.setHpRate((short)FULL_HP_RATE);
		
		RoleHero roleHero = GameContext.getUserHeroApp().getOnBattleRoleHero(role.getRoleId());
		if(null != roleHero){
			Float hpRate = this.getHeroHpRate(role.getRoleId(), roleHero.getHeroId());
			if(null != hpRate){
				respMsg.setHpRate((short)(hpRate * FULL_HP_RATE)); 
			}
		}
		// 加入面板列表
		this.addPanelRole(role);

		return respMsg;
	}

	private CampWarLeaderInfoItem getCampWarLeaderInfoItem(byte campId) {
		CampWarLeaderInfoItem item = new CampWarLeaderInfoItem();
		item.setCampId(campId);
		LeaderConfig config = this.leaderConfigMap.get(campId);
		if (null == config) {
			return item;
		}
		item.setCurHp(this.campMatchGroup.getHp(campId));
		item.setMaxHp(this.campMatchGroup.getMaxConfigHp());
		item.setName(config.getLeaderName());
		item.setResId(config.getLeaderResId());
		item.setResRate(config.getLeaderResRate());
		item.setLevel(config.getLeaderLevel());
		return item;
	}

	private boolean isActiveOpen() {
		return null != this.campWarActive && this.campWarActive.isTimeOpen();
	}

	@Override
	public void removeApplyInfo(String roleId) {
		ApplyInfo apply = this.applyInfoMap.remove(roleId);
		if (null == apply) {
			return;
		}
		apply.setCancel(true);
	}

	private String getText(String textId) {
		return GameContext.getI18n().getText(textId);
	}

	private String messageFormat(String textId, Object... obj) {
		return GameContext.getI18n().messageFormat(textId, obj);
	}

	@Override
	public Result apply(RoleInstance role) {
		Result result = new Result();
		// 判断是否已经选择阵营
		byte campId = role.getCampId();
		if (-1 == campId) {
			// 请先选择阵营
			result.setInfo(this
					.getText(TextId.CampWar_Canot_Join_By_NotSelectCamp));
			return result;
		}
		if (!this.isActiveOpen()) {
			// 时间未到
			result.setInfo(this.getText(TextId.CampWar_Not_Open_Now));
			return result;
		}
		// 触发匹配线程
		this.startMatchThread();
		// 判断当前阵营是否提前分出了胜负
		if (!this.campMatchGroup.isRun()) {
			// 活动已经结束
			result.setInfo(this.getText(TextId.CampWar_End_Now));
			return result;
		}
		// 判断自己是否已经报名
		ApplyInfo applyInfo = this.getApplyInfo(role.getRoleId());
		if (null != applyInfo) {
			result.setInfo(this.getText(TextId.CampWar_Already_Apply));
			return result;
		}
		ApplyGroup group = this.applyGroups[campId];
		applyInfo = new ApplyInfo(role.getRoleId());
		applyInfo.setCampId(role.getCampId());
		this.applyInfoMap.put(role.getRoleId(), applyInfo);
		group.addApplyInfo(applyInfo);
		this.totalApplyGroup.addApplyInfo(applyInfo);

		result.success();
		result.setInfo(this.getText(TextId.CampWar_Apply_Success));
		return result;
	}

	@Override
	public int onLogin(RoleInstance role, Object context) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public int onLogout(RoleInstance role, Object context) {
		try {
			if (null == role) {
				return 1;
			}
			this.removePanelRole(role);

			C0085_CampWarAutoApplyInternalMessage reqMsg = new C0085_CampWarAutoApplyInternalMessage();
			reqMsg.setRole(role);
			reqMsg.setApply(false);
			role.getBehavior().addEvent(reqMsg);
		} catch (Exception ex) {
			logger.error("", ex);
			return 0;
		}
		
		return 1;
	}
	
	@Override
	public int onCleanup(String roleId, Object context) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Result cancel(RoleInstance role) {
		Result result = new Result();
		// 判断自己是否已经报名
		String roleId = role.getRoleId();
		ApplyInfo applyInfo = this.getApplyInfo(roleId);
		if (null == applyInfo) {
			result.setInfo(this.getText(TextId.CampWar_Cancel_Not_Apply));
			return result;
		}
		// 判断是否已经匹配成功
		if (null != applyInfo.getMatch()) {
			result.setInfo(this.getText(TextId.CampWar_Cancel_Have_Match));
			return result;
		}
		if (!applyInfo.isCancel()) {
			applyInfo.setCancel(true);
			// 将匹配组的有效报名数据-1
			this.getApplyGroups()[applyInfo.getCampId()].decrementSize();
		}
		this.removeApplyInfo(roleId);
		result.success();
		result.setInfo(this.getText(TextId.CampWar_Cancel_Success));
		return result;
	}

	@Override
	public Float getHeroHpRate(String roleId, int heroId) {
		Map<Integer, Float> hp = this.roleHeroHpMap.get(roleId);
		if (null == hp) {
			return 1.0f;
		}
		Float f =  hp.get(heroId);
		if(null == f){
			return 1.0f;
		}
		return f ;
	}

	@Override
	public void addHeroHpRate(String roleId, int heroId, Float rate) {
		Map<Integer, Float> hp = this.roleHeroHpMap.get(roleId);
		if (null == hp) {
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
	public void start() {/*
		this.initCampInfo();
		this.loadCampWarActive();
		this.loadLeaderConfig();
		this.loadLeaderBattle();
		this.loadLeaderBattleEffect();
		this.loadRoleBattleConfig();
		this.loadBattleRewardConfig();
		this.loadConsequentKilledReward();
	*/}

	private int leaderPkTimes = 0;

	/**
	 * 阵营首领pk
	 */
	private void leaderPkEvent() {
		if (GameContext.getOnlineCenter().onlineUserSize() <= 0) {
			// 没有用户不执行
			return;
		}
		LeaderBattle battle = this.getLeaderBattle(LeaderBattleType.system
				.getType());
		if (null == battle || 0 >= battle.getHurt()) {
			return;
		}

		leaderPkTimes++;
		if (leaderPkTimes >= this.getApplyGroups().length) {
			leaderPkTimes = 0;
		}

		byte campId = (byte) leaderPkTimes;
		LeaderBattleEffect effect = this.getLeaderBattleEffect(campId,
				LeaderBattleType.system.getType());
		if (null == effect) {
			return;
		}
		// 其他三阵营都减hp
		this.reduTargetHp(campId, battle.getHurt());

		CampType campType = CampType.get(campId);
		String info = this.replace(battle.getReportInfo(),
				this.wildcard_campName, campType.getName(),
				this.wildcard_leaderName, this.getLeaderName(campId));
		// 全部广播
		this.broadcastLeaderBattle(campId, effect, 0, -battle.getHurt(), info);
	}

	private void reduTargetHp(int campId, int hurt) {
		for (int i = 0; i < this.applyGroups.length; i++) {
			if (i == campId) {
				continue;
			}
			this.campMatchGroup.reduHp(i, hurt);
		}
	}

	private int killEventWin(AbstractRole attacker, AbstractRole victim) {
		RoleScore winScore = this.getRoleScore(attacker.getRoleId());
		// 胜利者连杀数+1
		// 胜利者总胜利次数+1
		int killNum = winScore.getKillNum() + 1;
		winScore.setKillNum(killNum);
		winScore.setMaxWinTimes(Math.max(winScore.getMaxWinTimes(),
				winScore.getKillNum()));
		winScore.setTotalWinTimes(winScore.getTotalWinTimes() + 1);
		byte campId = attacker.getCampId();
		// byte targetCampId = victim.getCampId();
		// 连杀事件
		LeaderBattle battle = this
				.getLeaderBattle(LeaderBattleType.continuous_Kill.getType());
		if (null == battle || 0 >= battle.getParameter()
				|| 0 == winScore.getKillNum() % battle.getParameter()) {
			return killNum;
		}
		// 其他三阵营都减hp
		this.reduTargetHp(campId, battle.getHurt());

		// 构建消息
		LeaderBattleEffect effect = this.getLeaderBattleEffect(campId,
				LeaderBattleType.continuous_Kill.getType());
		if (null == effect) {
			return killNum;
		}
		// 替换通配符
		CampType campType = CampType.get(campId);
		String info = this.replace(battle.getReportInfo(),
				this.wildcard_campName, campType.getName(),
				this.wildcard_roleName, attacker.getRoleName(),
				this.wildcard_leaderName, this.getLeaderName(campId),
				this.wildcard_killedNum, String.valueOf(winScore.getKillNum()));
		this.broadcastLeaderBattle(campId, effect, 0, -battle.getHurt(), info);
		return killNum;
	}

	private int killEventFail(AbstractRole attacker, AbstractRole victim) {
		// 失败者最大连杀数=0
		// 失败者总失败次数+1
		RoleScore failScore = this.getRoleScore(victim.getRoleId());
		int killNum = failScore.getKillNum();
		failScore.setKillNum(0);
		failScore.setTotalFailTimes(failScore.getTotalFailTimes() + 1);
		if (killNum < 2) {
			return killNum;
		}
		LeaderBattle battle = this
				.getLeaderBattle(LeaderBattleType.interrupt_kill.getType());
		if (null == battle || battle.getParameter() <= 0) {
			return killNum;
		}
		byte campId = attacker.getCampId();
		int cureValue = battle.getParameter() * killNum;
		// +hp
		this.campMatchGroup.addHp(campId, cureValue);
		// 构建消息
		LeaderBattleEffect effect = this.getLeaderBattleEffect(campId,
				LeaderBattleType.interrupt_kill.getType());
		if (null == effect) {
			return killNum;
		}
		// 替换通配符
		CampType campType = CampType.get(campId);
		String info = this.replace(battle.getReportInfo(),
				this.wildcard_campName, campType.getName(),
				this.wildcard_roleName, attacker.getRoleName(),
				this.wildcard_leaderName, this.getLeaderName(campId),
				this.wildcard_killedNum, String.valueOf(killNum),
				this.wildcard_targetRoleName, victim.getRoleName(),
				this.wildcard_cureValue, String.valueOf(cureValue));
		this.broadcastLeaderBattle(campId, effect, cureValue, 0, info);
		return killNum;
	}

	private String replace(String info, String... args) {
		if (null == info || null == args) {
			return info;
		}
		for (int i = 0; i < args.length; i = i + 2) {
			info = StringUtil.replace(info, args[i], args[i + 1]);
		}
		return info;
	}

	private void campPrestigeEvent(AbstractRole role, int prestige) {
		if (null == role) {
			return;
		}
		RoleScore score = this.getRoleScore(role.getRoleId());
		int remainPrestige = score.getRemainPrestige().addAndGet(prestige);
		LeaderBattle battle = this
				.getLeaderBattle(LeaderBattleType.camp_prestige.getType());
		if (null == battle) {
			return;
		}
		int parameter = battle.getParameter();
		if (parameter <= 0) {
			return;
		}
		// 次数
		int times = remainPrestige / parameter;
		if (times <= 0) {
			return;
		}
		int redu = parameter * times;
		score.getRemainPrestige().addAndGet(-redu);
		
		int hurt = battle.getHurt() * times;
		byte campId = role.getCampId();
		// 其他三阵营都减hp
		this.reduTargetHp(campId, hurt);
		// 广播
		// 构建消息
		LeaderBattleEffect effect = this.getLeaderBattleEffect(campId,
				LeaderBattleType.camp_prestige.getType());
		if (null == effect) {
			return;
		}
		// 替换通配符
		CampType campType = CampType.get(campId);
		String info = this.replace(battle.getReportInfo(),
				this.wildcard_campName, campType.getName(),
				this.wildcard_roleName, role.getRoleName(),
				this.wildcard_leaderName, this.getLeaderName(campId));
		for (int i = 0; i < times; i++) {
			this.broadcastLeaderBattle(campId, effect, 0, -battle.getHurt(),
					info);
		}
		return;
	}

	private void broadcastLeaderBattle(byte campId, LeaderBattleEffect effect,
			int attackerHpChange, int defenseHpChange, String info) {
		C0354_CampWarLeaderBattleRespMessage msg = new C0354_CampWarLeaderBattleRespMessage();
		msg.setAttackerCampId(campId);
		msg.setAttackerAnimationId(effect.getAnimationId());
		msg.setAttackerEffectId(effect.getAttackerEffectId());
		msg.setDefenseEffectId(effect.getDefenseEffectId());
		List<CampWarLeaderBattleHpItem> hpItems = Lists.newArrayList();
		for (byte i = 0; i < this.applyGroups.length; i++) {
			CampWarLeaderBattleHpItem item = new CampWarLeaderBattleHpItem();
			item.setCampId(i);
			if (i == campId) {
				item.setChangedHp(attackerHpChange);
			} else {
				item.setChangedHp(defenseHpChange);
			}
			item.setCurHp(this.campMatchGroup.getHp(campId));
			hpItems.add(item);
		}
		msg.setHpItems(hpItems);

		C0358_CampWarLeaderTipsRespMessage leaderMsg = new C0358_CampWarLeaderTipsRespMessage();
		leaderMsg.setInfo(info);

		for (Iterator<Map.Entry<String, Byte>> it = this.panelRoleMap
				.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, Byte> entry = it.next();
			GameContext.getMessageCenter()
					.sendByRoleId("", entry.getKey(), msg);
			if (campId == entry.getValue()) {
				GameContext.getMessageCenter().sendByRoleId("", entry.getKey(),
						leaderMsg);
			}
		}

	}

	private String getLeaderName(byte campId) {
		LeaderConfig lc = this.leaderConfigMap.get(campId);
		if (null == lc) {
			return "";
		}
		return lc.getLeaderName();
	}

	@Override
	public List<RoleRewardResult> roleBattleEnd(MatchInfo match,
			AbstractRole winRole, AbstractRole failRole) {
		List<RoleRewardResult> list = Lists.newArrayList();
		// 获得需要奖励的阵营声望与游戏币
		if (null == winRole || null == failRole) {
			// 轮空,不计算连杀,只给轮空奖励
			list.add(this.roleReward(match.getRole1(), match.getRole2(), false,
					0, 0, RolePkResult.nothing));
			list.add(this.roleReward(match.getRole2(), match.getRole1(), false,
					0, 0, RolePkResult.nothing));
		} else {
			int killNum = this.killEventWin(winRole, failRole);
			int interruptKill = this.killEventFail(winRole, failRole);
			list.add(this.roleReward(winRole, failRole, true, killNum,
					interruptKill, RolePkResult.success));
			list.add(this.roleReward(failRole, winRole, false, 0, 0,
					RolePkResult.fail));
		}
		// 声望事件
		for (RoleRewardResult result : list) {
			this.campPrestigeEvent(
					result.getRoleId().equals(match.getRole1().getRoleId()) ? match
							.getRole1() : match.getRole2(), result
							.getAddPrestige());
		}
		return list;
	}

	private ConsequentKilledReward getConsequentKilledReward(int killNum) {
		int size = this.killedRewardMap.size();
		if (killNum > size) {
			killNum = size;
		}
		return this.killedRewardMap.get(killNum);
	}

	private void notMatchEvent(String roleId) {
		try {
			RoleInstance role = GameContext.getOnlineCenter()
					.getRoleInstanceByRoleId(roleId);
			if (null == role) {
				return;
			}
			RoleScore score = this.getRoleScore(role.getRoleId());
			RoleRewardResult result = this.roleReward(role, null, true,
					score.getKillNum(), 0, RolePkResult.nothing);
			this.campPrestigeEvent(role, result.getEffectAddPrestige());
		} catch (Exception ex) {
			logger.error("notMathcEvent error", ex);
		}
	}

	private RoleRewardResult roleReward(AbstractRole role,
			AbstractRole targetRole, boolean win, int killNum,
			int interruptKill, RolePkResult pkType) {
		String roleId = role.getRoleId();
		RoleRewardResult result = new RoleRewardResult();
		result.setRoleId(roleId);
		result.setPkStatus(pkType.getType());

		RoleScore score = this.getRoleScore(roleId);
		int prestige = 0;
		int gameMoney = 0;
		if (win) {
			prestige = this.battleRewardConfig.getWinCampPrestige();
			gameMoney = this.battleRewardConfig.getWinGameMoney();
			// 计算额外的奖励
			ConsequentKilledReward killReward = this
					.getConsequentKilledReward(killNum);
			if (null != killReward) {
				prestige += killReward.getCampPrestige();
				gameMoney += killReward.getGameMoney();
			}
			if (interruptKill >= 2) {
				prestige += (int) ((interruptKill - 1)
						* this.battleRewardConfig.getInterruptPrestigeModulus() / ParasConstant.PERCENT_BASE_VALUE);
				gameMoney += (int) ((interruptKill - 1)
						* this.battleRewardConfig
								.getInterruptGameMoneyModulus() / ParasConstant.PERCENT_BASE_VALUE);
			}
		} else {
			// 失败
			prestige = this.battleRewardConfig.getFailCampPrestige();
			gameMoney = this.battleRewardConfig.getFailGameMoney();
		}
		
		int oldPrestige = score.getGainPrestige();
		int newPrestige = Math.min(oldPrestige + prestige,
				this.battleRewardConfig.getMaxCampPrestige());
		score.setGainPrestige(newPrestige);
		
		int effectAddPrestige = newPrestige - oldPrestige;
		
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
						AttributeType.gameMoney, OperatorType.Add, gameMoney,
						OutputConsumeType.camp_war_reward);
				role.getBehavior().notifyAttribute();
				//个人战报
				this.notifyRoleBattleReport(role, targetRole, win, effectAddPrestige,
						gameMoney, killNum, interruptKill, pkType);
			}
		} catch (Exception ex) {
			logger.error("", ex);
		}
		return result;
	}
	
	private void notifyRoleBattleReport(AbstractRole role,
			AbstractRole targetRole, boolean win, int effectAddPrestige,
			int gameMoney, int killNum, int interruptKill, RolePkResult pkType) {
		if (RolePkResult.nothing == pkType) {
			return;
		}
		if (win) {
			this.notifyRoleBattleSuccessReport(role, targetRole,
					effectAddPrestige, gameMoney, killNum, interruptKill);
			return;
		}
		this.notifyRoleBattleFailueReport(role, targetRole, effectAddPrestige,
				gameMoney);

	}

	private void notifyRoleBattleSuccessReport(AbstractRole role,
			AbstractRole targetRole, int prestige, int gameMoney, int killNum,
			int interruptKill) {
		if (null == role || null == targetRole) {
			return;
		}
		StringBuilder buffer = new StringBuilder();
		if (interruptKill < 2) {
			buffer.append(this.messageFormat(TextId.CampWar_Role_Report_Win,
					targetRole.getRoleName()));
		} else {
			buffer.append(this.messageFormat(
					TextId.CampWar_Role_Report_Win_Super,
					targetRole.getRoleName(), String.valueOf(interruptKill)));
		}
		if (killNum > 1) {
			buffer.append(this.messageFormat(
					TextId.CampWar_Role_Report_Win_Continuous,
					String.valueOf(killNum)));
		}
		buffer.append(this.messageFormat(TextId.CampWar_Role_Report_GameMoney,
				String.valueOf(gameMoney)));
		if (prestige > 0) {
			buffer.append(this.messageFormat(
					TextId.CampWar_Role_Report_CampPrestige,
					String.valueOf(prestige)));
		} else {
			buffer.append(this.getText(TextId.CampWar_Role_Report_CampPrestige));
		}
		C0359_CampWarRoleTipsRespMessage respMsg = new C0359_CampWarRoleTipsRespMessage();
		respMsg.setInfo(buffer.toString());
		GameContext.getMessageCenter().sendSysMsg(role, respMsg);
	}

	private void notifyRoleBattleFailueReport(AbstractRole role,
			AbstractRole targetRole, int prestige, int gameMoney) {
		if (null == role || null == targetRole) {
			return;
		}
		StringBuilder buffer = new StringBuilder();
		buffer.append(this.messageFormat(TextId.CampWar_Role_Report_Fail,
				targetRole.getRoleName()));
		buffer.append(this.messageFormat(TextId.CampWar_Role_Report_GameMoney,
				String.valueOf(gameMoney)));
		if (prestige > 0) {
			buffer.append(this.messageFormat(
					TextId.CampWar_Role_Report_CampPrestige,
					String.valueOf(prestige)));
		} else {
			buffer.append(this.getText(TextId.CampWar_Role_Report_CampPrestige));
		}
		C0359_CampWarRoleTipsRespMessage respMsg = new C0359_CampWarRoleTipsRespMessage();
		respMsg.setInfo(buffer.toString());
		GameContext.getMessageCenter().sendSysMsg(role, respMsg);
	}

}
