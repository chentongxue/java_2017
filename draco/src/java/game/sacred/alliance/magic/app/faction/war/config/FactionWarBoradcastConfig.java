package sacred.alliance.magic.app.faction.war.config;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data class FactionWarBoradcastConfig implements KeySupport<Integer>{
	private int rounds;
	private String beginMessage;
	private String endMessage;
	@Override
	public Integer getKey() {
		return rounds;
	}
}
