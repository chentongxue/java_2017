package com.game.draco.app.target.cond;

import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.target.config.TargetCond;

public class TargetHorseNum extends TargetLogic {

	public TargetHorseNum() {
		super(TargetCondType.HorseNum);
	}

	@Override
	public int getCurValue(RoleInstance role, TargetCond cond) {
		return GameContext.getRoleHorseApp().getRoleHorseNum(role.getIntRoleId());
	}
	
	

}
