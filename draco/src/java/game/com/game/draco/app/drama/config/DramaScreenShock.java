package com.game.draco.app.drama.config;

import lombok.Data;

import com.game.draco.message.item.DramaBaseItem;
import com.game.draco.message.item.DramaBaseShockItem;

public @Data class DramaScreenShock extends DramaBase {
	private byte lastTime;
	
	@Override
	public DramaBaseItem getDramaBaseInfo() {
		DramaBaseShockItem item = new DramaBaseShockItem();
		item.setLastTime(this.lastTime);
		return item;
	}

}
