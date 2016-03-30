package com.game.draco.app.shopsecret.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1620_ShopSecretBuyReqMessage;
import com.game.draco.message.response.C1620_ShopSecretBuyRespMessage;

public class ShopSecretBuyAction extends BaseAction<C1620_ShopSecretBuyReqMessage>{

	@Override
	public Message execute(ActionContext context, C1620_ShopSecretBuyReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(role == null){
			return null;
		}
		C1620_ShopSecretBuyRespMessage resp = new C1620_ShopSecretBuyRespMessage();
		String shopId = req.getShopId();
		int id = req.getId();
		Result result = GameContext.getShopSecretApp().buy(role, shopId, id);
		if(result.isIgnore()){
			return null;
		}
		if(!result.isSuccess()) {
			resp.setInfo(result.getInfo());
			return resp;
		}
		resp.setType(result.getResult());
		resp.setId(req.getId());
		return resp;
	}
}
