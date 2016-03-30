package com.game.draco.app.npc.npcfunction;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.vo.RoleInstance;

public interface NpcTeachApp {
	
	
	
	public Message getChooseMenuMessage(RoleInstance role, String param ) ;
	
	
	public void reload()throws ServiceException;

}
