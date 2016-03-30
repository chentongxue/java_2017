package com.game.draco.app.qualify.domain;

import lombok.Data;

public @Data class QualifyRank {
	
	private short rank;
	private String roleId;
	private byte robot;
	
	// 以下字段从其它表获得
	private String roleName;
	private int roleLevel;
	private int battleScore;
	
}
