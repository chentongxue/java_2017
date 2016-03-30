package com.game.draco.app.union.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1701_UnionCreateReqMessage;
import com.game.draco.message.response.C1701_UnionCreateRespMessage;

/**
 * 创建公会
 * @author mofun030602
 *
 */
public class UnionCreateAction extends BaseAction<C1701_UnionCreateReqMessage> {

	@Override
	public Message execute(ActionContext context, C1701_UnionCreateReqMessage reqMsg) {
		C1701_UnionCreateRespMessage resp = new C1701_UnionCreateRespMessage();
		try {
			RoleInstance role = this.getCurrentRole(context);
			Result result = GameContext.getUnionApp().createUnion(role, reqMsg.getUnionName(), reqMsg.getUnionDesc());
			if(!result.isSuccess()){
				resp.setType((byte) 0);
				resp.setInfo(result.getInfo());
				return resp;
			}
			resp.setType((byte) 1);
			resp.setInfo(this.getText(TextId.UNION_CREATE_SUCCESS));
			return resp;
		} catch (ServiceException e) {
			this.logger.error("UnionCreateAction", e);
			resp.setInfo(this.getText(TextId.UNION_CREATE_FAIL));
			return resp;
		}
	}

}
