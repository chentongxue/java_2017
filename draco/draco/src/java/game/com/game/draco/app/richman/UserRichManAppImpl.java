package com.game.draco.app.richman;

import java.util.Map;

import com.game.draco.app.richman.domain.RoleRichMan;
import com.google.common.collect.Maps;

import lombok.Getter;

public class UserRichManAppImpl implements UserRichManApp {
	@Getter private Map<Integer, RoleRichMan> roleRichManMap = Maps.newConcurrentMap();

	@Override
	public void addRoleRichMan(RoleRichMan rrm) {
		this.roleRichManMap.put(rrm.getRoleId(), rrm);
	}

	@Override
	public void removeRoleRichMan(int roleId) {
		this.roleRichManMap.remove(roleId);
	}

	@Override
	public RoleRichMan getRoleRichMan(int roleId) {
		return this.roleRichManMap.get(roleId);
	}
}
