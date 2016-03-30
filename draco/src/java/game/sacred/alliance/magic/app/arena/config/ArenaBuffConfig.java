package sacred.alliance.magic.app.arena.config;

import lombok.Data;

public @Data class ArenaBuffConfig {

	private int arenaType ;
	private int minOpenDay;
	private int maxOpenDay;
	private short buffId;
	
	public boolean isSuitDay(int openDay){
		if(openDay < 0){
			return false;
		}
		return openDay >= minOpenDay && openDay <= maxOpenDay;
	}
}
