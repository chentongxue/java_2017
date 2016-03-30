package com.game.draco.app.quest.base;

import com.game.draco.GameContext;

import sacred.alliance.magic.constant.TextId;

public enum QuestType {

	MainLine(0,"主线任务",true, TextId.QuestType_MainLine_Name),
	Daily(1,"日常任务",true, TextId.QuestType_Daily_Name),
	Active(2,"活动任务",false, TextId.QuestType_Active_Name),
	Repeat(3,"随机任务",false, TextId.QuestType_Repeat_Name),
	
	;
	
	private final int type;
	private final String name;//在任务列表中显示的分类名称
	private final boolean putNpc;//是否要加载到NPC身上
	private final String i18nKey ; //文本key
	
	QuestType(int type, String name, boolean putNpc, String i18nKey){
		this.type = type;
		this.name = name;
		this.putNpc = putNpc;
		this.i18nKey = i18nKey ;
	}
	
	public int getType(){
		return type;
	}
	
	public String getName() {
		return GameContext.getI18n().getText(this.i18nKey);
	}
	
	public static QuestType get(int type){
		for(QuestType item : QuestType.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}

	public boolean isPutNpc() {
		return putNpc;
	}

}
