package com.game.draco.app.exchange.trade;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

@Data public class TradeFuctionConfig implements KeySupport<Byte>{
	private byte interId;
	private byte tradeType;
	private String param;
	@Override
	public Byte getKey() {
		return interId;
	}

}
