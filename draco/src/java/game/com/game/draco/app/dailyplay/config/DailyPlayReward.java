package com.game.draco.app.dailyplay.config;

import java.util.List;
import java.util.Map;

import lombok.Data;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.util.Initable;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;

import com.game.draco.GameContext;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public @Data class DailyPlayReward implements KeySupport<String>,Initable {

	private int playId;
	private short minRoleLevel;
	private short maxRoleLevel;
	private int goodsId1;
	private int goodsNum1;
	private byte bindType1;
	private int goodsId2;
	private int goodsNum2;
	private byte bindType2;
	private int goodsId3;
	private int goodsNum3;
	private byte bindType3;

	private byte attriType1;
	private int attriValue1;
	private byte attriType2;
	private int attriValue2;
	private byte attriType3;
	private int attriValue3;
	private int gameMoney;
	private int goldMoney;
	
	private Map<Byte,Integer> attriMap = Maps.newHashMap() ;
	private List<GoodsOperateBean> goodsList = Lists.newArrayList() ;
	
	private void initGoods(int goodsId,int goodsNum,byte bindType){
		if(goodsId <=0 || goodsNum <=0 ){
			return ;
		}
		if(!GameContext.getGoodsApp().isExistGoods(goodsId)){
			Log4jManager.CHECK.error("DailyPlayReward goods not exist,goodsId=" + goodsId);
			Log4jManager.checkFail();
		}
		goodsList.add(new GoodsOperateBean(goodsId,goodsNum,bindType)) ;
	}
	
	private void initAttri(byte attriType,int value) {
		if(attriType <=0 || value <=0 ){
			return ;
		}
		Integer v = attriMap.get(attriType) ;
		if(null == v){
			attriMap.put(attriType, value);
			return ;
		}
		attriMap.put(attriType, value + v) ;
	}
	
	@Override
	public String getKey() {
		return String.valueOf(this.playId + "_" + this.minRoleLevel + "_" + this.maxRoleLevel) ;
	}

	
	@Override
	public void init() {
		this.initGoods(goodsId1, goodsNum1, bindType1);
		this.initGoods(goodsId2, goodsNum2, bindType2);
		this.initGoods(goodsId3, goodsNum3, bindType3);
		
		this.initAttri(attriType1, attriValue1);
		this.initAttri(attriType2, attriValue2);
		this.initAttri(attriType3, attriValue3);
		this.initAttri(AttributeType.gameMoney.getType(), gameMoney);
		this.initAttri(AttributeType.goldMoney.getType(), goldMoney);
	}
}
