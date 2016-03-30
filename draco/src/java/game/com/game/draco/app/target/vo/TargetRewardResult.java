package com.game.draco.app.target.vo;

import com.game.draco.app.target.config.TargetConfig;
import com.game.draco.app.target.domain.RoleTarget;

import lombok.Data;
import sacred.alliance.magic.base.Result;

public @Data class TargetRewardResult extends Result {
	private TargetConfig targetConfig;
	private RoleTarget RoleTarget;
	private byte line;
}
