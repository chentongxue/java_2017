package com.game.draco.app.exchange.consumetype;

import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.union.FunType;

public class ConsumeDkp extends ConsumeLogic {
	@Override
	public int getRoleAttri(RoleInstance role) {
		return GameContext.getUnionApp().getUnionMemberDkp(role.getUnionId(),role.getIntRoleId());
	}

	@Override
	public boolean reduceRoleAttri(RoleInstance role, int value) {
		if(value <= 0) {
			return false;
		}
		GameContext.getUnionApp().changeMemberDkp(role, value, OperatorType.Decrease,FunType.exchange);
		return true;
	}
	
	@Override
	public Status getFailureStatus() {
		return Status.Exchange_UNION_DKP_NotEnough;
	}
}
