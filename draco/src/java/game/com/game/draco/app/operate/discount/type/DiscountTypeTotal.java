package com.game.draco.app.operate.discount.type;

import java.util.Date;
import java.util.List;

import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.operate.discount.config.Discount;
import com.game.draco.app.operate.discount.config.DiscountCond;
import com.game.draco.app.operate.discount.domain.RoleDiscount;

public class DiscountTypeTotal extends DiscountTypeLogic {

	@Override
	public boolean isSameCycle(RoleDiscount discountDbInfo, Date now) {
		return true;
	}

	@Override
	public boolean isCurCountMeet(int curCount) {
		return (0 == curCount);
	}

	@Override
	protected void updateCount(RoleInstance role, RoleDiscount roleDiscount, Discount discount, int value, boolean online) {
		List<DiscountCond> condList = discount.getCondList();
		if (Util.isEmpty(condList)) {
			return;
		}
		// 更新数据库对象
		roleDiscount.setTotalValue(roleDiscount.getTotalValue() + value);
		// 更新领奖状态
		for (int i = 0; i < condList.size(); i++) {
			DiscountCond cond = condList.get(i);
			if (null == cond) {
				continue;
			}
			int condCount = roleDiscount.getMeetCount(i);
			if (this.isCurCountMeet(condCount) && cond.isMeetParam(roleDiscount.getTotalValue())) {
				roleDiscount.updateCondCount(i);// 可领取次数加1
			}
		}
		roleDiscount.setOperateDate(new Date());
		roleDiscount.setUpdateDB(true);
		if (!online) {
			roleDiscount.updateDB();
		}
	}

	@Override
	public int getCurrValue(RoleDiscount roleDiscount) {
		return roleDiscount.getTotalValue();
	}

}
