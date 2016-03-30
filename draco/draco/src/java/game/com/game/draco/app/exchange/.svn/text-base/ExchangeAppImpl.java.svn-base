package com.game.draco.app.exchange;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.chat.ChannelType;
import sacred.alliance.magic.app.chat.ChatSysName;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.base.ExchangeType;
import sacred.alliance.magic.base.FrequencyType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.condition.Condition;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.exchange.consumetype.ConsumeLogic;
import com.game.draco.app.exchange.domain.ExchangeDbInfo;
import com.game.draco.app.exchange.domain.ExchangeItem;
import com.game.draco.app.exchange.domain.ExchangeMenu;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.message.item.ExchangeChildGoodsItem;
import com.game.draco.message.item.ExchangeChildItem;
import com.game.draco.message.item.ExchangeConditionItem;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.item.NpcFunctionItem;
/**
 * 物品兑换：
 * 数值兑换：
 */
public class ExchangeAppImpl implements ExchangeApp {
	
	private Map<Integer, ExchangeMenu> allMenuMap = new HashMap<Integer, ExchangeMenu>();
	private Map<Integer,ExchangeItem> allItemMap = new HashMap<Integer,ExchangeItem>();
	private Map<Integer, Condition> allConditionMap = new HashMap<Integer, Condition>();
	private final Logger logger = LoggerFactory.getLogger(ExchangeAppImpl.class);
	public final static String COLOR_RED = "ffFF0000";
	private Map<String, List<ExchangeMenu>> npcMenuMap = new HashMap<String, List<ExchangeMenu>>();
	private Map<Integer, ExchangeMenu> menuMap = new LinkedHashMap<Integer, ExchangeMenu>();
	private Map<Short, List<ExchangeItem>> copyResetMap = new HashMap<Short, List<ExchangeItem>>();
	
	@Override
	public void setArgs(Object arg0) {

	}

	@Override
	public void start() {
		this.loadExchangeMenu();
		this.loadExchangeItem();
		this.loadExchangeCondition();
		this.init();
	}

	@Override
	public void stop() {

	}
	
	private void loadExchangeMenu() {
		//加载兑换菜单配置
		String fileName = XlsSheetNameType.exchange_menu.getXlsName();
		String sheetName = XlsSheetNameType.exchange_menu.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			allMenuMap = XlsPojoUtil.sheetToGenericLinkedMap(sourceFile, sheetName, ExchangeMenu.class);
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
		}
		if(Util.isEmpty(this.allMenuMap)){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("not any config: sourceFile = "+fileName +" sheetName ="+sheetName);
		}
		
	}
	
	private void loadExchangeItem() {
		//加载兑换项
		String fileName = XlsSheetNameType.exchange_item.getXlsName();
		String sheetName = XlsSheetNameType.exchange_item.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			allItemMap = XlsPojoUtil.sheetToGenericLinkedMap(sourceFile, sheetName, ExchangeItem.class);
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
		}
		if(Util.isEmpty(this.allItemMap)){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("not any config: sourceFile = "+fileName +" sheetName ="+sheetName);
		}
	}
	
	private void loadExchangeCondition() {
		//加载条件类项
		String fileName = XlsSheetNameType.exchange_condition.getXlsName();
		String sheetName = XlsSheetNameType.exchange_condition.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			allConditionMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, Condition.class);
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
		}
		if(Util.isEmpty(this.allConditionMap)){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("not any config: sourceFile = "+fileName +" sheetName ="+sheetName);
		}
	}
	
	private void init() {
		//根据条件类型来初始化条件实例
		for(Integer id: this.allConditionMap.keySet()){
			Condition condition = this.allConditionMap.get(id);
			if(condition == null)
				continue;
			condition.getConditionTypeInstance();
			condition.init(condition.getCompareType());
		}
		
		//根据兑换的条件，物品初始化实例
		for(ExchangeMenu exchangeMenu : this.allMenuMap.values()) {
			if(null == exchangeMenu) {
				continue;
			}
			
			ExchangeType exchangeType = ExchangeType.getExchangeType(exchangeMenu.getType());
			exchangeMenu.setExchangeType(exchangeType);
			
			
			String npcId = exchangeMenu.getNpcId();
			if(!Util.isEmpty(npcId)) {
				if(!npcMenuMap.containsKey(npcId)) {
					npcMenuMap.put(npcId, new ArrayList<ExchangeMenu>());
				}
				npcMenuMap.get(npcId).add(exchangeMenu);
				continue;
			}
			
			menuMap.put(exchangeMenu.getId(), exchangeMenu);
		}
		
		List<Integer> ids = new ArrayList<Integer>();
		for(ExchangeItem exchangeItem : this.allItemMap.values()){
			if(exchangeItem == null)
				continue;
			if(!exchangeItem.init()){
				ids.add(exchangeItem.getId());
				continue;
			}
			ExchangeMenu exchangeMenu = this.allMenuMap.get(exchangeItem.getMenuId());
			if(null == exchangeMenu) {
				continue;
			}
			//验证
			if(exchangeMenu.getExchangeType() == ExchangeType.numerical) {
				if(null == exchangeItem.getConsumeLogic()) {
					Log4jManager.checkFail();
					Log4jManager.CHECK.error("ExchangeApp.init exchangeItem error: exchangeItemId=" + exchangeItem.getId() +",ExchangeMenu's exchangeType error");
					continue;
				}
			}
			
			String[] conditionIds = Util.splitString(exchangeItem.getConditionIds());
			for(int i=0; i<conditionIds.length; i++){
				int conditionId = Integer.valueOf(conditionIds[i]);
				Condition condition = this.allConditionMap.get(conditionId);
				if(null == condition){
					Log4jManager.checkFail();
					Log4jManager.CHECK.error("ExchangeApp.init Condition error: exchangeItemId=" + exchangeItem.getId() +",Condition = " + conditionId + " not exist");
					continue;
				}
				exchangeItem.getConditionList().add(condition);
			}
			exchangeMenu.getChildExchanges().add(exchangeItem);
			
			short enterResetCopyId = exchangeItem.getEnterResetCopyId();
			if(exchangeItem.getFrequencyType() == FrequencyType.FREQUENCY_TYPE_COPY.getType() ) {
				if(!copyResetMap.containsKey(enterResetCopyId)) {
					copyResetMap.put(enterResetCopyId, new ArrayList<ExchangeItem>());
				}
				copyResetMap.get(enterResetCopyId).add(exchangeItem);
			}
		}
		
		//删除过期的兑换
		if(ids.size() != 0){
			for(Integer id : ids){
				this.allItemMap.remove(id);
			}
		}
	}

	@Override
	public Status canExchange(RoleInstance role, ExchangeItem item) {
		try{
			return item.isMeet(role);
		}catch(Exception e) {
			this.logger.error("",e);
		}
		return Status.Exchange_Can_Not_EXchange;
	}

	@Override
	public GoodsResult exchange(RoleInstance role, ExchangeItem item) {
		GoodsResult goodsResult = new GoodsResult();
		if (!GameContext.getUserGoodsApp().canPutGoodsBean(role,item.getGainGoodsList())) {
			goodsResult.setResult(Result.FAIL);
			goodsResult.setInfo(Status.Exchange_Backpack_Not_Enough.getTips());
			return goodsResult;
		}
		goodsResult = GameContext.getUserGoodsApp().addDelGoodsForBag(
				role, item.getGainGoodsList(),
				OutputConsumeType.goods_exchange_output,
				item.getConsumeGoods(),
				OutputConsumeType.goods_exchange_consume);
		if (!goodsResult.isSuccess()) {
			return goodsResult;
		}
		//喊话
		this.broadcast(role, item.getFirstConsumeGoodsId(), item.getGainGoodsId(), item.getBroadcast());
		ConsumeLogic consumeLogic = item.getConsumeLogic();
		if(null == consumeLogic) {
			return goodsResult;
		}
		// 消耗
		boolean needNotify = item.getConsumeLogic().reduceRoleAttri(role, item.getConsumeDiscount());
		if (needNotify) {
			role.getBehavior().notifyAttribute();
		}
		
		return goodsResult;
	}
	
	private void broadcast(RoleInstance role, int consumeGoodsId, int gainGoodsId, String broadcastMsg){
		try{
			if(Util.isEmpty(broadcastMsg)) {
				return;
			}
			String consumeGoodsContent = Wildcard.getChatGoodsContent(consumeGoodsId, ChannelType.Publicize_Personal);
			String gainGoodsContent = Wildcard.getChatGoodsContent(gainGoodsId, ChannelType.Publicize_Personal);
			String message = MessageFormat.format(broadcastMsg,role.getRoleName(),consumeGoodsContent,gainGoodsContent);							
			GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Publicize_Personal, message, null, null);
		}catch(Exception e){
			logger.error("ExchangeApp.broadcast",e);
		}
	}

	@Override
	public void loadRoleExchange(RoleInstance role) {
		// TODO Auto-generated method stub
		//次数限制绑定类型：0：角色绑定，1：帐号绑定
		List<ExchangeDbInfo> roleExchange = GameContext.getBaseDAO().selectList(ExchangeDbInfo.class, "roleId", role.getRoleId());
		if(null==roleExchange || roleExchange.size()==0){
			return;
		}
		Map<Integer, ExchangeDbInfo> exchangeDbInfoMap = role.getExchangeDbInfo();
		for(ExchangeDbInfo exchangeDbInfo : roleExchange){
			if(null == exchangeDbInfo){
				continue;
			}
			exchangeDbInfo.setExistRecord(true);
			exchangeDbInfoMap.put(exchangeDbInfo.getId(), exchangeDbInfo);
		}
	}

	@Override
	public void saveRoleExchange(RoleInstance role) {
		Map<Integer, ExchangeDbInfo> exchangeDbInfoMap = role.getExchangeDbInfo();
		if(exchangeDbInfoMap.size() == 0){
			return;
		}
		for(ExchangeDbInfo exchangeDbInfo : exchangeDbInfoMap.values()){
			if(null == exchangeDbInfo){
				continue;
			}
			ExchangeItem exchangeItem = this.getAllItemMap().get(exchangeDbInfo.getId());
			if(null == exchangeItem){
				GameContext.getBaseDAO().delete(ExchangeDbInfo.class, "id", exchangeDbInfo.getId(), "roleId", exchangeDbInfo.getRoleId());
				continue;
			}
			exchangeItem.resetExchange(exchangeDbInfo);
			if(exchangeDbInfo.isExistRecord()){
				if(exchangeDbInfo.getTimes() == 0) {
					GameContext.getBaseDAO().delete(ExchangeDbInfo.class, "id", exchangeDbInfo.getId(), "roleId", exchangeDbInfo.getRoleId());
				}else{
					GameContext.getBaseDAO().update(exchangeDbInfo);
				}
			}else{
				if(exchangeDbInfo.getTimes() > 0) {
					GameContext.getBaseDAO().insert(exchangeDbInfo);
				}
			}
		}
	}
	
	@Override
	public void offlineLog(RoleInstance role) {
		try{
			Map<Integer, ExchangeDbInfo> exchangeDbInfoMap = role.getExchangeDbInfo();
			if(exchangeDbInfoMap.size() == 0){
				return;
			}
			for(Map.Entry<Integer, ExchangeDbInfo> entry : exchangeDbInfoMap.entrySet()){
				ExchangeDbInfo exchangeDbInfo = entry.getValue();
				if(null == exchangeDbInfo){
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
				sb.append(DateUtil.getTimeByDate(exchangeDbInfo.getExpiredTime()));
				sb.append(Cat.pound);
				Log4jManager.OFFLINE_EXCHANGE_DB_LOG.info(sb.toString());
			}
		}catch(Exception e){
			logger.error("saveRoleExchangeLog:",e);
		}
	}

	@Override
	public Map<Integer, ExchangeItem> getAllItemMap() {
		return allItemMap;
	}

	@Override
	public Map<Integer, ExchangeMenu> getAllMenuMap() {
		return allMenuMap;
	}
	
	@Override
	public List<ExchangeChildGoodsItem> getGoodsChildList(RoleInstance role, int menuId) {
		ExchangeMenu exchangeMenu = GameContext.getExchangeApp().getAllMenuMap().get(menuId);
		if(null == exchangeMenu) {
			return null;
		}
		List<ExchangeChildGoodsItem> childList = new ArrayList<ExchangeChildGoodsItem>();
		try{
			ExchangeChildGoodsItem exchangeChildGoodsItem = null;
			for(ExchangeItem exchangeItem : exchangeMenu.getChildExchanges()) {
				if(null == exchangeItem){
					continue;
				}
				if(!exchangeItem.isMeetConditionsAndDis(role)){
					continue;
				}
				if(exchangeItem.isOutDate()){
					continue;
				}
				ExchangeChildItem exchangeChildItem = getExchangeChildItem(role, exchangeItem);
				if(null == exchangeChildItem){
					continue;
				}
				exchangeChildGoodsItem = new ExchangeChildGoodsItem();
				exchangeChildGoodsItem.setExchangeChildItem(exchangeChildItem);
				//消耗物品
				List<GoodsLiteNamedItem> consumeGoods = new ArrayList<GoodsLiteNamedItem>();
				for(Integer goodId : exchangeItem.getConsumeGoods().keySet()){
					if(null == goodId){
						continue;
					}
					GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodId);
					if(null == gb){
						continue ;
					}
					GoodsLiteNamedItem consumeGoodItem = gb.getGoodsLiteNamedItem() ;
					//设置数量
					consumeGoodItem.setNum(exchangeItem.getConsumeGoods().get(goodId).shortValue());
					consumeGoods.add(consumeGoodItem);
				}
				exchangeChildGoodsItem.setConsumeGoods(consumeGoods);
				childList.add(exchangeChildGoodsItem);
			}
		}catch(Exception e){
			logger.error("ExchangeApp getChildList error:",e);
		}
		return childList;
	}
	
	/**
	 * 获得基础兑换信息
	 * @param role
	 * @param exchangeItem
	 * @return
	 */
	private ExchangeChildItem getExchangeChildItem(RoleInstance role, ExchangeItem exchangeItem){
		GoodsOperateBean goods = exchangeItem.getGainGoods();
		if(null == goods) {
			return null;
		}
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goods.getGoodsId());
		if(null == gb){
			return null;
		}
		
		ExchangeChildItem exchangeChildItem = new ExchangeChildItem();
		exchangeChildItem.setId(exchangeItem.getId());
		exchangeChildItem.setName(exchangeItem.getName());
		
		if(exchangeItem.isTimeOpen()) {
			//兑换日期
			if(!exchangeItem.isInDate()){
				exchangeChildItem.setStartDate(Util.getColorString(ExchangeAppImpl.COLOR_RED, DateUtil.getDayFormat(exchangeItem.getStart())));
				exchangeChildItem.setEndDate(Util.getColorString(ExchangeAppImpl.COLOR_RED,DateUtil.getDayFormat(exchangeItem.getEnd())));
			}else{
				exchangeChildItem.setStartDate(DateUtil.getDayFormat(exchangeItem.getStart()));
				exchangeChildItem.setEndDate(DateUtil.getDayFormat(exchangeItem.getEnd()));
			}
		}
		
		boolean canExchange = exchangeItem.isMeet(role) == Status.Exchange_Can_Exchange;
		exchangeChildItem.setCanExchange(canExchange ? (byte)1 : (byte)0);
		exchangeChildItem.setConsumeDiscount(exchangeItem.getConsumeDiscount());
		exchangeChildItem.setConsumeOriginal(exchangeItem.getConsumeOriginal());
		exchangeChildItem.setConsumeType(exchangeItem.getConsumeType());
		
		//兑换次数
		int remain = exchangeItem.getFrequencyValue() - exchangeItem.getFrequencyInfo(role);
		remain = remain > 0 ? remain : 0;
		exchangeChildItem.setRemainFrequency((byte)remain);
		exchangeChildItem.setFrequencyType(exchangeItem.getFrequencyType());
		//兑换条件
		List<ExchangeConditionItem> conditions = null;
		for(Condition condition : exchangeItem.getConditionList()){
			if(null == condition){
				continue;
			}
			if(condition.isMeet(role)) {//
				continue;
			}
			if(null == conditions){
				conditions = new ArrayList<ExchangeConditionItem>();
			}
			ExchangeConditionItem conditionItem = new ExchangeConditionItem();
			conditionItem.setCondition(condition.getName());
			if(condition.isStrReplace()){
				int attriValue = condition.getConditionLogic().getRoleAttri(role, condition);
				conditionItem.setCondition(condition.getName().replace(Condition.REPLACE_SIGN, String.valueOf(attriValue > condition.getMinValue() ? condition.getMinValue() : attriValue)));
			}
			conditions.add(conditionItem);
			exchangeChildItem.setConditions(conditions);
		}
		//奖励
		GoodsLiteNamedItem goodItem = null ;
		
		goodItem = gb.getGoodsLiteNamedItem() ;
		//设置数目
		goodItem.setNum((short)goods.getGoodsNum());
		//绑定类型
		goodItem.setBindType(goods.getBindType().getType());
		if(null != goodItem){
			exchangeChildItem.setGoods(goodItem);
		}
		return exchangeChildItem;
	}
	
	@Override
	public List<ExchangeChildItem> getChildList(RoleInstance role, int menuId) {
		ExchangeMenu exchangeMenu = GameContext.getExchangeApp().getAllMenuMap().get(menuId);
		if(null == exchangeMenu) {
			return null;
		}
		List<ExchangeChildItem> childList = new ArrayList<ExchangeChildItem>();
		try{
			for(ExchangeItem exchangeItem : exchangeMenu.getChildExchanges()) {
				if(null == exchangeItem){
					continue;
				}
				if(!exchangeItem.isMeetConditionsAndDis(role)){
					continue;
				}
				if(exchangeItem.isOutDate()){
					continue;
				}
				ExchangeChildItem exchangeChildItem = getExchangeChildItem(role, exchangeItem);
				childList.add(exchangeChildItem);
			}
		}catch(Exception e){
			logger.error("ExchangeApp getChildList error:",e);
		}
		return childList;
	}

	@Override
	public List<NpcFunctionItem> getNpcFunction(RoleInstance role,
			NpcInstance npc) {
		List<NpcFunctionItem> functionList = new ArrayList<NpcFunctionItem>();
		if(Util.isEmpty(npcMenuMap)){
			return functionList;
		}
		List<ExchangeMenu> menuList = npcMenuMap.get(npc.getNpcid());
		if(Util.isEmpty(menuList)){
			return functionList;
		}
		for(ExchangeMenu exchangeMenu:menuList){
			if(null == exchangeMenu) {
				return functionList;
			}
			if(exchangeMenu.getChildExchanges().size() <= 0) {
				return functionList;
			}
			
			NpcFunctionItem item = new NpcFunctionItem();
			item.setTitle(exchangeMenu.getName());
			item.setCommandId(ExchangeConstant.EXCHANGE_NPC_ITEM_CMD);
			String param = exchangeMenu.getId()+"";
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
			if(Util.isEmpty(list)) {
				return;
			}
			for(ExchangeItem item : list) {
				if(null == item) {
					continue;
				}
				int itemId = item.getId();
				ExchangeDbInfo info = role.getExchangeDbInfo().get(itemId);
				if(null == info) {
					continue;
				}
				info.setTimes((byte)0);
			}
		} catch (Exception e) {
			logger.error("ExchangeApp resetExchangeByType error:",e);
		}
	}
}
