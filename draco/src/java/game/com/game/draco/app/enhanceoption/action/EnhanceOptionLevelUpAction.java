package com.game.draco.app.enhanceoption.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0620_LevelUpEnhanceOptionReqMessage;
import com.game.draco.message.request.C2510_VipDisplayReqMessage;
/**
 * 
 */
public class EnhanceOptionLevelUpAction  extends BaseAction<C0620_LevelUpEnhanceOptionReqMessage>{
	@Override
	public Message execute(ActionContext ct, C0620_LevelUpEnhanceOptionReqMessage req) {
		RoleInstance role = this.getCurrentRole(ct);
		if(role==null){
			return null;
		}
		return GameContext.getEnhanceOptionApp().getEnhanceOptionLevelUpMessage(role);
	}
}
