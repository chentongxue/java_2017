package com.game.draco.app.forward.logic;

import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.forward.config.ForwardConfig;


public interface ForwardLogic {
	
	public void forward(RoleInstance role,ForwardConfig config) ;
	
	public ForwardLogicType getForwardLogicType() ;
}
