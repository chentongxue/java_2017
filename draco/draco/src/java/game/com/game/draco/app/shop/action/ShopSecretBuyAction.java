package com.game.draco.app.shop.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1620_ShopSecretBuyReqMessage;
import com.game.draco.message.response.C1620_ShopSecretBuyRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class ShopSecretBuyAction extends BaseAction<C1620_ShopSecretBuyReqMessage>{

	@Override
	public Message execute(ActionContext context, C1620_ShopSecretBuyReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(role == null){
			return null;
		}
		C1620_ShopSecretBuyRespMessage resp = new C1620_ShopSecretBuyRespMessage();
		Result result = GameContext.getShopSecretApp().buy(role, req.getId());
		if(!result.isSuccess()) {
			resp.setInfo(result.getInfo());
			return resp;
		}
		resp.setType(result.getResult());
		resp.setId(req.getId());
		return resp;
	}
}
