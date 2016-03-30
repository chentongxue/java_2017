package com.game.draco.app.drama.config;

import com.game.draco.GameContext;
import com.game.draco.message.item.DramaBaseItem;
import com.game.draco.message.item.DramaBaseNpcTalkItem;

import lombok.Data;

public @Data class DramaNpcTalk extends DramaBase {
	private short npcId;
	private String content;
	private byte imagePos;
	private byte lastTime;
	
	@Override
	public DramaBaseItem getDramaBaseInfo() {
		DramaBaseNpcTalkItem item = new DramaBaseNpcTalkItem();
		item.setNpcId(this.npcId);
		item.setContent(this.getContent());
		DramaNpc npc = GameContext.getDramaApp().getDramaNpc(this.npcId);
		item.setImageId(npc.getImageId());
		item.setNpcName(npc.getNpcName());
		item.setImagePos(this.imagePos);
		item.setLastTime(this.lastTime);
		return item;
	}
}
