package sacred.alliance.magic.app.arena.config;

import lombok.Data;

public @Data class Reward1V1Finish {
	
	private int minLevel ;
	private int maxLevel ;
	private int startRank ;
	private int endRank ;
	private int exp ;
	private int gameMoney ;
	private int goodsId ;
	private int goodsNum ;
	private byte bindType ;
}
