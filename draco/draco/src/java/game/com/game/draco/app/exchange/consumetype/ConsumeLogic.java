package com.game.draco.app.exchange.consumetype;

import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.vo.RoleInstance;

public abstract class ConsumeLogic {
	
	public abstract int getRoleAttri(RoleInstance role);
	
	/**
	 * 扣除对应的玩家钱数或相应的值
	 * @param role
	 */
	public abstract boolean reduceRoleAttri(RoleInstance role, int value);
	
	/**
	 * 获取失败Status
	 * @return
	 */
	public abstract Status getFailureStatus();
}
