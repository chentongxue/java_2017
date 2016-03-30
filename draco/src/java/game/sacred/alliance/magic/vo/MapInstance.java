package sacred.alliance.magic.vo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.ai.MessageType;
import sacred.alliance.magic.app.ai.Telegram;
import sacred.alliance.magic.app.ai.event.MessageDispatcher;
import sacred.alliance.magic.app.ai.event.MessageDispatcherFactory;
import sacred.alliance.magic.app.fall.BoxEntry;
import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.app.map.MapProperty;
import sacred.alliance.magic.app.map.data.MapConfig;
import sacred.alliance.magic.app.map.data.MapNpcBornData;
import sacred.alliance.magic.app.map.data.NpcBorn;
import sacred.alliance.magic.app.map.point.CollectPointConfig;
import sacred.alliance.magic.app.map.point.CollectablePoint;
import sacred.alliance.magic.app.map.point.EventPoint;
import sacred.alliance.magic.app.map.point.JumpMapPoint;
import sacred.alliance.magic.app.map.point.PointNode;
import sacred.alliance.magic.app.map.point.QuestCollectPoint;
import sacred.alliance.magic.base.Direction;
import sacred.alliance.magic.base.ForceRelation;
import sacred.alliance.magic.base.PathType;
import sacred.alliance.magic.base.PointType;
import sacred.alliance.magic.base.RebornType;
import sacred.alliance.magic.base.RolePkStatus;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.base.StateType;
import sacred.alliance.magic.constant.LoopConstant;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.domain.CollectPoint;
import sacred.alliance.magic.domain.GoodsHero;
import sacred.alliance.magic.module.cache.Cache;
import sacred.alliance.magic.scheduler.job.LoopCount;
import sacred.alliance.magic.scheduler.job.WorldTime;
import sacred.alliance.magic.util.*;

import com.game.draco.GameContext;
import com.game.draco.app.buff.Buff;
import com.game.draco.app.buff.BuffContext;
import com.game.draco.app.buff.stat.BuffStat;
import com.game.draco.app.drama.config.Drama;
import com.game.draco.app.drama.config.DramaTriggerType;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.app.horse.domain.RoleHorse;
import com.game.draco.app.npc.NpcInstanceFactroy;
import com.game.draco.app.npc.config.ForceConfig;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.npc.domain.NpcTemplate;
import com.game.draco.app.npc.refresh.NpcRefreshTask;
import com.game.draco.app.npc.type.NpcType;
import com.game.draco.app.pet.domain.RolePet;
import com.game.draco.app.skill.config.SkillDetail;
import com.game.draco.app.skill.domain.RoleSkillStat;
import com.game.draco.app.skill.vo.Skill;
import com.game.draco.message.item.DeathNotifySelfItem;
import com.game.draco.message.item.DramaInfoItem;
import com.game.draco.message.item.MapBaffleItem;
import com.game.draco.message.push.C0203_NpcBornNotifyMessage;
import com.game.draco.message.push.C0216_WalkTeleportNotifyMessage;
import com.game.draco.message.push.C0234_MapJumpPointRemoveNotifyMessage;
import com.game.draco.message.push.C0235_MapJumpPointNotifyMessage;
import com.game.draco.message.push.C0303_BuffMapNotifyMessage;
import com.game.draco.message.push.C0601_DeathNotifyMessage;
import com.game.draco.message.request.C2607_RoleHorseRideReqMessage;
import com.game.draco.message.response.C0204_MapUserEntryNoticeRespMessage;
import com.game.draco.message.response.C0603_FallPickupRespMessage;
import com.game.draco.message.response.C2001_RoleRebornRespMessage;


public abstract class MapInstance implements java.io.Serializable {
	
	protected final static Logger logger = LoggerFactory.getLogger(MapInstance.class);

	protected static AtomicInteger instanceIdGenerator = new AtomicInteger();

	protected String instanceId;

	protected Queue<BuffStat> buffList = new ConcurrentLinkedQueue<BuffStat>();
	
	protected java.util.Map<String,RoleInstance> roleMap = new ConcurrentHashMap<String,RoleInstance>();

	protected Queue<NpcInstance> npcList = new ConcurrentLinkedQueue<NpcInstance>();
	
	protected Queue<NpcInstance> roleCopyList = new ConcurrentLinkedQueue<NpcInstance>();
	
	//障碍物列表
	protected Queue<NpcInstance> baffleList = new ConcurrentLinkedQueue<NpcInstance>();

	/** 死亡Npc列表 */
	private Queue<NpcInstance> deathNpcs = new ConcurrentLinkedQueue<NpcInstance>();

	protected Map map;

	protected static GameContext context = GameContext.getGameContext();

	private WorldTime worldTime = new WorldTime();

	/** 当前地图是否正在执行updating操作 */
	private AtomicBoolean updating = new AtomicBoolean(false);

	/**
	 * 角色与其box的匹配关系 key: roleId value: boxId
	 */
	private java.util.Map<String, Set<String>> roleBoxMapping = new ConcurrentHashMap<String, Set<String>>();
	/**
	 * 采集点列表,key: 采集点实例ID
	 */
	private java.util.Map<String, CollectablePoint<RoleInstance>> collectPointMap = new ConcurrentHashMap<String, CollectablePoint<RoleInstance>>();

	/**
	 * 采集点匹配 key: 采集点模板ID, Set<String> 实例ID
	 */
	private java.util.Map<String, Set<String>> collectPointMapping = new ConcurrentHashMap<String, Set<String>>();

	/**
	 * 需要刷新的采集点列表
	 */
	private List<DisappearCollectPoint> disappearCollectPointList = new ArrayList<DisappearCollectPoint>();

	private MessageDispatcher messageDispatcher;

	private LoopCount defaultLoopCount = new LoopCount(LoopConstant.MAP_INSTANCE_DEFAULT_CYCLE);
	private LoopCount rebornNpcLoopCount = new LoopCount(LoopConstant.REBORN_NPC_CYCLE);
	private LoopCount npcRefreshLoopCount = new LoopCount(LoopConstant.NPC_REFRESH_CYCLE);
	

	protected Date lastAccessTime = new Date();

	protected MapContainer mapContainer;
	
	private AtomicBoolean inQueue = new AtomicBoolean(false);
	
	//地图刷怪规则<周ID，刷怪配置>
	protected List<NpcRefreshTask> npcRefreshTaskList = new ArrayList<NpcRefreshTask>();
	
	protected int lineId = -1;
	
	protected int bornIndex = -1;
	
	protected float hurtRatio = 1;//伤害比例系数
	
	protected List<JumpMapPoint> refreshJumpPointList = new ArrayList<JumpMapPoint>();//刷新的跳转点列表
	
	/**玩家死亡复活处理*/
	public void roleReborn(RoleInstance role){};
	
	public int getRoleCount(){
		if(null == this.roleMap){
			return 0 ;
		}
		return this.roleMap.size();
	}
	
	public AtomicBoolean getInQueue() {
		return inQueue;
	}

	public Date getLastAccessTime() {
		return lastAccessTime;
	}

	public void setLastAccessTime(Date lastAccessTime) {
		this.lastAccessTime = lastAccessTime;
	}

	public MapInstance() {
	}

	protected abstract String createInstanceId();
	
	public MapInstance(Map map) {
		this(map, -1);
	}
	
	/**
	 * 玩家满
	 * @return
	 */
	public boolean isMapRoleFull(){
		MapConfig mc = this.getMap().getMapConfig();
		int role_num = mc.getMaxRoleCount();
		return  role_num != -1 && role_num <= this.getRoleCount();
	}
	
	public MapInstance(Map map, int lineId){
		this.map = map;
		this.lineId = lineId;

		instanceId = createInstanceId();
		// 消息分发器
		messageDispatcher = MessageDispatcherFactory.createDispatcher();

		// 初始化任务采集点
		this.initCollectPoint(map.getQuestCollectPointConfig());
		// 初始化技能采集点
		//this.initCollectPoint(map.getSkillCollectPointConfig());
		
		//npc刷新配置
		GameContext.getNpcRefreshApp().installMapNpcRefreshConfig(this);
	}

	public MapContainer getMapContainer() {
		return mapContainer;
	}

	public void setMapContainer(MapContainer mapContainer) {
		this.mapContainer = mapContainer;
	}

	public MessageDispatcher getMessageDispatcher() {
		return this.messageDispatcher;
	}

	private CollectablePoint<RoleInstance> newCollectablePoint(Point point, CollectPoint cp) {
		// 处理点
		CollectablePoint<RoleInstance> pointInstance = null;
		if (cp.getType() == PointType.QuestCollectPoint.getType()) {
			pointInstance = new QuestCollectPoint(point.getX(), point.getY(), cp);
		} /*else if (cp.getType() == PointType.GeneralSkillCollectPoint.getType()) {
			pointInstance = new SkillCollectPoint(point.getX(), point.getY(),
					cp);
		} else if (cp.getType() == PointType.SpecialSkillCollectPoint.getType()) {
			pointInstance = new SkillCollectPoint(point.getX(), point.getY(),
					cp);
		} */else {
			return null;
		}

		this.collectPointMap.put(pointInstance.getInstanceId(), pointInstance);
		// 放入匹配
		String pointTemplateId = cp.getId();
		if (!collectPointMapping.containsKey(pointTemplateId)) {
			collectPointMapping.put(pointTemplateId, new HashSet<String>());
		}
		collectPointMapping.get(pointTemplateId).add(pointInstance.getInstanceId());
		
		return pointInstance;
	}

	/**
	 * 初始化任务采集点
	 */
	private void initCollectPoint(CollectPointConfig pointConfig) {
		if (null == pointConfig || Util.isEmpty(pointConfig.getNodes())) {
			return;
		}
		for (PointNode pn : pointConfig.getNodes()) {
			if (null == pn || Util.isEmpty(pn.getPoint())) {
				continue;
			}
			String pointTemplateId = pn.getId();
			CollectPoint cp = GameContext.getCollectPointLoader().getDataMap().get(pointTemplateId);
			if (null == cp) {
				logger.error("CollectPoint not exists,id=" + pointTemplateId
						+ " mapId=" + map.getMapId());
				continue;
			}
			List<Point> pointList = pn.getPoint();
			if (cp.isUnique()) {
				// 要求唯一
				int size = ProbabilityMachine.randomInt(pointList.size());
				Point point = pointList.get(size);
				this.newCollectablePoint(point, cp);
				continue;
			}
			for (Point point : pointList) {
				this.newCollectablePoint(point, cp);
			}
		}
	}

	public void removeCollectPoint(String instanceId) {
		// 从地图中删除此采集点,放入刷新列表
		CollectablePoint<RoleInstance> cp = collectPointMap.get(instanceId);
		if (null == cp) {
			return;
		}
		collectPointMap.remove(instanceId);
		Set<String> instanceIdSet = this.collectPointMapping.get(cp
				.getCollectPoint().getId());
		if (null != instanceIdSet) {
			instanceIdSet.remove(instanceId);
		}
		// 放入采集点刷新列表
		DisappearCollectPoint info = new DisappearCollectPoint();
		info.setDate(System.currentTimeMillis());
		info.setTemplateId(cp.getCollectPoint().getId());
		info.setX(cp.getX());
		info.setY(cp.getY());
		disappearCollectPointList.add(info);

	}

	public void clearBox(BoxEntry entry) {
		if (null == entry) {
			return;
		}
		if(null == roleBoxMapping || 0 == roleBoxMapping.size()){
			return ;
		}
		RoleInstance role = entry.getOwner();
		if(null == role){
			return ;
		}
		String roleId = role.getRoleId();
		Set<String> boxIdSet = roleBoxMapping.get(roleId);
		if (null != boxIdSet) {
			boxIdSet.remove(entry.getBoxId());
			if (0 == boxIdSet.size()) {
				roleBoxMapping.remove(roleId);
			}
		}
		MapInstance nowMap = role.getMapInstance();
		if(null == nowMap || !nowMap.getInstanceId().equals(this.getInstanceId())){
			//没有在当前地图无需给其发送消息
			return ;
		}
		C0603_FallPickupRespMessage respMsg = new C0603_FallPickupRespMessage();
		respMsg.setInstanceId(entry.getBoxId());
		respMsg.setStatus(RespTypeStatus.SUCCESS);
		//respMsg.setRemaining(new ArrayList<FallItem>());
		role.getBehavior().sendMessage(respMsg);
	}

	public void putBox(RoleInstance role, String boxId, BoxEntry boxEntry) {
		if (null == boxEntry) {
			return;
		}
		Cache<String, BoxEntry> boxes = GameContext.getMapApp()
				.getBoxesCache();
		//TODO:
		/*if (boxEntry.isCopy()) {
			boxes.put(boxId, boxEntry, 300000, TimeUnit.MILLISECONDS);
		} else {
			boxes.put(boxId, boxEntry);
		}*/
		boxes.put(boxId, boxEntry);
		if (!this.roleBoxMapping.containsKey(role.getRoleId())) {
			roleBoxMapping.put(role.getRoleId(), new HashSet<String>());
		}
		roleBoxMapping.get(role.getRoleId()).add(boxId);
	}

	public void broadcastMap(AbstractRole role, Message message) {
		this.broadcastMap(role, message, 0);
	}
	
	public void broadcastScreenMap(AbstractRole role, Message message){
		broadcastScreenMap(role,message,0);
	}
	
	public void broadcastScreenMap(AbstractRole role, Message message,int expireTime) {
		if(null == role){
			//同屏广播role不能为null
			return;
		}
		String srcUserId = "";
		if (role.getRoleType() == RoleType.PLAYER) {
			RoleInstance instance = (RoleInstance) role;
			srcUserId = instance.getUserId();
		}
		for (RoleInstance instance : getRoleList()) {
			if (null == instance || srcUserId.equals(instance.getUserId())) {
				continue;
			}
			if (!role.getBehavior().inTargetEyes(instance)) {
				continue;
			}
			if (expireTime <= 0) {
				context.getMessageCenter().send(srcUserId,
						instance.getUserId(), message);
			} else {
				context.getMessageCenter().send(srcUserId,
						instance.getUserId(), message, expireTime);
			}
		}
	}

	public void broadcastMap(AbstractRole role, Message message, int expireTime) {
		String srcUserId = "";
		if (null != role && role.getRoleType() == RoleType.PLAYER) {
			RoleInstance instance = (RoleInstance) role;
			srcUserId = instance.getUserId();
		}
		for (RoleInstance instance : getRoleList()) {
			if (null == instance || srcUserId.equals(instance.getUserId())) {
				continue;
			}
			if (expireTime <= 0) {
				context.getMessageCenter().send(srcUserId,
						instance.getUserId(), message);
			} else {
				context.getMessageCenter().send(srcUserId,
						instance.getUserId(), message, expireTime);
			}
		}
	}

	private void updateBuff() {

		try {
			long now = System.currentTimeMillis();
			// 更新地图buff
			for (Iterator<BuffStat> it = this.getBuffList().iterator(); it
					.hasNext();) {
				BuffStat buffStat = it.next();
				Buff buff = GameContext.getBuffApp().getBuff(buffStat.getBuffId());
				if (buff == null){
					continue;
				}
					
				/**
				 * TODO 1、如果间隔时间为0 更新间隔时间 直接判断 3、
				 * 
				 * 2、 如果now-开始时间<buff持续时间
				 * 
				 * t=now-上次执行时间
				 * 
				 * 如果now-开始时间>buff持续时间
				 * 
				 * t=buff开始时间+持续时间-上次时间
				 * 
				 * t/间隔时间=执行次数 上次执行时间=now-（t%间隔时间) 3、 如果(上次执行时间-开始时间+间隔时间)>持续时间
				 * buff结束
				 * 
				 */
				
				int intervalTime = buffStat.getIntervalTime();

				if (intervalTime <= 0) {
					if (buffStat.isTimeOver(now)) {
						delMapBuff(buffStat,buff);
					} else {
						buffStat.setRemainTime(buffStat.getRemainTime()
								- (int) (now - buffStat.getLastExecuteTime()));
						buffStat.setLastExecuteTime(now);
					}
					continue;
				}

				/*long caculateTime;
				if ((now - buffStat.getCreateTime().getTime()) < buffStat
						.getBuff().getPersistTime(buffStat.getBuffLevel())) {
					caculateTime = now - buffStat.getLastExecuteTime();
				} else {
					caculateTime = buffStat.getCreateTime().getTime()
							+ buffStat.getBuff().getPersistTime(
									buffStat.getBuffLevel())
							- buffStat.getLastExecuteTime();
				}*/
				long caculateTime = Math.min(now-buffStat.getLastExecuteTime(), buffStat.getRemainTime());
				int executeNum = (int) (caculateTime / (long) intervalTime);
				int yuliangTime = (int) (caculateTime % (long) intervalTime);

				boolean remove = false ;
				for (int i = 0; i < executeNum; i++) {
					BuffContext context = this.getBuffContext(buffStat.getCaster(), buffStat);
					buff.process(context);
					remove = context.isRemove();
				}
				if (executeNum > 0) {
					buffStat.setRemainTime(buffStat.getRemainTime()
							- intervalTime*executeNum);
					buffStat.setLastExecuteTime(now - yuliangTime);
				}
				if (buffStat.isTimeOver(now) || remove) {
					delMapBuff(buffStat,buff);
				}

			}
		} catch (Exception ex) {
			logger.error("", ex);
		}
	}
	
	private BuffContext getBuffContext(AbstractRole role,BuffStat buffStat){
		BuffContext context = new BuffContext();
		context.setBuffStat(buffStat);
		return context ;
	}

	public final void update() throws ServiceException {
		if (updating.compareAndSet(false, true)) {
			try {
				updateSub();
			} finally {
				updating.set(false);
				// 更新最后访问时间
				this.setLastAccessTime(new Date());
			}
		}
	}
	
	
	
	
	protected void updatePlayer(){
		// 角色行为,NPC行为需要进入后处理
		try {
			for (AbstractRole role : this.getRoleList()) {
				try {
					// try下，免得影响其他人
					//判断是否在本地图，发现有时候用户离开地图确没有从列表中移除
					MapInstance mapInstance = role.getMapInstance();
					if(null != mapInstance 
							&& !mapInstance.getInstanceId().equals(this.getInstanceId())){
						GameContext.getUserMapApp().exitMap(role, this);
						continue ;
					}
					role.getBehavior().update();
				} catch (Exception ex) {
				}
			}
		} catch (Exception ex) {
			logger.error("", ex);
		}
	}

	protected void updateSub() throws ServiceException {
		try{
			//地图刷怪逻辑
			if(npcRefreshLoopCount.isReachCycle() 
					&& !(Util.isEmpty(this.npcRefreshTaskList))){
				for(NpcRefreshTask task : this.npcRefreshTaskList){
					task.update();
				}
			}
		}catch(Exception e){
			logger.error("", e);
		}
		//用户行为
		this.updatePlayer() ;
		// 这里要考虑同步的问题
		try {
			for (AbstractRole role : this.getNpcList()) {
				try {
					role.getBehavior().update();
				} catch (Exception ex) {
					logger.error("", ex);
				}
			}
			//处理分身
			for (AbstractRole role : this.roleCopyList) {
				try {
					role.getBehavior().update();
				} catch (Exception ex) {
					logger.error("", ex);
				}
			}
		} catch (Exception ex) {
			logger.error("", ex);
		}

		if (rebornNpcLoopCount.isReachCycle()) {
			try {
				// NPC重生
				npcRebirth();
			} catch (Exception ex) {
				logger.error("", ex);
			}
			try {
				refreshDisappearCollectPoint();
			} catch (Exception ex) {
				logger.error("", ex);
			}
		}

		if (defaultLoopCount.isReachCycle()) {

			try {
				map.getMapLogic().update();
			} catch (Exception ex) {
				logger.error("", ex);
			}
			// 处理消息
			try {
				this.messageDispatcher.dispatchMessages(this.getWorldTime()
						.getCurrentWorldTime());
			} catch (Exception ex) {
				logger.error("", ex);
			}
			// 更新地图buff
			updateBuff();
		}
	}
	

	private void refreshDisappearCollectPoint() {
		for (Iterator<DisappearCollectPoint> it = this.disappearCollectPointList
				.iterator(); it.hasNext();) {
			DisappearCollectPoint current = it.next();
			// 判断是否可以刷新
			CollectPoint cp = GameContext.getCollectPointLoader()
					.getDataMap().get(current.getTemplateId());
			if (null == cp) {
				// 删除
				it.remove();
				continue;
			}
			if (current.getDate() + cp.getRefreshInteval() * 1000 > System
					.currentTimeMillis()) {
				continue;
			}

			// 删除
			it.remove();

			Point point = null;
			// 判断是否唯一
			if (cp.isUnique()) {
				// 如果唯一的话,再次随机一个地点
				List<PointNode> nodeList = null;
				if (cp.getType() == PointType.QuestCollectPoint.getType()) {
					nodeList = map.getQuestCollectPointConfig().getNodes();
				} /*else if (cp.getType() == PointType.GeneralSkillCollectPoint
						.getType()) {
					nodeList = map.getSkillCollectPointConfig().getNodes();
				} else if (cp.getType() == PointType.SpecialSkillCollectPoint
						.getType()) {
					nodeList = map.getSkillCollectPointConfig().getNodes();
				}*/
				if (null == nodeList) {
					continue;
				}
				for (PointNode node : nodeList) {
					if (node.getId().equals(current.getTemplateId())) {
						point = node.getPoint().get(
								ProbabilityMachine.randomInt(node.getPoint()
										.size()));
						break;
					}
				}
			} else {
				point = new Point(map.getMapId(), current.getX(), current
						.getY());
			}
			if (null == point) {
				continue;
			}
			CollectablePoint<RoleInstance> newPointInstance = this
					.newCollectablePoint(point, cp);

			if (null == newPointInstance) {
				continue;
			}
			// 通知地图内用户有新刷新点
			for (RoleInstance role : this.getRoleList()) {
				GameContext.getUserMapApp().notifyNewCollectPoint(
						role, newPointInstance, point);
			}
		}
	}

	private void npcRebirth() {
		if(null == deathNpcs){
			return ;
		}
		List<NpcBorn> npcBornList = this.getNpcBornList();
		if (null == npcBornList || 0 == npcBornList.size()) {
			return;
		}
		Date now = new Date();
		for(Iterator<NpcInstance> it = this.deathNpcs.iterator();it.hasNext();){
			// 判断是否符合重生条件
			NpcInstance npc = it.next();
			NpcBorn born = npcBornList.get(npc.getNpcBornDataIndex());
			if (born == null) {
				it.remove();
				continue;
			}
			/**
			 * 当设置最小复活时间小于0时，死亡不复活
			 */
			if (born.getMinrefreshsecond() < 0) {
				it.remove();
				continue;
			}

//			int startTime = Integer.parseInt(born.getStarttime());
//			int endTime = Integer.parseInt(born.getEndtime());
//			int nowTime = Integer.parseInt(DateUtil.date2FormatDate(now,"HHmmss"));
//			// 判断是否在刷新时间段
//			if (nowTime < startTime || nowTime > endTime) {
//				continue;
//			}
			// 判断是否已经到达刷新时间
			Date npcDieTime = npc.getDieTime();
			if (null == npcDieTime) {
				npc.setDieTime(now);
				continue;
			}
			long nowTimeDiff = now.getTime() - npc.getDieTime().getTime();
			if (nowTimeDiff < npc.getCurrentInterval() * 1000) {
				continue;
			}

			if (!npcRebirth(npc, born)) {
				continue;
			}
			// 添加到NPC列表
			this.addAbstractRole(npc);
			// 从死亡列表删除
			it.remove();
		}
	}

	public NpcInstance summonCreateNpc(String npcTempId, int gx, int gy, String summonRoleId) {
		NpcTemplate npcTemplate = context.getNpcApp().getNpcTemplate(npcTempId);
		if(npcTemplate == null){
			return null;
		}
		NpcBorn npcBorn = new NpcBorn();
		npcBorn.setBornmapgxbegin(gx);
		npcBorn.setBornmapgybegin(gy);
		npcBorn.setBornmapgxend(gx);
		npcBorn.setBornmapgyend(gy);
		npcBorn.setBornNpcDir(Direction.DOWN.getType());

		NpcInstance npcInstance = NpcInstanceFactroy.createNpcInstance(npcTemplate, map.getMapId(), npcBorn);
		npcInstance.setMapInstance(this);
		//调用AI刚出生方法
		npcInstance.getAi().justRespawned();
		npcInstance.setNpcBornDataIndex(-1);

		npcInstance.setSummonRoleId(summonRoleId);
		
		addAbstractRole(npcInstance);
		this.npcBornNotify(npcInstance);
		return npcInstance;
	}
	
	public NpcInstance summonUnionCreateNpc(String npcTempId, int gx, int gy, String summonRoleId) {
		NpcTemplate npcTemplate = context.getNpcApp().getNpcTemplate(npcTempId);
		if(npcTemplate == null){
			return null;
		}
		NpcBorn npcBorn = new NpcBorn();
		npcBorn.setBornmapgxbegin(gx);
		npcBorn.setBornmapgybegin(gy);
		npcBorn.setBornmapgxend(gx);
		npcBorn.setBornmapgyend(gy);
		npcBorn.setBornNpcDir(Direction.DOWN.getType());

		NpcInstance npcInstance = NpcInstanceFactroy.createNpcInstance(npcTemplate, map.getMapId(), npcBorn);
		npcInstance.setMapInstance(this);
		//调用AI刚出生方法
		npcInstance.getAi().justRespawned();
		npcInstance.setNpcBornDataIndex(-1);

		npcInstance.setSummonRoleId(summonRoleId);
		return npcInstance;
	}
	
	public NpcInstance summonCreateNpc(String npcTempId, int gx, int gy) {
		return summonCreateNpc(npcTempId, gx, gy, null);
	}
	
	public NpcInstance summonUnionCreateNpc(String npcTempId, int gx, int gy) {
		return summonUnionCreateNpc(npcTempId, gx, gy, null);
	}
	
	public NpcInstance summonCreateNpc(NpcBorn npcBorn) {
		NpcTemplate npcTemplate = context.getNpcApp().getNpcTemplate(npcBorn.getBornnpcid());
		if(npcTemplate == null){
			return null;
		}
		npcBorn.setBornNpcDir(Direction.DOWN.getType());
		NpcInstance npcInstance = NpcInstanceFactroy.createNpcInstance(npcTemplate, map.getMapId(), npcBorn);
		npcInstance.setMapInstance(this);
		//调用AI刚出生方法
		npcInstance.getAi().justRespawned();
		npcInstance.setNpcBornDataIndex(-1);
		addAbstractRole(npcInstance);
		this.npcBornNotify(npcInstance);
		return npcInstance;
	}
	
	
	public NpcInstance summonCreateNpc(NpcBorn npcBorn,String roleId) {
		NpcTemplate npcTemplate = context.getNpcApp().getNpcTemplate(npcBorn.getBornnpcid());
		if(npcTemplate == null){
			return null;
		}
		npcBorn.setBornNpcDir(Direction.DOWN.getType());
		NpcInstance npcInstance = NpcInstanceFactroy.createNpcInstance(npcTemplate, map.getMapId(), npcBorn);
		npcInstance.setMapInstance(this);
		//调用AI刚出生方法
		npcInstance.getAi().justRespawned();
		npcInstance.setNpcBornDataIndex(-1);
		npcInstance.setSummonRoleId(roleId);
		addAbstractRole(npcInstance);
		this.npcBornNotify(npcInstance);
		return npcInstance;
	}
	
	public NpcInstance summonCreateNpc(String npcTempId, int gx, int gy, List<Short> skillList, short resId) {
		NpcTemplate npcTemplate = GameContext.getNpcApp().getNpcTemplate(npcTempId);
		if(npcTemplate == null){
			return null;
		}
		NpcBorn npcBorn = new NpcBorn();
		npcBorn.setBornmapgxbegin(gx);
		npcBorn.setBornmapgybegin(gy);
		npcBorn.setBornmapgxend(gx);
		npcBorn.setBornmapgyend(gy);
		npcBorn.setBornNpcDir(Direction.DOWN.getType());

		NpcInstance npcInstance = NpcInstanceFactroy.createNpcInstance(npcTemplate, map.getMapId(), npcBorn);
		npcInstance.setMapInstance(this);
		//调用AI刚出生方法
		npcInstance.getAi().justRespawned();
		npcInstance.setNpcBornDataIndex(-1);
		
		npcInstance.getSkillMap().clear();
		RoleSkillStat normalSkill = npcInstance.getAi().getNormalSkill();
		npcInstance.getSkillMap().put(normalSkill.getSkillId(), normalSkill);
		
		for (short skillId : skillList) {
			Skill skill = GameContext.getSkillApp().getSkill(skillId);
			if (null == skill) {
			    continue;
			}
			RoleSkillStat stat = new RoleSkillStat();
			stat.setSkillId(skillId);
			stat.setSkillLevel(1);
			npcInstance.getSkillMap().put(skillId, stat);
		}
		
		int minRange = 0;
		for(short skillId : npcInstance.getSkillMap().keySet()) {
    		RoleSkillStat skillStat = npcInstance.getSkillMap().get(skillId);
    		Skill skill = GameContext.getSkillApp().getSkill(skillId);
    		if(null == skill) {
    			continue;
    		}
    		SkillDetail sd = skill.getSkillDetail(skillStat.getSkillLevel());
    		if(null == sd) {
    			continue;
    		}
    		int skillMinRange = sd.getMinUseRange();
    		if(minRange < skillMinRange) {
    			continue;
    		}
    		minRange = skillMinRange;
    	}
		npcInstance.setThinkArea(minRange);
		
		addAbstractRole(npcInstance);
		
		//设置resId
		npcInstance.setResid(resId);
		this.npcBornNotify(npcInstance);
		return npcInstance;
	}
	
	public NpcInstance summonCreateNpc(NpcBorn npcBorn, short resId) {
		NpcTemplate npcTemplate = GameContext.getNpcApp().getNpcTemplate(npcBorn.getBornnpcid());
		if(npcTemplate == null){
			return null;
		}
		npcBorn.setBornNpcDir(Direction.DOWN.getType());

		NpcInstance npcInstance = NpcInstanceFactroy.createNpcInstance(npcTemplate, map.getMapId(), npcBorn);
		npcInstance.setMapInstance(this);
		//调用AI刚出生方法
		npcInstance.getAi().justRespawned();
		npcInstance.setNpcBornDataIndex(-1);
		
		addAbstractRole(npcInstance);
		
		//设置resId
		npcInstance.setResid(resId);
		this.npcBornNotify(npcInstance);
		return npcInstance;
	}

	public void summonCreateNpcByNum(String npcTempId, int gx, int gy, int number, String summonRoleId) {
		if (number <= 0)
			return;
		for (int i = 0; i < number; i++) {
			summonCreateNpc(npcTempId, gx, gy, summonRoleId);
		}
	}
	
	public void summonCreateNpcByNum(String npcTempId, int gx, int gy, int number) {
		if (number <= 0)
			return;
		for (int i = 0; i < number; i++) {
			summonCreateNpc(npcTempId, gx, gy, null);
		}
	}
	
	public void summonCreateNpcByNum(String npcTempId, int gx, int gy, int circle, int number, String summonRoleId) {
		if (number <= 0)
			return;
		for (int i = 0; i < number; i++) {
			DefaultPoint p = PointUtil.randomPoint(gx,gy,circle);
			summonCreateNpc(npcTempId, p.getX(), p.getY(), summonRoleId);
		}
	}
	public void summonCreateNpcByNum(String npcTempId, int gx, int gy, int circle, int number) {
		summonCreateNpcByNum(npcTempId, gx, gy, circle, number, null);
	}
	
	public void summonCreateNpcByNum(String npcTempId, int x1, int y1, int x2,int y2, int number, String summonRoleId) {
		if (number <= 0)
			return;
		for (int i = 0; i < number; i++) {
			int bornX = Util.randomInRange(x1, x2-x1);
			int bornY = Util.randomInRange(y1, y2-y1);
			summonCreateNpc(npcTempId,bornX,bornY, summonRoleId);
		}
	}
	
	public void roleCopySelf(RoleInstance role, int atkRate, int atkValue, int number, int radius
			,short skillId, int skillLv, int lifeTime, short buffId) {
		if (number <= 0)
			return;
		RoleHero onBattle = GameContext.getUserHeroApp().getOnBattleRoleHero(role.getRoleId());
		if(null == onBattle) {
			return ;
		}
		GoodsHero hero = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class, 
				onBattle.getHeroId());
		if(null == hero) {
			return ;
		}
		
		//构建模版
		NpcTemplate template = GameContext.getSkillApp().createRoleCopyNpcTemplate(role, atkRate, 
				atkValue, hero.getResId(), hero.getSeriesId(), hero.getGearId(), lifeTime);
		
		for (int i = 0; i < number; i++) {
			DefaultPoint p = PointUtil.randomPoint(role.getMapX(),role.getMapY(), radius);
			NpcInstance npc = NpcInstanceFactroy.createRoleCopyNpcInstance(template, 
					this.map.getMapId(), p.getX(), p.getY(), skillId, skillLv);
			npc.setSummonRoleId(role.getRoleId());
			npc.setNpcBornDataIndex(-1);
			npc.setMapInstance(this);
			addAbstractRole(npc);
			this.npcBornNotify(npc);
			if(buffId > 0) {
				//给分身加buff
				GameContext.getUserBuffApp().addBuffStat(npc, npc, buffId, skillLv);
			}
			
		}
	}
	
	private void npcBornNotify(NpcInstance npcInstance) {
		// 广播 NPC重生消息
		for (RoleInstance ri : this.getRoleList()) {
			C0204_MapUserEntryNoticeRespMessage message = new C0204_MapUserEntryNoticeRespMessage();
			message.setItem(Converter.getNpcBodyItem(npcInstance,ri));
			context.getMessageCenter().send("", ri.getUserId(), message);
		}
	}
	
	public void roleCopyUseSkill(RoleInstance role, AbstractRole target) {
		if(Util.isEmpty(this.roleCopyList)) {
			return ;
		}
		String roleId = role.getRoleId();
		for(NpcInstance roleCopy : this.roleCopyList) {
			if(!roleCopy.getSummonRoleId().equals(roleId)) {
				continue;
			}
		
			roleCopy.getHatredTarget().addHatred(target, 1);
			roleCopy.getBehavior().switchBattleState();
		}
	}

	private boolean npcRebirth(NpcInstance npc, NpcBorn born) {
		// npc重生
		if (npc == null || born == null)
			return false;
		NpcInstanceFactroy.rebirthNpcInstance(npc, born);
		// 广播 NPC重生消息
		notifyNpcBirth(npc);
		return true;
	}
	
	public void mapBaffleDeath(NpcInstance npc){
		this.baffleList.remove(npc);
		// 通知同地图的用户
		this.notifyNpcDeath(npc);
	}
	
	public void mapBaffleDeath(String npcId, Point point, boolean isMapBaffle) {
		if (!isMapBaffle) {
			return;
		}
		if (Util.isEmpty(baffleList)) {
			return;
		}
		Iterator<NpcInstance> iter = baffleList.iterator();
		while (iter.hasNext()) {
			NpcInstance npc = (NpcInstance) iter.next();
			if (npcId.equals(npc.getNpcid()) && npc.getMapX() == point.getX()
					&& npc.getMapY() == point.getY()) {
				this.baffleList.remove(npc);
				this.notifyNpcDeath(npc);
			}
		}

	}
	
	public void notifyNpcDeath(NpcInstance npc){
		if(null == npc){
			return ;
		}
		// 通知同地图的用户
		C0601_DeathNotifyMessage message = new C0601_DeathNotifyMessage();
		message.setInstanceId(npc.getIntRoleId());
		broadcastMap(null, message);
		//触发剧情
		this.npcTriggerDrama(npc.getNpc().getNpcid(), DramaTriggerType.NpcDie);
	}

	public void npcDeath(NpcInstance npc) {
		npc.setCurHP(0);
		// 从NPC列表中删除
		this.removeAbstractRole(npc);
		this.notifyNpcDeath(npc);
		// 计算重生间隔
		int npcBornDataIndex = npc.getNpcBornDataIndex();
		if (npcBornDataIndex <= -1){
			return;
		}
		NpcBorn npcBorn = this.getNpcBornList().get(npcBornDataIndex);
		// 如果npc是通过gm工具刷新出来的，则NpcBornDataIndex=-1 & npcBorn=null。
		if (npcBorn == null){
			return;
		}
		
		//刷新周期都是-1直接把再刷出
		if(npcBorn.getMinrefreshsecond()<=0 
				&& npcBorn.getMaxrefreshsecond() <=0){
			return ;
		}
		
		Date now = new Date();
		// 设置死亡时间
		npc.setDieTime(now);
		long lifeTime = npc.getDieTime().getTime()
				- npc.getCreateTime().getTime();
		if (lifeTime <= npcBorn.getMinrefreshsecond() * 1000) {
			npc.setCurrentInterval(npcBorn.getMinrefreshsecond());
		} else if (lifeTime >= npcBorn.getMaxrefreshsecond() * 1000) {
			npc.setCurrentInterval(npcBorn.getMaxrefreshsecond());
		} else {
			npc.setCurrentInterval(Util.randomInRange(npcBorn
					.getMinrefreshsecond(), npcBorn.getMaxrefreshsecond()
					- npcBorn.getMinrefreshsecond()));
		}
		// 放入死亡Npc列表
		deathNpcs.add(npc);
	}
	
	public boolean hasPlayer() {
		return (this.getRoleCount() > 0);
	}

	public boolean isCopy() {
		return map.getMapConfig().iscopymode();
	}
	
	public boolean isSameMapInstance(MapInstance map){
		if(null == map){
			return false ;
		}
		return map.getInstanceId().equals(this.getInstanceId());
	}

	public void move(AbstractRole role, Point point, byte dir) {
		role.setMapId(point.getMapid());
		role.setMapX(point.getX());
		role.setMapY(point.getY());
		role.setDir(dir);

		/*if (RoleType.PLAYER == role.getRoleType()) {
			// 任务
			try {
				GameContext.getUserQuestApp().footOnPointForQuest(
						(RoleInstance) role, point);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}*/
	}
	
	
	public void createMapBaffle(NpcTemplate npcTemplate,NpcBorn npcBorn,
			int bornIndex,boolean whenCreateMap,int bornNum){
		for (int i = 0; i < bornNum; i++) {
			NpcInstance npcInstance = NpcInstanceFactroy.createMapBaffle(
					npcTemplate, map.getMapId(), npcBorn);
			npcInstance.setNpcBornDataIndex(bornIndex);
			if (!whenCreateMap) {
				// 不是创建地图的时候,需要广播
				notifyMapBaffleBirth(npcInstance);
			}
			this.baffleList.add(npcInstance);
		}
	}
	
	protected void notifyMapBaffleBirth(NpcInstance npc) {
		for (RoleInstance ri : this.getRoleList()) {
			C0204_MapUserEntryNoticeRespMessage message = new C0204_MapUserEntryNoticeRespMessage();
			MapBaffleItem item = new MapBaffleItem();
			item.setRoleId(npc.getIntRoleId());
			item.setMapx((short)npc.getMapX());
			item.setMapy((short)npc.getMapY());
			item.setResId(npc.getResid());
			message.setItem(item);
			context.getMessageCenter().send("", ri.getUserId(), message);
		}
	}
	
	
	public void createNpcInstance(NpcTemplate npcTemplate,NpcBorn npcBorn,
			int bornIndex,boolean whenCreateMap,int bornNum){
		for (int i = 0; i < bornNum; i++) {
			NpcInstance npcInstance = NpcInstanceFactroy.createNpcInstance(
					npcTemplate, map.getMapId(), npcBorn);
			npcInstance.setMapInstance(this);
			//调用AI刚出生方法
			npcInstance.getAi().justRespawned();
			// 很重要，如果没有setNpcBornDataIndex当怪物死亡后就不会在被刷出来了
			npcInstance.setNpcBornDataIndex(bornIndex);
			// 获得NPC行走路径
			if (!Util.isEmpty(npcBorn.getPathid()) && null != map.getWayMap()) {
				MapWay way = map.getWayMap().get(npcBorn.getPathid());
				if (null != way && !Util.isEmpty(way.getPoint())) {
					npcInstance.setWalkPath(new Path(PathType.getByType(npcBorn
							.getPathtype()), way.getPoint()));
				}
			}
			if (!whenCreateMap) {
				// 不是创建地图的时候,需要广播
				notifyNpcBirth(npcInstance);
			}
			addAbstractRole(npcInstance);

			// 原来考虑“如果最小复活时间大于0，则立刻出生；如果最小复活时间小于0，则默认已经死亡”
			// 但策划不用此功能，因此去掉此功能。
			// 如果需要刷新一直非策划配置的npc，请调用summoedCreateNpc方法。
			/*
			 * if(npcBorn.getMinrefreshsecond()>=0){
			 * addAbstractRole(npcInstance); }else{
			 * deathNpcMap.put(npcInstance.getRoleId(), npcInstance); }
			 */
		}
	}
	
	public boolean npcBorn(int bornIndex, NpcBorn npcBorn,boolean whenCreateMap) {
		int bornNum = npcBorn.getBornnpccount();
		if (bornNum <= 0) {
			return true;
		}
		String bornnpcid = npcBorn.getBornnpcid();
		NpcTemplate npcTemplate = context.getNpcApp().getNpcTemplate(
				bornnpcid);
		if (null == npcTemplate) {
			return true;
		}
		boolean isMapBaffle = (npcTemplate.getNpctype() == NpcType.baffle.getType());
		if(isMapBaffle){
			//创建地图障碍物
			this.createMapBaffle(npcTemplate, npcBorn, bornIndex, whenCreateMap,bornNum);
			return true ;
		}
		//创建NPC
		this.createNpcInstance(npcTemplate, npcBorn, bornIndex, whenCreateMap,bornNum);
		return true ;
	}
	
	public List<NpcBorn> getNpcBornList(){
		MapNpcBornData bornData = this.map.getNpcBornData();
		if(null == bornData){
			return null ;
		}
		return bornData.getNpcborn();
	}

	public void initNpc(boolean loadNpc) {
		if (!loadNpc) {
			return;
		}
		List<NpcBorn> listNpcBorn = this.getNpcBornList();
		if (null == listNpcBorn || 0 == listNpcBorn.size()) {
			return;
		}
		
		for (NpcBorn npcBorn : listNpcBorn) {
			// 获得NPC模板
			this.bornIndex++;
			this.npcBorn(this.bornIndex, npcBorn,true);
		}
	}

	/**
	 * 是否可以销毁副本 1. 地图实例是副本 2. 当前地图没有玩家 3.
	 * 
	 * @return
	 */

	public abstract boolean canDestroy();

	private void clearAllAbstractRole() {
		if (null != this.npcList) {
			this.npcList.clear();
			//this.npcList = null;
		}

		/*if (null != this.roleList) {
			this.roleList.clear();
			this.roleList = null;
		}*/
		if(null != this.roleMap){
			this.roleMap.clear();
			//this.roleMap = null ;
		}
	}

	public void destroy() {
		//地图怪物刷新逻辑
		context.getNpcRefreshApp().mapDestroyRefreshProcess(this);
		context.getMapApp().removeMapInstance(instanceId);
		// this.copyContainer = null ;
		// 手动清除地图中的用户
		clearAllAbstractRole();

		this.clearAllMapBuff();

		this.messageDispatcher = null;
		if (null != this.collectPointMap) {
			this.collectPointMap.clear();
			this.collectPointMap = null;
		}
		if (null != this.collectPointMapping) {
			this.collectPointMapping.clear();
			this.collectPointMapping = null;
		}

		if (null != this.roleBoxMapping) {
			this.roleBoxMapping.clear();
			this.roleBoxMapping = null;
		}
		if(null != deathNpcs){
			this.deathNpcs.clear();
			this.deathNpcs = null ;
		}
		if (null != this.disappearCollectPointList) {
			this.disappearCollectPointList.clear();
			this.disappearCollectPointList = null;
		}
	}

	public String getInstanceId() {
		return instanceId;
	}

	public NpcInstance getNpcInstance(String roleId) {
		if (roleId == null)
			return null;
		for (NpcInstance instance : this.getNpcList()) {
			if (roleId.equals(instance.getRoleId())) {
				return instance;
			}
		}
		return null;
	}

	/**
	 * 当所有玩家离开地图，再次有玩家进入地图时，清空所有地图buff和怪物仇恨列表
	 */
	protected void clearMapData() {
		try {
			this.clearAllMapBuff();
			for (NpcInstance instance : this.getNpcList()) {
				instance.getHatredTarget().clearHatredMap();
				instance.delAllBuffStat();
			}
		}catch(Exception ex){
			logger.error("",ex);
		}
	}

	public void addAbstractRole(AbstractRole role) {
		if(null == role){
			return ;
		}
		if (role.getRoleType() == RoleType.NPC) {
			npcList.add((NpcInstance) role);
		} else if (role.getRoleType() == RoleType.PLAYER) {
			if (this.getRoleCount() <= 0) {
				clearMapData();
			}
			this.roleMap.put(role.getRoleId(), (RoleInstance)role);
		} else if (role.getRoleType() == RoleType.COPY) {
			roleCopyList.add((NpcInstance) role);
		}
	}

	public void removeAbstractRole(AbstractRole role) {
		if(null == role){
			return ;
		}
		if (role.getRoleType() == RoleType.NPC && null != npcList) {
			npcList.remove(role);
			return ;
		}
		if (role.getRoleType() == RoleType.COPY && null != roleCopyList) {
			roleCopyList.remove(role);
			return ;
		}
		if (role.getRoleType() == RoleType.PLAYER && null != this.roleMap) {
			this.roleMap.remove(role.getRoleId());
		}
	}
	
	public RoleInstance getRoleInstance(String roleId){
		if(Util.isEmpty(roleId) || null == this.roleMap){
			return null ;
		}
		return this.roleMap.get(roleId);
	}

	public AbstractRole getAbstractRole(String roleId) {
		if(null != this.roleMap){
			AbstractRole player = this.roleMap.get(roleId);
			if(null != player){
				return player ;
			}
		}
		if(null != this.npcList){
			for (AbstractRole role : this.getNpcList()) {
				if (roleId.equals(role.getRoleId())) {
					return role;
				}
			}
		}
		return null;
	}

	/**
	 * 添加地图buff
	 * 
	 * @param buffStat
	 */
	public void addMapInstanceBuff(BuffStat buffStat) {
		/**
		 * TODO 地图buff应该没有替换和删除的。只有添加的。
		 */
		if (null == buffStat) {
			return;
		}
		Buff buff = context.getBuffApp().getBuff(buffStat.getBuffId());
		if (null == buff) {
			return;
		}
		Object info = buffStat.getContextInfo() ;
		if(null == info){
			return  ;
		}
		if(!(info instanceof Point)){
			return  ;
		}
		this.buffList.add(buffStat);
		buff.begin(this.getBuffContext(buffStat.getCaster(), buffStat));
		C0303_BuffMapNotifyMessage resp = new C0303_BuffMapNotifyMessage();
		//resp.setBuffId(buff.getBuffId());
		//resp.setBuffPersistTime(buff.getPersistTime(buffStat.getBuffLevel()));
		resp.setBuffRemainTime(buffStat.getRemainTime());
		resp.setEffectId(buff.getEffectId());
		Point p = (Point)buffStat.getContextInfo();
		resp.setX((short) p.getX());
		resp.setY((short) p.getY());
		broadcastMap(null, resp);
	}

	public Collection<NpcInstance> getBaffleList() {
		return this.baffleList;
	}

	public Collection<NpcInstance> getNpcList() {
		return npcList;
	}

	public Collection<RoleInstance> getRoleList() {
		if(null == this.roleMap){
			return null ;
		}
		return this.roleMap.values();
	}
	

	public Map getMap() {
		return map;
	}

	public void setMap(Map map) {
		this.map = map;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public WorldTime getWorldTime() {
		return worldTime;
	}

	public boolean isUpdating() {
		return updating.get();
	}

	public Collection<BuffStat> getBuffList() {
		return buffList;
	}

	private void delMapBuff(BuffStat stat,Buff buff) {
		this.buffList.remove(stat);
		buff.timeOver(this.getBuffContext(stat.getCaster(), stat));
	}

	public void clearAllMapBuff() {
		if (null != this.buffList) {
			this.buffList.clear();
		}

	}

	public boolean isFull() {
		if (null == this.getRoleList()) {
			return true;
		}
		return this.getRoleList().size() >= map.getMapLogic().maxPlayer();
	}

	public java.util.Map<String, CollectablePoint<RoleInstance>> getCollectPointMap() {
		return collectPointMap;
	}

	public java.util.Map<String, Set<String>> getCollectPointMapping() {
		return collectPointMapping;
	}

	public java.util.Map<String, Set<String>> getRoleBoxMapping() {
		return roleBoxMapping;
	}

	/**
	 * 消失采集点信息
	 * 
	 * @author Administrator
	 * 
	 */
	public class DisappearCollectPoint {

		public DisappearCollectPoint() {

		}

		private String templateId;
		private int x;
		private int y;
		private long date;// 消失时间

		public String getTemplateId() {
			return templateId;
		}

		public void setTemplateId(String templateId) {
			this.templateId = templateId;
		}

		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}

		public long getDate() {
			return date;
		}

		public void setDate(long date) {
			this.date = date;
		}

	}

	public abstract boolean canEnter(AbstractRole role);

	/*
	 * public boolean canEnter(AbstractRole role){ if(role.getRoleType() !=
	 * RoleType.PLAYER){ return false ; } if(this.isFull()){ return false ; }
	 * return !tooManyTimes((RoleInstance)role); }
	 */

	public boolean equals(MapInstance other) {
		if (null == other) {
			return false;
		}
		return this.instanceId.equals(other.instanceId);
	}

	public void notifyNpcBirth(NpcInstance npc) {
		//为了保证npc出生信息和剧情信息同时到达
		//this.npcTriggerDrama(npc.getNpcid(), DramaTriggerType.NpcBorn);
		
		String npcId = npc.getNpc().getNpcid() ;
		String mapId = this.getMap().getMapId() ;
		
		for (RoleInstance ri : this.getRoleList()) {
			//获得触发的剧情
			Drama drama = GameContext.getDramaApp().triggerNpcBornDrama(ri, mapId, npcId);
			DramaInfoItem dramaInfo = GameContext.getDramaApp().createDramaInfoItem(drama, mapId) ;
			if(null == dramaInfo){
				//没有触发
				C0204_MapUserEntryNoticeRespMessage message = new C0204_MapUserEntryNoticeRespMessage();
				message.setItem(Converter.getNpcBodyItem(npc,ri));
				context.getMessageCenter().send("", ri.getUserId(), message);
				continue ;
			}
			C0203_NpcBornNotifyMessage message = new C0203_NpcBornNotifyMessage() ;
			message.setItem(Converter.getNpcBodyItem(npc,ri));
			message.setDramaInfo(dramaInfo);
			context.getMessageCenter().send("", ri.getUserId(), message);
		}
		// 将NPC的头顶任务标识广播
		context.getUserQuestApp().notifyQuestNpcHeadSign(npc);
		
	}


	public void exitMap(AbstractRole role){
		synchronized (this){
			this.removeAbstractRole(role);
		}
		if(RoleType.PLAYER == role.getRoleType()){
			//清除副本中掉线的标记信息
			((RoleInstance)role).setCopyLostReLoginInfo(null);
		}
	}
	
	/**
	 * 角色死亡
	 * @param attacker 攻击者
	 * @param victim 受害者
	 */
	public void roleDeath(AbstractRole attacker, RoleInstance victim){
		List<DeathNotifySelfItem> rebornOptionList = this.rebornOptionFilter(victim);
		if(!this.roleCanPk()){
			// 通知角色自身死亡
			GameContext.getRoleRebornApp().notifySelfDeath(victim,
					attacker, rebornOptionList);
		}else{
			//PK地图相关操作
			GameContext.getPkApp().killPlayer(attacker, victim, rebornOptionList);
		}

		// 同步队伍信息
		if (victim.hasTeam()) {
			victim.getTeam().syschDataNotify();
		}
		//死亡广播
		C0601_DeathNotifyMessage message = new C0601_DeathNotifyMessage();
		message.setInstanceId(victim.getIntRoleId());
		this.broadcastMap(victim, message);
		this.deathDiversity(attacker, victim);
		this.deathLog(victim);
		//触发剧情
		GameContext.getDramaApp().triggerDrama(victim, DramaTriggerType.RoleDie,
				(short)0, this.getMap().getMapId(), 0, "");
	}
	
	/**
	 * 复活选项，不同类型地图可通过重写此方法进行过滤
	 * @param role
	 * @return
	 */
	protected List<DeathNotifySelfItem> rebornOptionFilter(RoleInstance role){
		return GameContext.getRoleRebornApp().getRebornOption(role);
	}
	
	/**
	 * 角色死亡在不同地图的处理差异
	 * @param attacker 攻击者
	 * @param victim 受害者
	 */
	protected abstract void deathDiversity(AbstractRole attacker, AbstractRole victim);
	/**
	 * 角色死亡时打日志
	 * @param victim
	 */
	protected abstract void deathLog(AbstractRole victim);
	
	/**
	 * 通知NPC AI
	 * @param role
	 */
	protected void notifyNpcAi(AbstractRole role){
		try{
			if (role.getMapInstance() != null) {
				role.getMapInstance().getMessageDispatcher().dispatch(
						new Telegram(role, null, MessageType.JUSTDIE, 0, role
								.getRoleId()));
			}
		}catch(Exception e){
			logger.error("MapInstance.notifyNpcAi() error:" + e);
		}
	}
	
	/**
	 * 瞬移
	 * @param role
	 * @param point
	 */
	public void teleport(AbstractRole role, Point point){
		if(null == point){
			return;
		}
		//判断跳转点是否在本地图
		if(!this.getMap().getMapId().equals(point.getMapid())){
			return;
		}
		role.setMapX(point.getX());
		role.setMapY(point.getY());
		//通知地图内所有用户
		C0216_WalkTeleportNotifyMessage notifyMessage = new C0216_WalkTeleportNotifyMessage();
		notifyMessage.setRoleId(role.getIntRoleId());
		notifyMessage.setX((short) point.getX());
		notifyMessage.setY((short) point.getY());
		notifyMessage.setEventType(point.getEventType());
		for(RoleInstance instance : this.getRoleList()){
			if(null == instance){
				continue;
			}
			instance.getBehavior().sendMessage(notifyMessage);
		}
	}
	
	protected void enter(AbstractRole role){
		if(role.roleType != RoleType.PLAYER) {
			return ;
		}
		//剧情相关
		RoleInstance player = (RoleInstance)role ;
        //剧情的触发放入MapEnterCompleteAction
		//GameContext.getDramaApp().enterMap(player, role.getMapId());
		this.autoDismount(player);
	}
	
	protected void autoDismount(RoleInstance player){
		//判断当前地图是否允许骑马
		if(GameContext.getMapApp().canMapProperty(
						player, MapProperty.canOnHorse.getType())){
			return ;
		}
		
		//判断当前是否骑马
		RoleHorse roleHorse = GameContext.getRoleHorseApp().getOnBattleRoleHorse(player.getIntRoleId());
		if(null == roleHorse){
			return ;
		}
		//下马
		C2607_RoleHorseRideReqMessage reqMsg = new C2607_RoleHorseRideReqMessage();
		reqMsg.setHorseId(roleHorse.getHorseId());
		reqMsg.setState((byte)0);
		player.getBehavior().addCumulateEvent(reqMsg);
	}

	
	public void footOnPoint(AbstractRole role) throws ServiceException{
		if(null == role || RoleType.PLAYER != role.getRoleType() ){
			return ;
		}
		// 获得角色的当前mapId
		String currentMapId = role.getMapId();
		// 角色死亡，不处理
		if (role.isDeath()) {
			return;
		}
		if (!currentMapId.equals(this.map.getMapId())) {
			return;
		}
		List<JumpMapPoint> pointList = getJumpPoint(role);
		if (null == pointList) {
			return;
		}
		for (EventPoint point : pointList) {
			String value = point.isSatisfyCond(role);
			if (null == value || 0 == value.length()) {
				point.trigger(role);
			}
		}
	}
	//获得地图的跳转点
	public List<JumpMapPoint> getJumpPoint(AbstractRole role) {
		List<JumpMapPoint> list = new ArrayList<JumpMapPoint>();
		//地编里配的固定点
		List<JumpMapPoint> jumpList = this.map.getJumpMapPointCollection().getPoint();
		if(!Util.isEmpty(jumpList)){
			list.addAll(jumpList);
		}
		//自动刷的跳转点
		list.addAll(this.refreshJumpPointList);
		return list;
	}
	
	public final ForceRelation getForceRelation(AbstractRole role,AbstractRole target){
		if(null == role || null == target){
			return ForceRelation.neutral;
		}
		if(role.getIntRoleId() == target.getIntRoleId()){
			return ForceRelation.friend ;
		}
		//处理法宝情况
		if(role.getRoleType() == RoleType.PET){
			role = ((RolePet)role).getRole();
		}
		if(target.getRoleType() == RoleType.PET){
			target = ((RolePet)target).getRole();
		}
		if(role.getRoleType() == RoleType.COPY) {
			role = ((NpcInstance)(role)).getMasterRole();
		}
		if(target.getRoleType() == RoleType.COPY){
			target = ((NpcInstance)(target)).getMasterRole();
		}
		//人对人
		if(role.getRoleType() == RoleType.PLAYER && target.getRoleType() == RoleType.PLAYER) {
			if(role.getIntRoleId() == target.getIntRoleId()) {
				return ForceRelation.friend;
			}
			return getForceRelation((RoleInstance)role, (RoleInstance)target);
		}
		//人对NPC
		if(role.getRoleType() == RoleType.PLAYER && target.getRoleType() == RoleType.NPC) {
			return getForceRelation((RoleInstance)role, (NpcInstance)target);
		}
		//NPC对人
		if(role.getRoleType() == RoleType.NPC && target.getRoleType() == RoleType.PLAYER) {
			return getForceRelation((NpcInstance)role, (RoleInstance)target);
		}
		//NPC对NPC
		if(role.getRoleType() == RoleType.NPC && target.getRoleType() == RoleType.NPC) {
			return getForceRelation((NpcInstance)role, (NpcInstance)target);
		}
		return ForceRelation.friend;
	}
	
	/**
	 * 玩家看玩家
	 * @param role
	 * @param target
	 * @return
	 */
	protected ForceRelation getForceRelation(RoleInstance role, RoleInstance target) {
		
		//如果是玩家可PK地图（玩家自己设置PK模式的地图）
		//获取玩家的势力关系，
		//如果一方开屠杀，则敌对
		//如果双方都是战斗，则敌对
		//否则走正常的势力关系
		if(role.inState(StateType.soul) || target.inState(StateType.soul)){
			return ForceRelation.neutral;
		}
		/*ForceRelation fr = null;
		if(roleCanPk()){
			fr = this.getForceRelationByPkMap(role, target);
		}
		if(null != fr){
			return fr;
		}*/
		
		//队友永远友好
		if(GameContext.getTeamApp().isInSameTeam(role, target)){
			return ForceRelation.friend;
		}
		MapConfig config = this.getMap().getMapConfig();
		if(1== config.getRoleCanPK()){
			return ForceRelation.enemy;
		}
		if(1 == config.getDiffUnionCanPK()
				&& !StringUtil.notEmptyAndSame(role.getUnionId(),target.getUnionId())){
			return ForceRelation.enemy;
		}
		return ForceRelation.friend;
	}



	/**
	 * 玩家看别人
	 * @param role
	 * @param target
	 * @return
	 */
	protected ForceRelation getForceRelation(RoleInstance role, NpcInstance target) {
		if (null == target) {
			return ForceRelation.neutral;
		}
		if (target.getRoleId().equals(role.getRoleId())) {
			return ForceRelation.friend;
		}
		ForceConfig config = GameContext.getNpcApp()
				.getForceConfig(target.getNpc().getForceId());
		if (null == config) {
			return ForceRelation.neutral;
		}
		return config.getForceRelation(role.getCampId());
	}
	
	/**
	 * NPC看别人
	 * @param npc
	 * @param target
	 * @return
	 */
	protected ForceRelation getForceRelation(NpcInstance npc, RoleInstance target) {
		if(null == target || target.isDramaState()){
			//剧情模式中立
			return ForceRelation.neutral;
		}
		if(npc.getRoleId().equals(target.getRoleId())){
			return ForceRelation.friend; 
		}
		//灵魂模式
		//当前地图允许灵魂模式复活
		if(1 == this.map.getMapConfig().getCanSoulReborn() && 
				target.inState(StateType.soul)){
			return ForceRelation.neutral ;
		}
		//获得势力关系表
		ForceConfig config = GameContext.getNpcApp().getForceConfig(npc.getNpc().getForceId());
		if(null == config){
			return ForceRelation.neutral;
		}
		return config.getForceRelation(target.getCampId());
	}
	
	/**
	 * NPC看NPC
	 * @param npc
	 * @param target
	 * @return
	 */
	protected ForceRelation getForceRelation(NpcInstance npc, NpcInstance target) {
		if(!npcCanPk()) {
			return ForceRelation.friend; 
		}
		if(null == target){
			return ForceRelation.neutral;
		}
		if(npc.getRoleId().equals(target.getRoleId())){
			return ForceRelation.friend; 
		}
		byte npcForceId = (byte)npc.getNpc().getForceId();
		byte targetForceId = (byte)target.getNpc().getForceId();
		return GameContext.getNpcApp().getNpcForceRelation(npcForceId, targetForceId);
	}
	
	private ForceRelation getForceRelationByPkMap(RoleInstance role, RoleInstance target){
		if(null == target){
			return null;
		}
		if(role.getRoleId().equals(target.getRoleId())){
			return ForceRelation.friend; 
		}
		if(role.getPkStatus() == RolePkStatus.MASSACRE.getType() || target.getPkStatus() == RolePkStatus.MASSACRE.getType()){
			return ForceRelation.enemy; 
		}
		if(role.getPkStatus() == RolePkStatus.BATTLE.getType() && target.getPkStatus() == RolePkStatus.BATTLE.getType()) {
			if(GameContext.getTeamApp().isInSameTeam(role, target)){
				return ForceRelation.friend;
			}
			return ForceRelation.enemy;
		}
		return ForceRelation.friend;
	}
	
	public boolean canUseSkill(RoleInstance role,int skillId){
		return true ;
	}
	
	public boolean canUseGoods(RoleInstance role,int goodsId){
		return true ;
	}
	

	public boolean canDoffWearEquip(){
		return true ;
	}
	
	public Point getRebornPoint(RoleInstance role,RebornType type){
		if(RebornType.situ == type || RebornType.soul == type){
			return role.getCurrentPoint();
		}
		String mapId = role.getMapId() ;
		MapInstance mapInstance = role.getMapInstance();
		if(null != mapInstance){
			mapId = mapInstance.getMap().getMapId();
		}
		RebornPointDetail detail = GameContext.getRoleRebornApp().getRebornPointDetail(mapId,role);
		if(null == detail){
			return null ;
		}
		return detail.createPoint();
	}
	
	/**
	 * 当前地图是否允许进行组队,入队操作
	 * @return
	 */
	public boolean canBuildTeam(){
		return true ;
	}
	
	public abstract void useGoods(int goodsId);
	
	/**
	 * 当有攻击者造成伤害
	 * @param attacker
	 * @param hurt
	 */
    public void damageTaken(AbstractRole attacker, AbstractRole victim, int hurt){
    	
    }
    
    
    /**
     * 完美恢复
     * 血和蓝恢复到最大值
     * @param role
     */
    protected void perfectBody(AbstractRole role){
    	if(null == role || role.getRoleType() != RoleType.PLAYER){
    		return ;
    	}
		try {
			boolean isDeath = role.isDeath();
			role.setCurHP(role.getMaxHP());
			//将三英雄的hp全部回满
			GameContext.getHeroApp().switchableHeroPerfectBody((RoleInstance)role);
			// !!!!
			role.getHasSendDeathMsg().compareAndSet(true, false);
			role.getBehavior().notifyAttribute();
			// 通知队伍
			RoleInstance player = (RoleInstance)role;
			if (player.hasTeam()) {
				player.getTeam().syschDataNotify();
			}
			if(isDeath){
				//告诉客户复活
				C2001_RoleRebornRespMessage respMsg = new C2001_RoleRebornRespMessage();
				respMsg.setType(RespTypeStatus.SUCCESS);
				role.getBehavior().sendMessage(respMsg);
			}
		} catch (Exception ex) {
			logger.error("", ex);
		}
	}
    
    /**
	 * NPC死亡在不同地图的处理差异
	 * @param attacker 攻击者
	 * @param victim 受害者
	 */
    protected void npcDeathDiversity(AbstractRole attacker, AbstractRole victim){
    	
    }
    
    protected void mapNpcDeath(NpcInstance npc){
		this.npcList.remove(npc);
		// 通知同地图的用户
		this.notifyNpcDeath(npc);
	}

	public int getLineId() {
		return lineId;
	}
	public void setLineId(int lineId) {
		this.lineId = lineId;
	}

	public List<NpcRefreshTask> getNpcRefreshConfigList() {
		return npcRefreshTaskList;
	}

	public void setNpcRefreshConfigList(
			List<NpcRefreshTask> npcRefreshConfigList) {
		this.npcRefreshTaskList = npcRefreshConfigList;
	}
	
	public boolean mustRunMapLoop(){
		return false ;
	}

	public float getHurtRatio(AbstractRole role) {
		if(null == role || role.getRoleType() != RoleType.PLAYER){
			return 1 ;
		}
		return hurtRatio;
	}

	public void setHurtRatio(float hurtRatio) {
		this.hurtRatio = hurtRatio;
	}
	
	/**
	 * NPC之间是否可PK
	 * @return
	 */
	public boolean npcCanPk(){
		return 1 == map.getMapConfig().getNpcPK();
	}
	
	public boolean roleCanPk(){
		return 1 == map.getMapConfig().getRoleCanPK();
	}
	
	protected void kickRole(RoleInstance role){
		try{
			GameContext.getUserMapApp().changeMap(role, role.getCopyBeforePoint());
		}catch(Exception e){
			logger.error("",e);
		}
	}

	public List<JumpMapPoint> getRefreshJumpPointList() {
		return refreshJumpPointList;
	}
	
	
	public void doEvent(RoleInstance role,MapInstanceEvent event){
		
	}
	
	/**
	 * 清除地图上的障碍物
	 */
	public void clearBaffle(){
		for(Iterator<NpcInstance> it = this.baffleList.iterator(); it.hasNext(); ){
			NpcInstance baffle = it.next();
			if(null == baffle) {
				continue;
			}
			
			this.mapBaffleDeath(baffle);
		}
	}
	
	public void clearBaffle(String templateId){
		if(Util.isEmpty(templateId)){
			return ;
		}
		for(Iterator<NpcInstance> it = this.baffleList.iterator(); it.hasNext(); ){
			NpcInstance baffle = it.next();
			if(null == baffle) {
				continue;
			}
			if(!baffle.getNpc().getNpcid().equals(templateId)){
				continue ;
			}
			this.mapBaffleDeath(baffle);
		}
	}
	
	/**
	 * 清除npc
	 */
	public void clearNpc() {
		if(Util.isEmpty(this.npcList)) {
			return ;
		}
		for(Iterator<NpcInstance> it = this.npcList.iterator(); it.hasNext();) {
			NpcInstance npc = it.next();
			if(null == npc) {
				continue;
			}
			this.npcDeath(npc);
		}
	}
	
	/**
	 * 地图中触发剧情
	 * @param npcId
	 */
	private void npcTriggerDrama(String npcId, DramaTriggerType triggerType) {
		if(!GameContext.getDramaApp().canNpcTrigger(npcId, 
				this.map.getMapId(), triggerType)){
			return ;
		}
		Collection<RoleInstance> roleList = this.getRoleList();
		if(Util.isEmpty(roleList)) {
			return ;
		}
		for(RoleInstance role : roleList) {
			if(null == role) {
				continue;
			}
			GameContext.getDramaApp().triggerDrama(role, triggerType, (short)0, this.map.getMapId(), 0, npcId);
		}
	}
	
	public void addJumpMapPoint(JumpMapPoint jumpPoint){
		if(null == jumpPoint){
			return ;
		}
		if(!jumpPoint.getMapid().equals(this.getMap().getMapId())){
			return ;
		}
		this.refreshJumpPointList.add(jumpPoint);
		//广播
		Map toMap = GameContext.getMapApp().getMap(jumpPoint.getTomapid());
		//通知跳转点message
		C0235_MapJumpPointNotifyMessage addNotifyMsg = new C0235_MapJumpPointNotifyMessage();
		addNotifyMsg.setJumpMapId(jumpPoint.getMapid());
		addNotifyMsg.setJumpX((short)jumpPoint.getX());
		addNotifyMsg.setJumpY((short)jumpPoint.getY());
		addNotifyMsg.setToMapName(toMap.getMapConfig().getMapdisplayname());
		this.broadcastMap(null, addNotifyMsg);
	}
	
	public void removeJumpMapPoint(JumpMapPoint jumpPoint){
		if(null == jumpPoint){
			return ;
		}
		if(!jumpPoint.getMapid().equals(this.getMap().getMapId())){
			return ;
		}
		if(!this.refreshJumpPointList.remove(jumpPoint)){
			return ;
		}
		C0234_MapJumpPointRemoveNotifyMessage removeNotify = new C0234_MapJumpPointRemoveNotifyMessage();
		removeNotify.setJumpMapId(jumpPoint.getMapid());
		removeNotify.setJumpX((short)jumpPoint.getX());
		removeNotify.setJumpY((short)jumpPoint.getY());
		this.broadcastMap(null, removeNotify);
	}
	
	public int mapLogicType(){
		return this.getMap().getMapConfig().getMapLogicType().getType() ;
	}
	
	public Point getBeforeEnterPoint(RoleInstance role){
		return role.getCopyBeforePoint() ;
	}
	
	/**
	 * 角色死亡时，变强途径部位显示的文本（与变强途径想互斥）
	 * @return
	 */
	public String roleDieEnhanceOptionTips() {
		return null ;
	}

	public boolean isNormalLive(AbstractRole role){
		//活着并且没在灵魂状态
		return  role.getCurHP() >0 && !role.inState(StateType.soul) ;
	}

    public void notifyRoleAttributeToOther(RoleInstance role,Message message){
        if(null == message || null == role){
            return ;
        }
        // 通知队友
        if (role.getTeam() != null) {
            role.getTeam().broadcast(role.getRoleId(), message, false);
        }
        role.getHatredTarget().broadcast(message);
    }
}
