package com.game.draco.app.quest.poker.config;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.app.quest.QuestAward;

import lombok.Data;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.Result;

public @Data class RmQuestAwardConfig {
	
	private int awardId;//奖励ID
	private int exp;//经验
	private int zp;//真气
	private int gameMoney;//游戏币
	private int bindGold;//绑金
	private int contribute;//帮派贡献度
	private int factionMoney;//门派资金
	private int goodsId1;//物品1ID
	private int goodsNum1;//物品1数量
	private byte bind1;//"物品1绑定类型
	private int goodsId2;//物品2ID
	private int goodsNum2;//物品2数量
	private byte bind2;//"物品2绑定类型
	private int goodsId3;//物品3ID
	private int goodsNum3;//物品3数量
	private byte bind3;//"物品3绑定类型
	
	//任务奖励
	private QuestAward questAward = new QuestAward();
	/** 奖励物品列表 */
	private List<GoodsOperateBean> goodsList = new ArrayList<GoodsOperateBean>();
	
	/**
	 * 验证并初始化配置
	 * @return
	 */
	public Result checkAndInit(){
		Result result = new Result();
		String info = "awardId=" + this.awardId + ".";
		if(this.awardId <= 0){
			return result.setInfo(info);
		}
		//添加物品奖励
		this.questAward.addToGoodsList(this.goodsId1, this.goodsNum1, this.bind1);
		this.questAward.addToGoodsList(this.goodsId2, this.goodsNum2, this.bind2);
		this.questAward.addToGoodsList(this.goodsId3, this.goodsNum3, this.bind3);
		//添加属性奖励
		this.questAward.addToAttributeMap(AttributeType.exp, this.exp);
		this.questAward.addToAttributeMap(AttributeType.potential, this.zp);
		this.questAward.addToAttributeMap(AttributeType.gameMoney, this.gameMoney);
//		this.questAward.addToAttributeMap(AttributeType.contribute, this.contribute);
		this.questAward.addToAttributeMap(AttributeType.factionMoney, this.factionMoney);
		return result.success();
	}
	
}
