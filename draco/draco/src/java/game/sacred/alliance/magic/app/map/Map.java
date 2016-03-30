package sacred.alliance.magic.app.map;

import java.util.HashMap;

import sacred.alliance.magic.app.map.data.MapCollideData;
import sacred.alliance.magic.app.map.data.MapConfig;
import sacred.alliance.magic.app.map.data.MapNpcBornData;
import sacred.alliance.magic.app.map.data.NpcBorn;
import sacred.alliance.magic.app.map.logic.MapLogic;
import sacred.alliance.magic.app.map.point.CollectPointConfig;
import sacred.alliance.magic.app.map.point.JumpMapPointCollection;
import sacred.alliance.magic.constant.MapCellSize;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.MapWay;
import sacred.alliance.magic.vo.Point;


public class Map {

	private String mapId;
	
	private String mapName;
	
	private MapConfig mapConfig;
	
	private java.util.Map<String, MapWay> wayMap;
	
	private MapLogic mapLogic;
	
	private MapCollideData mapCollideData;
	
	private MapNpcBornData npcBornData;
	
	private JumpMapPointCollection jumpMapPointCollection ;
	
//	private MapImageAssociate mapImageAssociate ;
	
	/**任务采集点*/
	private CollectPointConfig questCollectPointConfig ;
	
    private java.util.Map<String,Point> bornNpcMap = new HashMap<String,Point>();
    
	public Map(String mapId, String mapName, MapConfig mapConfig,
			java.util.Map<String, MapWay> wayMap,  MapLogic mapLogic,
			MapCollideData mapCollideData, MapNpcBornData npcBornData,
			JumpMapPointCollection jumpMapPointCollection,
			CollectPointConfig questCollectPointConfig,
			CollectPointConfig skillCollectPointConfig) {
		this.mapId = mapId;
		this.mapName = mapName;
		this.mapConfig = mapConfig;
		this.wayMap = wayMap;
		this.mapLogic = mapLogic;
		this.mapCollideData = mapCollideData;
		this.npcBornData = npcBornData;
		this.jumpMapPointCollection = jumpMapPointCollection ;
		this.questCollectPointConfig = questCollectPointConfig ;
		if(null != this.questCollectPointConfig){
			this.questCollectPointConfig.init(mapId);
		}
        if(null != npcBornData && null != npcBornData.getNpcborn()){
           for (NpcBorn npcBorn : npcBornData.getNpcborn()) {
				if (null == npcBorn) {
					continue;
				}
				String npcId = npcBorn.getBornnpcid();
				if (Util.isEmpty(npcId)) {
					continue;
				}
				if (this.bornNpcMap.containsKey(npcId)) {
					continue;
				}
				Point point = new Point(mapId,
						(npcBorn.getBornmapgxbegin() + npcBorn
								.getBornmapgxend()) / 2, (npcBorn
								.getBornmapgybegin() + npcBorn
								.getBornmapgyend()) / 2);
				this.bornNpcMap.put(npcId, point);
			}
        }
	}

	private int getCanMoveMapXMax(){
		return mapCollideData.getCollideData()[0].length*MapCellSize.MAP_CELL_X_SIZE;
	}
	
	private int getCanMoveMapYMax(){
		return (mapCollideData.getCollideData().length / 2)
				* MapCellSize.MAP_CELL_Y_SIZE
				+ (mapCollideData.getCollideData().length % 2 + 1)
				* MapCellSize.HALF_MAP_CELL_Y_SIZE;
	}
	
	private int getCanMoveMapXMin(){
		return MapCellSize.MAP_CELL_X_SIZE/2;
	}
	
	private int getCanMoveMapYMin(){
		return MapCellSize.MAP_CELL_Y_SIZE/2;
	}
	
	public boolean isBlock(int x, int y){

		if(x<=getCanMoveMapXMin()||y<=getCanMoveMapYMin()){
//			System.out.println("小过地图!!!!!!!!!x=" + x + " y=" + y
//					+ " getCanMoveMapXMin()=" + getCanMoveMapXMin()
//					+ " getCanMoveMapYMin()=" + getCanMoveMapYMin());
			return true;
		}
		
		if(x>=getCanMoveMapXMax()||y>=getCanMoveMapYMax()){
//			System.out.println("超出地图!!!!!!!!!x=" + x + " y=" + y
//					+ " getCanMoveMapXMax()=" + getCanMoveMapXMax()
//					+ " getCanMoveMapYMax()=" + getCanMoveMapYMax());
			return true;
		}
		//是否奇数
		boolean isOddNumber = (y/MapCellSize.HALF_MAP_CELL_Y_SIZE)%2==0?false:true;
		int gx,gy;
		if(isOddNumber){
			gy=(x-MapCellSize.HALF_MAP_CELL_X_SIZE)/MapCellSize.MAP_CELL_X_SIZE;
		}else{
			gy=x/MapCellSize.MAP_CELL_X_SIZE;
		}
		gx=y/MapCellSize.HALF_MAP_CELL_Y_SIZE;
		
		if(gx>=mapCollideData.getCollideData().length){
			gx=mapCollideData.getCollideData().length-1;
		}
		if(gy>=mapCollideData.getCollideData()[0].length){
			gy=mapCollideData.getCollideData()[0].length-1;
		}
		if(mapCollideData.getCollideData()[gx][gy]==1){
//			System.out.println("有碰撞!!!!!!!!!gx=" + gx + " gy=" + gy+" x="+x+" y="+y);
		}
		return mapCollideData.getCollideData()[gx][gy]==0?false:true;
	}
	
	public String getMapId() {
		return mapId;
	}

	public void setMapId(String mapId) {
		this.mapId = mapId;
	}

	public String getMapName() {
		return mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	public MapConfig getMapConfig() {
		return mapConfig;
	}

	public void setMapConfig(MapConfig mapConfig) {
		this.mapConfig = mapConfig;
	}

	

	public java.util.Map<String, MapWay> getWayMap() {
		return wayMap;
	}

	public void setWayMap(java.util.Map<String, MapWay> wayMap) {
		this.wayMap = wayMap;
	}

	public MapLogic getMapLogic() {
		return mapLogic;
	}

	public void setMapLogic(MapLogic mapLogic) {
		this.mapLogic = mapLogic;
	}

	public MapCollideData getMapCollideData() {
		return mapCollideData;
	}

	public void setMapCollideData(MapCollideData mapCollideData) {
		this.mapCollideData = mapCollideData;
	}

	public MapNpcBornData getNpcBornData() {
		return npcBornData;
	}

	public void setNpcBornData(MapNpcBornData npcBornData) {
		this.npcBornData = npcBornData;
	}
	
	public int getMapWidth(){
		if(mapCollideData==null || mapCollideData.getCollideData()[0]==null){
                    return 0;
                }
		return mapCollideData.getCollideData()[0].length;		
	}
	
	public int getMapHeight(){
		if(mapCollideData==null){
                    return 0;
                }
		return mapCollideData.getCollideData().length;
	}

	public JumpMapPointCollection getJumpMapPointCollection() {
		return jumpMapPointCollection;
	}

	public void setJumpMapPointCollection(
			JumpMapPointCollection jumpMapPointCollection) {
		this.jumpMapPointCollection = jumpMapPointCollection;
	}

	public CollectPointConfig getQuestCollectPointConfig() {
		return questCollectPointConfig;
	}

	public void setQuestCollectPointConfig(
			CollectPointConfig questCollectPointConfig) {
		this.questCollectPointConfig = questCollectPointConfig;
	}

	
    public boolean hasBaffle(int x1, int y1, int x2, int y2) {
        //TODO: 根据碰撞信息实现相关逻辑
        //

        return false;
    }

    public boolean hasBaffle(Point point1, Point point2) {
        return this.hasBaffle(point1.getX(), point1.getY(), point2.getX(), point2.getY());
    }
    
    public java.util.Map<String,Point> getBornNpcMap(){
    	return this.bornNpcMap ;
    }


    public boolean haveBornNpc(String npcId){
    	if(null == npcId){
    		return false ;
    	}
    	return this.bornNpcMap.containsKey(npcId);
    }
    
    public Point getNpcBornPoint(String npcId){
    	if(null == npcId){
    		return null ;
    	}
    	return this.bornNpcMap.get(npcId);
    }
    
    
}
