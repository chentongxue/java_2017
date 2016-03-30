package com.game.draco.app.medal;

import com.game.draco.app.medal.vo.MedalRoleData;

public interface MedalStorage {
	
	MedalRoleData getMedalRoleData(String roleId);
	
	void saveMedalRoleData(MedalRoleData data);
	
}
