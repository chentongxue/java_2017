package com.game.draco.app.skill.func;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.horse.domain.RoleHorse;
import com.game.draco.app.horse.domain.RoleHorseSkill;
import com.game.draco.app.skill.config.SkillSourceType;
import com.game.draco.app.skill.domain.RoleSkillStat;

public class HorseSkillLearnFunc extends SkillLearnFunc {
	
	public HorseSkillLearnFunc() {
		super(SkillSourceType.Horse);
	}

	private RoleHorse getRoleHorse(int roleId, int horseId) {
		return GameContext.getRoleHorseApp().getRoleHorse(roleId, horseId);
	}
	
	private RoleSkillStat getRoleSkillStat(int roleId, int horseId, short skillId) {
		RoleHorse horse = this.getRoleHorse(roleId, horseId);
		if(null == horse) {
			return null;
		}
		Map<Short,RoleSkillStat> map = GameContext.getRoleHorseApp().packRoleSkillStat(roleId, horse.getSkillList());
		return map.get(skillId);
	}
	
	@Override
	protected void deleteSkill(RoleInstance role, String parameter, short skillId) {
	}

	@Override
	public long getLastProcessTime(RoleInstance role, short skillId, String horseId) {
		RoleSkillStat stat = this.getRoleSkillStat(role.getIntRoleId(), Integer.parseInt(horseId), skillId);
		if(null == stat){
			return 0;
		}
		return stat.getLastProcessTime();
	}

	@Override
	public int getSkillLevel(RoleInstance role, short skillId, String horseId) {
		try {
			RoleSkillStat stat = this.getRoleSkillStat(role.getIntRoleId(), Integer.valueOf(horseId), skillId);
			if(null == stat){
				return 0;
			}
			return stat.getSkillLevel() ;
		} catch (Exception ex) {
			logger.error("HorseSkillLearnFunc.getSkillLevel error ", ex);
			return 0;
		}
	}

	@Override
	public List<Short> getSkillList(RoleInstance role, String horseId) {
		RoleHorse horse = this.getRoleHorse(role.getIntRoleId(), Integer.parseInt(horseId));
		if(null == horse) {
			return null;
		}
		List<Short> skillList = new ArrayList<Short>();
		Map<Short,RoleSkillStat> map = GameContext.getRoleHorseApp().packRoleSkillStat(role.getIntRoleId(), horse.getSkillList());
		if(map != null && !map.isEmpty()){
			skillList.addAll(map.keySet());
		}
		return skillList;
	}

	@Override
	public boolean hasLearnSkill(RoleInstance role, short skillId, String horseId) {
		return this.getSkillLevel(role, skillId, horseId) > 0 ;
	}

	@Override
	protected void saveSkill(RoleInstance role, String horseId, short skillId, int level) {
		RoleHorse roleHorse = this.getRoleHorse(role.getIntRoleId(), Integer.parseInt(horseId));
		if(null == roleHorse) {
			return ;
		}
		RoleHorseSkill horseSkill = new RoleHorseSkill();
		horseSkill.setHorseId(Integer.parseInt(horseId));
		horseSkill.setLevel((short)level);
		horseSkill.setSkillId(skillId);
		if(roleHorse.getState() == 1){
			//新学技能
			RoleSkillStat stat = new RoleSkillStat();
			stat.setSkillId(skillId);
			stat.setSkillLevel(level);
			stat.setRoleId(role.getRoleId());
			role.getSkillMap().put(skillId, stat);
		}
	}
	
	@Override
	public Result verifyInnerLevel(RoleInstance role, String parameter, int innerLevel) {
		return null;
	}
	
	@Override
	public void innerAddSkill(RoleInstance role, String horseId,short skillId, int level) {
		this.saveSkill(role, horseId, skillId, level);
	}
	
}
