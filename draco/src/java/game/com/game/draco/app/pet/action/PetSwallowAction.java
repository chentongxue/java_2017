package com.game.draco.app.pet.action;

import java.util.List;

import org.python.google.common.primitives.Ints;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.pet.domain.RolePet;
import com.game.draco.app.pet.vo.PetSwallowResult;
import com.game.draco.message.request.C1660_PetSwallowReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C1660_PetSwallowRespMessage;

public class PetSwallowAction extends BaseAction<C1660_PetSwallowReqMessage> {

	@Override
	public Message execute(ActionContext context, C1660_PetSwallowReqMessage reqMsg) {
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
		C1660_PetSwallowRespMessage resp = new C1660_PetSwallowRespMessage();
		PetSwallowResult result = GameContext.getPetApp().petSwallow(role, rolePet, reqMsg.getSwallowList());
		if (RespTypeStatus.FAILURE == result.getStatus()) {
			resp.setStatus(RespTypeStatus.FAILURE);
			resp.setInfo(result.getInfo());
			return resp;
		}
		List<Integer> swallowList = result.getSwallowPetList();
		if (!Util.isEmpty(swallowList)) {
			resp.setSwallowPetId(Ints.toArray(swallowList));
		}
		resp.setInfo(result.getInfo());
		resp.setStatus(result.getStatus());
		resp.setExp(rolePet.getExp());
		resp.setSwallowExp(result.getSwallowExp());
		if ((byte) 2 == result.getStatus()) {
			resp.setBattleScore(GameContext.getPetApp().getPetBattleScore(rolePet));
			resp.setLevel((byte) rolePet.getLevel());
			resp.setMaxExp(GameContext.getPetApp().getMaxExp(rolePet));
			return resp;
		}
		return resp;
	}
	
}
