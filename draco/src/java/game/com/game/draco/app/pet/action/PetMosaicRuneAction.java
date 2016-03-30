package com.game.draco.app.pet.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.pet.domain.RolePet;
import com.game.draco.app.pet.vo.PetMosaicResult;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C1665_PetMosaicReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C1665_PetMosaicRespMessage;

public class PetMosaicRuneAction extends BaseAction<C1665_PetMosaicReqMessage> {

	@Override
	public Message execute(ActionContext context, C1665_PetMosaicReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		String roleId = role.getRoleId();
		int petId = reqMsg.getPetId();
		RolePet rolePet = GameContext.getUserPetApp().getRolePet(roleId, petId);
		if (null == rolePet) {
			C0002_ErrorRespMessage message = new C0002_ErrorRespMessage();
			message.setInfo(GameContext.getI18n().getText(TextId.Pet_Not_Owned));
			return message;
		}

		byte[] holeInfo = GameContext.getPetApp().getHoleInfo(rolePet);
		if ((byte) 0 == holeInfo[reqMsg.getHole()]) {
			C0003_TipNotifyMessage message = new C0003_TipNotifyMessage();
			String msgContext = GameContext.getI18n().messageFormat(TextId.Pet_Hole_Star_Num, GameContext.getPetApp().getOpenHoleStar(reqMsg.getHole()));
			message.setMsgContext(msgContext);
			return message;
		}
		RoleGoods rg = GameContext.getUserGoodsApp().getRoleGoods(role, StorageType.bag, reqMsg.getRuneInstanceId(), 0);
		if (null == rg) {
			C0002_ErrorRespMessage message = new C0002_ErrorRespMessage();
			message.setInfo(GameContext.getI18n().getText(TextId.GOODS_NO_EXISTS));
			return message;
		}
		PetMosaicResult result = GameContext.getPetApp().rolePetMosaicRune(role, rolePet, rg, reqMsg.getHole());
		if(result.isIgnore()){
			return null;
		}
		C1665_PetMosaicRespMessage resp = new C1665_PetMosaicRespMessage();
		if (!result.isSuccess()) {
			resp.setInfo(result.getInfo());
			return resp;
		}
		resp.setBattleScore(result.getBattleScore());
		resp.setInfo(result.getInfo());
		resp.setMosaicRuneItem(result.getMosaicRuneItem());
		resp.setPetId(result.getPetId());
		resp.setStatus(result.getResult());
		return resp;
	}

}
