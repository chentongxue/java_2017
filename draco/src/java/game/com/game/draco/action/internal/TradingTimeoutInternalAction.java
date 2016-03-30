package com.game.draco.action.internal;

import com.game.draco.message.internal.C0063_TradingTimeoutInternalMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.trading.CancelReason;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;

public class TradingTimeoutInternalAction extends BaseAction<C0063_TradingTimeoutInternalMessage> {

	//蝠眢等盄最硒俴
	@Override
	public Message execute(ActionContext context,
			C0063_TradingTimeoutInternalMessage reqMsg) {
		reqMsg.getMatch().cancel(CancelReason.timeout, null);
		return null;
	}

}
