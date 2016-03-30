package com.game.draco.app.target.cond;

import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.target.config.TargetCond;

public class TargetHeroEquipStrength extends TargetLogic {

	public TargetHeroEquipStrength() {
		super(TargetCondType.HeroEquipStrength);
	}

	@Override
	public int getCurValue(RoleInstance role, TargetCond cond) {
		return GameContext.getHeroApp().getEquipStrengthenLevel(role.getRoleId(),
				Integer.parseInt(cond.getParam1()));
	}
	
	
}
