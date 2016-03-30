package com.game.draco.app.union.battle;

import java.util.List;

import lombok.Data;

public @Data class UnionBattleMapBuffParam {
	//攻击方BUFF
	private List<Short> buffIds;
	private int mapbuffLevel;
}
