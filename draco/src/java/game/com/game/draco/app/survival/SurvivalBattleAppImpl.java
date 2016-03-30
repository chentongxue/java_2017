package com.game.draco.app.survival;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

import org.python.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.app.count.type.CountType;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.map.MapUtil;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.ChallengeResultType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.channel.EmptyChannelSession;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.channel.ChannelSession;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.module.cache.Cache;
import sacred.alliance.magic.module.cache.CacheEvent;
import sacred.alliance.magic.module.cache.CacheListener;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.app.mail.domain.MailAttriBean;
import com.game.draco.app.mail.type.MailSendRoleType;
import com.game.draco.app.survival.config.SurvivalBase;
import com.game.draco.app.survival.config.SurvivalMail;
import com.game.draco.app.survival.config.SurvivalReward;
import com.game.draco.app.survival.vo.SurvivalApplyInfo;
import com.game.draco.app.survival.vo.SurvivalConfirm;
import com.game.draco.app.survival.vo.SurvivalResult;
import com.game.draco.app.team.PlayerTeam;
import com.game.draco.app.team.Team;
import com.game.draco.message.internal.C0068_SurvivalTeamMatchInternalMessage;
import com.game.draco.message.item.AttriTypeValueItem;
import com.game.draco.message.item.GoodsLiteItem;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.push.C0007_ConfirmationNotifyMessage;
import com.game.draco.message.request.C0281_SurvivalConfirmReqMessage;
import com.game.draco.message.response.C0280_SurvivalRespMessage;
import com.game.draco.message.response.C0282_SurvivalInfoRespMessage;
import com.game.draco.message.response.C0283_SurvivalOverRespMessage;

public class SurvivalBattleAppImpl implements SurvivalBattleApp {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final byte ENTER_SURVIVAL = 1;
	private final static ChannelSession emptyChannelSession = new EmptyChannelSession();
	private Thread matchThread = null;
	private volatile boolean matchRunning = false;
	private volatile boolean matchRunningActive = false;
	private volatile boolean matchNow = false;
	private Cache<String, SurvivalConfirm> survivalCache = null;
	private final static AtomicLong KEY_GEN = new AtomicLong(0);
	// 报名队列信息
	private Queue<SurvivalApplyInfo> applyQueue = new ConcurrentLinkedQueue<SurvivalApplyInfo>();
	
	// 组队副本确认
	private static final short ENTER_SURVIVAL_CMD = new C0281_SurvivalConfirmReqMessage().getCommandId();
	
	//当前时间
	private long nowTime;
	
	private SurvivalBase getBase(){
		return GameContext.getSurvivalApp().getSurvivalBase();
	}
	
	@Override
	public void start() {
		if(matchRunning){
			return;
		}
		
		SurvivalBase base = getBase();
		if(null == base){
			return;
		}
		nowTime = 0;
		// 启动系统匹配线程
		matchRunning = true;
		matchRunningActive = true;
		matchThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (matchRunningActive) {
					try {
						Thread.sleep(getMatchInterval());
					} catch (Exception e) {
					}
					try {
						if(matchRunning){
							if(!isTimeOpen(true)){
								continue;
							}
							sendSystemMatchReq();
						}
					} catch (Exception ex) {
						logger.error("", ex);
					}
					clearApplyQueueDate();
				}
			}
		});
		matchThread.setName("match thread for survival team ");
		matchThread.start();
		this.initCache();// 启动时间验证
	}	
	
	
	@Override
	public boolean isTimeOpen(boolean flag){
		Active active = GameContext.getActiveApp().getActive(getBase().getActiveId());
		if(null != active){
			boolean isTimeOpen = active.isTimeOpen();
			if(flag && !isTimeOpen){
				// 清空报名队列信息
				matchRunning = false;
				nowTime = System.currentTimeMillis() + DateUtil.ONE_HOUR_MILLIS;
			}
			return isTimeOpen;
		}
		return true;
	}
	
	private void clearApplyQueueDate(){
		Active active = GameContext.getActiveApp().getActive(getBase().getActiveId());
		if(null != active){
			if(nowTime <= 0){
				return;
			}
			if(System.currentTimeMillis() >= nowTime){
				matchRunningActive = false;
				applyQueue.clear();
			}
		}
	}
	
	@Override
	public void systemMatch() {
		try {
			if(Util.isEmpty(applyQueue)){
				return;
			}
			for (SurvivalApplyInfo info : applyQueue) {
				if(info.isStatus()){
					continue;
				}
				Iterator<Team> iter = info.getTeamList().iterator();
				while(iter.hasNext()){
					Team team = iter.next();
					byte memberSize = info.getTeamSizeMap().get(team.getTeamId());
					if (this.canMatch(team,memberSize)) {
						continue;
					}
					// 通知其他队员离开
					info.notifyLeave(false,team);
					// 发送匹配失败消息
					this.sendMatchFail(team);
					iter.remove();
				}
				//如果达到最大人数
				if(!info.isTeamMaxMember()){
					continue;
				}
				//通知第一队列玩家进入战斗
				enterSurvivalBattle(info);
				//队列中的队伍标识改为已进入
				info.changeStatus();
			}
		} catch (Exception ex) {
			logger.error("", ex);
		} finally {
			this.matchNow = false;
		}
	}
	
	/**
	 * 判断未响应组队请求
	 */
	private void initCache() {
		this.survivalCache.addCacheListener(new CacheListener<String, SurvivalConfirm>() {
			@Override
			public void entryRemoved(CacheEvent<String, SurvivalConfirm> event) {
				// 超时处理
				SurvivalConfirm confirm = event.getValue();
				if(null == confirm){
					return ;
				}
				doTimeoutListener(event.getKey(),confirm);
			}
			
			@Override
			public void entryAccessed(CacheEvent<String, SurvivalConfirm> event) {
			}
			@Override
			public void entryAdded(CacheEvent<String, SurvivalConfirm> event) {
			}
			@Override
			public void entryCleared(CacheEvent<String, SurvivalConfirm> event) {
			}
			@Override
			public void entryExpired(CacheEvent<String, SurvivalConfirm> event) {
			}
			@Override
			public void entryUpdated(CacheEvent<String, SurvivalConfirm> event) {
			}
		});
	}
	
	/**
	 * 超时处理
	 * @param teamId
	 * @param confirm
	 */
	private void doTimeoutListener(String teamId,SurvivalConfirm confirm){
		RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(String.valueOf(confirm.getRoleId()));
		if (null == role) {
			return;
		}
		Team team = role.getTeam();
		if (null == team) {
			return;
		}
		StringBuffer buffer = new StringBuffer();
		String cat = "";
		boolean flag = false;
		for (AbstractRole r : team.getMembers()) {
			if (null == r) {
				continue;
			}
			if (r.getRoleId().equals(confirm.getRoleId())) {
				continue;
			}
			if (confirm.haveConfirm(r.getIntRoleId())) {
				continue;
			}
			boolean isOnline = GameContext.getOnlineCenter().isOnlineByRoleId(r.getRoleId());
			if(isOnline){
				continue;
			}
			buffer.append(cat).append(r.getRoleName());
			cat = Cat.comma;
			flag = true;
		}
		if(flag){
			String message = GameContext.getI18n().messageFormat(TextId.SURVIVAL_BATTLE_ROLE_REFUSE, buffer.toString());
			C0003_TipNotifyMessage msg = new C0003_TipNotifyMessage();
			msg.setMsgContext(message);
			role.getBehavior().sendMessage(msg);
			GameContext.getChatApp().sendSysMessage(ChatSysName.Survival_Team, ChannelType.Team, message, null, team);
		}
	}


	/**
	 * 发送系统内部消息（匹配）
	 */
	private void sendSystemMatchReq() {
		// 匹配逻辑未结束
		if (this.matchNow) {
			return;
		}
		this.matchNow = true;
		C0068_SurvivalTeamMatchInternalMessage reqMsg = new C0068_SurvivalTeamMatchInternalMessage();
		GameContext.getUserSocketChannelEventPublisher().publish(null, reqMsg, emptyChannelSession);
	}

	/**
	 * 刷新时间
	 * @return
	 */
	private int getMatchInterval() {
		return GameContext.getParasConfig().getSurvivalSecond() * 1000;
	}
	
	/**
	 * 是否满足匹配条件
	 * @param applyInfo
	 * @return
	 */
	private boolean canMatch(Team team ,byte size) {
		// 判断成员是否发生变化
		for (AbstractRole role : team.getMembers()) {
			// 如果有玩家掉线，取消匹配
			if (!GameContext.getOnlineCenter().isOnlineByRoleId(role.getRoleId())) {
				return false;
			}
			RoleInstance player = (RoleInstance) role;
			Team t = player.getTeam();
			if (t == null) {
				return false;
			}
			// 如果更换队伍，取消匹配
			if (!team.getTeamId().equals(t.getTeamId())) {
				return false;
			}
			// 如果队伍成员变化，取消匹配
			if (team.getPlayerNum() != size) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 发送匹配失败信息
	 * @param info
	 */
	private void sendMatchFail(Team team) {
		if(team == null || Util.isEmpty(team.getMembers())){
			return;
		}
		for(AbstractRole role : team.getMembers()){
			if (!GameContext.getOnlineCenter().isOnlineByRoleId(role.getRoleId())) {
				continue;
			}
			RoleInstance player = (RoleInstance) role;
			Team t = player.getTeam();
			if (team == null || !team.getTeamId().equals(t.getTeamId())) {
				return;
			}
			SurvivalBase base = getBase();
			GameContext.getChatApp().sendSysMessage(ChatSysName.Survival_Team, ChannelType.System, GameContext.getI18n().messageFormat(TextId.SURVIVAL_MATCH_FAIL_BY_TEAM_MEMBER_CHANGE, base.getBaseName()), null, role);
		}
	}
	
	/**
	 * 报名生存战场
	 * @param role
	 * @return
	 */
	@Override
	public SurvivalResult apply(RoleInstance role,byte type) {
		SurvivalResult result = new SurvivalResult();
		boolean flag = false;// 是否是单人标记
		Team team = role.getTeam();
		if (team == null) {
			flag = true;
			team = new PlayerTeam(role);
		} else if (team.getPlayerNum() <= 1) {
			flag = true;
		}
		// 判断是否符合条件报名
		result = canApply(role, type);
		if (!result.isSuccess()) {
			return result;
		}
		// 如果不是个人，需要队员确认
		if (!flag) {
			// 创建确认信息并放到内存中
			SurvivalConfirm confirm = new SurvivalConfirm();
			confirm.setRoleId(role.getIntRoleId());
			confirm.setMemberNum(team.getMembers().size());
			survivalCache.put(team.getTeamId(), confirm);
			// 发送给客户端的二次确认信息
			String message = null;
			if (ENTER_SURVIVAL == type) {
				SurvivalBase base = GameContext.getSurvivalApp().getSurvivalBase();
				message = GameContext.getI18n().messageFormat(TextId.SURVIVAL_RESULT_ENTER,base.getBaseName());
			} 
			// 通知队内成员二次确认消息
			for (AbstractRole r : team.getMembers()) {
				if (r == null) {
					continue;
				}
				if (team.isLeader(r)) {
					continue;
				}
				RoleInstance roleInstance = (RoleInstance) r;
				this.sendConfirmMessage(roleInstance, message);
			}
			result.setInfo(GameContext.getI18n().getText(TextId.SURVIVAL_WAIT_OTHERS));
			result.failure();
			return result;
		} else {
			this.addApplyInfo(role, team);
		}
		result.setInfo(GameContext.getI18n().messageFormat(TextId.SURVIVAL_REGISTRATION_SUCCESS));
		result.success();
		return result;
	}
	
	/**
	 * 增加匹配信息
	 * @param role
	 * @param team
	 * @param copyId
	 */
	private void addApplyInfo(RoleInstance role, Team team) {
		SurvivalApplyInfo info = getFirstQueueInfo(role);
		if(info == null){
			info = new SurvivalApplyInfo(KEY_GEN.getAndIncrement());
			info.addSurvivalApplyInfo(team);
			applyQueue.offer(info);
			return;
		}
		info.addSurvivalApplyInfo(team);
	}
	
	/**
	 * 通知队内成员二次确认消息
	 * @param team
	 * @param info
	 */
	private void sendConfirmMessage(RoleInstance role, String info) {
		C0007_ConfirmationNotifyMessage confirmMsg = new C0007_ConfirmationNotifyMessage();
		confirmMsg.setAffirmCmdId(ENTER_SURVIVAL_CMD);
		confirmMsg.setAffirmParam(SurvivalConfirm.AFFIRM);
		confirmMsg.setCancelCmdId(ENTER_SURVIVAL_CMD);
		confirmMsg.setCancelParam(SurvivalConfirm.CANCEL);
		confirmMsg.setInfo(info);
		confirmMsg.setTime((byte) 30);
		role.getBehavior().sendMessage(confirmMsg);
	}
	
	/**
	 * 判断是否参与匹配
	 * @param role
	 * @param type
	 * @return
	 */
	private SurvivalResult canApply(RoleInstance role, byte type) {
		SurvivalResult result = new SurvivalResult();
		Team team = role.getTeam();
		
		// 队长才能报名
		if (!team.isLeader(role)) {
			result.setInfo(GameContext.getI18n().getText(TextId.SURVIVAL_TEAM_LEADER_APPLY));
			return result;
		}
		
		if(isApplyStatus(role)){
			result.setInfo(GameContext.getI18n().getText(TextId.SURVIVAL_REPEAT));
			return result;
		}
		
		// 判断队伍内成员是否满足条件
		for (AbstractRole m : team.getMembers()) {
			if (m == null) {
				continue;
			}
			RoleInstance member = (RoleInstance) m;
			// 判断是否离线
			if (!GameContext.getOnlineCenter().isOnlineByRoleId(member.getRoleId())) {
				result.setInfo(GameContext.getI18n().messageFormat(TextId.SURVIVAL_ROLE_NOT_ONLINE, member.getRoleName()));
				result.setNotifyTeam(true);
				return result;
			}
		}
		
		result.success();
		return result;
	}

	/**
	 * 取消报名
	 * @param role
	 * @return
	 */
	@Override
	public SurvivalResult cancel(RoleInstance role) {
		SurvivalResult result = new SurvivalResult();
		Team team = role.getTeam();
		if(team == null){
			result.setInfo(GameContext.getI18n().getText(TextId.SURVIVAL_TEAM_LEADER_CANCEL));
			return result;
		}
		//判断是否是队长
		if (!team.isLeader(role)) {
			result.setInfo(GameContext.getI18n().getText(TextId.SURVIVAL_TEAM_LEADER_CANCEL));
			return result;
		}
		
		SurvivalApplyInfo applyInfo = getSurvivalApplyInfo(role);
		if (applyInfo == null) {
			result.setInfo(GameContext.getI18n().getText(TextId.SURVIVAL_ROLE_NOT_APPLY));
			return result;
		}
		
		if(applyInfo.isStatus()){
			result.setInfo(GameContext.getI18n().getText(TextId.SURVIVAL_FLINCH));
			return result;
		}
		
		applyInfo.removeSurvivalApplyInfo(team);
		// 通知其他队员离开
		applyInfo.notifyLeave(true,team);
		result.success();
		return result;
	}
	
	@Override
	public SurvivalApplyInfo getSurvivalApplyInfo(RoleInstance role) {
		Team team = role.getTeam();
		if (null == team) {
			return null;
		}
		Iterator<SurvivalApplyInfo> iter = applyQueue.iterator();
		while(iter.hasNext()){
			SurvivalApplyInfo info = iter.next(); 
			Iterator<Team> teamIter = info.getTeamList().iterator();
			while(teamIter.hasNext()){
				Team t = teamIter.next();
				if(t.getTeamId() == null){
					continue;
				}
				if(t.getTeamId().equals(team.getTeamId())){
					return info;
				}
			}
		}
		return null;
	}
	
	private SurvivalApplyInfo getFirstQueueInfo(RoleInstance role) {
		Team team = role.getTeam();
		if (null == team) {
			return null;
		}
		Iterator<SurvivalApplyInfo> iter = applyQueue.iterator();
		while(iter.hasNext()){
			SurvivalApplyInfo info = iter.next(); 
			if(!info.isStatus() && !info.isTeamMaxMember()){
				return info;
			}
		}
		return null;
	}
	
	/**
	 * 生存战场队友确认
	 * @param role
	 * @param confirm
	 */
	@Override
	public void survivalTeamConfirm(RoleInstance role, String confirm) {
		Team team = role.getTeam();
		if (null == team) {
			return;
		}
		// 如果有人拒绝进入
		if (SurvivalConfirm.CANCEL.equals(confirm)) {
			String message = GameContext.getI18n().messageFormat(TextId.SURVIVAL_ROLE_REFUSE, role.getRoleName());
			RoleInstance roleInstance = (RoleInstance) team.getLeader();
			C0003_TipNotifyMessage msg = new C0003_TipNotifyMessage();
			msg.setMsgContext(message);
			roleInstance.getBehavior().sendMessage(msg);
			GameContext.getChatApp().sendSysMessage(ChatSysName.Survival_Team, ChannelType.Team, message, null, team);
			this.survivalCache.removeQuiet(team.getTeamId());
			return;
		}
		SurvivalConfirm sConfirm = this.getSurvivalConfirm(team.getTeamId());
		if (null == sConfirm) {
			return;
		} 
		//成员进入确认（如果都确认了，返回true）
		if (sConfirm.memberConfirm(role.getIntRoleId())) {
			addApplyInfo(role, team);
			//通知
			for(AbstractRole r : team.getMembers()){
				C0280_SurvivalRespMessage resp = new C0280_SurvivalRespMessage();
				resp.setType((byte)1);
				resp.setStatus((byte)1);
				r.getBehavior().sendMessage(resp);
			}
			
		}
	}
	
	/**
	 * 获得组队副本二次确认信息
	 * @param teamId
	 * @return
	 */
	private SurvivalConfirm getSurvivalConfirm(String teamId) {
		return survivalCache.getQuiet(teamId);
	}
	
	@Override
	public boolean isApplyStatus(RoleInstance role) {
		return null != this.getSurvivalApplyInfo(role);
	}

	@Override
	public C0282_SurvivalInfoRespMessage sendC0282_SurvivalInfoRespMessage(RoleInstance role) {
		
		C0282_SurvivalInfoRespMessage resp = new C0282_SurvivalInfoRespMessage();
		
		SurvivalBase base = getBase();
		
		Map<Byte,SurvivalReward> rewardMap = GameContext.getSurvivalApp().getSurvivalRewardMap();
		
		resp.setInfo(base.getDes());
		String rewardInfo = GameContext.getI18n().messageFormat(TextId.SURVIVAL_BATTLE_REWARD_DES, base.getRewardNum(),role.getRoleCount().getRoleTimesToInt(CountType.SurvivalTimes),base.getRewardNum());
		resp.setRewardInfo(rewardInfo);
		if(GameContext.getSurvivalBattleApp().isApplyStatus(role)){
			resp.setState((byte)1);
		}
		
		//胜利奖励
		SurvivalReward reward = rewardMap.get((byte)1);
		
		List<GoodsLiteItem> goodsLiteItemList = new ArrayList<GoodsLiteItem>();
		GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(reward.getGoodsId());
		GoodsLiteNamedItem goodsLiteItem = goodsBase.getGoodsLiteNamedItem();
		goodsLiteItem.setNum((short)(reward.getGoodsNum()));
		goodsLiteItemList.add(goodsLiteItem);
		
		//属性奖励
		List<AttriTypeValueItem> attrList = new ArrayList<AttriTypeValueItem>();
		
		AttriTypeValueItem attrItem = new AttriTypeValueItem();
		attrItem.setAttriType(AttributeType.gameMoney.getType());
		attrItem.setAttriValue(reward.getGold());
		attrList.add(attrItem);
		
		attrItem = new AttriTypeValueItem();
		attrItem.setAttriType(AttributeType.honor.getType());
		attrItem.setAttriValue(reward.getHonor());
		attrList.add(attrItem);
		
		resp.setAttrList(attrList);
		resp.setGoodsList(goodsLiteItemList);
		return resp;
	}
	
	/**
	 * 进入生存战场
	 * @param role
	 */
	private void enterSurvivalBattle(SurvivalApplyInfo info){
		try {
			List<Team> teamList = info.getTeamList();
			SurvivalBase base = getBase();
			for(Team team : teamList){
				for(AbstractRole member : team.getMembers()){
					RoleInstance r = (RoleInstance)member;
					Point targetPoint = MapUtil.randomCorrectRoadPoint(base.getMapId());
					GameContext.getUserMapApp().changeMap(r, targetPoint);
				}
			}
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendSurvivalBattleReward(Team team,ChallengeResultType result,String instanceId) {
		
		SurvivalBase base = getBase();
		for(AbstractRole member : team.getMembers()){
			if(GameContext.getOnlineCenter().isOnlineByRoleId(member.getRoleId())){
				ChallengeResultType tempResult = result;
				RoleInstance role = (RoleInstance)member;
				if(role.getMapInstance() == null){
					continue ;
				}
//				if(!role.getMapInstance().getInstanceId().equals(instanceId)){
//					continue;
//				}
				
				if(role.getRoleCount().getRoleTimesToInt(CountType.SurvivalTimes) >= base.getRewardNum()){
					tempResult = ChallengeResultType.Default;
				}
				
				//平分数量
				int avg = 1;
				if(tempResult == ChallengeResultType.Win){
					if(role.getTeam() != null){
						avg = role.getTeam().getMembers().size();
					}
				}
				
				SurvivalReward reward = GameContext.getSurvivalApp().getSurvivalRewardMap().get(tempResult.getType());
				SurvivalMail mail = GameContext.getSurvivalApp().getSurvivalMailMap().get(tempResult.getType());
				
				//添加物品
				List<GoodsOperateBean> goodsList = Lists.newArrayList();
				goodsList.add(new GoodsOperateBean(reward.getGoodsId(), reward.getGoodsNum()/avg, BindingType.get(reward.getGoodsBinded())));
				
				MailAttriBean attriBean = new MailAttriBean();
				attriBean.setSilverMoney(reward.getGold()/avg);
				attriBean.setHonor(reward.getHonor()/avg);
				
				OutputConsumeType consumeType = null;
				if(tempResult == ChallengeResultType.Lose){
					consumeType = OutputConsumeType.survival_battle_output;
				}else{
					consumeType = OutputConsumeType.survival_battle_win_output;
				}
				
				//异步发邮件
				GameContext.getMailApp().sendMailAsync(role.getRoleId(), mail.getTitle(), mail.getContent(), 
						MailSendRoleType.System.getName(), consumeType.getType(),goodsList,attriBean);
			}
		}
	}

	@Override
	public void gameOver(Team team, ChallengeResultType result,String instanceId) {
		
		SurvivalBase base = getBase();
		
		for(AbstractRole member : team.getMembers()){
			ChallengeResultType tempResult = result;
			if(!GameContext.getOnlineCenter().isOnlineByRoleId(member.getRoleId())){
				continue;
			}
			
			RoleInstance role = (RoleInstance)member;
			if(role.getMapInstance() == null){
				continue ;
			}
//			if(!role.getMapInstance().getInstanceId().equals(instanceId)){
//				continue;
//			}
			C0283_SurvivalOverRespMessage respMsg = new C0283_SurvivalOverRespMessage();
			
			int rewardNum = role.getRoleCount().getRoleTimesToInt(CountType.SurvivalTimes);
			//平分数量
			int avg = 1;
			if(result == ChallengeResultType.Win){
				role.getRoleCount().changeTimes(CountType.SurvivalTimes);//incrSurvivalRewardNum();
				if(rewardNum >= base.getRewardNum()){
					tempResult = ChallengeResultType.Default;
				}
				if(role.getTeam() != null){
					avg = role.getTeam().getMembers().size();
				}
			}
			
			SurvivalReward reward = GameContext.getSurvivalApp().getSurvivalRewardMap().get(tempResult.getType());
			
			List<AttriTypeValueItem> attrList = Lists.newArrayList();
			
			if(reward.getGoodsId() > 0){
				//物品
				List<GoodsLiteItem> goodsList = Lists.newArrayList();
				GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(
						reward.getGoodsId());
				GoodsLiteItem goodsItem = goodsBase.getGoodsLiteItem();
	
				goodsItem.setBindType(reward.getGoodsBinded());
				goodsItem.setNum((short)(reward.getGoodsNum()/avg));
	
				goodsList.add(goodsItem);
				respMsg.setGoodsList(goodsList);
			}
			
			//属性
			AttriTypeValueItem attr = new AttriTypeValueItem();
			attr.setAttriType(AttributeType.honor.getType());
			attr.setAttriValue(reward.getHonor()/avg);
			attrList.add(attr);
			
			attr = new AttriTypeValueItem();
			attr.setAttriType(AttributeType.gameMoney.getType());
			attr.setAttriValue(reward.getGold()/avg);
			attrList.add(attr);
			
			respMsg.setSuccess(tempResult.getType());
			respMsg.setAttrList(attrList);
			
			String info = "";
			String info2 = "";
			
			if(tempResult == ChallengeResultType.Lose){
				info = GameContext.getI18n().getText(TextId.SURVIVAL_BATTLE_LOST_INFO);
				info2 = GameContext.getI18n().getText(TextId.SURVIVAL_BATTLE_REWARD_LOST_INFO);
			}else if(tempResult == ChallengeResultType.Win){
				rewardNum = role.getRoleCount().getRoleTimesToInt(CountType.SurvivalTimes);
				info = GameContext.getI18n().getText(TextId.SURVIVAL_BATTLE_WIN_INFO);
				info2 = GameContext.getI18n().messageFormat(TextId.SURVIVAL_BATTLE_REWARD_INFO,rewardNum,base.getRewardNum());
			}else if(tempResult == ChallengeResultType.Default){
				info = GameContext.getI18n().getText(TextId.SURVIVAL_BATTLE_WIN_INFO);
				info2 = GameContext.getI18n().getText(TextId.SURVIVAL_BATTLE_REWARD_FULL_INFO);
			}
			respMsg.setInfo(info);
			respMsg.setInfo2(info2);
			role.getBehavior().sendMessage(respMsg);
		}
	}
	
	public Cache<String, SurvivalConfirm> getSurvivalCache() {
		return survivalCache;
	}
	
	public void setSurvivalCache(Cache<String, SurvivalConfirm> survivalCache) {
		this.survivalCache = survivalCache;
	}

	@Override
	public void pollApplyInfo(SurvivalApplyInfo info) {
		applyQueue.remove(info);
	}

}