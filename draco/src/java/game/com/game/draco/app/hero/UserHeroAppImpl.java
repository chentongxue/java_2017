package com.game.draco.app.hero;

import java.util.Collection;
import java.util.Map;

import lombok.Getter;
import sacred.alliance.magic.app.goods.HeroEquipBackpack;

import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.app.hero.domain.RoleHeroStatus;
import com.google.common.collect.Maps;

public class UserHeroAppImpl implements UserHeroApp{

	/**
	 * 在线角色英雄列表
	 * key: roleId
	 * value: heroMap
	 */
	@Getter private Map<String,Map<Integer,RoleHero>> roleHeroMap = Maps.newConcurrentMap();
	/**
	 * key roleId
	 * value: RoleHeroStatus
	 */
	@Getter private Map<String,RoleHeroStatus> roleHeroStatusMap = Maps.newConcurrentMap();
	
	@Getter private Map<String,Map<Integer,HeroEquipBackpack>> equipBackpackMap = Maps.newConcurrentMap();
	
	
	public void cleanHeroData(String roleId){
		roleHeroMap.remove(roleId);
		roleHeroStatusMap.remove(roleId);
		equipBackpackMap.remove(roleId);
	}
	
	
	@Override
	public Collection<RoleHero> getAllRoleHero(String roleId) {
		Map<Integer,RoleHero> map = this.roleHeroMap.get(roleId);
		if(null == map){
			return null ;
		}
		return map.values();
	}

	@Override
	public RoleHero getRoleHero(String roleId, int heroId) {
		Map<Integer,RoleHero> map = this.roleHeroMap.get(roleId);
		if(null == map){
			return null ;
		}
		return map.get(heroId) ;
	}

	@Override
	public RoleHero getOnBattleRoleHero(String roleId) {
		RoleHeroStatus status = this.roleHeroStatusMap.get(roleId) ;
		if(null == status){
			return null ;
		}
		return this.getRoleHero(roleId, status.getBattleHeroId());
	}
	
	@Override
	public boolean isOnBattleHero(String roleId,int heroId){
		RoleHero hero = this.getOnBattleRoleHero(roleId);
		return (null != hero) && hero.getHeroId() == heroId ;
	}
	
	@Override
	public void setOnBattleRoleHero(String roleId,RoleHero roleHero,int maxSwitch){
		if(null == roleHero){
			return ;
		}
		RoleHero oldOnBattle = this.getOnBattleRoleHero(roleId);
		if(null != oldOnBattle){
			oldOnBattle.setOnBattle((byte)0);
		}
		roleHero.setOnBattle((byte)1);
		RoleHeroStatus status = this.getRoleHeroStatus(roleId);
		status.setBattleHeroId(roleHero.getHeroId());
			//添加到可切换列表
		status.getSwitchHeroSet().add(roleHero.getHeroId());
		if(status.getSwitchHeroSet().size() > maxSwitch 
				&& null != oldOnBattle){
			//最多n个可切换
			status.getSwitchHeroSet().remove(oldOnBattle.getHeroId());
		}
	}

	
	@Override
	public HeroEquipBackpack getEquipBackpack(String roleId,int heroId) {
		Map<Integer,HeroEquipBackpack> map = this.equipBackpackMap.get(roleId);
		return (null == map)?null : map.get(heroId);
	}
	
	public Collection<HeroEquipBackpack> getEquipBackpack(String roleId) {
		Map<Integer,HeroEquipBackpack> map = this.equipBackpackMap.get(roleId);
		if(null == map){
			return null ;
		}
		return map.values();
	}
	
	@Override
	public void addRoleHero(String roleId,RoleHero roleHero){
		Map<Integer,RoleHero> all = this.roleHeroMap.get(roleId);
		if(null == all){
			all = Maps.newHashMap();
			this.roleHeroMap.put(roleId, all);
		}
		all.put(roleHero.getHeroId(), roleHero);
	}
	
	@Override
	public boolean deleteRoleHero(String roleId,int heroId){
		Map<Integer,RoleHero> all = this.roleHeroMap.get(roleId);
		if(null != all){
			all.remove(heroId);
		}
		//删除装备容器
		Map<Integer,HeroEquipBackpack> packMap = this.equipBackpackMap.get(roleId);
		if(null != packMap){
			packMap.remove(heroId);
		}
		RoleHeroStatus status = roleHeroStatusMap.get(roleId);
		if(null == status){
			return false ;
		}
		return status.deleteHero(heroId);
	}
	
	@Override
	public RoleHeroStatus getRoleHeroStatus(String roleId){
		RoleHeroStatus status = this.roleHeroStatusMap.get(roleId);
		if(null == status){
			status = new RoleHeroStatus();
			status.setRoleId(roleId);
			this.roleHeroStatusMap.put(roleId, status);
		}
		return status ;
	}
	
	@Override
	public void initRoleHeroStatus(RoleHeroStatus status){
		status.postFormStore();
		this.roleHeroStatusMap.put(status.getRoleId(), status);
	}
	
	@Override
	public void initHeroEquipBackpack(String roleId,HeroEquipBackpack pack){
		if(null == pack){
			return ;
		}
		Map<Integer,HeroEquipBackpack> map = this.equipBackpackMap.get(roleId);
		if(null == map){
			map = Maps.newHashMap();
			this.equipBackpackMap.put(roleId,map);
		}
		map.put(pack.getHeroId(), pack);
	}
	
}
