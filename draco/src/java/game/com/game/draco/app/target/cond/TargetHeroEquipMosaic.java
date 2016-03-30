package com.game.draco.app.target.cond;

import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.target.config.TargetCond;

public class TargetHeroEquipMosaic extends TargetLogic {

	public TargetHeroEquipMosaic() {
		super(TargetCondType.HeroEquipMosaic);
	}

	@Override
	public int getCurValue(RoleInstance role, TargetCond cond) {
		return GameContext.getHeroApp().getEquipMosaicLevel(role.getRoleId(), 
				Integer.parseInt(cond.getParam1()));
	}
	
	

}
