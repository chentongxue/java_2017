package sacred.alliance.magic.app.map;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import sacred.alliance.magic.app.map.data.MapRoadPointVO;
import sacred.alliance.magic.app.map.data.MapRoadVO;
import sacred.alliance.magic.app.map.point.JumpMapPoint;
import sacred.alliance.magic.constant.MapConstant;
import sacred.alliance.magic.util.RandomUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.Point;

import com.game.draco.GameContext;
import com.google.common.collect.Lists;

public class MapUtil {
	
	public static boolean nearJumpPoint(String mapId,int x, int y){
		List<JumpMapPoint> jumpMapPoints = GameContext.getMapApp().getMap(mapId)
			.getJumpMapPointCollection().getPoint();
		if(Util.isEmpty(jumpMapPoints)){
			return false ;
		}
		for(JumpMapPoint jumpMapPoint : jumpMapPoints){
			if(Util.inCircle(jumpMapPoint.getX(), jumpMapPoint.getY(), x, y,
					MapConstant.JUMP_POINT_EFFECT_RADIOS)) {
				return true ;
			}
		}
		return false ;
	}
	
	public static Point randomCorrectRoadPoint(String mapId){
		if(Util.isEmpty(mapId)){
			return null ;
		}
		MapRoadVO mapRoadVO = GameContext.getMapApp().getMapRoadVO(mapId);
		if(null == mapRoadVO || Util.isEmpty(mapRoadVO.getPoints())){
			return null ;
		}
		List<MapRoadPointVO> voList = Lists.newArrayList() ;
		for(MapRoadPointVO vo : mapRoadVO.getPoints().values()){
			if(Util.isEmpty(vo.getConnectList())){
				continue ;
			}
			//判断是否离跳转点太近
			if(MapUtil.nearJumpPoint(mapId, vo.getPosX(), vo.getPosY())){
				continue ;
			}
			voList.add(vo);
		}
		if(Util.isEmpty(voList)){
			return null ;
		}
		int index = RandomUtil.absRandomInt(voList.size());
		MapRoadPointVO vo = voList.get(index);
		return new Point(mapId, vo.getPosX(), vo.getPosY()); 
	}
	
	public static boolean existRoadPoint(String mapId, int x, int y){
		if(Util.isEmpty(mapId)){
			return false ;
		}
		MapRoadVO mapRoadVO = GameContext.getMapApp().getMapRoadVO(mapId);
		if(null == mapRoadVO){
			return false;
		}
		HashMap<Integer, MapRoadPointVO> points = mapRoadVO.getPoints();
		if(Util.isEmpty(points)){
			return false;
		}
		
		for(Entry<Integer, MapRoadPointVO> entry : points.entrySet()){
			MapRoadPointVO point = entry.getValue();
			if(null == point){
				continue ;
			}
			if(point.getPosX() == x && point.getPosY() == y){
				return true;
			}
		}
		return false;
	}
	
}
