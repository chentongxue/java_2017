package sacred.alliance.magic.vo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.app.map.data.MapNpcBornData;
import sacred.alliance.magic.app.map.data.NpcBorn;
import sacred.alliance.magic.app.map.point.JumpMapPoint;
import sacred.alliance.magic.base.RebornType;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.base.SettlementType;
import sacred.alliance.magic.base.StateType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.scheduler.job.LoopCount;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.MapInstanceEvent.EventType;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.app.copy.config.CopyConfig;
import com.game.draco.app.copy.config.CopyMapConfig;
import com.game.draco.app.copy.config.CopyMapRoleRule;
import com.game.draco.app.copy.vo.CopyJumpMapPoint;
import com.game.draco.app.copy.vo.CopyNpcRuleType;
import com.game.draco.app.copy.vo.CopyPassJumpType;
import com.game.draco.app.copy.vo.CopyType;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.team.LeaveTeam;
import com.game.draco.app.team.Team;
import com.game.draco.message.item.DeathNotifySelfItem;
import com.game.draco.message.push.C0208_CopyRemainTimeNotifyMessage;
import com.game.draco.message.push.C0226_CopySettlementRespMessage;
import com.game.draco.message.push.C0235_MapJumpPointNotifyMessage;
import com.google.common.collect.Lists;

public class MapMultiCopyInstance extends MapCopyInstance {
	private final int DETECT_ROLE_TIME = 10 * 1000; // 10秒
	private long sendRewardTime = System.currentTimeMillis();
	private final LoopCount detectRoleLoop = new LoopCount(DETECT_ROLE_TIME);
	protected final LoopCount mapStateLoop = new LoopCount(1000);// 1秒
	// 扣一次体力值
	private final int mapStateRefreshMaxIndex = 2;
	private int mapStateRefreshIndex = 0;

	private CopyType copyType;
	private CopyMapConfig mapCopyConfig;
	private CopyPassJumpType passJumpType;
	protected boolean passed = false;
	// 刷怪规则类型、规则ID
	private CopyNpcRuleType ruleType;
	private String npcRuleId;
	private int ruleMaxSize;// 刷怪最大个数
	protected MapState mapState = MapState.init;
	private MapSign mapSign = MapSign.init;
	protected Date startTime;// 开始时间（倒计时结束自动传出）
	private Date timeOverDate;// 倒计时结束时间
	private final int timeOverKickTime = 60 * 1000;// 倒计时结束后地图可停留时间（毫秒）
	private int ruleIndex = 0;// 已刷怪序列 npcRuleList的索引
	private List<NpcBorn> npcBornList;
	private boolean jumpPointRefreshed = false;// 是否已经刷出跳转点

	public MapMultiCopyInstance(sacred.alliance.magic.app.map.Map map) {
		super(map);
	}

	public void init() {
		MapMultiCopyContainer container = (MapMultiCopyContainer) this.getMapContainer();
		this.copyType = container.getCopyType();
		CopyConfig copyConfig = container.getCopyConfig();
		if (null != copyConfig) {
			this.passJumpType = copyConfig.getCopyPassJumpType();
		}
		this.mapCopyConfig = GameContext.getCopyLogicApp().getMapConfig(this.getMap().getMapId());
		this.ruleType = this.mapCopyConfig.getCopyNcpRuleType();
	}

	enum MapState {
		init, // 初始
		refresh, // 刷怪
		refresh_end, // 刷怪结束
		time_over, // 倒计时结束
		;
	}

	enum MapSign {
		init, 
		pass, // 通关标记
		fail,// 失败标记
		end, 
		;
	}

	@Override
	public void doEvent(RoleInstance role, MapInstanceEvent event) {
		if (null == event) {
			return;
		}
		if (event.getEventType() == EventType.refReshRule) {
			this.npcRuleId = event.getEventKey();
			this.ruleMaxSize = GameContext.getRefreshRuleApp().getRefreshMax(Integer.parseInt(this.npcRuleId));
			this.do_mapState_init();
		}
	}

	/**
	 * 几率进入次数
	 */
	@Override
	protected void enter(AbstractRole role) {
		super.enter(role);
		if (role.getRoleType() != RoleType.PLAYER) {
			return;
		}
		// 如果是掉线重新登录的，需要发倒计时
		if (MapState.init != this.mapState) {
			this.notifyCopyRemainTime();
			this.notifyMapRemainTime();
		}
	}

	@Override
	protected void updateSub() throws ServiceException {
		super.updateSub();
		// 10秒循环一次
		if (this.detectRoleLoop.isReachCycle()) {
			// 踢出不是副本拥有者
			this.detectRoleStatus();
		}
		// 1秒循环一次
		if (this.mapStateLoop.isReachCycle()) {
			// 更新容器拥有者时间
			this.updateOwnerTime();

			// 判断倒计时是否结束，如果结束只切换地图状态
			this.timeOver();

			// 根据不同状态做相应的处理
			switch (this.mapState) {
			case init:
				this.do_mapState_init();
				break;
			case refresh:
				this.mapStateRefreshIndex++;
				if (this.mapStateRefreshIndex >= this.mapStateRefreshMaxIndex) {
					this.mapStateRefreshIndex = 0;
					this.do_mapState_refresh();
				}
				break;
			case refresh_end:
				this.do_mapState_refreshEnd();
				break;
			case time_over:
				this.do_mapState_timeOver();
				break;
			}

			// 根据地图上通关标记，处理通关的逻辑
			switch (this.mapSign) {
			case init:
				// 如果通关，标记为已通关
				if (this.passed) {
					this.mapSign = MapSign.pass;
				}
				break;
			case pass:
				this.mapSignLogic();
				this.sendRewardTime = System.currentTimeMillis();
				break;
			case end:
				if (this.copyType == CopyType.team && System.currentTimeMillis() - sendRewardTime > DETECT_ROLE_TIME) {
					this.kickAllRole();
				}
				break;
			case fail:
				this.notifyFail();
				this.sendRewardTime = System.currentTimeMillis();
				break;
			}
		}
	}

	/**
	 * 通关状态逻辑 统计通关时间 发通关提示信息
	 */
	private void mapSignLogic() {
		// 判断是否刷跳转点
		this.refreshJumpPiont();
		// 通关提示信息
		this.notifyPass();
		// 每个人的逻辑
		this.mapPassEachRole();
		// 判断副本是否通关
		this.copyPassLogic();
		// 标记为结束
		this.mapSign = MapSign.end;
	}

	/**
	 * 副本通关奖励 地图通关时，判断副本是否通关
	 */
	private void copyPassLogic() {
		try {
			MapMultiCopyContainer container = (MapMultiCopyContainer) this.getMapContainer();
			container.copyPassReward();
			CopyMapConfig copyMapConfig = GameContext.getCopyLogicApp().getMapConfig(this.getMap().getMapId());
			if (null == copyMapConfig) {
				return ;
			}
			// 如果不是最后一张地图切不是英雄副本，发送结算面板（不弹出），执行挂机
			if (!copyMapConfig.isLastMap() && this.copyType != CopyType.hero) {
				GameContext.getCopyLogicApp().pushCopySettlementMessage(this.getFirstRole(), (byte)1, (byte)1, SettlementType.Hand.getType(), null, null, null);
			}
		} catch (Exception e) {
			logger.error("MapMultiCopyInstance.copyPassLogic error: ", e);
		}
	}

	/**
	 * 地图通关之后，每个人的处理 ①统计锁妖塔通关时间 ②地图通关完成任务
	 */
	private void mapPassEachRole() {
		try {
			String mapId = this.map.getMapId();
			for (RoleInstance role : this.getRoleList()) {
				if (null == role) {
					continue;
				}
				try {
					// 完成任务
					GameContext.getUserQuestApp().copyMapPass(role, mapId);
				} catch (ServiceException e) {
					logger.error("MapMultiCopyInstance.mapPassEachRole error: ", e);
				}
			}
		} catch (Exception e) {
			logger.error("MapMultiCopyInstance.mapPassEachRole error: ", e);
		}
	}

	/**
	 * 刷地图跳转点 地图未通关不会刷
	 */
	private void refreshJumpPiont() {
		// 已经刷过跳转点，就不再刷了
		if (this.jumpPointRefreshed) {
			return;
		}
		short jumpX = this.mapCopyConfig.getJumpX();
		short jumpY = this.mapCopyConfig.getJumpY();
		// 未配置跳转点的位置，则不刷
		if (jumpX <= 0 || jumpY <= 0) {
			return;
		}
		String toMapId = null;
		short toMapX = 0;
		short toMapY = 0;
		// 如果是最后一张地图，刷副本进入点或固定坐标
		if (this.mapCopyConfig.isLastMap()) {
			// 跳转到副本进入点（只使用于单人副本）
			Point point = this.getPersonalLastMapBeforEnterPoint();
			if (null != point) {
				toMapId = point.getMapid();
				toMapX = (short) point.getX();
				toMapY = (short) point.getY();
			} else {
				// 跳转到配置的固定点
				toMapId = this.mapCopyConfig.getToMapId();
				toMapX = this.mapCopyConfig.getToMapX();
				toMapY = this.mapCopyConfig.getToMapY();
			}
		} else {
			toMapId = this.mapCopyConfig.getToMapId();
			toMapX = this.mapCopyConfig.getToMapX();
			toMapY = this.mapCopyConfig.getToMapY();
		}
		// 没有找到可刷出的目标点
		if (Util.isEmpty(toMapId) || jumpX <= 0 || jumpY <= 0) {
			return;
		}
		String jumpMapId = this.map.getMapId();
		JumpMapPoint jumpPoint = null ;
		if(this.mapCopyConfig.isLastMap()){
			//只有最后一地图刷此类型点
			jumpPoint = new CopyJumpMapPoint();
		}else{
			jumpPoint = new JumpMapPoint() ;
		}
		
		jumpPoint.setMapid(jumpMapId);
		jumpPoint.setX(jumpX);
		jumpPoint.setY(jumpY);
		jumpPoint.setTomapid(toMapId);
		jumpPoint.setDesX(toMapX);
		jumpPoint.setDesY(toMapY);
		this.refreshJumpPointList.add(jumpPoint);
		Map toMap = GameContext.getMapApp().getMap(jumpPoint.getTomapid());
		// 通知跳转点message
		C0235_MapJumpPointNotifyMessage tjpnm = new C0235_MapJumpPointNotifyMessage();
		tjpnm.setJumpMapId(jumpMapId);
		tjpnm.setJumpX(jumpX);
		tjpnm.setJumpY(jumpY);
		if (this.isTeamCopy()) {
			tjpnm.setToMapName(GameContext.getI18n().getText(TextId.Team_Copy_Exit));
		} else {
			tjpnm.setToMapName(toMap.getMapConfig().getMapdisplayname());
		}
		this.broadcastMap(null, tjpnm);
		// 已经刷出跳转点，赋值为true
		this.jumpPointRefreshed = true;
	}

	/**
	 * 获取单人副本最后一张地图，角色进入副本前的坐标点
	 * @return
	 */
	private Point getPersonalLastMapBeforEnterPoint() {
		if (CopyType.personal != this.copyType && CopyType.hero != this.copyType) {
			return null;
		}
		if (CopyPassJumpType.Enter_Point != this.passJumpType) {
			return null;
		}
		if (!this.mapCopyConfig.isLastMap()) {
			return null;
		}
		RoleInstance role = this.getFirstRole();
		if (null != role) {
			return role.getCopyBeforePoint();
		}
		return null;
	}

	/**
	 * 判断倒计时是否结束逻辑 （如果计时结束，切换到倒计时结束状态）
	 */
	private void timeOver() {
		// 如果已经是超时状态，不需要处理
		if (MapState.time_over == this.mapState) {
			return;
		}
		// 判断是否超时，切换到超时状态
		MapMultiCopyContainer container = (MapMultiCopyContainer) this.mapContainer;
		boolean isTimeOver = this.isMapTimeOver() || container.isTimeOver();
		if (isTimeOver) {
			this.mapState = MapState.time_over;
			this.timeOverDate = new Date();
		}
	}

	/**
	 * 当前地图倒计时是否结束
	 */
	private boolean isMapTimeOver() {
		// 地图倒计时时间（单位：秒）
		int limitTime = this.mapCopyConfig.getLimitTime();
		// 限时时间<=0表示不需要倒计时
		if (limitTime <= 0 || null == this.startTime) {
			return false;
		}
		int mapTime = DateUtil.getSecondMargin(this.startTime);
		return mapTime > limitTime;
	}

	/**
	 * 倒计时结束逻辑
	 */
	private void do_mapState_timeOver() {
		// 已经通关，不需要将角色传出副本
		if (this.passed) {
			// 容错
			if (null == this.timeOverDate) {
				this.timeOverDate = new Date();
			}
			// 判断倒计时结束的时间，超过了可停留时间，将地图内所有人传出
			long time = DateUtil.getMillisecondGap(this.timeOverDate);
			if (time > this.timeOverKickTime) {
				this.kickAllRole();
			}
			return;
		}
		this.kickAllRole();
	}

	/**
	 * 将地图内所有玩家传出副本
	 */
	private void kickAllRole() {
		for (RoleInstance role : this.getRoleList()) {
			if (null == role) {
				continue;
			}
			// 满血满蓝
			this.perfectBody(role);
			this.kickRole(role);
		}
	}

	/**
	 * 初始化状态的逻辑 找到刷怪规则，切换到刷怪状态
	 */
	private void do_mapState_init() {
		switch (this.ruleType) {
		case Default:
			this.npcRuleId = this.mapCopyConfig.getRuleId();
			this.ruleMaxSize = GameContext.getRefreshRuleApp().getRefreshMax(Integer.parseInt(this.npcRuleId));
			break;
		case Role_Level_Auto:
			RoleInstance role = this.getFirstRole();
			if (null == role) {
				break;
			}
			CopyMapRoleRule rule = GameContext.getCopyLogicApp().getCopyMapRoleRule(role, this.map.getMapId());
			// 如果没有找到合适的规则，有可能是地图里种怪了
			if (null != rule) {
				this.npcRuleId = rule.getRuleId();
				this.ruleMaxSize = GameContext.getRefreshRuleApp().getRefreshMax(Integer.parseInt(this.npcRuleId));
			}
			break;
		case Role_Choose:

			break;
		}
		// 必须切换到刷怪状态，里面会发副本倒计时
		this.change_mapState_init_to_refresh();
	}

	private RoleInstance getFirstRole() {
		for (RoleInstance role : this.getRoleList()) {
			if (null != role) {
				return role;
			}
		}
		return null;
	}

	/**
	 * 切换到刷怪状态 开始计时
	 */
	private void change_mapState_init_to_refresh() {
		this.mapState = MapState.refresh;
		// 开始计时
		Date now = new Date();
		this.startTime = now;
		// 如果是首张地图，副本开始计时
		if (this.mapCopyConfig.isFirstMap()) {
			MapMultiCopyContainer container = (MapMultiCopyContainer) this.mapContainer;
			container.setCopyStartTime(now);
			this.notifyCopyRemainTime();
		}
		// 通知客户端当前地图倒计时
		this.notifyMapRemainTime();
	}

	/**
	 * 主推当前地图倒计时（秒）
	 */
	private void notifyMapRemainTime() {
		if (!this.mapCopyConfig.isTimeLimit()) {
			return;
		}
		// 剩余时间（秒）
		int time = this.mapCopyConfig.getLimitTime() - DateUtil.getSecondMargin(this.startTime);
		if (time < 0) {
			time = 0;
		}
		C0208_CopyRemainTimeNotifyMessage message = new C0208_CopyRemainTimeNotifyMessage();
		message.setType((byte) 0);
		message.setTime(time);
		this.broadcastMap(null, message);
	}

	/**
	 * 主推副本倒计时（秒）
	 */
	private void notifyCopyRemainTime() {
		MapMultiCopyContainer container = (MapMultiCopyContainer) this.mapContainer;
		int time = container.getCopyRemainTime();
		if (0 == time) {
			return;
		}
		C0208_CopyRemainTimeNotifyMessage message = new C0208_CopyRemainTimeNotifyMessage();
		message.setType((byte) 0);
		message.setTime(time);
		this.broadcastMap(null, message);
	}

	/**
	 * 刷怪状态逻辑 根据规则刷怪，无怪可刷或已刷完怪切到刷怪结束状态
	 */
	private void do_mapState_refresh() {
		if (this.ruleIndex >= this.ruleMaxSize) {
			// 如果没有怪可刷，则切换到刷怪结束状态
			// 刷完怪，切换到刷怪结束状态
			this.change_mapState_to_refresh_end();
			return;
		}
		int ruleId = Integer.parseInt(this.npcRuleId);
		this.ruleIndex = GameContext.getRefreshRuleApp().refresh(ruleId, this.ruleIndex, startTime, this, true);
	}

	/**
	 * 切换到刷怪结束状态
	 */
	private void change_mapState_to_refresh_end() {
		this.mapState = MapState.refresh_end;
	}

	/**
	 * 刷怪结束状态的逻辑 判断是否通关
	 */
	private void do_mapState_refreshEnd() {
		// 判断是否通关
		this.isPass();
	}

	/**
	 * 判断是否通关 怪死亡的时候，有判断副本是否通关 因此只需要在刷怪结束后调用此方法
	 */
	private void isPass() {
		try {
			if (this.passed) {
				return;
			}
			// 没有通关条件，则不发送通关消息
			if (!this.mapCopyConfig.hasPassCondition()) {
				this.passed = true;
				return;
			}
			// 没有通关
			if (!this.isCopyPass()) {
				return;
			}
			this.passed = true;
			// 清除所有NPC
			if (1 == this.mapCopyConfig.getPassCleanNpc()) {
				this.clearAllNpc();
				this.clearBaffle();
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	private void clearAllNpc() {
		Collection<NpcInstance> list = new ArrayList<NpcInstance>();
		list.addAll(this.getNpcList());
		this.npcList.clear();
		for (NpcInstance npc : list) {
			this.notifyNpcDeath(npc);
		}
		list.clear();
		list = null;
	}

	private void notifyPass() {
		String passTips = this.mapCopyConfig.getPassTips();
		if (!Util.isEmpty(passTips)) {
			GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Map, passTips, null, this);
		}
	}
	
	/**
	 * 通知副本失败
	 */
	private void notifyFail() {
		C0226_CopySettlementRespMessage message = new C0226_CopySettlementRespMessage();
		message.setInfo(GameContext.getI18n().getText(TextId.Team_Copy_Death_Fail));
		this.broadcastMap(null, message);
		this.mapSign = MapSign.end;
	}

	private boolean isCopyPass() {
		return GameContext.getCopyLogicApp().isCopyPass(this);
	}

	/**
	 * 踢出不是副本拥有者的玩家
	 */
	private void detectRoleStatus() {
		MapMultiCopyContainer container = (MapMultiCopyContainer) this.getMapContainer();
		String ownerId = container.getOwnerId();
		for (RoleInstance role : this.getRoleList()) {
			if (null == role) {
				continue;
			}
			// 如果是限制次数的副本，踢出没有扣除副本次数的玩家
			this.detectRoleForCopyCount(container, role);
			// 组队副本
			if (this.isTeamCopy()) {
				Team team = role.getTeam();
				if (team == null || !ownerId.equals(team.getTeamId())) {
					this.kickRole(role);
				}
			}
		}
	}

	/**
	 * 如果是有次数限制的副本，判断角色是否扣除了副本次数。 如果是没有扣除副本次数，则踢出地图。
	 * @param container
	 * @param role
	 */
	private void detectRoleForCopyCount(MapMultiCopyContainer container, RoleInstance role) {
		try {
			CopyConfig copyConfig = container.getCopyConfig();
			if (null == role || null == container || null == copyConfig) {
				return;
			}
			if (CopyConfig.ENTER_INCR != copyConfig.getIncrType()) {
				return;
			}
			int count = container.getCopyConfig().getCount();
			if (count <= 0) {
				return;
			}
			// 踢出没有扣次数二进入副本的角色
			if (!container.getDeductNumRoleIdSet().contains(role.getRoleId())) {
				this.kickRole(role);
			}
		} catch (RuntimeException e) {
			logger.error(this.getClass().getName() + ".detectRoleForCopyCount error: ", e);
		}
	}

	private void updateOwnerTime() {
		MapMultiCopyContainer container = (MapMultiCopyContainer) this.getMapContainer();
		container.setOwnerUpdateTime(System.currentTimeMillis());
	}

	@Override
	public List<NpcBorn> getNpcBornList() {
		if (null == this.npcBornList) {
			this.npcBornList = new ArrayList<NpcBorn>();
			MapNpcBornData bornData = this.map.getNpcBornData();
			if (null != bornData) {
				this.npcBornList.addAll(bornData.getNpcborn());
			}
		}
		return this.npcBornList;
	}

	@Override
	public void broadcastScreenMap(AbstractRole role, Message message) {
		super.broadcastMap(role, message, 0);
	}

	@Override
	public void broadcastScreenMap(AbstractRole role, Message message, int expireTime) {
		super.broadcastMap(role, message, expireTime);
	}

	public void destroy() {
		super.destroy();
	}

	@Override
	public boolean canEnter(AbstractRole role) {
		return true;
	}

	@Override
	public void exitMap(AbstractRole role) {
		synchronized (this) {
			this.removeAbstractRole(role);
		}
		RoleInstance roleInstance = (RoleInstance) role;
		try {
			if (this.isTeamCopy() && MapSign.init == this.mapSign && roleInstance.getTeam() != null) {
				roleInstance.getTeam().memberLeave(role, LeaveTeam.apply);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		// 处理掉线的情况
		// 记录副本容器ID和所在地图实例ID
		// 如果玩家死亡下线，默认传送到传入地图的出生点
		Point point = roleInstance.getCopyBeforePoint();
		// 容错判断，如果取不到默认出生点，传至角色出生点
		if (null == point) {
			point = GameContext.getRoleBornApp().getRoleBorn().getBornPoint();
		}
		// 如果退出地图时已通关
		if (this.mapCopyConfig.isLastMap() && this.passed) {
			role.setMapId(point.getMapid());
			role.setMapX(point.getX());
			role.setMapY(point.getY());
			return ;
		}
		if (role.isDeath()) {
			roleInstance.setMapId(point.getMapid());
			roleInstance.setMapX(point.getX());
			roleInstance.setMapY(point.getY());
			roleInstance.setCopyLostReLoginInfo("");
			return ;
		}
		roleInstance.setCopyLostReLoginInfo(this.getMapContainer().getInstanceId() + Cat.comma + this.instanceId + Cat.comma + point.getMapid());
	}

	@Override
	public void npcDeath(NpcInstance npc) {
		try {
			super.npcDeath(npc);
		} catch (Exception ex) {
			logger.error("", ex);
		}
		// 判断是否有通关条件
		if (null == this.mapCopyConfig) {
			return;
		}

		CopyMapConfig config = GameContext.getCopyLogicApp().getMapConfig(getMap().getMapId());
		if (config.isKillNpc(npc.getNpcid())) {
			if (config.hasStopRefCondition()) {
				this.ruleIndex = ruleMaxSize;
				return;
			}
		}
		String deathNpcId = this.mapCopyConfig.getNeedKillNpcId();
		if (null == deathNpcId || 0 == deathNpcId.trim().length()) {
			return;
		}
		if (npc.getNpc().getNpcid().equals(deathNpcId)) {
			this.passed = true;
		}
	}


	@Override
	public void footOnPoint(AbstractRole role) throws ServiceException {
		if (!this.passed) {
			return;
		}
		super.footOnPoint(role);
	}
	
	private void resetPersonalCopy(){
		if (CopyType.personal != this.copyType) {
			return;
		}
		MapMultiCopyContainer container = (MapMultiCopyContainer) this.getMapContainer();
		CopyConfig copyConfig = container.getCopyConfig();
		if (copyConfig.getReset() == (byte) 0) {
			return;
		}
		if (null != this.npcBornList) {
			this.npcBornList.clear();
		}
		this.bornIndex = -1;
		Collection<NpcInstance> npcList = this.getNpcList();
		if (npcList != null && !npcList.isEmpty()) {
			for (NpcInstance npc : npcList) {
				this.removeAbstractRole(npc);
				this.notifyNpcDeath(npc);
			}
		}
		resetRefresh();
	}

	@Override
	public void roleDeath(AbstractRole attacker, RoleInstance victim) {
		if (this.isTeamCopy() && this.isAllDeath()) {
			this.mapSign = MapSign.fail;
		}
		super.roleDeath(attacker, victim);
		//重置个人副本
		this.resetPersonalCopy();
	}
	
	/**
	 * 地图内所有玩家都死亡
	 * @return
	 */
	private boolean isAllDeath() {
		for (RoleInstance role : this.getRoleList()) {
			if (null == role) {
				continue;
			}
			if (role.isDeath() || role.inState(StateType.soul)) {
				continue;
			}
			return false;
		}
		return true;
	}

	/**
	 * 重置地图怪
	 */
	private void resetRefresh() {
		this.initNpc(true);
		ruleIndex = 0;
		this.mapState = MapState.refresh;
		do_mapState_refresh();
	}

	
	private boolean isTeamCopy(){
		return this.copyType == CopyType.team ;
	}
	
	/**
	 * 处理组队副本只有灵魂复活
	 */
	protected List<DeathNotifySelfItem> rebornOptionFilter(RoleInstance role){
		if(!this.isTeamCopy()){
			return super.rebornOptionFilter(role);
		}
		//组队副本才有灵魂复活
		List<DeathNotifySelfItem> list = Lists.newArrayList() ;
		DeathNotifySelfItem item = new DeathNotifySelfItem();
		item.setType(RebornType.soul.getId());
		list.add(item);
		return list ;
	}
	
	
	/**
	 * 组队副本并且在灵魂状态则无法使用技能
	 */
	public boolean canUseSkill(RoleInstance role,int skillId){
		if(this.isTeamCopy() && role.inState(StateType.soul)){
			return false ;
		}
		return true ;
	}
	
	public String roleDieEnhanceOptionTips() {
		if(!this.isTeamCopy()){
			return null ;
		}
		return GameContext.getI18n().getText(TextId.SELECT_SOUL_REBORN_TIPS);
	}
	
	/**
	 * 副本中不允许接受组队
	 */
	@Override
	public boolean canBuildTeam() {
		return false;
	}
	
}
