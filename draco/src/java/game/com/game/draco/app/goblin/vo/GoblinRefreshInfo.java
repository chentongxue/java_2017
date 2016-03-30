package com.game.draco.app.goblin.vo;

import com.game.draco.app.npc.domain.NpcTemplate;

import sacred.alliance.magic.vo.Point;
import lombok.Data;

public @Data class GoblinRefreshInfo {

	private String mapId;
	private Point point;
	private NpcTemplate goblin;
	
}
