package com.game.draco.app.copy.line.domain;

import lombok.Data;

@Data
public class RoleCopyLineReward {
	
	public static final String ROLEID = "roleId";
	public static final String CHAPTERID = "chapterId";
	
	private String roleId;
	private byte chapterId;
	private int takeStarNum;//已领奖星级
	
}
