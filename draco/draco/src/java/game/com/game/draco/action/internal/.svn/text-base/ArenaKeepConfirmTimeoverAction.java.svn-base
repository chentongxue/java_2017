package com.game.draco.action.internal;

import com.game.draco.GameContext;
import com.game.draco.message.internal.C0055_ArenaKeepConfirmTimeoverMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.arena.ArenaMatch;
import sacred.alliance.magic.app.arena.ArenaMatchStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.Util;

public class ArenaKeepConfirmTimeoverAction extends BaseAction<C0055_ArenaKeepConfirmTimeoverMessage>{

	@Override
	public Message execute(ActionContext context,
			C0055_ArenaKeepConfirmTimeoverMessage reqMsg) {
		ArenaMatch match = reqMsg.getMatch();
		if(null == match){
			return null ;
		}
		ArenaMatchStatus status = match.getStatus();
		if(null == status){
			return null ;
		}
		if(ArenaMatchStatus.sendkeep != status){
			return null ;
		}
		String roleId = match.getSendKeepRoleId();
		if(Util.isEmpty(roleId)){
			return null ;
		}
		//RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleId);
		//八缶年係
		GameContext.getArenaApp().getArena(match.getConfig().getActiveId()).applyKeep(roleId, false);
		return null ;
	}

}
