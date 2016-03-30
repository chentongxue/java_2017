package com.game.draco.app.rune.action;

import java.util.Map;
import java.util.Set;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsRune;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.app.rune.config.RuneAttributeConfig;
import com.game.draco.app.rune.config.RuneCostConfig;
import com.game.draco.message.request.C0556_RuneSmeltReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C0556_RuneSmeltRespMessage;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class RuneSmeltAction extends BaseAction<C0556_RuneSmeltReqMessage> {

	private static final int ONE = 1;
	private static final int TWO = 2;
	private static final int THREE = 3;
	private static Map<String, Integer> attriNumRuleMap = Maps.newHashMap();

	static {
		attriNumRuleMap.put("1_1", TWO);
		attriNumRuleMap.put("1_2", TWO);
		attriNumRuleMap.put("2_2", THREE);
		attriNumRuleMap.put("2_3", THREE);
		attriNumRuleMap.put("3_3", THREE);
	}

	@Override
	public Message execute(ActionContext context, C0556_RuneSmeltReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		// 判断数据是否合法
		String[] runesIds = reqMsg.getRuneforSmelt();
		if (runesIds.length != TWO) {
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(), this.getText(TextId.ERROR_INPUT));
		}
		if (this.errorInput(runesIds)) {
			C0002_ErrorRespMessage message = new C0002_ErrorRespMessage();
			message.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return message;
		}
		int index = -1;
		int[] runeLevelArr = new int[runesIds.length];
		int[] attrNumArr = new int[runesIds.length];
		BindingType bindType = null;
		for (String goodsId : runesIds) {
			index++;
			RoleGoods roleGoods = GameContext.getUserGoodsApp().getRoleGoods(role, StorageType.bag, goodsId, 0);
			if (null == roleGoods) {
				return new C0002_ErrorRespMessage(reqMsg.getCommandId(), this.getText(TextId.GOODS_NO_EXISTS));
			}
			if (BindingType.already_binding.getType() == roleGoods.getBind()) {
				bindType = BindingType.already_binding;
			}
			GoodsRune rune = GameContext.getGoodsApp().getGoodsTemplate(GoodsRune.class, roleGoods.getGoodsId());
			if (null == rune) {
				return new C0002_ErrorRespMessage(reqMsg.getCommandId(), this.getText(TextId.ERROR_INPUT));
			}
			if (null == bindType) {
				bindType = rune.getBindingType();
			}
			runeLevelArr[index] = rune.getLevel();
			attrNumArr[index] = rune.getSecondType();
			if (index != 0 && runeLevelArr[index] != runeLevelArr[index - 1] || index != 0 && this.smeltNotOnRules(attrNumArr))
				return new C0002_ErrorRespMessage(reqMsg.getCommandId(), this.getText(TextId.Rune_Smelt_Not_Rules));
		}

		int runeLevel = runeLevelArr[0];//
		String attriKey = Math.min(attrNumArr[0], attrNumArr[1]) + "_" + Math.max(attrNumArr[0], attrNumArr[1]);
		Integer attriNum = attriNumRuleMap.get(attriKey);
		if (null == attriNum) {
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(), this.getText(TextId.ERROR_INPUT));
		}
		RuneCostConfig costConfig = GameContext.getRuneApp().getRuneCostConfig(runeLevel) ;
		if(null == costConfig){
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(), this.getText(TextId.ERROR_DATA));
		}
		// 金币是否足够
		//【游戏币/潜能/钻石不足弹板】 判断
		Result ar = GameContext.getUserAttributeApp().getEnoughResult(role, AttributeType.gameMoney, costConfig.getSmeltMoney());
		if(ar.isIgnore()){
			return null;
		}
		if(!ar.isSuccess()){
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(), this.getText(TextId.NOT_ENOUGH_GAME_MOENY));
		}
		// 得到产物的模版ID
		int targetTemplateId = GameContext.getRuneApp().getRuleTemplateId(runeLevelArr[0], attriNum);
		if (targetTemplateId <= 0) {
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(), this.getText(TextId.ERROR_INPUT));
		}
		if (costConfig.getSmeltMoney() > 0) {
			GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.gameMoney, 
					OperatorType.Decrease, costConfig.getSmeltMoney(), OutputConsumeType.rune_consume_smelt);
			role.getBehavior().notifyAttribute();
		}
		for (String runesId : runesIds) {
			GameContext.getUserGoodsApp().deleteForBagByInstanceId(role, runesId, 1, OutputConsumeType.rune_consume_smelt);
		}
		// 添加符文
		GoodsResult result = GameContext.getUserGoodsApp().addGoodsForBag(role, targetTemplateId, ONE, bindType, OutputConsumeType.rune_reward_smelt);
		if (!result.isSuccess()) {
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(), result.getInfo());
		}
		// 世界广播
		this.broadcast(role, runeLevel, attriNum);
		// 返回成功消息
		C0556_RuneSmeltRespMessage respMsg = new C0556_RuneSmeltRespMessage();
		respMsg.setType(Result.SUCCESS);
		return respMsg;
	}
	
	/**
	 * 世界广播
	 * @param role
	 * @param level
	 * @param attriNum
	 */
	private void broadcast(RoleInstance role, int level, int attriNum) {
		try {
			RuneAttributeConfig runeConfig = GameContext.getRuneApp().getRuneAttriButeConfig(level, attriNum);
			if (null == runeConfig) {
				return ;
			}
			String info = runeConfig.getBroadCastTips(role);
			if (Util.isEmpty(info)) {
				return ;
			}
			GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Publicize_Personal, info, null, null);
		} catch (Exception e) {
			logger.error("RuneSmeltAction.broadcast error!", e);
		}
	}
	
	/**
	 * 不符合熔炼规则返回true
	 * @param attrNumArr
	 * @return
	 */
	private boolean smeltNotOnRules(int[] attrNumArr) {
		if (attrNumArr.length < 2) {
			return true;
		}
		if (attrNumArr[0] > attrNumArr[1]) {
			return attrNumArr[0] - attrNumArr[1] > 1;
		}
		return attrNumArr[1] - attrNumArr[0] > 1;
	}
	
	/**
	 * 是否有重复的实例ID
	 * @param ids
	 * @return
	 */
	private boolean errorInput(String[] ids) {
		Set<String> idSet = Sets.newHashSet();
		for (String id : ids) {
			if (idSet.contains(id)) {
				return true;
			}
		}
		return false;
	}

}
