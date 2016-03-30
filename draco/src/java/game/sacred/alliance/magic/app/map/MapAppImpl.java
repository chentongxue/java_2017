package sacred.alliance.magic.app.map;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;

import sacred.alliance.magic.app.fall.BoxEntry;
import sacred.alliance.magic.app.map.data.MapBasicConfig;
import sacred.alliance.magic.app.map.data.MapConfig;
import sacred.alliance.magic.app.map.data.MapRoadVO;
import sacred.alliance.magic.app.map.point.JumpMapPoint;
import sacred.alliance.magic.app.map.point.JumpMapPointCollection;
import sacred.alliance.magic.app.map.xml.data.MapConfigSaxReader;
import sacred.alliance.magic.base.MapLogicType;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.MapConstant;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.domain.MapLine;
import sacred.alliance.magic.module.cache.Cache;
import sacred.alliance.magic.module.cache.CacheEvent;
import sacred.alliance.magic.module.cache.CacheListener;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.ParallelRun;
import sacred.alliance.magic.util.StringUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.MapCopyContainer;
import sacred.alliance.magic.vo.MapDefaultContainer;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.MapLineContainer;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;
import sacred.alliance.magic.vo.map.MapRoleBornGuideContainer;

import com.game.draco.GameContext;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.npc.domain.NpcTemplate;
import com.game.draco.app.npc.type.NpcType;
import com.game.draco.app.quest.base.NpcQuestHeadSign;
import com.game.draco.message.item.MapJumpPointInfoItem;
import com.game.draco.message.item.MapJumpToPointItem;
import com.game.draco.message.item.MonsterItem;
import com.game.draco.message.item.NpcItem;
import com.game.draco.message.response.C0207_MapJumpPointInfoRespMessage;

public class MapAppImpl extends MapApp {
	//private final static Logger logger = LoggerFactory.getLogger(MapAppImpl.class);
	private final Logger loadLogger = Log4jManager.CHECK ;
	
	java.util.Map<String, MapInstance> mapInstanceMap = new ConcurrentHashMap<String, MapInstance>();
	java.util.Map<String, MapCopyContainer> copyContainerMap = new ConcurrentHashMap<String, MapCopyContainer>();

	//Key=地图ID
	java.util.Map<String, MapLineContainer> lineContainerMap = new ConcurrentHashMap<String, MapLineContainer>();
	
	MapDefaultContainer defaultMapContainer = new MapDefaultContainer();
	
	MapRoleBornGuideContainer roleBornGuideMapContainer = new MapRoleBornGuideContainer();
	
	java.util.Map<String, Map> mapInfos = new HashMap<String, Map>();
	
	/** NPC出生点信息 */
	private java.util.Map<String, List<Point>> whereNpcBornMap = new HashMap<String, List<Point>>();
	
	/** cache 地图数据 */
	private java.util.Map<String, MapDataInfo> mapDataMap = new HashMap<String, MapDataInfo>();
	private java.util.Map<String, MapRoadVO> mapRoadVOMap = new HashMap<String, MapRoadVO>();
	
	/** 宝箱掉落物品cache */
	private Cache<String, BoxEntry> boxesCache;
	private boolean compressMapData = true; // 是否压缩地图数据,默认压缩

	private java.util.Map<String, MapConfig> allMapConfigs = new HashMap<String, MapConfig>();
	
	//存储所有非副本地图的跳转点信息
	private byte[] mapJumpPointData = null ;
	
	private C0207_MapJumpPointInfoRespMessage mapJumpPointDataMessage = null ;
	
	private byte[] lock = new byte[0];
	
	private void initCache() {
		// 宝箱cache
		this.boxesCache.addCacheListener(new CacheListener<String, BoxEntry>() {
			@Override
			public void entryAccessed(CacheEvent<String, BoxEntry> event) {
			}
			@Override
			public void entryAdded(CacheEvent<String, BoxEntry> event) {
			}
			@Override
			public void entryCleared(CacheEvent<String, BoxEntry> event) {
			}
			@Override
			public void entryExpired(CacheEvent<String, BoxEntry> event) {
			}
			@Override
			public void entryRemoved(CacheEvent<String, BoxEntry> event) {
				// 删除roleBoxMapping中记录
				BoxEntry entry = event.getValue();
				if (null == entry) {
					return;
				}
				entry.destory();
			}
			@Override
			public void entryUpdated(CacheEvent<String, BoxEntry> event) {
			}
		});
	}

	private void init() {
		if(!this.loadMapConfig()){
			Log4jManager.checkFail() ;
		}
		//加载小地图资源
		this.loadMapBasicConfig();
		//this.initVersion();
		
		try {
			// 加载地图配置
			int nThreads = GameContext.getThreadsPools();
			
			List<Runnable> runList = new ArrayList<Runnable>();
			java.util.Map<String, List<File>> allMaps = this.loadAllMapsFile();
			
			for (List<File> gateMaps : allMaps.values()) {
				for(final File mapFile:gateMaps){
					Runnable rn = new Runnable(){
							public void run(){
								MapBuilder mb = new MapBuilder();
								mb.buildMap(mapFile);
							}};
					runList.add(rn);
				}
			}
			ParallelRun.execute(nThreads,runList);
			//加载分线地图配置
			this.loadMapLineConfig();
		} catch (Exception e) {
			Log4jManager.CHECK.error("load map error " , e);
			Log4jManager.checkFail();
		}
	}

	
	/** 获得当前所有地图文件夹 */
	private java.util.Map<String, List<File>> loadAllMapsFile() {
		java.util.Map<String, List<File>> allMaps = new HashMap<String, List<File>>();
		String mapDataPath = GameContext.getPathConfig().getMapDataPath();
		File dir = new File(mapDataPath);
		File[] files = dir.listFiles();
		if (files == null) {
			return allMaps;
		}
		for (File parent : files) {
			if (parent.getName().indexOf(".") >= 0 || !parent.isDirectory()) {
				// svn
				continue;
			}
			File[] maps = parent.listFiles();
			if (null == maps) {
				continue;
			}
			String gateId = parent.getName();
			for (File currentMap : maps) {
				String mapName = currentMap.getName();
				if (mapName.indexOf(".") >= 0 || !currentMap.isDirectory()) {
					// svn
					continue;
				}
				if (!allMaps.containsKey(gateId)) {
					allMaps.put(gateId, new ArrayList<File>());
				}
				allMaps.get(gateId).add(currentMap);
			}
		}
		return allMaps;
	}

	/*private void doCollectPoint(CollectPointConfig config, String mapId) {
		if (null == config || null == config.getNodes()) {
			return;
		}
		List<PointNode> nodeList = config.getNodes();
		for (PointNode node : nodeList) {
			if (null == node || null == node.getPoint()
					|| Util.isEmpty(node.getId())) {
				continue;
			}
			String key = node.getId();
			if (!this.whereCollectPointMap.containsKey(key)) {
				this.whereCollectPointMap.put(key, new ArrayList<Point>());
			}
			for (Point p : node.getPoint()) {
				Point point = new Point(mapId, p.getX(), p.getY());
				this.whereCollectPointMap.get(key).add(point);
			}
		}
	}*/

	public Map getMap(String mapId) {
		if(Util.isEmpty(mapId)){
			return null ;
		}
		return mapInfos.get(mapId);
	}

	public Collection<Map> getAllMap() {
		return mapInfos.values();
	}

	public MapInstance getMapInstance(String instanceId) {
		return mapInstanceMap.get(instanceId);
	}

	
	@Override
	public void buildWorldMapNpcItems(RoleInstance role, String mapId
										,List<NpcItem> npcItems, List<MonsterItem> monsterItems){
		java.util.Map<String,Point> npcBornPointMap = getMapNpcBornPointMap(mapId);
		if(null == npcBornPointMap){
			return;
		}
		//获取此地图所有npcId
		for(Iterator<java.util.Map.Entry<String, Point>> it = npcBornPointMap.entrySet().iterator();it.hasNext();){
			java.util.Map.Entry<String, Point> entry = it.next() ;
			String npcId = entry.getKey() ;
			NpcTemplate npcTemp = GameContext.getNpcApp().getNpcTemplate(npcId);
			if(null == npcTemp){
				continue;
			}
			Point point = entry.getValue() ;
			int npcType = npcTemp.getNpctype();
			if(NpcType.npc.getType() == npcType){
				NpcItem item = new NpcItem();
				item.setNpcFunction(npcTemp.getFunction());
				item.setNpcId(npcId);
				item.setNpcName(npcTemp.getNpcname());
				item.setNpcX((short)point.getX());
				item.setNpcY((short)point.getY());
				item.setQuestStatus(GameContext.getUserQuestApp()
						.getNpcQuestHeadSign(role, npcTemp));
				item.setNpcDesc(npcTemp.getDesc());
				npcItems.add(item);
			}else if(NpcType.monster.getType() == npcType){
				MonsterItem item = new MonsterItem();
				item.setNpcId(npcId);
				item.setNpcName(npcTemp.getNpcname());
				item.setNpcX((short)point.getX());
				item.setNpcY((short)point.getY());
				item.setNpcLevel((byte)npcTemp.getLevel());
				monsterItems.add(item);
			}
		}
		sortNpcItem(npcItems);
		sortMonsterItem(monsterItems);
	}
	
	/**
	 * NpcItem排序
	   1.任务相关排前(可交>可接>已接未完成)
	   2.功能性npc排前面
	 */
	private void sortNpcItem(List<NpcItem> list){
		Collections.sort(list, new Comparator<NpcItem>(){
			public int compare(NpcItem item1, NpcItem item2) {
				byte it1QuestStatus = item1.getQuestStatus();
				byte it2QuestStatus = item2.getQuestStatus();
				String it1Function = item1.getNpcFunction();
				String it2Function = item2.getNpcFunction();
				if((NpcQuestHeadSign.None.getType() != it1QuestStatus
						&& NpcQuestHeadSign.None.getType() == it2QuestStatus)//item1有任务 item2无任务
						|| (NpcQuestHeadSign.Submit.getType() == it1QuestStatus
								&& NpcQuestHeadSign.Submit.getType() != it2QuestStatus)//item1可交 item2可接||已接未完成
						|| (NpcQuestHeadSign.Accept.getType() == it1QuestStatus
								&& NpcQuestHeadSign.notComplete.getType() == it2QuestStatus)){//item1可接 item2已接未完成
					return -1;
				}
				if((NpcQuestHeadSign.None.getType() == it1QuestStatus
						&& NpcQuestHeadSign.None.getType() != it2QuestStatus)//item1无任务 item2有任务
						|| (NpcQuestHeadSign.Submit.getType() == it2QuestStatus
								&& NpcQuestHeadSign.Submit.getType() != it1QuestStatus)//item2可交 item1可接||已接未完成
						|| (NpcQuestHeadSign.Accept.getType() == it2QuestStatus
								&& NpcQuestHeadSign.notComplete.getType() == it1QuestStatus)){//item2可接 item1已接未完成
					return 1;
				}
				//功能性npc排前面
				if(StringUtil.nullOrEmpty(it1Function)
						&& StringUtil.nullOrEmpty(it2Function)){
					return 0;
				}
				return StringUtil.nullOrEmpty(it1Function) == true?1:-1;
			}			
		});
	}
	
	/**
	 * MonsterItem排序
	   1.低等级排前
	 */
	private void sortMonsterItem(List<MonsterItem> list){
		Collections.sort(list, new Comparator<MonsterItem>(){
			@Override
			public int compare(MonsterItem item1, MonsterItem item2) {
				byte it1Level = item1.getNpcLevel();
				byte it2Level = item2.getNpcLevel();
				return it1Level < it2Level?-1:(it1Level > it2Level?1:0);
			}		
		});
	}
	
	/** 本张地图所有NPC的出生点 **/
	private java.util.Map<String,Point> getMapNpcBornPointMap(String mapId){
		Map map = getMap(mapId);
		if(null == map){
			return null;
		}
		return map.getBornNpcMap() ;
	}
	
	private void loadMapBasicConfig(){
		String sourceFile = GameContext.getPathConfig().getXlsPath()+ XlsSheetNameType.map_basic_config.getXlsName();
		String sheetName = XlsSheetNameType.map_basic_config.getSheetName();
		java.util.Map<String,MapBasicConfig> configMap = XlsPojoUtil.sheetToMap(sourceFile, sheetName, MapBasicConfig.class);
		
		Set<String> allMapIdSet = new HashSet<String>();
		allMapIdSet.addAll(this.allMapConfigs.keySet());
		for(MapBasicConfig config : configMap.values()){
			MapConfig mapConfig = this.getMapConfig(config.getMapId());
			if(null == mapConfig){
				Log4jManager.CHECK.error("the mapId=" + config.getMapId() + ",it's MapConfig is not exist");
				Log4jManager.checkFail();
				continue ;
			}
			/* TODO:判断的太早，部分地图类型还没有修改呢（代码中修改的）
			//普通地图不能配置出口
			if(0 != config.getShowExit() && MapLogicType.defaultLogic == mapConfig.getMapLogicType()){
				Log4jManager.CHECK.error("the mapId=" + config.getMapId() + ",the defultLogic map can't config showExit!");
				Log4jManager.checkFail();
				continue ;
			}*/
			//将配置的相关信息放入MapConfig
			
			mapConfig.setMapdisplayname(config.getMapName());
			org.springframework.beans.BeanUtils.copyProperties(config, mapConfig);
			
			/*mapConfig.setSmallMapResId((short)config.getSmallMapResId());
			mapConfig.setMinTransLevel(config.getMinTransLevel());
			mapConfig.setMaxTransLevel(config.getMaxTransLevel());
			mapConfig.setMapweather(config.getWeather());
			mapConfig.setWeatherTimes(config.getWeatherTimes());
			mapConfig.setShowExit(config.getShowExit());
			mapConfig.setNpcPK(config.getNpcPK());
			mapConfig.setBroadcastAllMax(config.getBroadcastAllMax());
			mapConfig.setRoleCanPK(config.getRoleCanPK());
			mapConfig.setMapdisplayname(config.getMapName());
			mapConfig.setSwitchHero(config.getSwitchHero());
			mapConfig.setUseFood(config.getUseFood());
			mapConfig.setHpHealth(config.getHpHealth());
			mapConfig.setCanChange3Hero(config.getCanChange3Hero());
			mapConfig.setCanHook(config.getCanHook());*/
			allMapIdSet.remove(config.getMapId());
			
		}
		if( 0 == allMapIdSet.size()){
			return ;
		}
		for(String mapId : allMapIdSet){
			Log4jManager.CHECK.error("the mapId=" + mapId + ",not config map basic config,config file: " 
					+  XlsSheetNameType.map_basic_config.getXlsName());
			Log4jManager.checkFail();
		}
	}
	
	/** 加载分线地图 **/
	private void loadMapLineConfig(){
		String sourceFile = GameContext.getPathConfig().getXlsPath()+ XlsSheetNameType.map_line.getXlsName();
		String sheetName = XlsSheetNameType.map_line.getSheetName();
		List<MapLine> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, MapLine.class);
		if(Util.isEmpty(list)){
			return ;
		}
		for(MapLine mapLine : list){
			Map map = this.mapInfos.get(mapLine.getLineMapId());
			if(map == null){
				continue;
			}
			MapConfig mapConfig = map.getMapConfig();
			mapConfig.setHadLineMap(true);
			mapConfig.setMaxRoleCount(mapLine.getMaxRoleCount());
			
			int maxLineCount = mapLine.getMaxLineCount();
			if(maxLineCount <= 0){
				maxLineCount = Integer.MAX_VALUE;
			}
			mapConfig.setMaxLineCount(maxLineCount);
			mapConfig.setLifeCycle(mapLine.getLifeCycle());
			mapConfig.setChangeLine(mapLine.getChangeLine());
		}
	}
	
	
	
	@Override
	public MapLineContainer getMapLineContainer(String mapId){
		if(Util.isEmpty(mapId)){
			throw new java.lang.NullPointerException("mapId is null");
		}
		MapLineContainer container = this.lineContainerMap.get(mapId);
		if(container == null){
			return this.createMapLineContainer(mapId);
		}
		return container;
	}
	
	private MapLineContainer createMapLineContainer(String mapId){
		synchronized(lock){
			MapLineContainer container = this.lineContainerMap.get(mapId);
			if(container == null){
				MapLineContainer lineContainer = new MapLineContainer();
				this.lineContainerMap.put(mapId, lineContainer);
				return lineContainer;
			}
			return container;
		}
	}
	
	@Override
	public void removeMapLineContainer(String mapId){
		if(Util.isEmpty(mapId)){
			//throw new java.lang.NullPointerException("mapId is null");
			return ;
		}
		this.lineContainerMap.remove(mapId);
	}
	
	/**
	 * 此方法不支持副本 参考: mapInstance Id生成规则
	 */
	public MapInstance getMapInstanceByMapId(String mapId) {

		return mapInstanceMap.get(mapId);
	}

	public void addMapInstance(MapInstance mapInstance) {
		mapInstanceMap.put(mapInstance.getInstanceId(), mapInstance);
	}

	public void removeMapInstance(String instanceId) {
		mapInstanceMap.remove(instanceId);
	}

	@Override
	public Collection<MapInstance> getAllMapInstance() {
		return mapInstanceMap.values();
	}

	@Override
	public Collection<MapInstance> getAllMapInstance(String mapId) {
		List<MapInstance> list = new ArrayList<MapInstance>();
		if (null == mapId || 0 == mapId.trim().length()) {
			return list;
		}
		for (MapInstance it : mapInstanceMap.values()) {
			if (it.getMap().getMapId().equals(mapId)) {
				list.add(it);
			}
		}
		return list;
	}
	

	@Override
	public void start() {
		init();
		initCache();
		//构建地图跳转点信息
		try {
			initMapJumpPointInfo();
		} catch (Exception e) {
			Log4jManager.CHECK.error("initMapJumpPointInfo error");
			Log4jManager.checkFail();
		}
	}
	
	private void initMapJumpPointInfo() throws Exception {
		mapJumpPointDataMessage = new C0207_MapJumpPointInfoRespMessage();
		List<MapJumpPointInfoItem> pointItems = new ArrayList<MapJumpPointInfoItem>();
		mapJumpPointDataMessage.setPointItems(pointItems);
		for(Map map : this.mapInfos.values()){
			/*if(map.getMapConfig().iscopymode()){
				//副本模式的地图不参入寻路
				continue ;
			}*/
			JumpMapPointCollection coll = map.getJumpMapPointCollection();
			if(null == coll){
				continue ;
			}
			List<JumpMapPoint> points = coll.getPoint();
			if(Util.isEmpty(points)){
				continue ;
			}
			List<MapJumpToPointItem> toPointList = new ArrayList<MapJumpToPointItem>();
			for(JumpMapPoint point : points){
				short x = (short)point.getX();
				short y = (short)point.getY();
				//卡死复位点在跳转点之上，服务器不能启动
				if(Util.inCircle(x, y, map.getMapConfig().getMaporiginx(), 
						map.getMapConfig().getMaporiginy(), MapConstant.JUMP_POINT_EFFECT_RADIOS)){
					Log4jManager.CHECK.error("map config error: originPoint nearby jumpPoint. mapId=" + map.getMapId() + ",JumpMapPoint=("+x+","+y+")");
					Log4jManager.checkFail();
				}
				MapJumpToPointItem item = new MapJumpToPointItem();
				item.setX(x);
				item.setY(y);
				item.setToMapId(point.getTomapid());
				toPointList.add(item);
			}
			
			MapJumpPointInfoItem infoItem = new MapJumpPointInfoItem();
			infoItem.setMapId(map.getMapId());
			infoItem.setToPointList(toPointList);
			pointItems.add(infoItem);
		}
		
		//将message转化成byte[]存储
		IoBuffer buffer = GameContext.getIoBufferMessageParser().fromMessage(
				mapJumpPointDataMessage);
		// 需要压缩的数据
		byte[] data = new byte[buffer.remaining()];
		buffer.get(data);
		buffer.clear();
		buffer = null;
		this.mapJumpPointData = data ;
	}
	
	@Override
	public void stop() {
	}

	@Override
	public List<Point> whereNpcBorn(String templateId) {
		return whereNpcBornMap.get(templateId);
	}

	/*@Override
	public List<Point> whereCollectPoint(String templateId) {
		return whereCollectPointMap.get(templateId);
	}*/

	@Override
	public Point whereNpc(String npcInstanceId) {
		NpcInstance npc = this.getNpcInstance(npcInstanceId);
		if (null == npc) {
			return null;
		}
		return npc.getCurrentPoint();
	}

	@Override
	public NpcInstance getNpcInstance(String npcInstanceId) {
		// TODO 需要修改,这样效率太低
		if (Util.isEmpty(npcInstanceId)) {
			return null;
		}
		for (MapInstance instance : this.getAllMapInstance()) {
			NpcInstance npc = instance.getNpcInstance(npcInstanceId);
			if (null != npc) {
				return npc;
			}
		}
		return null;
	}

	@Override
	public MapDataInfo getMapData(String key) {
		return this.mapDataMap.get(key);
	}
	
	@Override
	public MapRoadVO getMapRoadVO(String key) {
		return this.mapRoadVOMap.get(key);
	}


	@Override
	public void setArgs(Object args) {

	}

	@Override
	public void addMapData(String key, MapDataInfo dataInfo) {
		synchronized (mapDataMap) {
			this.mapDataMap.put(key, dataInfo);
		}
	}
	
	@Override
	public void addMapRoadVO(String key, MapRoadVO mapRoadVO){
		synchronized(mapRoadVOMap) {
			this.mapRoadVOMap.put(key, mapRoadVO);
		}
	}

	/***加载地图配置数据*/
	private boolean loadMapConfig(){
		// 加载地图地图配置
		String mapDataPath = GameContext.getPathConfig().getMapDataPath();
		File dir = new File(mapDataPath);
		File[] files = dir.listFiles();
		if (files == null) {
			return false ;
		}
		boolean result = true ;
		// 读取路径下的所以地图数据
		for (File parent : files) {
			if (parent.getName().indexOf(".") >= 0 || !parent.isDirectory()) {
				continue;
			}
			File[] maps = parent.listFiles();
			if (null == maps) {
				continue;
			}
			for (File currentMap : maps) {
				String mapName = currentMap.getName();
				if (mapName.indexOf(".") >= 0 || !currentMap.isDirectory()) {
					continue;
				}
				String mapConfigFile = currentMap.getPath() + File.separator + "mapconfig.xml";
				MapConfig mapConfig = null;
				try{
					//mapConfig = XmlUtil.loadFromXml(mapConfigFile, MapConfig.class);
					MapConfigSaxReader reader = new MapConfigSaxReader();
					reader.parse(mapConfigFile);
					mapConfig = reader.getMapConfig();
				}catch(Exception e){
					result = false ;
					loadLogger.error("mapconfig.xml for " + mapName + " is not exists ");
					continue;
				}
				if (null == mapConfig) {
					result = false ;
					loadLogger.error("mapconfig.xml for " + mapName + " is not exists ");
					continue;
				}
				//判断卡死复位点
				if(mapConfig.getMaporiginx() <=0 
						|| mapConfig.getMaporiginy() <=0){
					//卡死复位点没有配置
					loadLogger.error("mapconfig not config the origin point,mapId=" + mapName);
					result = false ;
				}
				allMapConfigs.put(mapName, mapConfig);
			}
		}
		return result ;
	}


	public java.util.Map<String, MapCopyContainer> getCopyContainerMap() {
		return copyContainerMap;
	}
	
	@Override
	public MapCopyContainer getCopyContainer(String containerId){
		if(Util.isEmpty(containerId)){
			return null;
		}
		return this.copyContainerMap.get(containerId);
	}

	public void addCopyContainer(MapCopyContainer container) {
		this.copyContainerMap.put(container.getInstanceId(), container);
	}

	public void removeCopyContainer(String instanceId) {
		this.copyContainerMap.remove(instanceId);
	}
	
	@Override
	public MapDefaultContainer getDefaultMapContainer() {
		return defaultMapContainer;
	}
	
	public MapRoleBornGuideContainer getMapRoleBornGuideContainer() {
		return this.roleBornGuideMapContainer ;
	}
	
	
	public void setDefaultMapContainer(MapDefaultContainer defaultMapContainer) {
		this.defaultMapContainer = defaultMapContainer;
	}
	
	@Override
	public void addMapInfo(String mapId, Map map) {
		synchronized (mapInfos) {
			this.mapInfos.put(mapId, map);
		}

	}

	@Override
	public void clearMapConfig() {
		this.allMapConfigs.clear();
	}

	@Override
	public MapConfig getMapConfig(String mapId) {
		if(Util.isEmpty(mapId)){
			return null ;
		}
		return this.allMapConfigs.get(mapId);
	}

	@Override
	public void addNpcBorn(String npcTemplateId, Point point) {
		synchronized (whereNpcBornMap) {
			if (!this.whereNpcBornMap.containsKey(npcTemplateId)) {
				this.whereNpcBornMap.put(npcTemplateId, new ArrayList<Point>());
			}
			this.whereNpcBornMap.get(npcTemplateId).add(point);
		}
	}
	
	public Cache<String, BoxEntry> getBoxesCache() {
		return boxesCache;
	}

	public void setBoxesCache(Cache<String, BoxEntry> boxesCache) {
		this.boxesCache = boxesCache;
	}
	public boolean isCompressMapData() {
		return compressMapData;
	}
	public void setCompressMapData(boolean compressMapData) {
		this.compressMapData = compressMapData;
	}

	public java.util.Map<String, MapLineContainer> getLineContainerMap() {
		return lineContainerMap;
	}

	@Override
	public byte[] getMapJumpPointData() {
		return mapJumpPointData;
	}
	
	@Override
	public Message getMapJumpPointDataMessage() {
		return this.mapJumpPointDataMessage ;
	}
	
	@Override
	public boolean canMapProperty(RoleInstance role,int mapProperty){
		MapInstance mapInstance = role.getMapInstance() ;
		if(null == mapInstance){
			return false ;
		}
		MapConfig config = mapInstance.getMap().getMapConfig();
		if(null == config){
			return false ;
		}
		return 1 == config.getMapPropertyValue(mapProperty) ;
	}
	
	/**
	 * 只能获得分线地图和默认逻辑地图
	 */
	@Override
	public MapInstance getExistMapInstance(String mapId,int lineId){
		MapConfig mapConfig = this.getMapConfig(mapId);
		if(null == mapConfig){
			return null ;
		}
		if(mapConfig.isHadLineMap()){
			MapLineContainer container = this.lineContainerMap.get(mapId);
			if(null == container){
				return null ;
			}
			return container.getMapInstance(lineId);
		}
		if(mapConfig.getMapLogicType() == MapLogicType.defaultLogic){
			MapDefaultContainer container = GameContext.getMapApp().getDefaultMapContainer(); 
			return container.getMapInstance(mapId);
		}
		return null ;
	}
}

