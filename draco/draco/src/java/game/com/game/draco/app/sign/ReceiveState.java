package com.game.draco.app.sign;



public enum ReceiveState {
	already_receive((byte)0),
	can_receive((byte)1),
	canot_receive((byte)2);
	
	private byte type;
	
	ReceiveState(byte type){
		this.type = type;
	}

	public byte getType() {
		return type;
	}
	public void setType(byte type) {
		this.type = type;
	}
}
