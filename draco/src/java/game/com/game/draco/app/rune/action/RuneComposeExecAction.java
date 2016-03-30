package com.game.draco.app.rune.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.app.rune.config.RuneComposeRuleConfig;
import com.game.draco.message.request.C0547_RuneComposeExecReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C0547_RuneComposeExecRespMessage;
import com.google.common.collect.Sets;

public class RuneComposeExecAction extends BaseAction<C0547_RuneComposeExecReqMessage> {
	
	private static final int ONE = 1;

	@Override
	public Message execute(ActionContext context, C0547_RuneComposeExecReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		int count = reqMsg.getCount();// 合成次数
		String[] runesIds = reqMsg.getGoodsIds();// 实例Id数组
		// 判断接受数据的有效性
		if (null == runesIds || 0 == runesIds.length || count <= 0) {
			return this.buildErrorMessage(reqMsg.getCommandId(), GameContext.getI18n().getText(TextId.ERROR_INPUT));
		}
		if (this.errorInput(runesIds)) {
			C0002_ErrorRespMessage message = new C0002_ErrorRespMessage();
			message.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return message;
		}
		// 参数无误，合成逻辑
		BindingType bindType = null;
		RoleGoods preRoleGoods = null;
		int roleGoodsCount = 0;
		for (int i = 0; i < runesIds.length; i++) {
			RoleGoods goods = GameContext.getUserGoodsApp().getRoleGoods(role, StorageType.bag, runesIds[i], 0);
			// 判断物品是否存在
			if (null == goods) {
				return this.buildErrorMessage(reqMsg.getCommandId(), GameContext.getI18n().getText(TextId.GOODS_NO_EXISTS));
			}
			// 判断四个符文是否是同一模版
			if (null != preRoleGoods && preRoleGoods.getGoodsId() != goods.getGoodsId()) {
				return this.buildErrorMessage(reqMsg.getCommandId(), GameContext.getI18n().getText(TextId.ERROR_DATA));
			}
			// 判断材料中是否有绑定符文
			if (BindingType.already_binding.getType() == goods.getBind()) {
				bindType = BindingType.already_binding;
			}
			preRoleGoods = goods;
			roleGoodsCount += goods.getCurrOverlapCount();
		}

		// 取出材料模版
		int goodsId = preRoleGoods.getGoodsId();
		GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
		if (null == goodsBase) {
			return this.buildErrorMessage(reqMsg.getCommandId(), GameContext.getI18n().getText(TextId.ERROR_DATA));
		}
		// 取出合成规则
		RuneComposeRuleConfig runeComposeRuleConfig = GameContext.getRuneApp().getTargetComposeGoods(goodsId);
		if (null == runeComposeRuleConfig) {
			return this.buildErrorMessage(reqMsg.getCommandId(), GameContext.getI18n().getText(TextId.ERROR_DATA));
		}
		// 取出合成的符文的模版ID
		int targetTemplateId = runeComposeRuleConfig.getTargetId();
		GoodsBase targetGoodsBase = GameContext.getGoodsApp().getGoodsBase(targetTemplateId);
		if (null == targetGoodsBase) {
			return this.buildErrorMessage(reqMsg.getCommandId(), GameContext.getI18n().getText(TextId.ERROR_DATA));
		}
		// 如果材料中没有绑定的，默认使用模版的绑定类型
		if (null == bindType) {
			bindType = targetGoodsBase.getBindingType();
		}
		// 得到合成费用
		int composeMoney = runeComposeRuleConfig.getFee();
		int composeNum = runeComposeRuleConfig.getSrcNum();
		if (composeNum <= 0 || composeMoney < 0) {
			return this.buildErrorMessage(reqMsg.getCommandId(), GameContext.getI18n().getText(TextId.ERROR_DATA));
		}
		// 判断是否是多属性符文
		boolean isMultAttriRune = this.isMultAttriRune(goodsBase);
		// 多属性符文,只允许合成1次
		if (isMultAttriRune) {
			count = 1;
		}
		// 提示物品不足
		if (roleGoodsCount < composeNum * count) {
			return this.buildErrorMessage(reqMsg.getCommandId(), GameContext.getI18n().getText(TextId.NOT_ENOUGH_GOODS));
		}
		// 金币不够
		//【游戏币/潜能/钻石不足弹板】 判断
		Result ar = GameContext.getUserAttributeApp().getEnoughResult(role, AttributeType.gameMoney, composeMoney * count);
		if(ar.isIgnore()){
			return null;
		}
		if(!ar.isSuccess()){
			this.buildErrorMessage(reqMsg.getCommandId(), GameContext.getI18n().getText(TextId.NOT_ENOUGH_GAME_MOENY));
		}

//		if (role.getSilverMoney() < composeMoney * count) {
//			return this.buildErrorMessage(reqMsg.getCommandId(), TextId.NOT_ENOUGH_GAME_MOENY);
//		}
		
		// 单属性符文合成
		if (!isMultAttriRune) {
			// 要添加的物品
			GoodsOperateBean addGob = new GoodsOperateBean();
			addGob.setBindType(bindType);
			addGob.setGoodsId(targetTemplateId);
			addGob.setGoodsNum(count);
			List<GoodsOperateBean> addList = new ArrayList<GoodsOperateBean>();
			addList.add(addGob);
			// 要删除的物品
			GoodsOperateBean delGob = new GoodsOperateBean();
			delGob.setGoodsId(goodsId);
			delGob.setGoodsNum(count * composeNum);
			List<GoodsOperateBean> delList = new ArrayList<GoodsOperateBean>();
			delList.add(delGob);
			// 处理添加删除
			GoodsResult result = GameContext.getUserGoodsApp().addDelGoodsForBag(role, addList, OutputConsumeType.rune_reward_compose, delList, OutputConsumeType.rune_consume_compose);
			if (!result.isSuccess()) {
				return this.buildErrorMessage(reqMsg.getCommandId(), result.getInfo());
			}
			GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.gameMoney, OperatorType.Decrease, composeMoney * count, OutputConsumeType.rune_consume_compose);
			role.getBehavior().notifyAttribute();
			this.broadcast(role, runeComposeRuleConfig);
			return this.buildSuccessMessage();
		}
		// 删除材料
		this.delGoods(role, runesIds, composeNum * count);
		if (composeMoney > 0) {
			GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.gameMoney, OperatorType.Decrease, composeMoney, OutputConsumeType.rune_consume_compose);
			role.getBehavior().notifyAttribute();
		}
		// 多属性符文
		GoodsResult result = GameContext.getUserGoodsApp().addGoodsForBag(role, targetTemplateId, ONE, bindType, OutputConsumeType.rune_reward_compose);
		if (!result.isSuccess()) {
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(), result.getInfo());
		}
		// 世界广播
		this.broadcast(role, runeComposeRuleConfig);
		return this.buildSuccessMessage();
	}
	
	/**
	 * 世界广播
	 * @param role
	 * @param runeComposeRuleConfig
	 */
	private void broadcast(RoleInstance role, RuneComposeRuleConfig runeComposeRuleConfig) {
		try {
			String message = runeComposeRuleConfig.getBroadCastTips(role);
			if (Util.isEmpty(message)) {
				return ;
			}
			GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Publicize_Personal, message, null, null);
		} catch	(Exception e) {
			logger.error("RuneComposeExecAction.broadcast error!", e);
		}
	}

	private Message buildErrorMessage(short cmdId, String message) {
		return new C0002_ErrorRespMessage(cmdId, message);
	}

	private Message buildSuccessMessage() {
		C0547_RuneComposeExecRespMessage respMsg = new C0547_RuneComposeExecRespMessage();
		respMsg.setType(Result.SUCCESS);
		return respMsg;
	}

	// 是否是多属性符文
	private boolean isMultAttriRune(GoodsBase goodsBase) {
		if (GoodsType.GoodsRune.getType() != goodsBase.getGoodsType()) {
			return false;
		}
		return goodsBase.getSecondType() > 1;
	}

	// 删除材料
	private void delGoods(RoleInstance role, String[] runesIds, int delTotalCount) {
		// 避免死循环
		int delNum = 0;
		for (int cur = 0; cur < delTotalCount; cur++) {
			for (String id : runesIds) {
				GoodsResult delResult = GameContext.getUserGoodsApp().deleteForBagByInstanceId(role, id, OutputConsumeType.rune_consume_compose);
				if (delResult.isSuccess()) {
					delNum++;
				}
				if (delNum >= delTotalCount) {
					return;
				}
			}
		}
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
			idSet.add(id);
		}
		return false;
	}
	
}
