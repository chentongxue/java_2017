package com.game.draco.app.skill.func;

import java.util.List;
import java.util.Map;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.pet.domain.GoodsPet;
import com.game.draco.app.pet.domain.RolePet;
import com.game.draco.app.skill.config.SkillSourceType;
import com.game.draco.app.skill.domain.RoleSkillStat;

public class PetSkillLearnFunc extends SkillLearnFunc {

	public PetSkillLearnFunc() {
		super(SkillSourceType.Pet);
	}

	private RolePet getRolePet(String roleId, int petId) {
		return GameContext.getUserPetApp().getRolePet(roleId, petId);
	}

	@Override
	protected void deleteSkill(RoleInstance role, String parameter, short skillId) {
		// TODO Auto-generated method stub
	}

	@Override
	public long getLastProcessTime(RoleInstance role, short skillId, String goddesId) {
		RoleSkillStat stat = this.getRoleSkillStat(role.getRoleId(), Integer.parseInt(goddesId), skillId);
		if (null == stat) {
			return 0;
		}
		return stat.getLastProcessTime();
	}

	private RoleSkillStat getRoleSkillStat(String roleId, int petId, short skillId) {
		RolePet pet = this.getRolePet(roleId, petId);
		if (null == pet) {
			return null;
		}
		Map<Short, RoleSkillStat> map = pet.getSkillMap();
		return map.get(skillId);
	}

	@Override
	public int getSkillLevel(RoleInstance role, short skillId, String goddessId) {
		try {
			RoleSkillStat stat = this.getRoleSkillStat(role.getRoleId(), Integer.valueOf(goddessId), skillId);
			if (null == stat) {
				return 0;
			}
			return stat.getSkillLevel();
		} catch (Exception ex) {
			logger.error("GoddessSkillLearnFunc.getSkillLevel error ", ex);
			return 0;
		}
	}

	@Override
	public List<Short> getSkillList(RoleInstance role, String parameter) {
		GoodsPet goodsPet = GameContext.getGoodsApp().getGoodsTemplate(GoodsPet.class, Integer.parseInt(parameter));
		if (null == goodsPet) {
			return null;
		}
		return goodsPet.getSkillList();
	}

	@Override
	public boolean hasLearnSkill(RoleInstance role, short skillId, String goddessId) {
		return this.getSkillLevel(role, skillId, goddessId) > 0;
	}

	@Override
	protected void saveSkill(RoleInstance role, String goddessId, short skillId, int level) {
		RolePet goddess = this.getRolePet(role.getRoleId(), Integer.valueOf(goddessId));
		if (null == goddess) {
			return;
		}
		Map<Short, RoleSkillStat> map = goddess.getSkillMap();
		RoleSkillStat stat = map.get(skillId);
		if (null != stat) {
			stat.setSkillLevel(level);
		}
	}

	@Override
	public Result verifyInnerLevel(RoleInstance role, String parameter, int innerLevel) {
		Result result = new Result();
		if (innerLevel <= 0) {
			return result.success();
		}
		RolePet pet = this.getRolePet(role.getRoleId(), Integer.valueOf(parameter));
		if (null == pet) {
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result;
		}
		if (innerLevel > pet.getLevel()) {
//			result.setInfo(GameContext.getI18n().messageFormat(TextId.Goddess_level_must_reach, innerLevel));
			return result;
		}
		return result.success();
	}

}
