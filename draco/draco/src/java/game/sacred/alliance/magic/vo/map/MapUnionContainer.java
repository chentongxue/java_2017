package sacred.alliance.magic.vo.map;

import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.app.map.MapApp;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapContainer;
import sacred.alliance.magic.vo.MapCopyContainer;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.MapUnionInstance;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.union.domain.Union;

public class MapUnionContainer extends MapCopyContainer {
	
	private static MapApp mapApp = GameContext.getMapApp();
	private static final String ID = "union_";
	MapUnionInstance unionMapInstance;
	
	public MapUnionContainer(String unionId,short activityId){
		this.instanceId = ID + unionId + Cat.underline + activityId;
	}
	
	public static MapContainer getMapContainer(AbstractRole role, String mapId) {
		String unionId = ((RoleInstance)role).getUnionId();
		MapCopyContainer mapContainer = mapApp.getCopyContainerMap().get(ID + unionId + getCopyId(mapId));
		if(null != mapContainer){
			return mapContainer;
		}
		mapContainer = new MapUnionContainer(unionId,getCopyId(mapId));
		mapApp.addCopyContainer(mapContainer);
		
		return mapContainer;
	}
	
	private static short getCopyId(String mapId){
		Map map = mapApp.getMap(mapId);
		return map.getMapConfig().getCopyId();
	}
	
	@Override
	public MapInstance createMapInstance(Map map, AbstractRole role) {
		if(null != this.unionMapInstance){
			return this.unionMapInstance;
		}
		Union union = GameContext.getUnionApp().getUnion((RoleInstance)role);
		if(null != this.unionMapInstance){
			return this.unionMapInstance;
		}
		
		unionMapInstance = new MapUnionInstance(map,union.getUnionId());
		unionMapInstance.initNpc(true);
		addMapInstance(this.unionMapInstance);
		mapApp.addMapInstance(this.unionMapInstance);
		return this.unionMapInstance;
	}
	
	public void update(){
		
	}
	
	@Override
	public boolean canDestroy() {
		if(this.unionMapInstance == null){
			return false;
		}
		return this.unionMapInstance.canDestroy();
		
	}
	
}
