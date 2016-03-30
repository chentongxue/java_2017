package com.game.draco.app.shop.action;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.message.item.ShopTimeItem;
import com.game.draco.message.request.C2103_ShopTimeListReqMessage;
import com.game.draco.message.response.C2103_ShopTimeListRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;

public class ShopTimeListAction extends BaseAction<C2103_ShopTimeListReqMessage>{

	@Override
	public Message execute(ActionContext context, C2103_ShopTimeListReqMessage req) {
		C2103_ShopTimeListRespMessage resp = new C2103_ShopTimeListRespMessage();
		List<ShopTimeItem> shopList = new ArrayList<ShopTimeItem>();
		shopList.addAll(GameContext.getShopTimeApp().getSellGoodsList());
		resp.setShopList(shopList);
		resp.setOverTime(GameContext.getShopTimeApp().getOverTime());
		return resp;
	}
	
}
