package com.game.draco.app.pet.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.pet.domain.RolePet;
import com.game.draco.message.request.C1655_PetStarUpReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C1655_PetStarUpRespMessage;

public class PetStarUpAction extends BaseAction<C1655_PetStarUpReqMessage> {

	@Override
	public Message execute(ActionContext context, C1655_PetStarUpReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		String roleId = role.getRoleId();
		RolePet rolePet = GameContext.getUserPetApp().getRolePet(roleId, reqMsg.getPetId());
		if (null == rolePet) {
			C0002_ErrorRespMessage message = new C0002_ErrorRespMessage();
			message.setInfo(GameContext.getI18n().getText(TextId.Pet_Not_Owned));
			return message;
		}
		Result result = GameContext.getPetApp().petStarUp(role, rolePet);
		C1655_PetStarUpRespMessage resp = new C1655_PetStarUpRespMessage();
		resp.setPetId(reqMsg.getPetId());
		resp.setInfo(result.getInfo());
		resp.setStatus(result.getResult());
		resp.setStarProgress(rolePet.getStarProgress());
		if (GameContext.getPetApp().isPetMaxStar(rolePet)) {
			resp.setFullStar((byte) 1);
		}
		return resp;
	}

}
