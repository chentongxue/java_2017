package com.game.draco.app.goddess;

import java.util.Map;

import lombok.Getter;
import sacred.alliance.magic.app.goods.GoddessEquipBackpack;
import sacred.alliance.magic.domain.RoleGoods;

import com.game.draco.app.goddess.domain.RoleGoddess;
import com.game.draco.app.goddess.domain.RoleGoddessStatus;
import com.google.common.collect.Maps;

public class UserGoddessAppImpl implements UserGoddessApp {
	
	/**
	 * 在线角色女神列表
	 */
	@Getter private Map<String, Map<Integer, RoleGoddess>> roleGoddessMap = Maps.newConcurrentMap();
	
	/**
	 * 角色装备信息
	 * key: heroId
	 */
	private Map<String,GoddessEquipBackpack> goddessEquipMap = Maps.newConcurrentMap();
	
	private Map<String, RoleGoddessStatus> roleGoddessStatusMap = Maps.newConcurrentMap();

	@Override
	public void addRoleGoddess(String roleId, RoleGoddess roleGoddess) {
		Map<Integer, RoleGoddess> all = this.roleGoddessMap.get(roleId);
		if(null == all) {
			all = Maps.newHashMap();
			this.roleGoddessMap.put(roleId, all);
		}
		all.put(roleGoddess.getGoddessId(), roleGoddess);
	}

	@Override
	public Map<Integer, RoleGoddess> getAllRoleGoddess(String roleId) {
		return this.roleGoddessMap.get(roleId);
	}

	@Override
	public void removeAllRoleGoddess(String roleId) {
		this.roleGoddessMap.remove(roleId);
	}

	@Override
	public RoleGoddess getRoleGoddess(String roleId, int goddessId) {
		Map<Integer, RoleGoddess> all = this.roleGoddessMap.get(roleId);
		if(null == all) {
			return null;
		}
		return all.get(goddessId);
	}

	@Override
	public void removeEquipList(String roleId) {
		this.goddessEquipMap.remove(roleId);
	}
	
	@Override
	public RoleGoddess getOnBattleRoleGoddess(String roleId) {
		RoleGoddessStatus record = this.roleGoddessStatusMap.get(roleId);
		if(null == record) {
			return null;
		}
		
		return this.getRoleGoddess(roleId, record.getBattleGoddessId());
	}

	@Override
	public void addRoleGoddessRecord(String roleId,
			RoleGoddessStatus record) {
		this.roleGoddessStatusMap.put(roleId, record);
	}

	@Override
	public void setOnBattleRoleGoddess(String roleId, RoleGoddess roleGoddess) {
		if(null == roleGoddess) {
			return ;
		}
		RoleGoddess oldOnBattle = this.getOnBattleRoleGoddess(roleId);
		if(null != oldOnBattle) {
			oldOnBattle.setOnBattle(GoddessAppImpl.ON_BATTLE);
		}
		roleGoddess.setOnBattle(GoddessAppImpl.ON_BATTLE);
		RoleGoddessStatus record = this.getRoleGoddessStatus(roleId);
		if(null != record) {
			record.setBattleGoddessId(roleGoddess.getGoddessId());
		}
	}

	@Override
	public RoleGoddessStatus getRoleGoddessStatus(String roleId) {
		return this.roleGoddessStatusMap.get(roleId);
	}

	@Override
	public void removeRoleGoddessRecord(String roleId) {
		this.roleGoddessStatusMap.remove(roleId);
	}

	@Override
	public boolean isOwnGoddess(String roleId, int goddessId) {
		Map<Integer, RoleGoddess> all = this.roleGoddessMap.get(roleId);
		if(null == all) {
			return false;
		}
		return all.containsKey(goddessId);
	}
	
	@Override
	public void initGoddessEquipBackpack(String roleId,GoddessEquipBackpack pack){
		this.goddessEquipMap.put(roleId, pack);
	}

	@Override
	public GoddessEquipBackpack getGoddessEquipBackpack(String roleId){
		return this.goddessEquipMap.get(roleId);
	}
}
