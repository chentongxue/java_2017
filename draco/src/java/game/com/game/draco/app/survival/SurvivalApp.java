package com.game.draco.app.survival;

import java.util.Map;

import sacred.alliance.magic.core.Service;

import com.game.draco.app.survival.config.SurvivalBase;
import com.game.draco.app.survival.config.SurvivalMail;
import com.game.draco.app.survival.config.SurvivalReward;

public interface SurvivalApp extends Service{
	
	SurvivalBase getSurvivalBase();
	
	Map<Byte,SurvivalReward> getSurvivalRewardMap();
	
	Map<Byte,SurvivalMail> getSurvivalMailMap();
}
