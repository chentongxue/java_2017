package com.game.draco.app.talent.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2818_RoleTalentListReqMessage;
import com.game.draco.message.response.C2818_RoleTalentSaveRespMessage;

/**
 * 保存天赋
 * @author mofun030602
 *
 */
public class RoleTalentSaveAction extends BaseAction<C2818_RoleTalentListReqMessage> {

	@Override
	public Message execute(ActionContext context, C2818_RoleTalentListReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		
		C2818_RoleTalentSaveRespMessage respMsg = new C2818_RoleTalentSaveRespMessage();
		
		Result result = GameContext.getRoleTalentApp().saveTempTalent(role);
		
		respMsg.setSuccess(result.getResult());
		respMsg.setInfo(result.getInfo());
		
		if(result.isSuccess()){
			role.getBehavior().sendMessage(GameContext.getRoleTalentApp().sendC2816_RoleTalentListRespMessage(role));
		}
		
		return respMsg;
	}
	
}
