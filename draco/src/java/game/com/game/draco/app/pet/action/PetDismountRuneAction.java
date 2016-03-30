package com.game.draco.app.pet.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.pet.domain.RolePet;
import com.game.draco.app.pet.vo.PetMosaicResult;
import com.game.draco.message.request.C1666_PetMosaicRemovalReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C1666_PetMosaicFeeRespMessage;

public class PetDismountRuneAction extends BaseAction<C1666_PetMosaicRemovalReqMessage> {

	@Override
	public Message execute(ActionContext context, C1666_PetMosaicRemovalReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		String roleId = role.getRoleId();
		int petId = reqMsg.getPetId();
		RolePet rolePet = GameContext.getUserPetApp().getRolePet(roleId, petId);
		if (null == rolePet) {
			C0002_ErrorRespMessage message = new C0002_ErrorRespMessage();
			message.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return message;
		}
		PetMosaicResult result = GameContext.getPetApp().rolePetDismountRune(role, rolePet, reqMsg.getHole());
		if(result.isIgnore()){
			return null;
		}
		C1666_PetMosaicFeeRespMessage resp = new C1666_PetMosaicFeeRespMessage();
		if (!result.isSuccess()) {
			resp.setInfo(result.getInfo());
			return resp;
		}
		resp.setStatus(result.getResult());
		resp.setBattleScore(result.getBattleScore());
		resp.setInfo(result.getInfo());
		resp.setPetId(result.getPetId());
		resp.setHole(result.getHoleNum());
		resp.setHaveRune(result.getIsHavaRune());
		return resp;
	}

}
