package com.game.draco.app.camp.war.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0352_CampWarPanelReqMessage;

public class CampWarPanelAction extends BaseAction<C0352_CampWarPanelReqMessage>{

	@Override
	public Message execute(ActionContext context, C0352_CampWarPanelReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		if( 1 == reqMsg.getOpen()){
			return GameContext.getCampWarApp().getCampWarPanelMessage(role,false);
		}
		GameContext.getCampWarApp().removePanelRole(role);
		return null ;
	}

}
