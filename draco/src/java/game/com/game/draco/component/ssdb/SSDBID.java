package com.game.draco.component.ssdb;

public interface SSDBID {

	// 英雄出战的3英雄列表(排位赛离线对手信息用到)
	public static final String ROLE_ARENA_HEROS = "ROLE_ARENA_HEROS:S";

	// 英雄试炼记录
	public static final String HERO_ARENA_RECORD = "HERO_ARENA_RECORD:S";

	// 英雄装备列表(查看离线玩家的装备列表)
	public static final String HERO_EQUIP_RECORD = "HERO_EQUIP_RECORD:S";
	
	// 勋章缓存(查看离线角色)
	public static final String MEDAL_ROLE_DATA = "MEDAL_ROLE_DATA:S";

	// pvp 离线角色相关的属性
	public static final String ASYNCPVP_ROLEATTR = "ASYNCPVP_ROLEATTR:S";

	// 战力
	public static final String ROLE_BATTLESCORE = "ROLE_BATTLESCORE:S";

	// 异步竞技场
	public static final String ROLE_ASYNCARENA = "ROLE_ASYNCARENA:S";

	// 坐骑缓存(查看离线角色)
	public static final String ROLEHORSE_ONBATTLE = "ROLEHORSE_ONBATTLE:S";

	// 宠物战斗力
	public static final String PET_BATTLESORE = "PET_BATTLESORE:S";
	
	// 宠物掠夺对手列表
	public static final String ROLE_PET_BATTLE = "ROLE_PET_BATTLE:S";
	
	// 宠物掠夺信息
	public static final String PET_STATUS = "PET_STATUS:S";

	// 查看离线玩家的宠物
	public static final String PET_SHOW = "PET_SHOW:S";
	
	// 角色排位赛挑战记录
	public static final String QUALIFY_RECORD = "QUALIFY_RECORD:S";

}
