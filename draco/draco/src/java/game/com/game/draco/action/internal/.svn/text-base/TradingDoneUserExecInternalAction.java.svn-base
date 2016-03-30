package com.game.draco.action.internal;

import com.game.draco.message.internal.C0062_TradingDoneUserExecInternalMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.trading.TradingMatch;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;

public class TradingDoneUserExecInternalAction extends BaseAction<C0062_TradingDoneUserExecInternalMessage>{
	@Override
	public Message execute(ActionContext context,
			C0062_TradingDoneUserExecInternalMessage reqMsg) {
		TradingMatch match = reqMsg.getMatch();
		match.tradingExec(reqMsg.getRoleId());
		return null;
	}

}
