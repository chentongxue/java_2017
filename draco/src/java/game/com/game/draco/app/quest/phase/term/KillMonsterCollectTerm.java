package com.game.draco.app.quest.phase.term;

import com.game.draco.GameContext;
import com.game.draco.app.npc.domain.NpcTemplate;
import com.game.draco.app.quest.Quest;
import com.game.draco.app.quest.QuestTerm;
import com.game.draco.app.quest.base.QuestTermType;
import com.game.draco.message.item.QuestTermMapNpcItem;

import lombok.Data;
import sacred.alliance.magic.vo.RoleInstance;

public @Data class KillMonsterCollectTerm extends QuestTerm<QuestTermMapNpcItem> {
	
	private String npcId;//NPC的ID
	private int goodsId;//物品ID
	private String goodsName;
	
	public KillMonsterCollectTerm(QuestTermType questTermType, int count,
			String mapId, String npcId, int goodsId){
		super.setBaseValue(questTermType, count, String.valueOf(goodsId));
		this.mapId = mapId;
		this.npcId = npcId;
		this.initMapName();
		this.goodsName = this.findGoodsName(goodsId);
	}

	@Override
	public QuestTermMapNpcItem getQuestTermItem(RoleInstance role, Quest quest, boolean canSubmit, int index) {
		QuestTermMapNpcItem item = new QuestTermMapNpcItem();
		this.setBaseItemValue(role, quest, canSubmit, index, item);
		item.setMapId(this.mapId);
		NpcTemplate nt = GameContext.getNpcApp().getNpcTemplate(this.npcId);
		if(null != nt){
			item.setNpcName(nt.getNpcname());
		}
		item.setMapName(this.mapName);
		item.setParameter(this.goodsName);
		return item;
	}
	
}
