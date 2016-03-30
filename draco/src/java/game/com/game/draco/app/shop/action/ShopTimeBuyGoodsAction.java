package com.game.draco.app.shop.action;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.message.item.ShopTimeItem;
import com.game.draco.message.request.C2104_ShopTimeBuyGoodsReqMessage;
import com.game.draco.message.response.C2104_ShopTimeBuyGoodsRespMessage;
import com.game.draco.message.response.C2103_ShopTimeListRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;
/**
 * 限时抢购
 */
public class ShopTimeBuyGoodsAction extends BaseAction<C2104_ShopTimeBuyGoodsReqMessage>{

	@Override
	public Message execute(ActionContext context, C2104_ShopTimeBuyGoodsReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(role == null){
			return null;
		}
		Result result = GameContext.getShopTimeApp().shopping(role, req.getGoodsId(), req.getNum());
		if(result.isIgnore()){
			return null;
		}
		C2104_ShopTimeBuyGoodsRespMessage resp = new C2104_ShopTimeBuyGoodsRespMessage();
		resp.setType(result.getResult());
		resp.setInfo(result.getInfo());
		
		C2103_ShopTimeListRespMessage updateShopMsg = new C2103_ShopTimeListRespMessage();
		updateShopMsg.setOverTime(GameContext.getShopTimeApp().getOverTime());
		List<ShopTimeItem> shopList = new ArrayList<ShopTimeItem>();
		shopList.addAll(GameContext.getShopTimeApp().getSellGoodsList());
		updateShopMsg.setShopList(shopList);
		role.getBehavior().sendMessage(updateShopMsg);
		
		return resp;
	}
	
}
