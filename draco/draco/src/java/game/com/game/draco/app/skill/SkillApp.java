package com.game.draco.app.skill;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.game.draco.app.skill.config.SkillHurtRemit;
import com.game.draco.app.skill.domain.RoleSkillStat;
import com.game.draco.app.skill.func.SkillLearnFunc;
import com.game.draco.app.skill.vo.Skill;
import com.game.draco.message.item.SkillShowItem;
import com.google.common.collect.Maps;

import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.vo.RoleInstance;

public abstract class SkillApp implements Service{

	protected static Map<Short, Skill> skillMap = Maps.newConcurrentMap();
	
	public abstract Skill getSkill(short skillId);
	
	public abstract Map<Short,Skill> getSkillMap();
	
	public static void registerSkill(Skill skill){
		//在注册技能的时候进行验证
		//避免运行时出现不必要异常
		if(null == skill){
			return ;
		}
		if(!skill.verify()){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("skill: " + skill.getSkillId() +" can't verify success,pls check the script file");
		}
		skill.init();
		skillMap.put(skill.getSkillId(), skill);
	}
	
	public abstract Collection<Skill> getAllSkill();
	
	
	public abstract List<Short> getRoleLearnSkillList();
	
	public abstract boolean reLoad();
	
	public abstract List<Short> getAutoLearnSkills(int roleLevel);
	
	public abstract SkillHurtRemit getSkillHurtRemit(int roleLevel);
	
	public abstract List<SkillShowItem> getSkillShowItemList(RoleInstance role, SkillLearnFunc learnFunc, String parameter);
	
	public abstract SkillShowItem getSkillShowItem(RoleInstance role, SkillLearnFunc learnFunc, String parameter, short skillId);
	
	public abstract String skillIdLevelString(Map<Short,RoleSkillStat> map);
	
	public abstract int getSkillBattleScore(Map<Short,RoleSkillStat> map);
	
    
	/**
	 * 根据buffId获取技能信息
	 * @param buffId
	 * @param role
	 * @return
	 */
	//public abstract SkillDetail getSkillDetailByBuffId(int buffId, AbstractRole role);
	
	//public abstract Map<Integer, Map<Integer, SkillDetail>> getSkillDetailMap();
	
	
}
