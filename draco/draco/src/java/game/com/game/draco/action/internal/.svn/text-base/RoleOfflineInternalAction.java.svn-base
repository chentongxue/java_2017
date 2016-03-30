package com.game.draco.action.internal;

import com.game.draco.GameContext;
import com.game.draco.message.internal.C0050_RoleOfflineInternalMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.channel.ChannelSession;
import sacred.alliance.magic.vo.RoleInstance;

public class RoleOfflineInternalAction extends BaseAction<C0050_RoleOfflineInternalMessage>{

	@Override
	public Message execute(ActionContext context,
			C0050_RoleOfflineInternalMessage reqMsg) {
		//String userId = reqMsg.getUserId();
		ChannelSession session = reqMsg.getSession();
		RoleInstance role = reqMsg.getRole();
		if(null == role){
			GameContext.getOnlineCenter().offline(session);
			return null ;
		}
		//判断在线中心是否还存在
		role = GameContext.getOnlineCenter().getRoleInstanceByUserId(reqMsg.getUserId());
		GameContext.getOnlineCenter().offline(role,false);
		return null;
	}

}
