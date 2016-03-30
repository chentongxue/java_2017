package sacred.alliance.magic.app.arena.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;

public @Data class ArenaExpConfig implements KeySupport<Integer>{

	private int level ;
	private int s1v1Exp ;
	private int f1v1Exp ;
	private int snvnExp ;
	private int fnvnExp ;
	@Override
	public Integer getKey() {
		return this.level ;
	}
}
