package sacred.alliance.magic.vo;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.app.map.MapLineInstanceCreatedEvent;
import sacred.alliance.magic.app.map.data.MapConfig;
import sacred.alliance.magic.base.MapLogicType;
import sacred.alliance.magic.base.SwitchLineType;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.map.MapActiveInstance;
import sacred.alliance.magic.vo.map.MapAngelChestInstance;
import sacred.alliance.magic.vo.map.MapArenaTopInstance;
import sacred.alliance.magic.vo.map.MapRichManInstance;

import com.game.draco.GameContext;
import com.game.draco.app.team.Team;

public class MapLineContainer extends MapContainer<MapLineInstance> {
	private final static String PRE_FIX = "line_" ;
	private final static int ADD_LINE_ID = 1;
	private final static int DEC_LINE_ID = 0;
	private AtomicInteger lineIdSequence = new AtomicInteger(0);
	private Set<Integer> destroyLine = new TreeSet<Integer>();
	private java.util.Map<Integer,MapLineInstance> lineInstanceMap = new ConcurrentHashMap<Integer,MapLineInstance>();
	private String mapId;
	private byte[] lock = new byte[1];
	
	@Override
	protected String getNamePrefix(){
		return PRE_FIX ;
	}
	
	@Override
	public boolean canDestroy() {
		return false;
	}
	
	
	@Override
	public void destroy() {
	}

	@Override
	public void update() {
		for(MapLineInstance instance : this.lineInstanceMap.values()){
			if(instance.canDestroy()){
				instance.destroy();
				int lineId = instance.getLineId();
				this.destroyLineManager(ADD_LINE_ID, lineId);
			}
		}
	}
	
	
	
	@Override
	public MapInstance createMapInstance(Map map, AbstractRole role) {
		//用户切线逻辑,切到队友所在线逻辑
		MapLineInstance instance = null;
		if((instance = this.manualSwitchLine(role)) != null
				|| (instance = this.teamLineInstance(role, map.getMapId())) != null
				|| (instance = this.getMapLineInstance(map, role)) != null){
			return instance;
		}
		return createMapLineInstance(map,role);
	}
	
	
	private int getLineId(){
		if(!Util.isEmpty(this.destroyLine)){
			int lineId = this.destroyLineManager(DEC_LINE_ID, 0);
			return lineId;
		}
		return lineIdSequence.incrementAndGet();
	}
	
	
	//手动切线
	private MapLineInstance manualSwitchLine(AbstractRole role){
		SwitchLineType switchLineType = role.getSwitchLineType();
		if(switchLineType != SwitchLineType.Manual){
			return null;
		}
		int lineId = role.getLineId();
		MapLineInstance instance = this.lineInstanceMap.get(lineId);
		if(instance != null 
				&& instance.canEnter(role)
				&& instance.changeMapLineStatus(LineMapStatus.Assign)){
			role.setSwitchLineType(SwitchLineType.Automatic);
			return instance ;
		}
		return null;
	}
	
	
	//获得队友所在分线实例
	private MapLineInstance teamLineInstance(AbstractRole role, String mapId){
		//判断是否有队友在此地图中
		Team team = ((RoleInstance)role).getTeam();
		if(team == null){
			return null;
		}
		try{
			byte[] teamLock = team.getTeamLock();
			synchronized(teamLock){
				String roleId = role.getRoleId();
				for(AbstractRole teamRole : team.getMembers()){
					if(!mapId.equals(teamRole.getMapId()) || roleId.equals(teamRole.getRoleId())){
						continue ;
					}
					int lineId = teamRole.getLineId();
					role.setLineId(lineId);
					role.setSwitchLineType(SwitchLineType.Manual);
					return this.manualSwitchLine(role);
				}
			}
		}catch(Exception e){}
		
		return null;
	}
	
	private MapLineInstance createMapLineInstance(Map map, AbstractRole role){
		synchronized(lock){
			MapLineInstance instance = this.getMapLineInstance(map, role);
			if(instance != null){
				return instance;
			}
			int lineId = this.getLineId();
			MapLineInstance newInstance = this.newInstance(map, lineId);
			newInstance.initNpc(true);
			this.addMapInstance(newInstance);
			//放入eventbus
			GameContext.getEventBus().post(new MapLineInstanceCreatedEvent(newInstance));
			return newInstance;
		}
	}
	
	private MapLineInstance newInstance(Map map, int lineId){
		byte logicType = map.getMapConfig().getLogictype();
		if(MapLogicType.siege.getType() == logicType){
			//怪物攻城
			return new MapSiegeInstance(map, lineId);
		}
		if(MapLogicType.dps.getType() == logicType){
			return new MapDpsInstance(map,lineId);
		}
		if(MapLogicType.activeMap.getType() == logicType){
			return new MapActiveInstance(map,lineId);
		}
		if(MapLogicType.angelChest.getType() == logicType){
			return new MapAngelChestInstance(map,lineId);
		}
		if(MapLogicType.arenaTop.getType() == logicType){
			return new MapArenaTopInstance(map,lineId);
		}
		if(MapLogicType.richman.getType() == logicType){
			return new MapRichManInstance(map,lineId);
		}
		return new MapLineInstance(map, lineId);
	}
	
	
	
	/* 当前分线 >= 最大分线限制时，返回人数最少的那条分线
	 * 否则，返回人数最多，且未超过最大人数限制的分线
	 */
	private MapLineInstance getMapLineInstance(Map map, AbstractRole role){
		MapConfig mapConfig = map.getMapConfig();
		int maxLineCount = mapConfig.getMaxLineCount();
		int lineCount = this.lineInstanceMap.size();
		
		if(lineCount >= maxLineCount){
			return this.getMinRoleCountLine();
		}
		
		return this.getMaxRoleCountLine(map, role);
	}
	
	
	
	//返回队列中，人数最少的一条分线地图实例
	private MapLineInstance getMinRoleCountLine(){
		int mixRoleCount = Integer.MAX_VALUE;
		MapLineInstance minRoleLineInstance = null;
		for(MapLineInstance lineInstance : this.lineInstanceMap.values()){
			int roleCount = lineInstance.getRoleCount();
			if(roleCount < mixRoleCount){
				mixRoleCount = roleCount;
				minRoleLineInstance = lineInstance;
			}
		}
		if(minRoleLineInstance != null 
				&& minRoleLineInstance.changeMapLineStatus(LineMapStatus.Assign)){
			return minRoleLineInstance;
		}
		return null;
	}
	
	
	//返回队列中人数最多而小于限制人数上限的分线地图实例
	private MapLineInstance getMaxRoleCountLine(Map map, AbstractRole role){
		MapConfig mapConifg = map.getMapConfig();
		int limitRoleCount = mapConifg.getMaxRoleCount();
		
		int maxRoleCount = -1;
		MapLineInstance maxRoleLineInstance = null;
		for(MapLineInstance lineInstance : this.lineInstanceMap.values()){
			int roleCount = lineInstance.getRoleCount();
			if(roleCount >= limitRoleCount 
					|| !lineInstance.canEnter(role)){
				continue;
			}
			if(roleCount > maxRoleCount){
				maxRoleCount = roleCount;
				maxRoleLineInstance = lineInstance;
			}
		}
		if(maxRoleLineInstance != null 
				&& maxRoleLineInstance.changeMapLineStatus(LineMapStatus.Assign)){
			return maxRoleLineInstance;
		}
		return null;
	}
	
	private void addMapInstance(MapLineInstance newInstance){
		lineInstanceMap.put(newInstance.getLineId(), newInstance);
		newInstance.setMapContainer(this);
		GameContext.getMapApp().addMapInstance(newInstance);
	}
	
	
	private int destroyLineManager(int operate, int lineId){
		synchronized(lock){
			if(operate == ADD_LINE_ID){
				this.destroyLine.add(lineId);
			}
			else if(operate == DEC_LINE_ID){
				lineId = this.destroyLine.iterator().next();
				this.destroyLine.remove(lineId);
			}
		}
		return lineId;
	}

	public String getMapId() {
		return mapId;
	}
	public void setMapId(String mapId) {
		this.mapId = mapId;
	}
	
	public Collection<MapLineInstance> getMapInstances(){
		return this.lineInstanceMap.values();
	}
	
	protected java.util.Map<Integer,MapLineInstance> getLineInstanceMap(){
		return this.lineInstanceMap;
	}
	
	public MapLineInstance getMapInstance(int lineId){
		return this.lineInstanceMap.get(lineId);
	}
}
