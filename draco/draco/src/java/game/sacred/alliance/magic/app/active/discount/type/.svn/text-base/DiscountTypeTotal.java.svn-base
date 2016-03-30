package sacred.alliance.magic.app.active.discount.type;

import java.util.Date;

import sacred.alliance.magic.domain.DiscountDbInfo;
import sacred.alliance.magic.vo.RoleInstance;

public class DiscountTypeTotal extends DiscountTypeUpdate {

	@Override
	public boolean isSameCycle(DiscountDbInfo discountDbInfo, Date now) {
		return true;
	}

	@Override
	public int getValue(RoleInstance role, DiscountDbInfo discountDbInfo, int value) {
		return discountDbInfo.getTotalValue();
	}

	@Override
	public boolean isCurCountMeet(int curCount) {
		return (curCount == 0);
	}

}
