package com.game.draco.app.drama.config;

import lombok.Data;

import com.game.draco.message.item.DramaBaseItem;
import com.game.draco.message.item.DramaBaseTipItem;

public @Data class DramaTip extends DramaBase {
	private String tip;

	@Override
	public DramaBaseItem getDramaBaseInfo() {
		DramaBaseTipItem item = new DramaBaseTipItem();
		item.setTip(this.tip);
		return item;
	}

}
