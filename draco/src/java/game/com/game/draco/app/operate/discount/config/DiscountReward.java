package com.game.draco.app.operate.discount.config;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.app.operate.discount.domain.RoleDiscount;
import com.game.draco.message.item.AttriTypeValueItem;
import com.google.common.collect.Lists;

import lombok.Data;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;

public @Data class DiscountReward implements KeySupport<Integer>{
	public final static byte GOLD_VALUE_TYPE_GOLD=0;
	public final static byte GOLD_VALUE_TYPE_RATE=1;
	private int rewardId;
	private int goldMoney;
	private byte goldValueType; //金条值类型,0:表示goldMoney为玩家等到的金条数,1:表示goldMoney为玩家充值返利的百分比
	private int silverMoney;
	private int goods1;
	private int num1;
	private byte bind1;
	private int goods2;
	private int num2;
	private byte bind2;
	private int goods3;
	private int num3;
	private byte bind3;
	private int goods4;
	private int num4;
	private byte bind4;
	private int goods5;
	private int num5;
	private byte bind5;
	private int goods6;
	private int num6;
	private byte bind6;
	private int goods7;
	private int num7;
	private byte bind7;
	private int goods8;
	private int num8;
	private byte bind8;
	private int goods9;
	private int num9;
	private byte bind9;
	private int goods10;
	private int num10;
	private byte bind10;
	
	private List<GoodsOperateBean> goodsList;
	
	public boolean init(){
		boolean result = true;
		result = this.check(goods1, num1, bind1,result);
		result = this.check(goods2, num2, bind2,result);
		result = this.check(goods3, num3, bind3,result);
		result = this.check(goods4, num4, bind4,result);
		result = this.check(goods5, num5, bind5,result);
		result = this.check(goods6, num6, bind6,result);
		result = this.check(goods7, num7, bind7,result);
		result = this.check(goods8, num8, bind8,result);
		result = this.check(goods9, num9, bind9,result);
		result = this.check(goods10, num10, bind10,result);
		return result;
	}
	
	private boolean check(int goodsId, int num, byte bindType,boolean preCheck){
		if(goodsId > 0 && num > 0){
			if(goodsList == null){
				goodsList = new ArrayList<GoodsOperateBean>();
			}
			if(null == GameContext.getGoodsApp().getGoodsBase(goodsId)){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("active discount rewardId=" + rewardId + ", goodId=" + goodsId + " is not exsit!");
				return false;
			}
			goodsList.add(new GoodsOperateBean(goodsId, num, bindType));
		}
		return preCheck;
	}

	@Override
	public Integer getKey() {
		return this.rewardId;
	}
	
	/**
	 * 计算可以获得的钻石奖励
	 * @param roleDiscount
	 * @return
	 */
	public int calcRealGainGold(RoleDiscount roleDiscount){
		if(goldValueType == GOLD_VALUE_TYPE_GOLD){
			return this.goldMoney;
		}
		if(null == roleDiscount){
			return 0;
		}
		return roleDiscount.getTotalValue() * this.goldMoney / 100;
	}

	/**
	 * 获取属性奖励列表
	 * @param roleDiscount
	 * @return
	 */
	public List<AttriTypeValueItem> getAttriTypeValueList(RoleDiscount roleDiscount) {
		List<AttriTypeValueItem> list = Lists.newArrayList();
		if (this.silverMoney > 0) {
			list.add(this.createAttriTypeValueItem(AttributeType.gameMoney.getType(), this.getSilverMoney()));
		}
		if (this.goldMoney > 0) {
			list.add(this.createAttriTypeValueItem(AttributeType.goldMoney.getType(), this.calcRealGainGold(roleDiscount)));
		}
		return list;
	}
	
	/**
	 * 获取属性奖励
	 * @param attriType
	 * @param attriValue
	 * @return
	 */
	private AttriTypeValueItem createAttriTypeValueItem(byte attriType, int attriValue) {
		AttriTypeValueItem item = new AttriTypeValueItem();
		item.setAttriType(attriType);
		item.setAttriValue(attriValue);
		return item;
	}

}
