package com.game.draco.app.shop.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1619_ShopSecretRefreshReqMessage;
import com.game.draco.message.response.C1619_ShopSecretRefreshRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class ShopSecretRefreshAction extends BaseAction<C1619_ShopSecretRefreshReqMessage>{

	@Override
	public Message execute(ActionContext context, C1619_ShopSecretRefreshReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(role == null){
			return null;
		}
		C1619_ShopSecretRefreshRespMessage resp = new C1619_ShopSecretRefreshRespMessage();
		Result result = GameContext.getShopSecretApp().roleRefresh(role);
		if(!result.isSuccess()) {
			resp.setInfo(result.getInfo());
			return resp;
		}
		resp.setType(result.getResult());
		return resp;
	}
}
