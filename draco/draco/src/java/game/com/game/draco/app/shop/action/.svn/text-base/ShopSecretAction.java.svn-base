package com.game.draco.app.shop.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1618_ShopSecretReqMessage;
import com.game.draco.message.response.C1618_ShopSecretRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class ShopSecretAction extends BaseAction<C1618_ShopSecretReqMessage>{

	@Override
	public Message execute(ActionContext context, C1618_ShopSecretReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(role == null){
			return null;
		}
		C1618_ShopSecretRespMessage resp = GameContext.getShopSecretApp().getShopSecretRespMessage(role);
		return resp;
	}
	
}
