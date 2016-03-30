package com.game.draco.app.team.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1316_TeamPanelIntimateReqMessage;
import com.game.draco.message.response.C1316_TeamPanelIntimateResqMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class TeamPanelIntimateAction extends BaseAction<C1316_TeamPanelIntimateReqMessage> {

	@Override
	public Message execute(ActionContext context, C1316_TeamPanelIntimateReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		C1316_TeamPanelIntimateResqMessage resp = new C1316_TeamPanelIntimateResqMessage();
		resp.setAddition(GameContext.getSocialApp().getIntimateAddition(role));
		return resp;
	}

}
