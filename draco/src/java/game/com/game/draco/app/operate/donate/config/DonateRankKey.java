package com.game.draco.app.operate.donate.config;

import java.util.Map;

import com.game.draco.app.operate.donate.DonateResult;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data class DonateRankKey implements KeySupport<Integer>{
	private int rankId;
	private short rankStart;
	private short rankEnd;
	private String rankKey;
	
	private DonateRankReward rankReward = null;
	
	public Result init(DonateResult donateResult) {
		Result result = new Result();
		Map<String, DonateRankReward> rankKeyMap = donateResult.getRankRewardMap();
		DonateRankReward rankReward = rankKeyMap.get(rankKey);
		if(null == rankReward) {
			result.setInfo("DonateRankKey rankKey=" + rankKey + ", config rankReward do not exist!");
			return result;
		}
		this.rankReward = rankReward; 
		result.success();
		return result;
	}
	
	@Override
	public Integer getKey() {
		return this.rankId;
	}

}
