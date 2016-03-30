package com.game.draco.app.shop.action;

import com.game.draco.GameContext;
import com.game.draco.app.shop.type.ShopMoneyType;
import com.game.draco.message.request.C2102_ShopBuyGoodsReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C2102_ShopBuyGoodsRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.vo.RoleInstance;

public class ShopBuyGoodsAction extends BaseAction<C2102_ShopBuyGoodsReqMessage> {

	@Override
	public Message execute(ActionContext context, C2102_ShopBuyGoodsReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		int openLevel = GameContext.getParasConfig().getShopOpenRoleLevel();
		if(openLevel > 0 && role.getLevel() < openLevel){
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(), Status.Shop_Level_Not_Open.getTips().replace(Wildcard.Number, String.valueOf(openLevel)));
		}
		ShopMoneyType moneyType = ShopMoneyType.get(reqMsg.getMoneyType());
		Result result = GameContext.getShopApp().shopping(role, reqMsg.getGoodsId(), reqMsg.getNumber(), moneyType, false);
		C2102_ShopBuyGoodsRespMessage resp = new C2102_ShopBuyGoodsRespMessage();
		resp.setType(result.getResult());
		resp.setInfo(result.getInfo());
		return resp;
	}

}
