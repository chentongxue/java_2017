package sacred.alliance.magic.app.treasure;

import com.game.draco.GameContext;

import lombok.Data;
import sacred.alliance.magic.app.map.data.MapRoadVO;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;
/**
 * 虚空漩涡(藏宝图)地图
 */
public @Data class TreasureMap implements KeySupport<Integer>{
	private int id;
	private String mapId;
	
	@Override
	public Integer getKey() {
		return this.id;
	}
	
	public void check(){
		sacred.alliance.magic.app.map.Map map = GameContext.getMapApp().getMap(this.getMapId());
		if(null == map){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("reading map from treasure map id error,map:" + this.getMapId() + " not exist");
		}
		MapRoadVO mapRoadVO = GameContext.getMapApp().getMapRoadVO(this.getMapId());
		if(mapRoadVO == null){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("reading map from treasure map id error,map:" + this.getMapId() + " not config map road info");
		}
	}
}
