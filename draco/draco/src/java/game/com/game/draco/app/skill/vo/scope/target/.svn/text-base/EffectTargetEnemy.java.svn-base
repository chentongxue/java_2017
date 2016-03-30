package com.game.draco.app.skill.vo.scope.target;

import java.util.Collection;
import java.util.List;

import sacred.alliance.magic.base.ForceRelation;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;

import com.game.draco.app.skill.vo.scope.Area;
import com.google.common.collect.Lists;

public class EffectTargetEnemy extends EffectTargetLogic {
	private static EffectTargetEnemy instance = new EffectTargetEnemy();
	
	private EffectTargetEnemy(){
		
	}
	
	public static EffectTargetEnemy getInstance() {
		return instance;
	}
	
	@Override
	public List<AbstractRole> getTargetRole(AbstractRole attacker, Area area, byte targetNum) {
		List<AbstractRole> values = Lists.newArrayList();
		MapInstance mapInstance = attacker.getMapInstance();
		if(null == mapInstance){
			return values ;
		}
		Collection<? extends AbstractRole> list = null ;
		if(attacker.getRoleType() == RoleType.PLAYER || attacker.getRoleType() == RoleType.GODDESS){
			this.packValues(mapInstance.getNpcList(), values, attacker,ForceRelation.enemy,false, area, targetNum);
			this.packValues(mapInstance.getRoleList(), values, attacker,ForceRelation.enemy,false, area, targetNum);
		}else if(attacker.getRoleType() == RoleType.NPC){
			list = mapInstance.getRoleList();
			if(mapInstance.npcCanPk()){
				this.packValues(mapInstance.getNpcList(), values, attacker,ForceRelation.enemy,false, area, targetNum);
			}
			this.packValues(list, values, attacker,ForceRelation.enemy,false, area, targetNum);
		}
		return values ;
	}

}
