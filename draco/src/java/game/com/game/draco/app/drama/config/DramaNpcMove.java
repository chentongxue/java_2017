package com.game.draco.app.drama.config;

import com.game.draco.message.item.DramaBaseItem;
import com.game.draco.message.item.DramaBaseNpcMoveItem;

import lombok.Data;

public @Data class DramaNpcMove extends DramaBase {
	private short npcId;
	private short targetX;
	private short targetY;
	private short speed;
	
	
	@Override
	public DramaBaseItem getDramaBaseInfo() {
		DramaBaseNpcMoveItem item = new DramaBaseNpcMoveItem();
		item.setNpcId(this.npcId);
		item.setTargetX(this.targetX);
		item.setTargetY(this.targetY);
		item.setSpeed(this.speed);
		return item;
	}
}
