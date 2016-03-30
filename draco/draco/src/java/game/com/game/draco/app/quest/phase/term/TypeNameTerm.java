package com.game.draco.app.quest.phase.term;

import com.game.draco.app.quest.Quest;
import com.game.draco.app.quest.QuestTerm;
import com.game.draco.app.quest.base.QuestTermType;
import com.game.draco.message.item.QuestTermEventTypeNameItem;

import lombok.Data;
import sacred.alliance.magic.vo.RoleInstance;

public @Data class TypeNameTerm extends QuestTerm<QuestTermEventTypeNameItem> {
	
	private int type;//类型/ID
	private String name;//名称
	
	public TypeNameTerm(QuestTermType questTermType, int count, int type, String name){
		super.setBaseValue(questTermType, count, String.valueOf(type));
		this.type = type;
		this.name = name;
	}

	@Override
	public QuestTermEventTypeNameItem getQuestTermItem(RoleInstance role, Quest quest, boolean canSubmit, int index) {
		QuestTermEventTypeNameItem item = new QuestTermEventTypeNameItem();
		this.setBaseItemValue(role, quest, canSubmit, index, item);
		item.setType(this.type);
		item.setName(this.name);
		return item;
	}
	
}
