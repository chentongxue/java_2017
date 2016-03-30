package com.game.draco.app.drama;

import java.util.Map;

import com.game.draco.app.drama.domain.RoleDrama;
import com.google.common.collect.Maps;

public class UserDramaAppImpl implements UserDramaApp {
	/**
	 * 在线角色剧情信息
	 */
	private Map<String, Map<Short, RoleDrama>> roleDramaMap = Maps.newConcurrentMap();

	@Override
	public void addRoleHero(String roleId, RoleDrama roleDrama) {
		Map<Short, RoleDrama> dramas = roleDramaMap.get(roleId);
		if(null == dramas) {
			dramas = Maps.newConcurrentMap();
			this.roleDramaMap.put(roleId, dramas);
		}
		dramas.put(roleDrama.getDramaId(), roleDrama);
	}

	@Override
	public RoleDrama getRoleDrama(String roleId, short dramaId) {
		Map<Short, RoleDrama> dramas = this.roleDramaMap.get(roleId);
		if(null == dramas) {
			return null;
		}
		return dramas.get(dramaId);
	}

	@Override
	public Map<Short, RoleDrama> getRoleDramas(String roleId) {
		return this.roleDramaMap.get(roleId);
	}

	@Override
	public void removeRoleDramas(String roleId) {
		this.roleDramaMap.remove(roleId);
	}
	
}
