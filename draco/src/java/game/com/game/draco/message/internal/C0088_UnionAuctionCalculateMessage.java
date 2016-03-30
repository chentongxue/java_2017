package com.game.draco.message.internal;

import lombok.Data;

public @Data class C0088_UnionAuctionCalculateMessage extends InternalMessage{

	public C0088_UnionAuctionCalculateMessage(){
		this.commandId = 88 ;
	}
	
	private String auctionId;
	private String unionId ;
	
}
