package com.game.draco.app.operate.discount.type;

import java.util.Date;

import sacred.alliance.magic.util.DateUtil;

import com.game.draco.app.operate.discount.domain.RoleDiscount;

/**
 * 每日单次充值（消费）
 * @ClassName: DiscountTypeDailyTotal
 * @Description:
 * @date 2015-3-6 下午03:19:11
 */
public class DiscountTypeDailyOnce extends DiscountTypeOnce {

	@Override
	public boolean isSameCycle(RoleDiscount roleDiscount, Date now) {
		return DateUtil.sameDay(roleDiscount.getOperateDate(), now);
	}

}
