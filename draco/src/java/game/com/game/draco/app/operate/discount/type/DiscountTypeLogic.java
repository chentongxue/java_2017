package com.game.draco.app.operate.discount.type;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.operate.discount.config.Discount;
import com.game.draco.app.operate.discount.domain.RoleDiscount;

public abstract class DiscountTypeLogic {
	private static final Logger logger = LoggerFactory.getLogger(DiscountTypeLogic.class);
	
	/**
	 * 活动计数
	 * @param role
	 * @param discount
	 * @param value
	 * @return
	 */
	public void count(RoleInstance role, Discount discount, int value) {
		try {
			// 如果不再统计时间内
			if(!discount.inCountDate()){
				return;
			}
			boolean online = GameContext.getOnlineCenter().isOnlineByRoleId(role.getRoleId());
			// 更新数据库（包括领奖状态）
			this.updateCount(role, this.getRoleDiscount(role.getRoleId(), discount.getActiveId(), online), discount, value, online);
		} catch (Exception e) {
			logger.error("DiscountTypeLogic.count error! discountId = " + discount.getActiveId(), e);
		}
	}
	
	/**
	 * 获取折扣活动数据库储存信息（如果没有则创建）
	 * @param roleId
	 * @param activeId
	 * @return
	 */
	private RoleDiscount getRoleDiscount(String roleId, int activeId, boolean online) {
		RoleDiscount roleDiscount = null;
		if (online) {
			roleDiscount = GameContext.getDiscountApp().getRoleDiscount(roleId, activeId);
		} else {
			roleDiscount = GameContext.getDiscountApp().getOfflineRoleDiscount(roleId, activeId);
		}
		// 如果没有对应的记录，创建新的对象
		if (null == roleDiscount) {
			roleDiscount = GameContext.getDiscountApp().createRoleDiscount(roleId, activeId, online);
		}
		return roleDiscount;
	}
	
	/**
	 * 判断是否在同一时间周期内
	 * @param roleDiscount
	 * @param now
	 * @return
	 */
	public abstract boolean isSameCycle(RoleDiscount discountDbInfo, Date now);
	
	/**
	 * 获取折扣活动当前值
	 * @param roleDiscount
	 * @return
	 */
	public abstract int getCurrValue(RoleDiscount roleDiscount);
	
	/**
	 * 是否可以增加领奖次数
	 * @return
	 */
	protected abstract boolean isCurCountMeet(int curCount);
	
	/**
	 * 更新数据（不同逻辑类型重写该方法）
	 * @param role
	 * @param roleDiscount
	 * @param discount
	 * @param value
	 * @return
	 */
	protected abstract void updateCount(RoleInstance role, RoleDiscount roleDiscount, Discount discount, int value, boolean online);
	
}
