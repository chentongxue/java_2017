package com.game.draco.action.internal;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.internal.C0085_CampWarAutoApplyInternalMessage;

public class CampWarAutoApplyInternalAction extends BaseAction<C0085_CampWarAutoApplyInternalMessage> {

	@Override
	public Message execute(ActionContext context,
			C0085_CampWarAutoApplyInternalMessage reqMsg) {
		RoleInstance role = reqMsg.getRole() ;
		if(null == role){
			return null ;
		}
		if(reqMsg.isApply()){
			//报名
			if(!role.isOfflined()){
				return GameContext.getCampWarApp().getCampWarPanelMessage(role, true);
			}
			return null;
		}
		//取消
		GameContext.getCampWarApp().cancel(role);
		return null ;
	}

}
