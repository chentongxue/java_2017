package com.game.draco.app.recovery.type;

import sacred.alliance.magic.base.AttributeType;

public enum RecoveryConsumeType {
//	NONE_RECOVERY_CONSUME_TYPE((byte)0, ""),
	RECOVERY_CONSUME_FREE((byte)1, "免费"),
	RECOVERY_CONSUME_SILVERMONEY((byte)2, "金币"),
	RECOVERY_CONSUME_DIAMODNS((byte)3, "钻石"),
	;
	private final byte type;
	private final String name;
	
	private RecoveryConsumeType(byte type, String name){
		this.type = type;
		this.name = name;
	}

	public byte getType() {
		return type;
	}

	public String getName() {
		return name;
	}
	public static RecoveryConsumeType getType(byte type) {
		for(RecoveryConsumeType tp : RecoveryConsumeType.values()){
			if(tp.getType() == type){
				return tp;
			}
		}
		return null ;
	}
	//获得消耗的属性ENUM
	public AttributeType getAttributeType(){
		switch (this) {
		case RECOVERY_CONSUME_SILVERMONEY:
			return AttributeType.gameMoney;
		case RECOVERY_CONSUME_DIAMODNS:
			return AttributeType.goldMoney;
		default:
			return null;
		}
	}
}
