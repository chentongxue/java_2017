package com.game.draco.app.recovery.vo;

import lombok.Data;
import sacred.alliance.magic.base.Result;

public @Data class RecoveryResult extends Result{
	public RecoveryResult failure(){
		super.failure();
		return this;
	}
	public RecoveryResult ignore(){
		super.setIgnore(true);
		return this;
	}
	public RecoveryResult success(){
		super.success();
		return this;
	}
}
