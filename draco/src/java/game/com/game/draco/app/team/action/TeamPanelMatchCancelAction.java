package com.game.draco.app.team.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1314_TeamPanelMatchingCancelReqMessage;
import com.game.draco.message.response.C1314_TeamPanelMatchingCancelResqMessage;

public class TeamPanelMatchCancelAction extends BaseAction<C1314_TeamPanelMatchingCancelReqMessage> {

	@Override
	public Message execute(ActionContext context, C1314_TeamPanelMatchingCancelReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		Result result = GameContext.getTeamApp().cancelTeamApply(role);
		C1314_TeamPanelMatchingCancelResqMessage resp = new C1314_TeamPanelMatchingCancelResqMessage();
		resp.setInfo(result.getInfo());
		resp.setStatus(result.getResult());
		return resp;
	}

}
