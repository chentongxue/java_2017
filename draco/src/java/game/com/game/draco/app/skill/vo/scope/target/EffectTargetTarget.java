package com.game.draco.app.skill.vo.scope.target;

import java.util.List;

import sacred.alliance.magic.vo.AbstractRole;

import com.game.draco.app.skill.vo.scope.Area;
import com.google.common.collect.Lists;

public class EffectTargetTarget extends EffectTargetLogic {
	public static EffectTargetTarget instance = new EffectTargetTarget();
	
	private EffectTargetTarget() {

	}
	
	public static EffectTargetTarget getInstance() {
		return instance;
	}

	@Override
	public List<AbstractRole> getTargetRole(AbstractRole attacker, Area area, byte targetNum) {
		List<AbstractRole> values = Lists.newArrayList();
		AbstractRole target = attacker.getTarget();
		if(null == target){
			return values ;
		}
		if(null == area || area.inArea(attacker, target.getMapX(),target.getMapY(), target.getDir())){
			values.add(target);
		}
		return values ;
	}

}
