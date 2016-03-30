package com.game.draco.app.horse.action;

import java.util.List;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.item.RoleHorseItem;
import com.game.draco.message.request.C2600_RoleHorseListReqMessage;
import com.game.draco.message.response.C2600_RoleHorseListRespMessage;

public class RoleHorseListAction extends BaseAction<C2600_RoleHorseListReqMessage> {

	@Override
	public Message execute(ActionContext context, C2600_RoleHorseListReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		
		List<RoleHorseItem> horseList = GameContext.getRoleHorseApp().getRoleHorseList(role);

		C2600_RoleHorseListRespMessage respMsg = new C2600_RoleHorseListRespMessage();
		
		respMsg.setHorseList(horseList);
		
		return respMsg;
	}

}
