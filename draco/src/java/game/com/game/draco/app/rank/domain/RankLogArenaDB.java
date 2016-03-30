package com.game.draco.app.rank.domain;

import lombok.Data;
import sacred.alliance.magic.domain.RoleArena;
/**
 * 擂台赛表和角色表联合查询返回结果
 */
public @Data class RankLogArenaDB extends RoleArena{
	//职业
	private byte career;
}
