package sacred.alliance.magic.vo.map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.arena.ApplyInfo;
import sacred.alliance.magic.app.arena.ArenaMatch;
import sacred.alliance.magic.app.arena.ArenaType;
import sacred.alliance.magic.app.arena.config.ArenaConfig;
import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.app.map.MapApp;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapContainer;
import sacred.alliance.magic.vo.MapCopyContainer;
import sacred.alliance.magic.vo.MapInstance;

import com.game.draco.GameContext;

/**
 * 擂台赛地图容器
 *
 */
public class MapArenaContainer extends MapCopyContainer {
	private final static Logger logger = LoggerFactory.getLogger(MapArenaContainer.class);
	private final static String PRE_FIX = "arena_" ;
	private MapInstance mapInstance;
	private ArenaMatch match;

	@Override
	protected String getNamePrefix(){
		return PRE_FIX ;
	}
	
	public MapArenaContainer(Map map,ArenaMatch match) {
		super();
		this.match = match;
		//下面语句很重要,不要遗忘
		this.match.setContainerId(this.getInstanceId());
	}

	public static MapContainer getMapContainer(AbstractRole role,String mapId) {
		try {
			if (role == null) {
				return null;
			}
			// 获取此队伍中地图容器ID 
			ApplyInfo info = GameContext.getArenaApp().getApplyInfo(role.getRoleId());
			ArenaMatch match = info.getMatch();
			String containerID = match.getContainerId();
			MapContainer mapContainer = null ;
			if(!Util.isEmpty(containerID)){
				 //因为ArenaMatch的containerID是在创建MapArenaContainer时候赋值的
				 mapContainer = getMapApp().getCopyContainerMap().get(containerID);
			}
			if(null == mapContainer){
				synchronized(match){
					//containerID需要重新设置值,因为ArenaMatch的containerID是在创建MapArenaContainer时候赋值的
					containerID = match.getContainerId();
					if(!Util.isEmpty(containerID)){
						 mapContainer = getMapApp().getCopyContainerMap().get(containerID);
					}
					if(null != mapContainer){
						return mapContainer ;
					}
					//创建
					MapCopyContainer mc = new MapArenaContainer(getMapApp().getMap(mapId),match);
					getMapApp().addCopyContainer(mc);
					mapContainer = mc ;
					//地图实例创建成功 ArenaMatch的销毁可以依靠地图的destory方法
					//为了性能这里先删除
					GameContext.getArenaApp().removeArenaMatch(match.getKey());
				}
			}
			return mapContainer;
		} catch (Exception e) {
			logger.error("",e);
		}
		return null;
		
	}
	
	
	private static MapApp getMapApp(){
		return GameContext.getMapApp();
	}

	@Override
	public MapInstance createMapInstance(Map map, AbstractRole role) {
		if (null != mapInstance) {
			return mapInstance;
		}
		synchronized (this) {
			if (null != mapInstance) {
				return mapInstance;
			}
			MapInstance mapInstance = this.mapInstanceFactory(map, match);
			mapInstance.initNpc(true);
			mapInstance.setMapContainer(this);
			addMapInstance(mapInstance);
			GameContext.getMapApp().addMapInstance(mapInstance);
			this.mapInstance = mapInstance ;
			return mapInstance;
		}
	}
	
	private MapInstance mapInstanceFactory(Map map, ArenaMatch match){
		ArenaConfig config = match.getConfig();
		ArenaType arenaType = ArenaType.get(config.getArenaType());
		if(arenaType == ArenaType._LEARN){
			return new MapArenaLearnInstance(map, match);
		}
		if(arenaType == ArenaType._3V3){
			return new MapArena3V3Instance(map, match);
		}
		return new MapArenaInstance(map, match);
	}
	
	
	@Override
	public boolean canDestroy() {
		return mapInstance.canDestroy();
	}
	
	@Override
	public void destroy(){
		super.destroy();
		this.mapInstance = null ;
		//导致有时候无法取消的问题就是没有下面语句?
		if(null != match){
			match.cancelAll();
		}
		
	}

	public ArenaMatch getMatch() {
		return match;
	}

	public void setMatch(ArenaMatch match) {
		this.match = match;
	}

	
}
