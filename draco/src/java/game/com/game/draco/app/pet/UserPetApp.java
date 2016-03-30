package com.game.draco.app.pet;

import java.util.Map;

import com.game.draco.app.pet.domain.RolePet;
import com.game.draco.app.pet.domain.RolePetStatus;

public interface UserPetApp {
	
	public void addRolePet(String roleId, RolePet rolePet);// 增加宠物（不入库）
	
	public void insertRolePet(RolePet rolePet);// 增加宠物（入库）

	public Map<Integer, RolePet> getAllRolePet(String roleId);// 获得所有的宠物

	public void removeAllRolePet(String roleId);// 移除所有的宠物

	public RolePet getRolePet(String roleId, int petId);// 获得宠物对象

	public RolePet getOnBattleRolePet(String roleId);// 获得出战宠物对象

	public void addRolePetStatus(String roleId, RolePetStatus status);// 增加状态
	
	public void removeRolePetStatus(String roleId);// 移除状态

	public void setOnBattleRolePet(String roleId, RolePet offBattlePet, RolePet onBattlePet);// 更改出战宠物
	
	public void petOffBattle(String roleId);// 宠物休息

	public RolePetStatus getRolePetStatus(String roleId);// 获得状态

	public boolean isOnBattle(String roleId, int petId);// 是否出战
	
	public void rolePetUpdate(String roleId, RolePet rolePet);// 修改宠物信息（入库）
	
	public void removeRolePet(String roleId, int petId);// 删除宠物（不入库）
	
	public void cleanRolePetDate(String roleId);// 下线清楚数据
	
	public void deleteRolePet(String roleId, int petId);// 删除宠物（入库）
	
}
