package com.game.draco.app.target.cond;

import com.game.draco.GameContext;
import com.game.draco.app.target.config.TargetCond;
import sacred.alliance.magic.vo.RoleInstance;

public class TargetHeroSkillLevel extends TargetLogic {

	public TargetHeroSkillLevel() {
		super(TargetCondType.RoleHeroSkillLevel);
	}

	@Override
	public int getCurValue(RoleInstance role, TargetCond cond) {
		/*return GameContext.getUserSkillApp().getSkillLevelNum(role,
				Integer.parseInt(cond.getParam1()));*/

		return GameContext.getHeroApp().getSkillLevelNum(role.getRoleId(),
				Integer.parseInt(cond.getParam1())) ;
	}
	
	
}
