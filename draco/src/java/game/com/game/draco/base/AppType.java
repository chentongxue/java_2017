package com.game.draco.base;

import sacred.alliance.magic.base.ActiveType;

public enum AppType {
	boss_dps(1,ActiveType.BossDps),
	camp_war(2,ActiveType.CampWar),
	arena_1v1(3,ActiveType.SyncArena),
	angel_chest(4,ActiveType.AngelChest),
	;
	
	private final int type ;
	private final ActiveType activeType ;
	private AppType(int type,ActiveType activeType){
		this.type = type ;
		this.activeType = activeType ;
	}
	
	public int getType() {
		return type;
	}

	public ActiveType getActiveType() {
		return activeType;
	}
	
	
}
