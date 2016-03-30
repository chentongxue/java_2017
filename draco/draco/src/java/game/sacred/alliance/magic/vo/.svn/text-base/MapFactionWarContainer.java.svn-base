//package sacred.alliance.magic.vo;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.game.draco.GameContext;
//
//import sacred.alliance.magic.app.faction.war.FactionWarMatch;
//import sacred.alliance.magic.app.faction.war.domain.FactionWarInfo;
//import sacred.alliance.magic.app.map.Map;
//import sacred.alliance.magic.util.Util;
//
///**
// * 门派战地图容器
// *
// */
//public class MapFactionWarContainer extends MapCopyContainer {
//	private final static Logger logger = LoggerFactory.getLogger(MapFactionWarContainer.class);
//	private final static String PRE_FIX = "fac_war_" ;
//	private MapInstance mapInstance;
//	private FactionWarMatch match;
//
//	@Override
//	protected String getNamePrefix(){
//		return PRE_FIX ;
//	}
//	
//	public MapFactionWarContainer(Map map,FactionWarMatch match) {
//		super();
//		this.match = match;
//		//下面语句很重要,不要遗忘
//		this.match.setContainerId(this.getInstanceId());
//	}
//
//	public static MapContainer getMapContainer(AbstractRole role,String mapId) {
//		try {
//			if (role == null) {
//				return null;
//			}
//			String factionId = ((RoleInstance)role).getFactionId();
//			if(Util.isEmpty(factionId)){
//				return null;
//			}
//			// 获取此队伍中地图容器ID 
//			FactionWarInfo info = GameContext.getFactionWarApp().getFactionWarInfo(factionId);
//			FactionWarMatch match = info.getMatch();
//			String containerID = match.getContainerId();
//			MapContainer mapContainer = null ;
//			if(!Util.isEmpty(containerID)){
//				 //因为FactionWarFaction的containerID是在创建MapFactionWarContainer时候赋值的
//				 mapContainer = GameContext.getMapApp().getCopyContainerMap().get(containerID);
//			}
//			if(null == mapContainer){
//				synchronized(match){
//					//containerID需要重新设置值,因为FactionWarFaction的containerID是在创建MapFactionWarContainer时候赋值的
//					containerID = match.getContainerId();
//					if(!Util.isEmpty(containerID)){
//						 mapContainer = GameContext.getMapApp().getCopyContainerMap().get(containerID);
//					}
//					if(null != mapContainer){
//						return mapContainer ;
//					}
//					//创建
//					MapCopyContainer mc = new MapFactionWarContainer(GameContext.getMapApp().getMap(mapId),match);
//					GameContext.getMapApp().addCopyContainer(mc);
//					mapContainer = mc ;
//				}
//			}
//			return mapContainer;
//		} catch (Exception e) {
//			logger.error("",e);
//		}
//		return null;
//		
//	}
//	
//	
//	@Override
//	public MapInstance createMapInstance(Map map, AbstractRole role) {
//		if (null != mapInstance) {
//			return mapInstance;
//		}
//		synchronized (this) {
//			if (null != mapInstance) {
//				return mapInstance;
//			}
//			MapInstance mapInstance = new MapFactionWarInstance(map,match);
//			mapInstance.initNpc(true);
//			mapInstance.setMapContainer(this);
//			addMapInstance(mapInstance);
//			GameContext.getMapApp().addMapInstance(mapInstance);
//			this.mapInstance = mapInstance ;
//			return mapInstance;
//		}
//	}
//	
//	@Override
//	public boolean canDestroy() {
//		if(null == mapInstance){
//			return false;
//		}
//		return mapInstance.canDestroy();
//	}
//	
//	@Override
//	public void destroy(){
//		this.mapInstance.destroy();
//		super.destroy();
//	}
//}
