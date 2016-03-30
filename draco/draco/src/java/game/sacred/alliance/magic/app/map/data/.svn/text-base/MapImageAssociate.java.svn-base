package sacred.alliance.magic.app.map.data;

import java.util.List;

import com.game.draco.message.item.MapGetDataPluginItem;


public class MapImageAssociate {

	private String mapId;
	
	private List<MapGetDataPluginItem> pluginItems ;
	
	private String mapGroundRes;
	
	private int mapDataType;//0:老地图数据， 1：新地图数据 2:新老共存
	
	private List<TilesetLayer> tilesetLayers;//其他图层
	
	private byte[] roadblock;//路点信息
	
	private MapRoadVO mapRoadVO;
	
	public MapImageAssociate(String mapId,List<MapGetDataPluginItem> pluginItems,String mapGroundRes,int mapDataType, List<TilesetLayer> tilesetLayers, byte[] roadblock){
		this.mapId = mapId;
		this.pluginItems = pluginItems ;
		this.mapGroundRes = mapGroundRes;
		this.mapDataType = mapDataType;
		this.tilesetLayers = tilesetLayers;
		this.roadblock = roadblock;
	}
	
	public String getMapId() {
		return mapId;
	}

	public void setMapId(String mapId) {
		this.mapId = mapId;
	}

	public List<MapGetDataPluginItem> getPluginItems() {
		return pluginItems;
	}

	public void setPluginItems(List<MapGetDataPluginItem> pluginItems) {
		this.pluginItems = pluginItems;
	}

	public String getMapGroundRes() {
		return mapGroundRes;
	}

	public void setMapGroundRes(String mapGroundRes) {
		this.mapGroundRes = mapGroundRes;
	}

	public int getMapDataType() {
		return mapDataType;
	}

	public void setMapDataType(int mapDataType) {
		this.mapDataType = mapDataType;
	}

	public List<TilesetLayer> getTilesetLayers() {
		return tilesetLayers;
	}

	public void setTilesetLayers(List<TilesetLayer> tilesetLayers) {
		this.tilesetLayers = tilesetLayers;
	}

	public byte[] getRoadblock() {
		return roadblock;
	}

	public void setRoadblock(byte[] roadblock) {
		this.roadblock = roadblock;
	}

	public MapRoadVO getMapRoadVO() {
		return mapRoadVO;
	}

	public void setMapRoadVO(MapRoadVO mapRoadVO) {
		this.mapRoadVO = mapRoadVO;
	}
}
