package com.game.draco.app.operate.discount.type;

import java.util.Date;

import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.operate.discount.config.Discount;
import com.game.draco.app.operate.discount.domain.RoleDiscount;

/**
 * 套餐类型
 */
public class DiscountTypeSuite extends DiscountTypeLogic {

	@Override
	public boolean isCurCountMeet(int curCount) {
		return true;
	}

	@Override
	public boolean isSameCycle(RoleDiscount discountDbInfo, Date now) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void updateCount(RoleInstance role, RoleDiscount roleDiscount, Discount discount, int value, boolean online) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getCurrValue(RoleDiscount roleDiscount) {
		// TODO Auto-generated method stub
		return 0;
	}

}
