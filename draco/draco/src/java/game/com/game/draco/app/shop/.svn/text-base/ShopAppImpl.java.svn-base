package com.game.draco.app.shop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.game.draco.GameContext;
import com.game.draco.app.shop.domain.ShopGoods;
import com.game.draco.app.shop.type.ShopMoneyType;
import com.game.draco.app.shop.type.ShopShowType;

import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

public class ShopAppImpl implements ShopApp {
	
	private Map<Integer,ShopGoods> allShopGoodsMap;//全部商品
	private Map<ShopShowType,List<ShopGoods>> showGoodsMap;//商城物品（KEY=分页类型,VALUE=商城物品列表）
	
	private Map<Integer,ShopGoods> allGoodsTemp;//加载中介
	private Map<ShopShowType,List<ShopGoods>> showGoodsTemp;//加载中介
	
	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void start() {
		this.loadShopGoods();
		this.allShopGoodsMap = this.allGoodsTemp;
		this.showGoodsMap = this.showGoodsTemp;
		//清空加载中介
		this.allGoodsTemp = null;
		this.showGoodsTemp = null;
	}

	@Override
	public void stop() {
		
	}
	
	/** 加载商城物品信息 */
	private boolean loadShopGoods(){
		boolean loadSuccess = true;
		this.allGoodsTemp = new HashMap<Integer,ShopGoods>();
		this.showGoodsTemp = new HashMap<ShopShowType,List<ShopGoods>>();
		for(ShopShowType showType : ShopShowType.values()){
			this.showGoodsTemp.put(showType, new ArrayList<ShopGoods>());
		}
		String fileName = XlsSheetNameType.shop_list.getXlsName();
		String sheetName = XlsSheetNameType.shop_list.getSheetName();
		String error = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<ShopGoods> shopGoodsList = XlsPojoUtil.sheetToList(sourceFile, sheetName, ShopGoods.class);
			for(ShopGoods shopGoods : shopGoodsList){
				if(null == shopGoods){
					continue;
				}
				int goodsId = shopGoods.getGoodsId();
				if(null == GameContext.getGoodsApp().getGoodsBase(goodsId)){
					Log4jManager.CHECK.error(error + "goodsId=" + goodsId + ",the goods is not exist!");
					Log4jManager.checkFail();
					loadSuccess = false;
					continue;
				}
				Result result = shopGoods.checkAndInit();
				if(!result.isSuccess()){
					Log4jManager.CHECK.error(error + result.getInfo());
					Log4jManager.checkFail();
					loadSuccess = false;
					continue;
				}
				this.allGoodsTemp.put(goodsId, shopGoods);
				for(ShopShowType showType : shopGoods.getShowTypeList()){
					this.showGoodsTemp.get(showType).add(shopGoods);
				}
			}
			//商城物品排序
			for(List<ShopGoods> list : this.showGoodsTemp.values()){
				this.sortShopGoods(list);
			}
		}catch(Exception e){
			Log4jManager.CHECK.error(error,e);
			Log4jManager.checkFail();
			loadSuccess = false;
		}
		return loadSuccess;
	}
	
	/**
	 * 商城物品排序
	 * @param list
	 */
	private void sortShopGoods(List<ShopGoods> list){
		Collections.sort(list, new Comparator<ShopGoods>(){

			@Override
			public int compare(ShopGoods item1, ShopGoods item2) {
				if(item1.getIndex() < item2.getIndex()){
					return -1;
				}
				if(item1.getIndex() > item2.getIndex()){
					return 1;
				}
				return 0;
			}});
	}

	@Override
	public Result reLoad() {
		Result result = new Result();
		if(!this.loadShopGoods()){
			return result.setInfo(Status.Shop_Load_Failure.getTips());
		}
		this.allShopGoodsMap = this.allGoodsTemp;
		this.showGoodsMap = this.showGoodsTemp;
		//清空加载中介
		this.allGoodsTemp = null;
		this.showGoodsTemp = null;
		return result.success();
	}

	@Override
	public List<ShopGoods> getShopGoodsList(ShopShowType showType) {
		if(null == showType){
			return null;
		}
		return this.showGoodsMap.get(showType);
	}

	@Override
	public ShopGoods getShopGoods(int goodsId) {
		return this.allShopGoodsMap.get(goodsId);
	}

	@Override
	public Result shopping(RoleInstance role, int goodsId, short goodsNum, ShopMoneyType moneyType, boolean isOneKey) {
		Result result = new Result();
		if(goodsNum <= 0){
			return result.setInfo(Status.Shop_Req_Param_Error.getTips());
		}
		if(null == moneyType){
			return result.setInfo(Status.Shop_Req_Param_Error.getTips());
		}
		ShopGoods shopGoods = this.allShopGoodsMap.get(goodsId);
		GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
		if(null == shopGoods || null == goodsBase){
			return result.setInfo(Status.Shop_Goods_Not_Exist.getTips());
		}
		if(!isOneKey && !shopGoods.canSell()){
			return result.setInfo(Status.Shop_Time_Not_Open.getTips());
		}
		//售卖价格=单价×数量
		int goodsSellPrice = shopGoods.getSellPrice(moneyType);
		int sellPrice = goodsSellPrice*goodsNum;
		if(sellPrice <= 0){
			return result.setInfo(Status.Shop_Req_Param_Error.getTips());
		}
		BindingType bindType = BindingType.template;
		AttributeType attrTypeMoney = AttributeType.goldMoney;
		OutputConsumeType consumeType = OutputConsumeType.shop_buy_gold_money;
		if(ShopMoneyType.BindMoney == moneyType){
			//绑金购买的物品是绑定的
			bindType = BindingType.already_binding;
			attrTypeMoney = AttributeType.bindingGoldMoney;
			consumeType = OutputConsumeType.shop_buy_bind_money;
			if(role.getBindingGoldMoney() < sellPrice){
				return result.setInfo(Status.Shop_BindMoney_Not_Enough.getTips());
			}
		}else if(ShopMoneyType.GoldMoney == moneyType){
			//金条购买的物品，绑定类型根据模板配置
			bindType = BindingType.get(shopGoods.getGoldBindType());
			if(role.getGoldMoney() < sellPrice){
				return result.setInfo(Status.Shop_GoldMoney_Not_Enough.getTips());
			}
		}else{
			return result.setInfo(Status.Shop_Req_Param_Error.getTips());
		}
		//往背包添加物品
		result = GameContext.getUserGoodsApp().addGoodsForBag(role, goodsId, goodsNum, bindType, consumeType);
		if(!result.isSuccess()){
			return result;
		}
		//扣钱
		GameContext.getUserAttributeApp().changeRoleMoney(role, attrTypeMoney,
				OperatorType.Decrease, sellPrice, OutputConsumeType.shop_buy_consume);
		role.getBehavior().notifyAttribute();
		//购买物品日志
		GameContext.getStatLogApp().roleShopBuy(role, goodsId, goodsSellPrice, goodsNum, sellPrice, attrTypeMoney, consumeType);
		return result.success();
	}
}
