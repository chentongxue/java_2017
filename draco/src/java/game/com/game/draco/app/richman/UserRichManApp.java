package com.game.draco.app.richman;

import com.game.draco.app.richman.domain.RoleRichMan;

public interface UserRichManApp {
	void addRoleRichMan(RoleRichMan rrm);
	void removeRoleRichMan(int roleId);
	RoleRichMan getRoleRichMan(int roleId);
}
