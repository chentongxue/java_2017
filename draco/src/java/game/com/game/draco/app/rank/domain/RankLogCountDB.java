package com.game.draco.app.rank.domain;

import sacred.alliance.magic.domain.RoleCount;
import lombok.Data;

public @Data class RankLogCountDB extends RoleCount{
	
	public RankLogCountDB(){
		
	}
	//改为阵营
	private byte career;
}
