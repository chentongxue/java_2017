package com.game.draco.app.operate.growfund;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.app.hint.vo.HintType;
import com.game.draco.app.operate.OperateActive;
import com.game.draco.app.operate.domain.RoleOperateActive;
import com.game.draco.app.operate.growfund.config.GrowFundBaseConfig;
import com.game.draco.app.operate.growfund.config.GrowFundRewardConfig;
import com.game.draco.app.operate.growfund.domain.RoleGrowFund;
import com.game.draco.app.operate.vo.OperateActiveType;
import com.game.draco.app.operate.vo.OperateAwardType;
import com.game.draco.message.item.OperateGrowFundItem;
import com.game.draco.message.request.C2452_OperateActiveDetailReqMessage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class GrowFundAppImpl implements GrowFundApp {
	
	// 日志
	private static final Logger logger = LoggerFactory.getLogger(GrowFundAppImpl.class);
	// 配置
	private GrowFundBaseConfig growFundBaseConfig = new GrowFundBaseConfig();
	private Map<Integer, GrowFundRewardConfig> growFundRewardConfigMap = Maps.newHashMap();
	// 数据
	private Map<String, RoleGrowFund> roleGrowFundMap = Maps.newConcurrentMap();

	@Override
	public void setArgs(Object arg0) {
	}

	@Override
	public void start() {
		this.init();
	}
	
	/**
	 * 初始化
	 */
	private Result init() {
		Result result = new Result();
		String xlsPath = GameContext.getPathConfig().getXlsPath();
		result = this.loadGrowFundBaseConfig(xlsPath);
		if (!result.isSuccess()) {
			logger.error(result.getInfo());
			Log4jManager.checkFail();
		}
		result = this.loadGrowFundRewardConfig(xlsPath);
		if (!result.isSuccess()) {
			logger.error(result.getInfo());
			Log4jManager.checkFail();
		}
		result = this.registerOperateActive();
		if (!result.isSuccess()) {
			logger.error(result.getInfo());
			Log4jManager.checkFail();
		}
		return result;
	}
	
	/**
	 * 加载成长基金基本配置
	 * @param xlsPath
	 */
	private Result loadGrowFundBaseConfig(String xlsPath) {
		Result result = new Result();
		String fileName = XlsSheetNameType.operate_growfund_base.getXlsName();
		String sheetName = XlsSheetNameType.operate_growfund_base.getSheetName();
		String errorInfo = "load " + fileName + "_" + sheetName + "error!";
		try {
			this.growFundBaseConfig = XlsPojoUtil.getEntity(xlsPath + fileName, sheetName, GrowFundBaseConfig.class);
			if (null == this.growFundBaseConfig) {
				result.setInfo(errorInfo);
				return result;
			}
		} catch (Exception e) {
			logger.error(errorInfo, e);
			Log4jManager.checkFail();
		}
		return result.success();
	}
	
	/**
	 * 加载成长基金奖励配置
	 * @param xlsPath
	 * @return
	 */
	private Result loadGrowFundRewardConfig(String xlsPath) {
		Result result = new Result();
		String fileName = XlsSheetNameType.operate_growfund_reward.getXlsName();
		String sheetName = XlsSheetNameType.operate_growfund_reward.getSheetName();
		String errorInfo = "load " + fileName + "_" + sheetName + "error!";
		try {
			this.growFundRewardConfigMap = XlsPojoUtil.sheetToGenericMap(xlsPath + fileName, sheetName, GrowFundRewardConfig.class);
			if (Util.isEmpty(this.growFundRewardConfigMap)) {
				result.setInfo(errorInfo);
				return result;
			}
		} catch (Exception e) {
			logger.error(errorInfo, e);
			Log4jManager.checkFail();
		}
		return result.success();
	}
	
	/**
	 * 注册运营活动
	 * @return
	 */
	private Result registerOperateActive() {
		return GameContext.getOperateActiveApp().registerOperateActive(this.getOperateActiveList(), OperateActiveType.grow_fund);
	}
	
	/**
	 * 获取模块运营活动列表
	 * @return
	 */
	private List<OperateActive> getOperateActiveList() {
		List<OperateActive> activeList = Lists.newArrayList();
		activeList.add(new GrowFundActive(this.getGrowFundBaseConfig()));
		return activeList;
	}

	@Override
	public void stop() {
	}

	@Override
	public int onCleanup(String roleId, Object context) {
		RoleGrowFund roleGrowFund = this.getRoleGrowFund(roleId);
		if (null != roleGrowFund) {
			this.roleGrowFundMap.remove(roleId);
		}
		return 0;
	}

	@Override
	public int onLogin(RoleInstance role, Object context) {
		RoleOperateActive roleOperateActive = GameContext.getBaseDAO().selectEntity(RoleOperateActive.class, RoleOperateActive.ROLEID, role.getRoleId(),
				RoleOperateActive.ACTIVEID, this.getGrowFundBaseConfig().getActiveId());
		if (null != roleOperateActive) {
			// 反序列化
			RoleGrowFund roleGrowFund = roleOperateActive.createEntity(RoleGrowFund.class);
			if (null != roleGrowFund) {
				this.roleGrowFundMap.put(role.getRoleId(), roleGrowFund);
			}
		}
		return 0;
	}

	@Override
	public int onLogout(RoleInstance role, Object context) {
		RoleGrowFund roleGrowFund = this.getRoleGrowFund(role.getRoleId());
		if (null != roleGrowFund) {
			roleGrowFund.updateDB();
			this.onCleanup(role.getRoleId(), context);
		}
		return 0;
	}

	/**
	 * 是否可领奖
	 * @param role
	 * @return
	 */
	@Override
	public boolean haveReward(RoleInstance role) {
		RoleGrowFund roleGrowFund = this.getRoleGrowFund(role.getRoleId());
		if (null != roleGrowFund) {
			for (GrowFundRewardConfig config : this.growFundRewardConfigMap.values()) {
				if (null == config) {
					continue ;
				}
				if (!roleGrowFund.isReward(config.getLevel())) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 获取成长基金基础配置
	 * @return
	 */
	private GrowFundBaseConfig getGrowFundBaseConfig() {
		return this.growFundBaseConfig;
	}
	
	/**
	 * 获取成长基金奖励配置
	 * @param level
	 * @return
	 */
	private GrowFundRewardConfig getGrowFundRewardConfig(int level) {
		return this.growFundRewardConfigMap.get(level);
	}
	
	/**
	 * 获取用户数据
	 * @param roleId
	 * @return
	 */
	@Override
	public RoleGrowFund getRoleGrowFund(String roleId) {
		return this.roleGrowFundMap.get(roleId);
	}
	
	/**
	 * 最大可获得多少钻石
	 * @return
	 */
	@Override
	public int getMaxRewardPoint() {
		if (Util.isEmpty(this.growFundRewardConfigMap)) {
			return 0;
		}
		int maxPoint = 0;
		for (GrowFundRewardConfig config : this.growFundRewardConfigMap.values()) {
			if (null == config) {
				continue ;
			}
			maxPoint += config.getValue();
		}
		return maxPoint;
	}

	/**
	 * 默认状态阶段列表
	 * @return
	 */
	@Override
	public List<OperateGrowFundItem> getOperateGrowFundList(RoleInstance role) {
		if (Util.isEmpty(this.growFundRewardConfigMap)) {
			return null;
		}
		List<OperateGrowFundItem> itemList = Lists.newArrayList();
		for (GrowFundRewardConfig config : this.growFundRewardConfigMap.values()) {
			if (null == config) {
				continue ;
			}
			OperateGrowFundItem item = new OperateGrowFundItem();
			item.setLevel((byte) config.getLevel());
			item.setValue(config.getValue());
			item.setStatus(this.getOperateGrowFundStatus(role, config.getLevel()));
			itemList.add(item);
		}
		this.sortOperateGrowFundItemList(itemList);
		return itemList;
	}
	
	/**
	 * 排序
	 * @param list
	 */
	private void sortOperateGrowFundItemList(List<OperateGrowFundItem> list) {
		Collections.sort(list, new Comparator<OperateGrowFundItem>() {
			@Override
			public int compare(OperateGrowFundItem item1, OperateGrowFundItem item2) {
				if (item1.getLevel() > item2.getLevel()) {
					return 1;
				}
				if (item1.getLevel() < item2.getLevel()) {
					return -1;
				}
				return 0;
			}
		});
	}
	
	/**
	 * 获取阶段状态
	 * @param roleId
	 * @param level
	 * @return
	 */
	private byte getOperateGrowFundStatus(RoleInstance role, int level) {
		RoleGrowFund roleGrowFund = this.getRoleGrowFund(role.getRoleId());
		if (null == roleGrowFund) {
			return OperateAwardType.default_receive.getType();
		}
		if (role.getLevel() < level) {
			return OperateAwardType.default_receive.getType();
		}
		if (roleGrowFund.isReward(level)) {
			return OperateAwardType.have_receive.getType();
		}
		return OperateAwardType.can_receive.getType();
	}

	/**
	 * 获取阶段数目
	 * @return
	 */
	@Override
	public int getStageCount() {
		if (Util.isEmpty(this.growFundRewardConfigMap)) {
			return 0;
		}
		return this.growFundRewardConfigMap.size();
	}

	/**
	 * 充值
	 * @param role
	 * @param pointValue
	 */
	@Override
	public void onPay(RoleInstance role, int pointValue) {
		// 充值金额不满足条件
		if (this.getGrowFundBaseConfig().getRechargePoint() != pointValue) {
			return ;
		}
		boolean online = false;
		RoleGrowFund roleGrowFund = null;
		if (GameContext.getOnlineCenter().isOnlineByRoleId(role.getRoleId())) {
			online = true;
			roleGrowFund = this.getRoleGrowFund(role.getRoleId());
		} else {
			RoleOperateActive roleActive = GameContext.getBaseDAO().selectEntity(RoleOperateActive.class, RoleOperateActive.ROLEID, role.getRoleId(), RoleOperateActive.ACTIVEID,
					this.getGrowFundBaseConfig().getActiveId());
			if (roleActive != null) {
				// 反序列化
				roleGrowFund = roleActive.createEntity(RoleGrowFund.class);
			}
		}
		// 如果已存在成长基金信息直接返回
		if (null != roleGrowFund) {
			return ;
		}
		roleGrowFund = RoleGrowFund.createRoleGrowFund();
		roleGrowFund.setRoleId(role.getRoleId());
		roleGrowFund.setActiveId(this.getGrowFundBaseConfig().getActiveId());
		roleGrowFund.setActiveType(OperateActiveType.grow_fund.getType());
		if (!online) {
			// 离线入库返回
			roleGrowFund.updateDB();
			return ;
		}
		this.roleGrowFundMap.put(role.getRoleId(), roleGrowFund);
		if (this.haveReward(role)) {
			GameContext.getHintApp().hintChange(role, HintType.operate, true);
		}
		// 世界广播并刷新成长基金面板
		this.broadcast(role);
	}
	
	/**
	 * 世界广播
	 */
	private void broadcast(RoleInstance role) {
		try {
			String broadcastMessage = GameContext.getI18n().messageFormat(TextId.BROAD_CAST_OPEN_FUND, role.getRoleName());
			GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Publicize_Personal, broadcastMessage, null, null);
			C2452_OperateActiveDetailReqMessage reqMsg = new C2452_OperateActiveDetailReqMessage();
			reqMsg.setActiveId(this.getGrowFundBaseConfig().getActiveId());
			role.getBehavior().addCumulateEvent(reqMsg);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	/**
	 * 领取阶段奖励
	 * @param role
	 * @param level
	 * @return
	 */
	@Override
	public Result reward(RoleInstance role, int level) {
		Result result = new Result();
		GrowFundRewardConfig rewardConfig = this.getGrowFundRewardConfig(level);
		if (null == rewardConfig) {
			return result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
		}
		RoleGrowFund roleGrowFund = this.getRoleGrowFund(role.getRoleId());
		if (null == roleGrowFund || roleGrowFund.isReward(level)) {
			return result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
		}
		roleGrowFund.reward(level);
		GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.goldMoney, OperatorType.Add, rewardConfig.getValue(), OutputConsumeType.operate_grow_fund);
		role.getBehavior().notifyAttribute();
		if (!GameContext.getOperateActiveApp().hasHint(role)) {
			GameContext.getHintApp().hintChange(role, HintType.operate, false);
		}
		if (!this.haveReward(role)) {
			GameContext.getOperateActiveApp().pushHintChange(role, this.getGrowFundBaseConfig().getActiveId(), OperateAwardType.have_receive.getType());
		}
		return result.success();
	}

	@Override
	public Result reload() {
		return this.init();
	}

}
