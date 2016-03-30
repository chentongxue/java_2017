package com.game.draco.app.shopsecret.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1618_ShopSecretOpenPanelReqMessage;

public class ShopSecretOpenPanelAction extends BaseAction<C1618_ShopSecretOpenPanelReqMessage>{

	@Override
	public Message execute(ActionContext context, C1618_ShopSecretOpenPanelReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(role == null){
			return null;
		}
		return GameContext.getShopSecretApp().openShopSecretEnterRespMessage(role,req.getShopId());
	}
}
