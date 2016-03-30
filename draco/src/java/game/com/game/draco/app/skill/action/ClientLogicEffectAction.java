package com.game.draco.app.skill.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.speciallogic.SpecialLogicType;
import com.game.draco.message.request.C0309_ClientLogicEffectReqMessage;

public class ClientLogicEffectAction extends BaseAction<C0309_ClientLogicEffectReqMessage> {
	
	@Override
	public Message execute(ActionContext context, C0309_ClientLogicEffectReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		
		byte t = req.getType();
		
		Point point = new Point(role.getMapId(), req.getMapX(), req.getMapY());
		
		SpecialLogicType type = SpecialLogicType.getType(t);
		
		GameContext.getSpecialLogicApp().logic(role, type, point);
		
		return null;
	}

	
}
