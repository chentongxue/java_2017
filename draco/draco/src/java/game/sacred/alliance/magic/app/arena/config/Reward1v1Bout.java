package sacred.alliance.magic.app.arena.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;

public @Data class Reward1v1Bout implements KeySupport<Integer>{

	private int level ;
	private int successExp ;
	private int failureExp ;
	private int successGameMoney ;
	private int failureGameMoney ;
	@Override
	public Integer getKey() {
		return this.level ;
	}
}
