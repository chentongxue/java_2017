package sacred.alliance.magic.vo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import sacred.alliance.magic.app.chat.ChannelType;
import sacred.alliance.magic.app.chat.ChatSysName;
import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.app.map.data.MapNpcBornData;
import sacred.alliance.magic.app.map.data.NpcBorn;
import sacred.alliance.magic.app.map.point.JumpMapPoint;
import sacred.alliance.magic.app.team.Team;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.scheduler.job.LoopCount;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Util;

import com.game.draco.GameContext;
import com.game.draco.app.copy.CopyConfig;
import com.game.draco.app.copy.CopyMapConfig;
import com.game.draco.app.copy.CopyMapRoleRule;
import com.game.draco.app.copy.CopyNcpRuleType;
import com.game.draco.app.copy.CopyPassJumpType;
import com.game.draco.app.copy.CopySignType;
import com.game.draco.app.copy.CopyType;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.message.push.C0208_CopyRemainTimeNotifyMessage;
import com.game.draco.message.push.C0235_MapJumpPointNotifyMessage;

public class MapMultiCopyInstance extends MapCopyInstance  {
	
	private final int DETECT_ROLE_TIME = 10 * 1000; //10秒
	private final LoopCount detectRoleLoop = new LoopCount(DETECT_ROLE_TIME);
	protected final LoopCount mapStateLoop = new LoopCount(1000);//1秒
	//private final LoopCount rolePowerLoop = new LoopCount(60*1000);//1分钟 扣一次体力值 
	private final int mapStateRefreshMaxIndex = 2 ;
	private int mapStateRefreshIndex = 0 ;
	
	private CopyType copyType ;
	private short copyId ;
	private CopyMapConfig mapCopyConfig;
	private CopySignType copySignType;
	private CopyPassJumpType passJumpType;
	//private int minutePower = 0;//每分钟扣除的体力值
	protected boolean passed = false;
	//刷怪规则类型、规则ID
	private CopyNcpRuleType ruleType;
	private String npcRuleId;
	private int ruleMaxSize;//刷怪最大个数
	protected MapState mapState = MapState.init;
	private MapSign mapSign = MapSign.init;
	protected Date startTime;//开始时间（倒计时结束自动传出）
	private Date timeOverDate;//倒计时结束时间
	private final int timeOverKickTime = 60 * 1000;//倒计时结束后地图可停留时间（毫秒）
	private int ruleIndex = 0;//已刷怪序列 npcRuleList的索引
	private List<NpcBorn> npcBornList;
	private boolean jumpPointRefreshed = false;//是否已经刷出跳转点
	
	public MapMultiCopyInstance(sacred.alliance.magic.app.map.Map map) {
		super(map);
	}
	
	public void init(){
		MapMultiCopyContainer container = (MapMultiCopyContainer)this.getMapContainer();
		this.copyType = container.getCopyType();
		this.copyId = container.getCopyId();
		CopyConfig copyConfig = container.getCopyConfig();
		if(null != copyConfig){
			this.copySignType = copyConfig.getCopySignType();
			this.passJumpType = copyConfig.getCopyPassJumpType();
			//this.minutePower = coypConfig.getMinutePower();
		}
		this.mapCopyConfig = GameContext.getCopyLogicApp().getMapConfig(this.getMap().getMapId());
		
		this.ruleType = this.mapCopyConfig.getCopyNcpRuleType();
	}
	
	enum MapState{
		init,//初始
		refresh,//刷怪
		refresh_end,//刷怪结束
		time_over,//倒计时结束
		;
	}
	
	enum MapSign{
		init,
		pass,//通关标记
		end,
		;
	}
	
	/**
	 * 几率进入次数
	 */
	@Override
	protected void enter(AbstractRole role){
		super.enter(role);
		if(role.getRoleType() != RoleType.PLAYER){
			return ;
		}
		//如果是掉线重新登录的，需要发倒计时
		if(MapState.init != this.mapState){
			this.notifyCopyRemainTime();
			this.notifyMapRemainTime();
		}
	}
	
	@Override
	protected void updateSub() throws ServiceException {
			super.updateSub();
			//10秒循环一次
			if(this.detectRoleLoop.isReachCycle()){
				//踢出不是副本拥有者
				this.detectRoleStatus();
			}
			//1秒循环一次
			if(this.mapStateLoop.isReachCycle()){
				//更新容器拥有者时间
				this.updateOwnerTime();
				
				//判断倒计时是否结束，如果结束只切换地图状态
				this.timeOver();
				
				//根据不同状态做相应的处理
				switch(this.mapState){
				case init:
					this.do_mapState_init();
					break;
				case refresh:
					this.mapStateRefreshIndex ++ ;
					if(this.mapStateRefreshIndex >= this.mapStateRefreshMaxIndex){
						this.mapStateRefreshIndex = 0 ;
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
				
				//根据地图上通关标记，处理通关的逻辑
				switch(this.mapSign){
				case init:
					//如果通关，标记为已通关
					if(this.passed){
						this.mapSign = MapSign.pass;
					}
					break;
				case pass:
					this.mapSignLogic();
					break;
				case end:
					break;
				}
			}
			/*//1分钟循环一次，扣除体力值，体力值不足的传出副本
			if(this.rolePowerLoop.isReachCycle()){
				this.disposeRolePower();
			}*/
	}
	
	/**
	 * 处理体力值的逻辑
	 * ①扣除体力值
	 * ②没有体力值则传出副本
	 */
	/*private void disposeRolePower(){
		//不需要消耗体力值
		if(this.minutePower <= 0){
			return;
		}
		for(RoleInstance role : this.getRoleList()){
			if(null == role){
				continue;
			}
			//扣除体力值
			role.getBehavior().changeAttribute(AttributeType.curPower, OperatorType.Decrease, this.minutePower);
			role.getBehavior().notifyAttribute();
			//如果体力值不足，传出副本
			if(role.getCurPower() <= 0){
				this.kickRole(role);
			}
		}
	}*/
	
	/**
	 * 通关状态逻辑
	 * 统计通关时间
	 * 发通关提示信息
	 */
	private void mapSignLogic(){
		//判断是否刷跳转点
		this.refreshJumpPiont();
		//通关提示信息
		this.notifyPass();
		//每个人的逻辑
		this.mapPassEachRole();
		//判断副本是否通关
		this.copyPassLogic();
		//标记为结束
		this.mapSign = MapSign.end;
	}
	
	/**
	 * 副本通关奖励
	 * 地图通关时，判断副本是否通关
	 */
	private void copyPassLogic(){
		try {
			MapMultiCopyContainer container = (MapMultiCopyContainer)this.getMapContainer();
			container.copyPassReward();
		} catch (Exception e) {
			logger.error("MapMultiCopyInstance.copyPassLogic error: ", e);
		}
	}
	
	/**
	 * 地图通关之后，每个人的处理
	 * ①统计锁妖塔通关时间
	 * ②地图通关完成任务
	 */
	private void mapPassEachRole(){
		try {
			String mapId = this.map.getMapId();
			for(RoleInstance role : this.getRoleList()){
				if(null == role){
					continue;
				}
				try {
					//完成任务
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
	 * 刷地图跳转点
	 * 地图未通关不会刷
	 */
	private void refreshJumpPiont(){
		//已经刷过跳转点，就不再刷了
		if(this.jumpPointRefreshed){
			return;
		}
		short jumpX = this.mapCopyConfig.getJumpX();
		short jumpY = this.mapCopyConfig.getJumpY();
		//未配置跳转点的位置，则不刷
		if(jumpX <= 0 || jumpY <=0){
			return;
		}
		String toMapId = null;
		short toMapX = 0;
		short toMapY = 0;
		//如果是最后一张地图，刷副本进入点或固定坐标
		if(this.mapCopyConfig.isLastMap()){
			//跳转到副本进入点（只使用于单人副本）
			Point point = this.getPersonalLastMapBeforEnterPoint();
			if(null != point){
				toMapId = point.getMapid();
				toMapX = (short) point.getX();
				toMapY = (short) point.getY();
			}else{
				//跳转到配置的固定点
				toMapId = this.mapCopyConfig.getToMapId();
				toMapX = this.mapCopyConfig.getToMapX();
				toMapY = this.mapCopyConfig.getToMapY();
			}
		}else{
			toMapId = this.mapCopyConfig.getToMapId();
			toMapX = this.mapCopyConfig.getToMapX();
			toMapY = this.mapCopyConfig.getToMapY();
		}
		//没有找到可刷出的目标点
		if(Util.isEmpty(toMapId) || jumpX <= 0 || jumpY <= 0){
			return;
		}
		String jumpMapId = this.map.getMapId();
		JumpMapPoint jumpPoint = new JumpMapPoint();
		jumpPoint.setMapid(jumpMapId);
		jumpPoint.setX(jumpX);
		jumpPoint.setY(jumpY);
		jumpPoint.setTomapid(toMapId);
		jumpPoint.setDesX(toMapX);
		jumpPoint.setDesY(toMapY);
		this.refreshJumpPointList.add(jumpPoint);
		Map toMap = GameContext.getMapApp().getMap(jumpPoint.getTomapid());
		//通知跳转点message
		C0235_MapJumpPointNotifyMessage tjpnm = new C0235_MapJumpPointNotifyMessage();
		tjpnm.setJumpMapId(jumpMapId);
		tjpnm.setJumpX(jumpX);
		tjpnm.setJumpY(jumpY);
		tjpnm.setToMapName(toMap.getMapConfig().getMapdisplayname());
		this.broadcastMap(null, tjpnm);
		//已经刷出跳转点，赋值为true
		this.jumpPointRefreshed = true;
	}
	
	/**
	 * 获取单人副本最后一张地图，角色进入副本前的坐标点
	 * @return
	 */
	private Point getPersonalLastMapBeforEnterPoint(){
		if(CopyType.personal != this.copyType){
			return null;
		}
		if(CopyPassJumpType.Enter_Point != this.passJumpType){
			return null;
		}
		if(!this.mapCopyConfig.isLastMap()){
			return null;
		}
		RoleInstance role = this.getFirstRole();
		if(null != role){
			return role.getCopyBeforePoint();
		}
		return null;
	}
	
	/**
	 * 判断倒计时是否结束逻辑
	 * （如果计时结束，切换到倒计时结束状态）
	 */
	private void timeOver(){
		//如果已经是超时状态，不需要处理
		if(MapState.time_over == this.mapState){
			return;
		}
		//判断是否超时，切换到超时状态
		MapMultiCopyContainer container = (MapMultiCopyContainer)this.mapContainer;
		boolean isTimeOver = this.isMapTimeOver() || container.isTimeOver();
		if(isTimeOver){
			this.mapState = MapState.time_over;
			this.timeOverDate = new Date();
		}
	}
	
	/**
	 * 当前地图倒计时是否结束
	 */
	private boolean isMapTimeOver(){
		//地图倒计时时间（单位：秒）
		int limitTime = this.mapCopyConfig.getLimitTime();
		//限时时间<=0表示不需要倒计时
		if(limitTime <= 0 || null == this.startTime){
			return false;
		}
		int mapTime = DateUtil.getSecondMargin(this.startTime);
		return mapTime > limitTime;
	}
	
	/**
	 * 倒计时结束逻辑
	 */
	private void do_mapState_timeOver(){
		//已经通关，不需要将角色传出副本
		if(this.passed){
			//容错
			if(null == this.timeOverDate){
				this.timeOverDate = new Date();
			}
			//判断倒计时结束的时间，超过了可停留时间，将地图内所有人传出
			long time = DateUtil.getMillisecondGap(this.timeOverDate);
			if(time > this.timeOverKickTime){
				this.kickAllRole();
			}
			return;
		}
		this.kickAllRole();
	}
	
	
	
	/**
	 * 将地图内所有玩家传出副本
	 */
	private void kickAllRole(){
		for(RoleInstance role : this.getRoleList()){
			if(null == role){
				continue;
			}
			//满血满蓝
			this.perfectBody(role);
			this.kickRole(role);
		}
	}
	
	/**
	 * 初始化状态的逻辑
	 * 找到刷怪规则，切换到刷怪状态
	 */
	private void do_mapState_init(){
		switch(this.ruleType){
		case Default:
			this.npcRuleId = this.mapCopyConfig.getRuleId();
			this.ruleMaxSize = GameContext.getRefreshRuleApp().getRefreshMax(Integer.parseInt(this.npcRuleId));
			break;
		case Role_Level_Auto:
			RoleInstance role = this.getFirstRole();
			if(null == role){
				break;
			}
			CopyMapRoleRule rule = GameContext.getCopyLogicApp().getCopyMapRoleRule(role, this.map.getMapId());
			//如果没有找到合适的规则，有可能是地图里种怪了
			if(null != rule){
				this.npcRuleId = rule.getRuleId();
				this.ruleMaxSize = GameContext.getRefreshRuleApp().getRefreshMax(Integer.parseInt(this.npcRuleId));
			}
			break;
		case Role_Choose:
			//TODO:角色自己选择刷怪规则，后期再做
			break;
		}
		//必须切换到刷怪状态，里面会发副本倒计时
		this.change_mapState_init_to_refresh();
	}
	
	private RoleInstance getFirstRole(){
		for(RoleInstance role : this.getRoleList()){
			if(null != role){
				return role;
			}
		}
		return null;
	}
	
	/**
	 * 切换到刷怪状态
	 * 开始计时
	 */
	private void change_mapState_init_to_refresh(){
		this.mapState = MapState.refresh;
		//开始计时
		Date now = new Date();
		this.startTime = now;
		//如果是首张地图，副本开始计时
		if(this.mapCopyConfig.isFirstMap()){
			MapMultiCopyContainer container = (MapMultiCopyContainer)this.mapContainer;
			container.setCopyStartTime(now);
			this.notifyCopyRemainTime();
		}
		//通知客户端当前地图倒计时
		this.notifyMapRemainTime();
	}
	
	/**
	 * 主推当前地图倒计时（秒）
	 */
	private void notifyMapRemainTime(){
		if(!this.mapCopyConfig.isTimeLimit()){
			return;
		}
		//剩余时间（秒）
		int time = this.mapCopyConfig.getLimitTime() - DateUtil.getSecondMargin(this.startTime);
		if(time < 0){
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
	private void notifyCopyRemainTime(){
		MapMultiCopyContainer container = (MapMultiCopyContainer)this.mapContainer;
		int time = container.getCopyRemainTime();
		if(0 == time){
			return;
		}
		C0208_CopyRemainTimeNotifyMessage message = new C0208_CopyRemainTimeNotifyMessage();
		message.setType((byte) 0);
		message.setTime(time);
		this.broadcastMap(null, message);
	}
	
	/**
	 * 刷怪状态逻辑
	 * 根据规则刷怪，无怪可刷或已刷完怪切到刷怪结束状态
	 */
	private void do_mapState_refresh(){
		if(this.ruleIndex >= this.ruleMaxSize){
			//如果没有怪可刷，则切换到刷怪结束状态 
			//刷完怪，切换到刷怪结束状态
			this.change_mapState_to_refresh_end();
			return;
		}
		int ruleId = Integer.parseInt(this.npcRuleId);
		this.ruleIndex = GameContext.getRefreshRuleApp().refresh(ruleId, this.ruleIndex, startTime, this, true);
	}
	
	/**
	 * 切换到刷怪结束状态
	 */
	private void change_mapState_to_refresh_end(){
		this.mapState = MapState.refresh_end;
	}
	
	/**
	 * 刷怪结束状态的逻辑
	 * 判断是否通关
	 */
	private void do_mapState_refreshEnd(){
		//判断是否通关
		this.isPass();
	}
	
	/**
	 * 判断是否通关
	 * 怪死亡的时候，有判断副本是否通关
	 * 因此只需要在刷怪结束后调用此方法
	 */
	private void isPass(){
		try{
			if(this.passed){
				return ;
			}
			//没有通关条件，则不发送通关消息
			if(!this.mapCopyConfig.hasPassCondition()){
				this.passed = true;
				return ;
			}
			//没有通关
			if(!this.isCopyPass()){
				return ;
			}
			this.passed = true;
			//清除所有NPC
			if(1 == this.mapCopyConfig.getPassCleanNpc()){
				this.clearAllNpc();
			}
		}catch(Exception e){
			logger.error("",e);
		}
	}
	
	private void clearAllNpc(){
		Collection<NpcInstance> list = new ArrayList<NpcInstance>();
		list.addAll(this.getNpcList());
		this.npcList.clear();
		for(NpcInstance npc : list){
			this.notifyNpcDeath(npc);
		}
		list.clear();
		list = null ;
	}
	
	private void notifyPass(){
		String passTips = this.mapCopyConfig.getPassTips();
		if(!Util.isEmpty(passTips)){
			GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Map, passTips, null, this);
		}
	}
	
	private boolean isCopyPass(){
		return GameContext.getCopyLogicApp().isCopyPass(this);
	}
	
	private void detectRoleStatus(){
		MapMultiCopyContainer container = (MapMultiCopyContainer)this.getMapContainer();
		String ownerId = container.getOwnerId();
		for(RoleInstance role : this.getRoleList()){
			if(null == role){
				continue;
			}
			this.detectRoleForCopyCount(container, role);
			/*if(CopyType.faction == this.copyType){
				Faction faction = role.getFaction();
				if(faction == null 
						|| !faction.getFactionId().equals(ownerId)
						|| faction.getCopyProgress(this.copyId) == null){
					this.kickRole(role);
					continue ;
				}
			}*/
			if(CopyType.team == this.copyType){
				Team team = role.getTeam();
				if(team == null || !ownerId.equals(team.getTeamId())){
					this.kickRole(role);
				}
			}
		}
	}
	
	/**
	 * 如果是有次数限制的副本，判断角色是否扣除了副本次数。
	 * 如果是没有扣除副本次数，则踢出地图。
	 * @param container
	 * @param role
	 */
	private void detectRoleForCopyCount(MapMultiCopyContainer container, RoleInstance role){
		try {
			if(null == role || null == container){
				return;
			}
			int count = container.getCopyConfig().getCount();
			if(count <= 0){
				return;
			}
			//踢出没有扣次数二进入副本的角色
			if(!container.getDeductNumRoleIdSet().contains(role.getRoleId())){
				this.kickRole(role);
			}
		} catch (RuntimeException e) {
			logger.error(this.getClass().getName() + ".detectRoleForCopyCount error: ", e);
		}
	}
	
	private void updateOwnerTime(){
		MapMultiCopyContainer container = (MapMultiCopyContainer)this.getMapContainer();
		container.setOwnerUpdateTime(System.currentTimeMillis());
	}
	
	
	@Override
	public List<NpcBorn> getNpcBornList(){
		if(null == this.npcBornList){
			this.npcBornList = new ArrayList<NpcBorn>();
			MapNpcBornData bornData = this.map.getNpcBornData();
			if(null != bornData){
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
	public void broadcastScreenMap(AbstractRole role, Message message,int expireTime){
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
		synchronized(this){
			this.removeAbstractRole(role);
		}
		RoleInstance roleInstance = (RoleInstance) role;
		Point targetPoint = roleInstance.getCopyBeforePoint();
		role.setMapId(targetPoint.getMapid());
		role.setMapX(targetPoint.getX());
		role.setMapY(targetPoint.getY());
		//处理掉线的情况
		//记录副本容器ID和所在地图实例ID
		roleInstance.setCopyLostReLoginInfo(this.getMapContainer().getInstanceId() + Cat.comma + this.instanceId);
	}
	
	@Override
	public void npcDeath(NpcInstance npc) {
		try{
			super.npcDeath(npc);
		}catch(Exception ex){
			logger.error("",ex);
		}
		//判断是否有通关条件
		if(null == this.mapCopyConfig){
			return ;
		}
		String deathNpcId = this.mapCopyConfig.getNeedKillNpcId();
		if(null == deathNpcId || 0 == deathNpcId.trim().length()){
			return ;
		}
		if(npc.getNpc().getNpcid().equals(deathNpcId)){
			this.passed = true ;
		}
	}
	
	@Override
	protected void deathDiversity(AbstractRole attacker, AbstractRole victim) {
//		this.notifyNpcAi(victim);
	}

	@Override
	public void useGoods(int goodsId) {
		
	}

	@Override
	protected void deathLog(AbstractRole victim) {
		
	}
	
	@Override
	public void footOnPoint(AbstractRole role) throws ServiceException {
		if(!this.passed){
			return;
		}
		super.footOnPoint(role);
	}
	
}
