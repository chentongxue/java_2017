package com.game.draco.app.drama.config;

import lombok.Data;

import com.game.draco.message.item.DramaBaseItem;
import com.game.draco.message.item.DramaBaseNpcSetItem;

public @Data class DramaNpcSet extends DramaBase{
	private short npcId;
	private String color;

	@Override
	public DramaBaseItem getDramaBaseInfo() {
		DramaBaseNpcSetItem item = new DramaBaseNpcSetItem();
		item.setNpcId(this.npcId);
		item.setColor((int)Long.parseLong(this.color, 16));
		return item;
	}
}
