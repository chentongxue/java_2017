package com.game.draco.app.shop.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2105_ShopEnterReqMessage;
/**
 * 打开商城入口
 */
public class ShopEnterAction extends BaseAction<C2105_ShopEnterReqMessage> {

	@Override
	public Message execute(ActionContext context, C2105_ShopEnterReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(role == null){
			return null;
		}
		return GameContext.getShopApp().openShop(role);
	}
}
