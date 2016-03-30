package com.game.draco.app;

import sacred.alliance.magic.vo.RoleInstance;

public interface AppSupport {

	public int onLogin(RoleInstance role,Object context) ;
	public int onLogout(RoleInstance role,Object context) ;
	public int onCleanup(String roleId,Object context) ;
}
