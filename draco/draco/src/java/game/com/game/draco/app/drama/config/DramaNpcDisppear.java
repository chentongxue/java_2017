package com.game.draco.app.drama.config;

import com.game.draco.message.item.DramaBaseItem;
import com.game.draco.message.item.DramaBaseNpcDisppearItem;

import lombok.Data;

public @Data class DramaNpcDisppear extends DramaBase{
	private short npcId;
	private byte disppearType;
	
	@Override
	public DramaBaseItem getDramaBaseInfo() {
		DramaBaseNpcDisppearItem item  = new DramaBaseNpcDisppearItem();
		item.setNpcId(this.npcId);
		this.setDisppearType(this.disppearType);
		return item;
	}
	
}
