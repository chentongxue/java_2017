package com.game.draco.app.team.action;

import com.game.draco.GameContext;
import com.game.draco.app.team.Team;
import com.game.draco.message.request.C1302_TeamReplyReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C1302_TeamReplyRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class TeamReplyAction extends BaseAction<C1302_TeamReplyReqMessage> {

	@Override
	public Message execute(ActionContext context, C1302_TeamReplyReqMessage reqMsg) {
		try {
			RoleInstance role = this.getCurrentRole(context);
			//答复组队时，重置操作时间为0
			role.setTeamApplyTime(0);
			//邀请者
			String reqRoleId = String.valueOf(reqMsg.getRoleId());
			RoleInstance invitorRole = GameContext.getOnlineCenter().getRoleInstanceByRoleId(reqRoleId);
			if(role == null || invitorRole == null){
				return null;
			}
			C1302_TeamReplyRespMessage resp = new C1302_TeamReplyRespMessage();
			resp.setInfo(Status.Team_Fail.getTips());
			//只有队长或未组队的人才可以操作
			Team team = role.getTeam();
			if(null != team && team.getLeader().getIntRoleId() != role.getIntRoleId()){
				GameContext.getMessageCenter().send("", invitorRole.getUserId(), resp);
				return new C0002_ErrorRespMessage(reqMsg.getCommandId(), Status.Team_Oprate_Not_Leader.getTips());
			}
			//拒绝组队
			if (0 == reqMsg.getType()) {
				resp.setInfo(role.getRoleName() + Status.Team_Refuse.getTips());
				/** 答复激请人 */
				GameContext.getMessageCenter().send("", invitorRole.getUserId(), resp);
				return null;
			}
			Status status = GameContext.getTeamApp().buildTeam(role, invitorRole);
			if(!status.isSuccess()){
				return new C0002_ErrorRespMessage(reqMsg.getCommandId(),status.getTips());
			}
			return null;
		} catch (Exception e) {
			logger.error("", e);
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(), this.getText(TextId.SYSTEM_ERROR));

		}

	}

}
