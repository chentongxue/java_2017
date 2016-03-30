package com.game.draco.action.internal;

import com.game.draco.GameContext;
import com.game.draco.message.internal.C0060_TradingLockUserExecInternalMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.trading.TradingMatch;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;

public class TradingLockUserExecInternalAction extends BaseAction<C0060_TradingLockUserExecInternalMessage> {

	//等蚚誧等盄最硒俴
	@Override
	public Message execute(ActionContext context,
			C0060_TradingLockUserExecInternalMessage reqMsg) {
		TradingMatch match = reqMsg.getMatch();
		Result result = match.lockExec(reqMsg.getRole(), reqMsg.getMoney(), reqMsg.getGoods());
		if(result.isIgnore()){
			return null;
		}
		if(result.isSuccess()){
			return null ;
		}
		//祥夔眻諉return
		Message message = new C0002_ErrorRespMessage(reqMsg.getCommandId(),result.getInfo());
		//return message ;
		GameContext.getMessageCenter().send(null, reqMsg.getRole().getUserId(), message);
		return null ;
	}

}
