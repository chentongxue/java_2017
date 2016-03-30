package com.game.draco.app.operate.growfund;

import java.util.List;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.AppSupport;
import com.game.draco.app.operate.growfund.domain.RoleGrowFund;
import com.game.draco.message.item.OperateGrowFundItem;

public interface GrowFundApp extends Service, AppSupport {
	
	/**
	 * 获取玩家成长基金数据
	 * @param roleId
	 * @return
	 */
	public RoleGrowFund getRoleGrowFund(String roleId);
	
	/**
	 * 最大可获得多少钻石
	 * @return
	 */
	public int getMaxRewardPoint();
	
	/**
	 * 默认状态阶段列表
	 * @return
	 */
	public List<OperateGrowFundItem> getOperateGrowFundList(RoleInstance role);
	
	/**
	 * 获取阶段数目
	 * @return
	 */
	public int getStageCount();
	
	/**
	 * 充值
	 * @param role
	 * @param pointValue
	 */
	public void onPay(RoleInstance role, int pointValue);
	
	/**
	 * 领取阶段奖励
	 * @param role
	 * @param level
	 * @return
	 */
	public Result reward(RoleInstance role, int level);
	
	/**
	 * 是否可领奖
	 * @param role
	 * @return
	 */
	public boolean haveReward(RoleInstance role);
	
	/**
	 * 热加载
	 * @return
	 */
	public Result reload();
	
}
