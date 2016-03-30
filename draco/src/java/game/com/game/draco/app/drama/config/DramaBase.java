package com.game.draco.app.drama.config;

import com.game.draco.message.item.DramaBaseItem;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data abstract class DramaBase implements KeySupport<Integer>{
	private int id; //剧情元素唯一标识
	private byte type; //剧情类型
	
	public abstract DramaBaseItem getDramaBaseInfo();
	
	@Override
	public Integer getKey() {
		return this.id;
	}
}
