package com.game.draco.app.richman.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;

import com.game.draco.app.richman.vo.RichManCardHurtType;
import com.game.draco.app.richman.vo.RichManCardTargetType;
import com.game.draco.app.richman.vo.RichManVoucherType;

public @Data class RichManCard implements KeySupport<Integer>{
	private int goodsId; //物品id
	private byte targetType ; //目标类型 0：前方玩家1：前后方玩家2：自己或目标
	private byte targetNum ; //目标数量
	private short hitRate ; //成功概率
	private byte stateId ; //状态id
	private byte hurtType ; //目标是被抢还是掉 0:lose 1:rob
	private byte couponType ; //点券类型 0:数值 1：百分比
	private int minValue ; //被抢夺点券下限
	private int maxValue ; //被抢夺点券上限
	private short loseCardRate ; //丢失卡片概率
	private short effectId; //被击中特效id
	
	//变量
	private RichManCardTargetType cardTargetType = null;
	private RichManCardHurtType cardHurtType;
	private RichManVoucherType cardVoucherType;
	
	public void init() {
		cardTargetType = RichManCardTargetType.get(this.targetType);
		cardHurtType = RichManCardHurtType.get(this.hurtType);
		cardVoucherType = RichManVoucherType.get(this.couponType);
		if(null == cardTargetType || null == cardHurtType || null == cardVoucherType) {
			Log4jManager.CHECK.error("GoodsRichManCard init error: targetType or hurtType or voucherType config erro");
			Log4jManager.checkFail();
		}
	}

	@Override
	public Integer getKey() {
		return this.goodsId;
	}
}
