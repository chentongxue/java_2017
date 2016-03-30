package com.game.draco.app.chat.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleEntity;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.message.request.C1801_ChatSendReqMessage;
import com.game.draco.message.response.C1801_ChatSendRespMessage;

public class ChatSendAction extends BaseAction<C1801_ChatSendReqMessage>{
	
	@Override
	public Message execute(ActionContext context, C1801_ChatSendReqMessage reqMsg) {
		C1801_ChatSendRespMessage resp = new C1801_ChatSendRespMessage();
		RoleEntity speaker = this.getCurrentRole(context);
		RoleInstance targRole = null;
		String targetId = reqMsg.getTargetRoleId();
		if(1 == reqMsg.getTargetIdFlag()){
			targRole = GameContext.getOnlineCenter().getRoleInstanceByRoleName(targetId);
		} else {
			targRole = GameContext.getOnlineCenter().getRoleInstanceByRoleId(targetId);
		}
		Result result = GameContext.getChatApp().sendMessage(
							speaker, 
							ChannelType.getChannelType(reqMsg.getChannelType()), 
							reqMsg.getMessage(), 
							reqMsg.getContextList(), 
							targRole);
		if(result.isIgnore()){//impt
			return null;
		}
		resp.setType(result.getResult());
		resp.setInfo(result.getInfo());
		return resp;
	}
	
}
