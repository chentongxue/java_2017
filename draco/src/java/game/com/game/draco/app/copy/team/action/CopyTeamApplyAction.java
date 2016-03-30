package com.game.draco.app.copy.team.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.app.copy.team.vo.TeamResult;
import com.game.draco.app.team.Team;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C0220_CopyTeamApplyReqMessage;
import com.game.draco.message.response.C0220_CopyTeamApplyRespMessage;

public class CopyTeamApplyAction extends BaseAction<C0220_CopyTeamApplyReqMessage> {

	@Override
	public Message execute(ActionContext context, C0220_CopyTeamApplyReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		TeamResult result = GameContext.getCopyTeamApp().apply(role, reqMsg.getCopyId(), reqMsg.getType());
		if (result.isIgnore()) {
			return null;
		}
		C0220_CopyTeamApplyRespMessage resp = new C0220_CopyTeamApplyRespMessage();
		if (!result.isSuccess()) {
			Team team = role.getTeam();
			if (null != team && result.isNotifyTeam() && team.getPlayerNum() > 1) {
				GameContext.getChatApp().sendSysMessage(ChatSysName.Copy_Team, ChannelType.Team, result.getInfo(), null, team);
			}
			C0003_TipNotifyMessage message = new C0003_TipNotifyMessage();
			message.setMsgContext(result.getInfo());
			return message;
		}
		// 仅单人加入队列
		resp.setStatus(result.getResult());
		resp.setInfo(result.getInfo());
		resp.setCopyId(reqMsg.getCopyId());
		return resp;
	}

}
