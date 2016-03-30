package com.game.draco.app.hero.arena;

import java.util.List;

import com.game.draco.app.hero.arena.domain.RoleHeroArenaRecord;
import com.game.draco.app.hero.domain.RoleHero;

public interface HeroArenaStorage {
	
	RoleHeroArenaRecord getRoleHeroArenaRecord(String roleId);
	
	void saveRoleHeroArenaRecord(RoleHeroArenaRecord record);
	
	List<RoleHero> getRoleHeros(String roleId);
	
	void saveRoleHeros(String roleId, List<RoleHero> heros);
	
	/*public RoleHero getRoleHero(String roleId, int heroId);
	
	public void saveRoleHero(RoleHero roleHero);
	
	public Map<Integer, RoleHero> getRoleHeroMap(String roleId);*/
	
}
