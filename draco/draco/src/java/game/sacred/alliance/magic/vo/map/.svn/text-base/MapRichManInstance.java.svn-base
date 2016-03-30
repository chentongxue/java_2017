package sacred.alliance.magic.vo.map;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import lombok.Getter;
import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.scheduler.job.LoopCount;
import sacred.alliance.magic.util.RandomUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapLineInstance;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.richman.RichManApp;
import com.game.draco.app.richman.config.RichManEvent;
import com.game.draco.app.richman.config.RichManMapEvent;
import com.game.draco.app.richman.config.RichManState;
import com.game.draco.app.richman.domain.RoleRichMan;
import com.game.draco.app.richman.vo.RichManEventType;
import com.game.draco.app.richman.vo.RichManRoleBehavior;
import com.game.draco.app.richman.vo.RichManRoleStat;
import com.game.draco.app.richman.vo.event.RichManEventLogic;
import com.game.draco.message.item.RichManEventItem;
import com.game.draco.message.item.RichManMapRoleItem;
import com.game.draco.message.item.RichManRandomEventItem;
import com.game.draco.message.response.C2651_RichManMapRoleEnterExitNoticeMessage;
import com.game.draco.message.response.C2655_RichManRandomEventDelNoticeMessage;
import com.game.draco.message.response.C2656_RichManRandomEventAddNoticeMessage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MapRichManInstance extends MapLineInstance {
	private final static byte BEHAVIOR_LIST_ONE = 0;
	private final static byte BEHAVIOR_LIST_TWO = 1;
	private LoopCount randomEventRefreshLoopCount = new LoopCount(GameContext.getRichManApp()
			.getRichManConfig().getRandomEventRefreshTime());
	
	@Getter private RichManMapEvent[] mapEvent = null;
	private java.util.Map<Byte, HashSet<Integer>> gridRoleIdMap = null;
	private java.util.Map<Byte, HashSet<Integer>> gridRandomEventIdMap = null;
	private java.util.Map<Integer, RichManRoleStat> roleStatMap = Maps.newConcurrentMap();
	private List<RichManRoleBehavior> roleBehaviorOneList = Lists.newArrayList();
	private List<RichManRoleBehavior> roleBehaviorTwoList = Lists.newArrayList();
	private byte curHandleIndex;
	
	public MapRichManInstance(Map map, int lineId) {
		super(map, lineId);
		this.init();
	}
	
	private void init() {
		java.util.Map<Byte, RichManMapEvent> allMapEvent = GameContext.getRichManApp().getAllMapEvent();
		int gridSize = GameContext.getRichManApp().getMapGridNum();
		mapEvent = new RichManMapEvent[gridSize];
		gridRoleIdMap = new HashMap<Byte, HashSet<Integer>>(gridSize);
		gridRandomEventIdMap = new HashMap<Byte, HashSet<Integer>>(gridSize);
		for(Entry<Byte, RichManMapEvent> entry : allMapEvent.entrySet()) {
			RichManMapEvent event = entry.getValue();
			if(null == event) {
				continue;
			}
			byte gridId = event.getGridId();
			mapEvent[gridId] = event;
			gridRoleIdMap.put(gridId, new HashSet<Integer>());
			gridRandomEventIdMap.put(gridId, new HashSet<Integer>());
		}
		
		this.curHandleIndex = BEHAVIOR_LIST_ONE;
	}
	
	@Override
	protected void updateSub() throws ServiceException {
		super.updateSub();
		//随机事件
		if(randomEventRefreshLoopCount.isReachCycle()) {
			this.refreshRandomEvent();
		}
		
		//处理event
		List<RichManRoleBehavior> handleList = this.getRoleBehaviorHandleList();
		if(Util.isEmpty(handleList)) {
			return;
		}
		for(RichManRoleBehavior behavior : handleList) {
			this.handleRoleBehavior(behavior);
		}
		handleList.clear();
		
	}
	
	
	private void refreshRandomEvent() {
		RichManApp richManApp = GameContext.getRichManApp();
		int randomEventNum = this.getRoleCount() - richManApp.getRichManConfig().getRandomEventBaseNum();
		if(randomEventNum <= 0) {
			return ;
		}
		List<RichManRandomEventItem> eventItemList = Lists.newArrayList();
		for(int i=0; i < randomEventNum; i++) {
			RichManEvent event = richManApp.getRandomEvent();
			if(null == event) {
				continue ;
			}
			byte gridNum = richManApp.getMapGridNum();
			byte gridId = 3;//(byte)RandomUtil.randomInt(0, gridNum-1);
			HashSet<Integer> randomEventIdSet = this.gridRandomEventIdMap.get(gridId);
			if(null == randomEventIdSet) {
				continue ;
			}
			//随机事件加入格子
			randomEventIdSet.add(event.getId());
			RichManRandomEventItem eventItem = new RichManRandomEventItem();
			eventItem.setGridId(gridId);
			eventItem.setId((short)event.getId());
			eventItem.setAnimId(event.getAnimId());
			eventItemList.add(eventItem);
		}
		//广播
		C2656_RichManRandomEventAddNoticeMessage respMsg = new C2656_RichManRandomEventAddNoticeMessage();
		respMsg.setEventItemList(eventItemList);
		this.broadcastMap(null, respMsg);
	}
	
	@Override
	public void exitMap(AbstractRole role) {
		super.exitMap(role);
		int roleId = role.getIntRoleId();
		RichManRoleStat stat = this.roleStatMap.get(roleId);
		HashSet<Integer> roleIdSet = gridRoleIdMap.get(stat.getGridId());
		roleIdSet.remove(roleId);
		this.roleStatMap.remove(role.getRoleId());
		//广播角色出地图
		this.broadcastRoleEnterExist(role, (byte)1, stat.getGridId());
	}
	
	/**
	 * 返回玩家状态信息 
	 */
	public RichManRoleStat getRoleStat(int roleId) {
		return this.roleStatMap.get(roleId);
	}
	
	public void addRoleStat(int roleId, RichManRoleStat rolestat) {
		this.roleStatMap.put(roleId, rolestat);
	}
	
	/**
	 * 返回格子上的玩家角色id 
	 */
	public HashSet<Integer> getGridRoleIdSet(byte gridId) {
		return this.gridRoleIdMap.get(gridId);
	}
	
	private void handleRoleBehavior(RichManRoleBehavior behavior) {
		try {
			RoleInstance role = this.getRoleInstance(String.valueOf(behavior.getRoleId()));
			if(null == role) {
				return ;
			}
			RichManEventLogic logic = GameContext.getRichManApp().getEventLogic(
					behavior.getEvent().getEventType());
			logic.execute(this, role, behavior);
		} catch (Exception ex) {
			logger.error("MapRichManInstance.handleRoleBehavior() error, ", ex);
		}
	}
	
	/**
	 * 角色事件加入到执行队列中 
	 */
	public void addRoleBehavior(RichManRoleBehavior behavior) {
		List<RichManRoleBehavior> handleList = null;
		if(this.curHandleIndex == BEHAVIOR_LIST_ONE) {
			handleList = roleBehaviorTwoList;
		}
		else {
			handleList = roleBehaviorOneList;
		}
		handleList.add(behavior);
	}
	
	public void roleArrived(RoleInstance role) {
		int roleId = role.getIntRoleId();
		RichManRoleStat roleStat = this.getRoleStat(role.getIntRoleId());
		if(null == roleStat) {
			return ;
		}
		byte gridId = roleStat.getGridId();
		//格子上固定事件
		RichManMapEvent mapEvent = this.mapEvent[gridId];
		RichManEvent eventCfg = mapEvent.getRichManEvent();
		if(eventCfg.getEventType() != RichManEventType.None) {
			this.addRoleBehavior(new RichManRoleBehavior(roleId, eventCfg));
		}
		//格子上随机事件
		HashSet<Integer> randomEventIdSet = this.gridRandomEventIdMap.get(gridId);
		if(Util.isEmpty(randomEventIdSet)) {
			return ;
		}
		for(int randomEventId : randomEventIdSet) {
			RichManEvent event = GameContext.getRichManApp().getRichManEvent(randomEventId);
			if(null == event) {
				continue;
			}
			this.addRoleBehavior(new RichManRoleBehavior(roleId, event));
		}
		//清空格子上的随机事件
		randomEventIdSet.clear();
		//广播格子上随机事件清除
		C2655_RichManRandomEventDelNoticeMessage respMsg = new C2655_RichManRandomEventDelNoticeMessage();
		respMsg.setGridId(gridId);
		this.broadcastMap(null, respMsg);
	}
	
	private List<RichManRoleBehavior> getRoleBehaviorHandleList() {
		if(this.curHandleIndex == BEHAVIOR_LIST_ONE) {
			if(!Util.isEmpty(roleBehaviorOneList)) {
				return roleBehaviorOneList;
			}
			//玩家行为onelist 为空则需要判断twolist
			//如果twolist也为空则表示没有数据要处理
			if(Util.isEmpty(roleBehaviorTwoList)) {
				return roleBehaviorOneList;
			}
			//twolist不为空则把twolist作为当前处理的玩家行为list
			this.curHandleIndex = BEHAVIOR_LIST_TWO;
			return roleBehaviorTwoList;
		}
		else{
			if(!Util.isEmpty(roleBehaviorTwoList)) {
				return roleBehaviorTwoList;
			}
			if(Util.isEmpty(roleBehaviorOneList)) {
				return roleBehaviorTwoList;
			}
			this.curHandleIndex = BEHAVIOR_LIST_ONE;
			return roleBehaviorOneList;
		}
	}
	
	/**
	 * 格子上随机事件数据
	 * @param gridId
	 * @return
	 */
	public List<RichManEventItem> getGridRandomEventList(byte gridId) {
		//随机事件
		HashSet<Integer> randomEventIdSet = gridRandomEventIdMap.get(gridId);
		if(Util.isEmpty(randomEventIdSet)) {
			return null;
		}
		List<RichManEventItem> randomEventItemList = Lists.newArrayList();
		for(Object randomEventId : randomEventIdSet) {
			RichManEvent randomEvent = GameContext.getRichManApp()
			.getRichManEvent((Integer)randomEventId);
			if(null == randomEvent) {
				continue;
			}
			RichManEventItem randomEventItem = new RichManEventItem();
			randomEventItem.setId((short)randomEvent.getId());
			randomEventItem.setAnimId(randomEvent.getAnimId());
			randomEventItemList.add(randomEventItem);
		}
		return randomEventItemList;
	}
	
	/**
	 * 格子上其他玩家数据
	 * @param gridId
	 * @return
	 */
	public List<RichManMapRoleItem> getGridRoleItemList(byte gridId) {
		HashSet<Integer> roleIdSet = gridRoleIdMap.get(gridId);
		if(Util.isEmpty(roleIdSet)) {
			return null;
		}
		List<RichManMapRoleItem> gridRoleItemList = Lists.newArrayList();
		for(Object id : roleIdSet) {
			int roleId = (Integer)id;
			RichManMapRoleItem roleItem = this.getMapRoleItem(roleId);
			if(null == roleItem) {
				continue ;
			}
			gridRoleItemList.add(roleItem);
		}
		return gridRoleItemList;
	}
	
	private RichManMapRoleItem getMapRoleItem(int roleId) {
		RichManMapRoleItem roleItem = new RichManMapRoleItem();
		roleItem.setRoleId(roleId);
		RichManRoleStat roleStat = this.roleStatMap.get(roleId);
		roleItem.setFace(roleStat.getFace());
		List<RichManEventItem> eventItemList = Lists.newArrayList();
		//玩家身上状态
		java.util.Map<Byte, Long> stateOverTimeMap = roleStat.getStateOverTimeMap();
		if(!Util.isEmpty(stateOverTimeMap)) {
			for(Entry<Byte, Long> entry : stateOverTimeMap.entrySet()) {
				byte stateId = entry.getKey();
				RichManState stateConfig = GameContext.getRichManApp().getRichManState(stateId);
				if(null == stateConfig) {
					continue ;
				}
				RichManEventItem item = new RichManEventItem();
				item.setId(stateConfig.getId());
				item.setAnimId(stateConfig.getEffectId());
				eventItemList.add(item);
			}
			
		}
		//玩家身上随机事件
		RoleRichMan roleRichMan = GameContext.getUserRichManApp().getRoleRichMan(roleId);
		java.util.Map<Integer, Byte> randomEventMap = roleRichMan.getRandomEventMap();
		if(Util.isEmpty(randomEventMap)) {
			return roleItem;
		}
		
		for(Entry<Integer, Byte> entry : randomEventMap.entrySet()) {
			int eventId = entry.getKey();
			RichManEvent event = GameContext.getRichManApp()
				.getRichManEvent(eventId);
			RichManEventItem item = new RichManEventItem();
			item.setId((short)event.getId());
			item.setAnimId(event.getAnimId());
			eventItemList.add(item);
		}
		roleItem.setEventItemList(eventItemList);
		return roleItem;
	}
	
	/**
	 * 初始化角色并广播
	 * @param role
	 */
	public void broadcastRoleEnterExist(AbstractRole role, byte opType, byte gridId) {
		//广播角色进入地图
		C2651_RichManMapRoleEnterExitNoticeMessage pushMsg = new C2651_RichManMapRoleEnterExitNoticeMessage();
		pushMsg.setOpType(opType);
		pushMsg.setGridId(gridId);
		pushMsg.setMapRoleItem(this.getMapRoleItem(role.getIntRoleId()));
		this.broadcastMap(role, pushMsg);
	}
	
	public void addRoleIdInGrid(int roleId, byte gridId) {
		HashSet<Integer> roleIds = gridRoleIdMap.get(gridId);
		roleIds.add(roleId);
	}
	
}
