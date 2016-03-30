package sacred.alliance.magic.app.arena.config;

import lombok.Data;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.Point;

public @Data class ArenaConfig {

	private int arenaType ;
	private int activeId ;
	private String mapId ;
	private short x1 ;
	private short y1 ;
	private short x2 ;
	private short y2 ;
	private String levelRange ;
	private int clearBaffleTime ;
	private int matchInterval ;
	private int maxBattleTime ;
	private int successScore;
	private int failScore;
	private int battleScore ;
	//特殊次数(此次数下可以获得额外奖励)
	private short specialTimes ;
	private int[] levels = new int[0] ;
	private Point point1 = null ;
	private Point point2 = null ;
	
	private String exchangeMenuId;
	private String desc;
	
	public void init(){
		if(!Util.isEmpty(levelRange)){
			this.levelRange = this.levelRange.trim();
			String[] strs = this.levelRange.split(Cat.comma);
			this.levels = new int[strs.length];
			int index = 0 ;
			for(String s : strs){
				this.levels[index++] = Integer.parseInt(s);
			}
		}
		this.point1 = new Point(this.mapId,x1,y1);
		this.point2 = new Point(this.mapId,x2,y2);
	}
	
	public int getLevelRangeIndex(int level){
		if(0 == this.levels.length){
			return 0 ;
		}
		for(int i=this.levels.length-1;i>=0;i--){
			if(level > this.levels[i]){
				return i ;
			}
		}
		return 0 ;
	}
}
