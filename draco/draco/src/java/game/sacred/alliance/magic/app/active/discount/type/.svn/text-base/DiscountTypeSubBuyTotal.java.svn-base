package sacred.alliance.magic.app.active.discount.type;

import sacred.alliance.magic.domain.DiscountDbInfo;
import sacred.alliance.magic.vo.RoleInstance;

/**
 *  对应折扣活动小类型的累计消费eg：累计淘宝xx元宝
 */
public class DiscountTypeSubBuyTotal extends DiscountTypeUpdate {

	@Override
	public int getValue(RoleInstance role, DiscountDbInfo discountDbInfo, int value) {
		return discountDbInfo.getTotalValue();
	}

	@Override
	public boolean isCurCountMeet(int curCount) {
		return (curCount == 0);
	}

}
