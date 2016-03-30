package com.game.draco.app.copy.team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.channel.EmptyChannelSession;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.channel.ChannelSession;
import sacred.alliance.magic.module.cache.Cache;
import sacred.alliance.magic.module.cache.CacheEvent;
import sacred.alliance.magic.module.cache.CacheListener;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.app.copy.config.CopyConfig;
import com.game.draco.app.copy.team.vo.ApplyInfo;
import com.game.draco.app.copy.team.vo.CopyTeamConfirm;
import com.game.draco.app.copy.team.vo.MatchResult;
import com.game.draco.app.copy.team.vo.TeamResult;
import com.game.draco.app.copy.vo.CopyType;
import com.game.draco.app.team.PlayerTeam;
import com.game.draco.app.team.Team;
import com.game.draco.message.internal.C0066_CopyTeamMatchInternalMessage;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.push.C0007_ConfirmationNotifyMessage;
import com.game.draco.message.request.C0209_CopyEnterReqMessage;
import com.game.draco.message.request.C0223_CopyEnterConfirmReqMessage;
import com.game.draco.message.response.C0220_CopyTeamApplyRespMessage;
import com.google.common.collect.Maps;

public class CopyTeamAppImpl implements CopyTeamApp {
	private static final byte ENTER_COPY = 1;
	private final static Logger logger = LoggerFactory.getLogger(CopyTeamApp.class);
	private final static ChannelSession emptyChannelSession = new EmptyChannelSession();
	private Thread matchThread = null;
	private boolean matchRunning = false;
	private boolean matchNow = false;
	// 报名信息
	private Map<String, ApplyInfo> applyMap = Maps.newLinkedHashMap();
	// 组队副本确认
	private static final short ENTER_COPY_CMD = new C0223_CopyEnterConfirmReqMessage().getCommandId();
	private Cache<String, CopyTeamConfirm> teamCopyCache = null;

	@Override
	public void setArgs(Object arg0) {
	}

	private int getMatchInterval() {
		return GameContext.getParasConfig().getCopyTeamMatchSecond() * 1000;
	}

	@Override
	public void start() {
//		// 启动系统匹配线程
//		this.matchRunning = true;
//		matchThread = new Thread(new Runnable() {
//			@Override
//			public void run() {
//				while (matchRunning) {
//					try {
//						Thread.sleep(getMatchInterval());
//					} catch (Exception e) {
//					}
//					try {
//						sendSystemMatchReq();
//					} catch (Exception ex) {
//						logger.error("", ex);
//					}
//				}
//			}
//		});
//		matchThread.setName("match thread for copy team ");
//		matchThread.start();
		this.initCache();// 启动时间验证
	}
	
	/**
	 * 判断未响应组队请求
	 */
	private void initCache() {
		this.teamCopyCache.addCacheListener(new CacheListener<String, CopyTeamConfirm>() {
			
			@Override
			public void entryRemoved(CacheEvent<String, CopyTeamConfirm> event) {
				// 超时处理
				CopyTeamConfirm confirm = event.getValue();
				if(null == confirm){
					return ;
				}
				doTimeoutListener(event.getKey(),confirm);
			}
			
			@Override
			public void entryAccessed(CacheEvent<String, CopyTeamConfirm> event) {
			}
			@Override
			public void entryAdded(CacheEvent<String, CopyTeamConfirm> event) {
			}
			@Override
			public void entryCleared(CacheEvent<String, CopyTeamConfirm> event) {
			}
			@Override
			public void entryExpired(CacheEvent<String, CopyTeamConfirm> event) {
			}
			@Override
			public void entryUpdated(CacheEvent<String, CopyTeamConfirm> event) {
			}
			
		});
	}
	
	/**
	 * 超时处理
	 * @param key
	 * @param confirm
	 */
	private void doTimeoutListener(String teamId,CopyTeamConfirm confirm){
		RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(confirm.getRoleId());
		if (null == role) {
			return;
		}
		Team team = role.getTeam();
		if (null == team) {
			return;
		}
		StringBuffer buffer = new StringBuffer();
		String cat = "";
		for (AbstractRole r : team.getMembers()) {
			if (null == r) {
				continue;
			}
			if (r.getRoleId().equals(confirm.getRoleId())) {
				continue;
			}
			if (confirm.haveConfirm(r.getRoleName())) {
				continue;
			}
			buffer.append(cat).append(r.getRoleName());
			cat = Cat.comma;
		}
		String message = GameContext.getI18n().messageFormat(TextId.Team_Copy_Role_Refuse, buffer.toString());
		C0003_TipNotifyMessage msg = new C0003_TipNotifyMessage();
		msg.setMsgContext(message);
		role.getBehavior().sendMessage(msg);
		GameContext.getChatApp().sendSysMessage(ChatSysName.Copy_Team, ChannelType.Team, message, null, team);
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
		C0066_CopyTeamMatchInternalMessage reqMsg = new C0066_CopyTeamMatchInternalMessage();
		GameContext.getUserSocketChannelEventPublisher().publish(null, reqMsg, emptyChannelSession);
	}

	@Override
	public void stop() {
		this.matchRunning = false;
	}

	/**
	 * 是否满足匹配条件
	 * @param applyInfo
	 * @return
	 */
	private boolean canMatch(ApplyInfo applyInfo) {
		// 判断成员是否发生变化
		int size = applyInfo.getApplyRoles().size();
		for (AbstractRole role : applyInfo.getApplyRoles()) {
			// 如果有玩家掉线，取消匹配
			if (!GameContext.getOnlineCenter().isOnlineByRoleId(role.getRoleId())) {
				return false;
			}
			RoleInstance player = (RoleInstance) role;
			Team team = player.getTeam();
			if (null == team) {
				return false;
			}
			// 如果更换队伍，取消匹配
			if (!team.getTeamId().equals(applyInfo.getTeamId())) {
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
	private void sendMatchFail(ApplyInfo info) {
		for (AbstractRole role : info.getApplyRoles()) {
			if (!GameContext.getOnlineCenter().isOnlineByRoleId(role.getRoleId())) {
				continue;
			}
			RoleInstance player = (RoleInstance) role;
			Team team = player.getTeam();
			if (null == team || !team.getTeamId().equals(info.getTeamId())) {
				return;
			}
			GameContext.getChatApp().sendSysMessage(ChatSysName.Copy_Team, ChannelType.System, GameContext.getI18n().getText(TextId.Team_Copy_MATCH_FAIL_BY_TEAM_MEMBER_CHANGE), null, role);
		}
	}

	/**
	 * 判断是否参与匹配
	 * @param role
	 * @param copyId
	 * @return
	 */
	private TeamResult canApply(RoleInstance role, short copyId, byte type) {
		TeamResult result = new TeamResult();
		Team team = role.getTeam();
		// 队长才能报名
		if (!team.isLeader(role)) {
			result.setInfo(GameContext.getI18n().getText(TextId.Team_Copy_Lader_Can_Apply));
			return result;
		}
		// 验证队伍是否已经在匹配队列
		ApplyInfo applyInfo = this.getApplyInfo(role);
		if (null != applyInfo) {
			short oldCopyId = applyInfo.getCopyId();
			if (oldCopyId == copyId) {
				GameContext.getI18n().getText(TextId.Team_Copy_IsIn);
				return result;
			}
			CopyConfig oldConfig = GameContext.getCopyLogicApp().getCopyConfig(oldCopyId);
			result.setInfo(GameContext.getI18n().messageFormat(TextId.Team_Copy_In_Match, oldConfig.getCopyName()));
			return result;
		}
		// 验证是否等待队员验证
		CopyTeamConfirm confirm = this.teamCopyCache.getQuiet(team.getTeamId());
		if (null != confirm) {
			short oldCopyId = confirm.getCopyId();
			if (oldCopyId == copyId) {
				result.setInfo(GameContext.getI18n().getText(TextId.Team_Wait_Others));
				return result;
			}
			CopyConfig oldConfig = GameContext.getCopyLogicApp().getCopyConfig(oldCopyId);
			result.setInfo(GameContext.getI18n().messageFormat(TextId.Team_Copy_In_Match, oldConfig.getCopyName()));
			return result;
		}
		// 验证副本进入条件
		CopyConfig copyConfig = GameContext.getCopyLogicApp().getCopyConfig(copyId);
		if (null == copyConfig) {
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result;
		}
		if (copyConfig.getCopyType() != CopyType.team) {
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result;
		}
		if (team.getPlayerNum() > copyConfig.getMaxEnterCount()) {
			result.setInfo(GameContext.getI18n().getText(TextId.Copy_Role_Too_More));
			return result;
		}
		if (ENTER_COPY == type && team.getPlayerNum() < copyConfig.getMinEnterCount()) {
			result.setInfo(GameContext.getI18n().getText(TextId.Team_Copy_Not_Enough));
			return result;
		}
		// 判断队伍内成员是否满足条件
		for (AbstractRole m : team.getMembers()) {
			if (null == m) {
				continue;
			}
			RoleInstance member = (RoleInstance) m;
			// 判断是否离线
			if (!GameContext.getOnlineCenter().isOnlineByRoleId(member.getRoleId())) {
				result.setInfo(GameContext.getI18n().messageFormat(TextId.Team_Copy_Role_Not_Online, member.getRoleName()));
				result.setNotifyTeam(true);
				return result;
			}
			// 判断是否已经报名擂台赛
			sacred.alliance.magic.app.arena.ApplyInfo arenaApply = GameContext.getArenaApp().getApplyInfo(member.getRoleId());
			if (null != arenaApply) {
				result.setInfo(GameContext.getI18n().messageFormat(TextId.Team_Copy_Role_In_Arena, member.getRoleName()));
				result.setNotifyTeam(true);
				return result;
			}
			// 判断是否满足副本进入条件
			Result condResult = copyConfig.enterCondition(member);
			if (!condResult.isSuccess()) {
				result.setInfo(GameContext.getI18n().messageFormat(TextId.Team_Copy_Not_Match, member.getRoleName()) + condResult.getInfo());
				result.setNotifyTeam(true);
				return result;
			}
			// 副本进入次数判断
			if (!GameContext.getCopyLogicApp().isEnterCountEnough(member, copyConfig)) {
				result.setInfo(GameContext.getI18n().messageFormat(TextId.Copy_Team_Member_Today_Count_Finished, member.getRoleName()));
				result.setNotifyTeam(true);
				return result;
			}
		}
		result.success();
		return result;
	}

	/**
	 * 报名组队副本
	 * @param role
	 * @return
	 */
	@Override
	public TeamResult apply(RoleInstance role, short copyId, byte type) {
		TeamResult result = new TeamResult();
		boolean flag = false;// 是否是单人标记
		Team team = role.getTeam();
		if (null == team) {
			flag = true;
			team = new PlayerTeam(role);
		} else if (team.getPlayerNum() <= 1) {
			flag = true;
		}
		// 判断是否符合条件报名
		result = this.canApply(role, copyId, type);
		if (!result.isSuccess()) {
			return result;
		}
		result.failure();
		CopyConfig copyConfig = GameContext.getCopyLogicApp().getCopyConfig(copyId);
		// 如果不是个人，需要队员确认
		if (!flag) {
			// 创建确认信息并放到内存中
			CopyTeamConfirm teamConfirm = new CopyTeamConfirm();
			teamConfirm.setRoleId(role.getRoleId());
			teamConfirm.setCopyId(copyId);
			teamConfirm.setMemberNum(team.getMembers().size());
			teamConfirm.setType(type);
			this.teamCopyCache.put(team.getTeamId(), teamConfirm);
			// 发送给客户端的二次确认信息
			String message = null;
			if (ENTER_COPY == type) {
				message = GameContext.getI18n().messageFormat(TextId.COPY_RESULT_ENTER, copyConfig.getCopyName());
			} else {
				message = GameContext.getI18n().messageFormat(TextId.MATCH_RESULT_ENTER, copyConfig.getCopyName());
			}
			// 通知队内成员二次确认消息
			for (AbstractRole r : team.getMembers()) {
				if (null == r) {
					continue;
				}
				if (team.isLeader(r)) {
					continue;
				}
				RoleInstance roleInstance = (RoleInstance) r;
				this.sendConfirmMessage(roleInstance, message);
			}
			result.setInfo(GameContext.getI18n().getText(TextId.Team_Wait_Others));
			return result;
		} else {
			if (ENTER_COPY == type) {
				this.matchSuccess(team, copyId);
				result.setIgnore(true);
				return result;
			}
			boolean inMatch = this.addApplyInfo(role, team, copyId);
			if (!inMatch) {
				result.setIgnore(true);
				return result;
			}
		}
		// 单人加入匹配队列
		result.setInfo(GameContext.getI18n().messageFormat(TextId.Team_Panel_Enter_Match, copyConfig.getCopyName()));
		result.success();
		return result;
	}
	
	/**
	 * 通知队内成员二次确认消息
	 * @param team
	 * @param copyId
	 */
	private void sendConfirmMessage(RoleInstance role, String info) {
		C0007_ConfirmationNotifyMessage confirmMsg = new C0007_ConfirmationNotifyMessage();
		confirmMsg.setAffirmCmdId(ENTER_COPY_CMD);
		confirmMsg.setAffirmParam(CopyTeamConfirm.AFFIRM);
		confirmMsg.setCancelCmdId(ENTER_COPY_CMD);
		confirmMsg.setCancelParam(CopyTeamConfirm.CANCEL);
		confirmMsg.setInfo(info);
		confirmMsg.setTime((byte) 30);
		role.getBehavior().sendMessage(confirmMsg);
	}
	
	/**
	 * 增加匹配信息
	 * @param role
	 * @param team
	 * @param copyId
	 */
	private boolean addApplyInfo(RoleInstance role, Team team, short copyId) {
		// 如果队伍已满直接进入副本
		CopyConfig copyConfig = GameContext.getCopyLogicApp().getCopyConfig(copyId);
		if (team.getPlayerNum() >= copyConfig.getMaxEnterCount()) {
			this.matchSuccess(team, copyId);
			// 直接进入副本，未曾加入队列
			return false;
		}
		// 构建报名信息类
		ApplyInfo info = new ApplyInfo(role.getTeam(), copyId);
		this.applyMap.put(team.getTeamId(), info);
		return true;
	}
	
	/**
	 * 取消报名
	 * @param role
	 * @return
	 */
	@Override
	public TeamResult cancel(RoleInstance role, String enterCopyId) {
		TeamResult result = new TeamResult();
		try {
			ApplyInfo applyInfo = this.getApplyInfo(role);
			CopyConfig enterCopyConfig = null;
			if (!Util.isEmpty(enterCopyId)) {
				enterCopyConfig = GameContext.getCopyLogicApp().getCopyConfig(Short.parseShort(enterCopyId));
			}
			if (null != enterCopyConfig) {
				if (null == applyInfo) {
					this.sendEnterSignCopyMessage(role, enterCopyConfig);
					result.setIgnore(true);
					return result;
				}
				result = this.teamCancelApply(role.getTeam(), applyInfo);
				this.sendEnterSignCopyMessage(role, enterCopyConfig);
				return result;
			}
			// 如果没有匹配信息
			if (null == applyInfo) {
				result.setInfo(GameContext.getI18n().getText(TextId.Team_Copy_Role_Not_Apply));
				return result;
			}
			Team team = role.getTeam();
			if (!team.isLeader(role)) {
				result.setInfo(GameContext.getI18n().getText(TextId.Team_Copy_Lader_Can_Apply));
				return result;
			}
			result = this.teamCancelApply(team, applyInfo);
		} catch	(Exception e) {
			logger.error("CopyTeamAppImpl.cancel error!", e);
		}
		return result;
	}
	
	/**
	 * 取消匹配
	 * @param team
	 * @param applyInfo
	 * @return
	 */
	private TeamResult teamCancelApply(Team team, ApplyInfo applyInfo) {
		TeamResult result = new TeamResult();
		if (null == team) {
			return result;
		}
		this.removeApplyInfo(team.getTeamId());
		CopyConfig config = GameContext.getCopyLogicApp().getCopyConfig(applyInfo.getCopyId());
		String copyName = "";
		if (null != config) {
			copyName = config.getCopyName();
		}
		String message = GameContext.getI18n().messageFormat(TextId.Team_Panel_Leave_Match, copyName);
		result.setInfo(message);
		// 通知其他队员离开
		applyInfo.notifyLeave(message);
		result.success();
		return result;
	}
	
	/**
	 * 发送进入副本消息
	 * @param role
	 */
	private void sendEnterSignCopyMessage(RoleInstance role, CopyConfig copyConfig) {
		try {
			C0209_CopyEnterReqMessage message = new C0209_CopyEnterReqMessage();
			message.setCopyId(copyConfig.getCopyId());
			role.getBehavior().addCumulateEvent(message);
		} catch (Exception e) {
			logger.error("CopyTeamAppImpl.sendEnterSignCopyMessage error!", e);
		}
	}

	@Override
	public ApplyInfo getApplyInfo(RoleInstance role) {
		Team team = role.getTeam();
		if (null == team) {
			return null;
		}
		return this.applyMap.get(team.getTeamId());
	}

	@Override
	public boolean inApplyStatus(RoleInstance role) {
		return null != this.getApplyInfo(role);
	}

	@Override
	public void systemMatch() {
		try {
			int size = this.applyMap.size();
			if (size <= 0) {
				return;
			}
			Map<Short, MatchResult> groupMap = new HashMap<Short, MatchResult>();
			List<ApplyInfo> list = new ArrayList<ApplyInfo>();
			list.addAll(this.applyMap.values());
			for (ApplyInfo info : list) {
				try {
					if (!this.canMatch(info)) {
						this.teamCancelApply(info.getTeam(), info);
						// 发送匹配失败消息
						this.sendMatchFail(info);
						continue;
					}
				} catch (Exception ex) {
					logger.error("", ex);
				}
				// 分组
				short copyId = info.getCopyId();
				MatchResult match = groupMap.get(copyId);
				if (null == match) {
					match = new MatchResult(copyId);
					groupMap.put(copyId, match);
				}
				match.addApplyInfo(info);
			}
			for (MatchResult result : groupMap.values()) {
				result.match();
			}
		} catch (Exception ex) {
			logger.error("", ex);
		} finally {
			this.matchNow = false;
		}
	}

	@Override
	public void removeApplyInfo(String teamId) {
		if (null == teamId) {
			return;
		}
		this.applyMap.remove(teamId);
		return;
	}

	@Override
	public void matchSuccess(Team team, short copyId) {
		if (null == team) {
			return;
		}
		// 封装进入副本信息
		C0209_CopyEnterReqMessage message = new C0209_CopyEnterReqMessage();
		message.setCopyId(copyId);
		// 模拟队内成员发送进入副本信息
		for (AbstractRole role : team.getMembers()) {
			if (null == role) {
				continue;
			}
			role.getBehavior().addCumulateEvent(message);
		}
	}
	
	/**
	 * 获得组队副本二次确认信息
	 * @param teamId
	 * @return
	 */
	private CopyTeamConfirm getCopyTeamConfirm(String teamId) {
		return this.teamCopyCache.getQuiet(teamId);
	}
	
	/**
	 * 确认组队副本
	 * @param role
	 * @param confirm
	 */
	@Override
	public void copyTeamConfirm(RoleInstance role, String confirm) {
		Team team = role.getTeam();
		if (null == team) {
			return;
		}
		// 如果有人拒绝组队副本
		if (CopyTeamConfirm.CANCEL.equals(confirm)) {
			String message = GameContext.getI18n().messageFormat(TextId.Team_Copy_Role_Refuse, role.getRoleName());
			RoleInstance roleInstance = (RoleInstance) team.getLeader();
			C0003_TipNotifyMessage msg = new C0003_TipNotifyMessage();
			msg.setMsgContext(message);
			roleInstance.getBehavior().sendMessage(msg);
			GameContext.getChatApp().sendSysMessage(ChatSysName.Copy_Team, ChannelType.Team, message, null, team);
			this.teamCopyCache.removeQuiet(team.getTeamId());
			return;
		}
		CopyTeamConfirm teamConfirm = this.getCopyTeamConfirm(team.getTeamId());
		if (null == teamConfirm) {
			return;
		} 
		if (teamConfirm.memberConfirm(role.getRoleId())) {
			// 如果是进入副本，直接进入副本
			if (ENTER_COPY == teamConfirm.getType()) {
				this.matchSuccess(team, teamConfirm.getCopyId());
				this.teamCopyCache.removeQuiet(team.getTeamId());
				return;
			}
			boolean inMatch = this.addApplyInfo(role, team, teamConfirm.getCopyId());
			if (inMatch) {
				CopyConfig copyConfig = GameContext.getCopyLogicApp().getCopyConfig(teamConfirm.getCopyId());
				if (null == copyConfig) {
					return;
				}
				C0220_CopyTeamApplyRespMessage message = new C0220_CopyTeamApplyRespMessage();
				message.setStatus((byte) 1);
				message.setCopyId(teamConfirm.getCopyId());
				message.setInfo(GameContext.getI18n().messageFormat(TextId.Team_Panel_Enter_Match, copyConfig.getCopyName()));
				// 给每个队员发送进入队列消息
				for (AbstractRole r : team.getMembers()) {
					if (null == role) {
						continue;
					}
					RoleInstance member = (RoleInstance) r;
					member.getBehavior().sendMessage(message);
				}
			}
			this.teamCopyCache.removeQuiet(team.getTeamId());
		}
	}
	
	public void setTeamCopyCache(Cache<String, CopyTeamConfirm> teamCopyCache) {
		this.teamCopyCache = teamCopyCache;
	}
	
	public Cache<String, CopyTeamConfirm> getTeamCopyCache() {
		return this.teamCopyCache;
	}

	/**
	 * 是否满足副本进入条件（单人）
	 * @param role
	 * @param copyId
	 * @return
	 */
	@Override
	public Result canEnterCopy(RoleInstance role, short copyId) {
		Result result = new Result();
		// 验证副本进入条件
		CopyConfig copyConfig = GameContext.getCopyLogicApp().getCopyConfig(copyId);
		if (null == copyConfig) {
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result;
		}
		// 如果不是组队副本
		if (copyConfig.getCopyType() != CopyType.team) {
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result;
		}
		// 判断是否满足副本进入条件
		Result condResult = copyConfig.enterCondition(role);
		if (!condResult.isSuccess()) {
			result.setInfo(condResult.getInfo() + GameContext.getI18n().getText(TextId.Team_Copy_Not_Match));
			return result;
		}
		// 副本进入次数判断
		if (!GameContext.getCopyLogicApp().isEnterCountEnough(role, copyConfig)) {
			result.setInfo(GameContext.getI18n().messageFormat(TextId.Copy_Team_Member_Today_Count_Finished, role.getRoleName()));
			return result;
		}
		result.success();
		return result;
	}

	/**
	 * 是否满足发布副本条件
	 * @param role
	 * @param copyId
	 * @return
	 */
	@Override
	public TeamResult canApplyCopy(RoleInstance role, short copyId) {
		TeamResult result = new TeamResult();
		Team team = role.getTeam();
		// 队长才能报名
		if (!team.isLeader(role)) {
			result.setInfo(GameContext.getI18n().getText(TextId.Team_Copy_Lader_Can_Apply));
			return result;
		}
		// 验证副本进入条件
		CopyConfig copyConfig = GameContext.getCopyLogicApp().getCopyConfig(copyId);
		if (null == copyConfig) {
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result;
		}
		// 如果不是组队副本
		if (copyConfig.getCopyType() != CopyType.team) {
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result;
		}
		// 判断队伍内成员是否满足条件
		for (AbstractRole m : team.getMembers()) {
			if (null == m) {
				continue;
			}
			RoleInstance member = (RoleInstance) m;
			// 判断是否离线
			if (!GameContext.getOnlineCenter().isOnlineByRoleId(member.getRoleId())) {
				result.setInfo(GameContext.getI18n().messageFormat(TextId.Team_Copy_Role_Not_Online, member.getRoleName()));
				result.setNotifyTeam(true);
				return result;
			}
			// 判断是否满足副本进入条件
			Result condResult = copyConfig.enterCondition(member);
			if (!condResult.isSuccess()) {
				result.setInfo(GameContext.getI18n().messageFormat(TextId.Team_Copy_Not_Match, member.getRoleName()) + condResult.getInfo());
				result.setNotifyTeam(true);
				return result;
			}
			// 副本进入次数判断
			if (!GameContext.getCopyLogicApp().isEnterCountEnough(role, copyConfig)) {
				result.setInfo(GameContext.getI18n().messageFormat(TextId.Copy_Team_Member_Today_Count_Finished, member.getRoleName()));
				result.setNotifyTeam(true);
				return result;
			}
		}
		result.success();
		return result;
	}

}
