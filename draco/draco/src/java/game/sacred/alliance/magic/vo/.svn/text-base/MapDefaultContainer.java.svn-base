package sacred.alliance.magic.vo;

import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.base.MapLogicType;
import sacred.alliance.magic.vo.map.MapActiveInstance;
import sacred.alliance.magic.vo.map.MapAngelChestInstance;
import sacred.alliance.magic.vo.map.MapArenaTopInstance;

import com.game.draco.GameContext;

public class MapDefaultContainer extends MapContainer<MapDefaultInstance> {

	@Override
	public boolean canDestroy() {
		return false;
	}

	@Override
	public void destroy() {
		
	}

	private MapInstance createMapInstance(sacred.alliance.magic.app.map.Map map, boolean loadNpc) {
		MapInstance instance = subMapList.get(map.getMapId());
		if(instance != null){
			return instance;
		}
		synchronized(this){
			instance = subMapList.get(map.getMapId());
			if(instance != null){
				return instance;
			}
			instance = this.newInstance(map);
			instance.initNpc(loadNpc);
			addMapInstance(instance);
			GameContext.getMapApp().addMapInstance(instance);
			return instance;
		}
	}
	
	private MapInstance newInstance(Map map){
		byte logicType = map.getMapConfig().getLogictype();
		if(MapLogicType.siege.getType() == logicType){
			return new MapSiegeInstance(map, -1);
		}
		if(MapLogicType.dps.getType() == logicType){
			return new MapDpsInstance(map, -1);
		}
		if(MapLogicType.activeMap.getType() == logicType){
			return new MapActiveInstance(map,-1);
		}
		if(MapLogicType.angelChest.getType() == logicType){
			return new MapAngelChestInstance(map,-1);
		}
		if(MapLogicType.arenaTop.getType() == logicType){
			return new MapArenaTopInstance(map,-1);
		}
		return new MapDefaultInstance(map);
	}

	@Override
	public MapInstance createMapInstance(Map map, AbstractRole role) {
		return createMapInstance(map,true);
	}

	@Override
	public void update() {
		
	}

}
