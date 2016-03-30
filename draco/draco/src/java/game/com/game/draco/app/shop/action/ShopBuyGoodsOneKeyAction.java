package com.game.draco.app.shop.action;

import com.game.draco.GameContext;
import com.game.draco.app.shop.type.ShopMoneyType;
import com.game.draco.message.request.C2112_ShopBuyGoodsOneKeyReqMessage;
import com.game.draco.message.response.C2102_ShopBuyGoodsRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class ShopBuyGoodsOneKeyAction extends BaseAction<C2112_ShopBuyGoodsOneKeyReqMessage> {

	@Override
	public Message execute(ActionContext context, C2112_ShopBuyGoodsOneKeyReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		ShopMoneyType moneyType = ShopMoneyType.get(reqMsg.getMoneyType());
		Result result = GameContext.getShopApp().shopping(role, reqMsg.getGoodsId(), reqMsg.getNumber(), moneyType, true);
		C2102_ShopBuyGoodsRespMessage resp = new C2102_ShopBuyGoodsRespMessage();
		resp.setType(result.getResult());
		resp.setInfo(result.getInfo());
		return resp;
	}

}
