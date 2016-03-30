package sacred.alliance.magic.domain;

import java.util.HashMap;
import java.util.List;

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.app.map.MapUtil;
import sacred.alliance.magic.app.map.data.MapRoadPointVO;
import sacred.alliance.magic.app.map.data.MapRoadVO;
import sacred.alliance.magic.app.treasure.TreasureGood;
import sacred.alliance.magic.app.treasure.TreasureMonster;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.Point;

import com.game.draco.GameContext;
import com.game.draco.message.item.GoodsBaseItem;
import com.game.draco.message.item.GoodsBaseTreasureItem;

/**
 * 虚空漩涡（藏宝图）
 */

public @Data class GoodsTreasure extends GoodsBase{
	private final static Logger logger = LoggerFactory.getLogger(GoodsTreasure.class);
	private static final String CAT = "," ;
	public static final String IDENTIFY_FLAG = CAT + "1" ;
	private final int MAX_RANDOM_POINT_TIMES = 10 ;
	private short radius ;   //宝藏半径[int]象素
	private String clueDesc ; //线索描述
	private String maps	;
	private String mapProbs	;
	private int identifyGoodsId	;
	private String lackIdenGoodInfo;
	private int emptyProb;
	private int monsterProb	;
	private int goodsProb ;
	private String monsterIds ;
	private String monsterProbs	;
	private String goodsIds	;
	private String goodsProbs ;
	private int transGold;  //地图传送消耗的钻石
	

	//地图
	private List<Integer> mapProbsList;
	private List<String> mapsList;
	private int mapProbsTotal; //概率总和
	//怪物
	private List<Integer> monsterProbsList;
	private List<TreasureMonster> monsterList;
	private int monsterProbsTotal;
	//道具
	private List<Integer> goodsProbsList;
	private List<TreasureGood> goodsList;
	private int goodsProbsTotal;
	//触发事件概率 0:什么都没有,1:出现怪物,2:获得道具
	private List<Integer> thingsProbsList;
	private int thingsProbsTotal;
	
	@Override
	public List<AttriItem> getAttriItemList() {
		return null;
	}
	
	public boolean isRightMapId(String mapId){
		if(Util.isEmpty(mapId) || Util.isEmpty(this.mapsList)){
			return false ;
		}
		return this.mapsList.contains(mapId);
	}

	@Override
	public GoodsBaseItem getGoodsBaseInfo(RoleGoods roleGoods) {
		GoodsBaseTreasureItem item = new GoodsBaseTreasureItem();
		this.setGoodsBaseItem(roleGoods, item);
		item.setIdentify((byte)0);
		item.setDesc(this.desc);
		item.setClueDesc(this.clueDesc);
		item.setSecondType((byte)secondType);
		item.setLvLimit((byte)lvLimit);
		if(null == roleGoods){
			return item ;
		}
		String[] otherParam = Util.splitString(roleGoods.getOtherParm());
		//if(this.hasIdentify(otherParam)){
			//有鉴定字段说明已经鉴定
			String mapId = otherParam[0];
			Short x = Short.valueOf(otherParam[1]);
			Short y = Short.valueOf(otherParam[2]);
			item.setIdentify((byte)1);
			item.setMapName(GameContext.getMapApp().getMapConfig(mapId).getMapdisplayname());
			item.setMapId(mapId);
			item.setX(x);
			item.setY(y);
		//}
		return item;
	}


	@Override
	public void init(Object initData) {
		//物品的几率和怪物的几率不能同时出现
		//否则会出现刷的bug
		if(monsterProb > 0 && goodsProb > 0){
			Log4jManager.CHECK.error("GoodsTreasure config error monsterProb > 0 and goodsProb > 0,id=" + this.getId() );
			Log4jManager.checkFail();
		}
	}

	@Override
	public RoleGoods createSingleRoleGoods(String roleId, int overlapCount){
		RoleGoods rg = super.createSingleRoleGoods(roleId, overlapCount);
		Point point = createRandomPoint(null);
		if (point != null) {
			rg.setOtherParm(createOtherParams(point));
		} 
		return rg;
	}
	
	public String createOtherParams(Point point){
		if(null == point){
			return "" ;
		}
		return (point.getMapid() + CAT + point.getX() + CAT + point.getY());
	}
	
	public static String[] parseOtherParams(String otherParam){
		if(null == otherParam){
			return null ;
		}
		return Util.splitString(otherParam,CAT);
	}
	
	public boolean hasIdentify(String[] params){
		return (null != params && params.length==4) ;
	}
	
	
	/**
	 * 生成藏宝图随机点
	 * @param mapId 指定的地图id，如果为null,或者改地图中没有路点则根据配置随机生成
	 * @return
	 */
	public Point createRandomPoint(String mapId){
		MapRoadVO mapRoadVO = null;
		boolean canRandom = true;
		if(null != mapId){
			mapRoadVO = GameContext.getMapApp().getMapRoadVO(mapId);
			if(mapRoadVO != null && !Util.isEmpty(mapRoadVO.getPoints())){
				canRandom = false;
			}
		}
		MapRoadPointVO targetPoint = null;
		List<Integer> connectPointIdList = null;
		try {
			// 循环10次来生成目标点，如果10次都没有找到则可能是资源配置问题
			for (int i = 0; i < MAX_RANDOM_POINT_TIMES; i++) {
				if(canRandom){
					mapId = mapsList.get(Util.getProbsIndex(mapProbsList,
							mapProbsTotal));
					if (Util.isEmpty(mapId)) {
						continue;
					}
					mapRoadVO = GameContext.getMapApp().getMapRoadVO(mapId);
					if (null == mapRoadVO) {
						continue;
					}
				}
				targetPoint = getRandomPoint(mapRoadVO.getPoints());
				if (null == targetPoint) {
					continue;
				}
				// 如果开始点是野点则重新计算
				connectPointIdList = targetPoint.getConnectList();
				if (connectPointIdList.size() == 0) {
					continue;
				}
				// 判断目标点是否在跳转点附近
				// 如果离所以的跳转点都很近则重新计算
				int posX = targetPoint.getPosX();
				int posY = targetPoint.getPosY();
				if (MapUtil.nearJumpPoint(mapId, posX, posY)) {
					continue;
				}
				//成功找到目标点
				return new Point(mapId, posX, posY);
			}
		}catch(Exception ex){
			StringBuffer buffer = new StringBuffer("");
			buffer.append("createRandomPoint error, goodsId=");
			buffer.append(this.getId());
			buffer.append(" mapId=").append(mapId);
			if(null != targetPoint){
				buffer.append(" startPoint=" + targetPoint.getId());
			}
			logger.error(buffer.toString(),ex);
		}
		return null;
	}
	
	private MapRoadPointVO getRandomPoint(HashMap<Integer, MapRoadPointVO> points){
		int curIndex = 0 ;
		int index = (int)(Math.random() * points.size());
		for(Integer key : points.keySet()){
			if(curIndex == index){
				return points.get(key);
			}
			curIndex++;
		}
		return null ;
	}
	
}
