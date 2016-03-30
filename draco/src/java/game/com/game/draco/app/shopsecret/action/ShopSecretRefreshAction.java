package com.game.draco.app.shopsecret.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1619_ShopSecretRefreshReqMessage;
import com.game.draco.message.response.C1619_ShopSecretRefreshRespMessage;

public class ShopSecretRefreshAction extends BaseAction<C1619_ShopSecretRefreshReqMessage>{

	@Override
	public Message execute(ActionContext context, C1619_ShopSecretRefreshReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(role == null){
			return null;
		}
		if(Util.isEmpty(req.getParam())){
			return null;
		}
		String [] param = req.getParam().split(",");
		byte confirm = 0;
		if(param.length>1){
			confirm = 1;
		}
		
		String shopId = param[0];
		C1619_ShopSecretRefreshRespMessage resp = new C1619_ShopSecretRefreshRespMessage();
		Result result = GameContext.getShopSecretApp().refreshRoleShopSecret(role,shopId,confirm);
		if(result.isIgnore()){
			return null;
		}
		if(!result.isSuccess()) {
			resp.setInfo(result.getInfo());
			return resp;
		}
		resp.setType(result.getResult());
		return resp;
	}
}
