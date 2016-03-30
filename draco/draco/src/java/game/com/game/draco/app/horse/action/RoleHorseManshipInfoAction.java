package com.game.draco.app.horse.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.horse.config.HorseBase;
import com.game.draco.app.horse.domain.RoleHorse;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C2604_RoleHorseManshipInfoReqMessage;
import com.game.draco.message.response.C2604_RoleHorseManshipInfoRespMessage;

public class RoleHorseManshipInfoAction extends BaseAction<C2604_RoleHorseManshipInfoReqMessage> {

	@Override
	public Message execute(ActionContext context, C2604_RoleHorseManshipInfoReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		int horseId = reqMsg.getHorseId();
		HorseBase horseBase = GameContext.getHorseApp().getHorseBaseById(horseId);
		if(null == horseBase) {
			return new C0003_TipNotifyMessage(this.getText(TextId.Sys_Param_Error));
		}
		RoleHorse roleHorse = GameContext.getRoleHorseApp().getRoleHorse(role.getIntRoleId(), horseId);
		if(null == roleHorse) {
			return new C0003_TipNotifyMessage(this.getText(TextId.HORSE_ERROR_NO));
		}
		
		C2604_RoleHorseManshipInfoRespMessage respMsg = new C2604_RoleHorseManshipInfoRespMessage();
		
		respMsg = GameContext.getRoleHorseApp().sendC2604_RoleHorseManshipInfoRespMessage(role.getIntRoleId(),horseId);
		
		return respMsg;
	}

}
