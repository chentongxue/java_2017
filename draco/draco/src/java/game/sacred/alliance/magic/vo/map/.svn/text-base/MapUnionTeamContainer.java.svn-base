package sacred.alliance.magic.vo.map;

import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.app.map.MapApp;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapContainer;
import sacred.alliance.magic.vo.MapCopyContainer;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.MapUnionTeamInstance;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;

public class MapUnionTeamContainer extends MapCopyContainer {
	
	private static MapApp mapApp = GameContext.getMapApp();
	private static final String ID = "union_team_";
	
	public MapUnionTeamContainer(String unionId,short activityId,int roleId){
		this.instanceId = ID + unionId + Cat.underline + activityId + roleId;
	}
	
	public static MapContainer getMapContainer(AbstractRole role, String mapId) {
		String unionId = ((RoleInstance)role).getUnionId();
		MapCopyContainer mapContainer = mapApp.getCopyContainerMap().get(ID + unionId + getCopyId(mapId));
		if(null != mapContainer){
			return mapContainer;
		}
		mapContainer = new MapUnionTeamContainer(unionId,getCopyId(mapId),role.getIntRoleId());
		mapApp.addCopyContainer(mapContainer);
		
		return mapContainer;
	}
	
	private static short getCopyId(String mapId){
		Map map = mapApp.getMap(mapId);
		return map.getMapConfig().getCopyId();
	}
	
	@Override
	public MapInstance createMapInstance(Map map, AbstractRole role) {
		RoleInstance r = (RoleInstance)role;
		MapInstance instance = new MapUnionTeamInstance(map,r.getUnionId());
		instance.initNpc(true);
		addMapInstance(instance);
		mapApp.addMapInstance(instance);
		return instance;
	}
	
	public void update(){
		
	}
	
}
