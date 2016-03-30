package com.game.draco.app.pet.action;

import com.game.draco.GameContext;
import com.game.draco.app.pet.domain.GoodsPet;
import com.game.draco.app.pet.domain.RolePet;
import com.game.draco.message.request.C1669_PetShowReqMessage;
import com.game.draco.message.response.C1669_PetShowRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class PetShowAction extends BaseAction<C1669_PetShowReqMessage> {

	@Override
	public Message execute(ActionContext context, C1669_PetShowReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		C1669_PetShowRespMessage resp = new C1669_PetShowRespMessage();
		RolePet rolePet = GameContext.getPetApp().getShowRolePet(String.valueOf(reqMsg.getRoleId()));
		if (null == rolePet) {
			resp.setPetId(0);
			return resp;
		}
		GoodsPet goodsPet = GameContext.getGoodsApp().getGoodsTemplate(GoodsPet.class, rolePet.getPetId());
		if (null == goodsPet) {
			resp.setPetId(0);
			return resp;
		}
		resp.setImageId(goodsPet.getImageId());
		resp.setAttriList(GameContext.getPetApp().getRolePetAttriStrItemList(rolePet));
		resp.setBattleScore(GameContext.getPetApp().getPetBattleScore(rolePet));
		resp.setHoleInfo(GameContext.getPetApp().getHoleInfo(rolePet));
		resp.setLevel((byte) rolePet.getLevel());
		resp.setMosaicRuneList(GameContext.getPetApp().getMosaicRuneItemList(rolePet));
		resp.setName(goodsPet.getName());
		resp.setPetId(rolePet.getPetId());
		resp.setQuality(rolePet.getQuality());
		resp.setStar(rolePet.getStar());
		return resp;
	}

}
