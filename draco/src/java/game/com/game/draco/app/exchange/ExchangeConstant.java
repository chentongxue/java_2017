package com.game.draco.app.exchange;

import com.game.draco.message.request.C1401_ExchangeListReqMessage;

public interface ExchangeConstant {
	public final static short EXCHANGE_NPC_ITEM_CMD = new C1401_ExchangeListReqMessage().getCommandId();//1404
}
