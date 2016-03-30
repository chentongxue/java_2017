package com.game.draco.app.horse.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.horse.config.HorseBase;
import com.game.draco.app.horse.domain.RoleHorse;
import com.game.draco.app.horse.domain.RoleHorseSkill;
import com.game.draco.app.skill.vo.Skill;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C2612_RoleHorseSkillInfoReqMessage;
import com.game.draco.message.response.C2612_RoleHorseSkillInfoRespMessage;

public class RoleHorseSkillInfoAction extends BaseAction<C2612_RoleHorseSkillInfoReqMessage> {

	@Override
	public Message execute(ActionContext context, C2612_RoleHorseSkillInfoReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		int horseId = reqMsg.getHorseId();
		short skillId = reqMsg.getSkillId();
		HorseBase horseBase = GameContext.getHorseApp().getHorseBaseById(horseId);
		if(null == horseBase) {
			return new C0003_TipNotifyMessage(this.getText(TextId.Sys_Param_Error));
		}
		
		Skill skill = GameContext.getSkillApp().getSkill(skillId);
		if(null == skill){
			return new C0003_TipNotifyMessage(this.getText(TextId.Skill_Not_Exist));
		}

		RoleHorse roleHorse = GameContext.getRoleHorseApp().getRoleHorse(role.getIntRoleId(), horseId);
		if(null == roleHorse) {
			return new C0003_TipNotifyMessage(this.getText(TextId.HORSE_ERROR_NO));
		}
		
		RoleHorseSkill roleHorseSkill = null;
		for(RoleHorseSkill horseSkill : roleHorse.getSkillList()){
			if(horseSkill.getSkillId() == skillId){
				roleHorseSkill = horseSkill;
			}
		}
		if(null == roleHorseSkill) {
			return new C0003_TipNotifyMessage(this.getText(TextId.HORSE_SKILL_ERR));
		}
		
		C2612_RoleHorseSkillInfoRespMessage respMsg = new C2612_RoleHorseSkillInfoRespMessage();
		respMsg = GameContext.getRoleHorseApp().sendC2612_RoleHorseSkillInfoRespMessage(role, roleHorse, roleHorseSkill);
		respMsg.setHorseId(horseId);
		respMsg.setSkillId(skillId);
		return respMsg;
	}

}
