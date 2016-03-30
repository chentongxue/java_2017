package com.game.draco.app.exchange.action;

import java.util.List;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.ExchangeType;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.exchange.domain.ExchangeMenu;
import com.game.draco.message.item.ExchangeChildGoodsItem;
import com.game.draco.message.item.ExchangeChildItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C1403_ExchangeDetailReqMessage;
import com.game.draco.message.response.C1401_ExchangeNumericDetailRespMessage;
import com.game.draco.message.response.C1403_ExchangeGoodsDetailRespMessage;

public class ExchangeAction extends BaseAction<C1403_ExchangeDetailReqMessage>{

	@Override
	public Message execute(ActionContext context, C1403_ExchangeDetailReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null;
		}
		
		String menuIdStr = req.getParam();
		int menuId = -1;
		try {
			menuId = Integer.parseInt(menuIdStr);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("login game, query or save vip info failed", e);
			return new C0003_TipNotifyMessage(Status.Exchange_Param_ERR.getTips());
		}
		
		
		ExchangeMenu exchangeMenu = (ExchangeMenu) GameContext.getExchangeApp().getAllMenuMap().get(menuId);
		//用数值来兑换
		if(exchangeMenu.getExchangeType() == ExchangeType.numerical) {
			C1401_ExchangeNumericDetailRespMessage resp = new C1401_ExchangeNumericDetailRespMessage();
			List<ExchangeChildItem> childList = GameContext.getExchangeApp().getChildList(role, menuId);
			if(null == childList){
				return null;
			}
			resp.setName(exchangeMenu.getName());
			resp.setChildList(childList);
			return resp;
		}
		//用物品来兑换
		C1403_ExchangeGoodsDetailRespMessage resp = new C1403_ExchangeGoodsDetailRespMessage();
		List<ExchangeChildGoodsItem> childList = GameContext.getExchangeApp().getGoodsChildList(role, menuId);
		if(null == childList){
			return null;
		}
		resp.setName(exchangeMenu.getName());
		resp.setChildList(childList);
		return resp;
	}
}