package com.game.draco.app.qualify;

import com.game.draco.app.qualify.domain.RoleQualifyRecord;

public interface QualifyStorage {
	
	/**
	 * 保存挑战记录
	 * @param record
	 */
	public void saveRoleQualifyRecord(RoleQualifyRecord record);
	
	/**
	 * 得到玩家的挑战记录
	 * @param roleId
	 * @return
	 */
	public RoleQualifyRecord getRoleQualifyRecord(String roleId);
	
}
