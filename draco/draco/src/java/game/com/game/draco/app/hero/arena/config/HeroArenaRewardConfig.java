package com.game.draco.app.hero.arena.config;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AttributeOperateBean;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.item.QuestAttrAwardItem;

@Data
public class HeroArenaRewardConfig {
	
	private int gateId;//关卡ID
	private int minLevel;//等级下限
	private int maxLevel;//等级上限
	private int exp;//经验
	private int silver;//金币
	private int potential;//潜能
	private int heroCoin;//徽章
	private int goodsId1;//物品ID
	private short goodsNum1;//物品数量
	private int goodsId2;//物品ID
	private short goodsNum2;//物品数量
	private int goodsId3;//物品ID
	private short goodsNum3;//物品数量
	private String mailTitle;//邮件标题
	private String mailContent;//邮件内容
	
	private List<AttributeOperateBean> attrList = new ArrayList<AttributeOperateBean>();
	private List<GoodsOperateBean> goodsList = new ArrayList<GoodsOperateBean>();
	
	public void checkInit(String fileInfo){
		String info = fileInfo + "gateId=" + this.gateId + ",";
		if(this.gateId <= 0){
			this.checkFail(info + "gateId is error.");
		}
		if(this.minLevel < 0){
			this.checkFail(info + "minLevel is error.");
		}
		if(this.maxLevel < 0){
			this.checkFail(info + "maxLevel is error.");
		}
		if(this.exp > 0){
			this.attrList.add(new AttributeOperateBean(AttributeType.exp, this.exp));
		}
		if(this.silver > 0){
			this.attrList.add(new AttributeOperateBean(AttributeType.silverMoney, this.silver));
		}
		if(this.potential > 0){
			this.attrList.add(new AttributeOperateBean(AttributeType.potential, this.potential));
		}
		if(this.heroCoin > 0){
			this.attrList.add(new AttributeOperateBean(AttributeType.heroCoin, this.heroCoin));
		}
		this.addToGoodsList(info, this.goodsId1, this.goodsNum1);
		this.addToGoodsList(info, this.goodsId2, this.goodsNum2);
		this.addToGoodsList(info, this.goodsId3, this.goodsNum3);
		if(Util.isEmpty(this.mailTitle)){
			this.checkFail(info + "mailTile is empty.");
		}
	}
	
	private void addToGoodsList(String info, int goodsId, short goodsNum){
		if(goodsId <= 0 || goodsNum <= 0){
			return;
		}
		if(null == GameContext.getGoodsApp().getGoodsBase(goodsId)){
			this.checkFail(info + "goodsId=" + goodsId + ", it's not exist!");
			return;
		}
		this.goodsList.add(new GoodsOperateBean(goodsId, goodsNum));
	}
	
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
	public boolean isSuitLevel(RoleInstance role){
		if(null == role){
			return false;
		}
		int roleLevel = role.getLevel();
		return roleLevel >= this.minLevel && roleLevel <= this.maxLevel;
	}
	
	public List<QuestAttrAwardItem> buildAttrAwardList(){
		List<QuestAttrAwardItem> awardAttrList = new ArrayList<QuestAttrAwardItem>();
		for(AttributeOperateBean bean : this.attrList){
			if(null == bean){
				continue;
			}
			QuestAttrAwardItem item = new QuestAttrAwardItem();
			item.setAttrType(bean.getAttrType().getType());
			item.setValue(bean.getValue());
			awardAttrList.add(item);
		}
		return awardAttrList;
	}
	
	public List<GoodsLiteNamedItem> buildAwardGoodsList(){
		List<GoodsLiteNamedItem> awardGoodsList = new ArrayList<GoodsLiteNamedItem>();
		for(GoodsOperateBean bean : this.goodsList){
			if(null == bean){
				continue;
			}
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(bean.getGoodsId());
			GoodsLiteNamedItem item = gb.getGoodsLiteNamedItem();
			item.setNum((short) bean.getGoodsNum());
			awardGoodsList.add(item);
		}
		return awardGoodsList;
	}
	
}
