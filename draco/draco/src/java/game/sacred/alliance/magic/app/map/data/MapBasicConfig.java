package sacred.alliance.magic.app.map.data;

import lombok.Data;

public @Data class MapBasicConfig {

	private String mapId ;
	private String mapName ;
	private int smallMapResId ;
	private int minTransLevel ;
	private int maxTransLevel ;
	private byte weather ;
	private short weatherTimes ;
	private byte showExit ; //是否显示出口
	private byte npcPK = 0 ;
	private int broadcastAllMax = 0;
	private byte roleCanPK = 0;
}
