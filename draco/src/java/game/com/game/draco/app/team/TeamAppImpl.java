package com.game.draco.app.team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.app.count.type.CountType;
import sacred.alliance.magic.base.MapLogicType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.channel.EmptyChannelSession;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.TimeoutConstant;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.channel.ChannelSession;
import sacred.alliance.magic.module.cache.Cache;
import sacred.alliance.magic.module.cache.CacheEvent;
import sacred.alliance.magic.module.cache.CacheListener;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.RTSI;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.app.copy.config.CopyConfig;
import com.game.draco.app.copy.team.vo.TeamResult;
import com.game.draco.app.role.systemset.vo.TeamShieldType;
import com.game.draco.app.team.vo.ApplyInfo;
import com.game.draco.app.team.vo.MatchResult;
import com.game.draco.app.team.vo.TargetTypeLogic;
import com.game.draco.app.team.vo.TeamFullConfig;
import com.game.draco.app.team.vo.TeamPanelTargetType;
import com.game.draco.app.team.vo.TeamTargetConfig;
import com.game.draco.message.internal.C0069_TeamPanelMatchInternalMessage;
import com.game.draco.message.item.TeamPanelTargetDetailItem;
import com.game.draco.message.item.TeamPanelTargetTypeItem;
import com.game.draco.message.push.C1301_TeamApplyNotifyMessage;
import com.game.draco.message.push.C1311_TeamInviteNotifyMessage;
import com.game.draco.message.request.C1302_TeamReplyReqMessage;
import com.game.draco.message.request.C1312_TeamPanelPublishApplyReqMessage;
import com.game.draco.message.response.C1314_TeamPanelMatchingCancelResqMessage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class TeamAppImpl implements TeamApp {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private AtomicBoolean started = new AtomicBoolean(false);
	private Cache<String, Team> teamCache;// 下线后角色ID和队伍对象
	private Map<String, PlayerTeam> publishMap = Maps.newHashMap();// 发布队伍
	private Map<String, ApplyInfo> applyMap = Maps.newLinkedHashMap();// 匹配信息
	private static final short PUBLISH_APPLY = new C1312_TeamPanelPublishApplyReqMessage().getCommandId();// 申请发布入队协议
	private final static ChannelSession emptyChannelSession = new EmptyChannelSession();
	private Thread matchThread = null;
	private boolean matchRunning = false;
	private boolean matchNow = false;
	private Map<String, TeamFullConfig> teamFullConfigMap = Maps.newHashMap();// 组队满员提示
	private Map<Byte, Map<Short, TeamTargetConfig>> teamTargetConfigMap = Maps.newHashMap();// 组队目标配置
	private Map<Byte, TargetTypeLogic> targetTypeLogicMap = Maps.newHashMap();// 组队目标逻辑
	private static final int PUBLISH_CD_TIME = 30;// 发布CD时间30s
	private static final int TIME_INT = 1000;// 时间转换
	
	public void start() {
		// 启动系统匹配线程
		this.matchRunning = true;
		matchThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (matchRunning) {
					try {
						Thread.sleep(getMatchInterval());
					} catch (Exception e) {
					}
					try {
						sendSystemMatchReq();
					} catch (Exception ex) {
						logger.error("", ex);
					}
				}
			}
		});
		matchThread.setName("match thread for copy team ");
		matchThread.start();
		this.initCache();
		this.loadTeamFullConfig();
		this.loadTeamTargetConfig();
		this.loadTeamPanelTypeLogic();
	}
	
	/**
	 * 注册组队目标逻辑
	 */
	@SuppressWarnings("unchecked")
	private void loadTeamPanelTypeLogic() {
		try {
			List<String> pkgList = Lists.newArrayList();
			pkgList.add(TargetTypeLogic.class.getPackage().getName());
			// 获取目录下所有目标逻辑类
			Set<Class> logicList = RTSI.findClass(pkgList, TargetTypeLogic.class);
			for (Class clazz : logicList) {
				TargetTypeLogic logic = (TargetTypeLogic) clazz.newInstance();
				targetTypeLogicMap.put(logic.getTeamPanelTargetType().getType(), logic);
				logger.info("registerLogic:" + clazz.getName());
			}
		} catch (Exception ex) {
			Log4jManager.CHECK.error("register TargetTypeLogic error", ex);
			Log4jManager.checkFail();
		}
	}
	
	/**
	 * 加载队伍满员提示配置
	 */
	private void loadTeamFullConfig() {
		try {
			String xlsPath = GameContext.getPathConfig().getXlsPath();
			String fileName = XlsSheetNameType.team_full_config.getXlsName();
			String sheetName = XlsSheetNameType.team_full_config.getSheetName();
			this.teamFullConfigMap = XlsPojoUtil.sheetToGenericMap(xlsPath + fileName, sheetName, TeamFullConfig.class);
			for (TeamFullConfig config : this.teamFullConfigMap.values()) {
				if (null == config) {
					continue;
				}
				config.init(fileName + Cat.underline + sheetName);
			}
		} catch (Exception e) {
			logger.error("TeamAppImpl.loadTargetConfig error!", e);
			Log4jManager.checkFail();
		}
	}
	
	/**
	 * 加载组队目标配置
	 */
	private void loadTeamTargetConfig() {
		try {
			String xlsPath = GameContext.getPathConfig().getXlsPath();
			String fileName = XlsSheetNameType.team_target_config.getXlsName();
			String sheetName = XlsSheetNameType.team_target_config.getSheetName();
			List<TeamTargetConfig> targetList = XlsPojoUtil.sheetToList(xlsPath + fileName, sheetName, TeamTargetConfig.class);
			for (TeamTargetConfig config : targetList) {
				if (null == config) {
					continue;
				}
				config.init(fileName + Cat.colon + sheetName);
				Map<Short, TeamTargetConfig> targetMap = this.teamTargetConfigMap.get((byte) config.getTargetType());
				if (Util.isEmpty(targetMap)) {
					targetMap = Maps.newHashMap();
					this.teamTargetConfigMap.put((byte) config.getTargetType(), targetMap);
				}
				targetMap.put((short) config.getTargetId(), config);
			}
		} catch (Exception e) {
			logger.error("TeamAppImpl.loadTeamTargetConfig error!", e);
			Log4jManager.checkFail();
		}
	}
	
	/**
	 * 发送系统内部消息（匹配）
	 */
	private void sendSystemMatchReq() {
		// 匹配逻辑未结束
		if (this.matchNow) {
			return ;
		}
		this.matchNow = true;
		C0069_TeamPanelMatchInternalMessage reqMsg = new C0069_TeamPanelMatchInternalMessage();
		GameContext.getUserSocketChannelEventPublisher().publish(null, reqMsg, emptyChannelSession);
	}
	
	/**
	 * 匹配循环时差
	 * @return
	 */
	private int getMatchInterval() {
		return GameContext.getParasConfig().getCopyTeamMatchSecond() * 1000;
	}
	
	/**
	 * 初始化Cache
	 */
	private void initCache() {
		if (!started.compareAndSet(false, true)) {
			return;
		}
		teamCache.addCacheListener(new CacheListener<String, Team>() {
			@Override
			public void entryRemoved(CacheEvent<String, Team> event) {
				// 离线时间超过保护时间踢出队伍
				if (null == event) {
					return;
				}
				Team team = event.getValue();
				if (team == null) {
					return;
				}
				try {
					String roleId = event.getKey();
					Map<String, AbstractRole> off = team.getOfflineMembers();
					if (off.containsKey(roleId)) {
						team.expiredLeaveTeam(roleId);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void entryAccessed(CacheEvent<String, Team> event) {
			}
			@Override
			public void entryAdded(CacheEvent<String, Team> event) {
			}
			@Override
			public void entryCleared(CacheEvent<String, Team> event) {
			}
			@Override
			public void entryExpired(CacheEvent<String, Team> event) {
			}
			@Override
			public void entryUpdated(CacheEvent<String, Team> event) {
			}
		});
	}

	@Override
	public int onLogin(RoleInstance role, Object context) {
		try {
			String roleId = role.getRoleId();
			Team team = this.teamCache.get(roleId);
			if (team == null) {
				return 1;
			}
			// 如果在离线保护期内，重新加入队伍
			Map<String, AbstractRole> off = team.getOfflineMembers();
			if (off.containsKey(roleId)) {
				off.remove(roleId);
				team.memberJoin(role);
			}
		} catch (Exception e) {
			logger.error("onlineLoadTeam error", e);
			return 0;
		}
		return 1;
	}

	@Override
	public Status canBuildTeam(RoleInstance role, RoleInstance targRole) {
		if (null == role || null == targRole) {
			return Status.Team_Role_Not_Online;
		}
		if (role.getIntRoleId() == targRole.getIntRoleId()) {
			return Status.Team_Role_Self;
		}
		// 在同一队伍中，组队失败
		if (this.isInSameTeam(role, targRole)) {
			return Status.Team_In_SameTeam;
		}
		// 双方都有队伍，不是同一个队伍，组队失败
		if (this.hasTeam(role) && this.hasTeam(targRole)) {
			return Status.Team_In_differentTeam;
		}
		Team team = role.getTeam();
		Team targTeam = targRole.getTeam();
		boolean isInviteor = true;// 是否是邀请组队（组队成功后自己是队长）
		if (this.hasTeam(role)) {
			// 邀请组队（自己有队伍，目标没有队伍）
			if (team.getLeader().getIntRoleId() != role.getIntRoleId()) {
				return Status.Team_Role_OwnTeam;
			}
			if (this.hasTeam(targRole)) {
				return Status.Team_Targ_OwnTeam;
			}
		} else if (this.hasTeam(targRole)) {
			// 申请入队（自己没队伍，目标有队伍）
			if (targTeam.isFull()) {
				return Status.Team_Full;
			}
			isInviteor = false;
		}
		Status status = this.checkMapType(role);
		if (!status.isSuccess()) {
			return status;
		}
		status = this.checkMapType(targRole);
		if (!status.isSuccess()) {
			return status;
		}
		// 判断对方是否繁忙
		long currTime = System.currentTimeMillis();
		if (currTime - targRole.getTeamApplyTime() <= TimeoutConstant.Team_Reply_Timeout) {
			return Status.Team_TargetRole_Busy;
		}
		targRole.setTeamApplyTime(currTime);
		TeamShieldType shieldType = TeamShieldType.Open;
		if (isInviteor) {// 组队邀请时，对方是否拒绝
			shieldType = TeamShieldType.get(targRole.getSystemSet().getTeamInvite());
		} else {// 入队申请时，对方是否拒绝
			RoleInstance leader = (RoleInstance) targRole.getTeam().getLeader();
			shieldType = TeamShieldType.get(leader.getSystemSet().getTeamApply());
		}
		if (TeamShieldType.Open == shieldType) {
			// 正常组队，发送组队弹板消息
			this.notifyBuilTeam(role, targRole, isInviteor);
		} else if (TeamShieldType.Auto == shieldType) {
			// 自动组队，不需要给目标发组队弹板，直接组队
			return this.buildTeam(targRole, role);
		}
		return Status.SUCCESS;
	}

	/**
	 * 组队检测地图类型
	 * @param role
	 * @return
	 */
	private Status checkMapType(RoleInstance role) {
		String mapId = role.getMapId();
		sacred.alliance.magic.app.map.Map map = null;
		if (mapId != null) {
			map = GameContext.getMapApp().getMap(mapId);
			int logicType = map.getMapConfig().getLogictype();
			if (MapLogicType.isCopyType((byte) logicType)) {
				return Status.Team_Map_Not_Support;
			}
		}
		return Status.SUCCESS;
	}

	/**
	 * 发送组队弹板消息
	 * @param role
	 * @param targRole
	 * @param isInviteor
	 */
	private void notifyBuilTeam(RoleInstance role, RoleInstance targRole, boolean isInviteor) {
		if (isInviteor) {
			Team team = role.getTeam();
			byte currNum = 1;
			byte maxNum = 4;
			if (null != team) {
				currNum = (byte) team.getPlayerNum();
				maxNum = (byte) team.getMaxPlayerNum();
			}
			C1311_TeamInviteNotifyMessage inviteMsg = new C1311_TeamInviteNotifyMessage();
			inviteMsg.setRoleId(role.getIntRoleId());
			inviteMsg.setRoleName(role.getRoleName());
			inviteMsg.setRoleLevel((byte) role.getLevel());
			inviteMsg.setCurrNum(currNum);
			inviteMsg.setMaxNum(maxNum);
			targRole.getBehavior().sendMessage(inviteMsg);
		} else {
			C1301_TeamApplyNotifyMessage applyMsg = new C1301_TeamApplyNotifyMessage();
			applyMsg.setRoleId(role.getIntRoleId());
			applyMsg.setRoleName(role.getRoleName());
			applyMsg.setRoleLevel((byte) role.getLevel());
			AbstractRole leader = targRole.getTeam().getLeader();
			if (null == leader) {
				leader = targRole;
			}
			leader.getBehavior().sendMessage(applyMsg);
		}
	}

	@Override
	public Status buildTeam(RoleInstance role, RoleInstance invitorRole) {
		if (null == role || null == invitorRole) {
			return Status.Team_Role_Not_Online;
		}
		// 判断当前地图是否允许组队
		MapInstance roleMap = role.getMapInstance();
		MapInstance invitorRoleMap = invitorRole.getMapInstance();
		if (null == roleMap || null == invitorRoleMap || !roleMap.canBuildTeam() || !invitorRoleMap.canBuildTeam()) {
			return Status.Team_Map_Not_Support;
		}
		// 在同一队伍中，组队失败
		if (this.isInSameTeam(role, invitorRole)) {
			return Status.Team_In_SameTeam;
		}
		// 双方不在同一个队伍且，组队失败
		if (this.hasTeam(role) && this.hasTeam(invitorRole)) {
			return Status.Team_In_differentTeam;
		}
		Team team = role.getTeam();
		Team invitorTeam = invitorRole.getTeam();
		// 申请入队（自己有队伍，邀请者没有队伍）
		if (this.hasTeam(role) && !this.hasTeam(invitorRole)) {
			if (team.isFull()) {
				return Status.Team_Full;
			}
			team.memberJoin(invitorRole);
			return Status.SUCCESS;
		}
		// 邀请组队（自己没队伍，邀请者有队伍）
		if (!this.hasTeam(role) && this.hasTeam(invitorRole)) {
			if (invitorTeam.isFull()) {
				return Status.Team_Full;
			}
			invitorTeam.memberJoin(role);
			return Status.SUCCESS;
		}
		// 邀请组队（邀请者和自己都没有队伍，发起邀请的人是队长）
		if (null != invitorTeam) {
			invitorTeam.memberJoin(role);
		} else {
			team = new PlayerTeam(invitorRole, role);
			// new PlayerTeam里面已经同步
			// team.syschDataNotify();
		}
		return Status.SUCCESS;
	}

	/**
	 * 是否拥有多人队伍
	 *  && !role.getTeam().getLeader().getRoleId().equals(role.getRoleId())
	 * @param role
	 * @return
	 */
	private boolean hasTeam(RoleInstance role) {
		return null != role.getTeam() && role.getTeam().getPlayerNum() > 1;
	}

	/**
	 * 是否在同一队伍
	 * @param role1
	 * @param role2
	 * @return
	 */
	@Override
	public boolean isInSameTeam(AbstractRole role1, AbstractRole role2) {
		if (null == role1 || null == role2) {
			return false;
		}
		if (role1.getRoleType() != RoleType.PLAYER || role2.getRoleType() != RoleType.PLAYER) {
			return false;
		}
		RoleInstance player1 = (RoleInstance) role1;
		RoleInstance player2 = (RoleInstance) role2;
		if (null == player1.getTeam() || null == player2.getTeam()) {
			return false;
		}
		if (player1.getTeam().getTeamId().equals(player2.getTeam().getTeamId())) {
			return true;
		}
		return false;
	}

	/**
	 * 获取同一地图的活着的组队玩家数量
	 * @param role
	 * @return
	 */
	public List<AbstractRole> getTeamMembersInSameMap(RoleInstance role) {
		List<AbstractRole> result = new ArrayList<AbstractRole>();
		MapInstance roleMapInstance = role.getMapInstance();
		if (!role.hasTeam() || null == roleMapInstance) {
			if (role.isDeath()) {
				return result;
			}
			result.add(role);
			return result;
		}

		MapInstance currentRoleMap = null;
		for (AbstractRole current : role.getTeam().getMembers()) {
			currentRoleMap = current.getMapInstance();
			if (null == currentRoleMap) {
				continue;
			}
			if (!currentRoleMap.getInstanceId().equals(roleMapInstance.getInstanceId())) {
				continue;
			}
			if (current.isDeath()) {
				continue;
			}
			result.add(current);
		}
		currentRoleMap = null;
		return result;
	}

	/**
	 * 组队系数
	 * @param teamMembers
	 * @return
	 */
	public double teamCoefficient(int teamMembers) {
		double coefficient = 1 - teamMembers * (teamMembers * 0.01 + 0.02) - 0.05;
		return coefficient;
	}

	/**
	 * 加入下线队伍缓存
	 */
	public void addOfflineCache(AbstractRole role) {
		Team team = ((RoleInstance) role).getTeam();
		if (team == null) {
			return;
		}
		teamCache.put(role.getRoleId(), team);
	}

	public Cache<String, Team> getTeamCache() {
		return teamCache;
	}

	public void setTeamCache(Cache<String, Team> teamCache) {
		this.teamCache = teamCache;
	}

	@Override
	public int onLogout(RoleInstance role, Object context) {
		try {
			Team team = role.getTeam();
			if (null == team) {
				return 1;
			}
			team.memberLeave(role, LeaveTeam.offline);
		} catch (Exception ex) {
			logger.error("offline error: TeamApplication().offline()", ex);
			return 0;
		}
		return 1;
	}

	@Override
	public int onCleanup(String roleId, Object context) {
		return 0;
	}

	/**
	 * 获取队伍目标类型列表
	 * @return
	 */
	@Override
	public List<TeamPanelTargetTypeItem> getTeamPanelTargetTypeList(RoleInstance role) {
		List<TeamPanelTargetTypeItem> list = Lists.newArrayList();
		for (TeamPanelTargetType type : TeamPanelTargetType.values()) {
			if (null == type) {
				continue;
			}
			TeamPanelTargetTypeItem item = new TeamPanelTargetTypeItem();
			item.setTargetType(type.getType());
			item.setTargetTypeName(type.getName());
			item.setTargetDetailList(this.getTargetTypeLogic(type.getType()).getTeamPanelTargetDetailItemList());
			list.add(item);
		}
		return list;
	}

	/**
	 * 获取组队目标详情
	 * @param type
	 * @return
	 */
	@Override
	public List<TeamPanelTargetDetailItem> getTeamPanelTargetDetailList(byte targetType) {
		Map<Short, TeamTargetConfig> targetMap = this.getTeamTargetMap(targetType);
		if (Util.isEmpty(targetMap)) {
			return null;
		}
		// 排序
		List<TeamTargetConfig> targetList = Lists.newArrayList();
		targetList.addAll(targetMap.values());
		this.sortTargetConfig(targetList);
		// 封装
		List<TeamPanelTargetDetailItem> list = Lists.newArrayList();
		for (TeamTargetConfig config : targetList) {
			if (null == config) {
				continue;
			}
			TeamPanelTargetDetailItem item = new TeamPanelTargetDetailItem();
			item.setMaxLevel((byte) config.getMaxLevel());
			item.setMaxMember((byte) config.getMaxMember());
			item.setMinLevel((byte) config.getMinLevel());
			item.setMinMember((byte) config.getMinMember());
			item.setTargetId((short) config.getTargetId());
			item.setTargetName(config.getTargetName());
			list.add(item);
		}
		return list;
	}
	
	/**
	 * 目标排序
	 * @param list
	 */
	private void sortTargetConfig(List<TeamTargetConfig> list) {
		Collections.sort(list, new Comparator<TeamTargetConfig>() {
			@Override
			public int compare(TeamTargetConfig config1, TeamTargetConfig config2) {
				if (config1.getMinLevel() > config2.getMinLevel()) {
					return 1;
				}
				if (config1.getMinLevel() < config2.getMinLevel()) {
					return -1;
				}
				if (config1.getTargetId() > config2.getTargetId()) {
					return 1;
				}
				if (config1.getTargetId() < config2.getTargetId()) {
					return -1;
				}
				return 0;
			}
		});
	}

	/**
	 * 获取匹配信息
	 * @param teamId
	 * @return
	 */
	@Override
	public ApplyInfo getMatchApplyInfo(String teamId) {
		return this.applyMap.get(teamId);
	}

	/**
	 * 发布组队信息
	 * @param role
	 * @param applyInfo
	 * @return
	 */
	@Override
	public TeamResult teamPublish(RoleInstance role, byte targetType, short targetId, byte number) {
		TeamResult result = new TeamResult();
		// 如果在发布CD中
		if (this.getPublishCDTime(role) > 0) {
			result.setInfo(GameContext.getI18n().messageFormat(TextId.Team_Panel_Publish_CDTime, this.getPublishCDTime(role)));
			return result;
		}
		// 获取队伍
		Team team = role.getTeam();
		if (null == team) {
			team = new PlayerTeam(role);
		}
		PlayerTeam playerTeam = (PlayerTeam) team;
		// 如果队伍在匹配中更改队伍目标（客户端判断）
		ApplyInfo applyInfo = this.getMatchApplyInfo(playerTeam.getTeamId());
		if (null != applyInfo) {
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result;
		}
		// 验证是否满足目标，必须保证有队伍
		result = this.canApplyTarget(team, targetType, targetId);
		if (!result.isSuccess()) {
			return result;
		}
		result.failure();
		// 初始化队伍目标信息
		playerTeam.initTarget(targetType, targetId, number);
		// 生成连接
		String info = GameContext.getI18n().messageFormat(TextId.Team_Panel_Publish_Message, role.getRoleName(), role.getLevel(),
				PUBLISH_APPLY + Cat.colon + team.getTeamId() + Cat.comma + playerTeam.getTarget(), this.getTargetName(targetType, targetId),
				this.getTargetMinLevel(targetType, targetId), team.getPlayerNum(), team.getMaxPlayerNum());
		// 发布连接
		GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.World, info, null, null);
		// 保存发布队伍
		this.publishMap.put(playerTeam.getTeamId(), playerTeam);
		// 设置队伍状态为发布中
		playerTeam.setPublish(true);
		result.setInfo(GameContext.getI18n().getText(TextId.Team_Panel_Publish_Success));
		role.getRoleCount().changeTimes(CountType.PublishTime, new Date().getTime());
		result.success();
		return result;
	}
	
	/**
	 * 获取cd时间
	 * @param role
	 * @return
	 */
	private int getPublishCDTime(RoleInstance role) {
		long lastChallengeTime = role.getRoleCount().getRoleTimesToLong(CountType.PublishTime);
		if (lastChallengeTime <= 0) {
			return 0;
		}
		int cdTime = PUBLISH_CD_TIME - this.getIntTime(new Date().getTime() - lastChallengeTime);
		return cdTime >= 0 ? cdTime : 0;
	}
	
	/**
	 * 获得当前时间
	 * @return
	 */
	private int getIntTime(long date) {
		int nowTime = (int) (date / TIME_INT);
		return nowTime;
	}

	/**
	 * 获取目标的名称
	 * @param targetType
	 * @param targetId
	 * @return
	 */
	@Override
	public String getTargetName(byte targetType, short targetId) {
		TargetTypeLogic logic = this.getTargetTypeLogic(targetType);
		if (null == logic) {
			return "";
		}
		return logic.getTargetName(targetId);
	}
	
	/**
	 * 获取目标的要求等级
	 * @param targetType
	 * @param targetId
	 * @return
	 */
	private String getTargetMinLevel(byte targetType, short targetId) {
		switch (TeamPanelTargetType.getTeamPanelTargetType(targetType)) {
		case every:
			return "";
		case field:
			return "";
		case copy:
			CopyConfig copyConfig = GameContext.getCopyLogicApp().getCopyConfig(targetId);
			if (null == copyConfig) {
				break;
			}
			return "Lv" + copyConfig.getMinLevel();
		case active:
			Active active = GameContext.getActiveApp().getActive(targetId);
			if (null == active) {
				break;
			}
			return "Lv" + active.getMinLevel();
		}
		return "";
	}

//	/**
//	 * 成员是否符合所选择目标
//	 * @param role
//	 * @param applyInfo
//	 * @return
//	 */
//	private Result canEnterTarget(RoleInstance role, byte targetType, short targetId) {
//		Result result = new Result();
//		switch (TeamPanelTargetType.getTeamPanelTargetType(targetType)) {
//		case every:
//			break;
//		case field:
//			break;
//		case copy:
//			return GameContext.getCopyTeamApp().canEnterCopy(role, targetId);
//		case active:
//			return GameContext.getActiveApp().canEnterActive(role, targetId);
//		}
//		result.success();
//		return result;
//	}
	
	/**
	 * 小队是否可以发布或报名
	 * @param team
	 * @param targetType
	 * @param targetId
	 * @return
	 */
	private TeamResult canApplyTarget(Team team, byte targetType, short targetId) {
		TeamResult result = new TeamResult();
		// 获取组队目标类型
		TeamPanelTargetType teamPanelTargetType = TeamPanelTargetType.getTeamPanelTargetType(targetType);
		if (null == teamPanelTargetType) {
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result;
		}
		// 如果组队目标为任意目标或野外挂机
		if (TeamPanelTargetType.every == teamPanelTargetType || TeamPanelTargetType.field == teamPanelTargetType) {
			result.success();
			return result;
		}
		// 获取组队目标配置
		TeamTargetConfig targetConfig = this.getTeamTargetConfig(targetType, targetId);
		if (null == targetConfig) {
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result;
		}
		// 人数超过（客户端有判断）
		if (team.getPlayerNum() > targetConfig.getMaxMember()) {
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result;
		}
		// 获取该类型逻辑
		TargetTypeLogic targetTypeLogic = this.getTargetTypeLogic(targetType);
		if (null == targetTypeLogic) {
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result;
		}
		// 判断等级是否符合（因为有离线玩家不允许报名，所以只需判断在线是否符合）
		for (AbstractRole role : team.getMembers()) {
			if (null == role) {
				continue;
			}
			// 判断队员是否满足进入条件
			result = this.canEnterTarget(targetTypeLogic, (RoleInstance) role, targetConfig, targetId);
			if (!result.isSuccess()) {
				return result;
			}
		}
		result.success();
		return result;
	}
	
	/**
	 * 队伍是否符合条件
	 * @param targetTypeLogic
	 * @param role
	 * @param targetConfig
	 * @param targetId
	 * @return
	 */
	private TeamResult canEnterTarget(TargetTypeLogic targetTypeLogic, RoleInstance role, TeamTargetConfig targetConfig, short targetId) {
		TeamResult result = new TeamResult();
		// 等级不足
		if (role.getLevel() < targetConfig.getMinLevel()) {
			result.setNotifyTeam(true);// 通知队伍
			result.setInfo(GameContext.getI18n().messageFormat(TextId.Team_Panel_Level_Not_Enough, role.getRoleName()));
			return result;
		}
		// 等级超过
		if (role.getLevel() > targetConfig.getMaxLevel()) {
			result.setNotifyTeam(true);// 通知队伍
			result.setInfo(GameContext.getI18n().messageFormat(TextId.Team_Panel_Level_Exceed, role.getRoleName()));
			return result;
		}
		// 判断玩家是否还有可参与次数
		if (!targetTypeLogic.countEnough((RoleInstance) role, targetId)) {
			result.setNotifyTeam(true);// 通知队伍
			result.setInfo(GameContext.getI18n().messageFormat(TextId.Team_Panel_Count_Not_Enough, role.getRoleName()));
			return result;
		}
		result.success();
		return result;
	}
	
	/**
	 * 取消组队发布
	 * @param role
	 * @param applyInfo
	 * @return
	 */
	@Override
	public Result cancelTeamPublish(RoleInstance role) {
		Result result = new Result();
		Team team = role.getTeam();
		if (null == team) {
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result;
		}
		if (null == this.getPublishTeam(team.getTeamId())) {
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result;
		}
		PlayerTeam playerTeam = (PlayerTeam) team;
		if (!team.isLeader(role)) {
			result.setInfo(GameContext.getI18n().getText(TextId.Team_Copy_Lader_Can_Apply));
			return result;
		}
		if (!playerTeam.isPublish()) {
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result;
		}
		playerTeam.setPublish(false);
		this.removePublishInfo(team.getTeamId());
		result.setInfo(GameContext.getI18n().getText(TextId.Team_Panel_Publish_Cancel));
		result.success();
		return result;
	}

	/**
	 * 申请加入发布队伍
	 * @param role
	 * @return
	 */
	@Override
	public Result teamPanelPublishApply(RoleInstance role, PlayerTeam team) {
		Result result = new Result();
		TargetTypeLogic targetTypeLogic = this.getTargetTypeLogic(team.getTargetType());
		TeamTargetConfig teamTargetConfig = this.getTeamTargetConfig(team.getTargetType(), team.getTargetId());
		if (null == targetTypeLogic || null == teamTargetConfig) {
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result;
		}
		// 判断是否可参与目标
		result = this.canEnterTarget(targetTypeLogic, role, teamTargetConfig, team.getTargetId());
		if (!result.isSuccess()) {
			return result;
		}
		// 模拟发送答复队长组队邀请协议
		C1302_TeamReplyReqMessage message = new C1302_TeamReplyReqMessage();
		message.setRoleId(Integer.parseInt(team.getLeader().getRoleId()));
		message.setType((byte) 1);
		role.getBehavior().addCumulateEvent(message);
		// 提示申请成功
		result.setInfo(GameContext.getI18n().getText(TextId.Team_Panel_Publish_Team));
		result.success();
		return result;
	}
	
	/**
	 * 获取发布队伍
	 * @param roleId
	 * @return
	 */
	@Override
	public PlayerTeam getPublishTeam(String teamId) {
		return this.publishMap.get(teamId);
	}

	@Override
	public void setArgs(Object arg0) {
	}

	@Override
	public void stop() {
	}

	/**
	 * 系统匹配
	 */
	@Override
	public void systemMatch() {
		try {
			int size = this.applyMap.size();
			if (size <= 0) {
				return ;
			}
			Map<String, MatchResult> groupMap = Maps.newHashMap();
			List<ApplyInfo> list = new ArrayList<ApplyInfo>();
			list.addAll(this.applyMap.values());
			for (ApplyInfo info : list) {
				try {
					if (!this.canMatch(info)) {
						this.teamCancelApply(info.getTeam(), info);
						continue ;
					}
				} catch (Exception ex) {
					logger.error("", ex);
				}
				// 分组
				String target = info.getTarget();
				MatchResult match = groupMap.get(target);
				if (null == match) {
					match = new MatchResult(target);
					groupMap.put(target, match);
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
	
	/**
	 * 是否满足匹配条件
	 * @param applyInfo
	 * @return
	 */
	private boolean canMatch(ApplyInfo applyInfo) {
		for (AbstractRole role : applyInfo.getApplyRoles()) {
			RoleInstance player = (RoleInstance) role;
			Team team = player.getTeam();
			if (null == team) {
				return false;
			}
			// 如果更换队伍，取消匹配
			if (!team.getTeamId().equals(applyInfo.getTeamId())) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 取消匹配
	 * @param team
	 * @param applyInfo
	 * @return
	 */
	private void teamCancelApply(Team team, ApplyInfo applyInfo) {
		if (null == team) {
			return ;
		}
		this.removeApplyInfo(applyInfo.getTeamId());
		// 通知队长离开匹配队列
		C1314_TeamPanelMatchingCancelResqMessage message = new C1314_TeamPanelMatchingCancelResqMessage();
		message.setStatus((byte) 1);
		message.setInfo(GameContext.getI18n().messageFormat(TextId.Team_Panel_Leave_Match, this.getTargetName(applyInfo.getTargetType(), applyInfo.getTargetId())));
		RoleInstance role = (RoleInstance) team.getLeader();
		role.getBehavior().sendMessage(message);
	}
	
	/**
	 * 移除内存中匹配信息
	 * @param teamId
	 */
	@Override
	public void removeApplyInfo(String teamId) {
		this.applyMap.remove(teamId);
	}

	/**
	 * 报名匹配
	 * @param role
	 * @param applyInfo
	 * @return
	 */
	@Override
	public TeamResult teamApply(RoleInstance role, byte targetType, short targetId, byte number) {
		TeamResult result = new TeamResult();
		// 获取队伍（如果玩家没有队伍，创建一人队伍）
		Team team = role.getTeam();
		if (null == team) {
			team = new PlayerTeam(role);
		}
		// 如果设置最大人数小于当前队伍人数（客户端错误）
		PlayerTeam playerTeam = (PlayerTeam) team;
		if (number < playerTeam.getPlayerNum()) {
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result;
		}
		// 如果队伍已发布，不允许匹配（客户端判断）
		if (playerTeam.isPublish()) {
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result;
		}
		// 如果已经在匹配队列中不能匹配（客户端错误）
		ApplyInfo oldApplyInfo = this.getMatchApplyInfo(team.getTeamId());
		if (null != oldApplyInfo) {
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result;
		}
		// 如果队伍中有离线玩家不允许匹配
		if (!Util.isEmpty(playerTeam.getOfflineMembers())) {
			result.setInfo(GameContext.getI18n().messageFormat(TextId.Team_Copy_Role_Not_Online, this.getTeamOfflineName(playerTeam)));
			return result;
		}
		// 验证是否可以匹配
		result = this.canApplyTarget(team, targetType, targetId);
		if (!result.isSuccess()) {
			return result;
		}
		result.failure();// 重置
		// 初始话队伍的目标信息
		playerTeam.initTarget(targetType, targetId, number);
		if (playerTeam.isFull()) {
			result.setInfo(GameContext.getI18n().getText(TextId.Team_Full));
			return result;
		}
		// 初始化匹配信息中队伍信息
		ApplyInfo applyInfo = new ApplyInfo(playerTeam);
		this.applyMap.put(playerTeam.getTeamId(), applyInfo);
		playerTeam.notifyTeam(GameContext.getI18n().messageFormat(TextId.Team_Panel_Enter_Match, this.getTargetName(targetType, targetId)));
		result.setInfo(GameContext.getI18n().getText(TextId.Team_Panel_Match_Success));
		result.success();
		return result;
	}
	
	/**
	 * 获取离线玩家名称
	 * @param team
	 * @return
	 */
	private String getTeamOfflineName(Team team) {
		Map<String, AbstractRole> offlineMembers = team.getOfflineMembers();
		if (Util.isEmpty(offlineMembers)) {
			return "";
		}
		StringBuffer buffer = new StringBuffer();
		String cat = "";
		for (AbstractRole role : offlineMembers.values()) {
			if (null == role) {
				continue;
			}
			buffer.append(cat).append(role.getRoleName());
			cat = Cat.comma;
		}
		return buffer.toString();
	}
	
	/**
	 * 取消匹配
	 * @param role
	 * @return
	 */
	@Override
	public Result cancelTeamApply(RoleInstance role) {
		Result result = new Result();
		// 获取队伍
		Team team = role.getTeam();
		if (null == team) {
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result;
		}
		// 只有队长可以报名或取消报名
		if (!team.isLeader(role)) {
			result.setInfo(GameContext.getI18n().getText(TextId.Team_Copy_Lader_Can_Apply));
			return result;
		}
		// 获取匹配信息
		ApplyInfo applyInfo = this.getMatchApplyInfo(team.getTeamId());
		if (null == applyInfo) {
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result;
		}
		// 移除匹配队列
		this.removeApplyInfo(applyInfo.getTeamId());
		// 通知队伍，退出匹配队列
		PlayerTeam playerTeam = (PlayerTeam) team;
		playerTeam.notifyTeam(GameContext.getI18n().messageFormat(TextId.Team_Panel_Leave_Match, this.getTargetName(applyInfo.getTargetType(), applyInfo.getTargetId())));
		result.setInfo(GameContext.getI18n().getText(TextId.Team_Panel_Match_Cancel));
		result.success();
		return result;
	}

	/**
	 * 移除发布信息
	 * @param teamId
	 */
	@Override
	public void removePublishInfo(String teamId) {
		this.publishMap.remove(teamId);
	}
	
	/**
	 * 获取队伍目标配置
	 * @param target
	 * @return
	 */
	@Override
	public TeamFullConfig getTeamFullConfig(byte targetType, short targetId) {
		TeamFullConfig config = this.teamFullConfigMap.get(targetType + Cat.underline + targetId);
		if (null == config) {
			targetId = 0;// 通用匹配
		}
		return this.teamFullConfigMap.get(targetType + Cat.underline + targetId);
	}
	
	/**
	 * 获取指定类型队伍目标列表
	 * @param targetType
	 * @return
	 */
	private Map<Short, TeamTargetConfig> getTeamTargetMap(byte targetType) {
		return this.teamTargetConfigMap.get(targetType);
	}
	
	/**
	 * 获取队伍目标
	 * @param targetType
	 * @param targetId
	 * @return
	 */
	@Override
	public TeamTargetConfig getTeamTargetConfig(byte targetType, short targetId) {
		Map<Short, TeamTargetConfig> targetTypeMap = this.getTeamTargetMap(targetType);
		if (Util.isEmpty(targetTypeMap)) {
			return null;
		}
		return targetTypeMap.get(targetId);
	}
	
	/**
	 * 获取组队目标逻辑
	 * @param type
	 * @return
	 */
	private TargetTypeLogic getTargetTypeLogic(byte type) {
		return this.targetTypeLogicMap.get(type);
	}

	/**
	 * 组队副本开始
	 * @param playerTeam
	 * @return
	 */
	@Override
	public Message targetForward(RoleInstance role, PlayerTeam team) {
		TargetTypeLogic logic = this.getTargetTypeLogic(team.getTargetType());
		if (null == logic) {
			return null;
		}
		return logic.targetForword(role, team);
	}

//	@Override
//	public List<TeamPanelIntimateItem> getTeamPanelIntimateItemList(RoleInstance role) {
//		Team team = role.getTeam();
//		if (null == team || team.getPlayerNum() <= 1) {
//			return null;
//		}
//		List<TeamPanelIntimateItem> list = Lists.newArrayList();
//		for (AbstractRole ar : team.getMembers()) {
//			if (null == ar) {
//				continue;
//			}
//			SocialIntimateConfig config = GameContext.getSocialApp().getSocialIntimateConfig(GameContext.getSocialApp().getFriendIntimate(role, (RoleInstance) ar));
//			if (null == config || config.getLevel() <= 0) {
//				continue;
//			}
//			TeamPanelIntimateItem item = new TeamPanelIntimateItem();
//			item.setLevel((byte) config.getLevel());
//			item.setRoleName(ar.getRoleName());
//			list.add(item);
//		}
//		return list;
//	}
	
}
