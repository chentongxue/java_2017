package sacred.alliance.magic.app.arena.config;

import lombok.Data;

public @Data class ScoreResult {
	private float arenaLevel;
	private int score;
	private int maxCycleScore;
	private int maxRoleScore;
}
