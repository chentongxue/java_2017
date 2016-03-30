package sacred.alliance.magic.app.active.discount.type;

import java.util.Date;
import java.util.List;
import java.util.Map;

import sacred.alliance.magic.app.active.vo.Discount;
import sacred.alliance.magic.app.active.vo.DiscountCond;
import sacred.alliance.magic.domain.DiscountDbInfo;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

public abstract class DiscountTypeUpdate implements DiscountTypeLogic {

	@Override
	public boolean count(RoleInstance role, String userId,
			Map<Integer, DiscountDbInfo> discountDbInfoMap, Discount discount, int value) {
		if(!discount.inCountDate()){
			return false;
		}
		DiscountDbInfo discountDbInfo = this.getDiscountDbInfo(userId, discountDbInfoMap, discount.getId());
		discountDbInfo.setTotalValue(discountDbInfo.getTotalValue() + value);
		discountDbInfo.setCurDayTotal(discountDbInfo.calcCurDayTotal(discount, new Date(), value));
		return updateCondCount(role, discountDbInfo, discount, getValue(role, discountDbInfo, value));
	}

	@Override
	public boolean isSameCycle(DiscountDbInfo discountDbInfo, Date now) {
		return true;
	}

	@Override
	public boolean updateCondCount(RoleInstance role, DiscountDbInfo discountDbInfo, Discount discount, int value) {
		List<DiscountCond> condList = discount.getCondList();
		if(Util.isEmpty(condList)){
			return false;
		}
		boolean setTime = false ;
		for(int i=0; i<condList.size(); i++){
			DiscountCond cond = condList.get(i);
			if(null == cond){
				continue;
			}
			int condCount = discountDbInfo.getCondCount(i);
			if(isCurCountMeet(condCount) && cond.isMeet(condCount, value)){
				setTime = true ;
				discountDbInfo.updateCondCount(i);
			}
		}
		if(setTime){
			Date now = new Date();
			discountDbInfo.setOperateDate(now);
		}
		return setTime ;
	}
	
	
	protected void updateTypeNum(Map<Byte, Byte> map, byte type, byte count){
		Byte curCount = map.get(type);
		if(null == curCount){
			map.put(type, count);
			return;
		}
		map.put(type, (byte)(curCount + count));
	}
	
	/**
	 * 服务于xx类型xx值的条件
	 * @param condList
	 * @param dbInfo
	 * @param typeNumMap
	 * @param value
	 */
	protected boolean updateCondCount(List<DiscountCond> condList, DiscountDbInfo dbInfo, Map<Byte, Byte> typeNumMap, int value){
		if(Util.isEmpty(typeNumMap)){
			return false;
		}
		boolean setTime = false ;
		for(int i=0; i<condList.size(); i++){
			DiscountCond cond = condList.get(i);
			if(null == cond){
				continue;
			}
			int condCount = dbInfo.getCondCount(i);
			byte type = (byte)(cond.getMinValue());
			Byte num = typeNumMap.get(type);
			if(null == num || num <= 0){
				continue;
			}
			if(isCurCountMeet(condCount) && cond.isMeet(condCount, num)){
				setTime = true ;
				dbInfo.updateCondCount(i);
			}
		}
		if(setTime){
			Date now = new Date();
			dbInfo.setOperateDate(now);
		}
		return setTime ;
	}
	
	/**
	 * 根据是单笔，还是累积返回不同的值
	 * @param role 在取角色属性计数的时候用到
	 * @return
	 */
	public abstract int getValue(RoleInstance role, DiscountDbInfo discountDbInfo, int value);
	
	/**
	 * 累积条件下比单笔要判断当前有没有计数值
	 * @return
	 */
	public abstract boolean isCurCountMeet(int curCount);

	protected DiscountDbInfo getDiscountDbInfo(String userId, Map<Integer, DiscountDbInfo> discountDbInfoMap, int discountId){
		DiscountDbInfo discountDbInfo = discountDbInfoMap.get(discountId);
		if(null == discountDbInfo){
			discountDbInfo = new DiscountDbInfo();
			discountDbInfo.setOperateDate(new Date());
			discountDbInfo.setActiveId(discountId);
			discountDbInfo.setUserId(userId);
			discountDbInfo.setExistRecord(false);
			discountDbInfoMap.put(discountId, discountDbInfo);
		}else{
			Date now = new Date();
			//判断是否在同一周期
			boolean isSameCycle = this.isSameCycle(discountDbInfo, now);
			if(!isSameCycle){
				//重置
				discountDbInfo.resetAllCount();
				discountDbInfo.setOperateDate(now);
			}
		}
		return discountDbInfo;
	}
}
