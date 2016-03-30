package com.game.draco.action.internal;

import com.game.draco.message.internal.C0061_TradingCancelUserExecInternalMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.trading.TradingMatch;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;

public class TradingCancelUserExecInternalAction extends BaseAction<C0061_TradingCancelUserExecInternalMessage> {

	//等蚚誧等盄最硒俴
	@Override
	public Message execute(ActionContext context,
			C0061_TradingCancelUserExecInternalMessage reqMsg) {
		TradingMatch match = reqMsg.getMatch();
		match.cancelExec(reqMsg.getRollbackRoleId(), reqMsg.getReason(), reqMsg.getCanceler());
		return null;
	}

}
