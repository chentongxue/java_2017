package sacred.alliance.magic.app.active.discount.type;

import java.util.Date;
import java.util.List;

import sacred.alliance.magic.app.active.vo.Discount;
import sacred.alliance.magic.app.active.vo.DiscountCond;
import sacred.alliance.magic.domain.DiscountDbInfo;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

/**
 * 连续xx天类型
 *
 */
public class DiscountTypeContinuousDay extends DiscountTypeUpdate {

	@Override
	public int getValue(RoleInstance role, DiscountDbInfo discountDbInfo, int value) {
		return discountDbInfo.getCurDayTotal();
	}

	@Override
	public boolean isCurCountMeet(int curCount) {
		return (0 == curCount);
	}
	
	@Override
	public boolean updateCondCount(RoleInstance role, DiscountDbInfo discountDbInfo, Discount discount, int value) {
		List<DiscountCond> condList = discount.getCondList();
		if(Util.isEmpty(condList)){
			return false;
		}
		Date now = new Date();
		int diffDay = DateUtil.dateDiffDay(discountDbInfo.getOperateDate(), now);
		if(diffDay == 0 && discountDbInfo.getCondCount(0) != 0){
			return false;
		}
		int condIndex = this.getFirstZeroDay(discount, discountDbInfo);
		if(condIndex == GET_FIRST_ZERO_DAY_INVAILD){
			return false;
		}
		DiscountCond cond = condList.get(condIndex);
		if(null == cond){
			return false;
		}
		int condCount = discountDbInfo.getCondCount(condIndex);
		if(isCurCountMeet(condCount) && cond.isMeet(condCount, value)){
			discountDbInfo.updateCondCount(condIndex);
			discountDbInfo.setOperateDate(now);
			return true ;
		}
		return false ;
	}
	
	/**
	 * 返回记录中第一个不为0的天数,
	 * @param discount
	 * @param discountDbInfo
	 * @return -1：discount==null 或者 条件计数都不等于0
	 */
	private final static int GET_FIRST_ZERO_DAY_INVAILD = -1;
	private int getFirstZeroDay(Discount discount, DiscountDbInfo discountDbInfo){
		if(null == discount){
			return GET_FIRST_ZERO_DAY_INVAILD;
		}
		int size = discount.getCondList().size();
		for(int i = 0; i < size; i++){
			if(discountDbInfo.getCondCount(i) != 0){
				continue;
			}
			return i;
		}
		return GET_FIRST_ZERO_DAY_INVAILD;
	}
	
}
