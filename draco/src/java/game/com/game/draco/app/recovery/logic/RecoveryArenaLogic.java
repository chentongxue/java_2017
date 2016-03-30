package com.game.draco.app.recovery.logic;

import java.util.List;

import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.recovery.domain.RoleRecovery;
import com.game.draco.app.recovery.vo.RecoveryResult;
import com.game.draco.message.item.RecoveryConsumeItem;

/**
 * 谁与争锋（同步竞技场）
 */
public class RecoveryArenaLogic extends RecoveryLogic{
	private static RecoveryArenaLogic instance = new RecoveryArenaLogic();
	private RecoveryArenaLogic(){
	}
	
	public static RecoveryArenaLogic getInstance(){
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
