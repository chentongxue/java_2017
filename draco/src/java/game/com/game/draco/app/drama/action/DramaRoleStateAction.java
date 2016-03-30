package com.game.draco.app.drama.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.message.request.C3277_DramaRoleStateReqMessage;

public class DramaRoleStateAction extends BaseAction<C3277_DramaRoleStateReqMessage>{

	@Override
	public Message execute(ActionContext context,
			C3277_DramaRoleStateReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		if(1 == reqMsg.getState()){
			//剧情模式
			role.setDramaState(true);
			return null ;
		}
		role.setDramaState(false);
		return null ;
	}

}
