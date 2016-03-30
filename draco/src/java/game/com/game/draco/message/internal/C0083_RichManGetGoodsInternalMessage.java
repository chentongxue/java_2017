package com.game.draco.message.internal;

import lombok.Data;

public @Data class C0083_RichManGetGoodsInternalMessage extends InternalMessage {
	public C0083_RichManGetGoodsInternalMessage() {
		this.commandId = 83;
	}
	private int roleId ;
	private int goodsId;
}
