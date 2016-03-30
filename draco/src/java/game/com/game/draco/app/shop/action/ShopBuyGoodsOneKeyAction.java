package com.game.draco.app.shop.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2112_ShopBuyGoodsOneKeyReqMessage;
import com.game.draco.message.response.C2102_ShopBuyGoodsRespMessage;

public class ShopBuyGoodsOneKeyAction extends BaseAction<C2112_ShopBuyGoodsOneKeyReqMessage> {

	@Override
	public Message execute(ActionContext context, C2112_ShopBuyGoodsOneKeyReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(role == null){
			return null;
		}

		Result result = GameContext.getShopApp().shopping(role,reqMsg.getMoneyType(), reqMsg.getGoodsId(), reqMsg.getNumber(), true, (byte)1);
		if(result.isIgnore()){
			return null;
		}
		C2102_ShopBuyGoodsRespMessage resp = new C2102_ShopBuyGoodsRespMessage();
		resp.setType(result.getResult());
		resp.setInfo(result.getInfo());
		return resp;
	}

}
