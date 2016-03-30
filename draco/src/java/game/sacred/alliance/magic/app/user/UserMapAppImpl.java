package sacred.alliance.magic.app.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.fall.BoxEntry;
import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.app.map.MapApp;
import sacred.alliance.magic.app.map.data.MapConfig;
import sacred.alliance.magic.app.map.logic.MapLogic;
import sacred.alliance.magic.app.map.point.CollectablePoint;
import sacred.alliance.magic.app.role.RoleBornApp;
import sacred.alliance.magic.base.ChangeMapEvent;
import sacred.alliance.magic.base.ChangeMapResult;
import sacred.alliance.magic.base.CollectPointNotifyType;
import sacred.alliance.magic.base.MapLogicType;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.channel.ChannelHandler;
import sacred.alliance.magic.core.channel.ChannelListener;
import sacred.alliance.magic.core.channel.ChannelSession;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.module.cache.Cache;
import sacred.alliance.magic.util.SessionUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.*;

import com.game.draco.GameContext;
import com.game.draco.app.buff.BuffLostType;
import com.game.draco.message.item.CollectPointItem;
import com.game.draco.message.item.IdentityPositionItem;
import com.game.draco.message.item.MultiCollectPointItem;
import com.game.draco.message.push.C0205_MapSwitchNotifyMessage;
import com.game.draco.message.push.C0206_MapLeaveNotifyMessage;
import com.game.draco.message.push.C0606_CollectPointNotifyMessage;
import com.game.draco.message.response.C0240_MapGetDataAndEnterRespMessage;
import com.game.draco.message.response.C0604_CollectPointRespMessage;


public class UserMapAppImpl implements UserMapApp {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private MapApp mapApp;
	private ChannelHandler handler;
	private RoleBornApp roleBornApp;

	public void setMapApp(MapApp mapApp) {
		this.mapApp = mapApp;
	}

	private MapInstance getNewMapInstance(AbstractRole role, String newMapId) {
		return MapLogicType.createMapInstance(role, newMapId);
	}
	
	public void exitMap(AbstractRole role, MapInstance mapInstance) {
		if (null == mapInstance || null == role) {
			return;
		}
		// 通知地图用户离开
		try {
			C0206_MapLeaveNotifyMessage pushMsg = new C0206_MapLeaveNotifyMessage();
			pushMsg.setInstanceId(role.getIntRoleId());
			mapInstance.broadcastMap(role, pushMsg);
		} catch (Exception ex) {
		}
		mapInstance.exitMap(role);
	}
	

	public void exitMap(AbstractRole role) {
		MapInstance mapInstance = role.getMapInstance();
		this.exitMap(role, mapInstance);
	}
	

	public void enter(AbstractRole role) throws ServiceException {
		MapInstance oldInstance = role.getMapInstance();
		// 用户要进入的地图id为role.mapid
		String nowMapId = role.getMapId();
		// 异常，如果用户所在地图实例不为空，并且要进入的地图的id与现在的地图实例地图id相同则直接进入
		if (nowMapId != null && oldInstance != null
				&& nowMapId.equals(oldInstance.getMap().getMapId())) {
			return;
		}
		// 获得要前往的地图数据
		Map targetMap = mapApp.getMap(nowMapId);
		// 出现异常，进入的地图id为空，将用户进入地图id重新设置
		if (nowMapId == null || nowMapId.length() <= 0 || targetMap == null) {
			if (oldInstance != null) {
				// 如果用户当前地图实例存在，则设置到当前地图
				role.setMapId(oldInstance.getMap().getMapId());
			} else {
				// 如果oldInstance也不存在，则设置到新手村
				// 不同种族的新手村不一样
				RoleBorn roleBorn = roleBornApp.getRoleBorn();
				role.setMapId(roleBorn.getBornMapId());
			}
			nowMapId = role.getMapId();
			targetMap = mapApp.getMap(nowMapId);
		}
		MapLogic nowLogic = targetMap.getMapLogic();
		// 该地图已经不允许再进入,有可能因为用户逗留在副本然后下线再上线，该副本开放条件已经发生了变化
		if (nowLogic != null && !nowLogic.canEnter(role)) {
			// 切换地图
			// nowMapId = targetMap.getMapConfig().getReloginjumpmapid();
			// 跳转到关卡的死亡复活点
			RebornPointDetail detail = GameContext.getRoleRebornApp()
					.getRebornPointDetail(
							targetMap.getMapId(),role);
			role.setMapId(detail.getRebornMapId());
		}
		MapInstance mapInstance = getNewMapInstance(role, nowMapId);
		if (null == mapInstance) {
			return;
		}
//		synchronized (mapInstance) {
			mapInstance.getMap().getMapLogic().enter(role);
			mapInstance.addAbstractRole(role);// 角色添加到地图实例中
			role.setMapId(nowMapId);
			role.setMapInstance(mapInstance);// 角色设置地图实例
//		}
	}

	private void notitySwitchMap(RoleInstance role, MapInstance oldMapInstance,
			String mapId,byte pointType) {
		//跳地图标识
		role.getJumpMap().compareAndSet(false, true);
		try {
			role.getBehavior().exitMap();
		} catch (ServiceException e) {
			logger.error("",e);
		}
		// 发送通知客户端切换地图消息
		C0205_MapSwitchNotifyMessage pushMsg = new C0205_MapSwitchNotifyMessage();
		pushMsg.setMapId(mapId);
		pushMsg.setPointType(pointType);
		MapConfig mapConfig = GameContext.getMapApp().getMapConfig(mapId);
		if(null != mapConfig && 
				mapConfig.getLogictype() != MapLogicType.defaultLogic.getType()){
			//!!! 解决客户端在寻路状态下进入一个封闭地图奔溃问题
			pushMsg.setPointType(ChangeMapEvent.clientStopFindPath.getEventType());
		}
		GameContext.getMessageCenter().send("", role.getUserId(), pushMsg);
	}

	/**
	 * 1.成功 0.目标地图不存在 2.当前地图不允许离开 3.目标地图不允许进入
	 */
	public ChangeMapResult changeMap(AbstractRole role, Point targetPoint) throws ServiceException {
		MapInstance nowMapInstance = role.getMapInstance();
		if(!(targetPoint instanceof LinePoint) && !(targetPoint instanceof TowerPoint)){
			//跳线的时候不需要判断此条件
			//如果是同一个地图，直接瞬移
			if(null != nowMapInstance && 
					nowMapInstance.getMap().getMapId().equals(targetPoint.getMapid())){
				nowMapInstance.teleport(role, targetPoint);
				return ChangeMapResult.ok ;
			}
		}
		
		String targetMapId = targetPoint.getMapid();
		
		// 地图不存在，不允许进入
		Map targetMap = mapApp.getMap(targetMapId);
		if (targetMap == null) {
			return ChangeMapResult.target_map_not_exist ;
		}
		int toMapX = targetPoint.getX();
		int toMapY = targetPoint.getY();
		if (null == nowMapInstance) {
			role.delAllBuffStat(BuffLostType.transLost);
			//设置进入点
			role.setCopyBeforePoint(role.getMapId(), role.getMapX(), role.getMapY());
			
			// 出现异常，用户的mapInstance为空，如果role.mapid存在则先进入，否则直接允许目标地图
			if (role.getMapId() == null
					|| mapApp.getMap(role.getMapId()) == null) {
				role.setMapId(targetMapId);
			}
			// 发送通知客户端切换地图消息,并且删除
			this.notitySwitchMap((RoleInstance) role, nowMapInstance,
					targetMapId,targetPoint.getEventType());
			// 设置用户位置
			role.setMapId(targetMapId); //导致跳转地图失败，地图id还是原来的，坐标是新的
			role.setMapX(toMapX);
			role.setMapY(toMapY);
			return ChangeMapResult.ok ;
		}
		try {
			// 如果当前地图不允许离开
			if (!nowMapInstance.getMap().getMapLogic().canExit(role)) {
				return ChangeMapResult.current_map_canot_exit;
			}
			// 如果目标地图不允许进入
			if (!targetMap.getMapLogic().canEnter(role)) {
				return ChangeMapResult.target_map_canot_enter ;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		role.delAllBuffStat(BuffLostType.transLost);
		//设置进入点
		role.setCopyBeforePoint(role.getMapId(), role.getMapX(), role.getMapY());
		
		nowMapInstance.getMap().getMapLogic().exit(role);
		// 修改用户的mapid，当用户发送进入地图命令时，不需要再带mapid便可直接进入了。
		role.setMapId(targetMapId);
		// 发送通知消息,并且删除
		this.notitySwitchMap((RoleInstance) role, nowMapInstance, targetMapId,targetPoint.getEventType());
		//地图的exit方法有可能会修改此点,单以跳转方法优先,所有这里需要重新设置下
		role.setMapId(targetMapId);
		// 设置用户位置
		role.setMapX(toMapX);
		role.setMapY(toMapY);
		role.setMapInstance(null);
		return ChangeMapResult.ok ;
	}


	public void roleOnEntranceWhenLogon(RoleInstance role, boolean isDieNow) {
		Map map = this.mapApp.getMap(role.getMapId());
		if (null == map) {
			//说明所在的地图不存在了
			//只有放到出生点
			logger.error("current map exist: mapId=" + role.getMapId() + " role=" + role.getRoleId());
			//服务器启动的时候确保RoleBorn不为null
			RoleBorn rb = GameContext.getRoleBornApp().getRoleBorn() ;
			Point p = rb.getBornPoint();
			this.setRoleLocation(role, p);
			map = this.mapApp.getMap(p.getMapid());
		}
		Date lastOffTime = role.getLastOffTime();
		if (null == lastOffTime) {
			lastOffTime = new Date();
		}
		if(role.getCurHP() == 0 && role.getLevel() == 1){
			//刚出生,满血满蓝
			role.setCurHP(role.getMaxHP());
			return ;
		}
		if (isDieNow) {
			role.setCurHP(Math.max(1, role.getMaxHP() / 2));
			// 死亡后下线再上线直接放到该关卡的复活点
			//服务器启动的时候会确保 rebornPointDetail 不为null
			RebornPointDetail rebornPointDetail = GameContext.getRoleRebornApp().getRebornPointDetail(map.getMapId(), role);
			if(null != rebornPointDetail){
				role.setMapId(rebornPointDetail.getRebornMapId());
				role.setMapX(rebornPointDetail.getMapX());
				role.setMapY(rebornPointDetail.getMapY());
			}
			return;
		}
		MapConfig mapConfig = map.getMapConfig();
		if (!mapConfig.iscopymode()) {
			return;
		}
		if(MapLogicType.arenaLogic.getType() == mapConfig.getLogictype() 
				|| MapLogicType.arenaPK.getType() ==  mapConfig.getLogictype()){
			//这种情况其实是异常情况,如果是在擂台赛中下线,exitMap时会将坐标放入进入点
			// 置于擂台赛入口处
			this.setRoleLocation(role, role.getCopyBeforePoint());
			return ;
		}
	}
	
	private void setRoleLocation(RoleInstance role,Point point){
		role.setMapId(point.getMapid());
		role.setMapX(point.getX());
		role.setMapY(point.getY());
	}


	/**
	 * 进入地图时,把采集点push给用户
	 */
	@Override
	public void pushCollectPointMessage(RoleInstance role) {
		MapInstance mapInstance = role.getMapInstance();
		if (null == mapInstance) {
			return;
		}
		java.util.Map<String, Set<String>> collectPointMapping = mapInstance.getCollectPointMapping();
		if (Util.isEmpty(collectPointMapping)) {
			return;
		}
		java.util.Map<String, CollectablePoint<RoleInstance>> collectPointMap = mapInstance.getCollectPointMap();
		if (Util.isEmpty(collectPointMap)) {
			return;
		}
		C0604_CollectPointRespMessage notify = new C0604_CollectPointRespMessage();
		List<MultiCollectPointItem> points = new ArrayList<MultiCollectPointItem>();

		java.util.Map<String, Set<String>> collectPointMappingCopy = new HashMap<String, Set<String>>();
		collectPointMappingCopy.putAll(collectPointMapping);

		for (Iterator<java.util.Map.Entry<String, Set<String>>> it = collectPointMappingCopy.entrySet().iterator(); it.hasNext();) {
			java.util.Map.Entry<String, Set<String>> current = it.next();
			MultiCollectPointItem item = new MultiCollectPointItem();
			List<IdentityPositionItem> ipItems = new ArrayList<IdentityPositionItem>();
			int index = -1;
			for (String pointInstanceId : current.getValue()) {
				CollectablePoint<RoleInstance> cp = collectPointMap.get(pointInstanceId);
				if (null == cp) {
					continue;
				}
				index++;
				if (0 == index) {
					int collectType = cp.getCollectPoint().getType();
					//int skillType = cp.getCollectPoint().getLifeskillType();
					String info = cp.isSatisfyCond(role);
					if (null != info && info.trim().length() > 0) {
						item.setType((byte) 0);
						item.setCanPick(info);
					} else {
						item.setType((byte) 1);
					}
					item.setItemDesc(cp.getCollectPoint().getDesc());
					item.setItemImage((short) cp.getCollectPoint().getImageId());
					item.setItemName(cp.getCollectPoint().getName());
					item.setCollectType((byte) collectType);

					/*if (null == info
							&& PointType.get(collectType) == PointType.GeneralSkillCollectPoint) {
						if (cp.getCollectPoint().isCanAddProficiency(
								role.getRoleLifeskillBySkillType(skillType))) {
							item.setDisplayFlag((byte) 1);
						} else {
							item.setDisplayFlag((byte) 0);
						}
					}*/
					// 特殊采集点 特殊道具和熟练度不予处理
				}
				IdentityPositionItem idPosItem = new IdentityPositionItem();
				idPosItem.setId(cp.getInstanceId());
				idPosItem.setX((short) cp.getX());
				idPosItem.setY((short) cp.getY());
				ipItems.add(idPosItem);
			}
			item.setItems(ipItems);
			points.add(item);
		}
		collectPointMappingCopy.clear();
		collectPointMappingCopy = null;
		notify.setPoints(points);
		GameContext.getMessageCenter().send("", role.getUserId(), notify);
	}

	@Override
	public void notifyNewCollectPoint(RoleInstance role,
			CollectablePoint<RoleInstance> cp, Point point) {
		C0606_CollectPointNotifyMessage notify = new C0606_CollectPointNotifyMessage();
		notify.setType(CollectPointNotifyType.Refresh.getType());
		CollectPointItem pointItem = new CollectPointItem();
		pointItem.setInstanceId(cp.getInstanceId());
		pointItem.setItemDesc(cp.getCollectPoint().getDesc());
		pointItem.setItemImage((short) cp.getCollectPoint().getImageId());
		pointItem.setItemName(cp.getCollectPoint().getName());
		pointItem.setX((short) point.getX());
		pointItem.setY((short) point.getY());
		String info = cp.isSatisfyCond(role);
		if (info == null || info.length() <= 0) {
			pointItem.setType((byte) 1);
		} else {
			pointItem.setType((byte) 0);
			pointItem.setCanPick(info);
		}
		pointItem.setCollectType((byte) cp.getCollectPoint().getType());

		/*if (null == info
				&& PointType.get(cp.getCollectPoint().getType()) == PointType.GeneralSkillCollectPoint) {
			if (cp.getCollectPoint().isCanAddProficiency(
					role.getRoleLifeskillBySkillType(cp.getCollectPoint()
							.getLifeskillType()))) {
				pointItem.setDisplayFlag((byte) 1);
			} else {
				pointItem.setDisplayFlag((byte) 0);
			}
		}*/
		// 特殊采集点 特殊道具和熟练度不予处理
		notify.setItem(pointItem);
		GameContext.getMessageCenter().send("", role.getUserId(), notify);
	}
	
	
	private boolean isEnterMapSuccess(Object message){
		if(null == message){
			return false ;
		}
		if(message instanceof C0240_MapGetDataAndEnterRespMessage){
			C0240_MapGetDataAndEnterRespMessage respMsg = (C0240_MapGetDataAndEnterRespMessage)message ;
			return respMsg.getType() == RespTypeStatus.SUCCESS ;
		}
		/*if(message instanceof C0203_MapEnterNoticeRespMessage){
			C0203_MapEnterNoticeRespMessage respMsg = (C0203_MapEnterNoticeRespMessage)message ;
			return respMsg.getType() == RespTypeStatus.SUCCESS ;
		}*/
		return false ;
	}

	@Override
	public void start() {
		handler.addListener(new ChannelListener() {
			@Override
			public void fireMessageSent(ChannelSession session, Object message)
					throws Exception {
				try {
					if (null == session) {
						return;
					}
					if(!isEnterMapSuccess(message)){
						return ;
					}
					// 进入地图成功后,主动push NPC头顶任务标识
					// 获得角色
					String userId = SessionUtil.getUserId(session);
					RoleInstance role = GameContext.getOnlineCenter()
							.getRoleInstanceByUserId(userId);
					if(null == role){
						return ;
					}
					// NPC头顶
					GameContext.getUserQuestApp().notifyQuestNpcHeadSign(role);
					// 采集点
					pushCollectPointMessage(role);
					// 用户宝箱
					pushBoxMessage(role);
				} catch (Exception ex) {
					logger.error("", ex);
				}

			}

			@Override
			public void fireSessionClosed(ChannelSession session)
					throws Exception {
			}

			@Override
			public void messageReceived(ChannelSession arg0, Object arg1)
					throws Exception {
			}
		});
		//加载副本入口地图信息
		//世界地图传送使用
		//conveyCopyMap = conveyCopyMapLoader.getDataMap();
	}

	@Override
	public void stop() {

	}

	@Override
	public void pushBoxMessage(RoleInstance role) {
		MapInstance mapInstance = role.getMapInstance();
		if (null == mapInstance) {
			return;
		}
		Set<String> boxSet = mapInstance.getRoleBoxMapping().get(
				role.getRoleId());
		if (null == boxSet) {
			return;
		}
		for (String instanceId : boxSet) {
			Cache<String, BoxEntry> boxes = GameContext.getMapApp()
					.getBoxesCache();
			BoxEntry entry = boxes.getQuiet(instanceId);
			if (null == entry) {
				continue;
			}
			entry.notifyOwner();
		}
	}

	//重置副本
	/*@Override
	public void resetUserCopy(AbstractRole role) {
		Team team = role.getTeam();
		if(team == null){
			return;
		}
		if(team.getTeamType() == TeamType.COPY_TEAM){
			CopyTeam copyTeam = (CopyTeam)team;
			copyTeam.clearContainerMap();
		}
	}*/

	
	/** 调试工具重置副本 **/
	/*@Override
	public void resetUserCopy(AbstractRole role, String mapId) {
		Map map = mapApplication.getMap(mapId);
		String containerId = role.getUnStoreCopyMap().get(
				map.getMapConfig().getMaplevelname());
		if (null == containerId) {
			return;
		}
		MapContainer mapContainer = this.mapApplication.getCopyContainerMap()
				.get(containerId);
		if (null == mapContainer) {
			return;
		}
		mapContainer.destroy();
		role.getUnStoreCopyMap().remove(map.getMapConfig().getMaplevelname());
	}
	*/

	public void setRoleBornApp(RoleBornApp roleBornApp) {
		this.roleBornApp = roleBornApp;
	}


	@Override
	public void setArgs(Object args) {

	}


	
	/** 
	 *  传送/自动寻路到目标地图的目标点
	 *  type = 0 使用传送卷轴
	 *  type = 1 自动寻路
	 * @throws Exception 
	 */
	/*public void gotoTargetMapPoint(RoleInstance role, int type, Point point) throws Exception{
		ErrorRespMessage resp = new ErrorRespMessage();
		if (role == null || point == null || Util.isEmpty(point.getMapid())
				|| point.getX() <= 0 || point.getY() <= 0) {
			resp.setInfo("此地图不可传送和自动寻路或此地图尚未开启");
			GameContext.getMessageCenter().sendByRoleId("-1",
					role.getRoleId(), resp);
			return;
		}

		if (type == 0 || type == 2) {// 使用传送卷轴
			Map map = GameContext.getMapApp()
					.getMap(point.getMapid());
			if (map == null) {
				resp.setInfo("此地图尚未开启");
				GameContext.getMessageCenter().sendByRoleId("-1",
						role.getRoleId(), resp);
				return;
			}
			MapConfig mapConfig = map.getMapConfig();
			if (mapConfig.iscopymode()) {
				resp.setInfo("副本区域不可传送");
				GameContext.getMessageCenter().sendByRoleId("-1",
						role.getRoleId(), resp);
				return;
			}
			if (MapType.map_fee == MapType.getType(mapConfig.getMaptype())) {
				resp.setInfo("收费区不可传送");
				GameContext.getMessageCenter().sendByRoleId("-1",
						role.getRoleId(), resp);
				return;
			}
			if (role.getMapId().equals(point.getMapid())
					&& role.getMapX() == point.getX()
					&& role.getMapY() == point.getY()) {
				resp.setInfo("您已在此处,不需要传送");
				GameContext.getMessageCenter().sendByRoleId("-1",
						role.getRoleId(), resp);
				return;
			}
			RoleGoods roleGoods = role.getGoodsInfo().getEffectsprop(
					SpecialGoodsType.teammate);
			if (null == roleGoods) {
				MapChangeRespMessage mcresp = new MapChangeRespMessage();
				mcresp.setStatus((byte) 2);
				mcresp.setInfo("您没有传送卷轴,是否去商城购买");
				GameContext.getMessageCenter().sendByRoleId("-1",
						role.getRoleId(), mcresp);
				return;
			}
			GameContext.getUserMapApp().changeMap(role, point);
			GameContext.getUserGoodsApplication().deleteByGoodsId(
					role.getRoleId(), roleGoods.getGoodsId());
		}

		else if (type == 1 || type == 3) {// 自动寻路
			SearchRoadRespMessage sresp = new SearchRoadRespMessage();
			List<PointItem> points = GameContext
					.getAutoSearchRoadApp().searchRoad(
							role.getMapId(),
							point.getMapid(),
							new PointItem((short) point.getX(), (short) point
									.getY()), null);
			
			if (points == null) {
				resp.setInfo("不能使用这个功能");
				GameContext.getMessageCenter().sendByRoleId("-1",
						role.getRoleId(), sresp);
				return;
			}
			sresp.setResultId((byte) 1);
			sresp.setSearchMapPoint(points);
			GameContext.getMessageCenter().sendByRoleId("-1",
					role.getRoleId(), sresp);
		}
	}*/
	
	public void setHandler(ChannelHandler handler) {
		this.handler = handler;
	}
}
