package com.game.draco.app.dailyplay;

public enum DailyPlayStatus {

	/**
	 * -1 不显示(服务器专用)
	 * 0 已经领取
	 * 1 尚未完成
	 * 2 已经完成未领取
	 */
	
	canot_show((byte)-1),
	has_received((byte)0),
	un_finished((byte)1),
	can_receive((byte)2),
	;
	
	private final byte type;
	
	DailyPlayStatus(byte type){
		this.type = type ;
	}

	public byte getType() {
		return type;
	}
	
}
