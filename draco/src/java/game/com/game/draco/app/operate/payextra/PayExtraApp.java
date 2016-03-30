package com.game.draco.app.operate.payextra;

import java.util.Map;

import com.game.draco.app.AppSupport;
import com.game.draco.app.operate.payextra.config.PayExtraRewardConfig;
import com.game.draco.app.operate.payextra.domain.RolePayExtra;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

public interface PayExtraApp extends Service, AppSupport {
	
	/**
	 * 获得玩家充值赠送数据
	 * @param roleId
	 * @return
	 */
	public RolePayExtra getRolePayExtra(String roleId);
	
	/**
	 * 获得档位额外奖励配置
	 * @param pointValue
	 * @return
	 */
	public PayExtraRewardConfig getPayExtraRewardConfig(int pointValue);
	
	/**
	 * 充值
	 * @param role
	 * @param pointValue
	 */
	public void onPay(RoleInstance role, int pointValue);
	
	/**
	 * 热加载
	 * @return
	 */
	public Result reload();

	public Map<Integer, PayExtraRewardConfig> getPayExtraRewardConfigMap();
	
}
