package com.game.draco.app.skill.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.skill.config.SkillSourceType;
import com.game.draco.app.skill.func.SkillLearnFunc;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C0315_SkillTreeShowReqMessage;
import com.game.draco.message.response.C0315_SkillTreeShowRespMessage;

public class SkillTreeShowAction extends BaseAction<C0315_SkillTreeShowReqMessage>{

	@Override
	public Message execute(ActionContext context, C0315_SkillTreeShowReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		byte sourceType = reqMsg.getSourceType();
		SkillSourceType skillSourceType = SkillSourceType.get(sourceType);
		SkillLearnFunc learnFunc = GameContext.getUserSkillApp().getSkillLearnFunc(skillSourceType);
		if(null == learnFunc){
			return new C0003_TipNotifyMessage(this.getText(TextId.Sys_Param_Error));
		}
		String parameter = reqMsg.getParameter();
		C0315_SkillTreeShowRespMessage respMsg = new C0315_SkillTreeShowRespMessage();
		respMsg.setSourceType(sourceType);
		respMsg.setParameter(parameter);
		respMsg.setSkillList(GameContext.getSkillApp().getSkillShowItemList(role, learnFunc, parameter));
		respMsg.setAstaff((byte)learnFunc.getAstaff(role, parameter));
		return respMsg;
	}
	
}
