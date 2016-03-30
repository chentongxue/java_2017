package com.game.draco.app.shop;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.shop.config.ShopSecretConfig;
import com.game.draco.app.shop.config.ShopSecretRuleConfig;
import com.game.draco.app.shop.domain.RoleSecretShop;
import com.game.draco.app.shop.domain.ShopSecretResult;
import com.game.draco.app.shop.domain.ShopSecretRoleRule;
import com.game.draco.app.shop.domain.ShopSecretRule;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.item.NpcFunctionItem;
import com.game.draco.message.item.ShopSecretGoodsItem;
import com.game.draco.message.item.ShopSecretRecordItem;
import com.game.draco.message.request.C1618_ShopSecretReqMessage;
import com.game.draco.message.response.C1618_ShopSecretRespMessage;

import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.QualityType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.dao.impl.RoleShopSecretDAOImpl;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

public class ShopSecretAppImpl implements ShopSecretApp {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private List<ShopSecretRoleRule> roleRuleList = new ArrayList<ShopSecretRoleRule>();
	private Map<Integer, ShopSecretRuleConfig> ruleConfigMap = new HashMap<Integer, ShopSecretRuleConfig>();
	private Map<Integer, ShopSecretRule> shopSecretRuleMap = new HashMap<Integer, ShopSecretRule>();
	private ShopSecretConfig shopSecretConfig ;
	private final static int recordSize = 10;
	private ShopSecretRecordItem[] record = new ShopSecretRecordItem[recordSize];
	private AtomicInteger curIndex= new AtomicInteger(-1);
	private RoleShopSecretDAOImpl roleShopSecretDAO;
	private Active shopSecretActive;
	public static final short SHOP_SECRET_REQ_COMMAND= new C1618_ShopSecretReqMessage().getCommandId();
	private int openShopSecretMinLevel = 0;
	private final static int one_day_hours = 24;
	
	@Override
	public void start() {
		deleteBeforeOneDay();
		loadRoleRule();
		loadRule();
		loadShopSecretRefresh();
		loadActive();
	}
	
	/**
	 * 启动时删除一天前的记录
	 */
	private void deleteBeforeOneDay(){
		try{
			roleShopSecretDAO.deleteBeforeOneDay();
		}catch(Exception e){
			logger.error("ShopSecretApp.deleteBeforeOneDay error",e);
		}
	}
	
	private void loadRoleRule() {
		//加载配置项
		String fileName = XlsSheetNameType.shop_secret_role_rule.getXlsName();
		String sheetName = XlsSheetNameType.shop_secret_role_rule.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			roleRuleList = XlsPojoUtil.sheetToList(sourceFile, sheetName, ShopSecretRoleRule.class);
			if(Util.isEmpty(roleRuleList)) {
				return;
			}
			for(ShopSecretRoleRule rule : roleRuleList) {
				rule.init();
				if(openShopSecretMinLevel == 0 || rule.getMinRoleLevel() < openShopSecretMinLevel) {
					openShopSecretMinLevel = rule.getMinRoleLevel();
				}
			}
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
		}
	}
	
	private void loadRule() {
		//加载配置项
		String fileName = XlsSheetNameType.shop_secret_rule.getXlsName();
		String sheetName = XlsSheetNameType.shop_secret_rule.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			ruleConfigMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, ShopSecretRuleConfig.class);
			if(Util.isEmpty(ruleConfigMap)) {
				return;
			}
			for(ShopSecretRuleConfig config : ruleConfigMap.values()) {
				if(null == config) {
					continue;
				}
				config.init();
				int ruleId = config.getRuleId();
				if(!shopSecretRuleMap.containsKey(ruleId)) {
					shopSecretRuleMap.put(ruleId, new ShopSecretRule());
				}
				ShopSecretRule rule = shopSecretRuleMap.get(ruleId);
				rule.getRuleMap().put(config.getId(), config.getWeight());
				rule.setSumWeight(rule.getSumWeight() + config.getWeight());
			}
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
		}
	}
	
	private void loadShopSecretRefresh(){
		//加载配置项
		String fileName = XlsSheetNameType.shop_secret_refresh.getXlsName();
		String sheetName = XlsSheetNameType.shop_secret_refresh.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<ShopSecretConfig> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, ShopSecretConfig.class);
			if(Util.isEmpty(list)) {
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName + ",Refresh null");
			}
			shopSecretConfig = list.get(0);
			if(null == shopSecretConfig) {
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("shopSecretConfig error shopSecretConfig is null");
			}
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
		}
	}
	
	private void loadActive(){
		if(null == shopSecretConfig) {
			return;
		}
		shopSecretActive = GameContext.getActiveApp().getActive(shopSecretConfig.getActiveId());
		if(null == shopSecretActive) {
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("shopSecretActive error shopSecretActive not exist");
		}
	}
	
	@Override
	public void login(RoleInstance role) {
		try{
			RoleSecretShop roleSecretShop = GameContext.getBaseDAO().selectEntity(RoleSecretShop.class, "roleId", role.getRoleId());
			if(null == roleSecretShop){
				return;
			}
			roleSecretShop.setExistRecord(true);
			boolean flag = roleSecretShop.init();
			if(flag) {
				role.setRoleSecretShop(roleSecretShop);
			}
		}catch(Exception e){
			logger.error("ShopSecretApp.login error",e);
		}
	}
	
	@Override
	public void offlineRole(RoleInstance role) {
		try{
			RoleSecretShop roleSecretShop = role.getRoleSecretShop();
			if(null == roleSecretShop){
				return;
			}
			if(isTimeRefresh(roleSecretShop.getRefreshTime())) {
				return;
			}
			
			boolean flag = roleSecretShop.unite();
			if(!flag) {
				return;
			}
			
			if(roleSecretShop.isExistRecord()) {
				GameContext.getBaseDAO().update(roleSecretShop);
			}else{
				GameContext.getBaseDAO().insert(roleSecretShop);
			}
		}catch(Exception e){
			logger.error("ShopSecretApp.login error",e);
		}
	}
	
	private Map<Integer, Integer> getRoleSecretData(RoleInstance role) {
		try{
			RoleSecretShop roleSecretShop = role.getRoleSecretShop();
			if(null == roleSecretShop || isTimeRefresh(roleSecretShop.getRefreshTime())) {
				buildRoleSecretData(role);
			}
			roleSecretShop = role.getRoleSecretShop();
			if(null == roleSecretShop) {
				return null;
			}
			return role.getRoleSecretShop().getSecretShopMap();
		}catch(Exception e){
			logger.error("ShopSecretApp.getRoleSecretData error",e);
		}
		return null;
	}
	
	private boolean isTimeRefresh(Date refreshTime) {
		Date now = new Date();
		
		if(!DateUtil.sameDay(now, refreshTime)) {
			return true;
		}
		
		int nowHour = DateUtil.getHour(now);
		int roleHour = DateUtil.getHour(refreshTime);
		int refreshCycle = shopSecretConfig.getRefreshCycle();
		
		int nowCycle = nowHour/refreshCycle;
		int roleCycle = roleHour/refreshCycle;
		
		if(nowCycle == roleCycle) {
			return false;
		}
		return true;
	}
	
	private void buildRoleSecretData(RoleInstance role) {
		try{
			ShopSecretRoleRule rule = getShopSecretRoleRule(role);
			if(null == rule) {
				return;
			}
			RoleSecretShop roleSecretShop = role.getRoleSecretShop();
			if(null == roleSecretShop) {
				roleSecretShop = new RoleSecretShop();
			}else{
				roleSecretShop.getSecretShopMap().clear();
			}
			for(Integer ruleId : rule.getRuleMap().keySet()) {
				ShopSecretRule shopSecretRule = this.shopSecretRuleMap.get(ruleId);
				if(null == shopSecretRule) {
					continue;
				}
				int sumWeight = shopSecretRule.getSumWeight();
				Map<Integer, Integer> ruleMap = new HashMap<Integer, Integer>();
				ruleMap.putAll(shopSecretRule.getRuleMap());
				
				int count = rule.getRuleMap().get(ruleId);
				for(int i=0;i<count;i++) {
					int id = Util.getWeightCalct(ruleMap, sumWeight);
					ShopSecretRuleConfig config = ruleConfigMap.get(id);
					if(null == config) {
						continue;
					}
					roleSecretShop.getSecretShopMap().put(id, config.getGoodsNum());
					ruleMap.remove(id);
					sumWeight -= config.getWeight();
				}
			}
			roleSecretShop.setRoleId(role.getRoleId());
			roleSecretShop.setRefreshTime(new Date());
			role.setRoleSecretShop(roleSecretShop);
		}catch(Exception e){
			logger.error("ShopSecretApp.buildRoleSecretData error",e);
		}
	}
	
	private ShopSecretRoleRule getShopSecretRoleRule(RoleInstance role) {
		if(null == role){
			return null;
		}
		if(Util.isEmpty(roleRuleList)){
			return null;
		}
		for(ShopSecretRoleRule item : roleRuleList){
			if(null == item){
				continue;
			}
			if(item.isSuitLevel(role.getLevel())){
				return item;
			}
		}
		return null;
	}
	
	@Override
	public Result roleRefresh(RoleInstance role) {
		Result result = new Result();
		try{
			AttributeType attr = AttributeType.get(shopSecretConfig.getMoneyType());
			if(role.get(attr.getType()) < shopSecretConfig.getMoney()){
				String info = GameContext.getI18n().messageFormat(TextId.SHOP_SECRET_MONEY_NOT_ENOUGH, attr.getName());
				result.setInfo(info);
				return result ;
			}
			//扣除钱
			GameContext.getUserAttributeApp().changeRoleMoney(role,
					attr, OperatorType.Decrease, shopSecretConfig.getMoney(),OutputConsumeType.shop_secret_refresh);
			role.getBehavior().notifyAttribute();
			buildRoleSecretData(role);
			result.success();
		}catch(Exception e){
			logger.error("ShopSecretApp.roleRefresh error",e);
		}
		return result;
	}
	
	@Override
	public GoodsResult buy(RoleInstance role, int id) {
		GoodsResult goodsResult = new GoodsResult();
		try{
			ShopSecretResult result = canBuy(role, id);
			if(!result.isSuccess()) {
				return goodsResult.setInfo(result.getInfo());
			}
			ShopSecretRuleConfig config = result.getShopSecretRuleConfig();
			goodsResult = GameContext.getUserGoodsApp().addGoodsBeanForBag(role, config.getGoodsList(), OutputConsumeType.shop_secret_goods_output);
			if (!goodsResult.isSuccess()) {
				return goodsResult;
			}
			Map<Integer, Integer> roleSecretShopMap = role.getRoleSecretShop().getSecretShopMap();
			int freeCount = roleSecretShopMap.get(id) - 1;
			roleSecretShopMap.put(id, freeCount);
			AttributeType attrType = AttributeType.get(config.getMoneyType());
			//扣除钱
			GameContext.getUserAttributeApp().changeRoleMoney(role, attrType, 
					OperatorType.Decrease, config.getMoney(),OutputConsumeType.shop_secret_goods_output);
			role.getBehavior().notifyAttribute();
			
			GoodsBase gb = result.getGoodsBase();
			if(null == gb) {
				return goodsResult;
			}
			
			//购买物品日志
			GameContext.getStatLogApp().roleShopBuy(role, gb.getId(), config.getMoney(), 1, config.getMoney(), attrType, OutputConsumeType.shop_secret_goods_output);
			
			if(gb.getQualityType() < QualityType.blue.getType()) {
				return goodsResult;
			}
			
			int index = curIndex.incrementAndGet()%recordSize;
			ShopSecretRecordItem item = new ShopSecretRecordItem();
			item.setRoleName(role.getRoleName());
			item.setGoodsName(gb.getName());
			item.setQualityType(gb.getQualityType());
			record[index] = item;
			
		}catch(Exception e){
			logger.error("ShopSecretApp.buy error",e);
		}
		return goodsResult;
	}
	
	private ShopSecretResult canBuy(RoleInstance role, int id) {
		ShopSecretResult result = new ShopSecretResult();
		RoleSecretShop roleSecretShop = role.getRoleSecretShop();
		if(null == roleSecretShop) {
			return result.setInfo(GameContext.getI18n().getText(TextId.SHOP_SECRET_BUY_ERROR));
		}
		Map<Integer, Integer> roleSecretShopMap = roleSecretShop.getSecretShopMap();
		if(Util.isEmpty(roleSecretShopMap)) {
			return result.setInfo(GameContext.getI18n().getText(TextId.SHOP_SECRET_BUY_ERROR));
		}
		Integer freeNum = roleSecretShopMap.get(id);
		if(null == freeNum) {
			return result.setInfo(GameContext.getI18n().getText(TextId.SHOP_SECRET_BUY_ERROR));
		}
		int freeCount = freeNum.intValue();
		if(freeCount <= 0) {
			return result.setInfo(GameContext.getI18n().getText(TextId.SHOP_SECRET_FREQUENCY_NOT_ENOUGH));
		}
		
		ShopSecretRuleConfig config = this.ruleConfigMap.get(id);
		if(null == config) {
			return result.setInfo(GameContext.getI18n().getText(TextId.SHOP_SECRET_BUY_ERROR));
		}
		
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(config.getGoodsId());
		if(null == gb) {
			return result.setInfo(GameContext.getI18n().getText(TextId.SHOP_SECRET_BUY_ERROR));
		}
		
		AttributeType attr = AttributeType.get(config.getMoneyType());
		if(role.get(attr.getType()) < config.getMoney()){
			String info = GameContext.getI18n().messageFormat(TextId.SHOP_SECRET_MONEY_NOT_ENOUGH, attr.getName());
			return result.setInfo(info);
		}
		
		List<GoodsOperateBean> list = config.getGoodsList();
		if (!GameContext.getUserGoodsApp().canPutGoodsBean(role,list)) {
			return result.setInfo(GameContext.getI18n().getText(TextId.SHOP_SECRET_BACKPACK_NOT_ENOUGH));
		}
		result.setShopSecretRuleConfig(config);
		result.setGoodsBase(gb);
		result.success();
		return result;
	}
	
	@Override
	public void setArgs(Object arg0) {
	}

	@Override
	public void stop() {
	}

	public void setRoleShopSecretDAO(RoleShopSecretDAOImpl roleShopSecretDAO) {
		this.roleShopSecretDAO = roleShopSecretDAO;
	}

	@Override
	public List<NpcFunctionItem> getNpcFunction(RoleInstance role, NpcInstance npc) {
		List<NpcFunctionItem> functionList = new ArrayList<NpcFunctionItem>();
		if(role.getLevel() < openShopSecretMinLevel) {
			return functionList;
		}
		String activeNpcId = shopSecretActive.getNpcId(role.getCampId());
		if(null == activeNpcId || !activeNpcId.equals(npc.getNpcid())) {
			return functionList;
		}
		
		NpcFunctionItem item = new NpcFunctionItem();
		item.setTitle(shopSecretActive.getName());
		item.setContent(shopSecretActive.getDesc());
		item.setCommandId(SHOP_SECRET_REQ_COMMAND);
		functionList.add(item);
		return functionList;
	}
	
	@Override
	public C1618_ShopSecretRespMessage getShopSecretRespMessage(RoleInstance role) {
		C1618_ShopSecretRespMessage resp = new C1618_ShopSecretRespMessage();
		try{
			Map<Integer, Integer> roleShopSecretMap = this.getRoleSecretData(role);
			if(Util.isEmpty(roleShopSecretMap)) {
				return null;
			}
			List<ShopSecretGoodsItem> goodsList = new ArrayList<ShopSecretGoodsItem>();
			ShopSecretGoodsItem shopSecretGoodsItem ;
			for(Integer id : roleShopSecretMap.keySet()) {
				if(null == id) {
					continue;
				}
				Integer count = roleShopSecretMap.get(id);
				if(null == count) {
					continue;
				}
				int freeCount = count.intValue();
				ShopSecretRuleConfig config = this.ruleConfigMap.get(id);
				if(null == config){
					continue;
				}
				GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(config.getGoodsId());
				if(null == gb) {
					continue;
				}
				shopSecretGoodsItem = new ShopSecretGoodsItem();
				shopSecretGoodsItem.setId(id);
				GoodsLiteNamedItem goodsLiteNamedItem = gb.getGoodsLiteNamedItem();
				goodsLiteNamedItem.setNum((short)freeCount);
				goodsLiteNamedItem.setBindType(config.getBindType());
				shopSecretGoodsItem.setGoodsLiteNamedItem(goodsLiteNamedItem);
				shopSecretGoodsItem.setMoney(config.getMoney());
				shopSecretGoodsItem.setMoneyType(config.getMoneyType());
				goodsList.add(shopSecretGoodsItem);
			}
			resp.setGoodsList(goodsList);
			
			List<ShopSecretRecordItem> recordList = new ArrayList<ShopSecretRecordItem>();
			for(int i=0;i<record.length;i++) {
				ShopSecretRecordItem item = record[i];
				if(null == item) {
					continue;
				}
				recordList.add(item);
			}
			resp.setRecordList(recordList);
			resp.setRefreshMoney(shopSecretConfig.getMoney());
			resp.setRefreshMoneyType(shopSecretConfig.getMoneyType());
			
			resp.setRefreshTime((byte)shopSecretConfig.getRefreshCycle());
			
			int remainTime = getRemainTime(role.getRoleSecretShop().getRefreshTime());
			resp.setRemainTime(remainTime);
		}catch(Exception e){
			logger.error("ShopSecretApp.getShopSecretRespMessage error",e);
		}
		return resp;
	}
	
	private int getRemainTime(Date roleRefreshTime) {
		Date now = new Date();
		int nextRefreshHour = getRefreshTime(roleRefreshTime, now);
		Date nextResfreshDate = DateUtil.setDate(now, nextRefreshHour, 0, 5);
		if(nextRefreshHour == 0) {
			nextResfreshDate = DateUtil.addDayToDate(nextResfreshDate, 1);
		}
		return DateUtil.dateDiffSecond(now, nextResfreshDate);
	}
	
	private int getRefreshTime(Date roleRefreshTime, Date now) {
		int roleTime = DateUtil.getHour(roleRefreshTime);
		int refreshCycle = shopSecretConfig.getRefreshCycle();//每隔几小时刷新
		int dayCycle = one_day_hours/refreshCycle;//一天刷新几次
		for(int i = 1; i < dayCycle ; i++) {
			int nextRefreshTime = refreshCycle * i;//刷新时间点
			if(roleTime < nextRefreshTime) {
				return nextRefreshTime;
			}
		}
		return 0;
	}
	
	@Override
	public void offlineLog(RoleInstance role) {
		try{
			RoleSecretShop roleSecretShop = role.getRoleSecretShop();
			if(null == roleSecretShop) {
				return;
			}
			
			StringBuffer sb = new StringBuffer();
			sb.append(roleSecretShop.getRoleId());
			sb.append(Cat.pound);
			sb.append(roleSecretShop.getGoods());
			sb.append(Cat.pound);
			sb.append(roleSecretShop.getRefreshTime());
			sb.append(Cat.pound);
			Log4jManager.OFFLINE_SHOP_SECRET.info(sb.toString());
		}catch(Exception e){
			logger.error("ShopSecretApp.offlineLog:",e);
		}
	}
}
