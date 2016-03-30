package com.game.draco.app.quest.phase.term;

import com.game.draco.app.quest.Quest;
import com.game.draco.app.quest.QuestTerm;
import com.game.draco.app.quest.base.QuestTermType;
import com.game.draco.message.item.QuestTermCountParamItem;
import com.game.draco.message.item.QuestTermItem;

import sacred.alliance.magic.vo.RoleInstance;

public class CountParamTerm extends QuestTerm<QuestTermItem> {
	
	private String nameParam;//名称参数
	
	public CountParamTerm(QuestTermType questTermType, int count, String nameParam) {
		super.setBaseValue(questTermType, count, null);
		this.nameParam = nameParam;
	}

	@Override
	public QuestTermCountParamItem getQuestTermItem(RoleInstance role, Quest quest, boolean canSubmit, int index) {
		QuestTermCountParamItem item = new QuestTermCountParamItem();
		this.setBaseItemValue(role, quest, canSubmit, index, item);
		item.setParameter(this.nameParam);
		return item;
	}
	
}
