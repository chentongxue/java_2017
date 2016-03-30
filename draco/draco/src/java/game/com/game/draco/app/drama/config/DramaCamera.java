package com.game.draco.app.drama.config;

import com.game.draco.message.item.DramaBaseCameraItem;
import com.game.draco.message.item.DramaBaseItem;

import lombok.Data;

public @Data class DramaCamera extends DramaBase {
	private short camX;
	private short camY;
	private byte speed;
	
	
	@Override
	public DramaBaseItem getDramaBaseInfo() {
		DramaBaseCameraItem item = new DramaBaseCameraItem();
		item.setCamX(this.camX);
		item.setCamY(this.camY);
		item.setSpeed(this.speed);
		return item;
	}
}
