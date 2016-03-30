package com.game.draco.app.quest.phase.term;

import lombok.Data;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.npc.domain.NpcTemplate;
import com.game.draco.app.quest.Quest;
import com.game.draco.app.quest.QuestTerm;
import com.game.draco.app.quest.base.QuestTermType;
import com.game.draco.message.item.QuestTermGoodsItem;

public @Data class GoodsTerm extends QuestTerm<QuestTermGoodsItem> {
	
	private int goodsId;//物品ID
	private String npcId;//商店NPC的ID
	private String goodsName;
	
	public GoodsTerm(QuestTermType questTermType, int count, 
			int goodsId, String mapId, String npcId){
		super.setBaseValue(questTermType, count, String.valueOf(goodsId));
		this.goodsId = goodsId;
		this.mapId = mapId;
		this.npcId = npcId;
		this.initMapName();
		this.goodsName = this.findGoodsName(goodsId);
	}

	@Override
	public QuestTermGoodsItem getQuestTermItem(RoleInstance role, Quest quest, boolean canSubmit, int index) {
		QuestTermGoodsItem item = new QuestTermGoodsItem();
		this.setBaseItemValue(role, quest, canSubmit, index, item);
		item.setMapId(this.mapId);
		NpcTemplate nt = GameContext.getNpcApp().getNpcTemplate(this.npcId);
		if(null != nt){
			item.setNpcName(nt.getNpcname());
		}
		item.setGoodsId(this.goodsId);
		item.setMapName(this.mapName);
		item.setGoodsName(this.goodsName);
		return item;
	}
	
}
