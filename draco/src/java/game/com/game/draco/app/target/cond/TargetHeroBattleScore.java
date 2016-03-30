package com.game.draco.app.target.cond;

import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.target.config.TargetCond;

public class TargetHeroBattleScore extends TargetLogic {

	public TargetHeroBattleScore() {
		super(TargetCondType.RoleBattleScore);
	}

	@Override
	public int getCurValue(RoleInstance role, TargetCond cond) {
		return GameContext.getHeroApp().getOnBattleHeroBattleScore(role.getRoleId());
	}
	
	

}
