package com.game.draco.app.shopsecret.domain;

import lombok.Data;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.domain.GoodsBase;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.game.draco.GameContext;
import com.game.draco.message.item.GoodsLiteNamedItem;

/**
 * 
 */
@Data
public class ShopSecretGoodsRecord {
	// Constant
	public static final byte CAN_BUY = 0;// 未购买
	public static final byte HAS_BUY = 1;// 已经购买

	@Protobuf(fieldType = FieldType.INT32, order = 1)
	private int shopItemId;
	@Protobuf(fieldType = FieldType.INT32, order = 2)
	private int goodsId;
	@Protobuf(fieldType = FieldType.INT32, order = 3)
	private int num;
	@Protobuf(fieldType = FieldType.INT32, order = 4)
	private byte bind;
	@Protobuf(fieldType = FieldType.INT32, order = 5)
	private byte moneyType;
	@Protobuf(fieldType = FieldType.INT32, order = 6)
	private int price;
	@Protobuf(fieldType = FieldType.INT32, order = 7)
	private byte status;// 状态 0：未购买，1 已经购买

	public GoodsOperateBean getGoodsOperateBean() {
		return new GoodsOperateBean(goodsId, num, bind);
	}

	public GoodsLiteNamedItem getGoodsLiteNamedItem() {
		GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
		if (null == goodsBase) {
			return null;
		}
		GoodsLiteNamedItem goodsLiteNamedItem = goodsBase
				.getGoodsLiteNamedItem();
		goodsLiteNamedItem.setNum((short) num);
		goodsLiteNamedItem.setBindType(bind);
		return goodsLiteNamedItem;
	}

	public boolean canBuy() {
		return status == CAN_BUY;
	}
}
