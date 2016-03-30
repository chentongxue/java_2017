package com.game.draco.app.recovery.logic;

import java.util.List;

import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.recovery.config.RecoveryConfig;
import com.game.draco.app.recovery.domain.RoleRecovery;
import com.game.draco.app.recovery.vo.RecoveryResult;
import com.game.draco.message.item.RecoveryConsumeItem;
import com.game.draco.message.item.RecoveryShowItem;

public interface IRecoveryLogic {

	public static final int PERCENTS = 10000;
	/**
	 * 获得消耗的值
	 * @param role
	 * @param id 追回id
	 * @param consumeType
	 */
	public abstract int getRecoveryAwardConsumeValue(RoleInstance role, String id,
			byte consumeType);

	/**
	 * 获得一键追回的详情
	 * @param recovery
	 * @param vipLevel
	 * @return
	 * @date 2014-10-22 下午08:44:37
	 */
	public abstract List<RecoveryConsumeItem> getRecoveryConsumeItemList(
			final RoleRecovery recovery, byte vipLevel);

	/**
	 * 追回奖励
	 * @param role
	 * @param id
	 * @param consumeType
	 * @return
	 * @date 2014-10-22 下午08:26:38
	 */
	public abstract RecoveryResult recoveryAwardAndConsume(RoleInstance role, String id,
			byte consumeType, int num);
	/**
	 * 追回奖励，不消耗
	 * @param role
	 * @param id
	 * @param consumeType
	 * @return
	 * @date 2014-10-23 上午11:44:42
	 */
	public abstract RecoveryResult recoveryAward(RoleInstance role, String id,
			byte consumeType, int num);

	public abstract boolean canRecovery(RoleRecovery rc);

	public abstract RecoveryShowItem getRecoveryShowItem(RecoveryConfig cf, RoleRecovery recovery,
			byte vipLevel);

}