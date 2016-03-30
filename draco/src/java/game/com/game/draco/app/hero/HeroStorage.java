package com.game.draco.app.hero;

import java.util.List;

import com.game.draco.app.hero.domain.HeroEquip;
import com.game.draco.app.hero.domain.RoleHero;

public interface HeroStorage {
	
	List<RoleHero> getRoleHeros(String roleId);
	
	void saveRoleHeros(String roleId, List<RoleHero> heros);
	
	public HeroEquip getHeroEquip(String roleId);
	
	void saveHeroEquip(HeroEquip heroEquip) ;
}
