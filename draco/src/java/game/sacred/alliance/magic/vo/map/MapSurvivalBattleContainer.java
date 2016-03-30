package sacred.alliance.magic.vo.map;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.app.map.MapApp;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapContainer;
import sacred.alliance.magic.vo.MapCopyContainer;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.survival.config.SurvivalBase;
import com.game.draco.app.survival.vo.SurvivalApplyInfo;

public class MapSurvivalBattleContainer extends MapCopyContainer{
	private static MapApp mapApp = GameContext.getMapApp();
	private static final String ID = "survival_battle_";
	MapSurvivalBattleInstance survivalBattleInstance;
	private Lock creatLock = new ReentrantLock();//防止创建同样的MapInstance
	
	public MapSurvivalBattleContainer(long unixId){
		this.instanceId = ID + unixId;
	}
	
	public static MapContainer getMapContainer(AbstractRole role) {
		RoleInstance r = (RoleInstance)role;
		SurvivalApplyInfo info = GameContext.getSurvivalBattleApp().getSurvivalApplyInfo(r);
		if(info == null){
			return null;
		}
		MapCopyContainer mapContainer = mapApp.getCopyContainer(ID + info.getUnixId());
		if(null == mapContainer){
			synchronized(info){
				mapContainer = mapApp.getCopyContainer(ID + info.getUnixId());
				if(null == mapContainer){
					mapContainer = new MapSurvivalBattleContainer(info.getUnixId());
					mapApp.addCopyContainer(mapContainer);
				}
			}
		}
		return mapContainer;
	}
	
	@Override
	public MapInstance createMapInstance(Map map, AbstractRole role) {
		if(null != this.survivalBattleInstance){
			return this.survivalBattleInstance;
		}
		try {
			
			this.creatLock.lock();
			if(null != this.survivalBattleInstance){
				return this.survivalBattleInstance;
			}
			RoleInstance r = (RoleInstance)role;
			SurvivalApplyInfo info = GameContext.getSurvivalBattleApp().getSurvivalApplyInfo(r);
			SurvivalBase base = GameContext.getSurvivalApp().getSurvivalBase();
			Active active = GameContext.getActiveApp().getActive(base.getActiveId());
			survivalBattleInstance = new MapSurvivalBattleInstance(map);
			survivalBattleInstance.setSurvivalApplyInfo(info);
			survivalBattleInstance.setWaitTime(System.currentTimeMillis() + base.getWaitTime()*1000);
			survivalBattleInstance.setOverTime(active.getActiveEndTime().getTime());
			survivalBattleInstance.setInstanceId(this.instanceId);
			survivalBattleInstance.initNpc(true);
			addMapInstance(this.survivalBattleInstance);
			mapApp.addMapInstance(this.survivalBattleInstance);
			return this.survivalBattleInstance;
		} finally {
			creatLock.unlock();
		}
	}
	
	public void update(){
		
	}
	
	@Override
	public boolean canDestroy() {
		if(this.survivalBattleInstance == null){
			return false;
		}
		return this.survivalBattleInstance.canDestroy();
		
	}
	
}
