package com.game.draco.app.skill.func;

import java.util.List;
import java.util.Map;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.GoodsGoddess;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.goddess.domain.RoleGoddess;
import com.game.draco.app.skill.config.SkillSourceType;
import com.game.draco.app.skill.domain.RoleSkillStat;

public class GoddessSkillLearnFunc extends SkillLearnFunc {
	
	public GoddessSkillLearnFunc() {
		super(SkillSourceType.Goddess);
	}
	
	private RoleGoddess getRoleGoddess(String roleId, int goddessId) {
		return GameContext.getUserGoddessApp().getRoleGoddess(roleId, goddessId);
	}

	@Override
	protected void deleteSkill(RoleInstance role, String parameter, short skillId) {
		// TODO Auto-generated method stub
	}

	@Override
	public long getLastProcessTime(RoleInstance role, short skillId,
			String goddesId) {
		RoleSkillStat stat = this.getRoleSkillStat(role.getRoleId(), Integer.parseInt(goddesId), skillId);
		if(null == stat){
			return 0;
		}
		return stat.getLastProcessTime();
	}
	
	private RoleSkillStat getRoleSkillStat(String roleId, int goddessId, short skillId) {
		RoleGoddess goddess = this.getRoleGoddess(roleId, goddessId);
		if(null == goddess) {
			return null;
		}
		Map<Short,RoleSkillStat> map = goddess.getSkillMap();
		return map.get(skillId);
	}

	@Override
	public int getSkillLevel(RoleInstance role, short skillId, String goddessId) {
		try {
			RoleSkillStat stat = this.getRoleSkillStat(role.getRoleId(), Integer.valueOf(goddessId), skillId);
			if(null == stat){
				return 0;
			}
			return stat.getSkillLevel() ;
		} catch (Exception ex) {
			logger.error("GoddessSkillLearnFunc.getSkillLevel error ", ex);
			return 0;
		}
	}

	@Override
	public List<Short> getSkillList(RoleInstance role, String parameter) {
		GoodsGoddess goodsGoddess = GameContext.getGoodsApp()
				.getGoodsTemplate(GoodsGoddess.class, Integer.parseInt(parameter));
		if(null == goodsGoddess) {
			return null;
		}
		return goodsGoddess.getSkillIdList();
	}

	@Override
	public boolean hasLearnSkill(RoleInstance role, short skillId,
			String goddessId) {
		return this.getSkillLevel(role, skillId, goddessId) > 0 ;
	}

	@Override
	protected void saveSkill(RoleInstance role, String goddessId,
			short skillId, int level) {
		RoleGoddess goddess = this.getRoleGoddess(role.getRoleId(), Integer.valueOf(goddessId));
		if(null == goddess) {
			return ;
		}
		Map<Short,RoleSkillStat> map = goddess.getSkillMap();
		RoleSkillStat stat = map.get(skillId);
		if(null != stat){
			stat.setSkillLevel(level);
		}
		GameContext.getGoddessApp().saveRoleGoddess(goddess);
	}

	@Override
	public Result verifyInnerLevel(RoleInstance role, String parameter,
			int innerLevel) {
		Result result = new Result();
		if(innerLevel<=0){
			return result.success();
		}
		RoleGoddess goddess = this.getRoleGoddess(role.getRoleId(), Integer.valueOf(parameter)) ;
		if(null == goddess){
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result ;
		}
		if(innerLevel > goddess.getLevel()){
			result.setInfo(GameContext.getI18n().messageFormat(
					TextId.Goddess_level_must_reach, innerLevel));
			return result ;
		}
		return result.success();
	}

}
