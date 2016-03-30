package com.game.draco.app.operate.monthcard;

import lombok.Data;
import sacred.alliance.magic.base.Result;

import com.game.draco.app.operate.monthcard.config.MonthCardConfig;

public @Data class MonthCardResult extends Result {
	
	private MonthCardConfig monthCardConfig;
	
}
