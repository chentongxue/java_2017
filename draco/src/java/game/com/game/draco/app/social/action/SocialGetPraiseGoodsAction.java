package com.game.draco.app.social.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1217_SocialPraiseRecvReqMessage;
import com.game.draco.message.response.C1217_SocialPraiseRecvRespMessage;

public class SocialGetPraiseGoodsAction extends BaseAction<C1217_SocialPraiseRecvReqMessage> {

	@Override
	public Message execute(ActionContext context, C1217_SocialPraiseRecvReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		C1217_SocialPraiseRecvRespMessage resp = new C1217_SocialPraiseRecvRespMessage();
		Result result = GameContext.getSocialApp().getPraiseGoods(role);
		resp.setInfo(result.getInfo());
		resp.setStatus(result.getResult());
		return resp;
	}

}
