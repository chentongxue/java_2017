package com.game.draco.app.target.cond;

import com.game.draco.app.target.config.TargetCond;

import sacred.alliance.magic.vo.RoleInstance;

public class TargetRoleLevel extends TargetLogic {
	public TargetRoleLevel() {
		super(TargetCondType.RoleLevel);
	}

	@Override
	public int getCurValue(RoleInstance role, TargetCond cond) {
		return role.getLevel();
	}

}
