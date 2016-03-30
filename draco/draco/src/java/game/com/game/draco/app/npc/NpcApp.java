package com.game.draco.app.npc;

import java.util.List;

import com.game.draco.app.npc.config.ForceConfig;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.npc.domain.NpcTemplate;
import com.game.draco.app.npc.npcfunction.NpcFunctionSupport;

import sacred.alliance.magic.base.ForceRelation;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.core.exception.ServiceException;


public interface NpcApp extends Service,NpcFunctionSupport{
	
	public boolean reload() throws ServiceException;
	
	public NpcTemplate getNpcTemplate(String templateId);
	
	public NpcTemplate getBuildNpcTemplate(String templateId,int level);
	
	public boolean levelUp(NpcInstance npc);
	
	public boolean levelDown(NpcInstance npc);
	
	public List<String> getBuildNpcIdList();
	
	public int getBuildMaxLevel(String templateId);
	
	public ForceConfig getForceConfig (int forceId) ;
	
	public ForceRelation getNpcForceRelation(byte type1,byte type2);
}
