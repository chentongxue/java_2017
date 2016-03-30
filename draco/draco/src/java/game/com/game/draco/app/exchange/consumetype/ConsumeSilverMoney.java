package com.game.draco.app.exchange.consumetype;

import com.game.draco.GameContext;

import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.vo.RoleInstance;

public class ConsumeSilverMoney extends ConsumeLogic {
	@Override
	public int getRoleAttri(RoleInstance role) {
		return role.getSilverMoney();
	}

	@Override
	public boolean reduceRoleAttri(RoleInstance role, int value) {
		if(value <= 0) {
			return false;
		}
		GameContext.getUserAttributeApp().changeRoleMoney(role,
				AttributeType.silverMoney, OperatorType.Decrease,
				value, OutputConsumeType.goods_exchange_consume);
		return true;
	}
	
	@Override
	public Status getFailureStatus() {
		return Status.Exchange_Money_Not_Enough;
	}
}
