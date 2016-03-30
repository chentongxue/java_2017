package sacred.alliance.magic.app.map;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sacred.alliance.magic.app.map.data.MapCollideData;
import sacred.alliance.magic.app.map.data.MapConfig;
import sacred.alliance.magic.app.map.data.MapImageAssociate;
import sacred.alliance.magic.app.map.data.MapNpcBornData;
import sacred.alliance.magic.app.map.data.NpcBorn;
import sacred.alliance.magic.app.map.data.TilesetLayer;
import sacred.alliance.magic.app.map.logic.MapLogic;
import sacred.alliance.magic.app.map.point.CollectPointConfig;
import sacred.alliance.magic.app.map.point.JumpMapPoint;
import sacred.alliance.magic.app.map.point.JumpMapPointCollection;
import sacred.alliance.magic.app.map.xml.data.CollectPointConfigSaxReader;
import sacred.alliance.magic.app.map.xml.data.MapCollideDataReader;
import sacred.alliance.magic.app.map.xml.data.MapImageAssociateReader;
import sacred.alliance.magic.app.map.xml.data.MapJumpPointSaxReader;
import sacred.alliance.magic.app.map.xml.data.MapNpcBornDataSaxReader;
import sacred.alliance.magic.base.MapLogicType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.FileUtil;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XmlUtil;
import sacred.alliance.magic.vo.MapWay;
import sacred.alliance.magic.vo.Point;

import com.game.draco.GameContext;
import com.game.draco.app.npc.domain.NpcTemplate;
import com.game.draco.app.quest.Quest;
import com.game.draco.message.item.MapGetDataItem;
import com.game.draco.message.item.MapGetDataLayerItem;
import com.game.draco.message.item.MapJumpPointItem;

public class MapBuilder {
	//private final static Logger logger = LoggerFactory.getLogger(MapBuilder.class);
	private String gateName;
	private String mapPath;
	private String mapName;
	private String mapId;
	private MapCollideData mapCollideData;
	private JumpMapPointCollection jumpPointData;
	private CollectPointConfig questCollectPointConfig;
	private CollectPointConfig skillCollectPointConfig;
	private java.util.Map<String, MapWay> wayMap = new HashMap<String, MapWay>();
	private MapNpcBornData npcBornData = null;
	private Map map;
	private MapImageAssociate mapImageAssociate;
	private List<TilesetLayer> tilesetLayers = new ArrayList<TilesetLayer>();
	private int param;

	/**
	 * 构建地图
	 * 
	 * @param mapFile
	 */
	public void buildMap(File mapFile) {
		gateName = mapFile.getParentFile().getName();
		mapPath = mapFile.getPath();
		mapName = mapFile.getName();
		mapId = mapName;

		try {
			// 加载地图数据
			loadMapNpcBornData();
			loadMapCollideData();
			loadMapJumpPoint();
			
			loadQuestCollectPoint();
			
			//loadSkillCollectPoint();
			loadMapWay();
			
			loadMapLogic();
			
			build();
			
			compressMapData();
			
		} catch (Exception ex) {
			Log4jManager.CHECK.error(" buildMap error,mapName=" + mapName,ex);
			Log4jManager.checkFail();
		}
	}

	/*private byte[] compress(Message message) throws Exception {
		IoBuffer buffer = GameContext.getIoBufferMessageParser().fromMessage(
				message);
		// 需要压缩的数据
		byte[] data = new byte[buffer.remaining()];
		buffer.get(data);
		buffer.clear();
		buffer = null;
		return data;
	}*/
	
	private byte[] compressMapData(MapGetDataItem item) throws Exception{
		ByteArrayOutputStream byteOutputStream = null;
		DataOutputStream outStream = null;
		byte[] compressData = null;
		try{
			byteOutputStream = new java.io.ByteArrayOutputStream();
			outStream = new DataOutputStream(byteOutputStream);
			GameContext.getBytesProtoBuffer().pack(item, outStream);
			compressData = lzma.util.Util.lzmaZip(byteOutputStream.toByteArray());
		} finally {
			if( null != byteOutputStream){
				byteOutputStream.close();
			}
			if(null != outStream){
				outStream.close();
			}
		}
		return compressData;
	}

	private byte[] convertArr(byte[][] array, int length) {
		byte[] result = new byte[length];
		int byteIndex = 0;
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[0].length; j++) {
				result[byteIndex] = array[i][j];
				byteIndex++;
			}
		}
		return result;
	}

	private List<MapJumpPointItem> getJumpPoint(
			JumpMapPointCollection jumpPointData) {
		List<MapJumpPointItem> jumpItems = new ArrayList<MapJumpPointItem>();
		try {
			for (JumpMapPoint p : jumpPointData.getPoint()) {
				MapJumpPointItem item = new MapJumpPointItem();
				item.setX((short) p.getX());
				item.setY((short) p.getY());
				
				//!!!! 这里不能去Map因为目前有可能还没有构建放入
				//!!!! 应该获得mapconfig
				/*Map toMap = GameContext.getMapApplication().getMap(
						p.getTomapid());
				if (null != toMap) {
					item.setMapName(toMap.getMapConfig().getMapdisplayname());
				}*/
				boolean isSameMap = this.mapId.equals(p.getTomapid()) ;
				item.setPointType(isSameMap?(byte)1:(byte)0);
				MapConfig mc = GameContext.getMapApp().getMapConfig(p.getTomapid());
				if(null != mc){
					//相同地图不设置目标地图名
					item.setMapName(isSameMap?"":mc.getMapdisplayname());
				}else{
					Log4jManager.CHECK.error("canot get the mapconfig: " + p.getTomapid() + " currentMap=" + this.mapId);
					Log4jManager.checkFail();
				}
				item.setTips("");
				if (p.getQuestid() > 0) {
					Quest quest = GameContext.getQuestApp().getQuest(
							p.getQuestid());
					item.setTips(GameContext.getI18n().getText(TextId.MAP_JUMP_POINT_NEED) + quest.getQuestName());
				} else if (p.getGoodsid() > 0) {
					GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(
							p.getGoodsid());
					item.setTips(GameContext.getI18n().getText(TextId.MAP_JUMP_POINT_NEED) + gb.getName());
				} else if (p.getLevel() > 1) {
					item.setTips( p.getLevel() + GameContext.getI18n().getText(TextId.MAP_JUMP_POINT_LEVEL));
				}
				// 条件提示信息
				jumpItems.add(item);
			}
		} catch (Exception e) {
			Log4jManager.CHECK.error("getJumpPoint error",e);
			Log4jManager.checkFail();
		}
		return jumpItems;
	}

	private void buildItem(Map map, MapGetDataItem dataItem,
			MapImageAssociate mapImageAssociate) {
		// 碰撞点信息
		try {
			MapCollideData mapCollideData = map.getMapCollideData();
			if (mapCollideData.getCollideData() != null
					&& mapCollideData.getCollideData().length > 0) {
				byte[] collide = convertArr(mapCollideData.getCollideData(),
						mapCollideData.getCollideData().length
								* mapCollideData.getCollideData()[0].length);
				dataItem.setCollide(collide);
				dataItem.setCollideWidth((short)mapCollideData.getCollideWidth());
				dataItem.setCollideHeight((short)mapCollideData.getCollideHeight());
			}
		} catch (Exception e) {
			Log4jManager.CHECK.error("MapCollideData,mapId=" + mapImageAssociate.getMapId(), e);
			Log4jManager.checkFail() ;
		}

		// plugin 信息
		dataItem.setPluginItems(mapImageAssociate.getPluginItems());

		// 地图跳转点
		JumpMapPointCollection jumpPointData = map.getJumpMapPointCollection();
		if (null != jumpPointData || null != jumpPointData.getPoint()) {
			dataItem.setJumpPointItems(getJumpPoint(jumpPointData));
		}
		// NPC ResId
		MapNpcBornData npcBornData = map.getNpcBornData();
		List<Integer> resIdList = new ArrayList<Integer>();
		if (null != npcBornData && !Util.isEmpty(npcBornData.getNpcborn())) {
			for (NpcBorn npcBorn : npcBornData.getNpcborn()) {
				if (null == npcBorn) {
					continue;
				}
				String npcTemplateId = npcBorn.getBornnpcid();
				NpcTemplate npcTemplate = GameContext.getNpcApp().getNpcTemplate(
						npcTemplateId);
				if (null == npcTemplate) {
					Log4jManager.CHECK.error("npcTemplateId: " + npcTemplateId
							+ " not exists,mapId=" + map.getMapName());
					Log4jManager.checkFail();
					continue;
				}
				if (!resIdList.contains(npcTemplate.getResid())) {
					resIdList.add(npcTemplate.getResid());
				}
			}
		}
		if (resIdList.size() > 0) {
			int[] resIds = new int[resIdList.size()];
			for (int i = 0; i < resIdList.size(); i++) {
				resIds[i] = resIdList.get(i);
			}
			dataItem.setResIds(resIds);
		}
		resIdList.clear();
		resIdList = null;
	}

	private MapImageAssociate loadMapImageAssociate() {
		String dataPath = GameContext.getPathConfig().getDataPath();
		File dir = new File(dataPath);
		MapImageAssociate mapImageAssociate = null;
		String mapImageAssociatePath = "";
		String roadblockPath = "";
		String pluginPath = "";
		String roadblockXmlPath = "";

		File d = new File(dir + File.separator + "map_a" + File.separator);
		File[] files = d.listFiles();
		if (null == files || files.length < 1) {
			Log4jManager.CHECK.error("dir:" + dir + File.separator + "map_a" + File.separator + ",is null ");
			Log4jManager.checkFail();
			return null;
		}
		String path = dir + File.separator + "map_a" + File.separator + gateName + File.separator + mapName + File.separator;
		try {
			mapImageAssociatePath = path + "mapimageassociate.xml";
			roadblockPath = path + "maproad.array";
			pluginPath = path + "plugin.xml";
			roadblockXmlPath = path + "maproadpoint.xml";
			mapImageAssociate = MapImageAssociateReader.readMapImageAssociate(
					mapImageAssociatePath, roadblockPath, pluginPath, roadblockXmlPath);
		} catch (Exception e) {
			Log4jManager.CHECK.error("read map config error, path=" + mapImageAssociatePath, e);
			Log4jManager.checkFail();
		}
		return mapImageAssociate;
	}

	/** *压缩地图 */
	private void compressMapData() {
			mapImageAssociate = this.loadMapImageAssociate();
			tilesetLayers = mapImageAssociate.getTilesetLayers();
			param = 0;// 菱形地图
			if (mapImageAssociate.getMapDataType() == 1
					|| mapImageAssociate.getMapDataType() == 2) {
				param = 1;// 矩形地图
			}
			try {
				/*GameContext.getMapApplication().addMapData(
						version + "_" + mapId, compress(buildMapResp(version)));*/
				/* 压缩老版本
				GameContext.getMapApp().addMapData(mapId, compress(buildMapResp(version)));
				*/
				//压缩新版本
				byte[] mapCompressData = this.compressMapData(buildMapData());
				String md5str = GameContext.md5.getMD5(mapCompressData);
				MapDataInfo dataInfo = new MapDataInfo();
				dataInfo.setData(mapCompressData);
				dataInfo.setMd5(md5str);
				GameContext.getMapApp().addMapData(mapId, dataInfo);
				GameContext.getMapApp().addMapRoadVO(mapId, mapImageAssociate.getMapRoadVO());
			} catch (Exception e) {
				Log4jManager.CHECK.error("compress map data error，mapId= " + mapId + mapName,e);
				Log4jManager.checkFail(); 
			}
	}

	private MapGetDataItem buildMapData() {
		//MapGetDataRespMessage resp = new MapGetDataRespMessage();
		MapGetDataItem dataItem = new MapGetDataItem();
		try {
			//dataItem.setParam((byte) param);
			dataItem.setMapMusicIndex(map.getMapConfig().getMapMusic());
			buildItem(map, dataItem, mapImageAssociate);
			List<MapGetDataLayerItem> mapGetDataLayerItemList = new ArrayList<MapGetDataLayerItem>();
			for (TilesetLayer tilesetLayer : tilesetLayers) {
				int mapLength = tilesetLayer.getHeight()
						* tilesetLayer.getWidth();
				MapGetDataLayerItem mapGetDataLayerItem = new MapGetDataLayerItem();
				String[] strArr = tilesetLayer.getMapGroundRes().split(",");
				short[] groundResArr = new short[strArr.length];
				for (int i = 0; i < strArr.length; i++) {
					groundResArr[i] = Short.parseShort(strArr[i]);
				}
				mapGetDataLayerItem.setGroundres(groundResArr);
				mapGetDataLayerItem.setMapHeight((byte) tilesetLayer
						.getHeight());
				mapGetDataLayerItem.setMapWidth((byte) tilesetLayer.getWidth());
				try {
					if (tilesetLayer.getMapIndex() != null
							&& tilesetLayer.getMapIndex().length > 0) {
						byte[] cellIndex = convertArr(tilesetLayer
								.getMapIndex(), mapLength);
						mapGetDataLayerItem.setCellIndex(cellIndex);
					}
				} catch (Exception e) {
					Log4jManager.CHECK.error("MapIndex arr error ,mapId="+ mapImageAssociate.getMapId(), e);
					Log4jManager.checkFail() ;
				}
				try {
					if (tilesetLayer.getReverseType() != null
							&& tilesetLayer.getReverseType().length > 0) {
						byte[] reverseType = convertArr(tilesetLayer
								.getReverseType(), mapLength);
						mapGetDataLayerItem.setReverseType(reverseType);
					}
				} catch (Exception e) {
					Log4jManager.CHECK.error("tilesetLayer error,mapId="+ mapImageAssociate.getMapId(), e);
					Log4jManager.checkFail() ;
				}
				mapGetDataLayerItemList.add(mapGetDataLayerItem);
			}
			dataItem.setMapGetDataLayerItem(mapGetDataLayerItemList);
			dataItem.setRoadblock(mapImageAssociate.getRoadblock());
			//dataItem.setHorse(map.getMapConfig().getIsEquestrian());
			//小地图资源ID
			dataItem.setSmallMapResId(map.getMapConfig().getSmallMapResId());
			//是否显示地图出口
			//dataItem.setShowExit(map.getMapConfig().getShowExit());
			
			/*boolean compressMapDatea = GameContext.getMapApp()
					.isCompressMapData();
			resp.setCompress(compressMapDatea ? (byte) 1 : (byte) 0); // 压缩标识
			resp.setMapGetDataItem(dataItem);*/
		} catch (Exception e) {
			Log4jManager.CHECK.error("",e);
			Log4jManager.checkFail();
		}
		return dataItem;
	}

	/** *地图构建 */
	private void build() throws Exception {
		MapConfig mapConfig = GameContext.getMapApp().getMapConfig(
				mapName);
		map = new Map(mapId, mapName, mapConfig, wayMap, this.getMapLogic(
				mapId, mapConfig), mapCollideData, npcBornData, jumpPointData,
				questCollectPointConfig, skillCollectPointConfig);

		GameContext.getMapApp().addMapInfo(mapId, map);
	}

	private MapLogic getMapLogic(String mapId, MapConfig mapConfig) {
		return MapLogicType.createMapLogic(mapId, mapConfig);
	}

	/** * 加载MapWay */
	private void loadMapWay() throws Exception {
		List mapWayFileList = FileUtil.getDirFiles(mapPath + File.separator
				+ "mapway", null, "xml");
		for (Object iter : mapWayFileList) {
			MapWay mapWay = XmlUtil.loadFromXml(iter.toString(), MapWay.class);
			// 配置文件中的Point没有mapId,在此需要设置上
			List<Point> pointList = mapWay.getPoint();
			for (Point p : pointList) {
				if (null == p) {
					continue;
				}
				p.setMapid(mapId);
			}
			wayMap.put(mapWay.getPathid(), mapWay);
		}
	}

	
	/*private void loadSkillCollectPoint() throws Exception {
		String skillXmlPath = mapPath + File.separator
				+ "skillcollectpoint.xml";
		File skillXmlFile = new File(skillXmlPath);
		if (skillXmlFile.exists()) {
			skillCollectPointConfig = XmlUtil.loadFromXml(skillXmlPath,
					CollectPointConfig.class);
		}
	}*/

	/** * 加载任务采集点数据 */
	private void loadQuestCollectPoint() throws Exception {
		String questXmlPath = mapPath + File.separator
				+ "questcollectpoint.xml";
		File questXmlFile = new File(questXmlPath);
		if (questXmlFile.exists()) {
			/*questCollectPointConfig = XmlUtil.loadFromXml(questXmlPath,
					CollectPointConfig.class);*/
			CollectPointConfigSaxReader reader = new CollectPointConfigSaxReader();
			reader.parse(questXmlPath);
			questCollectPointConfig = reader.getCollectPointConfig();
		}
	}

	/** * 加载地图跳转点数据 */
	private void loadMapJumpPoint() throws Exception {
		String jumpmappointFile = mapPath + File.separator + "jumpmappoint.xml";
		
		/*jumpPointData = XmlUtil.loadFromXml(jumpmappointFile,
				JumpMapPointCollection.class);*/
		
		MapJumpPointSaxReader reader = new MapJumpPointSaxReader();
		reader.parse(jumpmappointFile);
		jumpPointData = reader.getJumpMapPointCollection();
		
		if (null != jumpPointData && null != jumpPointData.getPoint()) {
			for (JumpMapPoint jmp : jumpPointData.getPoint()) {
				// !!! 配置文件中的Point没有mapId,在此需要设置上
				if (null == jmp) {
					continue;
				}
				jmp.setMapid(mapId);
			}
		}
	}

	/** * 加载地图碰撞点数据 */
	private void loadMapCollideData() throws Exception {
		// 获得碰撞信息
		String mapcollidedataFile = mapPath + File.separator
				+ "mapcollidedata.xml";
		mapCollideData = MapCollideDataReader
				.readMapCollideData(mapcollidedataFile);
	}

	/** *加载NPC出生数据 */
	private void loadMapNpcBornData() throws Exception {
		String npcBornFile = mapPath + File.separator + "mapnpcborndata.xml";
		File file = new File(npcBornFile);
		if (file.exists()) {
			/*npcBornData = XmlUtil
					.loadFromXml(npcBornFile, MapNpcBornData.class);*/
			MapNpcBornDataSaxReader reader = new MapNpcBornDataSaxReader();
			reader.parse(npcBornFile);
			npcBornData = reader.getMapNpcBornData();
		}
		// 判断出生的npc是否存在
		if (null != npcBornData && !Util.isEmpty(npcBornData.getNpcborn())) {
			for (NpcBorn npcBorn : npcBornData.getNpcborn()) {
				if (null == npcBorn) {
					continue;
				}
				String npcTemplateId = npcBorn.getBornnpcid();
				NpcTemplate npcTemplate = GameContext.getNpcApp().getNpcTemplate(
						npcTemplateId);
				if (null == npcTemplate) {
					Log4jManager.CHECK.error("npcTemplateId: " + npcTemplateId
							+ " not exists,mapId=" + mapId);
					Log4jManager.checkFail();
				}
				// 处理npc出生点帮助信息
				Point point = new Point(mapId,
						(npcBorn.getBornmapgxbegin() + npcBorn
								.getBornmapgxend()) / 2, (npcBorn
								.getBornmapgybegin() + npcBorn
								.getBornmapgyend()) / 2);
				GameContext.getMapApp().addNpcBorn(npcTemplateId,
						point);
			}
		}
	}

	private void loadMapLogic() {
		// 加载脚本
		String dataPath = GameContext.getPathConfig().getMapLogicPath();
		File dir = new File(dataPath);
		String pyFile = dir + File.separator + mapName + ".py";
		if(!new File(pyFile).exists()){
			return ;
		}
		GameContext.getPyScriptSupport().loadScript(pyFile);
	}
}
