package com.game.draco.app.drama.config;

import com.game.draco.message.item.DramaBaseBubbleItem;
import com.game.draco.message.item.DramaBaseItem;

import lombok.Data;

public @Data class DramaBubble extends DramaBase {
	private short npcId;
	private String content;
	private byte lastTime;
	
	
	@Override
	public DramaBaseItem getDramaBaseInfo() {
		DramaBaseBubbleItem item = new DramaBaseBubbleItem();
		item.setNpcId(this.npcId);
		item.setContent(this.content);
		item.setLastTime(this.lastTime);
		return item;
	}
}
