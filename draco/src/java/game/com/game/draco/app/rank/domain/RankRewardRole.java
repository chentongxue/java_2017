package com.game.draco.app.rank.domain;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;

public @Data class RankRewardRole implements KeySupport<String> {
	private int rankId;
	private byte gender;
	private short levelStart;
	private short levelEnd;
	private String roleKey;

	@Override
	public String getKey() {
		return rankId + "_" + this.gender ;
	}

	public boolean inRange(int level){
		return level >= levelStart && level <= levelEnd ;
	}
}

