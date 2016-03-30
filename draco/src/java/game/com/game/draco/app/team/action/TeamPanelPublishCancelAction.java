package com.game.draco.app.team.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1310_TeamPanelPublishCancelReqMessage;
import com.game.draco.message.response.C1310_TeamPanelPublishCancelRespMessage;

public class TeamPanelPublishCancelAction extends BaseAction<C1310_TeamPanelPublishCancelReqMessage> {

	@Override
	public Message execute(ActionContext context, C1310_TeamPanelPublishCancelReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		Result result = GameContext.getTeamApp().cancelTeamPublish(role);
		C1310_TeamPanelPublishCancelRespMessage resp = new C1310_TeamPanelPublishCancelRespMessage();
		resp.setStatus(result.getResult());
		resp.setInfo(result.getInfo());
		return resp;
	}

}
