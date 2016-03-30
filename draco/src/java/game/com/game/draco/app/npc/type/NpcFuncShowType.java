package com.game.draco.app.npc.type;

public enum NpcFuncShowType {
	
	Default((byte)0,"默认"), 
	Popup((byte)1,"弹板"),
	Confirm((byte)2,"二次确定"),
	Select((byte)3,"选中"),
	KeepParents((byte)4,"保持父窗口"),
	;
	
	private final byte type;
	private final String name;
	
	private NpcFuncShowType(byte type, String name) {
		this.type = type;
		this.name = name;
	}

	public byte getType() {
		return type;
	}

	public String getName() {
		return name;
	}
	
}
