package com.game.draco.app.union.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1715_UnionDemiseReqMessage;
import com.game.draco.message.response.C1715_UnionDemiseRespMessage;

/**
 * 会长禅让
 * @author mofun030602
 *
 */
public class UnionDemiseAction extends BaseAction<C1715_UnionDemiseReqMessage> {

	@Override
	public Message execute(ActionContext context, C1715_UnionDemiseReqMessage reqMsg) {
		C1715_UnionDemiseRespMessage resp = new C1715_UnionDemiseRespMessage();
		resp.setType((byte) 0);
		try {
			RoleInstance leader = this.getCurrentRole(context);
			Result result = GameContext.getUnionApp().demisePresident(leader, reqMsg.getRoleId());
			if(!result.isSuccess()){
				resp.setInfo(result.getInfo());
				return resp;
			}
			resp.setType((byte) 1);
			resp.setInfo(this.getText(TextId.SYSTEM_SUCCESS));
			return resp;
		} catch (ServiceException e) {
			this.logger.error("UnionDemiseAction", e);
			resp.setInfo(this.getText(TextId.SYSTEM_ERROR));
			return resp;
		}
	}

}
