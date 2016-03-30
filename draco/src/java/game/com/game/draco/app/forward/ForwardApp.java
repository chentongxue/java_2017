package com.game.draco.app.forward;

import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

public interface ForwardApp extends Service{

	public void forward(RoleInstance role,short forwardId) ;
}
