package com.game.draco.app.shop.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2101_ShopGoodsListReqMessage;

/**
 * 普通商店
 */
public class ShopGoodsListAction extends
		BaseAction<C2101_ShopGoodsListReqMessage> {

	@Override
	public Message execute(ActionContext context,
			C2101_ShopGoodsListReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(role == null){
			return null;
		}
		return GameContext.getShopApp().getGoodsList(role);
	}
}
