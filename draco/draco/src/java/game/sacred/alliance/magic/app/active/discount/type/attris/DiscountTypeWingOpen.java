package sacred.alliance.magic.app.active.discount.type.attris;

import sacred.alliance.magic.app.active.discount.type.DiscountTypeUpdate;
import sacred.alliance.magic.domain.DiscountDbInfo;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

public class DiscountTypeWingOpen extends DiscountTypeUpdate {

	@Override
	public int getValue(RoleInstance role, DiscountDbInfo discountDbInfo, int value) {
		RoleGoods roleGoods = role.getRoleWingGoods();
		if(null == roleGoods){
			return 0;
		}
		return (roleGoods.getWingGrids()).length;
	}

	@Override
	public boolean isCurCountMeet(int curCount) {
		return (curCount == 0);
	}

}
