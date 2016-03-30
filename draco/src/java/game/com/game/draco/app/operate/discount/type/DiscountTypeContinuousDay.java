package com.game.draco.app.operate.discount.type;

import java.util.Date;
import java.util.List;

import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.operate.discount.config.Discount;
import com.game.draco.app.operate.discount.config.DiscountCond;
import com.game.draco.app.operate.discount.domain.RoleDiscount;

/**
 * 连续XX天充值（消费）XXX元宝
 * @ClassName: DiscountTypeContinuousDay
 * @Description: 
 * @date 2015-3-6 下午08:22:48
 */
public class DiscountTypeContinuousDay extends DiscountTypeLogic {
	
	/**
	 * 没有为0的默认值
	 */
	private final static int FIRST_ZERO_DAY = -1;

	@Override
	public boolean isCurCountMeet(int curCount) {
		return (0 == curCount);
	}
	
	/**
	 * 返回记录中第一个为0的天数,
	 * @param discount
	 * @param discountDbInfo
	 * @return -1：discount==null 或者 条件计数都不等于0
	 */
	private int getFirstZeroDay(Discount discount, RoleDiscount discountDbInfo) {
		int size = discount.getCondList().size();
		for (int i = 0; i < size; i++) {
			if (this.isCurCountMeet(discountDbInfo.getMeetCount(i))) {
				return i;
			}
		}
		return FIRST_ZERO_DAY;
	}

	@Override
	protected void updateCount(RoleInstance role, RoleDiscount roleDiscount, Discount discount, int value, boolean online) {
		// 如果没有配置条件
		List<DiscountCond> condList = discount.getCondList();
		if(Util.isEmpty(condList)){
			return;
		}
		roleDiscount.setTotalValue(roleDiscount.getTotalValue() + value);// 记录活动时间内总充值金额
		Date now = new Date();
		roleDiscount.setCurDayTotal(value, now);
		int condIndex = this.getFirstZeroDay(discount, roleDiscount);
		// 如果已全部触发
		if (FIRST_ZERO_DAY != condIndex) {
			DiscountCond cond = condList.get(condIndex);
			if (null != cond) {
				// 每天只能触发一次累计（触发累计时更改时间）
				if (!DateUtil.sameDay(this.getOperateTime(roleDiscount.getExtraInfo()), now)) {
					// 如果今天的累计值达到触发条件
					if (cond.isMeetCond(roleDiscount.getCurDayTotal())) {
						// 统计累计天数
						int continuousDay = this.getContinuousDay(roleDiscount.getExtraInfo()) + 1;
						roleDiscount.setExtraInfo(this.getExtraInfo(continuousDay, now));
						// 判断累计天数是否达到当前条件
						if (cond.isMeetParam(continuousDay)) {
							roleDiscount.updateCondCount(condIndex);
						}
					}
				}
			}
		}
		// 更新数据
		roleDiscount.setUpdateDB(true);
		roleDiscount.setOperateDate(now);
		if (!online) {
			roleDiscount.updateDB();
		}
	}
	
	/**
	 * 连续登录天数
	 * @param extraInfo
	 * @return
	 */
	private int getContinuousDay(String extraInfo) {
		if (Util.isEmpty(extraInfo)) {
			return 0;
		}
		String[] infos = Util.splitStr(extraInfo, Cat.comma);
		if (null == infos || infos.length <= 1) {
			return 0;
		}
		return Integer.parseInt(infos[0]);
	}
	
	/**
	 * 获取操作时间
	 * @param extraInfo
	 * @return
	 */
	private Date getOperateTime(String extraInfo) {
		if (Util.isEmpty(extraInfo)) {
			return null;
		}
		String[] infos = Util.splitStr(extraInfo, Cat.comma);
		if (null == infos || infos.length <= 1) {
			return null;
		}
		return new Date(Long.parseLong(infos[1]));
	}
	
	/**
	 * 获取额外字段
	 * @param continuousDay
	 * @param operateTime
	 * @return
	 */
	private String getExtraInfo(int continuousDay, Date operateTime) {
		return continuousDay + Cat.comma + operateTime.getTime();
	}

	@Override
	public boolean isSameCycle(RoleDiscount discountDbInfo, Date now) {
		return true;
	}

	@Override
	public int getCurrValue(RoleDiscount roleDiscount) {
		return this.getContinuousDay(roleDiscount.getExtraInfo());
	}
	
}
