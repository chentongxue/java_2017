package com.game.draco.app.target.cond;

import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.target.config.TargetCond;

public class TargetPetNum extends TargetLogic {

	public TargetPetNum() {
		super(TargetCondType.PetNum);
	}

	@Override
	public int getCurValue(RoleInstance role, TargetCond cond) {
		return GameContext.getPetApp().getRolePetNumber(role.getRoleId());
	}
	
	

}
