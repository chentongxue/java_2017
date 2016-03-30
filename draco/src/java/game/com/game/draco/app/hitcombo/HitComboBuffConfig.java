package com.game.draco.app.hitcombo;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data class HitComboBuffConfig implements KeySupport<Integer>{
	private int hitCombo;
	private short buffId;
	private int buffLevel;
	private short musicId ;
	
	@Override
	public Integer getKey() {
		return hitCombo;
	}
}
