package com.game.draco.app.operate.payextra;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.operate.OperateActive;
import com.game.draco.app.operate.domain.RoleOperateActive;
import com.game.draco.app.operate.payextra.config.PayExtraBaseConfig;
import com.game.draco.app.operate.payextra.config.PayExtraRewardConfig;
import com.game.draco.app.operate.payextra.domain.RolePayExtra;
import com.game.draco.app.operate.vo.OperateActiveType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class PayExtraAppImpl implements PayExtraApp {
	
	// 日志
	private static final Logger logger = LoggerFactory.getLogger(PayExtraAppImpl.class);
	// 配置
	private PayExtraBaseConfig payExtraBaseConfig = new PayExtraBaseConfig();
	private Map<Integer, PayExtraRewardConfig> payExtraRewardConfig = Maps.newHashMap();
	// 数据
	private Map<String, RolePayExtra> rolePayExtraMap = Maps.newConcurrentMap();
	
	@Override
	public void setArgs(Object arg0) {
	}

	@Override
	public void start() {
		Result result = this.init();
		if (!result.isSuccess()) {
			Log4jManager.checkFail();
			Log4jManager.CHECK.error(result.getInfo());
		}
	}
	
	/**
	 * 初始化配置
	 * @return
	 */
	private Result init() {
		PayExtraResult result = new PayExtraResult();
		try {
			String xlsPath = GameContext.getPathConfig().getXlsPath();
			this.loadPayExtraBaseConfig(result, xlsPath);
			if (!result.isSuccess()) {
				return result;
			}
			this.loadPayExtraRewardConfig(result, xlsPath);
			if (!result.isSuccess()) {
				return result;
			}
			Result regResult = GameContext.getOperateActiveApp().registerOperateActive(this.getOperateActiveList(), OperateActiveType.pay_extra);
			if (!regResult.isSuccess()) {
				return result;
			}
			this.payExtraBaseConfig = result.getPayExtraBaseConfig();
			this.payExtraRewardConfig = result.getPayExtraRewardMap();
		} catch (Exception e) {
			logger.error("PayExtraAppImpl.init error!", e);
			Log4jManager.checkFail();
			return result;
		}
		return result.success();
	}
	
	/**
	 * 获得模块运营活动列表
	 * @return
	 */
	private List<OperateActive> getOperateActiveList() {
		List<OperateActive> activeList = Lists.newArrayList();
		activeList.add(new PayExtraActive(this.getPayExtraBaseConfig()));
		return activeList;
	}
	
	/**
	 * 加载充值赠送基本配置
	 * @param xlsPath
	 */
	private void loadPayExtraBaseConfig(PayExtraResult result, String xlsPath) {
		result.failure();
		String fileName = XlsSheetNameType.operate_payextra_base.getXlsName();
		String sheetName = XlsSheetNameType.operate_payextra_base.getSheetName();
		String errorInfo = "load " + fileName + "_" + sheetName + "error!";
		try {
			PayExtraBaseConfig config = XlsPojoUtil.getEntity(xlsPath + fileName, sheetName, PayExtraBaseConfig.class);
			if (null == config) {
				result.setInfo(errorInfo);
				return ;
			}
			result.setPayExtraBaseConfig(config);
		} catch (Exception e) {
			result.setInfo(errorInfo);
			return ;
		}
		result.success();
	}
	
	/**
	 * 加载充值赠送奖励配置
	 * @param result
	 * @param xlsPath
	 */
	private void loadPayExtraRewardConfig(PayExtraResult result, String xlsPath) {
		result.failure();
		String fileName = XlsSheetNameType.operate_payextra_reward.getXlsName();
		String sheetName = XlsSheetNameType.operate_payextra_reward.getSheetName();
		String errorInfo = "load " + fileName + "_" + sheetName + "error!";
		try {
			Map<Integer, PayExtraRewardConfig> rewardMap = XlsPojoUtil.sheetToGenericMap(xlsPath + fileName, sheetName, PayExtraRewardConfig.class);
			if (Util.isEmpty(rewardMap)) {
				result.success();
				return ;
			}
			Result initResult = null;
			boolean flag = false;
			StringBuffer info = new StringBuffer();
			for (PayExtraRewardConfig config : rewardMap.values()) {
				if (null == config) {
					continue ;
				}
				initResult = config.init();
				if (!initResult.isSuccess()) {
					flag = true;
					info.append(initResult.getInfo() + "\n");
				}
			}
			if (flag) {
				result.setInfo(info.toString());
				return ;
			}
			result.setPayExtraRewardMap(rewardMap);
		} catch (Exception e) {
			result.setInfo(errorInfo);
			return ;
		}
		result.success();
	}

	@Override
	public void stop() {
	}

	@Override
	public int onCleanup(String roleId, Object context) {
		RolePayExtra rolePayExtra = this.getRolePayExtra(roleId);
		if (null != rolePayExtra) {
			this.rolePayExtraMap.remove(roleId);
		}
		return 0;
	}

	@Override
	public int onLogin(RoleInstance role, Object context) {
		RoleOperateActive roleOperateActive = GameContext.getBaseDAO().selectEntity(RoleOperateActive.class, RoleOperateActive.ROLEID, role.getRoleId(),
				RoleOperateActive.ACTIVEID, this.getPayExtraBaseConfig().getActiveId());
		if (null != roleOperateActive) {
			// 反序列化
			RolePayExtra rolePayExtra = roleOperateActive.createEntity(RolePayExtra.class);
			if (null != rolePayExtra) {
				this.rolePayExtraMap.put(role.getRoleId(), rolePayExtra);
			}
		}
		return 0;
	}

	@Override
	public int onLogout(RoleInstance role, Object context) {
		RolePayExtra rolePayExtra = this.getRolePayExtra(role.getRoleId());
		if (null != rolePayExtra) {
			rolePayExtra.updateDB();
			this.onCleanup(role.getRoleId(), context);
		}
		return 0;
	}
	
	/**
	 * 获取首冲赠送基础配置
	 * @return
	 */
	private PayExtraBaseConfig getPayExtraBaseConfig() {
		return this.payExtraBaseConfig;
	}
	/**
	 * 获取全部的额外充配置
	 */
	@Override
	public Map<Integer, PayExtraRewardConfig> getPayExtraRewardConfigMap(){
		return payExtraRewardConfig;
	}
	/**
	 * 获取档位奖励配置
	 * @param rechargePoint
	 * @return
	 */
	@Override
	public PayExtraRewardConfig getPayExtraRewardConfig(int rechargePoint) {
		if (Util.isEmpty(this.payExtraRewardConfig)) {
			return null;
		}
		return this.payExtraRewardConfig.get(rechargePoint);
	}
	
	@Override
	public RolePayExtra getRolePayExtra(String roleId) {
		if (Util.isEmpty(this.rolePayExtraMap)) {
			return null;
		}
		return this.rolePayExtraMap.get(roleId);
	}

	@Override
	public Result reload() {
		return this.init();
	}

	@Override
	public void onPay(RoleInstance role, int pointValue) {
		PayExtraRewardConfig rewardConfig = this.getPayExtraRewardConfig(pointValue);
		// 如果不是额外赠送活动不入库
		if (null == rewardConfig || rewardConfig.getExtraType() != PayExtraType.pay_extra.getType()) {
			return ;
		}
		boolean online = false;
		RolePayExtra rolePayExtra = null;
		if (GameContext.getOnlineCenter().isOnlineByRoleId(role.getRoleId())) {
			online = true;
			rolePayExtra = this.getRolePayExtra(role.getRoleId());
		} else {
			RoleOperateActive roleActive = GameContext.getBaseDAO().selectEntity(RoleOperateActive.class, RoleOperateActive.ROLEID, role.getRoleId(), RoleOperateActive.ACTIVEID,
					this.getPayExtraBaseConfig().getActiveId());
			if (roleActive != null) {
				// 反序列化
				rolePayExtra = roleActive.createEntity(RolePayExtra.class);
			}
		}
		if (null == rolePayExtra) {
			rolePayExtra = RolePayExtra.createRoleGrowFund();
			rolePayExtra.setRoleId(role.getRoleId());
			rolePayExtra.setActiveId(this.getPayExtraBaseConfig().getActiveId());
			rolePayExtra.setActiveType(OperateActiveType.pay_extra.getType());
			if (online) {
				this.rolePayExtraMap.put(role.getRoleId(), rolePayExtra);
			}
			this.giveRolePayExtraReward(role, rolePayExtra, rewardConfig, online);
			return ;
		}
		// 如果已获得过该奖励且不在重置活动内
		if (rolePayExtra.isReward(pointValue) && !rewardConfig.isReset()) {
			return ;
		}
		this.giveRolePayExtraReward(role, rolePayExtra, rewardConfig, online);
	}
	
	/**
	 * 发放充值额外奖励
	 * @param rolePayExtra
	 * @param pointValue
	 * @param online
	 */
	private void giveRolePayExtraReward(RoleInstance role, RolePayExtra rolePayExtra, PayExtraRewardConfig rewardConfig, boolean online) {
		rolePayExtra.reward(rewardConfig.getRechargePoint());
		GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.goldMoney, OperatorType.Add, rewardConfig.getRechargePoint(), OutputConsumeType.operate_pay_extra);
		if (online) {
			role.getBehavior().notifyAttribute();
			return ;
		}
		rolePayExtra.updateDB();
		GameContext.getBaseDAO().update(role);
	}
	
}
