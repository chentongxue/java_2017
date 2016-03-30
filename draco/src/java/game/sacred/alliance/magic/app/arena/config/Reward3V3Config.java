package sacred.alliance.magic.app.arena.config;

import lombok.Data;

public @Data class Reward3V3Config {
	private static final float FULL = 10000 ;
	private int arenaMinLevel ;
	private int arenaMaxLevel ;
	private int maxCycleScore ;
	private int maxRoleScore ;
	private int winScore ;
	private int loseScore ;
	private float modulus ;
	private float killModulus ;
    private String name ;
	
	public void init(){
		this.killModulus /= FULL ;
	}
	
	public boolean isSuitLevel(float arenaLevel){
		//必须转换为int或者会匹配不上
		int lv = (int)arenaLevel;
		if(lv < 0){
			return false;
		}
		return lv >= this.arenaMinLevel && lv <= this.arenaMaxLevel;
	}
}
