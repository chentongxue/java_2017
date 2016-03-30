package com.game.draco.app.skill.vo.scope.target;

import java.util.List;

import sacred.alliance.magic.vo.AbstractRole;

import com.game.draco.app.skill.vo.scope.Area;
import com.google.common.collect.Lists;

public class EffectTargetMaster extends EffectTargetLogic {
	private static EffectTargetMaster instance = new EffectTargetMaster();
	
	private EffectTargetMaster() {
		
	}
	
	public static EffectTargetMaster getInstance() {
		return instance ;
	}
	
	@Override
	public List<AbstractRole> getTargetRole(AbstractRole attacker, Area area,
			byte targetNum) {
		List<AbstractRole> values = Lists.newArrayList();
		values.add(attacker.getMasterRole());
		return values ;
	}

}
