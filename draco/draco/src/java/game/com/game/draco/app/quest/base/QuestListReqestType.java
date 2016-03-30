package com.game.draco.app.quest.base;

public enum QuestListReqestType {

	Received((byte)0,"已接任务列表"),
	CanAccept((byte)1,"可接任务列表"),
	Auto((byte)2,"已接或可接"),//优先已经，若为空，再可接
	
	;
	
	private final byte type;
	private final String name;
	
	QuestListReqestType(byte type, String name){
		this.type = type;
		this.name = name;
	}
	
	public byte getType(){
		return type;
	}
	
	public String getName() {
		return name;
	}
	
	public static QuestListReqestType get(byte type){
		for(QuestListReqestType item : QuestListReqestType.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}
	
}
