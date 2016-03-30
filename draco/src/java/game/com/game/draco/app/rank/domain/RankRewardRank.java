package com.game.draco.app.rank.domain;

import lombok.Data;

public @Data class RankRewardRank {
	private String roleKey;
	private short rankStart;
	private short rankEnd;
	private String rankKey;

	public boolean inRange(int rank){
		return rank >= this.rankStart && rank <= this.rankEnd ;
	}
}
