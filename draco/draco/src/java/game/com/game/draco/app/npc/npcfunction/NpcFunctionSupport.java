package com.game.draco.app.npc.npcfunction;

import java.util.List;

import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.message.item.NpcFunctionItem;

import sacred.alliance.magic.vo.RoleInstance;

public interface NpcFunctionSupport {
	List<NpcFunctionItem> getNpcFunction(RoleInstance role,NpcInstance npc) ;
}
