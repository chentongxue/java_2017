package com.game.draco.app.pet;

import java.util.Map;

import com.game.draco.app.pet.domain.RolePetBattleList;
import com.game.draco.app.pet.domain.RolePetShow;
import com.game.draco.app.pet.domain.RolePetStatus;

public interface PetStorage {
	
	public void saveRolePetStatus(RolePetStatus record);
	
	public RolePetStatus getRolePetStatus(String roleId);
	
	public void saveShowRolePet(String roleId, RolePetShow petShow);
	
	public RolePetShow getShowRolePet(String roleId);
	
	public void saveRolePetBattle(String roleId, int battleScore);
	
	public Map<String, String> getRolePetBattleScores(String roleId, int battleScore, int limit);
	
	public void saveRolePetBattleList(String roleId, RolePetBattleList petBattleList);
	
	public RolePetBattleList getRolePetBattleList(String roleId);
	
}
