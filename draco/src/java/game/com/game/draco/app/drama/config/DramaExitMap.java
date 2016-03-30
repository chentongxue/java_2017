package com.game.draco.app.drama.config;

import lombok.Data;

import com.game.draco.message.item.DramaBaseExitMapItem;
import com.game.draco.message.item.DramaBaseItem;

public @Data class DramaExitMap extends DramaBase {
	
	
	@Override
	public DramaBaseItem getDramaBaseInfo() {
		DramaBaseExitMapItem item = new DramaBaseExitMapItem();
		return item;
	}
}
