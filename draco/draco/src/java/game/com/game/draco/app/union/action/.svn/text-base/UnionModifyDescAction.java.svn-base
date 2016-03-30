package com.game.draco.app.union.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1717_UnionModifyDescReqMessage;
import com.game.draco.message.response.C1717_FactionModifyDescRespMessage;

/**
 * 修改公会描述
 * @author mofun030602
 *
 */
public class UnionModifyDescAction extends BaseAction<C1717_UnionModifyDescReqMessage> {

	@Override
	public Message execute(ActionContext context, C1717_UnionModifyDescReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		Result result = GameContext.getUnionApp().modifyUnionDesc(role, reqMsg.getUnionDesc());
		C1717_FactionModifyDescRespMessage resp = new C1717_FactionModifyDescRespMessage();
		if(!result.isSuccess()){
			resp.setType((byte) 0);
			resp.setInfo(result.getInfo());
			return resp;
		}
		resp.setType((byte) 1);
		resp.setInfo(this.getText(TextId.SYSTEM_SUCCESS));
		return resp;
	}


}
