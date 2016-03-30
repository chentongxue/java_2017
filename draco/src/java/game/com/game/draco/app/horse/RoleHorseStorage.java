package com.game.draco.app.horse;

import com.game.draco.app.horse.domain.RoleHorseCache;

public interface RoleHorseStorage {
	void saveRoleHorseOnBattle(RoleHorseCache record);
	RoleHorseCache getRoleHorseOnBattle(int roleId);
}
