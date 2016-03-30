package com.game.draco.app.goddess.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsGoddess;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.goddess.domain.RoleGoddess;
import com.game.draco.app.skill.config.SkillSourceType;
import com.game.draco.app.skill.func.SkillLearnFunc;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C1353_GoddessSkillInfoReqMessage;
import com.game.draco.message.response.C1353_GoddessSkillInfoRespMessage;

public class GoddessSkillInfoAction extends BaseAction<C1353_GoddessSkillInfoReqMessage> {

	@Override
	public Message execute(ActionContext context, C1353_GoddessSkillInfoReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		int goddessId = reqMsg.getId();
		GoodsGoddess goodsGoddess = GameContext.getGoodsApp().getGoodsTemplate(GoodsGoddess.class, goddessId);
		if(null == goodsGoddess) {
			return new C0003_TipNotifyMessage(this.getText(TextId.Sys_Param_Error));
		}
		RoleGoddess roleGoddess = GameContext.getUserGoddessApp().getRoleGoddess(role.getRoleId(), goddessId);
		if(null == roleGoddess) {
			return new C0003_TipNotifyMessage(this.getText(TextId.Goddess_had_no));
		}
		
		C1353_GoddessSkillInfoRespMessage respMsg = new C1353_GoddessSkillInfoRespMessage();
		SkillLearnFunc learnFunc = GameContext.getUserSkillApp().getSkillLearnFunc(SkillSourceType.Goddess);
		respMsg.setSkillItemList(GameContext.getSkillApp().getSkillShowItemList(role, learnFunc, String.valueOf(goddessId)));
		return respMsg;
	}

}
