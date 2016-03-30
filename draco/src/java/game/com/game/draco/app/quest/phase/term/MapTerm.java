package com.game.draco.app.quest.phase.term;

import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.quest.Quest;
import com.game.draco.app.quest.QuestTerm;
import com.game.draco.app.quest.base.QuestTermType;
import com.game.draco.message.item.QuestTermMapItem;

public class MapTerm extends QuestTerm<QuestTermMapItem> {
	
	public MapTerm(QuestTermType questTermType, int count, String mapId){
		super.setBaseValue(questTermType, count, mapId);
		this.mapId = mapId;
		this.initMapName();
	}

	@Override
	public QuestTermMapItem getQuestTermItem(RoleInstance role, Quest quest, boolean canSubmit, int index) {
		QuestTermMapItem item = new QuestTermMapItem();
		this.setBaseItemValue(role, quest, canSubmit, index, item);
		item.setMapId(this.mapId);
		item.setMapName(this.mapName);
		return item;
	}
	
}
