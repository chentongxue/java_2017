package sacred.alliance.magic.app.treasure;

import com.game.draco.GameContext;

import lombok.Data;
import sacred.alliance.magic.app.map.data.MapRoadVO;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;

public @Data class TreasureMap implements KeySupport<Integer>{
	private int id;
	private String mapId;
	@Override
	public Integer getKey() {
		return this.id;
	}
	
	private boolean haveCheck = false ;
	public void check(){
		if(haveCheck){
			return ;
		}
		haveCheck = true ;
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
