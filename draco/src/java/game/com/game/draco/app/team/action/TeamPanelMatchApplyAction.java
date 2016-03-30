package com.game.draco.app.team.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.app.copy.team.vo.TeamResult;
import com.game.draco.app.team.PlayerTeam;
import com.game.draco.app.team.Team;
import com.game.draco.message.request.C1313_TeamPanelMatchingReqMessage;
import com.game.draco.message.response.C1313_TeamPanelMatchingResqMessage;

public class TeamPanelMatchApplyAction extends BaseAction<C1313_TeamPanelMatchingReqMessage> {

	@Override
	public Message execute(ActionContext context, C1313_TeamPanelMatchingReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		TeamResult result = GameContext.getTeamApp().teamApply(role, reqMsg.getTargetType(), reqMsg.getTargetId(), reqMsg.getNumber());
		C1313_TeamPanelMatchingResqMessage resp = new C1313_TeamPanelMatchingResqMessage();
		resp.setStatus(result.getResult());
		resp.setInfo(result.getInfo());
		if (!result.isSuccess()) {
			Team team = role.getTeam();
			if (null != team && result.isNotifyTeam() && team.getPlayerNum() > 1) {
				GameContext.getChatApp().sendSysMessage(ChatSysName.Copy_Team, ChannelType.Team, result.getInfo(), null, team);
			}
			PlayerTeam playerTeam = (PlayerTeam) team;
			resp.setTargetId(playerTeam.getTargetId());
			resp.setTargetType(playerTeam.getTargetType());
			return resp;
		}
		resp.setTargetId(reqMsg.getTargetId());
		resp.setTargetType(reqMsg.getTargetType());
		return resp;
	}

}
