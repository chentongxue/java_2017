package sacred.alliance.magic.app.active.discount.type;

import java.util.Map;

import sacred.alliance.magic.app.active.vo.Discount;
import sacred.alliance.magic.domain.DiscountDbInfo;
import sacred.alliance.magic.vo.RoleInstance;

/**
 * 活动首次充值任意金额类型,玩家只有第一次充值计入db,并且计入到totalValue中
 * 返利是totalValue为基数
 */
public class DiscountTypePayFirst extends DiscountTypeUpdate {

	@Override
	public int getValue(RoleInstance role, DiscountDbInfo discountDbInfo,
			int value) {
		return discountDbInfo.getTotalValue();
	}

	@Override
	public boolean isCurCountMeet(int curCount) {
		return (curCount == 0);
	}
	
	@Override
	public boolean count(RoleInstance role, String userId, Map<Integer, DiscountDbInfo> discountDbInfoMap, Discount discountList, int value){
		if(!discountList.inCountDate()){
			return false;
		}
		DiscountDbInfo discountDbInfo = this.getDiscountDbInfo(userId, discountDbInfoMap, discountList.getId());
		//对于首次充值返利类型(PAY_FIRST_RETURN)
		//只有totalValue==0的时候才计入充值总额中
		if(discountDbInfo.getTotalValue() != 0){
			return false;
		}
		discountDbInfo.setTotalValue(value);
		if(isCurCountMeet(discountDbInfo.getCondCount(0))){
			discountDbInfo.updateCondCount(0);
		}
		return true ;
	}
	
}
