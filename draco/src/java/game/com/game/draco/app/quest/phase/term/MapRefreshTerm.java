package com.game.draco.app.quest.phase.term;

import com.game.draco.app.quest.Quest;
import com.game.draco.app.quest.QuestTerm;
import com.game.draco.app.quest.base.QuestTermType;
import com.game.draco.message.item.QuestTermMapRefreshlItem;

import lombok.Data;
import sacred.alliance.magic.vo.RoleInstance;

public @Data class MapRefreshTerm extends QuestTerm<QuestTermMapRefreshlItem> {
	
	private short refreshIndex;//刷怪波次
	
	public MapRefreshTerm(QuestTermType questTermType, int count, String mapId, short refreshIndex){
		super.setBaseValue(questTermType, count, null);
		this.mapId = mapId;
		this.initMapName();
		this.refreshIndex = refreshIndex;
	}

	@Override
	public QuestTermMapRefreshlItem getQuestTermItem(RoleInstance role, Quest quest, boolean canSubmit, int index) {
		QuestTermMapRefreshlItem item = new QuestTermMapRefreshlItem();
		this.setBaseItemValue(role, quest, canSubmit, index, item);
		item.setMapId(this.mapId);
		item.setMapName(this.mapName);
		item.setIndex(this.refreshIndex);
		return item;
	}
	
}
