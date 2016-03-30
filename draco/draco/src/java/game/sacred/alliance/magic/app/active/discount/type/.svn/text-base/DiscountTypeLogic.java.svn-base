package sacred.alliance.magic.app.active.discount.type;

import java.util.Date;
import java.util.Map;

import sacred.alliance.magic.app.active.vo.Discount;
import sacred.alliance.magic.domain.DiscountDbInfo;
import sacred.alliance.magic.vo.RoleInstance;

public interface DiscountTypeLogic {
	/**
	 * 判断计数的方式
	 * @param role 在统计角色属性的时候实时去角色身上的值
	 * @param userId 在统计角色充值消费的时候用到
	 * @param discountDbInfoMap
	 * @param discountList
	 * @param value
	 */
	public boolean count(RoleInstance role, String userId, Map<Integer, DiscountDbInfo> discountDbInfoMap, Discount discountList, int value);
	
	/**
	 * 更新数据库计数
	 * @param role 在条件类型为x类型n值的时候用到
	 * @param discountDbInfo
	 * @param discountList
	 * @param value
	 */
	public boolean updateCondCount(RoleInstance role, DiscountDbInfo discountDbInfo, Discount discountList, int value);
	
	/**
	 * 判断是否在同一时间周期内
	 * @return
	 */
	public boolean isSameCycle(DiscountDbInfo discountDbInfo, Date now);
	
}
