package com.game.draco.app.exchange.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.exchange.trade.TradeFuctionConfig;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C1401_ExchangeListReqMessage;
import com.game.draco.message.request.C1408_TradeFunctionReqMessage;
import com.game.draco.message.request.C1618_ShopSecretOpenPanelReqMessage;
/**
 * 打开神秘商店或兑换面板
 */
public class TradeFuctionAction extends BaseAction<C1408_TradeFunctionReqMessage>{
	private static final byte EXCHANGE_TYPE = 1;                                                             
	private static final byte SHOP_SECRET_TYPE = 2;

	@Override
	public Message execute(ActionContext context, C1408_TradeFunctionReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){ 
			return null;
		}
		byte interId = req.getInterId();
		TradeFuctionConfig cf = GameContext.getExchangeApp().getTradeFuctionConfig(interId); 
		if(cf == null){
			logger.error("tradeFunctionAction execute  err:enterId =" + interId);
			return new C0003_TipNotifyMessage(getText(TextId.TRADE_FUNCTION_PARAM_ERR));
		}
		if(cf.getTradeType() == EXCHANGE_TYPE){
			C1401_ExchangeListReqMessage msg = new C1401_ExchangeListReqMessage();
			String param = cf.getParam();
			if(req.getEnterType() == 1){
				param = param + Cat.and + req.getEnterType();
			}
			msg.setParam(param);
			role.getBehavior().addCumulateEvent(msg);
			return null;
		}
		if(cf.getTradeType() == SHOP_SECRET_TYPE){
			C1618_ShopSecretOpenPanelReqMessage msg2 = new C1618_ShopSecretOpenPanelReqMessage();
			msg2.setShopId(cf.getParam());
			role.getBehavior().addCumulateEvent(msg2);
			return null;
		}
		logger.error("tradeFunctionAction execute  err:enterId =" + interId);
		return new C0003_TipNotifyMessage(getText(TextId.TRADE_FUNCTION_PARAM_ERR));
	}
}