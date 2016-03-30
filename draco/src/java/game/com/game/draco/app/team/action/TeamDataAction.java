package com.game.draco.app.team.action;

import com.game.draco.app.team.Team;
import com.game.draco.message.request.C1300_TeamDataReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C1300_TeamDataRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class TeamDataAction extends BaseAction<C1300_TeamDataReqMessage> {
	
	@Override
	public Message execute(ActionContext context, C1300_TeamDataReqMessage reqMessage) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		try {
			Team team = role.getTeam();
			if(null == team || team.getPlayerNum() < 2){
				return new C1300_TeamDataRespMessage();
			}
			role.getTeam().syschDataNotify(role);
			return null;
		} catch (Exception e) {
			logger.error("", e);
			return new C0002_ErrorRespMessage(reqMessage.getCommandId(), this.getText(TextId.SYSTEM_ERROR));

		}

	}

}
