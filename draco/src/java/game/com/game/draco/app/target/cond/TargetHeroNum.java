package com.game.draco.app.target.cond;

import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.target.config.TargetCond;

public class TargetHeroNum extends TargetLogic {

	public TargetHeroNum() {
		super(TargetCondType.HeroNum);
	}

	@Override
	public int getCurValue(RoleInstance role, TargetCond cond) {
		return GameContext.getHeroApp().getRoleHeroNum(role.getRoleId());
	}
	
}
