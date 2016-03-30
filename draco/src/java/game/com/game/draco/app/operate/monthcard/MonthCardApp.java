package com.game.draco.app.operate.monthcard;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.AppSupport;
import com.game.draco.app.operate.monthcard.config.MonthCardConfig;
import com.game.draco.app.operate.monthcard.domain.RoleMonthCard;

public interface MonthCardApp extends Service,AppSupport {
	
	/**
	 * 领取月卡福利
	 * @param role
	 * @return
	 */
	public Result receiveAwards(RoleInstance role);
	
	/**
	 * 更新用户月卡信息
	 * @param role
	 * @param rmbMoneyValue
	 */
	public void onPay(RoleInstance role, int rmbMoneyValue);
	
	/**
	 * 获取用户月卡信息
	 * @param role
	 * @return
	 */
	public RoleMonthCard getRoleMonthCard(RoleInstance role);
	
	/**
	 * 获取月卡说明
	 * @return
	 */
	public String getRoleMonthCardDesc();
	
	/**
	 * 获得月卡配置
	 * @return
	 */
	public MonthCardConfig getMonthCardConfig();
	
	/**
	 * 热加载接口
	 * @return
	 */
	public Result reLoad();
	
}
