package com.game.draco.app.union;

import sacred.alliance.magic.base.OutputConsumeType;

public enum FunType {

	donate(OutputConsumeType.union_donate_dkp_reward),
	buy(OutputConsumeType.union_shop_dkp_consume),
	exchange(OutputConsumeType.union_exchange_dkp_consume),
	instanceReward(OutputConsumeType.union_instance_dkp_reward),
	gemActivity(OutputConsumeType.union_activity_gem_consume),
	didAuction(OutputConsumeType.union_auction_did_consume),
	rollbackAuction(OutputConsumeType.union_auction_dkp_reward),	
	recovery(OutputConsumeType.recovery_add_dkp_reward),
	exitUnion(OutputConsumeType.union_exit_union_consume),
	recoUnion(OutputConsumeType.union_add_union_reward),
	buyUnionBuff(OutputConsumeType.union_buff_dkp_consume),
	battleRewardDkp(OutputConsumeType.union_battle_role_reward),
	monsterFallDkp(OutputConsumeType.monster_fall),
	integralRewardDkp(OutputConsumeType.union_integral_battle_role_reward),
	;
	
	OutputConsumeType type;
	
	FunType(OutputConsumeType type){
		this.type = type;
	}
	
	public OutputConsumeType getType() {
		return type;
	}
}
