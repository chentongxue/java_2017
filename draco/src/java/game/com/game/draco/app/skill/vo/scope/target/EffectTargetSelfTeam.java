package com.game.draco.app.skill.vo.scope.target;

import java.util.Collection;
import java.util.List;

import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.skill.vo.scope.Area;
import com.game.draco.app.team.Team;
import com.google.common.collect.Lists;

public class EffectTargetSelfTeam extends EffectTargetLogic {
	private static EffectTargetSelfTeam instance = new EffectTargetSelfTeam();

	private EffectTargetSelfTeam() {
		
	}
	
	public static EffectTargetSelfTeam getInstance() {
		return instance;
	}
	
	@Override
	public List<AbstractRole> getTargetRole(AbstractRole attacker, Area area,
			byte targetNum) {
		List<AbstractRole> values = Lists.newArrayList();
		Team team = ((RoleInstance)attacker).getTeam();
		if(null == team){
			values.add(attacker);
			return values;
		}
		Collection<AbstractRole> roleList = team.getMembers();
		if(null == area){
			values.addAll(roleList);
			return values ;
		}
		for(AbstractRole role:roleList){
			if(!area.inArea(attacker, role.getMapX(), role.getMapY(), role.getDir())){
				continue ;
			}
			values.add(role);
		}
		return values ;
	}

}
