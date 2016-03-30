package com.game.draco.app.store.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1602_NpcStoreBuyReqMessage;
import com.game.draco.message.response.C1602_NpcStoreBuyRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class NpcStoreBuyAction extends BaseAction<C1602_NpcStoreBuyReqMessage> {

	@Override
	public Message execute(ActionContext context, C1602_NpcStoreBuyReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		C1602_NpcStoreBuyRespMessage resp = new C1602_NpcStoreBuyRespMessage();
		int buyNum =  req.getBuyNum();
		Result result = GameContext.getNpcStoreApp().buy(role,
				req.getNpcTemplateId(), req.getType(),
				req.getGoodsTemplateId(),buyNum );
		if(result.isIgnore()){
			return null;
		}
		resp.setInfo(result.getInfo());
		return resp;
	}
}
