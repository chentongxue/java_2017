package com.game.draco.app.team.action;

import com.game.draco.GameContext;
import com.game.draco.app.team.Team;
import com.game.draco.message.request.C1304_TeamChangCaptainReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class TeamChangCaptainAction extends BaseAction<C1304_TeamChangCaptainReqMessage> {
	@Override
	public Message execute(ActionContext context, C1304_TeamChangCaptainReqMessage reqMsg) {
		try {
			RoleInstance leaderRole =  this.getCurrentRole(context);
			if(leaderRole.getIntRoleId() == reqMsg.getRoleId()){
				return null;
			}
			RoleInstance winLeaderRole = GameContext.getOnlineCenter().getRoleInstanceByRoleId(String.valueOf(reqMsg.getRoleId()));
			if(null == winLeaderRole){
				return new C0002_ErrorRespMessage(reqMsg.getCommandId(), Status.Team_Offline_Role_Is_Leader.getTips());
			}
			Team team = leaderRole.getTeam();
			if (null == team || !team.isLeader(leaderRole)) {
				return new C0002_ErrorRespMessage(reqMsg.getCommandId(), Status.Team_Oprate_Not_Leader.getTips());
			}
			team.changeLeader(winLeaderRole);
			return null;
		} catch (Exception e) {
			logger.error("", e);
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(), this.getText(TextId.SYSTEM_ERROR));
		}

	}

}
