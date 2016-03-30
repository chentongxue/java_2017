package com.game.draco.app.pet.action;

import java.util.List;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.pet.domain.GoodsPet;
import com.game.draco.app.pet.domain.GoodsPetAid;
import com.game.draco.app.pet.domain.RolePet;
import com.game.draco.message.item.AttriTypeStrValueItem;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.request.C1654_PetStarUpInfoReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C1654_PetStarUpInfoRespMessage;

public class PetStarUpInfoAction extends BaseAction<C1654_PetStarUpInfoReqMessage> {

	@Override
	public Message execute(ActionContext context, C1654_PetStarUpInfoReqMessage reqMsg) {
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
		C1654_PetStarUpInfoRespMessage resp = new C1654_PetStarUpInfoRespMessage();
		if (GameContext.getPetApp().isPetMaxStar(rolePet)) {
			resp.setFullStar((byte) 1);
			resp.setAttriList(GameContext.getPetApp().getFullStarAttriList(rolePet));
			return resp;
		}
		resp.setFullStar((byte) 0);
		resp.setPetId(rolePet.getPetId());
		resp.setStarProgress(rolePet.getStarProgress());
		// 获取模版信息
		GoodsPet goodsPet = GameContext.getGoodsApp().getGoodsTemplate(GoodsPet.class, rolePet.getPetId());
		GoodsPetAid goodsPetAid = GameContext.getGoodsApp().getGoodsTemplate(GoodsPetAid.class, goodsPet.getShadowId());
		GoodsLiteNamedItem goodsItem = new GoodsLiteNamedItem();
		goodsItem.setGoodsId(goodsPetAid.getId());
		goodsItem.setGoodsName(goodsPetAid.getName());
		goodsItem.setBindType(goodsPetAid.getBindType());
		goodsItem.setGoodsImageId(goodsPetAid.getImageId());
		goodsItem.setGoodsLevel((byte) goodsPetAid.getLevel());
		goodsItem.setQualityType(goodsPetAid.getQualityType());
		goodsItem.setNum((short) GameContext.getPetApp().getStarShadowNumber(rolePet));
		resp.setGoodsItem(goodsItem);
		List<AttriTypeStrValueItem> attriList = GameContext.getPetApp().getStarChangeAttriList(rolePet);
		resp.setAttriList(attriList);
		return resp;
	}

}
