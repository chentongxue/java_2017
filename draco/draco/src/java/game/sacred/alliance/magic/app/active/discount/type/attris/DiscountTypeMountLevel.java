package sacred.alliance.magic.app.active.discount.type.attris;

import sacred.alliance.magic.app.active.discount.type.DiscountTypeUpdate;
import sacred.alliance.magic.domain.DiscountDbInfo;
import sacred.alliance.magic.vo.RoleInstance;

public class DiscountTypeMountLevel extends DiscountTypeUpdate {

	@Override
	public int getValue(RoleInstance role, DiscountDbInfo discountDbInfo, int value) {
		return 0 ;
		/*
		RoleMount mount = role.getRoleMount();
		if(null == mount){
			return 0;
		}
		return mount.getMountLevel();
	*/}

	@Override
	public boolean isCurCountMeet(int curCount) {
		return (curCount == 0);
	}

}
