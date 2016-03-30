package com.game.draco.app.pet.action;

import com.game.draco.GameContext;
import com.game.draco.app.pet.domain.RolePet;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C1656_PetOnBattleReqMessage;
import com.game.draco.message.response.C1656_PetOnBattleRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class PetOnBattleAction extends BaseAction<C1656_PetOnBattleReqMessage> {

	@Override
	public Message execute(ActionContext context, C1656_PetOnBattleReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		String roleId = role.getRoleId();
		RolePet rolePet = GameContext.getUserPetApp().getRolePet(roleId, reqMsg.getPetId());
		if (null == rolePet) {
			return new C0003_TipNotifyMessage(GameContext.getI18n().getText(TextId.ERROR_INPUT));
		}
		C1656_PetOnBattleRespMessage resp = new C1656_PetOnBattleRespMessage();
		if ((byte) 1 == reqMsg.getOnBattle()) {
			Result result = GameContext.getPetApp().petOffBattle(role);
			resp.setStatus(result.getResult());
			resp.setPetId(reqMsg.getPetId());
			resp.setInfo(result.getInfo());
			return resp;
		}
		Result result = GameContext.getPetApp().petOnBattle(role, rolePet);
		resp.setPetId(reqMsg.getPetId());
		resp.setStatus(result.getResult());
		resp.setInfo(result.getInfo());
		return resp;
	}

}
