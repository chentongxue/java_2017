package com.game.draco.app.recovery.config;

import lombok.Data;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;

import com.game.draco.GameContext;
import com.game.draco.app.recovery.IRecoveryInitable;
import com.game.draco.app.recovery.MultiKeySupport;
import com.game.draco.app.recovery.logic.IRecoveryLogic;
import com.game.draco.message.item.AttriTypeValueItem;
import com.game.draco.message.item.GoodsLiteNamedItem;
/**
 * 一键还原“产出”的配置
 */
public @Data class RecoveryOutPutConfig implements KeySupport<String>,IRecoveryInitable,MultiKeySupport<String>{
	public static final byte OUTPUT_ATTRIBUTE_TYPE = 1;
	public static final byte OUTPUT_GOODS_TYPE = 2;
	
	private String id;
	private short minLevel;
	private short maxLevel;
	private byte type; //1 为属性，2为物品
	private short outputId;
	private int a;

	public boolean meetCondition(int roleLevel) {
		if(roleLevel >= minLevel && roleLevel <= maxLevel){
			return true;
		}
		return false;
	}
	
	@Override
	public String getKey() {
		return String.valueOf(id) + Cat.underline + String.valueOf(type) 
		+ Cat.underline + String.valueOf(outputId)
		+ Cat.underline + String.valueOf(minLevel)
		+ Cat.underline + String.valueOf(maxLevel);
	}
	@Override
	public String getMultiKey(){
		return String.valueOf(id); 
	}
	public Object getAwardItem(){
		if(isAttribute()){
			AttriTypeValueItem at = new AttriTypeValueItem();
			at.setAttriType((byte)outputId);
			at.setAttriValue(a);
			return at;
		}
		if(isGoods()){
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(outputId);
			if(null == goodsBase){
				return null;
			}
			GoodsLiteNamedItem goodsLiteNamedItem = goodsBase.getGoodsLiteNamedItem();
			goodsLiteNamedItem.setNum((short)a);
//			goodsLiteNamedItem.setBindType(bind);
			return goodsLiteNamedItem;
		}
		return null;
	}

	public boolean isGoods() {
		return type == OUTPUT_GOODS_TYPE;
	}

	public boolean isAttribute() {
		return type == OUTPUT_ATTRIBUTE_TYPE;
	}
	public Object getAwardItem(int percentage){
		if(isAttribute()){
			AttriTypeValueItem at = new AttriTypeValueItem();
			at.setAttriType((byte)outputId);
			at.setAttriValue(getValue(percentage));
			return at;
		}
		if(isGoods()){
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(outputId);
			if(null == goodsBase){
				return null;
			}
			GoodsLiteNamedItem goodsLiteNamedItem = goodsBase.getGoodsLiteNamedItem();
			goodsLiteNamedItem.setNum((short)getValue(percentage));
//			goodsLiteNamedItem.setBindType(bind);
			return goodsLiteNamedItem;
		}
		return null;
	}
	public int getValue(int percentage){
		double ratio = (double)percentage/IRecoveryLogic.PERCENTS;
		return ((int)Math.ceil(a * ratio));
	}
	public int getValue(int percentage, int num){
		double ratio = (double)percentage/IRecoveryLogic.PERCENTS;
		return ((int)Math.ceil(a * num * ratio));
	}
	@Override
	public void init(){
		if(!checkType()){
			checkFail("onekey recovery recoveryOutPutConfig init  err," + "type = " + type);
		}
		//属性
		if(checkAttributeFail()){
			checkFail("onekey recovery recoveryOutPutConfig init  err," + "type = " 
					+ type 
					+ ", outputId "
					+ " is not attribute type!");
		}
		//物品
		if(checkGoodsFail())
		{
			checkFail("onekey recovery recoveryOutPutConfig init  err," + "outputId =" + outputId + " is not exsit!");
		}
	}
	private boolean checkType(){
		return (isAttribute() || isGoods());
	}
	private boolean checkAttributeFail(){
		return (isAttribute() && AttributeType.get((byte)outputId) == null);
	}
	private boolean checkGoodsFail(){
		return (isGoods() && GameContext.getGoodsApp().getGoodsBase(outputId) == null);
	}
	public boolean checkSuccess(){
		if(!checkType() || checkAttributeFail() ||checkGoodsFail()){
			return false;
		}
		return true;
	}
	private void checkFail(String errInfo) {
		Log4jManager.CHECK.error(errInfo);
		Log4jManager.checkFail();
	}
}
