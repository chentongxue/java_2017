package com.game.draco.app.skill.vo.scope.target;

import java.util.Collection;
import java.util.List;

import sacred.alliance.magic.base.ForceRelation;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;

import com.game.draco.app.skill.vo.scope.Area;
import com.google.common.collect.Lists;

public class EffectTargetFriend extends EffectTargetLogic {
	private static EffectTargetFriend instance = new EffectTargetFriend();
	
	private EffectTargetFriend() {
		
	}
	
	public static EffectTargetFriend getInstance() {
		return instance ;
	}
	
	@Override
	public List<AbstractRole> getTargetRole(AbstractRole attacker, Area area,
			byte targetNum) {
		List<AbstractRole> values = Lists.newArrayList();
		MapInstance mapInstance = attacker.getMapInstance();
		if(null == mapInstance){
			return values ;
		}
		Collection<? extends AbstractRole> list = null ;
		if(attacker.getRoleType() == RoleType.PLAYER){
			list = mapInstance.getRoleList();
		}else if(attacker.getRoleType() == RoleType.NPC){
			list = mapInstance.getNpcList();
			this.packValues(mapInstance.getRoleList(), values, attacker,ForceRelation.friend,
					false, area, targetNum);
		}
		this.packValues(list, values, attacker,ForceRelation.friend,false, area, targetNum);
		return values ;
	}

}
