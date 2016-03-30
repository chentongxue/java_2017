package com.game.draco.app.inslogic;

import java.util.List;

import sacred.alliance.magic.core.Service;

import com.game.draco.app.inslogic.config.InsLogic;

public interface InsLogicDataApp extends Service{

	List<InsLogic> getInsLogic(String key);
	
}
