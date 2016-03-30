package sacred.alliance.magic.vo;

import lombok.Data;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.KeySupport;

public @Data class RebornPointDetail implements KeySupport<String> {
	private String mapId;
	private byte campId; 
	private String rebornMapId;
	private int mapX;
	private int mapY;
	//是否开启原地复活
	private boolean rebornPlace;
	//墓地复活倒记时时间
	private short countDownTime ;
	
	
	@Override
	public String getKey() {
		return String.valueOf(this.mapId + Cat.underline + this.campId);
	}
	public Point createPoint(){
		return new Point(rebornMapId,mapX,mapY);
	}
}