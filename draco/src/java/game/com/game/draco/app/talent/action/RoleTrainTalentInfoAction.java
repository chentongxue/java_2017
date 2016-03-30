package com.game.draco.app.talent.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2819_RoleTrainTalentInfoReqMessage;
import com.game.draco.message.response.C2819_RoleTrainTalentInfoRespMessage;

/**
 * 天赋详情
 * @author mofun030602
 *
 */
public class RoleTrainTalentInfoAction extends BaseAction<C2819_RoleTrainTalentInfoReqMessage> {

	@Override
	public Message execute(ActionContext context, C2819_RoleTrainTalentInfoReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		
		C2819_RoleTrainTalentInfoRespMessage respMsg = GameContext.getRoleTalentApp().sendC2819_RoleTrainTalentInfoRespMessage(role);
		
		return respMsg;
	}
	
}
