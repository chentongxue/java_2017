package sacred.alliance.magic.vo.map;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.app.map.MapApp;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapContainer;
import sacred.alliance.magic.vo.MapCopyContainer;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.union.domain.Union;

public class MapUnionTerritoryContainer extends MapCopyContainer{
	private static MapApp mapApp = GameContext.getMapApp();
	private static final String ID = "union_";
	MapUnionTerritoryInstance unionTerritoryInstance;
	private Lock creatLock = new ReentrantLock();//防止创建同样的MapInstance
	
	public MapUnionTerritoryContainer(String unionId){
		this.instanceId = ID + unionId;
	}
	
	public static MapContainer getMapContainer(AbstractRole role) {
		String unionId = ((RoleInstance)role).getUnionId();
		
		Union union = GameContext.getUnionApp().getUnion(unionId);
		
		MapCopyContainer mapContainer = mapApp.getCopyContainer(ID + unionId);
		if(null == mapContainer){
			synchronized(union){
				mapContainer = mapApp.getCopyContainer(ID + unionId);
				if(null == mapContainer){
					mapContainer = new MapUnionTerritoryContainer(unionId);
					mapApp.addCopyContainer(mapContainer);
				}
			}
		}
		
		return mapContainer;
	}
	
	@Override
	public MapInstance createMapInstance(Map map, AbstractRole role) {
		if(null != this.unionTerritoryInstance){
			return this.unionTerritoryInstance;
		}
		
		try{
			creatLock.lock();
			if(null != this.unionTerritoryInstance){
				return this.unionTerritoryInstance;
			}
			unionTerritoryInstance = new MapUnionTerritoryInstance(map);
			unionTerritoryInstance.setInstanceId(this.instanceId);
			unionTerritoryInstance.initNpc(true);
			RoleInstance r = (RoleInstance)role;
			unionTerritoryInstance.setUnionId(r.getUnionId());
			addMapInstance(this.unionTerritoryInstance);
			mapApp.addMapInstance(this.unionTerritoryInstance);
			return this.unionTerritoryInstance;
		}finally{
			creatLock.unlock();
		}
	}
	
	public void update(){
		
	}
	
	@Override
	public boolean canDestroy() {
		if(this.unionTerritoryInstance == null){
			return false;
		}
		return this.unionTerritoryInstance.canDestroy();
		
	}
	
}
