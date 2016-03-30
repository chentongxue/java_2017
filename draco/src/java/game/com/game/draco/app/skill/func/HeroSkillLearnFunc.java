package com.game.draco.app.skill.func;

import java.util.List;
import java.util.Map;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.GoodsHero;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.app.skill.config.SkillDetail;
import com.game.draco.app.skill.config.SkillSourceType;
import com.game.draco.app.skill.domain.RoleSkillStat;
import com.game.draco.app.skill.vo.Skill;
import com.game.draco.message.item.HintHeroRulesItem;
import com.game.draco.message.item.HintSkillRulesItem;
import com.google.common.collect.Lists;

public class HeroSkillLearnFunc extends SkillLearnFunc {

	public HeroSkillLearnFunc() {
		super(SkillSourceType.Hero);
	}

	private RoleHero getRoleHero(String roleId, int heroId) {
		return GameContext.getUserHeroApp().getRoleHero(roleId, heroId);
	}

	private Map<Short, RoleSkillStat> getHeroSkillMap(RoleHero hero) {
		return hero.getSkillMap();
	}

	private RoleSkillStat getRoleSkillStat(String roleId, String heroId, short skillId) {
		int intHeroId = Integer.parseInt(heroId);
		RoleHero hero = this.getRoleHero(roleId, intHeroId);
		if (null == hero) {
			return null;
		}
		Map<Short, RoleSkillStat> map = hero.getSkillMap();
		return map.get(skillId);
	}

	private void saveRoleHero(RoleHero hero) {
		GameContext.getHeroApp().saveRoleHero(hero);
	}

	@Override
	protected void deleteSkill(RoleInstance role, String heroId, short skillId) {
		int intHeroId = Integer.parseInt(heroId);
		RoleHero hero = this.getRoleHero(role.getRoleId(), intHeroId);
		if (null == hero) {
			return;
		}
		Map<Short, RoleSkillStat> map = this.getHeroSkillMap(hero);
		if (null == map.remove(skillId)) {
			return;
		}
		this.saveRoleHero(hero);
	}

	@Override
	public long getLastProcessTime(RoleInstance role, short skillId, String heroId) {
		RoleSkillStat stat = this.getRoleSkillStat(role.getRoleId(), heroId, skillId);
		if (null == stat) {
			return 0;
		}
		return stat.getLastProcessTime();
	}

	@Override
	public int getSkillLevel(RoleInstance role, short skillId, String heroId) {
		RoleSkillStat stat = this.getRoleSkillStat(role.getRoleId(), heroId, skillId);
		if (null == stat) {
			return 0;
		}
		return stat.getSkillLevel();
	}

	public int getSkillAddLevel(RoleInstance role, short skillId, String heroId) {
		RoleSkillStat stat = this.getRoleSkillStat(role.getRoleId(), heroId, skillId);
		if (null == stat) {
			return 0;
		}
		return stat.getAddSkillLevel();
	}

	@Override
	public List<Short> getSkillList(RoleInstance role, String heroId) {
		int intHeroId = Integer.parseInt(heroId);
		RoleHero hero = this.getRoleHero(role.getRoleId(), intHeroId);
		if (null == hero) {
			return null;
		}
		GoodsHero goodsHero = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class, hero.getHeroId());
		if (null == goodsHero) {
			return null;
		}
		return goodsHero.getSkillIdList();
	}

	@Override
	public int getAstaff(RoleInstance role, String heroId) {
		int intHeroId = Integer.parseInt(heroId);
		return GameContext.getHeroApp().getAstaff(role.getRoleId(), intHeroId);
	}

	@Override
	public boolean hasLearnSkill(RoleInstance role, short skillId, String heroId) {
		return this.getSkillLevel(role, skillId, heroId) > 0;
	}

	@Override
	protected void saveSkill(RoleInstance role, String heroId, short skillId, int level) {
		int intHeroId = Integer.parseInt(heroId);
		RoleHero hero = this.getRoleHero(role.getRoleId(), intHeroId);
		if (null == hero) {
			return;
		}
		Map<Short, RoleSkillStat> map = hero.getSkillMap();
		RoleSkillStat stat = map.get(skillId);
		if (null != stat) {
			stat.setSkillLevel(level);
		} else {
			// 新学技能
			stat = new RoleSkillStat();
			stat.setSkillId(skillId);
			stat.setSkillLevel(level);
			stat.setRoleId(role.getRoleId());
			map.put(skillId, stat);
			// 如果是当前出战英雄，需要加入角色技能Map
			if (1 == hero.getOnBattle()) {
				role.getSkillMap().put(skillId, stat);
			}
		}
		//重新计算英雄的战斗力
		hero.setScore(GameContext.getHeroApp().getBattleScore(hero));
		this.saveRoleHero(hero);
	}

	@Override
	public Result verifyInnerLevel(RoleInstance role, String heroId, int innerLevel) {
		Result result = new Result();
		if (innerLevel <= 0) {
			return result.success();
		}
		int intHeroId = Integer.parseInt(heroId);
		RoleHero hero = this.getRoleHero(role.getRoleId(), intHeroId);
		if (null == hero) {
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result;
		}
		if (innerLevel > hero.getLevel()) {
			result.setInfo(GameContext.getI18n().messageFormat(TextId.Hero_level_must_reach, String.valueOf(innerLevel)));
			return result;
		}
		return result.success();
	}

	@Override
	protected boolean isSendActiveSkillLevelup(RoleInstance role, String heroId, Skill skill) {
		// 当前出战的技能才需要
		RoleHero onHero = GameContext.getUserHeroApp().getOnBattleRoleHero(role.getRoleId());
		if (null == onHero) {
			return false;
		}
		return String.valueOf(onHero.getHeroId()).equals(heroId);
	}

	@Override
	public Result learnSkill(RoleInstance role, short skillId, String parameter) {
		Result result = super.learnSkill(role, skillId, parameter);
		if (!result.isSuccess()) {
			return result;
		}
        GameContext.getHeroApp().syncBattleScore(role,Integer.parseInt(parameter),false);
		// 红点提示规则变化
		this.pushHintSkillChange(role, skillId, GameContext.getUserHeroApp().getRoleHero(role.getRoleId(), Integer.parseInt(parameter)));
		return result;
	}

	private void pushHintSkillChange(RoleInstance role, short skillId, RoleHero hero) {
		try {
			HintSkillRulesItem skillItem = new HintSkillRulesItem();
			skillItem.setSkillId(skillId);
			Skill skill = GameContext.getSkillApp().getSkill(skillId);
			int skillLevel = 0;
			RoleSkillStat skillStat = hero.getSkillMap().get(skillId);
			if (null != skillStat) {
				skillLevel = skillStat.getSkillLevel();
			}
			SkillDetail skillDetail = skill.getSkillDetail(skillLevel + 1);
			if (null == skillDetail) {
				skillItem.setLevel(Short.MAX_VALUE);
			} else {
				skillItem.setLevel((short) skillDetail.getLevel());
			}
			List<HintSkillRulesItem> hintSkillRulesList = Lists.newArrayList();
			hintSkillRulesList.add(skillItem);
			HintHeroRulesItem item = new HintHeroRulesItem();
			item.setHeroId(hero.getHeroId());
			item.setHintSkillRulesList(hintSkillRulesList);
			GameContext.getHintApp().pushHintSkillChange(role, item);
		} catch (Exception ex) {
			logger.error("HeroSkillLearnFunc.pushHintSkillChange error!", ex);
		}
	}
}
