package com.game.draco.app.exchange.action;

import java.util.List;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.exchange.domain.ExchangeMenu;
import com.game.draco.message.item.ExchangeChildItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C1401_ExchangeListReqMessage;
import com.game.draco.message.response.C1401_ExchangeListRespMessage;
/**
 * 兑换
 */
public class ExchangeListAction extends BaseAction<C1401_ExchangeListReqMessage>{

	@Override
	public Message execute(ActionContext context, C1401_ExchangeListReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null;
		}
		String[] params = req.getParam().split(Cat.and);//&
		String menuIdStr = params[0];
		if(!Util.isNumber(menuIdStr)){
			return new C0003_TipNotifyMessage(Status.Exchange_Param_ERR.getTips());
		}
		int menuId = Integer.parseInt(menuIdStr); 
		ExchangeMenu exchangeMenu = GameContext.getExchangeApp().getAllMenuMap().get(menuId);
		if(null == exchangeMenu) {
			logger.error("exchange err:ExchangeAction.execute() failed exchangeMenu is null");
			return new C0003_TipNotifyMessage(Status.Exchange_Param_ERR.getTips());
		}
		//不区分数值兑换和
		C1401_ExchangeListRespMessage resp = new C1401_ExchangeListRespMessage();
		List<ExchangeChildItem> childList = GameContext.getExchangeApp().getChildList(role, menuId);
		resp.setName(exchangeMenu.getName());
		resp.setChildList(childList);
		
		if(params.length > 1){
			if(Util.isNumber(params[1])){
				resp.setEnterType(Byte.parseByte(params[1]));
			}
		}
		return resp;
	}
}