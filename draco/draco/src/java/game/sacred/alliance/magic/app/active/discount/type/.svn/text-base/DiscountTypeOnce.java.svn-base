package sacred.alliance.magic.app.active.discount.type;

import java.util.Date;
import sacred.alliance.magic.vo.RoleInstance;

import sacred.alliance.magic.domain.DiscountDbInfo;

public class DiscountTypeOnce extends DiscountTypeUpdate {

	@Override
	public boolean isSameCycle(DiscountDbInfo discountDbInfo, Date now) {
		return true;
	}

	@Override
	public int getValue(RoleInstance role, DiscountDbInfo discountDbInfo, int value) {
		return value;
	}

	@Override
	public boolean isCurCountMeet(int curCount) {
		return true;
	}
	
	
}
