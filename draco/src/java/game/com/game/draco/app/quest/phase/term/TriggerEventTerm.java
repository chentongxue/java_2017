package com.game.draco.app.quest.phase.term;

import com.game.draco.app.quest.Quest;
import com.game.draco.app.quest.QuestTerm;
import com.game.draco.app.quest.base.QuestTermType;
import com.game.draco.message.item.QuestTermMapEventItem;

import lombok.Data;
import sacred.alliance.magic.vo.RoleInstance;

public @Data class TriggerEventTerm extends QuestTerm<QuestTermMapEventItem> {
	
	private String eventId;//采集点ID
	private String eventName;
	
	public TriggerEventTerm(QuestTermType questTermType, int count, String eventId, String mapId) {
		super.setBaseValue(questTermType, count, eventId);
		this.eventId = eventId;
		this.mapId = mapId;
		this.initMapName();
		this.eventName = this.findCollectPointName(eventId);
	}

	@Override
	public QuestTermMapEventItem getQuestTermItem(RoleInstance role, Quest quest, boolean canSubmit, int index) {
		QuestTermMapEventItem item = new QuestTermMapEventItem();
		this.setBaseItemValue(role, quest, canSubmit, index, item);
		item.setMapId(this.mapId);
		item.setEventId(this.eventId);
		item.setMapName(this.mapName);
		item.setParameter(this.eventName);
		return item;
	}
	
}
