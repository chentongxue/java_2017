package com.game.draco.app.hint.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1170_HintListReqMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class HintListAction extends BaseAction<C1170_HintListReqMessage> {

	@Override
	public Message execute(ActionContext context, C1170_HintListReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null;
		}
		GameContext.getHintApp().pushHintListMessage(role);
		GameContext.getHintApp().pushHintTimeListMessage(role);
		// 客户端处理提示规则
		GameContext.getHintApp().pushHintRulesMessage(role);
		return null;
	}
	
}
