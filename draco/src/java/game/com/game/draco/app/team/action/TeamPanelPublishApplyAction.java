package com.game.draco.app.team.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.team.PlayerTeam;
import com.game.draco.message.request.C1312_TeamPanelPublishApplyReqMessage;
import com.game.draco.message.response.C1312_TeamPanelApplyResqMessage;

public class TeamPanelPublishApplyAction extends BaseAction<C1312_TeamPanelPublishApplyReqMessage> {

	@Override
	public Message execute(ActionContext context, C1312_TeamPanelPublishApplyReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		C1312_TeamPanelApplyResqMessage resp = new C1312_TeamPanelApplyResqMessage();
		String[] params = Util.splitStr(reqMsg.getParam(), Cat.comma);
		if (params.length < 2) {
			resp.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return resp;
		}
		// 连接是否过期
		PlayerTeam team = GameContext.getTeamApp().getPublishTeam(params[0]);
		if (null == team || !team.isPublish() || !team.getTarget().equals(params[1])) {
			resp.setInfo(GameContext.getI18n().getText(TextId.Team_Panel_Publish_Overdue));
			return resp;
		}
		// 是否已经在队伍中
		if (GameContext.getTeamApp().isInSameTeam(role, team.getLeader())) {
			resp.setInfo(GameContext.getI18n().getText(TextId.Team_Panel_Already_In_Team));
			return resp;
		}
		// 队伍是否满员
		if (team.isFull()) {
			resp.setInfo(GameContext.getI18n().getText(TextId.Team_Full));
			return resp;
		}
		// 申请入队
		Result result = GameContext.getTeamApp().teamPanelPublishApply(role, team);
		resp.setStatus(result.getResult());
		resp.setInfo(result.getInfo());
		return resp;
	}
	
}
