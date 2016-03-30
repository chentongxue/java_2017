package com.game.draco.app.hero.arena;

import com.game.draco.app.hero.arena.domain.RoleHeroArenaRecord;

public interface HeroArenaStorage {
	
	RoleHeroArenaRecord getRoleHeroArenaRecord(String roleId);
	
	void saveRoleHeroArenaRecord(RoleHeroArenaRecord record);
	
}
