package com.game.draco.action.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;
import com.game.draco.message.internal.C0080_RecallSendAwardReqMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;

public class RecallSendAwardAction extends BaseAction<C0080_RecallSendAwardReqMessage> {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Override
	public Message execute(ActionContext context, C0080_RecallSendAwardReqMessage reqMsg) {
		try{
			GameContext.getRecallApp().sendRecallAward(reqMsg.getRoleId(), reqMsg.getLastLoginTime());
		}catch (Exception e){
			logger.error("", e);
		}
		return null;
	}

}
