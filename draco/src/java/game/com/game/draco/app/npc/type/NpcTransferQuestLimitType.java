package com.game.draco.app.npc.type;

public enum NpcTransferQuestLimitType {
	
	NotAccept((byte)0,"没有接过的任务"),
	NotComplete((byte)1,"正在做的任务"),
	Completed((byte)2,"曾经完成过的任务");
	
	private byte type;
	private String name;
	
	private NpcTransferQuestLimitType(byte type, String name) {
		this.type = type;
		this.name = name;
	}

	public byte getType() {
		return type;
	}

	public String getName() {
		return name;
	}
	
	public static NpcTransferQuestLimitType get(byte type){
		for(NpcTransferQuestLimitType actType:NpcTransferQuestLimitType.values()){
			if(actType.getType() == type){
				return actType;
			}
		}
		return null;
	}
}
