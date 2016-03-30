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
import com.game.draco.message.push.C2609_RoleHorseUpgradeNotifyMessage;
import com.game.draco.message.request.C2607_RoleHorseRideReqMessage;
import com.game.draco.message.response.C2607_RoleHorseRideRespMessage;

public class RoleHorseRideAction extends BaseAction<C2607_RoleHorseRideReqMessage> {

	@Override
	public Message execute(ActionContext context, C2607_RoleHorseRideReqMessage reqMsg) {
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
		
		int oldHorseId = roleHorse.getHorseId();
		
		C2607_RoleHorseRideRespMessage respMsg = new C2607_RoleHorseRideRespMessage();
		
		roleHorse = GameContext.getRoleHorseApp().onBattle(role, horseId,reqMsg.getState());
		
		//英雄姻缘
		GameContext.getHeroApp().onHorseChanged(role.getIntRoleId(), roleHorse.getHorseId(), oldHorseId);
		
		if(oldHorseId == roleHorse.getHorseId()){
			if(roleHorse.getState() == (byte)0){
				//下马成功
				respMsg.setMsg(GameContext.getI18n().getText(TextId.HORSE_RIDE_OFF));
			}else{
				//上马成功
				respMsg.setMsg(GameContext.getI18n().getText(TextId.HORSE_RIDE_ON));
			}
		}else{
			//上马成功
			respMsg.setMsg(GameContext.getI18n().getText(TextId.HORSE_RIDE_ON));
		}
		
		respMsg.setHorseId(horseId);
		respMsg.setState(roleHorse.getState());
		respMsg.setHorseImageId(horseBase.getImageId());

		//视野通知
		C2609_RoleHorseUpgradeNotifyMessage msg = new C2609_RoleHorseUpgradeNotifyMessage();
		msg.setRoleId(role.getIntRoleId());
		msg.setHorseId(horseId);
		msg.setState(roleHorse.getState());
		msg.setImageId(horseBase.getImageId());
		GameContext.getRoleHorseApp().broadcast(role,msg);
		
		return respMsg;
	}

}
