package com.game.draco.app.exchange;

import com.game.draco.message.request.C1403_ExchangeDetailReqMessage;


public interface ExchangeConstant {
	public final static short EXCHANGE_NPC_ITEM_CMD = new C1403_ExchangeDetailReqMessage().getCommandId();//1404
}
