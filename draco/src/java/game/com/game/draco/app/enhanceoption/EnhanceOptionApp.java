package com.game.draco.app.enhanceoption;

import java.util.Collection;
import java.util.List;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;
import com.game.draco.app.enhanceoption.config.*;
import com.game.draco.app.enhanceoption.type.EnhanceOptionType;
import com.game.draco.message.item.EnhanceOptionItem;
public interface EnhanceOptionApp extends Service{

	public LevelupEnhanceConfig getLevelupEnhanceConfig();
	//use
	public List<EnhanceOptionItem> getEnhanceOptionItems(RoleInstance role, EnhanceOptionType tp);
	public Message getEnhanceOptionLevelUpMessage(RoleInstance role);
}
