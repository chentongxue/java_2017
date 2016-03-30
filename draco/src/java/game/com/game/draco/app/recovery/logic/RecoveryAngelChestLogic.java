package com.game.draco.app.recovery.logic;

import java.util.List;

import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.recovery.domain.RoleRecovery;
import com.game.draco.app.recovery.vo.RecoveryResult;
import com.game.draco.message.item.RecoveryConsumeItem;

/**
 * 赏金宝库（即神仙福地）
 */
public class RecoveryAngelChestLogic extends RecoveryLogic{
	private static RecoveryAngelChestLogic instance = new RecoveryAngelChestLogic();
	private RecoveryAngelChestLogic(){
	}
	
	public static RecoveryAngelChestLogic getInstance(){
		return instance ;
	}


	@Override
	public int getRecoveryAwardConsumeValue(RoleInstance role, String id, byte consumeType){
		return super.getRecoveryAwardConsumeValue(role, id, consumeType);
	}
	@Override
	public List<RecoveryConsumeItem> getRecoveryConsumeItemList(
			final RoleRecovery recovery, byte vipLevel){
		return super.getRecoveryConsumeItemList(recovery, vipLevel);
	}

	@Override
	public RecoveryResult recoveryAwardAndConsume(RoleInstance role, String id,
			byte consumeType, int num){
		return super.recoveryAwardAndConsume(role, id, consumeType, num);
	}
	@Override
	public boolean canRecovery(RoleRecovery rc) {
		return super.canRecovery(rc);
	}
}
