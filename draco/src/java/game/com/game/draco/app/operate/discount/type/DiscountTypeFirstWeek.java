package com.game.draco.app.operate.discount.type;

import java.util.Date;

import com.game.draco.app.operate.discount.domain.RoleDiscount;

import sacred.alliance.magic.util.DateUtil;

public class DiscountTypeFirstWeek extends DiscountTypeOnce {
	
	@Override
	public boolean isSameCycle(RoleDiscount discountDbInfo, Date now){
		return DateUtil.isSameWeek(discountDbInfo.getOperateDate(), new Date());
	}
	
	@Override
	public boolean isCurCountMeet(int curCount) {
		return (curCount == 0);
	}
	
}
