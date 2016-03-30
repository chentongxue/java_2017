package com.game.draco.app.vip.config;

import lombok.Data;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;

import com.game.draco.GameContext;
import com.game.draco.message.item.GoodsLiteNamedItem;
/**
 * 运营商城中的VIP礼包配置
 */
public @Data class VipGiftConfig implements KeySupport<String>{
	private byte vipLevel;//key	
	private int goodsId; 
	private short num;
	private byte bind;
	private String giftInfo;
	private int diamonds;

	
	@Override
	public String getKey() {
		return String.valueOf(vipLevel);
	}
	
	
	public GoodsLiteNamedItem getGoodsLiteNamedItem(){
		GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
		if(null == goodsBase){
			return null;
		}
		GoodsLiteNamedItem goodsLiteNamedItem = goodsBase.getGoodsLiteNamedItem();
		goodsLiteNamedItem.setNum(num);
		goodsLiteNamedItem.setBindType(bind);
		return goodsLiteNamedItem;
	}
	//init check
	public void init(){
		GoodsBase  goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
		if (goodsBase == null) {
			Log4jManager.CHECK.error("vip VipGiftConfig init  err," + "goodId=" + goodsId + " is not exsit!" +
					"please check vip.xls->vip_gift");
			Log4jManager.checkFail();
		}
	}
}
