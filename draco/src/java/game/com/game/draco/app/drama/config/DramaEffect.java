package com.game.draco.app.drama.config;

import lombok.Data;

import com.game.draco.message.item.DramaBaseEffectItem;
import com.game.draco.message.item.DramaBaseItem;

public @Data class DramaEffect extends DramaBase {
	
	private short x ;
	private short y ;
	private short effectId ;
	private short time ;

	@Override
	public DramaBaseItem getDramaBaseInfo() {
		DramaBaseEffectItem item = new DramaBaseEffectItem();
		item.setX(x);
		item.setY(y);
		item.setEffectId(effectId);
		item.setTime(time);
		return item;
	}

}
