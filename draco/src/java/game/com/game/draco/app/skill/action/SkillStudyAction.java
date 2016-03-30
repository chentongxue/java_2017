package com.game.draco.app.skill.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.skill.config.SkillSourceType;
import com.game.draco.app.skill.func.SkillLearnFunc;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C0316_SkillStudyReqMessage;

public class SkillStudyAction extends BaseAction<C0316_SkillStudyReqMessage> {

	@Override
	public Message execute(ActionContext context, C0316_SkillStudyReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		short skillId = reqMsg.getSkillId();
		byte sourceType = reqMsg.getSourceType();
		SkillSourceType skillSourceType = SkillSourceType.get(sourceType);
		SkillLearnFunc learnFunc = GameContext.getUserSkillApp().getSkillLearnFunc(skillSourceType);
		if(null == learnFunc){
			return new C0003_TipNotifyMessage(this.getText(TextId.Sys_Param_Error));
		}
		String parameter = reqMsg.getParameter();
		Result result = learnFunc.learnSkill(role, skillId, parameter);
		if(result.isIgnore()){
			return null;
		}
		if(!result.isSuccess()){
			return new C0003_TipNotifyMessage(result.getInfo());
		}
		return new C0003_TipNotifyMessage(this.getText(TextId.Skill_Learn_Success));
	}
	
}