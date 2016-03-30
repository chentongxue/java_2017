package com.game.draco.app.operate.payextra;

import java.util.Map;

import lombok.Data;

import com.game.draco.app.operate.payextra.config.PayExtraBaseConfig;
import com.game.draco.app.operate.payextra.config.PayExtraRewardConfig;

import sacred.alliance.magic.base.Result;

public @Data class PayExtraResult extends Result {
	
	private PayExtraBaseConfig payExtraBaseConfig;
	private Map<Integer, PayExtraRewardConfig> payExtraRewardMap;
	
}
