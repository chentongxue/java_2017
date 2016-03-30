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
import com.game.draco.app.unionbattle.domain.UnionIntegralState;

public class MapUnionIntegralBattleContainer extends MapCopyContainer{
	private static MapApp mapApp = GameContext.getMapApp();
	private static final String ID = "integral_battle_";
	private Lock creatLock = new ReentrantLock();//防止创建同样的MapInstance
	MapUnionIntegralBattleInstance integralBattleInstance;
	
	public MapUnionIntegralBattleContainer(String groupId){
		this.instanceId = ID + groupId;
	}
	
	public static MapContainer getMapContainer(AbstractRole role) {
		RoleInstance r = (RoleInstance)role;
		UnionIntegralState integralState = GameContext.getUnionIntegralBattleApp().getIntegralState(r);
		String groupId = String.valueOf(integralState.getGroupId());
		MapCopyContainer mapContainer = mapApp.getCopyContainer(ID + groupId);
		if(null == mapContainer){
			synchronized(integralState){
				mapContainer = mapApp.getCopyContainer(ID + groupId);
				if(null == mapContainer){
					mapContainer = new MapUnionIntegralBattleContainer(groupId);
					mapApp.addCopyContainer(mapContainer);
				}
			}
		}
		return mapContainer;
	}
	
	@Override
	public MapInstance createMapInstance(Map map, AbstractRole role) {
		
		RoleInstance r = (RoleInstance)role;
		
		if (integralBattleInstance != null) {
			return integralBattleInstance;
		}
		try {
			this.creatLock.lock();
			if(null != integralBattleInstance){
				return integralBattleInstance;
			}
			UnionIntegralState integralState = GameContext.getUnionIntegralBattleApp().getIntegralState(r);
			long overTime = GameContext.getUnionIntegralBattleApp().getOverTime();
			integralBattleInstance = new MapUnionIntegralBattleInstance(map);
			integralBattleInstance.setOverTime(overTime);
			integralBattleInstance.setInstanceId(this.instanceId);
			integralBattleInstance.setGroupId(integralState.getGroupId());
			integralBattleInstance.setRound(integralState.getRound());
			integralBattleInstance.initNpc(true);
			integralBattleInstance.initNpc();
			addMapInstance(integralBattleInstance);
			mapApp.addMapInstance(integralBattleInstance);
			return integralBattleInstance;
		} finally {
			creatLock.unlock();
		}
	}
	
	public void update(){
		
	}
	
	@Override
	public boolean canDestroy() {
		if(this.integralBattleInstance == null){
			return false;
		}
		return this.integralBattleInstance.canDestroy();
		
	}
	
}
