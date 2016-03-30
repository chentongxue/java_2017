package com.game.draco.app.speciallogic;

import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.Point;

import com.game.draco.app.speciallogic.config.SpecialLogic;
import com.game.draco.app.speciallogic.config.WorldLevelGroupLogic;

public interface SpecialLogicApp extends Service{

	SpecialLogic getSpecialLogic(String key);
	
	void logic(AbstractRole abstractRole,SpecialLogicType type,Point point);

	WorldLevelGroupLogic getWorldLevelGroupLogic(String key);

	String getNpcName(String npcname,byte logicType);
}
