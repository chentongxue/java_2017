package com.game.draco.app.shop.config;

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;

import com.game.draco.GameContext;
import com.game.draco.app.shop.ShopGoodsComparable;
import com.game.draco.app.shop.domain.DateTimeBeanSupport;
import com.game.draco.app.shop.domain.RoleShopDailyLimit;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.item.ShopGoodsItem;

/**
 * 普通商店
 */
public @Data class ShopGoodsConfig extends DateTimeBeanSupport implements
		KeySupport<Integer>, ShopGoodsComparable {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private int goodsId;// 物品id
	private byte index;// 排序索引（显示优先级0-9,0为最高，如果优先级相同，则按表格中的填写顺序排列）
	private int price;// 钻石价格
	private int disPrice;// 折扣钻石价格(真实价格)
	private byte moneyType;// 消费类型 4：钻石，5：游戏币金币
	private byte bind;// 绑定类型
	private byte stateType;//0:默认,1：热卖,2：打折

	public Result init() {
		Result result = new Result();
		String info = "goodsId=" + this.goodsId + ".";
		try {
			Result timeResult = this.initDateTimeBean();
			if (!timeResult.isSuccess()) {
				return result.setInfo(info + timeResult.getInfo());
			}
			// 所有价格都为零
			if (0 == this.price && 0 == this.disPrice) {
				return result.setInfo(info
						+ "Please config the goldPrice or bindPrice.");
			}
			// 折扣价格不能大于原价，价格不能是负数
			if (this.disPrice > this.price || this.disPrice <= 0) {
				return result.setInfo(info
						+ "The price must greater than the disPrice.");
			}
			return result.success();
		} catch (Exception e) {
			return result.setInfo(info + "catch exception: " + e.toString());
		}
	}
	public void check(){
		String info = "goodsId=" + this.goodsId + ".";
		GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
		if (goodsBase == null) {
			checkFail("shop ShopGoodsConfig.check() fail: getGoodsLiteNamedItem failed,"
					+ info + " is not exsit!");
		}
		// 折扣价格不能大于原价，价格不能是负数
		if (this.disPrice > this.price || this.disPrice < 0) {
			checkFail("shop ShopGoodsConfig.check() fail:" + info + "The price must greater than the disPrice.");
		}
	}
	/**
	 * 商品是否可出售
	 */
	public boolean canSell() {
		return this.inTime();
	}

	@Override
	public int compareTo(Object o) {
		if (!(o instanceof ShopGoodsComparable)) {
			return -1;
		}
		ShopGoodsComparable other = (ShopGoodsComparable) o;
		return this.getWeight() - other.getWeight();
	}

	@Override
	public int getWeight() {
		return index;
	}

	// 记录相关
	public GoodsLiteNamedItem getGoodsLiteNamedItem() {
		GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
		if (goodsBase == null) {
			logger.error("shop DailyLimitConfig getGoodsLiteNamedItem failed, goodId="
					+ goodsId + " is not exsit!");
			return null;
		}
		GoodsLiteNamedItem rt = goodsBase.getGoodsLiteNamedItem();
//		rt.setNum(num);
		rt.setBindType(bind);
		return rt;
	}

	public ShopGoodsItem getShopGoodsItem() {
		GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
		if (goodsBase == null) {
			logger.error("shop DailyLimitConfig getGoodsLiteNamedItem failed,"
					+ "goodId=" + goodsId + " is not exsit!");
			return null;
		}
		GoodsLiteNamedItem nt = goodsBase.getGoodsLiteNamedItem();

		ShopGoodsItem it = new ShopGoodsItem();
		it.setDisPrice(disPrice);
		it.setPrice(price);
		it.setGoodsItem(nt);
		it.setMoneyType(moneyType);
		it.setStateType(stateType);
		return it;
	}

	@Override
	public Integer getKey() {
		return goodsId;
	}
	
	private void checkFail(String errInfo) {
		Log4jManager.CHECK.error(errInfo);
		Log4jManager.checkFail();
	}
}
