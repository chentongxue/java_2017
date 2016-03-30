package com.game.draco.app.pet.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C1664_PetPvpReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;

public class PetPvpAction extends BaseAction<C1664_PetPvpReqMessage> {

	@Override
	public Message execute(ActionContext context, C1664_PetPvpReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		String roleId = role.getRoleId();
		String targetId = reqMsg.getRoleId();
		if (roleId.equals(targetId)) {
			C0002_ErrorRespMessage message = new C0002_ErrorRespMessage();
			message.setInfo(GameContext.getI18n().getText(TextId.Pet_Challenge_Cannot_Self));
			return message;
		}
		// 宠物抢夺PVP
		Result result = GameContext.getPetApp().petChallenge(role, reqMsg.getRoleId(), reqMsg.getRoleName(), reqMsg.getPetId(), reqMsg.getType());
		if (!result.isSuccess()) {
			C0003_TipNotifyMessage tipMsg = new C0003_TipNotifyMessage();
			tipMsg.setMsgContext(result.getInfo());
			return tipMsg;
		}
		return null;
	}

}
