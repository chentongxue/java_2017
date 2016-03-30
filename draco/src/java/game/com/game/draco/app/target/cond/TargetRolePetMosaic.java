package com.game.draco.app.target.cond;

import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.target.config.TargetCond;

public class TargetRolePetMosaic extends TargetLogic {

	public TargetRolePetMosaic() {
		super(TargetCondType.PetMosaic);
	}

	@Override
	public int getCurValue(RoleInstance role, TargetCond cond) {
		return GameContext.getPetApp().getMosaicLevelRuneNum(role.getRoleId(), Byte.parseByte(cond.getParam1()));
	}

}
