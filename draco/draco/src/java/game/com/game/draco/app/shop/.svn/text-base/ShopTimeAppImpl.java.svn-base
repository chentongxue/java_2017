package com.game.draco.app.shop;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;
import com.game.draco.app.shop.domain.ShopTimeGoods;
import com.game.draco.app.shop.domain.ShopTimeParam;
import com.game.draco.app.shop.type.ShopTimePerType;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.item.ShopTimeItem;

import sacred.alliance.magic.app.chat.ChannelType;
import sacred.alliance.magic.app.chat.ChatSysName;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

public class ShopTimeAppImpl implements ShopTimeApp{
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	/** KEY=物品ID,VALUE=限时物品 */
	private Map<Integer,ShopTimeGoods> goodsMap = new HashMap<Integer,ShopTimeGoods>();
	/** KEY=物品ID,VALUE=权重 */
	//private Map<Integer,Integer> goodsWeightMap = new HashMap<Integer, Integer>();
	
	//所售卖的物品
	private ShopTimeParam timeParam = new ShopTimeParam();
	private Map<Integer,ShopTimeItem> sellGoodsMap = new HashMap<Integer,ShopTimeItem>();
	private long lastUpdateShopTime;
	private long nextUpdateShopTime;
	
	//是否刷新
	private AtomicBoolean refresh = new AtomicBoolean(false);
	//每个剩余时间点只扣除一次物品个数，用来存放已扣除的时间比率(不控制并发)
	private Set<Integer> detimeRatioSet = new HashSet<Integer>();

	@Override
	public void start() {
		//加载限时购买配置
		this.loadShopTimeConfig();
	}
	
	@Override
	public void stop() {
		
	}

	@Override
	public void setArgs(Object arg0) {
		
	}
	
	/**
	 * 加载限时购买的配置
	 */
	private void loadShopTimeConfig(){
		String fileName = "";
		String sheetName = "";
		try{
			String xlsPath = GameContext.getPathConfig().getXlsPath();
			//①加载限时商品列表
			fileName = XlsSheetNameType.shop_limit_list.getXlsName();
			sheetName = XlsSheetNameType.shop_limit_list.getSheetName();
			List<ShopTimeGoods> goodsList = XlsPojoUtil.sheetToList(xlsPath + fileName, sheetName, ShopTimeGoods.class);
			String info = "load Excel error: " + fileName + ",sheet=" + sheetName + ".";
			for(ShopTimeGoods shopGoods : goodsList){
				if(null == shopGoods){
					continue;
				}
				int goodsId = shopGoods.getGoodsId();
				GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
				if(null == goodsBase){
					this.checkFail(info + "goodsId=" + goodsId + ",it's not exist!");
					continue;
				}
				int goldPrice = shopGoods.getGoldPrice();
				int goldPriceDis = shopGoods.getGoldPriceDis();
				if(goldPrice <= 0 || goldPriceDis <= 0 || goldPriceDis > goldPrice){
					this.checkFail(info + "goodsId=" + goodsId + ",goldPrice or goldPriceDis config error!");
					continue;
				}
				Result initResult = shopGoods.init();
				if(!initResult.isSuccess()){
					this.checkFail(info + initResult.getInfo());
					continue;
				}
				this.goodsMap.put(goodsId, shopGoods);
				//this.goodsWeightMap.put(goodsId, shopGoods.getWeight());
			}
			//②加载限时购买参数
			fileName = XlsSheetNameType.shop_limit_param.getXlsName();
			sheetName = XlsSheetNameType.shop_limit_param.getSheetName();
			List<ShopTimeParam> paramList = XlsPojoUtil.sheetToList(xlsPath + fileName, sheetName, ShopTimeParam.class);
			this.timeParam = paramList.get(0);
			if(null == this.timeParam){
				this.checkFail("load Excel error: " + fileName + ",sheet=" + sheetName + ".limit_param is not config!");
			}
		}catch(Exception e){
			this.checkFail("load Excel error: " + fileName + ",sheet=" + sheetName + ".");
		}
	}
	
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
	@Override
	public int getOverTime(){
		int overTime = (int)((nextUpdateShopTime - System.currentTimeMillis())/1000);
		if(overTime < 0){
			return 0;
		}
		return overTime;
	}
	
	@Override
	public Result buyGoods(RoleInstance role, int goodsId, int num){
		Result result = new Result();
		ShopTimeGoods shopGoods = this.getShopTimeGoods(goodsId);
		if(shopGoods == null){
			return result.setInfo(Status.Shop_Goods_Not_Exist.getTips());
		}
		if(num <= 0){
			return result.setInfo(Status.Shop_Req_Param_Error.getTips());
		}
		ShopTimeItem shopTimeItem = this.sellGoodsMap.get(goodsId);
		if(null == shopTimeItem){
			return result.setInfo(Status.Shop_Goods_Not_Exist.getTips());
		}
		int remainNum = shopTimeItem.getNum();
		if(remainNum <= 0 || num > remainNum){
			return result.setInfo(Status.Shop_Remain_Num_Not_Enough.getTips());
		}
		
		int goldPriceDis = shopGoods.getGoldPriceDis();
		int gold = goldPriceDis * num;
		if(gold <= 0){
			return result.setInfo(Status.Shop_Req_Param_Error.getTips());
		}
		if(role.getGoldMoney() < gold){
			return result.setInfo(Status.Shop_GoldMoney_Not_Enough.getTips());
		}
		result = GameContext.getUserGoodsApp().addGoodsForBag(role, goodsId, num, shopGoods.getBindingType(),OutputConsumeType.shop_time_output);
		if(result.isSuccess()){
			GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.goldMoney, 
					OperatorType.Decrease, gold, OutputConsumeType.shop_time_buy_consume);
			role.getBehavior().notifyAttribute();
			
			/*GameContext.getStatLogApp().logShopFlow(role, goodsId,gold,
					AttributeType.goldMoney.getType(), num);*/
			//购买成功后，修改剩余数量
			int goodsNum = remainNum - num;
			if(goodsNum < 0){
				goodsNum = 0;
			}
			shopTimeItem.setNum(goodsNum);
			
			//购买物品日志
			GameContext.getStatLogApp().roleShopBuy(role, goodsId, goldPriceDis, num, gold, AttributeType.goldMoney, OutputConsumeType.shop_time_output);
		}
		return result;
	}
	
	@Override
	public Collection<ShopTimeItem> getSellGoodsList(){
		//到达下一个刷新点
		if(System.currentTimeMillis() >= nextUpdateShopTime){
			this.updateShopGoods();
			return this.sellGoodsMap.values();
		}
		//计算时间比率，减物品数目
		int plus = (int)((nextUpdateShopTime - System.currentTimeMillis()) / (double)this.getUpdateTimeCycle() * 100);
		int per = ShopTimePerType.getPer(plus).getPer();
		
		//此时间比率已扣除物品数目，返回
		if(per == 0 || this.detimeRatioSet.contains(per)){
			return this.sellGoodsMap.values();
		}
		//按照时间比率获取该时间比率的物品剩余量
		for(ShopTimeItem item : this.sellGoodsMap.values()){
			ShopTimeGoods timeGoods = this.getShopTimeGoods(item.getGoodsItem().getGoodsId());
			if(timeGoods == null){
				continue;
			}
			//剩余量＝物品总数 ＊ 时间比率(整型) / 100 
			int remainNum = timeGoods.getNumber() * per / 100;
			//大于等于剩余量便减去递减值
			if(item.getNum() >= remainNum){
				int num = item.getNum();
				if(num <= timeGoods.getDecre()){
					continue;
				}
				item.setNum(num - timeGoods.getDecre());
			}
		}
		//已扣除的时间比率
		this.detimeRatioSet.add(per);
		return this.sellGoodsMap.values();
	}
	
	private ShopTimeGoods getShopTimeGoods(int goodsId){
		return this.goodsMap.get(goodsId);
	}
	
	private void updateShopTime(){
		this.lastUpdateShopTime = System.currentTimeMillis();
		this.nextUpdateShopTime = this.lastUpdateShopTime + this.getUpdateTimeCycle();
	}
	
	//刷新周期（毫秒）
	private long getUpdateTimeCycle(){
		return this.timeParam.getRefreshTime() * 60 * 60 * 1000;
	}
	
	//刷新商城物品
	private void updateShopGoods(){
		if(this.refresh.compareAndSet(false, true)){
			try{
				if(this.nextUpdateShopTime > System.currentTimeMillis()){
					return ;
				}
				this.updateShopTime();
				Set<Integer> goodsIds = Util.getWeightCalct(this.timeParam.getGoodsNum(), this.calcGoodsWeightMap());
				this.sellGoodsMap.clear();
				for(int goodsId : goodsIds){
					ShopTimeItem item = this.buildShopTimeItem(goodsId);
					if(item == null){
						continue;
					}
					this.sellGoodsMap.put(goodsId, item);
				}
				//广播
				String content = this.timeParam.getContent();
				if(!Util.isEmpty(content)){
					GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Publicize_System, content, null, null);
				}
				this.detimeRatioSet.clear();
			}catch(Exception e){
				this.logger.error("ShopTimeApp.updateShopGoods error: ", e);
			}finally{
				this.refresh.set(false);
			}
		}
	}
	
	/**
	 * 遍历所有物品获得权重map KEY=物品ID,VALUE=权重
	 * @return
	 */
	private Map<Integer,Integer> calcGoodsWeightMap(){
		Map<Integer,Integer> goodsWeightMap = new HashMap<Integer, Integer>();
		for(Entry<Integer, ShopTimeGoods> entry : this.goodsMap.entrySet()){
			ShopTimeGoods goods = entry.getValue();
			if(null == goods){
				continue;
			}
			if(!goods.inTime()){
				continue;
			}
			goodsWeightMap.put(goods.getGoodsId(), goods.getWeight());
		}
		return goodsWeightMap;
	}
	
	private ShopTimeItem buildShopTimeItem(int goodsId){
		try {
			ShopTimeItem item = new ShopTimeItem();
			ShopTimeGoods shopTimeGoods = this.getShopTimeGoods(goodsId);
			if(shopTimeGoods == null){
				return null;
			}
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
			if(null == goodsBase){
				return null;
			}
			item.setDisPrice(shopTimeGoods.getGoldPriceDis());
			item.setPrice(shopTimeGoods.getGoldPrice());
			item.setNum(shopTimeGoods.getNumber());
			
			GoodsLiteNamedItem liteItem = goodsBase.getGoodsLiteNamedItem();
			int stackNum = Math.min(goodsBase.getOverlapCount(), 
					shopTimeGoods.getDefaultBuyNum());
			liteItem.setNum((short)stackNum) ;
			
			item.setGoodsItem(liteItem);
			item.setResId((short)goodsBase.getResId());
			return item;
		} catch (RuntimeException e) {
			this.logger.error("ShopTimeApp.buildShopTimeItem error: ", e);
			return null;
		}
	}
	
}
