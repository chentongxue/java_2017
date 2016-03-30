package com.game.draco.app.union.domain.auction;

import lombok.Data;
import sacred.alliance.magic.util.Util;

public @Data class UnionAuction implements java.io.Serializable{
	
	private static final long serialVersionUID = 1L;
	
	public final static String ID = "id" ;
	
	//id
	private String id;

	//公会ID
	private String unionId;

	//数据
	private byte [] data;
	
	/**
	 * 读取某个公会拍卖行数据
	 * @param data
	 */
	public Auction parseAuctionData() {
		return Util.decode(this.data, Auction.class) ;
	}

	/**
	 * 存储公会拍卖数据
	 * @param roleDpsMap
	 * @return
	 */
	public void buildAuctionData(Auction auction) {
		this.data = Util.encode(auction);
	}

}
