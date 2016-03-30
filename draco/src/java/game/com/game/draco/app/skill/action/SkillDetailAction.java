package com.game.draco.app.skill.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.skill.config.SkillDetail;
import com.game.draco.app.skill.config.SkillSourceType;
import com.game.draco.app.skill.func.SkillLearnFunc;
import com.game.draco.app.skill.vo.Skill;
import com.game.draco.message.item.SkillShowItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C0317_SkillDetailReqMessage;
import com.game.draco.message.response.C0317_SkillDetailRespMessage;

public class SkillDetailAction extends BaseAction<C0317_SkillDetailReqMessage>{

	@Override
	public Message execute(ActionContext context, C0317_SkillDetailReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		short skillId = reqMsg.getSkillId();
		Skill skill = GameContext.getSkillApp().getSkill(skillId);
		if(null == skill){
			return new C0003_TipNotifyMessage(this.getText(TextId.Sys_Param_Error));
		}
		byte sourceType = reqMsg.getSourceType();
		SkillSourceType skillSourceType = SkillSourceType.get(sourceType);
		SkillLearnFunc learnFunc = GameContext.getUserSkillApp().getSkillLearnFunc(skillSourceType);
		if(null == learnFunc){
			return new C0003_TipNotifyMessage(this.getText(TextId.Sys_Param_Error));
		}
		String parameter = reqMsg.getParameter();
		C0317_SkillDetailRespMessage respMsg = new C0317_SkillDetailRespMessage();
		respMsg.setSourceType(sourceType);
		respMsg.setParameter(parameter);
		
		SkillShowItem skillShowItem = GameContext.getSkillApp().getSkillShowItem(role, learnFunc, parameter, skillId);
		if(null != skillShowItem){
			respMsg.setSkillShowItem(skillShowItem);
		}
		int currLevel = learnFunc.getSkillLevel(role, skillId, parameter);
		int maxLevel = skill.getMaxLevel();
		if(currLevel >0){
			SkillDetail detail = skill.getSkillDetail(currLevel);
			respMsg.setCd((short)detail.getRealCd(role));
			respMsg.setDesc(detail.getDesc());
		}
		boolean isFull = currLevel >= maxLevel ;
		respMsg.setFull(isFull? (byte)1 : (byte)0);
		if(isFull){
			return respMsg ;
		}
		SkillDetail nextDetail = skill.getSkillDetail(currLevel + 1);
		if(null == nextDetail){
			return respMsg ;
		}
		respMsg.setNextCd((short)nextDetail.getRealCd(role));
		respMsg.setNextDesc(nextDetail.getDesc());
		return respMsg;
	}

}
