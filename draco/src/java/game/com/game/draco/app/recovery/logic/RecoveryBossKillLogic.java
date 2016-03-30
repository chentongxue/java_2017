package com.game.draco.app.recovery.logic;

import java.util.List;

import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.recovery.domain.RoleRecovery;
import com.game.draco.app.recovery.vo.RecoveryResult;
import com.game.draco.message.item.RecoveryConsumeItem;

/**
 * 肉山必须死
 */
public class RecoveryBossKillLogic extends RecoveryLogic{
	private static RecoveryBossKillLogic instance = new RecoveryBossKillLogic();
	private RecoveryBossKillLogic(){
	}
	
	public static RecoveryBossKillLogic getInstance(){
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
