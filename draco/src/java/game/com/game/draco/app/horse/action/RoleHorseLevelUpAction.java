package com.game.draco.app.horse.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.horse.config.HorseBase;
import com.game.draco.app.horse.domain.RoleHorse;
import com.game.draco.app.horse.vo.RoleHorseLevelUpResult;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C2602_RoleHorseLevelUpReqMessage;
import com.game.draco.message.response.C2602_RoleHorseLevelUpRespMessage;

/**
 * 升星
 * @author mofun030602
 *
 */
public class RoleHorseLevelUpAction extends BaseAction<C2602_RoleHorseLevelUpReqMessage> {

	@Override
	public Message execute(ActionContext context, C2602_RoleHorseLevelUpReqMessage reqMsg) {
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
		
		C2602_RoleHorseLevelUpRespMessage respMsg = new C2602_RoleHorseLevelUpRespMessage();
		RoleHorseLevelUpResult result = GameContext.getRoleHorseApp().levelUp(role, horseId);
		respMsg.setHorseId(result.getHorseId());
		respMsg.setStarNum(result.getStarNum());
		respMsg.setSuccess(result.getResult());
		respMsg.setMsg(result.getInfo());
		return respMsg;
	}

}
