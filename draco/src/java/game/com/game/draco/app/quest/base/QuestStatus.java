package com.game.draco.app.quest.base;

public enum QuestStatus {
	
    noneTask(0, "没有任务"),
	canAccept(1, "可接"),//可接(当前未接)
	canSubmit(2, "完成未提交"),
	notComplete(3, "未完成"),//未完成(已接不可提交)
	failure(4, "失败"),
	success(5, "成功提交"),
	
	;
	
	private final int type;
	private final String name;
	
	QuestStatus(int type, String name){
		this.type = type;
		this.name = name;
	}
	
	public int getType(){
		return type;
	}
	
    public String getName() {
		return name;
	}
    
	public static QuestStatus get(int type){
		for(QuestStatus item : QuestStatus.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
    }
}
