package com.game.draco.app.team.action;

import com.game.draco.GameContext;
import com.game.draco.app.team.LeaveTeam;
import com.game.draco.app.team.Team;
import com.game.draco.message.request.C1303_TeamLeaveReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class TeamLeaveAction extends BaseAction<C1303_TeamLeaveReqMessage> {

	@Override
	public Message execute(ActionContext context, C1303_TeamLeaveReqMessage reqMessage) {
		try {
			RoleInstance role = this.getCurrentRole(context);
			if(role == null){
				return null;
			}
			Team team = role.getTeam();
			if (team == null) {
				return null;
			}
			String reqRoleId = String.valueOf(reqMessage.getRoleId());
			RoleInstance target = getRoleInstance(reqRoleId); //被操作者
			int operate = reqMessage.getType(); //操作类型 1：主动离队，0：队长踢人
			
			// 主动离队
			LeaveTeam leaveType = LeaveTeam.getLeaveType(operate);
			if (LeaveTeam.apply == leaveType) {
				team.memberLeave(role, LeaveTeam.apply);
				return null;
			}
			//队长踢人
			else if(LeaveTeam.kicked == leaveType){
				//请求操作者是否为队长
				if (!team.isLeader(role)) {
					return new C0002_ErrorRespMessage(reqMessage.getCommandId(), Status.Team_Oprate_Not_Leader.getTips());
				}
				if (role.getRoleId().equals(reqRoleId)) {
					return new C0002_ErrorRespMessage(reqMessage.getCommandId(), Status.Team_Role_Self.getTips());
				}
				team.memberLeave(target, LeaveTeam.kicked);
			}
			
			return null;
		} catch (Exception e) {
			logger.error("", e);
			return new C0002_ErrorRespMessage(reqMessage.getCommandId(), this.getText(TextId.SYSTEM_ERROR));

		}

	}
	
	private RoleInstance getRoleInstance(String roleId){
		try{
			return GameContext.getUserRoleApp().getRoleByRoleId(roleId);
		}catch(Exception e){
			return null;
		}
	}

}
