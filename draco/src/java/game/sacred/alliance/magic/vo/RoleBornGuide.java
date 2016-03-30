package sacred.alliance.magic.vo;

import lombok.Data;

public @Data
class RoleBornGuide {

	private String mapId;
	private short mapX;
	private short mapY;
	private short buffId;
	private int timeout;
	private String npcRuleId ;
	
	//给与的英雄ID
	private int giveHeroId ;
	//此npc出现时给角色多余的英雄
	private String giveHeroNpcId ;
	

	public Point getPoint(){
		return new Point(this.mapId,this.mapX,this.mapY) ;
	}
}
