package sacred.alliance.magic.app.chest;

import com.game.draco.GameContext;

import lombok.Data;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;

public @Data class ChestRefreshInfo {

	private String mapId ;
	private int refreshTime	;
	private short chestType	;
	private short chestNum	;
	private String rangeIds ;
	private String broadcast ;

	/**
	 * 区域列表
	 */
	private String[] rangeList = null ;
	
	public void init(){
		this.rangeList = Util.splitString(this.rangeIds);
		if(null == this.rangeList 
				|| 0 == this.rangeList.length){
			Log4jManager.checkFail();
			Log4jManager.CHECK
					.error("ChestRefreshInfo not config rangeIds mapId="+ mapId + " refreshTime=" + refreshTime );
			return ;
		}
		for(String rangeId : this.rangeList){
			ChestRefreshRange rangeInfo = GameContext.getChestApp().getChestRefreshRange(rangeId);
			if(null == rangeInfo || !rangeInfo.getMapId().equals(mapId)){
				Log4jManager.checkFail();
				Log4jManager.CHECK
						.error("ChestRefreshInfo config error, rangeId not exist or mapId not same,rangeId=" 
								+ rangeId + " mapId="+ mapId + " refreshTime=" + refreshTime );
			}
		}
	}
}
