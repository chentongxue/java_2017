package com.game.draco.app.skill.vo.scope.target;

import java.util.List;

import sacred.alliance.magic.vo.AbstractRole;

import com.game.draco.app.skill.vo.scope.Area;
import com.google.common.collect.Lists;

public class EffectTargetSelf extends EffectTargetLogic {
	private static EffectTargetSelf instance = new EffectTargetSelf();
	
	private EffectTargetSelf() {
		
	}
	
	public static EffectTargetSelf getInstance() {
		return instance;
	}
	
	@Override
	public List<AbstractRole> getTargetRole(AbstractRole attacker, Area area, byte targetNum) {
		List<AbstractRole> values = Lists.newArrayList();
		if(null == area || area.inArea(attacker, attacker.getMapX(),attacker.getMapY(), attacker.getDir())){
			values.add(attacker);
		}
		return values ;
	}

}
