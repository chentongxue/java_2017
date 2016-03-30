package com.game.draco.app.rune.action;

import java.util.Collection;
import java.util.List;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;

import com.game.draco.GameContext;
import com.game.draco.app.rune.config.RuneCostConfig;
import com.game.draco.message.item.RuneSmeltRuleItem;
import com.game.draco.message.request.C0545_RuneSmeltRulesReqMessage;
import com.game.draco.message.response.C0545_RuneSmeltRulesRespMessage;
import com.google.common.collect.Lists;

public class RuneSmeltRulesAction extends BaseAction<C0545_RuneSmeltRulesReqMessage>{

	@Override
	public Message execute(ActionContext context,
			C0545_RuneSmeltRulesReqMessage reqMsg) {
		
		C0545_RuneSmeltRulesRespMessage respMsg = new C0545_RuneSmeltRulesRespMessage();
		Collection<RuneCostConfig> configList = GameContext.getRuneApp().getAllRuneCostConfig() ;
		if(null == configList){
			return respMsg ;
		}
		List<RuneSmeltRuleItem> ruleList = Lists.newArrayList() ;
		for(RuneCostConfig config : configList){
			RuneSmeltRuleItem item = new RuneSmeltRuleItem();
			item.setRuneLevel((byte)config.getLevel());
			item.setGameMoney(config.getSmeltMoney());
			ruleList.add(item);
		}
		respMsg.setRuleList(ruleList);
		return respMsg ;
	}

}
