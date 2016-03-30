package com.game.draco.app.horse.action;

import java.util.List;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.horse.config.HorseBase;
import com.game.draco.app.horse.config.ManshipDes;
import com.game.draco.app.horse.config.ManshipLevelFilter;
import com.game.draco.app.horse.domain.RoleHorse;
import com.game.draco.app.horse.vo.RoleHorseLevelUpResult;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C2603_RoleHorseManshipReqMessage;

public class RoleHorseManshipLevelUpAction extends BaseAction<C2603_RoleHorseManshipReqMessage> {

	@Override
	public Message execute(ActionContext context, C2603_RoleHorseManshipReqMessage reqMsg) {
		
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
		
//		C2603_RoleHorseManshipExpRespMessage respMsg = new C2603_RoleHorseManshipExpRespMessage();
		
		short maxManshipLevel = 0;
		//获取骑术数据
		List<ManshipDes> manshipDesList = GameContext.getHorseApp().getManshipDesList();
	
		if(manshipDesList != null && !manshipDesList.isEmpty()){
			int i = 0;
			for(ManshipDes manship : manshipDesList){
				i++;
				if(roleHorse.getManshipLevel() < manship.getMaxLevel()){
					maxManshipLevel = manship.getMaxLevel();
					break;
				}
				if(i == manshipDesList.size()){
					maxManshipLevel = manship.getMaxLevel();
				}
			}
			
			if(roleHorse.getManshipLevel() >= maxManshipLevel){
				return new C0003_TipNotifyMessage(this.getText(TextId.HORSE_MANSHIP_ERR_MAX_LEVEL));
			}
		}

		ManshipLevelFilter filter = GameContext.getHorseApp().getManshipLevelFilterByType(horseBase.getRace() + Cat.underline + (roleHorse.getManshipLevel()+1));
		if(filter != null){
			if(filter.getRoleLevel() > role.getLevel()){
				return new C0003_TipNotifyMessage(this.messageFormat(TextId.HORSE_MANSHIP_ERR_ROLE_LEVEL,filter.getRoleLevel()));
			}
		}
		RoleHorseLevelUpResult result = GameContext.getRoleHorseApp().manshipLevelUp(role, horseId);
		if(result.isSuccess()){
			role.getBehavior().sendMessage(GameContext.getRoleHorseApp().sendC2604_RoleHorseManshipInfoRespMessage(role.getIntRoleId(),horseId));
			return null;
		}
		return new C0003_TipNotifyMessage(result.getInfo());
		
	}

}
