package com.game.draco.app.survival.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.app.survival.vo.SurvivalResult;
import com.game.draco.app.team.Team;
import com.game.draco.message.request.C0280_SurvivalReqMessage;
import com.game.draco.message.response.C0280_SurvivalRespMessage;

public class SurvivalAction extends BaseAction<C0280_SurvivalReqMessage> {

	@Override
	public Message execute(ActionContext context, C0280_SurvivalReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		
		C0280_SurvivalRespMessage resp = new C0280_SurvivalRespMessage();
		SurvivalResult result = null; 
		//获得类型
		byte type = reqMsg.getType();
		resp.setType(type);
		if(type == 0){
			result = GameContext.getSurvivalBattleApp().cancel(role);
		}else if(type == 1){
			result = GameContext.getSurvivalBattleApp().apply(role,type);
		}
		
		if (result.isIgnore()) {
			return null;
		}
		if (!result.isSuccess()) {
			Team team = role.getTeam();
			if (null != team && result.isNotifyTeam() && team.getPlayerNum() > 1) {
				GameContext.getChatApp().sendSysMessage(ChatSysName.Survival_Team, ChannelType.Team, result.getInfo(), null, team);
			}
		}
		// 单人加入队列
		resp.setStatus(result.getResult());
		resp.setInfo(result.getInfo());
		return resp;
	}

}
