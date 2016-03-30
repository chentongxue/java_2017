package com.game.draco.app.hero;

import java.util.Collection;

import sacred.alliance.magic.app.goods.HeroEquipBackpack;

import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.app.hero.domain.RoleHeroStatus;

public interface UserHeroApp {

	public Collection<RoleHero> getAllRoleHero(String roleId) ;
	
	public RoleHero getRoleHero(String roleId,int heroId) ;
	
	public RoleHero getOnBattleRoleHero(String roleId) ;
	
	public boolean isOnBattleHero(String roleId,int heroId) ;
	
	public void setOnBattleRoleHero(String roleId,RoleHero roleHero,int maxSwitch);
	
	public HeroEquipBackpack getEquipBackpack(String roleId,int heroId) ;
	
	public Collection<HeroEquipBackpack> getEquipBackpack(String roleId) ;
	
	public void addRoleHero(String roleId,RoleHero roleHero) ;
	
	public boolean deleteRoleHero(String roleId,int heroId) ;
	
	public RoleHeroStatus getRoleHeroStatus(String roleId) ;
	
	public void initRoleHeroStatus(RoleHeroStatus status) ;
	
	public void cleanHeroData(String roleId);
	
	public void initHeroEquipBackpack(String roleId,HeroEquipBackpack pack);
	
	
}
