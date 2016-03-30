package com.game.draco.app.pet.action;

import java.util.List;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.pet.domain.RolePet;
import com.game.draco.message.item.AttriTypeStrValueItem;
import com.game.draco.message.item.SkillSimpleItem;
import com.game.draco.message.request.C1653_PetDetailReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C1653_PetDetailRespMessage;

public class PetDetailAction extends BaseAction<C1653_PetDetailReqMessage> {

	@Override
	public Message execute(ActionContext context, C1653_PetDetailReqMessage reqMsg) {
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
		C1653_PetDetailRespMessage resp = new C1653_PetDetailRespMessage();
		List<AttriTypeStrValueItem> attriList = GameContext.getPetApp().getRolePetAttriStrItemList(rolePet);
		List<SkillSimpleItem> skillList = GameContext.getPetApp().getSkillSimpleItemList(rolePet);
		resp.setAttriList(attriList);
		resp.setSkillList(skillList);
		return resp;
	}

}
