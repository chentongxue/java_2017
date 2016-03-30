package com.game.draco.app.copy.action;

import com.game.draco.GameContext;
import com.game.draco.app.copy.team.CopyTeamResult;
import com.game.draco.message.request.C0220_CopyTeamApplyReqMessage;
import com.game.draco.message.response.C0220_CopyTeamApplyRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.chat.ChannelType;
import sacred.alliance.magic.app.chat.ChatSysName;
import sacred.alliance.magic.app.team.Team;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class CopyTeamApplyAction extends BaseAction<C0220_CopyTeamApplyReqMessage>{

	@Override
	public Message execute(ActionContext context, C0220_CopyTeamApplyReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null;
		}
		short copyId = reqMsg.getCopyId();
		CopyTeamResult result = GameContext.getCopyTeamApp().apply(role, copyId);
		C0220_CopyTeamApplyRespMessage respMsg = new C0220_CopyTeamApplyRespMessage();
		respMsg.setStatus(result.getResult());
		respMsg.setInfo(result.getInfo());
		if(result.isSuccess()){
			respMsg.setInfo(this.getText(TextId.COPY_TEAM_APPLY_SUCCESS));
		}
		respMsg.setCopyId(copyId);
		Team team = role.getTeam();
		//勦斪け耋枑尨
		if(null != team && result.isNotifyTeam() && team.getPlayerNum() >1 ){
			GameContext.getChatApp().sendSysMessage(ChatSysName.Copy_Team, ChannelType.Team, result.getInfo(), null, team);
		}
		return respMsg ;
	}

}
