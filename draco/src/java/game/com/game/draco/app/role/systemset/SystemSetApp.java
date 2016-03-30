package com.game.draco.app.role.systemset;

import java.util.List;

import com.game.draco.app.AppSupport;
import com.game.draco.message.item.SystemSetItem;

import sacred.alliance.magic.vo.RoleInstance;

public interface SystemSetApp extends AppSupport{
	
	/**
	 * 玩家更改系统设置
	 * @param role
	 * @param sysSetList
	 * @return
	 */
	public boolean modifyRoleSysSet(RoleInstance role, List<SystemSetItem> sysSetList);
	
	/**
	 * 获得角色的系统设置
	 * @param role
	 * @return
	 */
	public List<SystemSetItem> getSystemSetList(RoleInstance role);
	
}
