package com.game.draco.app.target.cond;

import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.target.config.TargetCond;

public class TargetQuestFinish extends TargetLogic {

	public TargetQuestFinish() {
		super(TargetCondType.QuestFinish);
	}

	@Override
	public int getCurValue(RoleInstance role, TargetCond cond) {
		return role.hasFinishQuest(Integer.parseInt(cond.getParam1())) ? 1 : 0;
	}

}
