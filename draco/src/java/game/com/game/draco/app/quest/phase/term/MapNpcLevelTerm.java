package com.game.draco.app.quest.phase.term;

import com.game.draco.app.quest.Quest;
import com.game.draco.app.quest.QuestTerm;
import com.game.draco.app.quest.base.QuestTermType;
import com.game.draco.message.item.QuestTermMapNpcLevelItem;

import lombok.Data;
import sacred.alliance.magic.vo.RoleInstance;

public @Data class MapNpcLevelTerm extends QuestTerm<QuestTermMapNpcLevelItem> {
	
	private int minLevel;//等级下限
	private int maxLevel;//等级上限
	
	public MapNpcLevelTerm(QuestTermType questTermType, int count, String mapId, int minLevel, int maxLevel){
		super.setBaseValue(questTermType, count, null);
		this.mapId = mapId;
		this.minLevel = minLevel;
		this.maxLevel = maxLevel;
		this.initMapName();
	}

	@Override
	public QuestTermMapNpcLevelItem getQuestTermItem(RoleInstance role, Quest quest, boolean canSubmit, int index) {
		QuestTermMapNpcLevelItem item = new QuestTermMapNpcLevelItem();
		this.setBaseItemValue(role, quest, canSubmit, index, item);
		item.setMapId(this.mapId);
		item.setMinLevel((byte) this.minLevel);
		item.setMaxLevel((byte) this.maxLevel);
		item.setMapName(this.mapName);
		return item;
	}
	
}
