package sacred.alliance.magic.domain;

import lombok.Data;

public @Data class CarnivalRankInfo{
	private int id;
	private int activeId;
	private String targetId;//角色ID 门派ID
	private String name;//角色NAME 门派NAME
	private byte campId;//阵营ID
	private byte career;
	private int targetValue;
	private byte rank;//名次
}
