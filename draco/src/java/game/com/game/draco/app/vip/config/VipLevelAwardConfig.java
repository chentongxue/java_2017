package com.game.draco.app.vip.config;

import com.game.draco.GameContext;
import com.game.draco.message.item.GoodsLiteItem;
import com.game.draco.message.item.GoodsLiteNamedItem;

import lombok.Data;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;
/**
 * VIP 等级奖励
 */
public @Data class VipLevelAwardConfig implements KeySupport<Byte>{
	private byte vipLevel;
	private int goodsId; 
	private short num;
	private byte bind;
	private byte qualityType ;
	@Override
	public Byte getKey() {
		return vipLevel;
	}

	public GoodsLiteItem getGoodsLiteItem(){
		GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
		if(null == goodsBase){
			return null;
		}
		GoodsLiteItem goodsLiteItem = goodsBase.getGoodsLiteItem();
		goodsLiteItem.setNum(num);
		goodsLiteItem.setBindType(bind);
		goodsLiteItem.setQualityType(goodsBase.getQualityType());
		return goodsLiteItem;
	}
	public GoodsLiteNamedItem getGoodsLiteNamedItem(){
		GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
		if(null == goodsBase){
			return null;
		}
		GoodsLiteNamedItem goodsLiteNamedItem = goodsBase.getGoodsLiteNamedItem();
		goodsLiteNamedItem.setNum(num);
		goodsLiteNamedItem.setBindType(bind);
		goodsLiteNamedItem.setQualityType(goodsBase.getQualityType());
		return goodsLiteNamedItem;
	}
	
	public void init(){
		GoodsBase  goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
		if (goodsBase == null) {
			Log4jManager.CHECK.error("vip VipLevelAwardConfig init fail:" + "goodId=" + goodsId + " is not exsit!" +
					"check vip.xls->vip_level_award plz");
			Log4jManager.checkFail();
		}
	}
	
}
