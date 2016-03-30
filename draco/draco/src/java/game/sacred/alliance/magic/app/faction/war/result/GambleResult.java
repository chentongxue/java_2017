package sacred.alliance.magic.app.faction.war.result;

import lombok.Data;
import sacred.alliance.magic.app.faction.war.config.FactionWarGambleConfig;
import sacred.alliance.magic.base.Result;

public @Data class GambleResult extends Result {
	private FactionWarGambleConfig factionWarGambleConfig;
	private String factionId;
	private int totalMoney;
}
