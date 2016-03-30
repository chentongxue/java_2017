package com.game.draco.app.npc.inspire;

import sacred.alliance.magic.base.AttributeType;


public enum NpcInspireType {
	
	GameMoney((byte)0, "游戏币", AttributeType.gameMoney),
	GoldMoney((byte)1, "元宝", AttributeType.goldMoney),
	Zp((byte)3, "真气", AttributeType.potential),
		
	;
	
	private final byte type;
	private final String name;
	private final AttributeType attrType;
	
	NpcInspireType(byte type, String name, AttributeType attrType){
		this.type = type;
		this.name = name;
		this.attrType = attrType;
	}
	
	public byte getType(){
		return type;
	}
	
    public String getName() {
		return name;
	}
    
	public AttributeType getAttrType() {
		return attrType;
	}

	public static NpcInspireType get(byte type){
		for(NpcInspireType item : NpcInspireType.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
    }
	
}
