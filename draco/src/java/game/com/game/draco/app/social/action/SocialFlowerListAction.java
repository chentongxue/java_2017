package com.game.draco.app.social.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C1210_SocialFlowerListReqMessage;

public class SocialFlowerListAction extends BaseAction<C1210_SocialFlowerListReqMessage> {

	@Override
	public Message execute(ActionContext context, C1210_SocialFlowerListReqMessage reqMsg) {
		RoleInstance targRole = GameContext.getOnlineCenter().getRoleInstanceByRoleId(String.valueOf(reqMsg.getRoleId()));
		if(null == targRole){
			return new C0003_TipNotifyMessage(Status.Social_TargRole_Offline.getTips());
		}
		return GameContext.getSocialApp().getFlowerListMessage(reqMsg.getRoleId());
	}
	
}
