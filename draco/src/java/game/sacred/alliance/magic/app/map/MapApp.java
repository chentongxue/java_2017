package sacred.alliance.magic.app.map;

import java.util.Collection;
import java.util.List;

import sacred.alliance.magic.app.fall.BoxEntry;
import sacred.alliance.magic.app.map.data.MapConfig;
import sacred.alliance.magic.app.map.data.MapRoadVO;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.module.cache.Cache;
import sacred.alliance.magic.vo.MapCopyContainer;
import sacred.alliance.magic.vo.MapDefaultContainer;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.MapLineContainer;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;
import sacred.alliance.magic.vo.map.MapRoleBornGuideContainer;

import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.message.item.MonsterItem;
import com.game.draco.message.item.NpcItem;

public abstract class MapApp implements Service {
	
	public abstract void addMapInfo(String mapId, Map map);
	
	public abstract MapConfig getMapConfig(String mapId);
	
	public abstract void clearMapConfig();
	
	public abstract void addNpcBorn(String npcTemplateId,Point point);
	
	public abstract boolean isCompressMapData();
	
	public abstract Map getMap(String mapId);

    public abstract Collection<Map> getAllMap() ;

    public abstract MapDataInfo getMapData(String key) ;
    
    public abstract void addMapData(String key,MapDataInfo dataInfo);
    
    public abstract MapRoadVO getMapRoadVO(String key);
    
    public abstract void addMapRoadVO(String key, MapRoadVO mapRoadVO);
    
    public abstract void addMapInstance(MapInstance mapInstance);
	
	public abstract MapInstance getMapInstance(String instanceId);
	
	public abstract MapInstance getMapInstanceByMapId(String mapId);
	
	public abstract MapDefaultContainer getDefaultMapContainer();
	
	public abstract MapRoleBornGuideContainer getMapRoleBornGuideContainer() ;
	
	public abstract MapCopyContainer getCopyContainer(String containerId);
	
	public abstract void addCopyContainer(MapCopyContainer container);
	
	public abstract void removeMapInstance(String instanceId);
	
	public abstract void removeCopyContainer(String instanceId);
	
	//public abstract MapLogic getMapLogic(String mapId,MapConfig mapConfig);
	
	
	/**
	 * 先生成mapLogic，再生成map
	 * 如果要修改mapLogic,就需要重新reload map
	 */
	
	
	/**
	 * 获得系统中当前所有的MapInstance
	 */
	public abstract Collection<MapInstance> getAllMapInstance();

    public abstract Collection<MapInstance> getAllMapInstance(String mapId);
    
    
    
    public abstract java.util.Map<String, MapCopyContainer> getCopyContainerMap();
    
	/*public static void registerMapLogic(MapLogic mapLogic){
		if(null == mapLogic){
			return ;
		}
		mapLogicMap.put(mapLogic.getMapId(), mapLogic);
	}*/
	
	/**
	 * 获得NPC所有的出生点(出生区域的中心点)
	 * @param npcId
	 * @return
	 */
	public abstract List<Point> whereNpcBorn(String templateId);
	
	/**
	 * 获得采集点的位置
	 * @param templateId
	 * @return
	 */
	//public abstract List<Point> whereCollectPoint(String templateId);
	
	/**
	 * 根据NPC实例ID获得npc位置信息
	 * @param npcInstanceId
	 * @return
	 */
	public abstract Point whereNpc(String npcInstanceId);
	
	/**
	 * 获得npc实例
	 * @param npcInstanceId
	 * @return
	 */
	public abstract NpcInstance getNpcInstance(String npcInstanceId);
	
	
	public abstract Cache<String, BoxEntry> getBoxesCache();
	
	
	public abstract MapLineContainer getMapLineContainer(String mapId);
	public abstract void removeMapLineContainer(String mapId);
	public abstract java.util.Map<String, MapLineContainer> getLineContainerMap();
	
	/** 区域地图Npc详情
	 * @param npcItems:Npc详情
	 *        monsterItems:怪物详情
	 ***/
	public abstract void buildWorldMapNpcItems(RoleInstance role, String mapId
										,List<NpcItem> npcItems, List<MonsterItem> monsterItems);
	
	public abstract byte[] getMapJumpPointData() ;
	
	public abstract Message getMapJumpPointDataMessage() ;
	
	public abstract boolean canMapProperty(RoleInstance role,int mapProperty) ;
	
	public abstract MapInstance getExistMapInstance(String mapId,int lineId) ;
}
