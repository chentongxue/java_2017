package com.game.draco.app.camp.war.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.camp.war.config.RoleBattleConfig;
import com.game.draco.message.request.C0355_CampWarDescReqMessage;
import com.game.draco.message.response.C0355_CampWarDescRespMessage;

public class CampWarDescAction extends BaseAction<C0355_CampWarDescReqMessage>{

	@Override
	public Message execute(ActionContext context, C0355_CampWarDescReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		RoleBattleConfig config = GameContext.getCampWarApp().getRoleBattleConfig() ;
		C0355_CampWarDescRespMessage respMsg = new C0355_CampWarDescRespMessage();
		if(null != config){
			respMsg.setDesc(config.getDesc());
		}
		return respMsg;
	}

}
