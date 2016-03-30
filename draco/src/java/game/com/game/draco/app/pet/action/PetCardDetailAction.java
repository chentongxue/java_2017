package com.game.draco.app.pet.action;

import com.game.draco.GameContext;
import com.game.draco.app.pet.domain.GoodsPet;
import com.game.draco.message.item.GoodsBasePetItem;
import com.game.draco.message.request.C1670_PetCardDetailReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C1670_PetCardDetailRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class PetCardDetailAction extends BaseAction<C1670_PetCardDetailReqMessage> {

	@Override
	public Message execute(ActionContext context, C1670_PetCardDetailReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		GoodsPet goodsPet = GameContext.getGoodsApp().getGoodsTemplate(GoodsPet.class, reqMsg.getPetId());
		if (null == goodsPet) {
			C0002_ErrorRespMessage message = new C0002_ErrorRespMessage();
			message.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return message;
		}
		C1670_PetCardDetailRespMessage resp = new C1670_PetCardDetailRespMessage();
		resp.setSource(reqMsg.getSource());
		resp.setBaseItem((GoodsBasePetItem) goodsPet.getGoodsBaseInfo(null));
		return resp;
	}

}
