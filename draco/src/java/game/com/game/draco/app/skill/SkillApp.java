package com.game.draco.app.skill;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.buff.BuffContext;
import com.game.draco.app.npc.domain.NpcTemplate;
import com.game.draco.app.skill.domain.RoleSkillStat;
import com.game.draco.app.skill.func.SkillLearnFunc;
import com.game.draco.app.skill.vo.Skill;
import com.game.draco.message.item.RoleSkillItem;
import com.game.draco.message.item.SkillShowItem;
import com.google.common.collect.Maps;

public abstract class SkillApp implements Service{
	
	//private static final float  TEN_THOUSAND_F = SkillFormula.TEN_THOUSAND_F ;

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
	
	
	public abstract boolean reLoad();
	
	public abstract List<Short> getAutoLearnSkills(int roleLevel);
	
	public abstract List<SkillShowItem> getSkillShowItemList(RoleInstance role, SkillLearnFunc learnFunc, String parameter);
	
	public abstract SkillShowItem getSkillShowItem(RoleInstance role, SkillLearnFunc learnFunc, String parameter, short skillId);
	
	public abstract String skillIdLevelString(Map<Short,RoleSkillStat> map);
	
	public abstract int getSkillBattleScore(Map<Short,RoleSkillStat> map);
	/**
	 * 角色变身修改技能
	 * @param role 
	 * @param skillIds
	 */
	public abstract void heroMorphChangeSkill(BuffContext context, short[] skillIds);
	/**
	 * 角色变身结束恢复技能
	 * @param context
	 */
	public abstract void heroMorphRecoverSkill(BuffContext context);
	
	/**
	 * 角色或npc改变外形
	 * @param context
	 * @param resId
	 */
	public abstract void roleChangeShape(AbstractRole role, int resId, int effectTime);
	
	/**
	 * 角色或npc恢复外形
	 * @param context
	 */
	public abstract void roleRecoverShape(AbstractRole role);
	
	/**
	 * 角色或npc变色
	 * @param context
	 * @param color
	 */
	public abstract void roleChangeColor(AbstractRole role, int color, int effectTime);
	
	/**
	 * 角色或npc恢复颜色
	 * @param context
	 */
	public abstract void roleRecoverColor(AbstractRole role);
	
	/**
	 * 角色或npc缩放
	 * @param context
	 * @param zoom
	 */
	public abstract void roleChangeZoom(AbstractRole role, int zoom, int effectTime);
	
	/**
	 * 角色或npc恢复大小
	 * @param context
	 */
	public abstract void roleRecoverZoom(AbstractRole role);
	
	public abstract List<RoleSkillItem> getRoleSkillItem(RoleInstance role, 
			Collection<RoleSkillStat> skillStats);
	
	/**
	 * 角色变身
	 * @param role
	 * @param resId 外形id
	 * @param color 颜色 -1为不改变
	 * @param zoom 缩放 -1为不改变
	 * @param effectTime 持续时间
	 */
	public abstract void npcMorph(AbstractRole role, int resId, int color, int zoom,
			int effectTime);
	
	/**
	 * 角色分身 攻击力=本体攻击*A% + B
	 * @param role
	 * @param atkRate 分身攻击力=本体的攻击力 * attrRate
	 * @param atkValue 分身攻击力B,由脚本提供
	 * @param number 分身个数
	 * @param radius 分身范围(已本体位置为中心,radius为半径的圆)
	 * @param skillId 分身技能id
	 * @param skillLv 分身技能等级
	 * @param lifeTime 分身持续时间
	 * @param buffId 给分身加的buffId
	 */
	public abstract void roleCopySelf(RoleInstance role, int atkRate, int atkValue, int number, int radius,
			short skillId, int skillLv, int lifeTime, short buffId);
	
	/**
	 * 角色分身使用技能
	 * @param role
	 * @param targetRole
	 */
	public abstract void roleCopyAttack(RoleInstance role, AbstractRole targetRole);
	
	/**
	 * 根据角色创建分身的模版
	 * @param role
	 * @param atkRate
	 * @param atkValue
	 * @param resId
	 * @param seriesId
	 * @param gearId
	 * @return
	 */
	public abstract NpcTemplate createRoleCopyNpcTemplate(RoleInstance role, int atkRate, int atkValue, int resId, 
			byte seriesId, byte gearId, int lifeTime) ;
	
	public abstract Map<Short,short [] > getAllSkillMusicConfig() ;
}
