package com.game.draco.app.rune.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.Util;

import com.game.draco.GameContext;
import com.game.draco.app.rune.config.RuneComposeRuleConfig;
import com.game.draco.message.item.GoodsRuneComposeInfoItem;
import com.game.draco.message.request.C0548_RuneComposeRulesReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C0548_RuneComposeRulesRespMessage;

public class RuneComposeRulesAction extends BaseAction<C0548_RuneComposeRulesReqMessage> {

	@Override
	public Message execute(ActionContext context, C0548_RuneComposeRulesReqMessage reqMsg) {
		try {
			List<GoodsRuneComposeInfoItem> runeComposeInfoList = new ArrayList<GoodsRuneComposeInfoItem>();
			Map<Integer, RuneComposeRuleConfig> runeComposeRuleConfigMap = GameContext.getRuneApp().getRuneComposeRuleConfigMap();
			if (Util.isEmpty(runeComposeRuleConfigMap)) {
				return new C0002_ErrorRespMessage(reqMsg.getCommandId(), this.getText(TextId.GOODS_NO_EXISTS));
			}
			// 封装规则
			for (RuneComposeRuleConfig runeComposeRuleConfig : runeComposeRuleConfigMap.values()) {
				GoodsRuneComposeInfoItem goodsRuneComposeInfoItem = new GoodsRuneComposeInfoItem();
				goodsRuneComposeInfoItem.setFee(runeComposeRuleConfig.getFee());
				goodsRuneComposeInfoItem.setSrcId(runeComposeRuleConfig.getSrcId());
				goodsRuneComposeInfoItem.setSrcNum(runeComposeRuleConfig.getSrcNum());
				goodsRuneComposeInfoItem.setTargetId(runeComposeRuleConfig.getTargetId());
				goodsRuneComposeInfoItem.setTargetItem(runeComposeRuleConfig.getTargetGoods().getGoodsLiteNamedItem());
				runeComposeInfoList.add(goodsRuneComposeInfoItem);
			}
			C0548_RuneComposeRulesRespMessage respMsg = new C0548_RuneComposeRulesRespMessage();
			respMsg.setRunesOfCompose(runeComposeInfoList);
			return respMsg;
		} catch (Exception e) {
			logger.error("", e);
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(), Status.Goods_System_Busy.getTips());
		}
	}

}
