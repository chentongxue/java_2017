package com.game.draco.app.drama.config;

import java.util.ArrayList;
import java.util.List;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data class Drama implements KeySupport<Short>{
	public final static byte REPLAY_NO = 0;
	public final static byte REPLAY_YES = 1;
	private short dramaId; //剧情id
	private String componentIds; //剧情元素ids
	private byte replay; //0:不重播,1:重播
	
	private List<DramaBase> dramaBaseList = new ArrayList<DramaBase>();
	
	@Override
	public Short getKey() {
		return this.dramaId;
	}
	
}
