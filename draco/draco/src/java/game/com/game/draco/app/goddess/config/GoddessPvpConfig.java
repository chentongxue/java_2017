package com.game.draco.app.goddess.config;

import lombok.Data;

public @Data class GoddessPvpConfig {
	private int challengeNum;
	private String mapId;
	private int mapX;
	private int mapY;
	private int targetMapX;
	private int targetMapY;
	private int recordSize;
	private int challengeCount;
	private byte payChallengeMoneyType;
	private int payChallengeMoney;
	private int maxPayMoney;
	private int clearBaffleTime;
	private int rewardCycle;
	private int rewardTime;
	private int rewardMinute;
	private int selfSoulHpRate;
	private int targetSoulHpRate;
	private int challengeCd;
	private int noCdVipLevel;
	private int cdMoney;
}
