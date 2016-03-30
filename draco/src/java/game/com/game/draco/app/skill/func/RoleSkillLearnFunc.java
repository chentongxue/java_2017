package com.game.draco.app.skill.func;

import java.util.List;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.skill.config.SkillSourceType;
import com.game.draco.app.skill.domain.RoleSkillStat;

public class RoleSkillLearnFunc extends SkillLearnFunc {

	public RoleSkillLearnFunc() {
		super(SkillSourceType.Role);
	}

	@Override
	protected void deleteSkill(RoleInstance role, String parameter, short skillId) {
		role.delSkillStat(skillId);
		GameContext.getBaseDAO().delete(RoleSkillStat.class, RoleSkillStat.ROLEID, role.getRoleId(), RoleSkillStat.SKILLID, skillId);
	}

	@Override
	public long getLastProcessTime(RoleInstance role, short skillId, String parameter) {
		RoleSkillStat stat = this.getSkillStat(role, skillId);
		if(null == stat){
			return 0;
		}
		return stat.getLastProcessTime();
	}

	@Override
	public int getSkillLevel(RoleInstance role, short skillId, String parameter) {
		RoleSkillStat stat = this.getSkillStat(role, skillId);
		if(null == stat){
			return 0;
		}
		return stat.getSkillLevel();
	}

	@Override
	public List<Short> getSkillList(RoleInstance role, String parameter) {
		//return GameContext.getSkillApp().getRoleLearnSkillList();
		return null ;
	}

	@Override
	public boolean hasLearnSkill(RoleInstance role, short skillId, String parameter) {
		return role.hasLearnSkill(skillId);
	}

	@Override
	protected void saveSkill(RoleInstance role, String parameter, short skillId, int level) {
		RoleSkillStat stat = role.getSkillStat(skillId);
		if(null == stat){
			stat = new RoleSkillStat();
			stat.setRoleId(role.getRoleId());
			stat.setSkillId(skillId);
			stat.setSkillLevel(level);
			role.addSkillStat(stat);
			GameContext.getBaseDAO().insert(stat);
		}else{
			stat.setSkillLevel(level);
			GameContext.getBaseDAO().update(stat);
		}
	}
	
	private RoleSkillStat getSkillStat(RoleInstance role, short skillId){
		return role.getSkillStat(skillId);
	}

	@Override
	public Result verifyInnerLevel(RoleInstance role, String parameter, int innerLevel) {
		return new Result().success();
	}
	
}
