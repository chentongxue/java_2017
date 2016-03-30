package com.game.draco.app.pet;

import java.util.Map;

import com.game.draco.GameContext;
import com.game.draco.app.pet.domain.RolePet;
import com.game.draco.app.pet.domain.RolePetStatus;
import com.google.common.collect.Maps;

public class UserPetAppImpl implements UserPetApp {

	public final static byte OFF_BATTLE = 1;
	public final static byte ON_BATTLE = 2;

	private Map<String, Map<Integer, RolePet>> rolePetMap = Maps.newConcurrentMap();// 角色拥有宠物列表
	private Map<String, RolePetStatus> rolePetStatus = Maps.newConcurrentMap();// 角色宠物状态

	private Map<Integer, RolePet> getRolePetMap(String roleId) {
		return this.rolePetMap.get(roleId);
	}

	@Override
	public void addRolePet(String roleId, RolePet rolePet) {
		Map<Integer, RolePet> rolePetMap = this.getRolePetMap(roleId);
		if (null == rolePetMap) {
			rolePetMap = Maps.newHashMap();
			this.rolePetMap.put(roleId, rolePetMap);
		}
		rolePetMap.put(rolePet.getPetId(), rolePet);
	}

	@Override
	public void addRolePetStatus(String roleId, RolePetStatus status) {
		this.rolePetStatus.put(roleId, status);
	}

	@Override
	public Map<Integer, RolePet> getAllRolePet(String roleId) {
		return this.getRolePetMap(roleId);
	}

	@Override
	public RolePet getOnBattleRolePet(String roleId) {
		RolePetStatus status = this.getRolePetStatus(roleId);
		if (null == status) {
			return null;
		}
		return this.getRolePet(status.getRoleId(), status.getBattlePetId());
	}

	@Override
	public RolePet getRolePet(String roleId, int petId) {
		Map<Integer, RolePet> rolePetMap = this.getRolePetMap(roleId);
		if (null == rolePetMap) {
			return null;
		}
		return rolePetMap.get(petId);
	}

	@Override
	public RolePetStatus getRolePetStatus(String roleId) {
		RolePetStatus status = this.rolePetStatus.get(roleId);
		if (null == status) {
			status = new RolePetStatus();
			status.setRoleId(roleId);
			this.rolePetStatus.put(roleId, status);
		}
		return this.rolePetStatus.get(roleId);
	}

	@Override
	public void removeAllRolePet(String roleId) {
		this.rolePetMap.remove(roleId);
	}

	@Override
	public void removeRolePetStatus(String roleId) {
		this.rolePetStatus.remove(roleId);
	}
	
	@Override
	public boolean isOnBattle(String roleId, int petId) {
		RolePet rolePet = this.getOnBattleRolePet(roleId);
		if (null == rolePet) {
			return false;
		}
		if (petId == this.getOnBattleRolePet(roleId).getPetId()) {
			return true;
		}
		return false;
	}


	@Override
	public void setOnBattleRolePet(String roleId, RolePet offBattlePet, RolePet onBattlePet) {
		if (null == offBattlePet) {
			onBattlePet.setOnBattle(ON_BATTLE);
			this.petOnBattle(roleId, onBattlePet);
			return;
		}
		offBattlePet.setOnBattle(OFF_BATTLE);
		onBattlePet.setOnBattle(ON_BATTLE);
		this.petOnBattle(roleId, onBattlePet);
	}
	
	private void petOnBattle(String roleId, RolePet onBattlePet) {
		RolePetStatus status = this.getRolePetStatus(roleId);
		if (null == status) {
			status = new RolePetStatus();
			status.setRoleId(roleId);
			status.setBattlePetId(onBattlePet.getPetId());
			this.rolePetStatus.put(roleId, status);
			return;
		}
		status.setBattlePetId(onBattlePet.getPetId());
	}

	@Override
	public void petOffBattle(String roleId) {
		RolePetStatus status = this.getRolePetStatus(roleId);
		if (null != status) {
			status.setBattlePetId(OFF_BATTLE);
		}
	}

	@Override
	public void removeRolePet(String roleId, int petId) {
		Map<Integer,RolePet> allRolePet = this.rolePetMap.get(roleId);
		if (null == allRolePet) {
			return;
		}
		RolePet rolePet = allRolePet.get(petId);
		if (null == rolePet) {
			return;
		}
		allRolePet.remove(petId);
	}

	@Override
	public void cleanRolePetDate(String roleId) {
		this.rolePetMap.remove(roleId);
		this.rolePetStatus.remove(roleId);
	}

	@Override
	public void insertRolePet(RolePet rolePet) {
		GameContext.getBaseDAO().insert(rolePet);
	}
	
	@Override
	public void deleteRolePet(String roleId, int petId) {
		GameContext.getBaseDAO().delete(RolePet.class, RolePet.MASTER_ID, roleId, RolePet.PET_ID, petId);
	}
	
	@Override
	public void rolePetUpdate(String roleId, RolePet rolePet) {
		Map<Integer, RolePet> rolePetMap = this.getRolePetMap(roleId);
		if (null == rolePetMap) {
			return;
		}
		rolePetMap.put(rolePet.getPetId(), rolePet);// 更改Map中的宠物信息
		GameContext.getBaseDAO().update(rolePet);// 入库
	}

}
