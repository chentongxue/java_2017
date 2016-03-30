package com.game.draco.app.exchange.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.exchange.domain.ExchangeItem;
import com.game.draco.message.request.C1402_ExchangeExeReqMessage;
import com.game.draco.message.response.C1402_ExchangeExeRespMessage;

public class ExchangeExeAction extends BaseAction<C1402_ExchangeExeReqMessage> {

	@Override
	public Message execute(ActionContext context, C1402_ExchangeExeReqMessage req) {
		C1402_ExchangeExeRespMessage resp = new C1402_ExchangeExeRespMessage();
		RoleInstance role = this.getCurrentRole(context);
		if(null == role)
			return null;
		ExchangeItem exchangeItem = (ExchangeItem) GameContext.getExchangeApp().getAllItemMap().get(req.getId());
		if(null == exchangeItem)
			return null;
		Status status = GameContext.getExchangeApp().canExchange(role, exchangeItem);
		if(!status.isSuccess()){
			resp.setType((byte)0);
			resp.setInfo(status.getTips());
			return resp;
		}
		//扣除消耗和添加物品
		GoodsResult goodsResult = GameContext.getExchangeApp().exchange(role, exchangeItem);
		if(!goodsResult.isSuccess()){
			resp.setType((byte)0);
			resp.setInfo(goodsResult.getInfo());
			return resp;
		}
		//兑换成功
		resp.setType((byte)1);
		resp.setInfo(Status.Exchange_Success.getTips());
		resp.setExchangeId(exchangeItem.getId());
		//修个role身上数据
		exchangeItem.updateDbInfo(role);
		return resp;
	}

	
}
