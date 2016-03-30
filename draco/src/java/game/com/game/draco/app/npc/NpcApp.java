package com.game.draco.app.npc;

import sacred.alliance.magic.base.ForceRelation;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.core.exception.ServiceException;

import com.game.draco.app.npc.config.ForceConfig;
import com.game.draco.app.npc.domain.NpcTemplate;
import com.game.draco.app.npc.npcfunction.NpcFunctionSupport;


public interface NpcApp extends Service,NpcFunctionSupport{
	
	public boolean reload() throws ServiceException;
	
	public NpcTemplate getNpcTemplate(String templateId);
	
	public ForceConfig getForceConfig (int forceId) ;
	
	public ForceRelation getNpcForceRelation(byte type1,byte type2);
}
