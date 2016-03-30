package com.game.draco.app.operate.discount.type;

import java.util.Date;

import com.game.draco.app.operate.discount.domain.RoleDiscount;


public class DiscountTypeFirstActive extends DiscountTypeOnce {
	
	@Override
	public boolean isSameCycle(RoleDiscount discountDbInfo, Date now){
		return true ;
	}
	
	@Override
	public boolean isCurCountMeet(int curCount) {
		return (curCount == 0);
	}
	
}
