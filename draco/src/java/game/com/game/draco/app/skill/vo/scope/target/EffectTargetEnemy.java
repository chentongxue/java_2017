package com.game.draco.app.skill.vo.scope.target;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import sacred.alliance.magic.base.ForceRelation;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.skill.vo.scope.Area;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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
		MapInstance mapInstance = attacker.getMasterRole().getMapInstance();
		if(null == mapInstance){
			return values ;
		}
		
		Map<Integer,NpcInstance> npcMap = Maps.newHashMap();
		Collection<NpcInstance> tempNpcList = mapInstance.getNpcList();
		for(NpcInstance npc : tempNpcList){
			npcMap.put(npc.getIntRoleId(), npc);
		}
		Collection<? extends AbstractRole> npcList = npcMap.values();
		
		Map<Integer,RoleInstance> roleMap = Maps.newHashMap();
		Collection<RoleInstance> tempRoleList = mapInstance.getRoleList();
		for(RoleInstance r : tempRoleList){
			roleMap.put(r.getIntRoleId(), r);
		}
		Collection<? extends AbstractRole> roleList = roleMap.values();
		
		
		if(attacker.getRoleType() == RoleType.PLAYER || attacker.getRoleType() == RoleType.PET
				|| attacker.getRoleType() == RoleType.COPY){
			this.packValues(npcList, values, attacker,ForceRelation.enemy,true, area, targetNum);
			this.packValues(roleList, values, attacker,ForceRelation.enemy,true, area, targetNum);
		}else if(attacker.getRoleType() == RoleType.NPC){
			if(mapInstance.npcCanPk()){
				this.packValues(npcList, values, attacker,ForceRelation.enemy,true, area, targetNum);
			}
			this.packValues(roleList, values, attacker,ForceRelation.enemy,true, area, targetNum);
		}
		return values ;
	}

}
