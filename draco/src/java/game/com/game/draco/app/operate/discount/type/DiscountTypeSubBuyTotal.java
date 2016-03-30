package com.game.draco.app.operate.discount.type;

import java.util.Date;

import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.operate.discount.config.Discount;
import com.game.draco.app.operate.discount.domain.RoleDiscount;

/**
 *  对应折扣活动小类型的累计消费eg：累计淘宝xx元宝
 */
public class DiscountTypeSubBuyTotal extends DiscountTypeLogic {

	@Override
	public boolean isCurCountMeet(int curCount) {
		return (curCount == 0);
	}

	@Override
	public boolean isSameCycle(RoleDiscount discountDbInfo, Date now) {
		return false;
	}

	@Override
	protected void updateCount(RoleInstance role, RoleDiscount roleDiscount, Discount discount, int value, boolean online) {
	}

	@Override
	public int getCurrValue(RoleDiscount roleDiscount) {
		// TODO Auto-generated method stub
		return 0;
	}

}
