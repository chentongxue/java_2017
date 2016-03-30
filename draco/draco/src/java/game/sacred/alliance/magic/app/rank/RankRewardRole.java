package sacred.alliance.magic.app.rank;

import lombok.Data;

public @Data class RankRewardRole {
	private int rankId;
	private byte career;
	private byte gender;
	private byte levelStart;
	private byte levelEnd;
	private String roleKey;
}
