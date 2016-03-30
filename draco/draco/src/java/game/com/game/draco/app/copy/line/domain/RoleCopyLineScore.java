package com.game.draco.app.copy.line.domain;

import lombok.Data;

@Data
public class RoleCopyLineScore {
	
	public static final String ROLEID = "roleId";
	public static final String CHAPTERID = "chapterId";
	public static final String COPYINDEX = "copyIndex";
	
	private String roleId;
	private byte chapterId;
	private byte copyIndex;
	private byte maxStar;//最优星级
	
}
