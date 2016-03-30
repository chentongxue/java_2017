package com.game.draco.app.choicecard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lombok.Getter;

import org.python.google.common.collect.Lists;
import org.python.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.behavior.result.AddGoodsBeanResult;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.QualityType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsHero;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.app.choicecard.activity.config.ActivityShow;
import com.game.draco.app.choicecard.base.BaseConsume;
import com.game.draco.app.choicecard.base.BaseLeaf;
import com.game.draco.app.choicecard.base.BaseMain;
import com.game.draco.app.choicecard.base.BasePreview;
import com.game.draco.app.choicecard.base.BaseTree;
import com.game.draco.app.choicecard.base.BaseTreeItem;
import com.game.draco.app.choicecard.domain.RoleChoiceCard;
import com.game.draco.app.choicecard.domain.RoleChoiceCardLuck;
import com.game.draco.app.dailyplay.DailyPlayType;
import com.game.draco.app.forward.logic.ClientLogicForwardType;
import com.game.draco.app.hint.vo.HintType;
import com.game.draco.app.mail.type.MailSendRoleType;
import com.game.draco.message.item.ActivityCardItem;
import com.game.draco.message.item.CommonCardItem;
import com.game.draco.message.item.GoodsLiteNamedExItem;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.item.HintTimeNotifyItem;
import com.game.draco.message.response.C1348_CommonRewardRespMessage;
import com.game.draco.message.response.C2809_CardPreviewRespMessage;
import com.game.draco.message.response.C2810_CardGoldRespMessage;
import com.game.draco.message.response.C2811_CardGemRespMessage;
import com.game.draco.message.response.C2812_CardActivityRespMessage;


public class RoleChoiceCardAppImpl implements RoleChoiceCardApp {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	//Map<角色ID,Map<大类型,Map<小类型,数据>>>
	@Getter private Map<Integer,Map<Byte,Map<Byte,RoleChoiceCard>>> roleChoiceCardMap = Maps.newConcurrentMap();
	
	//Map<角色ID,Map<大类型,幸运值>>
	@Getter private Map<Integer,Map<Byte,RoleChoiceCardLuck>> roleChoiceCardLuckMap = Maps.newConcurrentMap();
	
	private void saveOrUpd(RoleChoiceCard card){
		try{
			//获取数据
			Map<Byte,Map<Byte,RoleChoiceCard>> choiceCardMap = roleChoiceCardMap.get(card.getRoleId());
			Map<Byte,RoleChoiceCard> map = choiceCardMap.get(card.getType());
			map.put(card.getSpecificType(), card);
			GameContext.getBaseDAO().saveOrUpdate(card);
		}catch(Exception ex){
			logger.error("saveOrUpd",ex);
		}
	}
	
	private void saveOrUpdLuck(RoleChoiceCardLuck roleLuck){
		try{
			Map<Byte,RoleChoiceCardLuck> luckMap = null;
			if(roleChoiceCardLuckMap.containsKey(roleLuck.getRoleId())){
				luckMap = roleChoiceCardLuckMap.get(roleLuck.getRoleId());
				luckMap.put(roleLuck.getType(),roleLuck);
			}else{
				luckMap = Maps.newConcurrentMap();
				luckMap.put(roleLuck.getType(),roleLuck);
				roleChoiceCardLuckMap.put(roleLuck.getRoleId(), luckMap);
			}
			GameContext.getBaseDAO().saveOrUpdate(roleLuck);
		}catch(Exception ex){
			logger.error("saveOrUpd",ex);
		}
	}
	
	@Override
	public Result choiceCard(RoleInstance role,byte funType,byte specificType){
		Result result = new Result();
		try{
			
			RoleChoiceCard roleChoiceCard = getRoleChoiceCard(role,funType,specificType);
			
			if(roleChoiceCardMap.containsKey(role.getIntRoleId())){
				result = isChoiceCondition(role,funType,specificType);
			}
			
			if(!result.isSuccess()||result.isIgnore()){
				return result;
			}
			
			//金币
			if(funType == ChoiceFunType.GOLD.ordinal()){
				result = choiceGoldCard(role,roleChoiceCard);
			}
			
			//钻石
			if(funType == ChoiceFunType.GEM.ordinal()){
				result = choiceGemCard(role,roleChoiceCard);
			}
			
			//活动
			if(funType == ChoiceFunType.ACTIVITY.ordinal()){
				result = choiceActivityCard(role,roleChoiceCard);
			}
			
			saveOrUpd(roleChoiceCard);
			
			int times = 1;
			if(specificType == 2){
				times = 10;
			}
			//活跃度
			GameContext.getDailyPlayApp().incrCompleteTimes(role, times, DailyPlayType.choice_card, "");
		}catch(Exception ex){
			logger.error("choiceCard",ex);
		}
		
		getHintTimeNotifyList(role);
		
		return result;
	}
	
	/**
	 * 初始化抽卡
	 * @param role
	 * @param funType
	 * @param specificType
	 * @return
	 */
	private RoleChoiceCard initRoleChoiceCard(RoleInstance role,byte funType,byte specificType){
		RoleChoiceCard roleChoiceCard = new RoleChoiceCard();
		roleChoiceCard.setRoleId(role.getIntRoleId());
		roleChoiceCard.setSpecificType(specificType);
		roleChoiceCard.setType(funType);
		return roleChoiceCard;
	}

	@Override
	public Result isChoiceCondition(RoleInstance role, byte funType,byte specificType) {
		Result result = new Result();
		try{
			RoleChoiceCard roleChoiceCard = null;
			Map<Byte,Map<Byte,RoleChoiceCard>> choiceCardMap = roleChoiceCardMap.get(role.getIntRoleId());
			
			if(choiceCardMap.containsKey(funType)){
				
				Map<Byte,RoleChoiceCard> map = choiceCardMap.get(funType);
				
				//金币
				if(funType == ChoiceFunType.GOLD.ordinal()){
						roleChoiceCard = map.get(specificType);
				}
				//钻石
				if(funType == ChoiceFunType.GEM.ordinal()){
						roleChoiceCard = map.get(specificType);
				}
				//活动
				if(funType == ChoiceFunType.ACTIVITY.ordinal()){
						roleChoiceCard = map.get(specificType);
				}
				
				if(roleChoiceCard != null){
					result = isCondition(role,roleChoiceCard,specificType);
				}
			}
			
		}catch(Exception e){
			logger.error("isChoiceCondition",e);
		}
		return result;
	}
	
	/**
	 * 抽卡验证
	 * @param role
	 * @param roleChoiceCard
	 * @param funType
	 * @return
	 */
	private Result isCondition(RoleInstance role,RoleChoiceCard roleChoiceCard,byte specificType){
		Result result = new Result();
		
		try{
		
			BaseConsume consume = null;
			if(roleChoiceCard.getType() == ChoiceFunType.GOLD.ordinal()){
				//默认都是免费抽
				BaseMain main = GameContext.getChoiceCardApp().getGameMoneyMain((byte)GoldSpecificType.FREE.ordinal());
				if(specificType == GoldSpecificType.SPEND.ordinal()){
					if(main.getFreeNum() != -1){
						if(roleChoiceCard.getFreeNum() < main.getFreeNum()
								&& System.currentTimeMillis() > (roleChoiceCard.getCdTime() + main.getCdTime())){
							specificType = (byte)GoldSpecificType.FREE.ordinal();
						}
					}
				}
				main = GameContext.getChoiceCardApp().getGameMoneyMain(specificType);
				consume = GameContext.getChoiceCardApp().getGameMoneyConsume(specificType, role.getLevel());
				if(consume == null){
					consume = GameContext.getChoiceCardApp().getMaxGameMoneyConsume(specificType);
				}
			}
			if(roleChoiceCard.getType() == ChoiceFunType.GEM.ordinal()){
				BaseMain main = GameContext.getChoiceCardApp().getGemMain((byte)GemSpecificType.FREE.ordinal());
				if(specificType == GemSpecificType.SPEND.ordinal()){
					if(main.getFreeNum() != -1){
						if(roleChoiceCard.getFreeNum() < main.getFreeNum()
								&& System.currentTimeMillis() > (roleChoiceCard.getCdTime() + main.getCdTime())){
							specificType = (byte)GemSpecificType.FREE.ordinal();
						}
					}
				}
				consume = GameContext.getChoiceCardApp().getGemConsume(specificType, roleChoiceCard.getNum() + 1);
				if(consume == null){
					consume = GameContext.getChoiceCardApp().getMaxGemConsume(specificType);
				}
			}
			if(roleChoiceCard.getType() == ChoiceFunType.ACTIVITY.ordinal()){
				BaseMain main = GameContext.getChoiceCardApp().getActivityMain(specificType);
				if(specificType == ActivitySpecificType.SPEND.ordinal()){
					if(main.getFreeNum() != -1){
						if(roleChoiceCard.getFreeNum() < main.getFreeNum()
								&& System.currentTimeMillis() > (roleChoiceCard.getCdTime() + main.getCdTime())){
							specificType = (byte)ActivitySpecificType.FREE.ordinal();
						}
					}
				}
				consume = GameContext.getChoiceCardApp().getActivityConsume(specificType, roleChoiceCard.getNum() + 1);
				if(consume == null){
					consume = GameContext.getChoiceCardApp().getMaxActivityConsume(specificType);
				}
			}
			//获取消耗类型 0金币 1钻石
			//【游戏币/潜能/钻石不足弹板】判断
			byte consumptionType = consume.getConsumptionType();
			if(consumptionType == AttributeType.gameMoney.getType()){
				Result ar = GameContext.getUserAttributeApp().getEnoughResult(role, AttributeType.gameMoney, consume.getConsumption());
				if(ar.isIgnore()){
					return ar;
				}
				if(!ar.isSuccess()){
					return ar.setInfo(GameContext.getI18n().messageFormat(TextId.CARD_GOLD_MONEY_ERR));
				}
//				if(role.getSilverMoney() < consume.getConsumption()){
//					result.setInfo(GameContext.getI18n().messageFormat(TextId.CARD_GOLD_MONEY_ERR));
//					return result;
//				}
			}
			if(consumptionType == AttributeType.goldMoney.getType()){
				Result ar = GameContext.getUserAttributeApp().getEnoughResult(role, AttributeType.goldMoney, consume.getConsumption());
				if(ar.isIgnore()){
					return ar;
				}
				if(!ar.isSuccess()){
					return ar.setInfo(GameContext.getI18n().messageFormat(TextId.CARD_GEM_MONEY_ERR));
				}
//				if(role.getGoldMoney() < consume.getConsumption()){
//					result.setInfo(GameContext.getI18n().messageFormat(TextId.CARD_GEM_MONEY_ERR));
//					return result;
//				}
			}
			
			result.success();
			
		}catch(Exception e){
			logger.error("isGoldCondition",e);
		}
		return result;
	}
	
	/**
	 * 金币抽卡
	 * @param role
	 * @param funType
	 * @param choiceType
	 * @return
	 */
	private Result choiceGoldCard(RoleInstance role,RoleChoiceCard roleChoiceCard){
		Result result = new Result();
		
		try{
			
			//金币抽或者十连抽
			byte specificType = roleChoiceCard.getSpecificType();
			
			//默认都是免费抽
			BaseMain goldMain = GameContext.getChoiceCardApp().getGameMoneyMain((byte)GoldSpecificType.FREE.ordinal());
			if(specificType == GoldSpecificType.SPEND.ordinal()){
				if(goldMain.getFreeNum() != -1){
					if(roleChoiceCard.getFreeNum() < goldMain.getFreeNum() 
							&& System.currentTimeMillis() > (roleChoiceCard.getCdTime() + goldMain.getCdTime())){
						specificType = (byte)GoldSpecificType.FREE.ordinal();
					}
				}
			}
			goldMain = GameContext.getChoiceCardApp().getGameMoneyMain(specificType);
			
			if(roleChoiceCard.getFreeNum() >= goldMain.getFreeNum()){
				//消费抽卡逻辑
				BaseConsume goldConsume = GameContext.getChoiceCardApp().getGameMoneyConsume(specificType, role.getLevel());
				if(goldConsume == null){
					goldConsume = GameContext.getChoiceCardApp().getMaxGameMoneyConsume(specificType);
				}
				
				if(goldConsume != null){
				
					//获取消耗类型 0金币 1钻石
					decrease(role,goldConsume);
				}
			}
			
			choiceGoldLogic(role,roleChoiceCard);
			
			result.success();
		}catch(Exception e){
			logger.error("choiceGoldCard",e);
		}
		return result;
	}
	
	/**
	 * 金币抽卡
	 * @param role
	 * @param funType
	 * @param choiceType
	 * @return
	 */
	private Result choiceGemCard(RoleInstance role,RoleChoiceCard roleChoiceCard){
		Result result = new Result();
		
		try{
			
			//金币抽或者十连抽
			byte specificType = roleChoiceCard.getSpecificType();
			
			//默认都是免费抽
			BaseMain gemMain = GameContext.getChoiceCardApp().getGemMain((byte)GemSpecificType.FREE.ordinal());
			if(specificType == GemSpecificType.SPEND.ordinal()){
				if(gemMain.getFreeNum() != -1){
					if(roleChoiceCard.getFreeNum() < gemMain.getFreeNum()
							&& System.currentTimeMillis() > (roleChoiceCard.getCdTime() + gemMain.getCdTime())){
						specificType = (byte)GemSpecificType.FREE.ordinal();
					}
				}
			}
			gemMain = GameContext.getChoiceCardApp().getGemMain(specificType);
			
			if(roleChoiceCard.getFreeNum() > gemMain.getFreeNum()){
				//消费抽卡逻辑
				BaseConsume gemConsume = GameContext.getChoiceCardApp().getGemConsume(specificType, roleChoiceCard.getNum() + 1);
				if(gemConsume == null){
					gemConsume = GameContext.getChoiceCardApp().getMaxGemConsume(specificType);
				}
				
				if(gemConsume != null){
				
					//获取消耗类型 0金币 1钻石
					decrease(role,gemConsume);
				}
			}
			
			choiceGemLogic(role,roleChoiceCard);
			
			result.success();
		}catch(Exception e){
			logger.error("choiceGoldCard",e);
		}
		return result;
	}
	
	private void decrease(RoleInstance role,BaseConsume consume){
		if(consume.getConsumptionType() == AttributeType.gameMoney.getType()){
			//扣除消耗
			GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.gameMoney, 
					OperatorType.Decrease, consume.getConsumption(), OutputConsumeType.choice_gold_money);
		}else if(consume.getConsumptionType() == AttributeType.goldMoney.getType()){
			//扣除消耗
			GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.goldMoney, 
					OperatorType.Decrease, consume.getConsumption(), OutputConsumeType.choice_gem_money);
		}
		//通知用户属性变化
		role.getBehavior().notifyAttribute();
	}
	
	/**
	 * 活动抽卡
	 * @param role
	 * @param funType
	 * @param choiceType
	 * @return
	 */
	private Result choiceActivityCard(RoleInstance role,RoleChoiceCard roleChoiceCard){
		Result result = new Result();
		
		try{
			
			//活动抽卡
			byte specificType = roleChoiceCard.getSpecificType();
			
			BaseConsume activityConsume = null;
			
			//消费抽卡逻辑
			activityConsume = GameContext.getChoiceCardApp().getActivityConsume(specificType, roleChoiceCard.getNum() + 1);
			if(activityConsume == null){
				activityConsume = GameContext.getChoiceCardApp().getMaxActivityConsume(specificType);
			}
			
			if(activityConsume != null){
				//获取消耗类型 0金币 1钻石
				decrease(role,activityConsume);
			}
			choiceActivityLogic(role,roleChoiceCard);
			
			result.success();
		}catch(Exception e){
			logger.error("choiceGoldCard",e);
		}
		return result;
	}
	
	/**
	 * 检查记录是否存在
	 * @param role
	 * @param funType
	 * @param choiceType
	 */
	private void checkHaveRecord(RoleInstance role,byte funType,byte specificType){
		try{
			RoleChoiceCard roleChoiceCard = null;
			Map<Byte,RoleChoiceCard> map = null;
			Map<Byte,Map<Byte,RoleChoiceCard>> choiceCardMap = roleChoiceCardMap.get(role.getIntRoleId());

			if(Util.isEmpty(choiceCardMap)){
				choiceCardMap = Maps.newConcurrentMap();
				map = Maps.newConcurrentMap();
				roleChoiceCard = initRoleChoiceCard(role,funType,specificType);
				map.put(specificType, roleChoiceCard);
				choiceCardMap.put(funType, map);
				roleChoiceCardMap.put(role.getIntRoleId(), choiceCardMap);
				return;
			}
			
			if(!choiceCardMap.containsKey(funType)){
				roleChoiceCard = initRoleChoiceCard(role,funType,specificType);
				map = Maps.newConcurrentMap();
				map.put(specificType, roleChoiceCard);
				choiceCardMap.put(funType, map);
				return;
			}
			
			if(choiceCardMap.containsKey(funType)){
				map = choiceCardMap.get(funType);
				if(!map.containsKey(specificType)){
					roleChoiceCard = initRoleChoiceCard(role,funType,specificType);
					map.put(specificType, roleChoiceCard);
				}
			}
			
		}catch(Exception e){
			logger.error("isHaveRecord",e);
		}
	}
	
	private RoleChoiceCard getRoleChoiceCard(RoleInstance role,byte funType,byte specificType){
		try{
			//检查数据
			checkHaveRecord(role,funType,specificType);
			
			//获取数据
			Map<Byte,Map<Byte,RoleChoiceCard>> choiceCardMap = roleChoiceCardMap.get(role.getIntRoleId());
			Map<Byte,RoleChoiceCard> map = choiceCardMap.get(funType);
			RoleChoiceCard roleChoiceCard = map.get(specificType);
			return roleChoiceCard;
			
		}catch(Exception e){
			logger.error("getRoleChoiceCard",e);
		}
		return null;
	}
	
	/**
	 * 金币抽卡逻辑
	 */
	private void choiceGoldLogic(RoleInstance role,RoleChoiceCard roleChoiceCard){
		try{
			
			//金币抽或者十连抽
			byte specificType = roleChoiceCard.getSpecificType();
			
			RoleChoiceCardLuck roleLuck = getRoleChoiceCardLuck(role.getIntRoleId(),roleChoiceCard.getType());
			
			//默认都是免费抽
			BaseMain goldMain = GameContext.getChoiceCardApp().getGameMoneyMain((byte)GoldSpecificType.FREE.ordinal());
			if(specificType == GoldSpecificType.SPEND.ordinal()){
				if(goldMain.getFreeNum() != -1){
					if(roleLuck.getGoldFirstNum() <= 0 ){
						specificType = (byte)GoldSpecificType.FIEST_FREE.ordinal();
					}else if(roleChoiceCard.getFreeNum() < goldMain.getFreeNum() 
							&& System.currentTimeMillis() > (roleChoiceCard.getCdTime() + goldMain.getCdTime())){
						specificType = (byte)GoldSpecificType.FREE.ordinal();
					}
				}
			}
			
			if(specificType == GemSpecificType.FREE.ordinal()){
				roleChoiceCard.setFreeNum(roleChoiceCard.getFreeNum() + 1);
				roleChoiceCard.setCdTime(System.currentTimeMillis());
			}else if(specificType == GemSpecificType.SPEND.ordinal() 
					|| specificType == GemSpecificType.TEN.ordinal()){
				roleChoiceCard.setNum(roleChoiceCard.getNum() + 1);
			}else if(specificType == GoldSpecificType.FIEST_FREE.ordinal()){
				roleLuck.setGoldFirstNum(1);
				roleChoiceCard.setFreeNum(roleChoiceCard.getFreeNum() + 1);
				roleChoiceCard.setCdTime(System.currentTimeMillis());
			}
			
			goldMain = GameContext.getChoiceCardApp().getGameMoneyMain(specificType);
			
			iterationTree(role ,goldMain,roleChoiceCard);
			
			BaseMain main = GameContext.getChoiceCardApp().getGameMoneyMain((byte)GoldSpecificType.FREE.ordinal());
			//金币
			if(main.getFreeNum() != -1){
				List<HintTimeNotifyItem> list = getHintTimeNotifyList(role);
				for(HintTimeNotifyItem item : list){
					if (null == item) {
						continue;
					}
					GameContext.getHintApp().pushHintTimeChangeMessage(role, item);
				}
			}
			
		}catch(Exception e){
			logger.error("",e);
		}
	}
	
	/**
	 * 钻石抽卡逻辑
	 */
	private void choiceGemLogic(RoleInstance role,RoleChoiceCard roleChoiceCard){
		try{

			//钻石抽或者十连抽
			byte specificType = roleChoiceCard.getSpecificType();
			RoleChoiceCardLuck roleLuck = getRoleChoiceCardLuck(role.getIntRoleId(),roleChoiceCard.getType());
			//默认都是免费抽
			BaseMain gemMain = GameContext.getChoiceCardApp().getGemMain((byte)GemSpecificType.FREE.ordinal());
			if(specificType == GemSpecificType.SPEND.ordinal()){
				if(gemMain.getFreeNum() != -1){
					if(roleLuck.getFreeNum() <= 0 ){
						specificType = (byte)GemSpecificType.GEM_FREE.ordinal();
					}else if(roleChoiceCard.getFreeNum() < gemMain.getFreeNum()){
							specificType = (byte)GemSpecificType.FREE.ordinal();
					}
				}else{
					if(roleChoiceCard.getNum() == 0){
						specificType = (byte)GemSpecificType.GEM_SPEND.ordinal();
					}
				}
			}
			
			if(specificType == GemSpecificType.SPEND.ordinal() 
					|| specificType == GemSpecificType.GEM_SPEND.ordinal()
					|| specificType == GemSpecificType.TEN.ordinal()){
				roleChoiceCard.setNum(roleChoiceCard.getNum() + 1);
			}else if(specificType == GemSpecificType.GEM_FREE.ordinal() 
					|| specificType == GemSpecificType.FREE.ordinal()){
				roleChoiceCard.setFreeNum(roleChoiceCard.getFreeNum() + 1);
				roleChoiceCard.setCdTime(System.currentTimeMillis());
			}
			if(specificType == GemSpecificType.GEM_FREE.ordinal()){
				roleLuck.setFreeNum(roleLuck.getFreeNum() + 1);
			}
			
			saveOrUpdLuck(roleLuck);
			
			gemMain = GameContext.getChoiceCardApp().getGemMain(specificType);
			iterationTree(role ,gemMain,roleChoiceCard);
			
			BaseMain main = GameContext.getChoiceCardApp().getGemMain((byte)GemSpecificType.FREE.ordinal());
			//钻石
			if(main.getFreeNum() != -1){
				List<HintTimeNotifyItem> list = getHintTimeNotifyList(role);
				for(HintTimeNotifyItem item : list){
					if (null == item) {
						continue;
					}
					GameContext.getHintApp().pushHintTimeChangeMessage(role, item);
				}
			}
			
		}catch(Exception e){
			logger.error("",e);
		}
	}
	
	/**
	 * 活动抽卡逻辑
	 */
	private void choiceActivityLogic(RoleInstance role,RoleChoiceCard roleChoiceCard){
		try{
			
			//活动抽
			byte specificType = roleChoiceCard.getSpecificType();
			
			roleChoiceCard.setNum(roleChoiceCard.getNum() + 1);
			
			BaseMain activityMain = GameContext.getChoiceCardApp().getActivityMain(specificType);
			
			iterationTree(role ,activityMain,roleChoiceCard);
			
		}catch(Exception e){
			logger.error("",e);
		}
	}
	
	private void iterationTree(RoleInstance role, BaseMain baseMain, RoleChoiceCard roleChoiceCard){
		
		List<GoodsOperateBean> goodsList = new ArrayList<GoodsOperateBean>();
		
		RoleChoiceCardLuck roleLuck = getRoleChoiceCardLuck(role.getIntRoleId(),roleChoiceCard.getType());
		
		for(BaseTreeItem tree : baseMain.getList()){
			int treeId = tree.getId();
			int treeNum = tree.getNum();
			//不受幸运值影响
			int i = 0;
			
			BaseTree baseTree = null;
			int leafId = 0;
			for(;i<treeNum;i++){
				if(roleLuck.getLuck() > 5){
					roleLuck.setLuck(0);
				}
				String key = treeId + Cat.underline + roleLuck.getLuck();
				if(tree.getLuckType() == (byte)0){
					key = treeId + Cat.underline + 0;
				}
				
				if(roleChoiceCard.getType() == ChoiceFunType.GOLD.ordinal()){
					baseTree = GameContext.getChoiceCardApp().getGameMoneyTree(key);
				}else if(roleChoiceCard.getType() == ChoiceFunType.GEM.ordinal()){
					baseTree = GameContext.getChoiceCardApp().getGemTree(key);
				}else if(roleChoiceCard.getType() == ChoiceFunType.ACTIVITY.ordinal()){
					baseTree = GameContext.getChoiceCardApp().getActivityTree(key);
				}
				
				boolean flag = true;
				
				BaseTree bTree = baseTree;
				String treeKey = key;
				boolean clear = false;
			
				if(bTree == null){
					logger.error("抽卡异常=" + treeKey);
					
				}
				
				while(flag){
					if(roleChoiceCard.getType() == ChoiceFunType.GOLD.ordinal()){
						if(GameContext.getChoiceCardApp().getGameMoneyTree(treeKey) == null){
							flag = false;
							continue;
						}
					}else if(roleChoiceCard.getType() == ChoiceFunType.GEM.ordinal()){
						if(GameContext.getChoiceCardApp().getGemTree(treeKey) == null){
							flag = false;
							continue;
						}
					}else if(roleChoiceCard.getType() == ChoiceFunType.ACTIVITY.ordinal()){
						if(GameContext.getChoiceCardApp().getActivityTree(treeKey) == null){
							flag = false;
							continue;
						}
					}
					
					if(roleChoiceCard.getType() == ChoiceFunType.GOLD.ordinal()){
						bTree = GameContext.getChoiceCardApp().getGameMoneyTree(treeKey);
					}else if(roleChoiceCard.getType() == ChoiceFunType.GEM.ordinal()){
						bTree = GameContext.getChoiceCardApp().getGemTree(treeKey);
					}else if(roleChoiceCard.getType() == ChoiceFunType.ACTIVITY.ordinal()){
						bTree = GameContext.getChoiceCardApp().getActivityTree(treeKey);
					}
					
					leafId = Util.getWeightCalct(bTree.getWeightMap());
					treeKey = leafId + Cat.underline + roleLuck.getLuck();
					if(tree.getLuckType() == (byte)0){
						treeKey = leafId + Cat.underline + 0;
					}
					if(bTree.getFlagMap().get(leafId) == (byte)1){
						clear = true;
					}
				}
				
				if(tree.getLuckType() == (byte)1){
					if(clear){
						roleLuck.setLuck(0);
					}else{
						roleLuck.setLuck(roleLuck.getLuck() + 1);
					}
				}
				addGoods(roleChoiceCard.getType(),leafId,goodsList);
			}
		}
		saveOrUpdLuck(roleLuck);
		//发送物品
		sendGoods(role,goodsList,roleChoiceCard.getType());
	}
	
	private RoleChoiceCardLuck getRoleChoiceCardLuck(int roleId,byte type){
		RoleChoiceCardLuck roleLuck = null;
		Map<Byte,RoleChoiceCardLuck> luckMap = null;
		if(roleChoiceCardLuckMap.containsKey(roleId)){
			luckMap = roleChoiceCardLuckMap.get(roleId);
			if(luckMap.containsKey(type)){
				return luckMap.get(type);
			}
		}
		if(roleLuck == null){
			roleLuck = initRoleChoiceCardLuck(roleId,type);
			if(roleChoiceCardLuckMap.containsKey(roleLuck.getRoleId())){
				luckMap = roleChoiceCardLuckMap.get(roleLuck.getRoleId());
				luckMap.put(roleLuck.getType(),roleLuck);
			}else{
				luckMap = Maps.newConcurrentMap();
				luckMap.put(roleLuck.getType(),roleLuck);
				roleChoiceCardLuckMap.put(roleLuck.getRoleId(), luckMap);
			}
		}
		return roleLuck;
	}
	
	private RoleChoiceCardLuck initRoleChoiceCardLuck(int roleId,byte type){
		RoleChoiceCardLuck roleLuck = new RoleChoiceCardLuck();
		roleLuck.setRoleId(roleId);
		roleLuck.setType(type);
		return roleLuck;
	}
	
	private void addGoods(byte funType,int treeId,List<GoodsOperateBean> goodsList){
		BaseLeaf leaf = null;
		Map<Integer,Integer> leafMap = null;
		if(funType == ChoiceFunType.GOLD.ordinal()){
			leafMap = GameContext.getChoiceCardApp().getGameMoneyLeafWeight(treeId);
			if(Util.isEmpty(leafMap)){
				return;
			}
			int indexId = Util.getWeightCalct(leafMap);
			leaf = GameContext.getChoiceCardApp().getGameMoneyLeaf(treeId, indexId);
			
		}else if(funType == ChoiceFunType.GEM.ordinal()){
			leafMap = GameContext.getChoiceCardApp().getGemLeafWeight(treeId);
			if(Util.isEmpty(leafMap)){
				return;
			}
			int indexId = Util.getWeightCalct(leafMap);
			leaf = GameContext.getChoiceCardApp().getGemLeaf(treeId, indexId);
			
		}else if(funType == ChoiceFunType.ACTIVITY.ordinal()){
			leafMap = GameContext.getChoiceCardApp().getActivityLeafWeight(treeId);
			if(Util.isEmpty(leafMap)){
				return;
			}
			int indexId = Util.getWeightCalct(leafMap);
			leaf = GameContext.getChoiceCardApp().getActivityLeaf(treeId, indexId);
		}
		if(leaf != null){
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(leaf.getGoodsId());
			goodsList.add(new GoodsOperateBean(leaf.getGoodsId(), leaf.getGoodsNum(),goodsBase.getBindingType().getType()));
		}
	}
	
	private void sendGoods(RoleInstance role,List<GoodsOperateBean> goodsList,byte funType){
		OutputConsumeType outputConsumeType = null;
		if(funType == ChoiceFunType.GOLD.ordinal()){
			outputConsumeType = OutputConsumeType.card_gold_output;
		}else if(funType == ChoiceFunType.GEM.ordinal()){
			outputConsumeType = OutputConsumeType.card_gem_output;
		}else if(funType == ChoiceFunType.ACTIVITY.ordinal()){
			outputConsumeType = OutputConsumeType.card_activity_output;
		}
		
		C1348_CommonRewardRespMessage respMsg = new C1348_CommonRewardRespMessage();
		respMsg.setSuccess(Result.SUCCESS);
		List<GoodsLiteNamedExItem> awardGoodsList = new ArrayList<GoodsLiteNamedExItem>();
		// 记录抽卡奖励中的英雄
		List<Integer> heroList = Lists.newArrayList();
		for(GoodsOperateBean goods : goodsList){
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goods.getGoodsId());
			if(goodsBase == null){
				logger.error("RoleChoiceCardAppImpl error! Goods id is" + goods.getGoodsId());
				continue;
			}
			// 如果物品类型为英雄
			if (goodsBase.getGoodsType() == GoodsType.GoodsHero.getType()) {
				heroList.add(goods.getGoodsId());
			}
			goodsBase.getGoodsType();
			GoodsLiteNamedExItem goodsItem = goodsBase.getGoodsLiteNamedExItem();
			goodsItem.setBindType(goods.getBindType().getType());
			goodsItem.setNum((short) goods.getGoodsNum());
			awardGoodsList.add(goodsItem);
		}
		// 如果抽奖中有英雄，世界走马灯广播
		if (!Util.isEmpty(heroList)) {
			for (int heroId : heroList) {
				GoodsHero goodsHero = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class, heroId);
				if (null == goodsHero) {
					continue;
				}
				if (goodsHero.getBroadcast() == 1) {
					this.broadcast(role, goodsHero);
				}
			}
		}
		respMsg.setFunId(ClientLogicForwardType.HeroChoiceCardReward.getType());
		respMsg.setAwardGoodsList(awardGoodsList);
		role.getBehavior().sendMessage(respMsg);
		
		Iterator<GoodsOperateBean> iter = goodsList.iterator();
		while(iter.hasNext()){
			GoodsOperateBean goodsBean = (GoodsOperateBean)iter.next();
			Result result = new Result();
			for(int i=0;i<goodsBean.getGoodsNum();i++){
				result = GameContext.getHeroApp().useHeroGoods(role, goodsBean.getGoodsId());
			}
			if(result.isSuccess()){
				iter.remove();
			}
		}
		
		// 向背包中添加物品
		AddGoodsBeanResult goodsResult = GameContext.getUserGoodsApp()
				.addSomeGoodsBeanForBag(role, goodsList,outputConsumeType);
		
		// 背包满了则发邮件
		List<GoodsOperateBean> putFailureList = goodsResult.getPutFailureList();
		try {
			if(!Util.isEmpty(putFailureList)){
				String context = GameContext.getI18n().messageFormat(TextId.CARD_CHOICE_DES);
				GameContext.getMailApp().sendMail(role.getRoleId(),MailSendRoleType.CardChoice.getName(), 
							context,
							MailSendRoleType.CardChoice.getName(), 
							outputConsumeType.getType(),
							putFailureList);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}
	
	/**
	 * 世界广播
	 * @param role
	 */
	private void broadcast(RoleInstance role, GoodsHero goodsHero) {
		try {
			// 名称信息
			String roleName = Util.getColorRoleName(role, ChannelType.Publicize_Personal);
			// 英雄信息
			String heroName = Util.getColorString(goodsHero.getName(), QualityType.get(goodsHero.getQualityType()).getColor(), ChannelType.Publicize_Personal.getColor());
			// 广播信息
			String broadcastInfo = GameContext.getI18n().getText(TextId.BROAD_CAST_CARD_HERO).replace(Wildcard.Role_Name, roleName).replace(Wildcard.GoodsName, heroName);
			GameContext.getChatApp().sendSysMessage(ChatSysName.Goods_Strengthen, ChannelType.Publicize_Personal, broadcastInfo, null, null);
		} catch (Exception ex) {
			logger.error("strengthen broadcast error", ex);
		}
	}

	@Override
	public C2810_CardGoldRespMessage sendC2810_GoldCardRespMessage(
			RoleInstance role) {
		
		//检查时间是否过期
		checkRefTime(role,(byte)ChoiceFunType.GOLD.ordinal());
		
		C2810_CardGoldRespMessage respMsg = new C2810_CardGoldRespMessage();
		
		CommonCardItem item = new CommonCardItem();
		
		//单次抽卡
		RoleChoiceCard roleChoiceCard = getRoleChoiceCard(role, (byte)ChoiceFunType.GOLD.ordinal(), (byte)GoldSpecificType.SPEND.ordinal());
		byte specificType = roleChoiceCard.getSpecificType();
		//默认都是免费抽
		BaseMain goldMain = GameContext.getChoiceCardApp().getGameMoneyMain((byte)GoldSpecificType.FREE.ordinal());
		
		item.setFreeNum(goldMain.getFreeNum() - roleChoiceCard.getFreeNum());
		item.setMaxFreeNum(goldMain.getFreeNum());
		long overCd = roleChoiceCard.getCdTime();
		if(overCd != 0){
			if(System.currentTimeMillis() < overCd + goldMain.getCdTime() && roleChoiceCard.getFreeNum() < goldMain.getFreeNum()){
				overCd = (overCd + goldMain.getCdTime() - System.currentTimeMillis());
				item.setOverCd((int)overCd);
			}
		}
		
		BaseConsume goldConsume = null;
		
		//消费抽卡逻辑
		goldConsume = GameContext.getChoiceCardApp().getGameMoneyConsume((byte)GoldSpecificType.SPEND.ordinal(), role.getLevel());
		if(goldConsume == null){
			goldConsume = GameContext.getChoiceCardApp().getMaxGameMoneyConsume(specificType);
		}
		
		if(goldConsume != null){
			//获取消耗类型 0金币 1钻石
			item.setSpendPriceType(goldConsume.getConsumptionType());
			item.setSpendPrice(goldConsume.getConsumption());
		}
		
		if(item.getOverCd() <= 0 && item.getFreeNum() > 0 ){
			item.setSpendPrice(0);
		}
		
		specificType = (byte)GoldSpecificType.TEN.ordinal();
		//十连抽
//		RoleChoiceCard tenRoleChoiceCard = getRoleChoiceCard(role, (byte)ChoiceFunType.GOLD.ordinal(), specificType);
		goldMain = GameContext.getChoiceCardApp().getGameMoneyMain((byte)GoldSpecificType.TEN.ordinal());
		//消费抽卡逻辑
		goldConsume = GameContext.getChoiceCardApp().getGameMoneyConsume(specificType, role.getLevel());
		if(goldConsume == null){
			goldConsume = GameContext.getChoiceCardApp().getMaxGameMoneyConsume(specificType);
		}
		
		if(goldConsume != null){
			//获取消耗类型 0金币 1钻石
			item.setTenSpendPriceType(goldConsume.getConsumptionType());
			item.setTenSpendPrice(goldConsume.getConsumption());
		}
		item.setInfo(GameContext.getChoiceCardApp().getGameMoneyInfo());
		respMsg.setItem(item);
		return respMsg;
	}
	
	@Override
	public C2811_CardGemRespMessage sendC2811_GemCardRespMessage(
			RoleInstance role) {
		
		//检查时间是否过期
		checkRefTime(role,(byte)ChoiceFunType.GEM.ordinal());
		
		C2811_CardGemRespMessage respMsg = new C2811_CardGemRespMessage();
		
		CommonCardItem item = new CommonCardItem();
		
		//钻石抽卡
		item = new CommonCardItem();
		
		RoleChoiceCard roleChoiceCard = getRoleChoiceCard(role, (byte)ChoiceFunType.GEM.ordinal(), (byte)GemSpecificType.SPEND.ordinal());
		
		//默认都是免费抽
		BaseMain gemMain = GameContext.getChoiceCardApp().getGemMain((byte)GemSpecificType.FREE.ordinal());
		
		item.setFreeNum(gemMain.getFreeNum() - roleChoiceCard.getFreeNum());
		item.setMaxFreeNum(gemMain.getFreeNum());
		long overCd = roleChoiceCard.getCdTime();
		if(overCd != 0){
			if(System.currentTimeMillis() < overCd + gemMain.getCdTime()){
				overCd = (overCd + gemMain.getCdTime() - System.currentTimeMillis());
				item.setOverCd((int)overCd);
			}
		}
		
		BaseConsume gemConsume = null;
		
		//消费抽卡逻辑
		gemConsume = GameContext.getChoiceCardApp().getGemConsume((byte)GemSpecificType.SPEND.ordinal(), roleChoiceCard.getNum() + 1);
		if(gemConsume == null){
			gemConsume = GameContext.getChoiceCardApp().getMaxGemConsume((byte)GemSpecificType.SPEND.ordinal());
		}
		
		if(gemConsume != null){
			//获取消耗类型 0金币 1钻石
			item.setSpendPriceType(gemConsume.getConsumptionType());
			item.setSpendPrice(gemConsume.getConsumption());
		}
		
		if(item.getOverCd() <= 0 && item.getFreeNum() > 0){
			item.setSpendPrice(0);
		}
		
		byte specificType = (byte)GoldSpecificType.TEN.ordinal();
		//十连抽
		RoleChoiceCard tenRoleChoiceCard = getRoleChoiceCard(role, (byte)ChoiceFunType.GEM.ordinal(), specificType);
		gemMain = GameContext.getChoiceCardApp().getGemMain((byte)GemSpecificType.TEN.ordinal());
		//消费抽卡逻辑
		gemConsume = GameContext.getChoiceCardApp().getGemConsume(specificType, tenRoleChoiceCard.getNum() + 1);
		if(gemConsume == null){
			gemConsume = GameContext.getChoiceCardApp().getMaxGemConsume(specificType);
		}
		
		if(gemConsume != null){
			//获取消耗类型 0金币 1钻石
			item.setTenSpendPriceType(gemConsume.getConsumptionType());
			item.setTenSpendPrice(gemConsume.getConsumption());
		}
		item.setInfo(GameContext.getChoiceCardApp().getGemInfo());
		respMsg.setItem(item);
		return respMsg;
	}

	@Override
	public C2812_CardActivityRespMessage sendC2812_ActivityCardRespMessage(
			RoleInstance role) {
		
		C2812_CardActivityRespMessage respMsg = new C2812_CardActivityRespMessage();
		ActivityShow show = GameContext.getChoiceCardApp().getActivityShow();
		if(show != null){
			
			RoleChoiceCard roleChoiceCard = getRoleChoiceCard(role, (byte)ChoiceFunType.ACTIVITY.ordinal(), (byte)ActivitySpecificType.SPEND.ordinal());
			
			GoodsHero hero1 = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class,
					show.getHeroId1());
			GoodsHero hero2 = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class,
					show.getHeroId2());
			GoodsHero hero3 = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class,
					show.getHeroId3());
			GoodsHero hero4 = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class,
					show.getHeroId4());
			
			ActivityCardItem item = new ActivityCardItem();
			int [] resId = new int[4];
			resId[0] = hero1.getImageId();
			resId[1] = hero2.getImageId();
			resId[2] = hero3.getImageId();
			resId[3] = hero4.getImageId();
			item.setResId(resId);
			BaseConsume consume = GameContext.getChoiceCardApp().getActivityConsume(roleChoiceCard.getSpecificType(), roleChoiceCard.getNum());
			if(consume == null){
				consume = GameContext.getChoiceCardApp().getMaxActivityConsume(roleChoiceCard.getSpecificType());
			}
			item.setSpendPrice(consume.getConsumption());
			item.setSpendPriceType(consume.getConsumptionType());
			item.setInfo(GameContext.getChoiceCardApp().getActivityInfo());
			respMsg.setItem(item);
		}
		
		return respMsg;
	}
	
	/**
	 * 检测时间是否过期
	 * @param role
	 * @param roleChoiceCard
	 */
	private void checkRefTime(RoleInstance role,byte funType){
		Map<Byte,Map<Byte,RoleChoiceCard>> funMap = roleChoiceCardMap.get(role.getIntRoleId());
		if(Util.isEmpty(funMap)){
			return;
		}
		
		RoleChoiceCard roleChoiceCard = null;
		if(funType == ChoiceFunType.GOLD.ordinal()){
			//金币
			roleChoiceCard = getRoleChoiceCard(role, (byte)ChoiceFunType.GOLD.ordinal(), (byte)GoldSpecificType.SPEND.ordinal());
			if(!DateUtil.sameDay((int)(roleChoiceCard.getCdTime()/1000))){
				roleChoiceCard.setCdTime(0);
				roleChoiceCard.setFreeNum(0);
				saveOrUpd(roleChoiceCard);
			}
		}else if(funType == ChoiceFunType.GEM.ordinal()){
			//钻石
			roleChoiceCard = getRoleChoiceCard(role, (byte)ChoiceFunType.GEM.ordinal(), (byte)GemSpecificType.SPEND.ordinal());
			if(!DateUtil.sameDay((int)(roleChoiceCard.getCdTime()/1000))){
				roleChoiceCard.setCdTime(0);
				roleChoiceCard.setFreeNum(0);
				saveOrUpd(roleChoiceCard);
			}
		}
		
	}

	@Override
	public int onLogin(RoleInstance role, Object context) {
		try{
			
			// 初始化角色抽卡数据
			List<RoleChoiceCard> roleChoiceCardList = GameContext.getBaseDAO().selectList(RoleChoiceCard.class, RoleChoiceCard.ROLE_ID,role.getIntRoleId());
			if(Util.isEmpty(roleChoiceCardList)){
				return 0;
			}
			
			List<RoleChoiceCardLuck> roleChoiceCardLuckList = GameContext.getBaseDAO().selectList(RoleChoiceCardLuck.class, RoleChoiceCardLuck.ROLE_ID,role.getIntRoleId());
			
			Map<Byte,Map<Byte,RoleChoiceCard>> typeMap = null;
			Map<Byte,RoleChoiceCard> map = null;
			for(RoleChoiceCard choiceCard : roleChoiceCardList){
				if(roleChoiceCardMap.containsKey(choiceCard.getRoleId())){
					typeMap = roleChoiceCardMap.get(choiceCard.getRoleId());
					if(typeMap.containsKey(choiceCard.getType())){
						map = typeMap.get(choiceCard.getType());
						map.put(choiceCard.getSpecificType(), choiceCard);
					}else{
						map = Maps.newConcurrentMap();
						map.put(choiceCard.getSpecificType(), choiceCard);
						typeMap.put(choiceCard.getType(), map);
					}
					
				}else{
					typeMap = Maps.newConcurrentMap();
					map = Maps.newConcurrentMap();
					map.put(choiceCard.getSpecificType(), choiceCard);
					typeMap.put(choiceCard.getType(), map);
					roleChoiceCardMap.put(choiceCard.getRoleId(), typeMap);
				}
			}
			
			Map<Byte,RoleChoiceCardLuck> luckMap = null;
			for(RoleChoiceCardLuck roleLuck : roleChoiceCardLuckList){
				if(roleChoiceCardLuckMap.containsKey(roleLuck.getRoleId())){
					luckMap = roleChoiceCardLuckMap.get(roleLuck.getRoleId());
					luckMap.put(roleLuck.getType(),roleLuck);
				}else{
					luckMap = Maps.newConcurrentMap();
					luckMap.put(roleLuck.getType(),roleLuck);
					roleChoiceCardLuckMap.put(roleLuck.getRoleId(), luckMap);
				}
			}
			
		}catch(Exception ex){
			logger.error("onJoinGame",ex);
			return 0;
		}
		return 1;
	}

	@Override
	public int onLogout(RoleInstance role, Object context) {
		try{
			if(roleChoiceCardMap.containsKey(role.getIntRoleId())){
				roleChoiceCardMap.remove(role.getIntRoleId());
			}
			if(roleChoiceCardLuckMap.containsKey(role.getIntRoleId())){
				roleChoiceCardLuckMap.remove(role.getIntRoleId());
			}
			return 1;
		}catch(Exception ex){
			logger.error("onJoinGame",ex);
			return 0;
		}
		
	}

	@Override
	public int onCleanup(String roleId, Object context) {
		int rId = Integer.parseInt(roleId);
		if(roleChoiceCardMap.containsKey(rId)){
			roleChoiceCardMap.remove(rId);
		}
		if(roleChoiceCardLuckMap.containsKey(rId)){
			roleChoiceCardLuckMap.remove(rId);
		}
		return 1;
	}
	
	private HintTimeNotifyItem addItem(byte id,RoleChoiceCard roleChoiceCard,BaseMain main){
		HintTimeNotifyItem item = new HintTimeNotifyItem();
		item.setId(id);
		if(roleChoiceCard.getFreeNum() < main.getFreeNum()){
			if(System.currentTimeMillis() >= (roleChoiceCard.getCdTime() + main.getCdTime())){
				item.setTime(0);
			}else if(roleChoiceCard.getCdTime() + main.getCdTime() >= System.currentTimeMillis()){
				int time = (int)(roleChoiceCard.getCdTime() + main.getCdTime() - System.currentTimeMillis()) / 1000;
				item.setTime(time);
			}
		}else{
			item.setTime(-1);
		}
		return item;
	}

	@Override
	public List<HintTimeNotifyItem> getHintTimeNotifyList(RoleInstance role) {

		List<HintTimeNotifyItem> hintTimeNotifyList = Lists.newArrayList(); 
		HintTimeNotifyItem item = new HintTimeNotifyItem();
		RoleChoiceCard roleChoiceCard = null;
		BaseMain main = null;
		
		checkRefTime(role,(byte)ChoiceFunType.GOLD.ordinal());
		//金币
		roleChoiceCard = getRoleChoiceCard(role, (byte)ChoiceFunType.GOLD.ordinal(), (byte)GoldSpecificType.SPEND.ordinal());
		main = GameContext.getChoiceCardApp().getGameMoneyMain((byte)GoldSpecificType.FREE.ordinal());
		if(main.getFreeNum() != -1){
			item = addItem(HintType.choicegold.getId(),roleChoiceCard,main);
			hintTimeNotifyList.add(item);
		}
		
		checkRefTime(role,(byte)ChoiceFunType.GEM.ordinal());
		//钻石
		roleChoiceCard = getRoleChoiceCard(role, (byte)ChoiceFunType.GEM.ordinal(), (byte)GemSpecificType.SPEND.ordinal());
		main = GameContext.getChoiceCardApp().getGemMain((byte)GemSpecificType.FREE.ordinal());
		if(main.getFreeNum() != -1){
			item = addItem(HintType.choicegem.getId(),roleChoiceCard,main);
			hintTimeNotifyList.add(item);
		}
		return hintTimeNotifyList;
	}

	@Override
	public C2809_CardPreviewRespMessage sendC2820_CardPreviewRespMessage(byte type) {
		C2809_CardPreviewRespMessage respMsg = new C2809_CardPreviewRespMessage();
		
		List<BasePreview> previewlist = null;
		
		if(type == 0){
			previewlist = GameContext.getChoiceCardApp().getGameMoneyPreview();
		}else{
			previewlist = GameContext.getChoiceCardApp().getGemPreview();
		}
		
		if(Util.isEmpty(previewlist)){
			return respMsg;
		}
		
		List<GoodsLiteNamedItem> goodsHeroList = Lists.newArrayList(); 
		List<GoodsLiteNamedItem> goodsList = Lists.newArrayList(); 
		List<GoodsLiteNamedItem> goodsLiteItemList = new ArrayList<GoodsLiteNamedItem>();
		for(BasePreview preview : previewlist){
			if(!preview.isHero()){
				continue;
			}
			addPreview(goodsHeroList,preview.getGoodsId());
		}
		
		for(BasePreview preview : previewlist){
			if(preview.isHero()){
				continue;
			}
			addPreview(goodsList,preview.getGoodsId());
		}
		Collections.sort(goodsHeroList,this.goodsHeroItemComparator);
		Collections.sort(goodsList,this.goodsItemComparator);
		
		goodsLiteItemList.addAll(goodsHeroList);
		goodsLiteItemList.addAll(goodsList);
		
		respMsg.setGoodsList(goodsLiteItemList);
		return respMsg;
	}
	
	private void addPreview(List<GoodsLiteNamedItem> list,int goodsId){
		GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
		GoodsLiteNamedItem goodsLiteItem = goodsBase.getGoodsLiteNamedItem();
		list.add(goodsLiteItem);
	}
	
	Comparator<GoodsLiteNamedItem> goodsHeroItemComparator = new Comparator<GoodsLiteNamedItem>(){
		@Override
		public int compare(GoodsLiteNamedItem h1, GoodsLiteNamedItem h2) {
			if(h1.getQualityType() > h2.getQualityType()){
				return -1;
			}
			if(h1.getQualityType() < h2.getQualityType()){
				return 1;
			}
			
			return 0;
		}
	} ;
	
	Comparator<GoodsLiteNamedItem> goodsItemComparator = new Comparator<GoodsLiteNamedItem>(){
		@Override
		public int compare(GoodsLiteNamedItem h1, GoodsLiteNamedItem h2) {
			if(h1.getGoodsId() < h2.getGoodsId()){
				return -1;
			}
			if(h1.getGoodsId() > h2.getGoodsId()){
				return 1;
			}
			
			return 0;
		}
	} ;
}
