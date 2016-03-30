package com.game.draco.action.internal;

import java.util.Collection;

import com.game.draco.GameContext;
import com.game.draco.message.internal.C0065_ActiveSiegeAwardInternalMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class ActiveSiegeAwardInternalAction extends BaseAction<C0065_ActiveSiegeAwardInternalMessage>{
	@Override
	public Message execute(ActionContext context,
			C0065_ActiveSiegeAwardInternalMessage reqMsg) {
		
		try {
			Collection<RoleInstance> roleList = reqMsg.getRoleList();
			if(null == roleList || roleList.size() == 0){
				return null;
			}
			if(reqMsg.isSuccess()){
				GameContext.getActiveSiegeApp().winAward(reqMsg.getActive(), roleList);
				return null;
			}
			GameContext.getActiveSiegeApp().failAward(reqMsg.getActive(), roleList, 
					reqMsg.getCurrHp(), reqMsg.getMaxHp());
			
		} catch (Exception ex) {
			logger.error("",ex);
		}
		return null;
	}
}
