package com.game.draco.app.target;

import java.util.Map;

import com.game.draco.app.target.domain.RoleTarget;
import com.google.common.collect.Maps;

public class UserTargetAppImpl implements UserTargetApp {
	private Map<Integer, RoleTarget> roleTargetMap = Maps.newConcurrentMap();

	@Override
	public void addRoleTarget(RoleTarget roleTarget) {
		this.roleTargetMap.put(roleTarget.getRoleId(), roleTarget);
	}

	@Override
	public RoleTarget getRoleTarget(int roleId) {
		return this.roleTargetMap.get(roleId);
	}

	@Override
	public RoleTarget removeRoleTarget(int roleId) {
		return this.roleTargetMap.remove(roleId);
	}


}
