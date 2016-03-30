package com.game.draco.app.target.cond;

import java.util.Map;

import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.target.config.TargetCond;

public abstract class TargetLogic {
	protected TargetCondType condType;
	
	public TargetLogic(TargetCondType condType) {
		this.condType = condType;
	}
	
	public TargetCondType getTargetCondType() {
		return this.condType;
	}
	
	/**
	 * 目标当前值
	 * @return
	 */
	public abstract int getCurValue(RoleInstance role, TargetCond cond);
	/**
	 * 角色是否满足目标
	 * @param role
	 * @param cond
	 * @return
	 */
	public boolean isMeetCond(RoleInstance role, TargetCond cond) {
		return this.getCurValue(role, cond) >= cond.getValue();
	}
	

	


}
