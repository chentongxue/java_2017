package com.game.draco.app.skill.vo.scope.target;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import sacred.alliance.magic.base.ForceRelation;
import sacred.alliance.magic.vo.AbstractRole;

import com.game.draco.app.skill.vo.scope.Area;
import com.google.common.collect.Lists;

public abstract class EffectTargetLogic {
	public abstract List<AbstractRole> getTargetRole(AbstractRole attacker, Area area, byte targetNum);
	protected void packValues(Collection<? extends AbstractRole> list, List<AbstractRole> values,
			AbstractRole attacker,ForceRelation fr, boolean self, Area area, byte targetNum) {
		if (null == list) {
			return ;
		}
		if(isTargetNumMax(values, targetNum)) {
			return ;
		}
		
		List<AbstractRole> newList = Lists.newArrayList();
		newList.addAll(list);
		Collections.shuffle(newList);
		
		for (AbstractRole r : newList) {
			if (self && (r.getRoleId().equals(attacker.getRoleId()))) {
				continue;
			}
			if (fr != attacker.getForceRelation(r)) {
				continue;
			}
			if ((null != area)
					&& !area.inArea(attacker, r.getMapX(), r.getMapY(), r.getDir())) {
				continue;
			}
			values.add(r);
			if(isTargetNumMax(values, targetNum)) {
				return ;
			}
		}
	}
	
	/** 目标数限制 */
	private boolean isTargetNumMax(List<AbstractRole> targets, byte targetNum) {
		if(targetNum <= 0) {
			return false;
		}
		if(targets.size() >= targetNum) {
			return true;
		}
		return false;
	}
}
