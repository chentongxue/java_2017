package sacred.alliance.magic.vo;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;

import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.map.MapGoblinInstance;


public class MapGoblinContainer extends MapContainer<MapGoblinInstance>{
	private static final Logger logger = LoggerFactory.getLogger(MapGoblinContainer.class);
	private byte[] createMapLock = new byte[0];//创建地图实例锁

	@Override
	public boolean canDestroy() {
		return false;
	}

	@Override
	public MapInstance createMapInstance(Map map, AbstractRole role) {
		String pointKey = GameContext.getGoblinApp().getRoleSecretPointKey(role.getRoleId());
		if (Util.isEmpty(pointKey)) {
			return null;
		}
		synchronized(this.createMapLock) {
			MapGoblinInstance mapInstance = (MapGoblinInstance) this.subMapList.get(pointKey);
			if (null !=  mapInstance) {
				return mapInstance;
			}
			mapInstance = new MapGoblinInstance(map);
			mapInstance.setInstanceId(pointKey);
			mapInstance.setGoblinSecretConfig(GameContext.getGoblinApp().getGoblinSecretConfig(new Date()));
			GameContext.getMapApp().addMapInstance(mapInstance);// 加入主循环
			this.addMapInstance(mapInstance);// 放到容器中
			mapInstance.initNpc(true);
			return mapInstance;
		}
	}
	
	@Override
	protected void addMapInstance(MapInstance mapInstance){
		subMapList.put(mapInstance.getInstanceId(), mapInstance);
		mapInstance.setMapContainer(this);
	}

	@Override
	public void destroy() {
		try {
			if (Util.isEmpty(this.subMapList)) {
				return;
			}
			for (MapInstance mapInstance : this.subMapList.values()) {
				if (null == mapInstance) {
					continue;
				}
				mapInstance.destroy();
			}
			// 容错
			this.subMapList.clear();
		} catch (Exception e) {
			logger.error("MapGoblinContainer.destroy mapInstance destroy error!", e);
		}
	}
	
	/**
	 * 从subMapList中删除地图实例
	 * @param mapId
	 */
	public void destroySignMap(String mapId) {
		try {
			this.subMapList.remove(mapId);
		} catch (Exception e) {
			logger.error("MapGoblinContainer.destroySignMap mapInstance destroy error!", e);
		}
	}

	@Override
	public void update() {
	}
	
}
