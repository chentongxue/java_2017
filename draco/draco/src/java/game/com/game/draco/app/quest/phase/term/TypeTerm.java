package com.game.draco.app.quest.phase.term;

import com.game.draco.app.quest.Quest;
import com.game.draco.app.quest.QuestTerm;
import com.game.draco.app.quest.base.QuestTermType;
import com.game.draco.message.item.QuestTermEventTypeItem;

import lombok.Data;
import sacred.alliance.magic.vo.RoleInstance;

public @Data class TypeTerm extends QuestTerm<QuestTermEventTypeItem> {
	
	private byte type;//属性类型
	
	public TypeTerm(QuestTermType questTermType, int count, byte type){
		super.setBaseValue(questTermType, count, String.valueOf(type));
		this.type = type;
	}

	@Override
	public QuestTermEventTypeItem getQuestTermItem(RoleInstance role, Quest quest, boolean canSubmit, int index) {
		QuestTermEventTypeItem item = new QuestTermEventTypeItem();
		this.setBaseItemValue(role, quest, canSubmit, index, item);
		item.setType(this.type);
		return item;
	}
	
}
