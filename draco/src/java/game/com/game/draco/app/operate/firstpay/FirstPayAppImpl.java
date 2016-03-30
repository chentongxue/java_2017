package com.game.draco.app.operate.firstpay;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.goods.GoodsHelper;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.app.menu.MenuIdType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.AttributeOperateBean;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.app.hint.vo.HintType;
import com.game.draco.app.operate.OperateActive;
import com.game.draco.app.operate.domain.RoleOperateActive;
import com.game.draco.app.operate.firstpay.config.FirstPayBaseConfig;
import com.game.draco.app.operate.firstpay.domain.RoleFirstPay;
import com.game.draco.app.operate.vo.OperateActiveType;
import com.game.draco.app.operate.vo.OperateAwardType;
import com.game.draco.app.operate.vo.OperateRewardAttributeConfig;
import com.game.draco.app.operate.vo.OperateRewardGoodsConfig;
import com.game.draco.message.item.AttriTypeValueItem;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class FirstPayAppImpl implements FirstPayApp {
	private FirstPayBaseConfig firstPayBaseConfig = new FirstPayBaseConfig();
	private Map<String, RoleFirstPay> firstPayMap = Maps.newConcurrentMap();
	private List<GoodsOperateBean> firstPayGoodsList = Lists.newArrayList();// 首冲物品奖励
	private List<AttributeOperateBean> firstPayAttributeList = Lists.newArrayList();// 首冲奖励属性

	@Override
	public List<GoodsLiteNamedItem> getFirstPayGift() {
		return GoodsHelper.getGoodsLiteNamedList(this.firstPayGoodsList);
	}

	@Override
	public byte getRoleFirstPayStatus(RoleInstance role) {
		RoleFirstPay roleFirstPay = this.getRoleFirstPay(role.getRoleId());
		if (null == roleFirstPay) {
			return OperateAwardType.default_receive.getType();
		}
		// 如果已经领取首冲奖励
		if (OperateAwardType.have_receive.getType() == roleFirstPay.getStatus()) {
			return OperateAwardType.have_receive.getType();
		}
		return OperateAwardType.can_receive.getType();
	}

	@Override
	public void onPay(RoleInstance role, int pointValue) {
		// 充值金额不足以激活首冲
		if (pointValue < this.getFirstPayBaseConfig().getMinPoint()) {
			return ;
		}
		boolean online = false;
		RoleFirstPay roleFirstPay = null;
		if (GameContext.getOnlineCenter().isOnlineByRoleId(role.getRoleId())) {
			online = true;
			roleFirstPay = this.getRoleFirstPay(role.getRoleId());
		} else {
			RoleOperateActive roleActive = GameContext.getBaseDAO().selectEntity(RoleOperateActive.class, RoleOperateActive.ROLEID, role.getRoleId(), RoleOperateActive.ACTIVEID,
					this.getFirstPayBaseConfig().getActiveId());
			if (roleActive != null) {
				// 反序列化
				roleFirstPay = roleActive.createEntity(RoleFirstPay.class);
			}
		}
		// 如果有首冲记录
		if (null != roleFirstPay) {
			return ;
		}
		roleFirstPay = RoleFirstPay.createRoleFirstPay();
		// 设置为可领取帐号首冲奖励
		roleFirstPay.setRoleId(role.getRoleId());
		roleFirstPay.setActiveId(this.getFirstPayBaseConfig().getActiveId());
		roleFirstPay.setActiveType(OperateActiveType.first_pay.getType());
		roleFirstPay.setStatus(OperateAwardType.can_receive.getType());
		if (online) {
			// 如果在线，放到内存中
			this.firstPayMap.put(role.getRoleId(), roleFirstPay);
			// 红点提示领奖
			GameContext.getHintApp().hintChange(role, HintType.firstpay, true);
			// 世界广播
			String broadcastMessage = GameContext.getI18n().messageFormat(TextId.BROAD_CAST_FIRST_PAY, role.getRoleName());
			GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Publicize_Personal, broadcastMessage, null, null);
			return ;
		}
		// 入库
		roleFirstPay.updateDB();
	}

	@Override
	public Result reLoad() {
		return this.init();
	}

	@Override
	public void setArgs(Object arg0) {
	}

	@Override
	public void start() {
		this.init();
	}

	private Result init() {
		Result result = new Result();
		result.failure();

		try {
			boolean success = true;
			FirstPayResult firstPayResult = new FirstPayResult();
			
			// 加载配置文件
			this.loadFirstPayBaseConfig(firstPayResult);
			if (!firstPayResult.isSuccess()) {
				success = false;
				result.setInfo(result.getInfo() + "\n" + firstPayResult.getInfo());
			}
			this.loadFirstPayGoodsConfig(firstPayResult);
			if (!firstPayResult.isSuccess()) {
				success = false;
				result.setInfo(result.getInfo() + "\n" + firstPayResult.getInfo());
			}
			this.loadFirstPayAttributeConfig(firstPayResult);
			if (!firstPayResult.isSuccess()) {
				success = false;
				result.setInfo(result.getInfo() + "\n" + firstPayResult.getInfo());
			}
			// 配置错误直接返回
			if (!success) {
				Log4jManager.checkFail();
				Log4jManager.CHECK.error(result.getInfo());
				return result;
			}

			// 注册活动
			Result regResult = GameContext.getOperateActiveApp().registerOperateActive(this.getOperateActiveList(firstPayResult), OperateActiveType.first_pay);
			if (!regResult.isSuccess()) {
				success = false;
				result.setInfo("FirstPayApp init error," + result.getInfo() + "\n" + regResult.getInfo());
			}
			// 加载配置失败
			if (!success) {
				Log4jManager.checkFail();
				Log4jManager.CHECK.error(result.getInfo());
				return result;
			}
			
			// 更改内存中配置
			this.firstPayBaseConfig = firstPayResult.getFirstPayBaseConfig();
			this.firstPayGoodsList = firstPayResult.getGoodsOperateBeanList();
			this.firstPayAttributeList = firstPayResult.getAttributeOperateBeanList();
		} catch (Exception e) {
			Log4jManager.CHECK.error("FirstPayAppImpl.init error!", e);
			Log4jManager.checkFail();
			return result;
		}
		result.success();
		return result;
	}

	/**
	 * 获取该模块所有的运营活动列表
	 * @param result
	 * @return
	 */
	private List<OperateActive> getOperateActiveList(FirstPayResult result) {
		List<OperateActive> activeList = Lists.newArrayList();
		activeList.add(new FirstPayActive(result.getFirstPayBaseConfig()));
		return activeList;
	}

	/**
	 * 加载基本配置
	 * @param result
	 */
	private void loadFirstPayBaseConfig(FirstPayResult result) {
		result.failure();
		String fileName = XlsSheetNameType.operate_firstpay_base.getXlsName();
		String sheetName = XlsSheetNameType.operate_firstpay_base.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		FirstPayBaseConfig baseConfig = null;
		// 读取表格
		try {
			baseConfig = XlsPojoUtil.getEntity(sourceFile, sheetName, FirstPayBaseConfig.class);
		} catch (Exception e) {
			result.setInfo("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName + ", " + e);
			return;
		}
		if (null == baseConfig) {
			result.setInfo("not any config: sourceFile = " + fileName + " sheetName =" + sheetName);
			return;
		}
		result.setFirstPayBaseConfig(baseConfig);
		result.success();
	}

	/**
	 * 加载首冲奖励物品配置
	 * @param result
	 */
	private void loadFirstPayGoodsConfig(FirstPayResult result) {
		result.failure();
		String fileName = XlsSheetNameType.operate_firstpay_goods.getXlsName();
		String sheetName = XlsSheetNameType.operate_firstpay_goods.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		List<OperateRewardGoodsConfig> goodsList = null;
		// 读取表格
		try {
			goodsList = XlsPojoUtil.sheetToList(sourceFile, sheetName, OperateRewardGoodsConfig.class);
		} catch (Exception e) {
			result.setInfo("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName + ", " + e);
			return ;
		}
		if (Util.isEmpty(goodsList)) {
			result.setInfo("not any config: sourceFile = " + fileName + " sheetName =" + sheetName);
			return ;
		}
		// 验证数据
		boolean flag = false;// 错误标志
		String fileInfo = fileName + " : " + sheetName;
		for (OperateRewardGoodsConfig config : goodsList) {
			if (null == config) {
				continue;
			}
			Result initResult = config.init(fileInfo);
			if (!initResult.isSuccess()) {
				flag = true;
				result.setInfo(initResult.getInfo());
			}
		}
		if (flag) {
			return ;
		}
		result.setFirstPayGoodsList(goodsList);
		result.success();
	}

	/**
	 * 加载首冲奖励属性配置
	 * @param result
	 */
	private void loadFirstPayAttributeConfig(FirstPayResult result) {
		result.failure();
		String fileName = XlsSheetNameType.operate_firstpay_attribute.getXlsName();
		String sheetName = XlsSheetNameType.operate_firstpay_attribute.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		List<OperateRewardAttributeConfig> attriList = null;
		// 读取表格
		try {
			attriList = XlsPojoUtil.sheetToList(sourceFile, sheetName, OperateRewardAttributeConfig.class);
		} catch (Exception e) {
			result.setInfo("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName + ", " + e);
			return;
		}
		if (Util.isEmpty(attriList)) {
			result.setInfo("not any config: sourceFile = " + fileName + " sheetName =" + sheetName);
			return;
		}
		// 验证数据
		boolean flag = false;
		String fileInfo = fileName + " : " + sheetName;
		for (OperateRewardAttributeConfig config : attriList) {
			if (null == config) {
				continue;
			}
			Result initResult = config.init(fileInfo);
			if (!initResult.isSuccess()) {
				flag = true;
				result.setInfo(initResult.getInfo());
			}
		}
		if (flag) {
			return ;
		}
		result.setFirstPayAttributeList(attriList);
		result.success();
	}

	@Override
	public void stop() {
	}

	@Override
	public FirstPayBaseConfig getFirstPayBaseConfig() {
		return this.firstPayBaseConfig;
	}

	private RoleFirstPay getRoleFirstPay(String roleId) {
		return this.firstPayMap.get(roleId);
	}

	@Override
	public int onCleanup(String roleId, Object context) {
		RoleFirstPay roleFirstPay = this.getRoleFirstPay(roleId);
		if (null != roleFirstPay) {
			this.firstPayMap.remove(roleId);
		}
		return 0;
	}

	@Override
	public int onLogin(RoleInstance role, Object context) {
		RoleOperateActive roleActive = GameContext.getBaseDAO().selectEntity(RoleOperateActive.class, RoleOperateActive.ROLEID, role.getRoleId(), RoleOperateActive.ACTIVEID,
				this.getFirstPayBaseConfig().getActiveId());
		if (null != roleActive) {
			// 反序列化
			RoleFirstPay roleFirstPay = roleActive.createEntity(RoleFirstPay.class);
			if (null != roleFirstPay) {
				this.firstPayMap.put(role.getRoleId(), roleFirstPay);
			}
		}
		return 0;
	}

	@Override
	public int onLogout(RoleInstance role, Object context) {
		RoleFirstPay roleFirstPay = this.getRoleFirstPay(role.getRoleId());
		if (null != roleFirstPay) {
			roleFirstPay.updateDB();
		}
		this.onCleanup(role.getRoleId(), context);
		return 0;
	}

	@Override
	public Result receiveAwards(RoleInstance role) {
		Result result = new Result();
		RoleFirstPay roleFirstPay = this.getRoleFirstPay(role.getRoleId());
		if (null == roleFirstPay) {
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result;
		}
		// 没有可领取奖励
		if (OperateAwardType.can_receive.getType() != roleFirstPay.getStatus()) {
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result;
		}
		// 添加物品
		GoodsResult goodsResult = GameContext.getUserGoodsApp().addGoodsBeanForBag(role, this.firstPayGoodsList, OutputConsumeType.operate_first_pay);
		if (!goodsResult.isSuccess()) {
			return goodsResult;
		}
		// 添加属性
		if (!Util.isEmpty(this.firstPayAttributeList)) {
			AttriBuffer buffer = new AttriBuffer();
			for (AttributeOperateBean operateBean : this.firstPayAttributeList) {
				if (null == operateBean) {
					continue;
				}
				if (operateBean.getAttrType().isMoney()) {
					GameContext.getUserAttributeApp().changeRoleMoney(role, operateBean.getAttrType(), OperatorType.Add, operateBean.getValue(), OutputConsumeType.operate_first_pay);
					continue;
				}
				buffer.append(operateBean.getAttrType(), operateBean.getValue(), operateBean.getPrecValue());
			}
			GameContext.getUserAttributeApp().changeAttribute(role, buffer);
			role.getBehavior().notifyAttribute();
		}
		// 更改领奖状态
		roleFirstPay.setStatus(OperateAwardType.have_receive.getType());
		roleFirstPay.setUpdateDB(true);
		// 刷新动态菜单
		GameContext.getMenuApp().refresh(MenuIdType.FirstPay);
		// 红点提示
		if (!isHaveHint(role)) {
			GameContext.getHintApp().hintChange(role, HintType.firstpay, false);
		}
		return result.success();
	}

	@Override
	public List<AttriTypeValueItem> getFirstPayAttriList() {
		if (Util.isEmpty(this.firstPayAttributeList)) {
			return null;
		}
		List<AttriTypeValueItem> list = Lists.newArrayList();
		for (AttributeOperateBean operateBean : this.firstPayAttributeList) {
			if (null == operateBean) {
				continue;
			}
			AttriTypeValueItem item = new AttriTypeValueItem();
			item.setAttriType(operateBean.getAttrType().getType());
			item.setAttriValue(operateBean.getValue());
			list.add(item);
		}
		return list;
	}

	@Override
	public boolean isShowMenu(RoleInstance role) {
		RoleFirstPay roleFirstPay = this.getRoleFirstPay(role.getRoleId());
		if (null == roleFirstPay) {
			return true;
		}
		return OperateAwardType.have_receive.getType() != roleFirstPay.getStatus();
	}

	@Override
	public Set<HintType> getHintTypeSet(RoleInstance role) {
		if (this.isHaveHint(role)) {
			Set<HintType> set = new HashSet<HintType>();
			set.add(HintType.firstpay);
			return set;
		}
		return null;
	}
	
	/**
	 * 是否红点提示
	 * @param role
	 * @return
	 */
	private boolean isHaveHint(RoleInstance role) {
		RoleFirstPay roleFirstPay = this.getRoleFirstPay(role.getRoleId());
		if (null == roleFirstPay) {
			return false;
		}
		return roleFirstPay.getStatus() == OperateAwardType.can_receive.getType();
	}

}
