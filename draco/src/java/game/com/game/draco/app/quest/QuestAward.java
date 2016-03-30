package com.game.draco.app.quest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.exception.GoodsIsOnlyException;
import sacred.alliance.magic.app.goods.exception.OutOfGoodsBagException;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AttributeOperateBean;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.item.QuestAttrAwardItem;

public @Data class QuestAward {
	
	private Map<AttributeType,AttributeOperateBean> attributeMap = new HashMap<AttributeType,AttributeOperateBean>();
	private List<GoodsOperateBean> goodsList = new ArrayList<GoodsOperateBean>();
	
	/**
	 * 添加属性奖励
	 */
	public void addToAttributeMap(AttributeType attrType, int value){
		if(null == attrType || value <=0){
			return;
		}
		this.attributeMap.put(attrType, new AttributeOperateBean(attrType, value));
	}
	
	/**
	 * 添加励物品奖励
	 * @param goodsId
	 * @param goodsNum
	 * @param bind
	 */
	public void addToGoodsList(int goodsId, int goodsNum, int bind){
		if(goodsId <= 0 || goodsNum <= 0){
			return;
		}
		//验证物品是否存在，如果不存在则不让服务器启动。
		if(null == GameContext.getGoodsApp().getGoodsBase(goodsId)){
			Log4jManager.CHECK.error("quest award config error: goodsId = " + goodsId + ", goods not exist!");
			Log4jManager.checkFail();
			return;
		}
		this.goodsList.add(new GoodsOperateBean(goodsId, goodsNum, bind));
	}
	
	/**
	 * 获取属性奖励的值
	 * @param attrType
	 * @return
	 */
	public int getAttrAwardValue(AttributeType attrType){
		AttributeOperateBean bean = this.attributeMap.get(attrType);
		if(null == bean){
			return 0;
		}
		return bean.getValue();
	}
	
	/**
	 * 发任务奖励
	 * @param role
	 * @param quest
	 * @param delGoodsMap
	 * @return
	 * @throws GoodsIsOnlyException
	 * @throws OutOfGoodsBagException
	 * @throws ServiceException
	 */
	public Result execute(RoleInstance role, Quest quest, Map<Integer,Integer> delGoodsMap) throws GoodsIsOnlyException,OutOfGoodsBagException,ServiceException{
		List<GoodsOperateBean> addList = new ArrayList<GoodsOperateBean>();
		List<GoodsOperateBean> mustList = this.matchCareerGoods(this.goodsList, role);
		if(!Util.isEmpty(mustList)){
			addList.addAll(mustList);
		}
		/* 如果所需物品不够，则交任务会失败。无法满足付费完成任务的需求，改动如下。
		 * Result result = GameContext.getUserGoodsApp().addDelGoodsForBag(role, addList, 
				OutputConsumeType.quest_award, delGoodsMap, OutputConsumeType.quest_submit_consume);
		 */
		//先添加物品，添加失败则退出
		Result result = GameContext.getUserGoodsApp().addGoodsBeanForBag(role, addList, OutputConsumeType.quest_award);
		if(!result.isSuccess()){
			return result;
		}
		//再删除物品，删除角色所拥有的。有没有、够不够都会成功
		GameContext.getUserGoodsApp().deleteSomeForBagByMap(role, delGoodsMap, OutputConsumeType.quest_submit_consume);
		this.attributeAward(role,quest);
		return result.success();
	}
	
	/**
	 * 属性奖励
	 * @param role
	 * @param quest
	 */
	private void attributeAward(RoleInstance role, Quest quest){
		boolean notify = false;
		int exp = 0;//经验
		int gameMoney = 0;//金币
		int heroExp = 0 ;
		for(AttributeOperateBean bean : this.attributeMap.values()){
			if(null == bean){
				continue;
			}
			AttributeType attrType = bean.getAttrType();
			int value = bean.getValue();
			if(null == attrType || value <= 0){
				continue;
			}
			if(AttributeType.heroExp == attrType){
				//英雄经验
				heroExp += value ;
				continue ;
			}
//			//门派积分
//			if(AttributeType.factionIntegral == attrType){
//				QuestAwardChannel qa = new QuestAwardChannel(quest);
//				GameContext.getFactionApp().changeFactionIntegral(role, OperatorType.Add, value, qa, true);
//				continue;
//			}
//			//门派贡献
//			if(AttributeType.contribute == attrType){
//				GameContext.getFactionApp().changeContributeNum(role, OperatorType.Add, value);
//				continue;
//			}
//			//门派资金
//			if(AttributeType.factionMoney == attrType){
//				GameContext.getFactionFuncApp().changeFactionMoney(role, OperatorType.Add, value, OutputConsumeType.faction_money_quest);
//				continue;
//			}
			//角色属性
			GameContext.getUserAttributeApp().changeAttribute(role, attrType, OperatorType.Add, value, OutputConsumeType.quest_award);
			notify = true ;
			if(AttributeType.exp == attrType){
				exp += value;
			}
			if(AttributeType.gameMoney == attrType){
				gameMoney += value;
			}
		}
		if(notify){
			role.getBehavior().notifyAttribute();
		}
		if(heroExp>0){
			GameContext.getHeroApp().addHeroExp(role, heroExp);
		}
		//打日志
		if(exp > 0 || gameMoney > 0){
			GameContext.getStatLogApp().roleQuest(role, quest, exp, gameMoney);
		}
	}
	
	/**
	 * 物品过滤职业条件
	 * @param goods
	 * @param role
	 * @return
	 */
	private List<GoodsOperateBean> matchCareerGoods(List<GoodsOperateBean> goods, RoleInstance role){
		if(Util.isEmpty(goods)){
			return goods ;
		}
		List<GoodsOperateBean> result = new ArrayList<GoodsOperateBean>();
		for(GoodsOperateBean bean : goods){
			if(null == bean){
				continue;
			}
			int goodsId = bean.getGoodsId();
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsId);
			//过滤职业不匹配的
			if(null == gb || !gb.isCareerMatch(role.getCareer())){
				continue ;
			}
			result.add(bean);
		}
		return result;
	}

	/**
	 * 获取属性奖励显示信息
	 * @return
	 */
	public List<QuestAttrAwardItem> getAttrAwardList(){
		List<QuestAttrAwardItem> awardList = new ArrayList<QuestAttrAwardItem>();
		for(AttributeOperateBean bean : this.attributeMap.values()){
			if(null == bean){
				continue;
			}
			awardList.add(new QuestAttrAwardItem(bean.getAttrType().getType(), bean.getValue()));
		}
		return awardList;
	}
	
	/**
	 * 获取物品奖励显示信息
	 * @return
	 */
	public List<GoodsLiteNamedItem> getGoodsAwardList(RoleInstance role){
		List<GoodsLiteNamedItem> awardList = new ArrayList<GoodsLiteNamedItem>();
		if (Util.isEmpty(this.goodsList)) {
			return awardList;
		}
		for(GoodsOperateBean bean : this.goodsList){
			if(null == bean){
				continue;
			}
			int goodsId = bean.getGoodsId();
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsId);
			//过滤职业不匹配的
			if(null == gb || !gb.isCareerMatch(role.getCareer())){
				continue ;
			}
			GoodsLiteNamedItem item = gb.getGoodsLiteNamedItem();
			item.setBindType(bean.getBindType().getType());
			//设置数目
			item.setNum((short) bean.getGoodsNum());
			awardList.add(item);
		}
		return awardList;
	}
	
}
