package com.game.draco.app.pet.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.pet.domain.GoodsPet;
import com.game.draco.app.pet.domain.RolePet;
import com.game.draco.message.request.C1652_PetInfoReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C1652_PetInfoRespMessage;

public class PetInfoAction extends BaseAction<C1652_PetInfoReqMessage> {

	@Override
	public Message execute(ActionContext context, C1652_PetInfoReqMessage reqMsg) {
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
		C1652_PetInfoRespMessage resp = new C1652_PetInfoRespMessage();
		resp.setBattleScore(rolePet.getScore());
		resp.setHoleInfo(GameContext.getPetApp().getHoleInfo(rolePet));
		resp.setMosaicRuneList(GameContext.getPetApp().getMosaicRuneItemList(rolePet));
		GoodsPet goodsPet = GameContext.getGoodsApp().getGoodsTemplate(GoodsPet.class, rolePet.getPetId());
		resp.setImageId(goodsPet.getImageId());
		resp.setLevel((byte) rolePet.getLevel());
		resp.setMaxExp(GameContext.getPetApp().getMaxExp(rolePet));
		resp.setMaxStar(goodsPet.getMaxStar());
		resp.setName(goodsPet.getName());
		resp.setOnBattle(GameContext.getUserPetApp().isOnBattle(roleId, rolePet.getPetId()) ? (byte) 1 : (byte) 0);
		resp.setPetId(rolePet.getPetId());
		resp.setQuality(goodsPet.getQualityType());
		resp.setPanelResId(goodsPet.getPanelResId());
		resp.setShadowId(goodsPet.getShadowId());
		resp.setPanelRatio(goodsPet.getPanelRatio());
		resp.setStar((byte) rolePet.getStar());
		resp.setBattleScore(rolePet.getScore());
		resp.setExp(rolePet.getExp());
		return resp;
	}

}
