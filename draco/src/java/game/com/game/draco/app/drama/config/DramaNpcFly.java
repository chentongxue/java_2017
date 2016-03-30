package com.game.draco.app.drama.config;

import lombok.Data;

import com.game.draco.message.item.DramaBaseFlyItem;
import com.game.draco.message.item.DramaBaseItem;

public @Data class DramaNpcFly extends DramaBase {
	
	private short npcId;
	private short anim ;

	@Override
	public DramaBaseItem getDramaBaseInfo() {
		DramaBaseFlyItem item = new DramaBaseFlyItem();
		item.setNpcId(npcId);
		item.setAnim(anim) ;
		return item;
	}

}
