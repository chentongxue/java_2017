package com.game.draco.app.target.cond;

import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.target.config.TargetCond;

public class TargetHeroEquipQuality extends TargetLogic {

	public TargetHeroEquipQuality() {
		super(TargetCondType.HeroEquipQuality);
	}

	@Override
	public int getCurValue(RoleInstance role, TargetCond cond) {
		
		return GameContext.getHeroApp().getEquipQualityNum(
				role.getRoleId(), Integer.parseInt(cond.getParam1()));
	}
	
	

}
