package com.game.draco.action.internal;

import com.game.draco.GameContext;
import com.game.draco.message.internal.C0054_ArenaMatchConfirmTimeoverMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.arena.ApplyInfo;
import sacred.alliance.magic.app.arena.Arena;
import sacred.alliance.magic.app.arena.ArenaMatch;
import sacred.alliance.magic.app.arena.ArenaMatchStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class ArenaMatchConfirmTimeoverAction extends BaseAction<C0054_ArenaMatchConfirmTimeoverMessage>{

	@Override
	public Message execute(ActionContext context,
			C0054_ArenaMatchConfirmTimeoverMessage reqMsg) {
		ArenaMatch match = reqMsg.getMatch();
		if(null == match){
			return null ;
		}
		ArenaMatchStatus status = match.getStatus();
		if(null == status){
			return null ;
		}
		if(ArenaMatchStatus.common != status){
			return null ;
		}
		//对没有进行选择的用户都选择确定
		Arena arena = GameContext.getArenaApp().getArena(match.getConfig().getActiveId());
		this.matchConfirm(arena, match.getTeam1(), match);
		this.matchConfirm(arena, match.getTeam2(), match);
		return null;
	}
	
	private void matchConfirm(Arena arena,ApplyInfo team,ArenaMatch match){
		for(String roleId : team.getAppliers()){
			if(match.getSelectYes().contains(roleId)){
				continue ;
			}
			RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleId);
			boolean select = true ;
			if(null == role){
				//不在线取消
				select = false ;
			}
			arena.matchConfirm(roleId, select);
		}
	}
}
