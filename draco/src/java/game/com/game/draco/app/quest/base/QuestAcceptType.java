package com.game.draco.app.quest.base;

public enum QuestAcceptType {
	
	Npc((byte)0,"NPC",true),
	Poker((byte)1,"诈金花",false),
	
	;
	
	private final byte type;
	private final String name;
	/**
	 * 交接任务的时候是否判断距离
	 */
	private final boolean distance ;
	
	private QuestAcceptType(byte type, String name,boolean distance) {
		this.type = type;
		this.name = name;
		this.distance = distance ;
	}

	public byte getType() {
		return type;
	}

	public String getName() {
		return name;
	}
	
	
	public boolean isDistance() {
		return distance;
	}

	public static QuestAcceptType get(byte type){
		for(QuestAcceptType item : QuestAcceptType.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}
	
}
