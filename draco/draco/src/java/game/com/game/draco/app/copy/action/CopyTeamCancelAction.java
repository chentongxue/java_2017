package com.game.draco.app.copy.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0221_CopyTeamCancelReqMessage;
import com.game.draco.message.response.C0221_CopyTeamCancelRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.chat.ChannelType;
import sacred.alliance.magic.app.chat.ChatSysName;
import sacred.alliance.magic.app.team.Team;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class CopyTeamCancelAction extends BaseAction<C0221_CopyTeamCancelReqMessage>{

	@Override
	public Message execute(ActionContext context, C0221_CopyTeamCancelReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null;
		}
		Result result = GameContext.getCopyTeamApp().cancel(role);
		C0221_CopyTeamCancelRespMessage respMsg = new C0221_CopyTeamCancelRespMessage();
		respMsg.setStatus(result.getResult());
		respMsg.setInfo(result.getInfo());
		if(!result.isSuccess()){
			return respMsg ;
		}
		Team team = role.getTeam();
		//勦斪け耋枑尨
		if(null != team && team.getPlayerNum() >1){
			//GameContext.getChatApp().sendMessage(role, ChannelType.Team, result.getInfo(), null, null, (byte)0);
			GameContext.getChatApp().sendSysMessage(ChatSysName.Copy_Team, ChannelType.Team, result.getInfo(), null, team);
		}
		return respMsg ;
	}

}
