package com.game.draco.app.talent.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.talent.domain.RoleTalent;
import com.game.draco.app.talent.vo.RoleTrainTalentResult;
import com.game.draco.message.request.C2817_RoleTrainTalentReqMessage;
import com.game.draco.message.response.C2817_RoleTalentRefRespMessage;

/**
 * 培养天赋
 * @author mofun030602
 *
 */
public class RoleTrainTalentAction extends BaseAction<C2817_RoleTrainTalentReqMessage> {

	@Override
	public Message execute(ActionContext context, C2817_RoleTrainTalentReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		
		C2817_RoleTalentRefRespMessage respMsg = new C2817_RoleTalentRefRespMessage();
		RoleTrainTalentResult result = GameContext.getRoleTalentApp().trainTalent(role, reqMsg.getType());
		if(result.isSuccess()){
			RoleTalent temp = result.getTemp();
			respMsg = GameContext.getRoleTalentApp().sendC2817_RoleTalentRefRespMessage(role,temp);
			respMsg.setSuccess(result.getResult());
		}else{
			respMsg.setSuccess(result.getResult());
			respMsg.setInfo(result.getInfo());
		}
		return respMsg;
	}
	
}
