package sacred.alliance.magic.app.ai;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;

public @Data class AiBossRefreshRule implements KeySupport<Integer> {
	private int id;
	private String npcId1;
	private int npcCount1;
	private String npcId2;
	private int npcCount2;
	private int circle;
	
	@Override
	public Integer getKey() {
		return this.getId();
	}
}
