package com.game.draco.app.horse.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.horse.config.HorseBase;
import com.game.draco.app.horse.config.HorseSkillLimit;
import com.game.draco.app.horse.domain.RoleHorse;
import com.game.draco.app.horse.domain.RoleHorseSkill;
import com.game.draco.app.horse.vo.RoleHorseSkillResult;
import com.game.draco.app.skill.vo.Skill;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C2611_RoleHorseTrainReqMessage;
import com.game.draco.message.response.C2611_RoleHorseTrainRespMessage;

/**
 * 坐骑技能训练
 * @author zhb
 *
 */
public class RoleHorseTrainAction extends BaseAction<C2611_RoleHorseTrainReqMessage> {

	@Override
	public Message execute(ActionContext context, C2611_RoleHorseTrainReqMessage reqMsg) {
		RoleHorseSkillResult result = new RoleHorseSkillResult();
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
		
		String key = roleHorseSkill.getSkillId() + Cat.underline + (roleHorseSkill.getLevel()+1);
		HorseSkillLimit limit = GameContext.getHorseApp().getSkillLimit(key);
		
		if(limit == null || roleHorseSkill.getLevel() >= skill.getMaxLevel()){
			return new C0003_TipNotifyMessage(this.getText(TextId.Skill_MaxLevel_Fail));
		}
		
		int oldLuck = roleHorseSkill.getLuck();
		
		result = GameContext.getRoleHorseApp().trainSkill(role, roleHorse, roleHorseSkill);
		if(result.isIgnore()){
			return null;
		}
		
		C2611_RoleHorseTrainRespMessage respMsg = new C2611_RoleHorseTrainRespMessage();
		
		if(result.isSuccess()){
			if(result.isFlag()){
				if(roleHorseSkill.getLevel() >= skill.getMaxLevel()){
					respMsg.setSuccess((byte)2);
					result.setInfo(GameContext.getI18n().messageFormat(TextId.HORSE_SKILL_LEVELUP_SUCCESS)+getText(TextId.HORSE_SKILL_MAX_LEVEL_ERR));
				}else{
					result.setInfo(getText(TextId.HORSE_SKILL_LEVELUP_SUCCESS));
					respMsg.setSuccess(result.getResult());
				}
			}else{
				int nowLuck = result.getLuck() - oldLuck;
				result.setInfo(GameContext.getI18n().messageFormat(TextId.HORSE_SKILL_LEVEL_ADD_LUCK,nowLuck));
				respMsg.setLuck(result.getLuck());
			}
		}
		respMsg.setFlag(result.isFlag());
		respMsg.setMsg(result.getInfo());
		respMsg.setSuccess(result.getResult());
		return respMsg;
		
	}

}
