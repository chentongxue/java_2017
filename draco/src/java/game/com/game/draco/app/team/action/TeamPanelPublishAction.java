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
import com.game.draco.message.request.C1309_TeamPanelPublishReqMessage;
import com.game.draco.message.response.C1309_TeamPanelPublishRespMessage;

public class TeamPanelPublishAction extends BaseAction<C1309_TeamPanelPublishReqMessage> {

	@Override
	public Message execute(ActionContext context, C1309_TeamPanelPublishReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		TeamResult result = GameContext.getTeamApp().teamPublish(role, reqMsg.getTargetType(), reqMsg.getTargetId(), reqMsg.getNumber());
		C1309_TeamPanelPublishRespMessage resp = new C1309_TeamPanelPublishRespMessage();
		resp.setStatus(result.getResult());
		resp.setInfo(result.getInfo());
		// 如果发布失败，消息通知队伍频道并返回原有目标
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
		// 返回当前目标
		resp.setTargetId(reqMsg.getTargetId());
		resp.setTargetType(reqMsg.getTargetType());
		return resp;
	}

}
