package com.game.draco.app.union;

import sacred.alliance.magic.base.OutputConsumeType;

public enum FunType {

	donate(OutputConsumeType.union_donate_dkp_reward),
	buy(OutputConsumeType.union_shop_dkp_consume),
	exchange(OutputConsumeType.union_exchange_dkp_consume),
	instanceReward(OutputConsumeType.union_instance_dkp_reward),
	gemActivity(OutputConsumeType.union_activity_gem_consume)
	;
	
	OutputConsumeType type;
	
	FunType(OutputConsumeType type){
		this.type = type;
	}
	
	public OutputConsumeType getType() {
		return type;
	}
}
