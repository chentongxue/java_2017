package sacred.alliance.magic.app.arena.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.vo.Point;

public @Data class ArenaMapRule implements KeySupport<Integer>{

	private int ruleId ;
	private String mapId ;
	private short x1 ;
	private short y1 ;
	private short x2 ;
	private short y2 ;
	
	private Point point1 = null ;
	private Point point2 = null ;
	
	public void init(){
		this.point1 = new Point(this.mapId,x1,y1);
		this.point2 = new Point(this.mapId,x2,y2);
	}

	@Override
	public Integer getKey() {
		return ruleId;
	}
}
