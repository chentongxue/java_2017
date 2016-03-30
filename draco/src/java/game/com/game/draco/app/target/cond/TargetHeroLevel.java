package com.game.draco.app.target.cond;

import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.target.config.TargetCond;

public class TargetHeroLevel extends TargetLogic {

	public TargetHeroLevel() {
		super(TargetCondType.HeroLevel);
	}

	@Override
	public int getCurValue(RoleInstance role, TargetCond cond) {
		int level = Integer.parseInt(cond.getParam1());
		return GameContext.getHeroApp().getHeroLevelNum(role.getRoleId(), level);
	}
	
	

}
