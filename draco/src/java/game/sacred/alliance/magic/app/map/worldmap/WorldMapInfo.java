package sacred.alliance.magic.app.map.worldmap;

import com.game.draco.GameContext;

import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.app.map.data.MapConfig;
import lombok.Data;

public @Data class WorldMapInfo {
	
	private String mapId;
	private byte mapIndex ;
	private byte minLevel ;
	private byte maxLevel ;
	private byte camp0 ;
	private byte camp1 ;
	private byte camp2 ;
	private byte camp3 ;
	
	/**
	 * 区域地图是否可以PK
	 * @return 0:否
	 *         1:是
	 * */
	public byte worldMapCanPk(){
		Map map = GameContext.getMapApp().getMap(mapId);
		if(null == map){
			return (byte)0 ;
		}
		MapConfig config = map.getMapConfig();
		return config.isPvpMap()? (byte)1:(byte)0;
	}
	
	
	public byte getCampValue(byte camp){
		if(camp == 0){
			return camp0 ;
		}
		if(camp == 1){
			return camp1 ;
		}
		if(camp == 2){
			return camp2 ;
		}
		if(camp == 3){
			return camp3 ;
		}
		return 1 ;
	}
	
}
