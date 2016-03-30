package com.game.draco.app.goddess;

import java.util.Map;

import sacred.alliance.magic.app.goods.GoddessEquipBackpack;
import sacred.alliance.magic.domain.RoleGoods;

import com.game.draco.app.goddess.domain.RoleGoddess;
import com.game.draco.app.goddess.domain.RoleGoddessStatus;

public interface UserGoddessApp {
	void addRoleGoddess(String roleId, RoleGoddess roleGoddess);
	Map<Integer, RoleGoddess> getAllRoleGoddess(String roleId);
	void removeAllRoleGoddess(String roleId);
	RoleGoddess getRoleGoddess(String roleId, int goddessId);
	void removeEquipList(String roleId);
	RoleGoddess getOnBattleRoleGoddess(String roleId);
	void addRoleGoddessRecord(String roleId, RoleGoddessStatus record);
	void removeRoleGoddessRecord(String roleId);
	void setOnBattleRoleGoddess(String roleId, RoleGoddess roleGoddess);
	RoleGoddessStatus getRoleGoddessStatus(String roleId);
	boolean isOwnGoddess(String roleId, int goddessId);
	void initGoddessEquipBackpack(String roleId,GoddessEquipBackpack pack);
	GoddessEquipBackpack getGoddessEquipBackpack(String roleId);
}
