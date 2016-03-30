package com.game.draco.app.exchange;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.app.quickbuy.QuickCostHelper;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.FrequencyType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.condition.Condition;
import sacred.alliance.magic.condition.ConditionType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.app.exchange.domain.ExchangeDbInfo;
import com.game.draco.app.exchange.domain.ExchangeItem;
import com.game.draco.app.exchange.domain.ExchangeMenu;
import com.game.draco.app.exchange.trade.TradeFuctionConfig;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.message.item.ExchangeChildItem;
import com.game.draco.message.item.ExchangeConditionItem;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.item.NpcFunctionItem;
import com.game.draco.message.request.C1401_ExchangeListReqMessage;
import com.game.draco.message.request.C1402_ExchangeExeReqMessage;

/**
 * 物品兑换： 数值兑换：
 */
public class ExchangeAppImpl implements ExchangeApp {
	private final static short EXCAHNE_CMDID = new C1402_ExchangeExeReqMessage().getCommandId();
	private final static String COLOR_RED = "ffFF0000";
	private final static String COND_COLOR_RED_PREFIX = "[\\C]FFFF0000[C]" ;
	private final static String COND_COLOR_GREEN_PREFIX = "[\\C]FF00FF00[C]" ;
	private final static String COLOR_END_FLAG = "[\\C]FFFFFFFF[C]" ;
	private final Logger logger = LoggerFactory.getLogger(ExchangeAppImpl.class);

	private Map<Integer, ExchangeMenu> allMenuMap = new HashMap<Integer, ExchangeMenu>();
	private Map<Integer, ExchangeItem> allItemMap = new HashMap<Integer, ExchangeItem>();
	private Map<Integer, Condition> allConditionMap = new HashMap<Integer, Condition>();

	private Map<String, List<ExchangeMenu>> npcMenuMap = new HashMap<String, List<ExchangeMenu>>();
	private Map<Integer, ExchangeMenu> menuMap = new LinkedHashMap<Integer, ExchangeMenu>();
	private Map<Short, List<ExchangeItem>> copyResetMap = new HashMap<Short, List<ExchangeItem>>();

	// 交易配置表
	private Map<Byte, TradeFuctionConfig> tradeFuctionMap = null;

	@Override
	public void setArgs(Object arg0) {

	}

	@Override
	public void start() {
		this.loadTradeFuction();
		this.loadExchangeMenu();
		this.loadExchangeItem();
		this.loadExchangeCondition();
		try {
			this.init();
		} catch (Exception ex) {
			this.checkFail("ExchangeApp init error", ex);
		}
	}

	private void loadTradeFuction() {
		tradeFuctionMap = loadConfigMap(XlsSheetNameType.trade_function, TradeFuctionConfig.class, false);
	}

	@Override
	public TradeFuctionConfig getTradeFuctionConfig(byte interId) {
		return tradeFuctionMap.get(interId);
	}

	private <K, V extends KeySupport<K>> Map<K, V> loadConfigMap(XlsSheetNameType xls, Class<V> clazz, boolean linked) {
		String fileName = xls.getXlsName();
		String sheetName = xls.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		Map<K, V> map = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, clazz, linked);
		if (Util.isEmpty(map)) {
			checkFail("not config the " + clazz.getSimpleName() + " ,file=" + sourceFile + " sheet=" + sheetName);
		}
		return map;
	}

	private void checkFail(String info) {
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}

	private void checkFail(String info, Exception ex) {
		Log4jManager.CHECK.error(info, ex);
		Log4jManager.checkFail();
	}

	@Override
	public void stop() {
	}

	private void loadExchangeMenu() {
		// 加载兑换菜单配置
		String fileName = XlsSheetNameType.exchange_menu.getXlsName();
		String sheetName = XlsSheetNameType.exchange_menu.getSheetName();
		try {
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			allMenuMap = XlsPojoUtil.sheetToGenericLinkedMap(sourceFile, sheetName, ExchangeMenu.class);
		} catch (Exception ex) {
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
		}
		if (Util.isEmpty(this.allMenuMap)) {
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("not any config: sourceFile = " + fileName + " sheetName =" + sheetName);
		}

	}

	private void loadExchangeItem() {
		// 加载兑换项
		String fileName = XlsSheetNameType.exchange_item.getXlsName();
		String sheetName = XlsSheetNameType.exchange_item.getSheetName();
		try {
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			allItemMap = XlsPojoUtil.sheetToGenericLinkedMap(sourceFile, sheetName, ExchangeItem.class);
		} catch (Exception ex) {
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
		}
		if (Util.isEmpty(this.allItemMap)) {
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("not any config: sourceFile = " + fileName + " sheetName =" + sheetName);
		}
	}

	private void loadExchangeCondition() {
		// 加载条件类项
		String fileName = XlsSheetNameType.exchange_condition.getXlsName();
		String sheetName = XlsSheetNameType.exchange_condition.getSheetName();
		try {
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			allConditionMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, Condition.class);
		} catch (Exception ex) {
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
		}
	}

	private void init() {
		// 根据条件类型来初始化条件实例
		for (Integer id : this.allConditionMap.keySet()) {
			Condition condition = this.allConditionMap.get(id);
			if (condition == null) {
				continue;
			}
			condition.getConditionTypeInstance();
			condition.init(condition.getCompareType());
		}
		// 根据兑换的条件，物品初始化实例
		for (ExchangeMenu exchangeMenu : this.allMenuMap.values()) {
			if (null == exchangeMenu) {
				continue;
			}
			String npcId = exchangeMenu.getNpcId();
			if (!Util.isEmpty(npcId)) {
				if (!npcMenuMap.containsKey(npcId)) {
					npcMenuMap.put(npcId, new ArrayList<ExchangeMenu>());
				}
				npcMenuMap.get(npcId).add(exchangeMenu);
				continue;
			}

			menuMap.put(exchangeMenu.getId(), exchangeMenu);
		}
		List<Integer> ids = new ArrayList<Integer>();
		for (ExchangeItem exchangeItem : this.allItemMap.values()) {
			if (exchangeItem == null)
				continue;
			if (!exchangeItem.init()) {
				ids.add(exchangeItem.getId());
				continue;
			}
			ExchangeMenu exchangeMenu = this.allMenuMap.get(exchangeItem.getMenuId());
			if (null == exchangeMenu) {
				continue;
			}
			String[] conditionIds = Util.splitString(exchangeItem.getConditionIds());
			for (int i = 0; i < conditionIds.length; i++) {
				int conditionId = Integer.valueOf(conditionIds[i]);
				Condition condition = this.allConditionMap.get(conditionId);
				if (null == condition) {
					Log4jManager.checkFail();
					Log4jManager.CHECK.error("ExchangeApp.init Condition error: exchangeItemId=" + exchangeItem.getId() + ",Condition = " + conditionId + " not exist");
					continue;
				}
				exchangeItem.getConditionList().add(condition);
			}
			exchangeMenu.getChildExchanges().add(exchangeItem);
			short enterResetCopyId = exchangeItem.getEnterResetCopyId();
			if (exchangeItem.getFrequencyType() == FrequencyType.FREQUENCY_TYPE_COPY.getType()) {
				if (!copyResetMap.containsKey(enterResetCopyId)) {
					copyResetMap.put(enterResetCopyId, new ArrayList<ExchangeItem>());
				}
				copyResetMap.get(enterResetCopyId).add(exchangeItem);
			}
		}
		// 删除过期的兑换
		for (Integer id : ids) {
			this.allItemMap.remove(id);
		}
	}
	
	private boolean isRefresh(ExchangeItem item) {
		List<Condition> conditionList = item.getConditionList();
		if (Util.isEmpty(conditionList)) {
			return false;
		}
		for (Condition c : conditionList) {
			ConditionType condType = ConditionType.get(c.getType());
			if (null == condType) {
				continue;
			}
			if (condType.isRefresh()) {
				return true;
			}
		}
		return false;
	}
	
	private static String getTipformat(String pattern, Object... arguments) {
		String pStr = GameContext.getI18n().getText(pattern);
		return MessageFormat.format(pStr, arguments);
	}
	
	/**
	 * 判断物品能否兑换，返回相应信息
	 * @param role
	 * @param item
	 * @return
	 */
	@Override
	public GoodsResult exchange(RoleInstance role, int exchangeId, short num, byte enterType, byte confirm) {
		GoodsResult goodsResult = new GoodsResult();
		try {
			if (num <= 0) {// 输入兑换数目为0
				String info = GameContext.getI18n().getText(TextId.Exchange_Num);
				String infoStr = MessageFormat.format(info, num);
				goodsResult.setResult(Result.FAIL);
				goodsResult.setInfo(infoStr);
				return goodsResult;
			}
			ExchangeItem item = this.getExchangeItem(exchangeId);
			if (null == item) {
				goodsResult.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
				return goodsResult;
			}
			// 是否满足兑换条件
			Result result = item.isMeet(role, num);
			if (!result.isSuccess()) {
				goodsResult.setInfo(result.getInfo());
				return goodsResult;
			}
			List<GoodsOperateBean> gainGoodsList = new ArrayList<GoodsOperateBean>();
			for (GoodsOperateBean gb : item.getGainGoodsList()) {
				GoodsOperateBean goodsBean = new GoodsOperateBean(gb.getGoodsId(), gb.getGoodsNum() * num, gb.getBindType());
				gainGoodsList.add(goodsBean);
			}
			if (!GameContext.getUserGoodsApp().canPutGoodsBean(role, gainGoodsList)) {
				goodsResult.setResult(Result.FAIL);
				goodsResult.setInfo(Status.Exchange_Backpack_Not_Enough.getTips());
				return goodsResult;
			}
			/*
			 * 如果只有属性消耗，且是钻石的，有二次面板判断
			 */
			if (item.isDiamondConusume() && confirm == 0) {
				int diamonds = item.getConsumeAttrItems().get(0).getAttriValue();
				confirm = 1;
				String confirmTips = getTipformat(TextId.SHOP_BUY_DIAMOND_CONFIRM, AttributeType.goldMoney.getName(), diamonds);
				// id,num,enterType,confirm
				Message notifyMsg = QuickCostHelper.getMessage(role, EXCAHNE_CMDID, item.getId() + "," + num + "," + enterType + "," + confirm, (short) 0,
						"", diamonds, 0, confirmTips);
				role.getBehavior().sendMessage(notifyMsg);
				goodsResult.setIgnore(true);
				return goodsResult;
			}
			if (item.isHasAttributeConsumption()) {// 含有属性消耗
				AttributeType type = item.isHasEnoughNum(role, num);
				if (null == type) {// 角色身上属性够
					item.consumeAttribute(role, num);
					role.getBehavior().notifyAttribute();
				} else {
					goodsResult.setResult(Result.FAIL);
					goodsResult.setInfo(GameContext.getI18n().messageFormat(TextId.Exchange_Attribute_Not_Enough, type.getName()));
					return goodsResult;
				}
			}
			Map<Integer, Integer> consumeGoods = new LinkedHashMap<Integer, Integer>();
			for (Map.Entry<Integer, Integer> entry : item.getConsumeGoods().entrySet()) {
				consumeGoods.put(entry.getKey(), entry.getValue() * num);
			}
			goodsResult = GameContext.getUserGoodsApp().addDelGoodsForBag(role, gainGoodsList, OutputConsumeType.goods_exchange_output, consumeGoods, OutputConsumeType.goods_exchange_consume);
			if (!goodsResult.isSuccess()) {
				return goodsResult;
			}
			// 更新用户数据
			item.updateDbInfo(role, num);
			// 喊话
			this.broadcast(role, item.getFirstConsumeGoodsId(), item.getGainGoodsId(), item.getBroadcast());

			if (this.isRefresh(item)) {
				// 需要刷新兑换List界面
				C1401_ExchangeListReqMessage listReqMsg = new C1401_ExchangeListReqMessage();
				listReqMsg.setParam(String.valueOf(item.getMenuId()));
				role.getBehavior().addCumulateEvent(listReqMsg);
			}
		} catch (Exception e) {
			this.logger.error("ExchangeAppImpl.exchange error!", e);
		}
		return goodsResult;
	}

	private void broadcast(RoleInstance role, int consumeGoodsId, int gainGoodsId, String broadcastMsg) {
		try {
			if (Util.isEmpty(broadcastMsg)) {
				return;
			}
			String consumeGoodsContent = Wildcard.getChatGoodsContent(consumeGoodsId, ChannelType.Publicize_Personal);
			String gainGoodsContent = Wildcard.getChatGoodsContent(gainGoodsId, ChannelType.Publicize_Personal);
			String message = MessageFormat.format(broadcastMsg, role.getRoleName(), consumeGoodsContent, gainGoodsContent);
			GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Publicize_Personal, message, null, null);
		} catch (Exception e) {
			logger.error("ExchangeApp.broadcast", e);
		}
	}

	@Override
	public int onLogin(RoleInstance role, Object context) {
		// 次数限制绑定类型：0：角色绑定，1：帐号绑定
		List<ExchangeDbInfo> roleExchange = GameContext.getBaseDAO().selectList(ExchangeDbInfo.class, ExchangeDbInfo.ROLE_ID, role.getRoleId());
		if (Util.isEmpty(roleExchange)) {
			return 1;
		}
		Map<Integer, ExchangeDbInfo> exchangeDbInfoMap = role.getExchangeDbInfo();
		for (ExchangeDbInfo exchangeDbInfo : roleExchange) {
			if (null == exchangeDbInfo) {
				continue;
			}
			exchangeDbInfo.setExistRecord(true);
			exchangeDbInfoMap.put(exchangeDbInfo.getId(), exchangeDbInfo);
		}
		return 1;
	}

	@Override
	public int onLogout(RoleInstance role, Object context) {
		try {
			Map<Integer, ExchangeDbInfo> exchangeDbInfoMap = role.getExchangeDbInfo();
			if (Util.isEmpty(exchangeDbInfoMap)) {
				return 1;
			}
			for (ExchangeDbInfo exchangeDbInfo : exchangeDbInfoMap.values()) {
				if (null == exchangeDbInfo) {
					continue;
				}
				ExchangeItem exchangeItem = this.getExchangeItem(exchangeDbInfo.getId());
				if (null == exchangeItem) {
					GameContext.getBaseDAO().delete(ExchangeDbInfo.class, ExchangeDbInfo.ID, exchangeDbInfo.getId(), ExchangeDbInfo.ROLE_ID, exchangeDbInfo.getRoleId());
					continue;
				}
				exchangeItem.resetExchange(exchangeDbInfo);
				if (exchangeDbInfo.isExistRecord()) {
					if (exchangeDbInfo.getTimes() <= 0) {
						GameContext.getBaseDAO().delete(ExchangeDbInfo.class, ExchangeDbInfo.ID, exchangeDbInfo.getId(), ExchangeDbInfo.ROLE_ID, exchangeDbInfo.getRoleId());
						continue;
					}
					GameContext.getBaseDAO().update(exchangeDbInfo);
					continue;
				}
				if (exchangeDbInfo.getTimes() > 0) {
					GameContext.getBaseDAO().insert(exchangeDbInfo);
				}
			}
		} catch (Exception ex) {
			GameContext.getExchangeApp().offlineLog(role);
			Log4jManager.OFFLINE_ERROR_LOG.error("exchangeApp.offline error,roleId=" + role.getRoleId() + ",userId=" + role.getUserId(), ex);
		}
		return 1;
	}

	@Override
	public int onCleanup(String roleId, Object context) {
		return 0;
	}

	@Override
	public void offlineLog(RoleInstance role) {
		try {
			Map<Integer, ExchangeDbInfo> exchangeDbInfoMap = role.getExchangeDbInfo();
			if (exchangeDbInfoMap.size() == 0) {
				return;
			}
			for (Map.Entry<Integer, ExchangeDbInfo> entry : exchangeDbInfoMap.entrySet()) {
				ExchangeDbInfo exchangeDbInfo = entry.getValue();
				if (null == exchangeDbInfo) {
					continue;
				}
				StringBuffer sb = new StringBuffer();
				sb.append(exchangeDbInfo.getId());
				sb.append(Cat.pound);
				sb.append(exchangeDbInfo.getRoleId());
				sb.append(Cat.pound);
				sb.append(exchangeDbInfo.getTimes());
				sb.append(Cat.pound);
				sb.append(DateUtil.getTimeByDate(exchangeDbInfo.getLastExTime()));
				sb.append(Cat.pound);
				Log4jManager.OFFLINE_EXCHANGE_DB_LOG.info(sb.toString());
			}
		} catch (Exception e) {
			logger.error("saveRoleExchangeLog:", e);
		}
	}

	/**
	 * 获取指定兑换
	 * 
	 * @param exchangeId
	 * @return
	 */
	private ExchangeItem getExchangeItem(int exchangeId) {
		return this.allItemMap.get(exchangeId);
	}

	@Override
	public Map<Integer, ExchangeMenu> getAllMenuMap() {
		return allMenuMap;
	}

	/**
	 * 获得基础兑换信息
	 * @param role
	 * @param exchangeItem
	 * @return
	 */
	private ExchangeChildItem getExchangeChildItem(RoleInstance role, ExchangeItem exchangeItem) {
		GoodsOperateBean goods = exchangeItem.getGainGoods();
		if (null == goods) {
			return null;
		}
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goods.getGoodsId());
		if (null == gb) {
			return null;
		}
		ExchangeChildItem exchangeChildItem = new ExchangeChildItem();
		exchangeChildItem.setId(exchangeItem.getId());
		exchangeChildItem.setName(exchangeItem.getName());
		if (exchangeItem.isTimeOpen()) {
			// 兑换日期
			if (!exchangeItem.isInDate()) {
				exchangeChildItem.setStartDate(Util.getColorString(ExchangeAppImpl.COLOR_RED, DateUtil.getDayFormat(exchangeItem.getStart())));
				exchangeChildItem.setEndDate(Util.getColorString(ExchangeAppImpl.COLOR_RED, DateUtil.getDayFormat(exchangeItem.getEnd())));
			} else {
				exchangeChildItem.setStartDate(DateUtil.getDayFormat(exchangeItem.getStart()));
				exchangeChildItem.setEndDate(DateUtil.getDayFormat(exchangeItem.getEnd()));
			}
		}
		// 至少满兑换一个
		exchangeChildItem.setCanExchange(exchangeItem.isMeet(role, (short) 1).getResult());
		// 设置消耗
		exchangeChildItem.setConsumeAttrs(exchangeItem.getConsumeAttrItems());
		exchangeChildItem.setConsumeGoods(exchangeItem.getConsumeGoodsItems());
		// 兑换次数
		int remain = exchangeItem.getFrequencyValue() - exchangeItem.getFrequencyInfo(role);
		remain = remain > 0 ? remain : 0;
		exchangeChildItem.setRemainFrequency((byte) remain);
		exchangeChildItem.setMaxFrequency(exchangeItem.getFrequencyValue());
		exchangeChildItem.setFrequencyType(exchangeItem.getFrequencyType());
		// 兑换条件
		List<ExchangeConditionItem> conditions = null;
		for (Condition condition : exchangeItem.getConditionList()) {
			if (null == condition) {
				continue;
			}
			boolean isMeet = condition.isMeet(role);
			if (null == conditions) {
				conditions = new ArrayList<ExchangeConditionItem>();
			}
			ExchangeConditionItem conditionItem = new ExchangeConditionItem();
			String condName = condition.getName();
			conditionItem.setCondition((isMeet ? COND_COLOR_GREEN_PREFIX : COND_COLOR_RED_PREFIX) + condName + COLOR_END_FLAG);
			conditions.add(conditionItem);
			exchangeChildItem.setConditions(conditions);
		}
		// 奖励
		GoodsLiteNamedItem goodItem = null;
		goodItem = gb.getGoodsLiteNamedItem();
		// 设置数目
		goodItem.setNum((short) goods.getGoodsNum());
		// 绑定类型
		goodItem.setBindType(goods.getBindType().getType());
		if (null != goodItem) {
			exchangeChildItem.setGoods(goodItem);
		}
		return exchangeChildItem;
	}

	@Override
	public List<ExchangeChildItem> getChildList(RoleInstance role, int menuId) {
		ExchangeMenu exchangeMenu = GameContext.getExchangeApp().getAllMenuMap().get(menuId);
		if (null == exchangeMenu) {
			return null;
		}
		List<ExchangeChildItem> childList = new ArrayList<ExchangeChildItem>();
		try {
			for (ExchangeItem exchangeItem : exchangeMenu.getChildExchanges()) {
				if (null == exchangeItem) {
					continue;
				}
				if (exchangeItem.isOutDate()) {
					continue;
				}
				if (!exchangeItem.isMeetConditionsAndDis(role)) {
					continue;
				}
				ExchangeChildItem exchangeChildItem = getExchangeChildItem(role, exchangeItem);
				childList.add(exchangeChildItem);
			}
		} catch (Exception e) {
			logger.error("ExchangeApp getChildList error:", e);
		}
		return childList;
	}

	@Override
	public List<NpcFunctionItem> getNpcFunction(RoleInstance role, NpcInstance npc) {
		List<NpcFunctionItem> functionList = new ArrayList<NpcFunctionItem>();
		if (Util.isEmpty(npcMenuMap)) {
			return functionList;
		}
		List<ExchangeMenu> menuList = npcMenuMap.get(npc.getNpcid());
		if (Util.isEmpty(menuList)) {
			return functionList;
		}
		for (ExchangeMenu exchangeMenu : menuList) {
			if (null == exchangeMenu) {
				return functionList;
			}
			if (exchangeMenu.getChildExchanges().size() <= 0) {
				return functionList;
			}

			NpcFunctionItem item = new NpcFunctionItem();
			item.setTitle(exchangeMenu.getName());
			item.setCommandId(ExchangeConstant.EXCHANGE_NPC_ITEM_CMD);
			String param = exchangeMenu.getId() + "";
			item.setParam(param);
			functionList.add(item);
		}
		return functionList;
	}

	@Override
	public Map<Integer, ExchangeMenu> getMenuMap() {
		return menuMap;
	}

	@Override
	public List<ExchangeMenu> getNpcMenuMap(String npcId) {
		return npcMenuMap.get(npcId);
	}

	@Override
	public void resetExchangeByCopyId(RoleInstance role, short copyId) {
		try {
			List<ExchangeItem> list = copyResetMap.get(copyId);
			if (Util.isEmpty(list)) {
				return;
			}
			for (ExchangeItem item : list) {
				if (null == item) {
					continue;
				}
				int itemId = item.getId();
				ExchangeDbInfo info = role.getExchangeDbInfo().get(itemId);
				if (null == info) {
					continue;
				}
				info.setTimes((byte) 0);
			}
		} catch (Exception e) {
			logger.error("ExchangeApp resetExchangeByType error:", e);
		}
	}

	@Override
	public String getExchangeName(int exchangeId) {
		Map<Integer, ExchangeMenu> map = getAllMenuMap();
		if (Util.isEmpty(map)) {
			return null;
		}
		ExchangeMenu em = map.get(exchangeId);
		if (em == null) {
			return null;
		}
		return em.getName();
	}

}
