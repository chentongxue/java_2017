package com.game.draco.app.qualify.domain;

import lombok.Data;

public @Data class ChallengeRecord {
	
	private byte status;//[0.挑战失败 1.挑战成功 2.被挑战我失败 3.被挑战我胜利]
	private int challengeTime;
	private String roleName;
	private byte type;// [0.不变 1.上升 2.下降]
	private short currRank;

}
