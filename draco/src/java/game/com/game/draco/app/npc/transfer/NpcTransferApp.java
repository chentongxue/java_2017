package com.game.draco.app.npc.transfer;

import com.game.draco.app.npc.npcfunction.NpcFunctionSupport;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.vo.RoleInstance;

public interface NpcTransferApp extends NpcFunctionSupport,Service{
	
	/**
	 * 換冞俙模善硌隅華芞
	 */
	public Result transferRole(RoleInstance role,String param);
	
	public void reload()throws ServiceException;

}
