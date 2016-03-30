package com.game.draco.app.team.action;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C1301_TeamApplyReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.vo.RoleInstance;

public class TeamApplyAction extends BaseAction<C1301_TeamApplyReqMessage> {

	@Override
	public Message execute(ActionContext context, C1301_TeamApplyReqMessage reqMsg) {
		try {
			short commandId = reqMsg.getCommandId();
			//邀请者
			RoleInstance role = this.getCurrentRole(context);
			String targetRoleId = String.valueOf(reqMsg.getRoleId());
			RoleInstance targRole = GameContext.getOnlineCenter().getRoleInstanceByRoleId(targetRoleId);
			//自己队伍人数已满，提示组队失败
			if(null != role.getTeam() && role.getTeam().isFull()){
				return new C0002_ErrorRespMessage(commandId, Status.Team_Full.getTips());
			}
			if(null == targRole){
				return new C0002_ErrorRespMessage(commandId, Status.Team_Target_Role_Offline.getTips());
			}
			//在对方黑名单中，自动被拒绝组队
			boolean flag = GameContext.getSocialApp().isShieldByTarget(role.getRoleId(), targetRoleId);
			if(flag){
				return new C0002_ErrorRespMessage(commandId, Status.Team_Shield_By_Target.getTips());
			}
			//对方设置了组队屏蔽，直接返回组队失败
			boolean shield = false;
			if(null != targRole.getTeam() && targRole.getTeam().getPlayerNum() > 1){//
				RoleInstance leader = (RoleInstance) targRole.getTeam().getLeader();
				shield = leader.getSystemSet().isShieldTeamApply();
			}else{
				shield = targRole.getSystemSet().isShieldTeamInvite();
			}
			if(shield){
				//给目标发提示信息
				String tips = Status.Team_Auto_Refuse_Targ_tip.getTips().replace(Wildcard.Role_Name, role.getRoleName());
				targRole.getBehavior().sendMessage(new C0003_TipNotifyMessage(tips));
				//给申请组队人发提示信息
				return new C0002_ErrorRespMessage(commandId, Status.Team_Auto_Refuse_Apply_Tip.getTips());
			}
			//组队验证逻辑
			Status status = GameContext.getTeamApp().canBuildTeam(role, targRole);
			if(!status.isSuccess()){
				return new C0002_ErrorRespMessage(commandId,status.getTips());
			}
			return null;
		} catch (Exception e) {
			logger.error("", e);
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(), this.getText(TextId.SYSTEM_ERROR));
		}

	}


}
