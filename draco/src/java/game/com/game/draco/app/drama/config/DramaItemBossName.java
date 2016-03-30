package com.game.draco.app.drama.config;

import lombok.Data;

import com.game.draco.message.item.DramaBaseItem;
import com.game.draco.message.item.DramaBaseNameItem;

public @Data class DramaItemBossName extends DramaBase{

	private String name ;
	private byte time ;
	private byte drift ;
	
	
	@Override
	public DramaBaseItem getDramaBaseInfo() {
		DramaBaseNameItem item = new DramaBaseNameItem() ;
		item.setName(name);
		item.setTime(time);
		item.setDrift(drift);
		return item;
	}
}
