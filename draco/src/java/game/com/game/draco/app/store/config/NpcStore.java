package com.game.draco.app.store.config;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.item.NpcStoreConsumeItem;

public @Data class NpcStore {
	private final static Logger logger = LoggerFactory.getLogger(NpcStore.class);
	private String npcTemplateId;//	npc模板ID
	private int showType;//类型
	private int goodsId;//物品ID
	private byte bindType = -1;//绑定类型
	private byte consumeType1;//消耗类型1	
	private int value1;//值
	private byte consumeType2;//消耗类型2
	private int value2;//值
	private int defaultBuyNum = 1 ;// 默认购买数
	

	private AttributeType consumeEnum1 ;
	private AttributeType consumeEnum2 ;
	
	//所以条件list
	public boolean isTemplateBindType(){
		return this.bindType == -1 ;
	}
	
	private BindingType getBindingType(int goodsId){
		if(this.isTemplateBindType()){
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsId);
			return gb.getBindingType();
		}
		return BindingType.get(this.bindType);
	}
	
	private AttributeType init(byte consumeType,int value){
		if(value <=0){
			return null ;
		}
		AttributeType ret = AttributeType.get(consumeType);
		if(null == ret){
			Log4jManager.CHECK.error("npc store consumeType config error,consumeType=" + consumeType);
			Log4jManager.checkFail() ;
		}
		return ret ;
	}
	
	public void init(){
		if(this.defaultBuyNum <=0){
			this.defaultBuyNum = 1 ;
		}
		this.consumeEnum1 = this.init(consumeType1, value1);
		this.consumeEnum2 = this.init(consumeType2, value2);
		if(value1 <=0 && value2<=0){
			Log4jManager.CHECK.error("npc store config error,consume value all <= 0 ");
			Log4jManager.checkFail() ;
		}
	}
	
	private Result isEnough(RoleInstance role,AttributeType attriType,int value){
		if(role.get(attriType) >= value){
			return new Result().success();
		}
		//【游戏币/潜能/钻石不足弹板】 判断
		Result ar = GameContext.getUserAttributeApp().getEnoughResult(role, attriType, value);
		if(ar.isIgnore()){
			return ar;
		}
		if(!ar.isSuccess()){
			return ar.setInfo(attriType.getName()+GameContext.getI18n().getText(TextId.NPC_STORE_NOT_ENOUGH));
		}
		return new Result().success();
	}
	
	private void consume(RoleInstance role, AttributeType attriType, int value) {
		if (value <= 0) {
			return;
		}
		if (attriType.isMoney()) {
			GameContext.getUserAttributeApp().changeRoleMoney(role, attriType,
					OperatorType.Decrease, value,
					OutputConsumeType.npc_store_buy_consume);
			return;
		}
		GameContext.getUserAttributeApp().changeAttribute(role, attriType,
				OperatorType.Decrease, value,
				OutputConsumeType.npc_store_buy_consume);
	}
	
	/**购买物品*/
	public Result buy(RoleInstance role,int buyNum){
		Result result = new Result().failure();
		try {
			int v1 = value1 * buyNum ;
			int v2 = value2 * buyNum ;
			if(v1 < 0 || v2 < 0 ||(v1==0 && v2==0)){
				result.setInfo(GameContext.getI18n().getText(TextId.NPC_STORE_MONEY_ERROR));
				return result ;
			}
			AttributeType attr1 = AttributeType.get(consumeType1);
			result = this.isEnough(role, attr1, v1) ;
			if(!result.isSuccess()){
				return result ;
			}
			AttributeType attr2 = AttributeType.get(consumeType2);
			result = this.isEnough(role, attr2, v2) ;
			if(!result.isSuccess()){
				return result ;
			}
			//判断背包是否满
			List<GoodsOperateBean> goodsList = Lists.newArrayList() ;
			goodsList.add(new GoodsOperateBean(goodsId,buyNum,bindType)) ;

			if(!GameContext.getUserGoodsApp().canPutGoodsBean(role,goodsList)){
				//提示背包已满
				return result.setInfo(GameContext.getI18n().getText(TextId.Bag_Is_Full)) ;
			}
			//扣除消耗
			this.consume(role, attr1, v1);
			this.consume(role, attr2, v2);
			
			BindingType bingType = this.getBindingType(goodsId);;
			//给物品
			GoodsResult gr = GameContext.getUserGoodsApp().addGoodsForBag(role, goodsId, buyNum, bingType,
					OutputConsumeType.npc_store_buy_goods);
			if(!gr.isSuccess()){
				return result.setInfo(gr.getInfo());
			}
			GoodsBase base = GameContext.getGoodsApp().getGoodsBase(goodsId);
			String message = GameContext.getI18n().messageFormat(TextId.NPC_STORE_BUY_MSG, buyNum, base.getName());
			String cat = "" ;
			if(v1 > 0){
				message += AttributeType.get(consumeType1).getName() + ":" + v1;
				cat = "," ;
			}
			if(v2 > 0){
				message += cat + AttributeType.get(consumeType2).getName() + ":" + v2;
			}
			role.getBehavior().notifyAttribute();
			result.success();
			return result.setInfo(message) ;
		} catch (Exception e) {
			logger.error("",e);
			return result.setInfo(GameContext.getI18n().getText(TextId.Sys_Error)) ;
		}
		
	}
	
	/**消耗列表*/
	public List<NpcStoreConsumeItem> getConsumeList(){
		List<NpcStoreConsumeItem> consumeList = new ArrayList<NpcStoreConsumeItem>();
		if(value1 > 0){
			NpcStoreConsumeItem item = new NpcStoreConsumeItem();
			item.setCount(value1);
			item.setType(consumeType1);
			consumeList.add(item);
		}
		if(value2 > 0){
			NpcStoreConsumeItem item = new NpcStoreConsumeItem();
			item.setCount(value2);
			item.setType(consumeType2);
			consumeList.add(item);
		}
		return consumeList;
	}
	
}
