package sacred.alliance.magic.app.active.discount.type;

import java.util.Date;

import sacred.alliance.magic.domain.DiscountDbInfo;
import sacred.alliance.magic.util.DateUtil;

public class DiscountTypeFirstDay extends DiscountTypeOnce {
	
	@Override
	public boolean isSameCycle(DiscountDbInfo discountDbInfo, Date now){
		return DateUtil.sameDay(discountDbInfo.getOperateDate(), new Date());
	}
	
	@Override
	public boolean isCurCountMeet(int curCount) {
		return (curCount == 0);
	}
	
}
