package com.game.draco.app.union.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2753_UnionInfoReqMessage;
import com.game.draco.message.response.C2753_UnionInfoRespMessage;

/**
 * 公会信息
 * @author mofun030602
 *
 */
public class UnionInfoAction extends BaseAction<C2753_UnionInfoReqMessage> {

	@Override
	public Message execute(ActionContext context, C2753_UnionInfoReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		C2753_UnionInfoRespMessage respMsg = GameContext.getUnionApp().sendC2753_UnionInfoRespMessage(role);
		return respMsg;
	}

}
