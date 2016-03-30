package com.game.draco.app.team.action;

import com.game.draco.GameContext;
import com.game.draco.app.team.PlayerTeam;
import com.game.draco.app.team.Team;
import com.game.draco.message.request.C1315_TeamPanelForwardReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class TeamPanelForwardAction extends BaseAction<C1315_TeamPanelForwardReqMessage> {

	@Override
	public Message execute(ActionContext context, C1315_TeamPanelForwardReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		Team team = role.getTeam();
		if (null == team) {
			C0002_ErrorRespMessage message = new C0002_ErrorRespMessage();
			message.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return message;
		}
		return GameContext.getTeamApp().targetForward(role, (PlayerTeam) team);
	}

}
