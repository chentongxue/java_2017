package com.game.draco.app.shopsecret;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.app.quickbuy.QuickCostHelper;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.shopsecret.config.ManualRefreshRuleConfig;
import com.game.draco.app.shopsecret.config.ShopConfig;
import com.game.draco.app.shopsecret.config.ShopSecretGoodsConfig;
import com.game.draco.app.shopsecret.config.ShopSecretPoolConfig;
import com.game.draco.app.shopsecret.config.SysRefreshRuleConfig;
import com.game.draco.app.shopsecret.domain.RoleSecretShop;
import com.game.draco.app.shopsecret.domain.ShopSecretGoodsRecord;
import com.game.draco.app.vip.type.VipPrivilegeType;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.item.NpcFunctionItem;
import com.game.draco.message.item.ShopSecretGoodsItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.push.C0009_GoldOrBindConfirmNotifyMessage;
import com.game.draco.message.request.C1619_ShopSecretRefreshReqMessage;
import com.game.draco.message.response.C1618_ShopSecretRespMessage;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public class ShopSecretAppImpl implements ShopSecretApp {
	private final short SHOP_SECRET_REFRESH_CMDID = new C1619_ShopSecretRefreshReqMessage()
			.getCommandId();
	private static Logger logger = LoggerFactory
			.getLogger(ShopSecretAppImpl.class);

	// Exel配置
	private Map<String, ShopConfig> shopConfigMap;// key shopId
	// key:shopId_times value:ManualRefreshRuleConfig
	private Map<String, ManualRefreshRuleConfig> manualRefreshRuleConfigMap;
	private Map<String, List<SysRefreshRuleConfig>> sysRefreshRuleConfigMap;
	// cache <roleId,Map<shopId,shop>>
	private Map<String, Map<String, RoleSecretShop>> roleSecretShopMap = Maps
			.newConcurrentMap();

	// oddsMap
	// 池子配置
	private Map<String, ShopSecretPoolConfig> poolConfigMap = Maps.newHashMap();
	private Map<String, ShopSecretPoolConfig> poolConfigMapTemp = Maps
			.newHashMap();
	private Multimap<String, ShopSecretPoolConfig> poolMap = ArrayListMultimap
			.create();
	// good config
	private Map<String, ShopSecretGoodsConfig> goodsConfigMap;
	private Map<String, ShopSecretGoodsConfig> goodsConfigMapTemp;

	// 热加载中介
	private Map<String, ShopConfig> shopConfigMapTemp;// key shopId
	private Map<String, ManualRefreshRuleConfig> manualRefreshRuleConfigMapTemp;
	private Map<String, List<SysRefreshRuleConfig>> sysRefreshRuleConfigMapTemp;

	@Override
	public void setArgs(Object arg0) {

	}

	@Override
	public void start() {
		this.loadAllConfig();

	}

	private void loadAllConfig() {
		reloadAll();

	}

	public Result reloadAll() {
		// goods
		this.goodsConfigMapTemp = loadGoodsConfigMap();
		this.manualRefreshRuleConfigMapTemp = loadManualRefreshRuleConfigMap();
		this.sysRefreshRuleConfigMapTemp = loadSysRefreshRuleConfigMap();
		this.shopConfigMapTemp = loadShopConfigMap();//在manualRefreshRuleConfigMapTemp后加载
		initSysRefreshTimeConfigMap(sysRefreshRuleConfigMapTemp);
		initShopConfigMap(shopConfigMapTemp);
		// pool
		this.poolConfigMapTemp = loadPoolConfigMap();
		if (Util.isEmpty(goodsConfigMapTemp)) {
			return returnReloadFailResult("goodsConfigMapTemp");
		}

		if (Util.isEmpty(poolConfigMapTemp)) {
			return returnReloadFailResult("poolConfigMapTemp");
		}

		if (Util.isEmpty(shopConfigMapTemp)) {
			return returnReloadFailResult("shopConfigMapTemp");
		}
		if (Util.isEmpty(manualRefreshRuleConfigMapTemp)) {
			return returnReloadFailResult("manualRefreshRuleConfigMapTemp");
		}
		if (Util.isEmpty(sysRefreshRuleConfigMapTemp)) {
			return returnReloadFailResult("sysRefreshRuleConfigMapTemp");
		}
		this.goodsConfigMap = goodsConfigMapTemp;
		this.shopConfigMap = shopConfigMapTemp;
		this.manualRefreshRuleConfigMap = manualRefreshRuleConfigMapTemp;
		this.sysRefreshRuleConfigMap = sysRefreshRuleConfigMapTemp;

		shopConfigMapTemp = null;
		manualRefreshRuleConfigMapTemp = null;
		sysRefreshRuleConfigMapTemp = null;

		// pool
		this.poolConfigMap = poolConfigMapTemp;
		this.poolMap = getPoolMap();
		poolConfigMapTemp = null;
		goodsConfigMapTemp = null;
		return new Result().setResult(Result.SUCCESS);
	}

	private void initShopConfigMap(Map<String, ShopConfig> shopConfigMap) {
		for(ShopConfig cf : shopConfigMap.values()){
			if(canRefresh(cf.getShopId())){
				cf.setCanRefresh(true);
			}
		}
	}

	private Multimap<String, ShopSecretPoolConfig> getPoolMap() {
		for (ShopSecretPoolConfig cf : poolConfigMap.values()) {
			if (cf == null) {
				continue;
			}
			if (getPoolGoodsNum(cf.getPoolId()) < cf.getNum()) {
				Log4jManager.CHECK
						.error("shop secret, the  specified pool has not enough goodsconfig, poolId = "
								+ cf.getPoolId()
								+ ", goodsNum = "
								+ getPoolGoodsNum(cf.getPoolId())
								+ " ,config num ="
								+ cf.getNum()
								+ " plz check shop_secret.xls->goods_config");
				Log4jManager.checkFail();
			}
			this.poolMap.put(cf.getShopId(), cf);
		}
		return poolMap;
	}

	private int getPoolGoodsNum(String poolId) {
		int num = 0;
		for (ShopSecretGoodsConfig cf : goodsConfigMap.values()) {
			if (cf == null) {
				continue;
			}
			if (poolId.equals(cf.getPoolId())) {
				num++;
			}
		}
		return num;
	}

	// chec
	private Collection<ShopSecretPoolConfig> getPoolList(String shopId) {
		return poolMap.get(shopId);
	}

	private Map<String, ShopSecretGoodsConfig> loadGoodsConfigMap() {
		return this.loadConfigMap(XlsSheetNameType.shop_secret_goods_config,
				ShopSecretGoodsConfig.class, false);
	}


	private Map<String, ShopSecretPoolConfig> loadPoolConfigMap() {
		return this.loadConfigMap(XlsSheetNameType.shop_secret_pool_config,
				ShopSecretPoolConfig.class, false);
	}

	private Result returnReloadFailResult(String loadName) {
		String info = "reload the secret shop failed, <" + loadName + ">  err";
		logger.error(info);
		return new Result().setResult(Result.FAIL).setInfo(info);
	}

	// 获得hour，minutes值，并排序
	private void initSysRefreshTimeConfigMap(
			final Map<String, List<SysRefreshRuleConfig>> sysRefreshConfigMap) {
		for (List<SysRefreshRuleConfig> list : sysRefreshConfigMap.values()) {
			for (SysRefreshRuleConfig config : list) {
				config.init();
			}
			Collections.sort(list);
		}
	}

	private Map<String, ShopConfig> loadShopConfigMap() {
		return this.loadConfigMap(XlsSheetNameType.shop_secret_shop_config,
				ShopConfig.class, false);
	}

	private Map<String, ManualRefreshRuleConfig> loadManualRefreshRuleConfigMap() {
		return this.loadConfigMap(
				XlsSheetNameType.shop_secret_manual_refresh_rule_config,
				ManualRefreshRuleConfig.class, false);
	}

	private <K, V extends KeySupport<K>> Map<K, V> loadConfigMap(
			XlsSheetNameType xls, Class<V> clazz, boolean linked) {
		String fileName = xls.getXlsName();
		String sheetName = xls.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		Map<K, V> map = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName,
				clazz, linked);
		if (Util.isEmpty(map)) {
			Log4jManager.CHECK.error("not config the " + clazz.getSimpleName()
					+ " ,file=" + sourceFile + " sheet=" + sheetName);
			Log4jManager.checkFail();
		}
		return map;
	}

	private Map<String, List<SysRefreshRuleConfig>> loadSysRefreshRuleConfigMap() {
		Map<String, List<SysRefreshRuleConfig>> rtMap = Maps.newHashMap();
		List<SysRefreshRuleConfig> list = loadConfigList(
				XlsSheetNameType.shop_secret_sys_refresh_rule_config,
				SysRefreshRuleConfig.class);
		List<SysRefreshRuleConfig> lst = null;
		for (SysRefreshRuleConfig config : list) {
			String key = config.getShopId();
			if (!rtMap.containsKey(key)) {
				lst = Lists.newArrayList();
				rtMap.put(key, lst);
			} else {
				lst = rtMap.get(key);
			}
			lst.add(config);
		}
		return rtMap;
	}

	private <T> List<T> loadConfigList(XlsSheetNameType xls, Class<T> t) {
		List<T> list = null;
		String fileName = xls.getXlsName();
		String sheetName = xls.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		try {
			list = XlsPojoUtil.sheetToList(sourceFile, sheetName, t);
		} catch (Exception e) {
			Log4jManager.CHECK
					.error("load " + t.getSimpleName() + " error:fileName="
							+ fileName + ",sheetName=" + sheetName);
			Log4jManager.checkFail();

		}
		if (list == null) {
			Log4jManager.CHECK.error("load " + t.getSimpleName()
					+ " error: result is null fileName=" + fileName
					+ ",sheetName=" + sheetName);
			Log4jManager.checkFail();
		}
		return list;
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public List<NpcFunctionItem> getNpcFunction(RoleInstance role,
			NpcInstance npc) {
		return null;
	}

	@Override
	public void saveOrUpdRoleShopSecret(RoleSecretShop roleSecretShop) {
		roleSecretShop.preToDatabase();
		GameContext.getBaseDAO().saveOrUpdate(roleSecretShop);
	}

	@Override
	public RoleSecretShop selectRoleSecretShopFromDB(String roleId,
			String shopId) {
		RoleSecretShop rs = GameContext.getBaseDAO().selectEntity(
				RoleSecretShop.class, RoleSecretShop.ROLE_ID, roleId,
				RoleSecretShop.SHOP_ID, shopId);
		if (rs == null) {
			return null;
		}
		if (rs.getData() == null) {
			return null;
		}
		rs.postFromDatabase();
		return rs;
	}

	@Override
	public int onLogin(RoleInstance role, Object context) {
		return 1;
	}

	private RoleSecretShop getNewRoleSecretShopByShopId(RoleInstance role,
			String shopId) {
		return getRefreshRoleSecretShopByShopId(role, shopId, 0);
	}

	private ShopConfig getShopConfig(String shopId){
		return shopConfigMap.get(shopId) ;
	}
	
	
	private RoleSecretShop getRefreshRoleSecretShopByShopId(RoleInstance role,
			String shopId, int alreadyRefreshTimes) {

		ShopConfig shopConfig = this.getShopConfig(shopId);
		if (shopConfig == null) {
			return null;
		}
		byte vipLevel = GameContext.getVipApp().getVipLevel(role);
		RoleSecretShop roleSecretShop = null;
		if (shopConfigFitRole(shopConfig, vipLevel)) {
			roleSecretShop = getNewRoleSecretShop(role, shopConfig,
					alreadyRefreshTimes);
			// cache
			putRoleSecretShopMap(roleSecretShop);
			// db
			saveOrUpdRoleShopSecret(roleSecretShop);
		}
		return roleSecretShop;
	}

	private void putRoleSecretShopMap(RoleSecretShop shop) {
		String roleId = shop.getRoleId();
		String shopId = shop.getShopId();
		Map<String, RoleSecretShop> shopMap = roleSecretShopMap.get(roleId);
		if (shopMap == null) {
			shopMap = Maps.newHashMap();
		}
		shopMap.put(shopId, shop);
		roleSecretShopMap.put(roleId, shopMap);
	}

	private RoleSecretShop getNewRoleSecretShop(RoleInstance role,
			ShopConfig shopConfig, int currentDayRefreshTimes) {
		Date now = new Date();

		String shopId = shopConfig.getShopId();
		// record list
		List<ShopSecretGoodsRecord> goodsRecordList = Lists.newArrayList();

		Collection<ShopSecretPoolConfig> pools = getPoolList(shopId);
		for (ShopSecretPoolConfig cf : pools) {
			int num = cf.getNum();
			// err
			Map<Integer, Integer> roleShopSecretOddsMap = getShopSecretOddsMap(
					role, shopConfig, cf.getPoolId(), goodsConfigMap);
			List<Integer> oddsList = Util.getLuckyDrawUnique(num,
					roleShopSecretOddsMap);
			if (!Util.isEmpty(oddsList)) {
				goodsRecordList = addGoodsRecordList(goodsRecordList, oddsList,
						goodsConfigMap);
			}
		}
		
		RoleSecretShop roleSecretShop = new RoleSecretShop();
		roleSecretShop.setRoleId(role.getRoleId());
		roleSecretShop.setShopId(shopId);
		roleSecretShop.setCurrentDayRefreshTimes(currentDayRefreshTimes);
		roleSecretShop.setGoodsRecordList(goodsRecordList);
		roleSecretShop.setRefreshTime(now);
		return roleSecretShop;
	}

	private List<ShopSecretGoodsRecord> addGoodsRecordList(
			List<ShopSecretGoodsRecord> goodsRecordList,
			List<Integer> oddsList,
			final Map<String, ShopSecretGoodsConfig> goodsConfigMap) {

		for (Integer goodsItemId : oddsList) {
			String key = String.valueOf(goodsItemId);
			ShopSecretGoodsConfig goodsConfig = goodsConfigMap.get(key);
			if (goodsConfig == null) {
				continue;
			}
			ShopSecretGoodsRecord record = buildShopSecretGoodsRecord(goodsConfig);
			goodsRecordList.add(record);
		}
		return goodsRecordList;
	}

	private ShopSecretGoodsRecord buildShopSecretGoodsRecord(
			ShopSecretGoodsConfig goodsConfig) {
		ShopSecretGoodsRecord record = new ShopSecretGoodsRecord();
		record.setShopItemId(goodsConfig.getShopItemId());
		record.setGoodsId(goodsConfig.getGoodsId());
		record.setNum(goodsConfig.getNum());
		record.setBind(goodsConfig.getBind());
		record.setMoneyType(goodsConfig.getPayType());
		record.setPrice(goodsConfig.getPrice());
		record.setStatus((byte) 0);
		return record;
	}

	private Map<Integer, Integer> getShopSecretOddsMap(RoleInstance role,
			ShopConfig shopConfig, String poolId,
			final Map<String, ShopSecretGoodsConfig> goodsConfigMap) {
		Map<Integer, Integer> oddsMap = Maps.newHashMap();
		for (ShopSecretGoodsConfig goodsConfig : goodsConfigMap.values()) {
			if (goodsConfig.fitRole(role)
					&& poolId.equals(goodsConfig.getPoolId())) {
				oddsMap.put(goodsConfig.getShopItemId(),
						goodsConfig.getWeight());
			}
		}
		return oddsMap;
	}

	private boolean shopConfigFitRole(final ShopConfig shopConfig, byte vipLevel) {
		if (shopConfig.getVipLevel() > vipLevel) {
			return false;
		}
		return true;
	}

	@Override
	public int onLogout(RoleInstance role, Object context) {
		// 玩家下线清除缓存
		try {
			roleSecretShopMap.remove(role.getRoleId());
		} catch (Exception ex) {
			Log4jManager.OFFLINE_ERROR_LOG.error(
					"ShopSecretApp.offline error,roleId=" + role.getRoleId()
							+ ",userId=" + role.getUserId(), ex);
			return 0;
		}
		return 1;
	}

	@Override
	public int onCleanup(String roleId, Object context) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 1618,打开神秘商店面板，系统刷新判断 判断上次刷新时间
 	 * VIP等级有可能去掉
	 */
	@Override
	public Message openShopSecretEnterRespMessage(RoleInstance role,
			String shopId) {
		String roleId = role.getRoleId();
		ShopConfig shopConfig = shopConfigMap.get(shopId);
		if (shopConfig == null) {
			String context = this.getText(TextId.ERROR_INPUT);
			return new C0003_TipNotifyMessage(context);
		}
		byte vipLevel = GameContext.getVipApp().getVipLevel(role);
		if (!shopConfigFitRole(shopConfig, vipLevel)) {
			// 提示玩家获取秘密商店失败
			String context = MessageFormat.format(
					getText(TextId.SHOP_SECRET_GET_NOT_FIT_VIPLEVEL),
					shopConfig.getShopName());
			return new C0003_TipNotifyMessage(context);
		}

		RoleSecretShop shop = getRoleSecretShop(roleId, shopId);

		// 清空刷新次数
		if (shop != null
				&& !DateUtil.sameDay(new Date(), shop.getRefreshTime())) {
			shop.setCurrentDayRefreshTimes(0);
		}

		if (shop == null) {
			shop = getNewRoleSecretShopByShopId(role, shopId);
		} else if (needRefreshShop(shop)) {
			// 获得今日刷新次数
			int alreadyRefreshTimes = shop.getCurrentDayRefreshTimes();
			shop = getRefreshRoleSecretShopByShopId(role, shopId,
					alreadyRefreshTimes);//系统刷新不扣除“刷新次数”
		}
		if (shop == null) {
			// 提示玩家获取秘密商店失败
			String context = MessageFormat.format(
					getText(TextId.SHOP_SECRET_NULL), shopId);
			return new C0003_TipNotifyMessage(context);
		}
		List<ShopSecretGoodsItem> goodsList = getShopSecretGoodsItemList(shop);

		// 获得今日刷新次数
		int toDayRefreshTimes = shop.getCurrentDayRefreshTimes();

		int freshTimes = toDayRefreshTimes + 1;
//		ManualRefreshRuleConfig manualRefreshConfig = getRefreshMoney(shopId,
//				freshTimes);
		
		ManualRefreshRuleConfig manualRefreshConfig = getManualRefreshRuleConfig(shopId,
				freshTimes);
		C1618_ShopSecretRespMessage msg = new C1618_ShopSecretRespMessage();
		if (manualRefreshConfig != null) {
			// 刷新价格
			int refreshMoney = manualRefreshConfig.getMoney();
			byte refreshMoneyType = manualRefreshConfig.getMoneyType();
			msg.setRefreshMoneyType(refreshMoneyType);
			msg.setRefreshMoney(refreshMoney);
		}
		if(shopConfig.isCanRefresh()){
			msg.setCanRefresh((byte)1);
		}
		String shopName = shopConfig.getShopName();
		String refreshTimeInfo = getRefreshTimeInfo(shopId);
		msg.setGoodsList(goodsList);
		msg.setNextRefreshTime(refreshTimeInfo);

		msg.setShopId(shopId);
		msg.setShopName(shopName);
		return msg;
	}

	private RoleSecretShop getRoleSecretShop(String roleId, String shopId) {
		Map<String, RoleSecretShop> roleShopMap = roleSecretShopMap.get(roleId);
		RoleSecretShop shop;
		if (!Util.isEmpty(roleShopMap) && roleShopMap.containsKey(shopId)) {
			shop = roleShopMap.get(shopId);
		} else {
			shop = getRoleSecretShopFromDB(roleId, shopId);
		}
		return shop;
	}

	private RoleSecretShop getRoleSecretShop(RoleInstance role, String shopId) {
		return role == null ? null
				: getRoleSecretShop(role.getRoleId(), shopId);
	}

	/**
	 *  获取上一次刷新后的下次刷新的时间，下次刷新时间比当前时间早或相等，则刷新.
	 *  (时间刚好在刷新点不刷新)
	 */ 
	private boolean needRefreshShop(RoleSecretShop shop) {
		Date nextRefreshTime = getNextRefreshTime(shop);
		if(nextRefreshTime == null){
			return false;
		}
		return System.currentTimeMillis() - nextRefreshTime.getTime() > 0;
	}

	/**
	 * 根据上次记录得到下次刷新时间
	 * @param shop
	 */
	private Date getNextRefreshTime(RoleSecretShop shop) {
		if(shop == null){
			return null;
		}
		Date shopRefreshTime = shop.getRefreshTime();// 上次刷新时间
		List<SysRefreshRuleConfig> list = sysRefreshRuleConfigMap.get(shop.getShopId());
		if(Util.isEmpty(list)){
			logger.error("shopSecretAppImpl.getNextRefreshTime() err: shopId = " + shop.getShopId() +",sys_refresh_rule_config not configured in shop_secret.xls");
			return null;
		}
		return DateUtil.getNextDate(shopRefreshTime, list);
	}

	private RoleSecretShop getRoleSecretShopFromDB(String roleId, String shopId) {
		return selectRoleSecretShopFromDB(roleId, shopId);
	}

	// 得到下一次刷新时间的说明，今日21:00 次日21:00
	private String getRefreshTimeInfo(String shopId) {
		Date now = new Date();
		List<SysRefreshRuleConfig> list = sysRefreshRuleConfigMap.get(shopId);
		if (list.size() == 1) {
			return getText(TextId.SHOP_SECRET_REFRESHTIME_EVERYDAY)
					+ list.get(0).getRefreshTime();
		}
		for (SysRefreshRuleConfig c : list) {
			if (beforeFreshTime(now, c)) {
				return getText(TextId.SHOP_SECRET_REFRESHTIME_TODAY_PRE)
						+ c.getRefreshTime();
			}
		}
		return getText(TextId.SHOP_SECRET_REFRESHTIME_TOMORROW_PRE)
				+ list.get(0).getRefreshTime();
	}

	/**
	 * @param d 验证时间
	 * @param c 参考时间
	 * @return true：d比c早，false：c与d相等或比d早
	 * @date 2014-8-12 上午10:33:07
	 */
	public boolean beforeFreshTime(Date d, SysRefreshRuleConfig c) {
		int hour = DateUtil.getHour(d);
		int minutes = DateUtil.getMinutes(d);
		if (hour != c.getHour()) {
			return hour < c.getHour();
		}
		return minutes < c.getMinute();
	}

	private String getText(String textId) {
		return GameContext.getI18n().getText(textId);
	}

	private ManualRefreshRuleConfig getRefreshMoney(String shopId,
			int freshTimes) {
		String freshKey = shopId + "_" + freshTimes;
		ManualRefreshRuleConfig manualRefreshConfig = manualRefreshRuleConfigMap
				.get(freshKey);
		return manualRefreshConfig;
	}

	private List<ShopSecretGoodsItem> getShopSecretGoodsItemList(
			RoleSecretShop shop) {
		List<ShopSecretGoodsRecord> goodsRecordList = shop.getGoodsRecordList();
		List<ShopSecretGoodsItem> goodsList = buildShopSecretGoodsItemList(goodsRecordList);
		return goodsList;
	}

	private List<ShopSecretGoodsItem> buildShopSecretGoodsItemList(
			final List<ShopSecretGoodsRecord> goodsRecordList) {
		List<ShopSecretGoodsItem> list = Lists.newArrayList();
		for (ShopSecretGoodsRecord record : goodsRecordList) {
			ShopSecretGoodsItem goodItem = buildShopSecretGoodsItem(record);
			if (goodItem != null) {
				list.add(goodItem);
			}
		}
		return list;
	}

	private ShopSecretGoodsItem buildShopSecretGoodsItem(
			ShopSecretGoodsRecord record) {
		ShopSecretGoodsItem goodItem = new ShopSecretGoodsItem();

		GoodsLiteNamedItem goodsLiteNamedItem = record.getGoodsLiteNamedItem();
		if (goodsLiteNamedItem == null) {
			return null;
		}
		int id = record.getShopItemId();
		byte moneyType = record.getMoneyType();
		int price = record.getPrice();
		byte status = record.getStatus();
		goodItem.setGoodsLiteNamedItem(goodsLiteNamedItem);
		goodItem.setId(id);
		goodItem.setMoneyType(moneyType);
		goodItem.setPrice(price);
		goodItem.setStatus(status);
		return goodItem;
	}

	@Override
	public Result refreshRoleShopSecret(RoleInstance role, String shopId,
			byte confirm) {
		Result result = new Result();
		ShopConfig shopConfig = this.getShopConfig(shopId);
		if (shopConfig == null) {
			String context = getText(TextId.SHOP_SECRET_NULL);
			result.setResult(Result.FAIL);
			result.setInfo(context);
			return result;
		}
		String roleId = role.getRoleId();
		RoleSecretShop roleSecretShop = getRoleSecretShop(roleId, shopId);
		int alreadyRefreshTimes = roleSecretShop.getCurrentDayRefreshTimes();
		//判断是否已配置刷新
		if(!shopConfig.isCanRefresh()){
			String context = getText(TextId.SHOP_SECRET_REFRESH_FORBID);
			result.setResult(Result.FAIL);
			result.setInfo(context);
			return result;
		}
		// 判断次数限制，不同的VIP刷新次数不同
		byte vipLevel = GameContext.getVipApp().getVipLevel(role);
		int limitTimes = getVipTimes(roleId, shopId) + getDeneralRefreshTimes(shopId);
		// 判断这次刷新是否在VIP的限制之内,如果超出XLS配置的VIP等级范围，取最大的次数限制
		if (alreadyRefreshTimes >= limitTimes) {
			// 提示VIP等级限制的刷新次数已经用尽
			String context = getText(TextId.SHOP_SECRET_REFRESH_FAIL_INFO);
			result.setResult(Result.FAIL);
			result.setInfo(context);
			return result;
		}
		// 判断角色自身的条件，不会出现这种情况
		if (!shopConfigFitRole(shopConfig, vipLevel)) {
			String context = getText(TextId.SHOP_SECRET_GET_NOT_FIT_VIPLEVEL);
			result.setResult(Result.FAIL);
			result.setInfo(context);
			return result;
		}

		// 判断消耗
		ManualRefreshRuleConfig ruleConfig = getManualRefreshRuleConfig(shopId,
				alreadyRefreshTimes + 1);
		if (ruleConfig == null) {
			return result.setInfo(GameContext.getI18n().getText(
					TextId.SHOP_SECRET_REFRESH_CONSUME_ERR));
		}
		int money = ruleConfig.getMoney();
		byte moneyType = ruleConfig.getMoneyType();
		AttributeType attr = AttributeType.get(moneyType);

		// 二次确认
		if (confirm == 0) {
			confirm = 1;
			String shopSecretBuyTips = getTipformat(
					TextId.SHOP_SECRET_REFRESH_CONFIRM_TIPS,
					shopConfig.getShopName(), attr.getName(), money);
			if(attr.getType() == AttributeType.goldMoney.getType()){
				Message notifyMsg = QuickCostHelper.getMessage(role,
						SHOP_SECRET_REFRESH_CMDID, shopId + "," + confirm,
						(short) 0, "", money, 0, shopSecretBuyTips);
				role.getBehavior().sendMessage(notifyMsg);
				result.setIgnore(true);
				return result;
			}
			C0009_GoldOrBindConfirmNotifyMessage confirmMsg = new C0009_GoldOrBindConfirmNotifyMessage();
			confirmMsg.setInfo(shopSecretBuyTips);
			confirmMsg.setCmdId(SHOP_SECRET_REFRESH_CMDID);
			confirmMsg.setParam(shopId + "," + confirm);
			confirmMsg.setGoldType((byte) 1);
			role.getBehavior().sendMessage(confirmMsg);
			result.setIgnore(true);
			return result;
		}
		// 【游戏币/潜能/钻石不足弹板】 判断
		Result ar = GameContext.getUserAttributeApp().getEnoughResult(role,
				attr, money);
		if (ar.isIgnore()) {
			return ar;
		}
		if (!ar.isSuccess()) {
			return ar.setInfo(getRefreshMoneyNotEnoughResult(shopConfig.getShopName(), money, attr));
		}
		consumeAttribute(role, money, attr, OutputConsumeType.shop_secret_refresh_consume);
		// 通知用户属性变化
		role.getBehavior().notifyAttribute();
		getRefreshRoleSecretShopByShopId(role, shopId, alreadyRefreshTimes + 1);
		result.setResult(Result.SUCCESS);
		return result;
	}

	private int getDeneralRefreshTimes(String shopId) {
		ShopConfig config = this.getShopConfig(shopId);
		return (null != config) ? config.getGeneralRefreshTimes() : 0;
	}

	private String getRefreshMoneyNotEnoughResult(String shopName, int expendMoney,
			AttributeType consumeType) {
		return MessageFormat.format(
				getText(TextId.SHOP_SECRET_REFRESH_MONEY_NOT_ENOUGH_INFO),
				shopName,
				consumeType.getName(), expendMoney);
	}


	private short getVipTimes(String roleId, String shopId) {
		return (short) GameContext.getVipApp().getVipPrivilegeTimes(roleId,
				VipPrivilegeType.SHOP_SECRET_TIMES.getType(), shopId);
	}

	// TO T
	private ManualRefreshRuleConfig getManualRefreshRuleConfig(String shopId,
			Integer times) {
		if(times <= 0){
			return null;
		}
		ManualRefreshRuleConfig ruleConfig;
		ruleConfig = getManualRefreshRuleConfigFromMap(shopId, times);
		if(ruleConfig == null){
			ruleConfig = getManualRefreshRuleConfig(shopId, times - 1);
		}
		return ruleConfig;
	}

	private ManualRefreshRuleConfig getManualRefreshRuleConfigFromMap(
			String shopId, Integer times) {
		String manualRefreshKey = shopId + "_" + times;
		return manualRefreshRuleConfigMap.get(manualRefreshKey);
	}
	/**
	 * 仅初始化时调用
	 */
	private boolean canRefresh(String shopId){
		for(ManualRefreshRuleConfig cf : manualRefreshRuleConfigMapTemp.values()){
			if(shopId.equals(cf.getShopId())){
				return true;
			}
		}
		return false;
	}
	// 判断是否已购买
	@Override
	public Result buy(RoleInstance role, String shopId, int id) {

		Result result = new Result().setResult(Result.FAIL);

		RoleSecretShop roleSecretShop = getRoleSecretShop(role, shopId);
		if (null == roleSecretShop) {
			return result.setInfo(GameContext.getI18n().getText(
					TextId.SHOP_SECRET_BUY_ERROR));
		}
		ShopSecretGoodsRecord record = roleSecretShop
				.getShopSecretGoodsRecord(id);

		if (null == record) {
			return result.setInfo(GameContext.getI18n().getText(
					TextId.SHOP_SECRET_BUY_ERROR));
		}

		if (!record.canBuy()) {
			return result.setInfo(GameContext.getI18n().getText(
					TextId.SHOP_SECRET_HAS_BUY));
		}

		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(
				record.getGoodsId());
		if (null == gb) {
			return result.setInfo(GameContext.getI18n().getText(
					TextId.SHOP_SECRET_BUY_ERROR));
		}
		byte moneyType = record.getMoneyType();
		int money = record.getPrice();
		int goodsNum = record.getNum();
		AttributeType attr = AttributeType.get(moneyType);

		// 【游戏币/潜能/钻石不足弹板】 判断
		Result ar = GameContext.getUserAttributeApp().getEnoughResult(role,
				attr, money);
		if (ar.isIgnore()) {// 弹板
			return ar;
		}
		if (!ar.isSuccess()) {// 不足
			return ar;
		}
		GoodsOperateBean oprbean = record.getGoodsOperateBean();
		List<GoodsOperateBean> list = Lists.newArrayList(oprbean);
		if (!GameContext.getUserGoodsApp().canPutGoodsBean(role, list)) {
			return result.setInfo(GameContext.getI18n().getText(
					TextId.SHOP_SECRET_BACKPACK_NOT_ENOUGH));
		}

		try {
			GoodsResult goodsResult = GameContext.getUserGoodsApp()
					.addGoodsBeanForBag(role, list,
							OutputConsumeType.shop_secret_goods_output);
			// 购买失败提示
			if (!goodsResult.isSuccess()) {
				return result.setInfo(goodsResult.getInfo());
			}

			// 扣除钱/dkp
			consumeAttribute(role, money, attr, OutputConsumeType.shop_secret_goods_output);

			// 购买物品日志
			GameContext.getStatLogApp().roleShopBuy(role, gb.getId(), money,
					goodsNum, money, attr,
					OutputConsumeType.shop_secret_goods_output);

			// 更改记录
			record.setStatus(ShopSecretGoodsRecord.HAS_BUY);
			// 存库
			saveOrUpdRoleShopSecret(roleSecretShop);
			// 世界广播
			this.broadcast(role, String.valueOf(id));
			result.setResult(Result.SUCCESS);
		} catch (Exception e) {
			logger.error("ShopSecretApp.buy error", e);
		}
		return result;
	}

	/**
	 * 世界广播
	 * 
	 * @param role
	 * @param shopItemId
	 */
	private void broadcast(RoleInstance role, String shopItemId) {
		try {
			ShopSecretGoodsConfig goodsConfig = this
					.getShopSecretGoodsConfig(shopItemId);
			if (null == goodsConfig) {
				return;
			}
			String broadcastInfo = goodsConfig.getBroadCastTips(role);
			if (Util.isEmpty(broadcastInfo)) {
				return;
			}
			GameContext.getChatApp().sendSysMessage(ChatSysName.System,
					ChannelType.Publicize_Personal, broadcastInfo, null, null);
		} catch (Exception e) {
			logger.error("ShopSecretAppImpl.broadcast error!", e);
		}
	}

	private ShopSecretGoodsConfig getShopSecretGoodsConfig(String shopItemId) {
		return this.goodsConfigMap.get(shopItemId);
	}

	private void consumeAttribute(RoleInstance role, int money,
			AttributeType attr, OutputConsumeType outputConsumeType) {
		GameContext.getUserAttributeApp().changeAttribute(role, attr,
					OperatorType.Decrease, money,
					outputConsumeType);
		role.getBehavior().notifyAttribute();
	}

	@Override
	public Result reLoad() {
		return reloadAll();
	}

	public static String getTipformat(String pattern, Object... arguments) {
		String pStr = GameContext.getI18n().getText(pattern);
		return MessageFormat.format(pStr, arguments);
	}
}
