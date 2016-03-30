package sacred.alliance.magic.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.app.map.MapApp;
import sacred.alliance.magic.app.map.data.MapConfig;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Util;

import com.game.draco.GameContext;
import com.game.draco.app.copy.config.CopyConfig;
import com.game.draco.app.copy.config.CopyMapConfig;
import com.game.draco.app.copy.vo.CopyType;
import com.game.draco.app.team.PlayerTeam;
import com.game.draco.app.team.Team;
import com.game.draco.message.push.C0003_TipNotifyMessage;

public class MapMultiCopyContainer extends MapCopyContainer{

	private static final Logger logger = LoggerFactory.getLogger(MapCopyContainer.class);
	private static final int Destroy_Time = 2000;//在销毁状态时，超过此时间须销毁地图容器（毫秒 2秒）
		
	private static MapApp mapApp = GameContext.getMapApp();
	//是否点击出口退出
	private boolean applyExist = false ;
	//容器拥有者标识ID
	private String ownerId ;
	private CopyType copyType;
	private short copyId;
	private CopyConfig copyConfig;
	private Team team = null;
	private Set<String> enterRoleSet = new HashSet<String>();//曾经进入过副本的玩家
	/* 
	 * 扣除副本次数的角色ID
	 * 主循环中判断：如果是有次数限制的副本，若是已扣次数列表中没有角色的ID，则将其踢出副本。
	 * */
	private Set<String> deductNumRoleIdSet = new HashSet<String>();
	
	//容器拥有存活时间
	//用于判断超过一定时间范围表示此容器可以回收
	//地图实例的主循环定时更新此时间，表示已占用
	private long ownerUpdateTime;
	private byte[] ownerlock = new byte[0];//拥有者锁
	private byte[] createMapLock = new byte[0];//创建地图实例锁
	
	private Date copyStartTime;//副本开始计时时间（倒计时结束自动传出）
	private ContainerState containerState = null;;
	
	private static final int State_Own_Time = 10 * 1000;//占有状态保护时间（毫秒 10秒）
	private static final int State_Create_Time = 30 * 1000;//新建状态保护时间（毫秒 30秒）
	private Date copyCreateTime;//副本创建时间，创建副本时赋值。
	
	private Date copyOwnTime;//副本切为占有状态的时间
	private byte[] stateOwenLock = new byte[0];//占有状态锁
	
	public void flagApplyExist(){
		//只有个人副本才设置此标识，加速副本的销毁
		if(null != copyType && copyType == CopyType.personal ){
			this.applyExist = true ;
		}
	}
	
	/**
	 * 是否曾经进入过副本
	 * @param roleId
	 * @return
	 */
	public boolean haveEnterCopy(String roleId){
		return this.enterRoleSet.contains(roleId);
	}
	
	/**
	 * 将角色添加到曾经进入列表中
	 * @param roleId
	 */
	public void addRoleToEnterSet(String roleId){
		this.enterRoleSet.add(roleId);
	}
	
	public enum ContainerState{
		create,//新创建状态
		normal,//正常状态
		own,//占有状态
		destroy,//销毁状态
		;
	}
	
	@Override
	public void update(){
		synchronized(this.stateOwenLock){
			switch(this.containerState){
			case create:
				this.do_containerState_create();
				break;
			case normal:
				this.do_containerState_normal();
				break;
			case own:
				this.do_containerState_own();
				break;
			case destroy:
				//销毁状态不需要处理
				break;
			}
		}
	}
	
	/**
	 * 新建状态的逻辑
	 */
	private void do_containerState_create(){
		long createTime = DateUtil.getMillisecondGap(this.copyCreateTime);
		if(createTime <= State_Create_Time){
			return;
		}
		//超过创建保护时间，切到正常状态
		this.containerState = ContainerState.normal;
	}
	
	private boolean timeOverToDestroy(){
		long time = Destroy_Time ;
		if(!this.applyExist){
			time += GameContext.getParasConfig().getCopyLostReLogin() ;
		}
		return this.getRecoverTimeInterval() > time ;
	}
	
	/**
	 * 正常状态的逻辑
	 */
	private void do_containerState_normal(){
		synchronized(ownerlock){
			//副本中没有人，清除队伍上的副本容器ID
			int roleCount = this.getRoleCount() ;
			if(roleCount <= 0 && null != this.team){
				this.team.removeCopyContainer(this.copyId,this.instanceId);
			}
			if(roleCount <= 0 && this.timeOverToDestroy()){
				this.containerState = ContainerState.destroy;
			}
		}
	}
	
	/**
	 * 副本通关奖励（副本地图通关时调用）
	 * 发通关提示、副本奖励
	 */
	public void copyPassReward(){
		//判断副本是否通关
		if(!this.isCopyPass()){
			return;
		}
		// 副本通关的提示信息
		String msgContext = this.copyConfig.getPassTips();
		for (RoleInstance role : this.getRoleList()) {
			if (null == role) {
				continue;
			}
			if (!Util.isEmpty(msgContext)) {
				this.sendTipNotifyMessage(role, msgContext);
			}
			// 通关逻辑（通关奖励及首次额外奖励）
			GameContext.getCopyLogicApp().copyPass(role, this.copyConfig);
		}
		//好友亲密度奖励
		this.rewardFriendIntimate();
		//目标系统
		//this.updateRoleTarget();
	}
	

	/**
	 * 副本通关 奖励好友亲密度
	 */
	private void rewardFriendIntimate(){
		try {
			int intimate = this.copyConfig.getIntimate();
			if(intimate <= 0){
				return;
			}
			if(CopyType.team != this.copyType){
				return;
			}
			if(null == this.team){
				return;
			}
			this.team.addFriendIntimate(intimate);
		} catch (RuntimeException e) {
			logger.error("MapMultiCopyContainer.rewardFriendIntimate error: ", e);
		}
	}
	
	/**
	 * 占有状态的逻辑
	 */
	private void do_containerState_own(){
		long ownTime = DateUtil.getMillisecondGap(this.copyOwnTime);
		if(ownTime <= State_Own_Time){
			return;
		}
		//超过占有保护时间，切到正常状态
		this.containerState = ContainerState.normal;
	}
	
	/**
	 * 副本容器状态切为占有状态
	 */
	public boolean change_containerState_to_own(){
		synchronized(this.stateOwenLock){
			//不能从销毁状态切为其他状态
			if(ContainerState.destroy == this.containerState){
				return false;
			}
			//可以从新建状态切为占有状态，但不能修改状态
			if(ContainerState.create == this.containerState){
				return true;
			}
			//正常状态切为占有状态，修改占有时间
			if(ContainerState.normal == this.containerState){
				this.containerState = ContainerState.own;
			}
			//占有状态下切为占有状态，只需要修改占有时间
			this.copyOwnTime = new Date();
			return true;
		}
	}
	
	private void sendTipNotifyMessage(RoleInstance role, String msgContext){
		C0003_TipNotifyMessage tipMsg = new C0003_TipNotifyMessage();
		tipMsg.setMsgContext(msgContext);
		role.getBehavior().sendMessage(tipMsg);
	}
	
	/** 容器拥有者ID **/
	public String getOwnerId(){
		return ownerId;
	}
	
	/** 设置容器拥有者ID **/
	private void setOwner(String ownerId){
		synchronized(ownerlock){
			this.ownerId = ownerId;
			this.ownerUpdateTime = System.currentTimeMillis();
		}
	}
	
	/** 
	 * 获取ownerUpdateTime
	 * @return
	 */
	public long getOwnerUpdateTime(){
		return ownerUpdateTime;
	}
	
	/** 返回容器中所有的人物 */
	public List<RoleInstance> getRoleList(){
		List<RoleInstance> list = new ArrayList<RoleInstance>();
		for(MapInstance instance : this.subMapList.values()){
			list.addAll(instance.getRoleList());
		}
		return list;
	}
	
	/** 副本中的人数 **/
	public int getRoleCount(){
		int count = 0;
		for(MapInstance instance : this.subMapList.values()){
			count += instance.getRoleCount();
		}
		return count;
	}
	
	/** 根据角色id返回地图中的角色实例 **/
	public boolean hasExistRole(int roleId){
		for(RoleInstance role : this.getRoleList()){
			if(role.getIntRoleId() == roleId){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 设置拥有时间
	 */
	public void setOwnerUpdateTime(long updateTime){
		synchronized(ownerlock){
			if(ContainerState.destroy == this.containerState){
				return ;
			}
			this.ownerUpdateTime = updateTime;
		}
	}
	
	private long getRecoverTimeInterval(){
		return System.currentTimeMillis() - ownerUpdateTime;
	}
	
	public MapMultiCopyContainer(){
		super();
	}
	
	public MapMultiCopyContainer(String instanceId){
		super(instanceId);
	}
	
	/**
	 * 创建副本时，初始化信息
	 * @param role
	 * @param config
	 */
	public void initByCreate(RoleInstance role, CopyConfig config) {
		this.copyId = config.getCopyId();
		this.copyConfig = config;
		this.copyType = config.getCopyType();

		if (CopyType.personal == this.copyType || CopyType.hero == this.copyType) {
			this.setOwner(role.getRoleId());
			role.setCopyContainerId(this.getInstanceId());
		} else if (CopyType.team == this.copyType) {
			this.team = role.getTeam();
			this.setOwner(this.team.getTeamId());
			this.team.addCopyContainer(this.copyId, this.getInstanceId());
		}
		// 切换到新建状态，并设置创建时间
		this.containerState = ContainerState.create;
		this.copyCreateTime = new Date();
		// 将地图容器放到Map模块中
		mapApp.addCopyContainer(this);
	}
	
	@Override
	public boolean canDestroy() {
		return ContainerState.destroy == this.containerState;
	}
	
	@Override
	public void destroy() {
		for(MapInstance mapInstance : this.getSubMapList().values()){
			mapInstance.destroy();
		}
		mapApp.removeCopyContainer(this.instanceId);
	}
	
	public static MapContainer getMapContainer(AbstractRole abstractRole, String mapId) {
		try {
			RoleInstance role = (RoleInstance) abstractRole;
			sacred.alliance.magic.app.map.Map map = GameContext.getMapApp().getMap(mapId);
			MapConfig mapConfig = map.getMapConfig();
			short copyId = mapConfig.getCopyId();
			CopyConfig config = GameContext.getCopyLogicApp().getCopyConfig(copyId);
			MapMultiCopyContainer container = null;
			String containerId = "";
			CopyType cpType = config.getCopyType();
			if (CopyType.personal == cpType || CopyType.hero == cpType) {
				containerId = role.getCopyContainerId();
				container = (MapMultiCopyContainer) mapApp.getCopyContainer(containerId);
				// 如果容器不存在或者副本ID不相同，则创建一个副本
				if (null == container || container.getCopyId() != copyId) {
					container = new MapMultiCopyContainer();
					container.initByCreate(role, config);
				}
			} else if (CopyType.team == cpType) {
				Team team = role.getTeam();
				if (null == team) {
					team = new PlayerTeam(role);
				}
				containerId = team.getCopyContainerId(copyId);
				container = (MapMultiCopyContainer) mapApp.getCopyContainer(containerId);
				if (null == container) {
					synchronized(team){
						container = (MapMultiCopyContainer) mapApp.getCopyContainer(containerId);
						if(null == container){
							container = new MapMultiCopyContainer();
							container.initByCreate(role, config);
						}
					}
				}
			}

			/*container.setCopyType(cpType);
			container.setCopyId(copyId);
			container.setCopyConfig(config);
			// 切换到新建状态，并设置创建时间
			container.setContainerState(ContainerState.create);
			container.setCopyCreateTime(new Date());
			mapApp.addCopyContainer(container);*/
			return container;
		} catch (Exception e) {
			logger.error("MapContainer.getMapContainer error: ", e);
			return null;
		}
	}
	
	@Override
	public MapInstance createMapInstance(Map map, AbstractRole role) {
		MapInstance instance = subMapList.get(map.getMapId());
		if(instance != null){
			return instance;
		}
		//避免并发创建多个地图实例
		synchronized(createMapLock){
			instance = subMapList.get(map.getMapId());
			if(instance != null){
				return instance;
			}
			MapMultiCopyInstance copyInstance = new MapMultiCopyInstance(map);
			mapApp.addMapInstance(copyInstance); // 加入主循环
			addMapInstance(copyInstance);// 加到容器中
			copyInstance.setMapContainer(this);
			copyInstance.initNpc(true);
			copyInstance.init();
			
			return copyInstance;
		}
	}
	
	// 判断此副本是否已通关
	private boolean isCopyPass() {
		// 公会副本忽略
		for (MapInstance instance : this.subMapList.values()) {
			if (!GameContext.getCopyLogicApp().isCopyPass(instance)) {
				return false;
			}
			// 并且是最后一张地图(有可能副本地图还未创建全)
			CopyMapConfig config = GameContext.getCopyLogicApp().getMapConfig(instance.getMap().getMapId());
			if (config == null) {
				continue;
			}
			if (config.isLastMap()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 副本倒计时是否结束
	 * @return
	 */
	public boolean isTimeOver(){
		//副本倒计时时间（单位：秒）
		int totalTime = this.copyConfig.getTotalTime();
		//总时间<=0表示不需要倒计时
		if(totalTime <= 0 || null == this.copyStartTime){
			return false;
		}
		int time = DateUtil.getSecondMargin(this.copyStartTime);
		return time > totalTime;
	}
	
	/**
	 * 获取副本倒计时（秒）
	 * @return
	 */
	public int getCopyRemainTime(){
		int totalTime = this.copyConfig.getTotalTime();
		if(totalTime <= 0 ){
			return 0;
		}
		if(null == this.copyStartTime){
			return totalTime;
		}
		//副本倒计时（秒）
		int time = totalTime - DateUtil.getSecondMargin(this.copyStartTime);
		if(time < 0){
			time = 0;
		}
		return time;
	}
	
	/**
	 * 扣除角色的副本次数
	 * @param role
	 */
	public void deductRoleCopyCount(RoleInstance role){
		if(null == role){
			return;
		}
		this.deductNumRoleIdSet.add(role.getRoleId());
	}
	
	public Set<String> getDeductNumRoleIdSet() {
		return deductNumRoleIdSet;
	}

	public java.util.Map<String, MapInstance> getSubMapList() {
		return subMapList;
	}
	public void setSubMapList(java.util.Map<String, MapInstance> subMapList) {
		this.subMapList = subMapList;
	}
	
	public CopyType getCopyType() {
		return copyType;
	}

	public void setCopyType(CopyType copyType) {
		this.copyType = copyType;
	}

	public short getCopyId() {
		return copyId;
	}
	public void setCopyId(short copyId) {
		this.copyId = copyId;
	}

	public Date getCopyStartTime() {
		return copyStartTime;
	}

	public void setCopyStartTime(Date copyStartTime) {
		this.copyStartTime = copyStartTime;
	}

	public CopyConfig getCopyConfig() {
		return copyConfig;
	}

	public void setCopyConfig(CopyConfig copyConfig) {
		this.copyConfig = copyConfig;
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	public ContainerState getContainerState() {
		return containerState;
	}

	public void setContainerState(ContainerState containerState) {
		this.containerState = containerState;
	}

	public Date getCopyCreateTime() {
		return copyCreateTime;
	}

	public void setCopyCreateTime(Date copyCreateTime) {
		this.copyCreateTime = copyCreateTime;
	}

}
