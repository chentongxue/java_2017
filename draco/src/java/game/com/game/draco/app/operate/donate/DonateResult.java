package com.game.draco.app.operate.donate;

import java.util.List;
import java.util.Map;

import com.game.draco.app.operate.donate.config.DonateInfo;
import com.game.draco.app.operate.donate.config.DonateRankKey;
import com.game.draco.app.operate.donate.config.DonateRankReward;
import com.game.draco.app.operate.donate.config.DonateRule;
import com.game.draco.app.operate.donate.config.DonateScore;
import com.game.draco.app.operate.donate.config.DonateWorldReward;

import lombok.Data;
import sacred.alliance.magic.base.Result;

public @Data class DonateResult extends Result {
	private Map<Integer, DonateWorldReward> worldRewardMap;
	private Map<Integer, DonateScore> scoreMap;
	private Map<Integer, DonateRule> ruleMap;
	private Map<Integer, DonateInfo> donateMap;
	private Map<String, DonateRankReward> rankRewardMap;
	private Map<Integer, List<DonateRankKey>> rankKeyListMap;
	
	private byte rewardState; //0:无奖励 1:有奖励但尚不能领取 2:可领奖 3:已领奖
	private DonateRankReward rankReward;
	private DonateInfo donateInfo;
}
