package com.game.draco.app.target;

import com.game.draco.app.target.domain.RoleTarget;

public interface UserTargetApp {
	void addRoleTarget(RoleTarget roleTarget);
	RoleTarget removeRoleTarget(int roleId);
	RoleTarget getRoleTarget(int roleId);

}
