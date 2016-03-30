package com.game.draco.app.goddess.config;

import com.game.draco.GameContext;
import com.game.draco.message.item.GoodsLiteNamedItem;

import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data class GoddessGrade implements KeySupport<Byte>{
	private byte grade; 
	private int goddessLv;
	private int goodsId;
	private short goodsNum;
	private int blessMax;
	private short attriAddRate;
	
	//最大阶数
	private static byte maxGrade;
	
	@Override
	public Byte getKey() {
		return this.grade;
	}
	
	public static void setMaxGrade(byte grade) {
		maxGrade = grade;
	}
	
	public static byte getMaxGrade() {
		return maxGrade;
	}
	
	public GoodsLiteNamedItem getUpgradeGoodsLiteNamedItem(){
		if(this.goodsId <= 0){
			return null;
		}
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(this.goodsId);
		if(null == gb){
			return null;
		}
		GoodsLiteNamedItem item = gb.getGoodsLiteNamedItem();
		item.setNum(this.goodsNum);
		return item;
	} 
}
