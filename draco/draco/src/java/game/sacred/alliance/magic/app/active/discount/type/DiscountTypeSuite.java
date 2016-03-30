package sacred.alliance.magic.app.active.discount.type;

import sacred.alliance.magic.domain.DiscountDbInfo;
import sacred.alliance.magic.vo.RoleInstance;

/**
 * 套餐类型
 */
public class DiscountTypeSuite extends DiscountTypeUpdate {

	@Override
	public int getValue(RoleInstance role, DiscountDbInfo discountDbInfo, int value) {
		return discountDbInfo.getTotalValue();
	}

	@Override
	public boolean isCurCountMeet(int curCount) {
		return true;
	}

}
