package com.game.draco.action.internal;

import com.game.draco.GameContext;
import com.game.draco.message.internal.C0067_TimingWriteDBReqMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class TimingWriteDBAction extends BaseAction<C0067_TimingWriteDBReqMessage> {

	@Override
	public Message execute(ActionContext context, C0067_TimingWriteDBReqMessage reqMsg) {
		RoleInstance role = reqMsg.getRole();
		if(null == role){
			return null;
		}
		GameContext.getOnlineCenter().timingWriteDB(role);
		return null;
	}

}
