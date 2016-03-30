package com.game.draco.app.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsTask;
import sacred.alliance.magic.domain.GoodsTaskprops;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.npc.npcfunction.NpcFunctionSupport;
import com.game.draco.app.store.domain.NpcStore;
import com.game.draco.app.store.domain.NpcStoreName;
import com.game.draco.message.item.NpcFunctionItem;
import com.game.draco.message.request.C1601_NpcStoreListReqMessage;
import com.game.draco.message.response.C1603_NpcStoreSellRespMessage;

public class NpcStoreAppImpl implements NpcStoreApp, NpcFunctionSupport {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	/** 请求NPC买卖列表协议号 */
	public static final short NPC_STORE_REQ_COMMAND = new C1601_NpcStoreListReqMessage()
			.getCommandId();
	private final Logger loadLogger = Log4jManager.CHECK;
	private Map<String, Map<Integer, List<NpcStore>>> npcStoreMap = new HashMap<String, Map<Integer, List<NpcStore>>>();
	private Map<String, Map<Integer, NpcStore>> fastFindMap = new HashMap<String, Map<Integer, NpcStore>>();// 快速查找到某件物品
	private Map<String, NpcStoreName> storeNameMap = new HashMap<String, NpcStoreName>();// 商店名称

	private String getNpcStoreName(int showType) {
		NpcStoreName storeName = this.storeNameMap
				.get(String.valueOf(showType));
		if (null == storeName) {
			return "";
		}
		return storeName.getStoreName();
	}

	@Override
	public void setArgs(Object arg0) {

	}

	@Override
	public void start() {
		try {
			reload();
		} catch (ServiceException e) {
			loadLogger.error("npc store start error", e);
			Log4jManager.checkFail();
		}
	}

	@Override
	public void stop() {
	}

	public boolean reload() throws ServiceException {
		this.load();
		this.loadStoreName();
		return true;
	}

	private void loadStoreName() {
		storeNameMap.clear();
		String fileName = "";
		String sheetName = "";
		String sourceFile = "";
		try {
			String path = GameContext.getPathConfig().getXlsPath();
			// 加裁NPC商店名称
			fileName = XlsSheetNameType.npc_storeName.getXlsName();
			sourceFile = path + fileName;
			sheetName = XlsSheetNameType.npc_storeName.getSheetName();
			storeNameMap = XlsPojoUtil.sheetToMap(sourceFile, sheetName,
					NpcStoreName.class);
		} catch (Exception e) {
			loadLogger.error("loadStoreName error", e);
			Log4jManager.checkFail();
		}
	}

	private void load() {
		fastFindMap.clear();
		npcStoreMap.clear();
		List<NpcStore> npcStoreList = new ArrayList<NpcStore>();
		String fileName = "";
		String sourceFile = "";
		try {
			String path = GameContext.getPathConfig().getXlsPath();
			// 功能NPC加裁
			fileName = XlsSheetNameType.npc_store.getXlsName();
			sourceFile = path + fileName;
			String sheetName = XlsSheetNameType.npc_store.getSheetName();
			npcStoreList.addAll(XlsPojoUtil.sheetToList(sourceFile, sheetName,
					NpcStore.class));
		} catch (Exception e) {
			loadLogger.error("load npc store error", e);
			Log4jManager.checkFail();
		}
		if (Util.isEmpty(npcStoreList)) {
			return;
		}
		// 初始化条件&封装
		for (NpcStore store : npcStoreList) {
			store.init();

			String key = store.getNpcTemplateId() + Cat.colon
					+ store.getShowType();
			if (!fastFindMap.containsKey(key)) {
				fastFindMap.put(key, new HashMap<Integer, NpcStore>());
			}
			fastFindMap.get(key).put(store.getGoodsId(), store);

			if (!npcStoreMap.containsKey(store.getNpcTemplateId())) {
				npcStoreMap.put(store.getNpcTemplateId(),
						new HashMap<Integer, List<NpcStore>>());
			}
			if (!npcStoreMap.get(store.getNpcTemplateId()).containsKey(
					store.getShowType())) {
				npcStoreMap.get(store.getNpcTemplateId()).put(
						store.getShowType(), new ArrayList<NpcStore>());
			}
			npcStoreMap.get(store.getNpcTemplateId()).get(store.getShowType())
					.add(store);
		}
	}

	/** 得到显示页的物品列表 */
	@Override
	public List<NpcStore> getNpcStoreList(String npcTemplateId, int showType) {
		Map<Integer, List<NpcStore>> typeMap = npcStoreMap.get(npcTemplateId);
		if (Util.isEmpty(typeMap)) {
			return null;
		}
		if (Util.isEmpty(this.getNpcStoreName(showType))) {
			return null;
		}
		return typeMap.get(showType);
	}

	@Override
	public String buy(RoleInstance role, String npcTemplateId, int showType,
			int goodsTemplateId, int buyNum) {
		if (buyNum <= 0) {
			return Status.NpcStore_Is_Negative.getTips();
		}
		GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(
				goodsTemplateId);
		if (null == goodsBase) {
			return Status.NpcStore_Goods_Null.getTips();
		}
		String key = npcTemplateId + Cat.colon + showType;
		NpcStore store = fastFindMap.get(key).get(goodsTemplateId);
		if (null == store) {
			return Status.NpcStore_Not_Npc.getTips();
		}
		return store.buy(role, buyNum);
	}

	@Override
	public Status sell(RoleInstance role, String npcTemplateId,
			String goodsInstanceId, int sellNum) {
		if (sellNum <= 0) {
			return Status.NpcStore_Is_Negative;
		}
		RoleGoods roleGoods = null;
		try {
			roleGoods = role.getRoleBackpack().getRoleGoodsByInstanceId(
					goodsInstanceId);
		} catch (Exception e) {
		}
		if (null == roleGoods) {
			return Status.NpcStore_Goods_Null;
		}
		try {
			GoodsBase base = GameContext.getGoodsApp().getGoodsBase(
					roleGoods.getGoodsId());
			if (null == base) {
				return Status.NpcStore_Goods_Null;
			}
			if (base.getRecycling() != 1) {
				return Status.NpcStore_Goods_Not_Sell;
			}
			if (base.getRecyclePrice() < 0) {
				return Status.NpcStore_Price_Error;
			}
			if ((base instanceof GoodsTask) || (base instanceof GoodsTaskprops)) {
				return Status.NpcStore_Task_Goods;
			}
			/*
			 * if(null == npcStoreMap.get(npcTemplateId)){ return
			 * Status.NpcStore_Not_Npc; }
			 */
			if (roleGoods.getCurrOverlapCount() < 0
					|| sellNum > roleGoods.getCurrOverlapCount()) {
				return Status.NpcStore_Sell_Num_Error;
			}
			// 扣除物品
			GameContext.getUserGoodsApp().deleteForBagByInstanceId(role,
					goodsInstanceId, sellNum,
					OutputConsumeType.npc_store_sell_consume);

			int money = base.getRecyclePrice() * sellNum;
			// 给钱
			GameContext.getUserAttributeApp().changeRoleMoney(role,
					AttributeType.silverMoney, OperatorType.Add, money,
					OutputConsumeType.npc_store_buy_money);
			role.getBehavior().notifyAttribute();// 同步-400

			String message = GameContext.getI18n().messageFormat(
					TextId.NPC_STORE_SELL_MSG, String.valueOf(sellNum), base.getName(),String.valueOf(money));
			C1603_NpcStoreSellRespMessage nssrm = new C1603_NpcStoreSellRespMessage();
			nssrm.setInfo(message);
			role.getBehavior().sendMessage(nssrm);
			return Status.SUCCESS;
		} catch (Exception e) {
			logger.error("npc sell error", e);
			return Status.NpcStore_Goods_Null;
		}
	}

	@Override
	public List<NpcFunctionItem> getNpcFunction(RoleInstance role,
			NpcInstance npc) {
		String npcTemplateId = npc.getNpc().getNpcid();
		Map<Integer, List<NpcStore>> typeMap = npcStoreMap.get(npcTemplateId);
		if (null == typeMap || typeMap.size() == 0) {
			return null;
		}
		List<NpcFunctionItem> functionList = new ArrayList<NpcFunctionItem>();
		for (int i : typeMap.keySet()) {
			String storeName = this.getNpcStoreName(i);
			if (Util.isEmpty(storeName)) {
				continue;
			}
			NpcFunctionItem item = new NpcFunctionItem();
			item.setCommandId(NPC_STORE_REQ_COMMAND);
			item.setTitle(storeName);
			String param = npcTemplateId + Cat.comma + i;
			item.setParam(param);
			functionList.add(item);
		}
		return functionList;
	}

	public Map<String, NpcStoreName> getStoreNameMap() {
		return storeNameMap;
	}
}
