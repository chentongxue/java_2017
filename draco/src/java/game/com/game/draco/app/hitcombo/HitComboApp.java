package com.game.draco.app.hitcombo;

import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

public interface HitComboApp extends Service{
	public void clearHitCombo(RoleInstance role);
	
	public void addHitCombo(RoleInstance role);
	
	public void pushHitComboConfig(RoleInstance role) ;
}
