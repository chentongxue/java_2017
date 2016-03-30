package com.game.draco.app.talent.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2816_RoleTalentListReqMessage;
import com.game.draco.message.response.C2816_RoleTalentListRespMessage;

/**
 * 天赋列表
 * @author mofun030602
 *
 */
public class RoleTalentListAction extends BaseAction<C2816_RoleTalentListReqMessage> {

	@Override
	public Message execute(ActionContext context, C2816_RoleTalentListReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		
		C2816_RoleTalentListRespMessage respMsg = GameContext.getRoleTalentApp().sendC2816_RoleTalentListRespMessage(role);
		
		return respMsg;
	}
	
}
