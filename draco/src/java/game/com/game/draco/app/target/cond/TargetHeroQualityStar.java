package com.game.draco.app.target.cond;

import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.target.config.TargetCond;

public class TargetHeroQualityStar extends TargetLogic {

	public TargetHeroQualityStar() {
		super(TargetCondType.HeroQualityStar);
	}

	@Override
	public int getCurValue(RoleInstance role, TargetCond cond) {
		return GameContext.getHeroApp().getHeroQualityStarNum(role.getRoleId(),
				Byte.parseByte(cond.getParam1()), Byte.parseByte(cond.getParam2()));
	}
	
	
}
