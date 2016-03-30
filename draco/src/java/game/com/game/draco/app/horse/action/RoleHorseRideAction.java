package com.game.draco.app.horse.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.map.MapProperty;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.horse.config.HorseBase;
import com.game.draco.app.horse.config.HorseProp;
import com.game.draco.app.horse.domain.RoleHorse;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C2607_RoleHorseRideReqMessage;
import com.game.draco.message.response.C2607_RoleHorseRideRespMessage;

public class RoleHorseRideAction extends BaseAction<C2607_RoleHorseRideReqMessage> {

	@Override
	public Message execute(ActionContext context, C2607_RoleHorseRideReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		boolean isOnHorse =( 1 == reqMsg.getState() );
		if(isOnHorse){
			MapInstance mapInstance = role.getMapInstance();
			if(null == mapInstance){
				return null ;
			}
			if(!GameContext.getMapApp().canMapProperty(
					role, MapProperty.canOnHorse.getType())){
				return new C0003_TipNotifyMessage(this.getText(TextId.HORSE_MAP_CANOT_ON_HORSE));
			}
		}
		C2607_RoleHorseRideRespMessage respMsg = new C2607_RoleHorseRideRespMessage();
		int horseId = reqMsg.getHorseId();
		HorseBase horseBase = GameContext.getHorseApp().getHorseBaseById(horseId);
		if(null == horseBase) {
			return null;
		}
		RoleHorse roleHorse = GameContext.getRoleHorseApp().getRoleHorse(role.getIntRoleId(), horseId);
		if(null == roleHorse) {
			horseId = GameContext.getRoleHorseApp().getBestStrongHorse(role);
			roleHorse = GameContext.getRoleHorseApp().getRoleHorse(role.getIntRoleId(), horseId);
			if(null == roleHorse){
				return new C0003_TipNotifyMessage(this.getText(TextId.HORSE_ERROR_NO));
			}
		}
		
		if(roleHorse.getState() == reqMsg.getState()){
			return null;
		}
		
		int oldHorseId = roleHorse.getHorseId();
		
		byte state = reqMsg.getState();
		if(reqMsg.getState() == -1){
			state = 0;
		}
		roleHorse = GameContext.getRoleHorseApp().onBattle(role, horseId,state);
	
		if(oldHorseId == roleHorse.getHorseId()){
			if(roleHorse.getState() == (byte)0){
				//下马成功
				if(reqMsg.getState() == -1){
					respMsg.setMsg(GameContext.getI18n().getText(TextId.HORSE_RIDE_KILL_OFF));
				}else{
					respMsg.setMsg(GameContext.getI18n().getText(TextId.HORSE_RIDE_OFF));
				}
			}else{
				//上马成功
				respMsg.setMsg(GameContext.getI18n().getText(TextId.HORSE_RIDE_ON));
			}
		}else{
			//上马成功
			respMsg.setMsg(GameContext.getI18n().getText(TextId.HORSE_RIDE_ON));
		}
	
		HorseProp horseProp = GameContext.getRoleHorseApp().getHorseProp(roleHorse.getHorseId(),roleHorse.getQuality(),roleHorse.getStar());
		respMsg.setHorseId(horseId);
		respMsg.setState(roleHorse.getState());
		respMsg.setHorseResId(horseProp.getResId());

		GameContext.getRoleHorseApp().broadcastHorse(role, horseProp.getResId(), horseId, roleHorse.getState());
		return respMsg;
	}

}
