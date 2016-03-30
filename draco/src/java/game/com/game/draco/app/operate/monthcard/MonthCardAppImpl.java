package com.game.draco.app.operate.monthcard;

import java.util.List;
import java.util.Map;

import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.app.hint.vo.HintType;
import com.game.draco.app.operate.OperateActive;
import com.game.draco.app.operate.domain.RoleOperateActive;
import com.game.draco.app.operate.monthcard.config.MonthCardConfig;
import com.game.draco.app.operate.monthcard.domain.RoleMonthCard;
import com.game.draco.app.operate.vo.OperateActiveType;
import com.game.draco.app.operate.vo.OperateAwardType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MonthCardAppImpl implements MonthCardApp {
	
	/**
	 * 月卡有效天数
	 */
	public static final int EFFECTIVE_TIME = 30;
	private MonthCardConfig monthCardConfig = new MonthCardConfig();
	private Map<String, RoleMonthCard> roleMonthCardMap = Maps.newConcurrentMap() ;

	@Override
	public Result receiveAwards(RoleInstance role) {
		Result result = new Result();
		RoleMonthCard roleMonthCard = this.getRoleMonthCard(role);
		// 如果不具有领奖资格
		if (!this.canReceiveAwards(roleMonthCard)) {
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result;
		}
		// 领取月卡每日福利
		GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.goldMoney, OperatorType.Add, this.getMonthCardConfig().getRewardPoint(), OutputConsumeType.month_card_reward);
		role.getBehavior().notifyAttribute();
		// 更改最新领奖时间
		roleMonthCard.receiveAwards();
		// 更改状态
		GameContext.getOperateActiveApp().pushHintChange(role, this.getMonthCardConfig().getActiveId(), OperateAwardType.have_receive.getType());
		if (!GameContext.getOperateActiveApp().hasHint(role)) {
			GameContext.getHintApp().hintChange(role, HintType.operate, false);
		}
		return result.success();
	}
	
	/**
	 * 判断是否拥有领奖资格
	 * @param roleMonthCard
	 * @return
	 */
	private boolean canReceiveAwards(RoleMonthCard roleMonthCard) {
		// 如果不是月卡用户
		if (null == roleMonthCard || !roleMonthCard.isEffective()) {
			return false;
		}
		// 如果今天已领取福利
		if (roleMonthCard.getReceiveAwardsType() == OperateAwardType.have_receive.getType()) {
			return false;
		}
		return true;
	}

	@Override
	public void onPay(RoleInstance role, int rmbMoneyValue) {
		// 如果充值没达到条件
		if (rmbMoneyValue != this.getMonthCardConfig().getRechargePoint()) {
			return ;
		}
		boolean online = false;
		RoleMonthCard roleMonthCard = null;
		if (GameContext.getOnlineCenter().isOnlineByRoleId(role.getRoleId())) {
			online = true;
			roleMonthCard = this.getRoleMonthCard(role);
		} else {
			RoleOperateActive roleActive = GameContext.getBaseDAO().selectEntity(RoleOperateActive.class, RoleOperateActive.ROLEID, role.getRoleId(), RoleOperateActive.ACTIVEID, this.getMonthCardConfig().getActiveId());
			if (roleActive != null) {
				// 反序列化
				roleMonthCard = roleActive.createEntity(RoleMonthCard.class);
			}
		}
		// 如果当前是月卡用户
		if (null != roleMonthCard && roleMonthCard.isEffective()) {
			return ;
		}
		// 升级为月卡用户
		roleMonthCard = RoleMonthCard.createRoleMonthCard();
		roleMonthCard.setRoleId(role.getRoleId());
		roleMonthCard.setActiveId(this.getMonthCardConfig().getActiveId());
		roleMonthCard.setActiveType(OperateActiveType.month_card.getType());
		if (online) {
			this.roleMonthCardMap.put(role.getRoleId(), roleMonthCard);
			// 红点提示
			GameContext.getHintApp().hintChange(role, HintType.operate, true);
			// 世界广播
			String broadcastMessage = GameContext.getI18n().messageFormat(TextId.BROAD_CAST_MONTH_CARD, role.getRoleName());
			GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Publicize_Personal, broadcastMessage, null, null);
			return ;
		}
		// 入库
		roleMonthCard.updateDB();
	}
	
	@Override
	public RoleMonthCard getRoleMonthCard(RoleInstance role) {
		return this.getRoleMonthCard(role.getRoleId());
	}
	
	@Override
	public String getRoleMonthCardDesc() {
		return this.getMonthCardConfig().getActiveDesc();
	}

	@Override
	public void setArgs(Object arg0) {
	}

	@Override
	public void start() {
		this.init();
	}

	@Override
	public void stop() {
	}

	@Override
	public int onCleanup(String roleId, Object context) {
		this.roleMonthCardMap.remove(roleId);
		return 0;
	}

	@Override
	public int onLogin(RoleInstance role, Object context) {
		RoleOperateActive roleActive = GameContext.getBaseDAO().selectEntity(RoleOperateActive.class, RoleOperateActive.ROLEID, role.getRoleId(), RoleOperateActive.ACTIVEID, this.getMonthCardConfig().getActiveId());
		if (roleActive != null) {
			// 反序列化
			RoleMonthCard roleMonthCard = roleActive.createEntity(RoleMonthCard.class);
			if (null != roleMonthCard) {
				// 判断月卡是否有效
				if (roleMonthCard.isEffective()) {
					this.roleMonthCardMap.put(role.getRoleId(), roleMonthCard);
				}
			}
		}
		return 0;
	}

	@Override
	public int onLogout(RoleInstance role, Object context) {
		RoleMonthCard roleMonthCard = this.getRoleMonthCard(role.getRoleId());
		if (null != roleMonthCard) {
			roleMonthCard.updateDB();
		}
		this.onCleanup(role.getRoleId(), null);
		return 0;
	}
	
	/**
	 * 加载月卡配置
	 * @param result
	 */
	private void loadMonthCardConfig(MonthCardResult result) {
		result.failure();
		String fileName = XlsSheetNameType.operate_monthcard_base.getXlsName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		String sheetName = XlsSheetNameType.operate_monthcard_base.getSheetName();
		MonthCardConfig config = null;
		try {
			config = XlsPojoUtil.getEntity(sourceFile, sheetName, MonthCardConfig.class);
		} catch (Exception e) {
			result.setInfo("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName + ", " + e);
			return;
		}
		if (null == config) {
			result.setInfo("not any config: sourceFile = "+fileName +" sheetName ="+sheetName);
			return;
		}
		// 验证数据
		Result initResult = config.init(sourceFile);
		if (!initResult.isSuccess()) {
			result.setInfo(initResult.getInfo());
			return;
		}
		result.setMonthCardConfig(config);
		result.success();
	}
	
	private Result init() {
		Result result = new Result();
		result.failure();
		
		try {
			boolean success = true;
			MonthCardResult monthCardResult = new MonthCardResult();
			// 加载配置
			this.loadMonthCardConfig(monthCardResult);
			if (!monthCardResult.isSuccess()) {
				success = false;
				result.setInfo(result.getInfo() + "\n" + monthCardResult.getInfo());
			}
			if (!success) {
				Log4jManager.checkFail();
				Log4jManager.CHECK.error(result.getInfo());
				return result;
			}
			// 注册到运营活动模块
			Result regResult = GameContext.getOperateActiveApp().registerOperateActive(this.getOperateActiveList(monthCardResult), OperateActiveType.month_card);
			if (!regResult.isSuccess()) {
				success = false;
				result.setInfo("MonthCardApp init error," + result.getInfo() + "\n" + regResult.getInfo());
			}
			if (!success) {
				Log4jManager.checkFail();
				Log4jManager.CHECK.error(result.getInfo());
				return result;
			}
			// 更改内存配置
			this.monthCardConfig = monthCardResult.getMonthCardConfig();
		} catch (Exception e) {
			Log4jManager.CHECK.error("MonthCardAppImpl.init error!", e);
			Log4jManager.checkFail();
			return result;
		}
		return result.success();
	}
	
	/**
	 * 月卡模块活动列表
	 * @param result
	 * @return
	 */
	private List<OperateActive> getOperateActiveList(MonthCardResult result) {
		List<OperateActive> activeList = Lists.newArrayList();
		activeList.add(new MonthCardActive(result.getMonthCardConfig()));
		return activeList;
	}
	
	@Override
	public MonthCardConfig getMonthCardConfig() {
		return this.monthCardConfig;
	}
	
	private RoleMonthCard getRoleMonthCard(String roleId) {
		return this.roleMonthCardMap.get(roleId);
	}

	@Override
	public Result reLoad() {
		return this.init();
	}
	
}
