package com.game.draco.app.horse.action;

import java.util.List;
import java.util.Map;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.horse.domain.RoleHorse;
import com.game.draco.message.item.RoleHorseListItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C2600_RoleHorseListReqMessage;
import com.game.draco.message.response.C2600_RoleHorseListRespMessage;

public class RoleHorseListAction extends BaseAction<C2600_RoleHorseListReqMessage> {

	@Override
	public Message execute(ActionContext context, C2600_RoleHorseListReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		
		Map<Integer,RoleHorse> horseBaseMap  = GameContext.getRoleHorseApp().getAllRoleHorseByRoleId(role.getIntRoleId());
		
		if(horseBaseMap  == null || horseBaseMap .isEmpty()) {
			return new C0003_TipNotifyMessage(this.getText(TextId.HORSE_ERROR_NO));
		}
		
		List<RoleHorseListItem> horseList = GameContext.getRoleHorseApp().getRoleHorseList(role.getIntRoleId());

		C2600_RoleHorseListRespMessage respMsg = new C2600_RoleHorseListRespMessage();
		
		respMsg.setHorseList(horseList);
		
		return respMsg;
	}

}
