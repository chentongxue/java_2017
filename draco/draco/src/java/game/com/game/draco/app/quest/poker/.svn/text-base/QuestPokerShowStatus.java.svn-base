package com.game.draco.app.quest.poker;

public enum QuestPokerShowStatus {
	
	Not_Open((byte)0,"未开启"),
	Finished((byte)1,"已完成"),
	Can_Accept((byte)2,"可接取"),
	
	;
	
	private final byte type;
	private final String name;

	QuestPokerShowStatus(byte type, String name){
		this.type = type;
		this.name = name;
	}
	
	public byte getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}

	public static QuestPokerShowStatus get(byte type){
		for(QuestPokerShowStatus item : QuestPokerShowStatus.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}
}
