package sacred.alliance.magic.app.active.discount.type;

import java.util.Date;

import sacred.alliance.magic.domain.DiscountDbInfo;

public class DiscountTypeFirstActive extends DiscountTypeOnce {
	
	@Override
	public boolean isSameCycle(DiscountDbInfo discountDbInfo, Date now){
		return true ;
	}
	
	@Override
	public boolean isCurCountMeet(int curCount) {
		return (curCount == 0);
	}
	
}
