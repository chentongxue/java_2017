package com.game.draco.app.skill.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Data;
import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.base.SkillApplyResult;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;

import com.game.draco.GameContext;
import com.game.draco.app.skill.config.SkillApplyType;
import com.game.draco.app.skill.config.SkillAttackType;
import com.game.draco.app.skill.config.SkillDetail;
import com.game.draco.app.skill.config.SkillEffectType;
import com.game.draco.app.skill.config.SkillPassiveType;
import com.game.draco.app.skill.config.SkillSourceType;
import com.game.draco.app.skill.config.SkillTargetType;
import com.game.draco.app.skill.domain.RoleSkillStat;
import com.game.draco.app.skill.vo.scope.TargetScope;
import com.game.draco.message.item.SkillUpdateItem;
import com.game.draco.message.response.C0318_SkillUpdateRespMessage;

public @Data abstract class Skill {
	public Skill(short skillId){
		this.skillId = skillId ;
	}
	protected short skillId ;
	protected String name ;
	protected short iconId ;
	protected SkillSourceType skillSourceType;//系统来源
	protected SkillApplyType skillApplyType;//是否主动使用[0主动|1被动]
	protected SkillEffectType skillEffectType ;//技能效果类型
	protected SkillTargetType skillTargetType ;//目标类型(服务器端)[0任意|1敌方|2友方]
	protected SkillAttackType skillAttackType ;//攻击方式 [0默认 | 1普通攻击 | 2冲锋 | 3闪现 | 5恢复] 
	
	private boolean canLearnFromSystem = false ;//是否可从系统中学习到 
	protected boolean useGlobalCd = true;//是否复用公共cd 
	protected boolean triggerPassive = false ;//是否触发被动技能 
	
	
	private String affectSkills;//被动技能影响的主动技能(-1全部)
	
	/**普通攻击*/
	public boolean isNormalAttack(){
		if(skillAttackType == SkillAttackType.NormalAttack){
			return true;
		}
		return false;
	}
	
	/**主动技能*/
	public boolean isActiveSkill(){
		if(SkillApplyType.active == skillApplyType){
			return true;
		}
		return false;
	}
	/**获得用户CD*/
	public abstract int getCd(AbstractRole role);
	/**获得用户使用技能消耗的MP*/
	public abstract int getMp(AbstractRole role);
	/**获得用户使用技能消耗的HP*/
	public abstract int getHp(AbstractRole role);
	/**获得最大施法距离*/
	public abstract int getMaxUseRange(AbstractRole role);
	/**获得最小施法距离*/
	public abstract int getMinUseRange(AbstractRole role);
	/**获得技能描述*/
	public abstract String getDesc(AbstractRole role);
	/**判断用户是否符合使用技能条件*/
	public abstract SkillApplyResult condition(AbstractRole role);
	/**使用技能*/
	public abstract SkillApplyResult use(AbstractRole role);
	
	public abstract SkillApplyResult use(AbstractRole role,int delayTime);
	
	public abstract SkillApplyResult use(AbstractRole role,int delayTime,boolean systemTrigger,boolean judgeUseCond);
	
	public abstract SkillApplyResult use(SkillContext context) ;
	/**获得技能动作id*/
	public abstract byte getActionId(AbstractRole role) ;
	
	/**根据等级，获取技能配置信息*/
	public abstract SkillDetail getSkillDetail(int level);
	
	public abstract int getMaxLevel();
	
	/**获得技能对角色持久属性*/
	public abstract AttriBuffer getAttriBuffer(AbstractRole role);
	
	public abstract void init() ;
	
	public abstract boolean verify();
	
	/**
	 * 判断是否某种类型的被动技能
	 * @param skillPassiveType
	 * @return
	 */
	protected abstract boolean hasPassiveType(SkillPassiveType skillPassiveType);
	
	private void changeRoleAttribute(AbstractRole role,AttriBuffer preBuffer,AttriBuffer nowBuffer){
		if(null == preBuffer && null == nowBuffer){
			return ;
		}
		if(null == preBuffer && null != nowBuffer){
			GameContext.getUserAttributeApp().changeAttribute(role, nowBuffer);
			return ;
		}
		if(null != preBuffer && null == nowBuffer){
			GameContext.getUserAttributeApp().changeAttribute(role, preBuffer.reverse());
			return ;
		}
		GameContext.getUserAttributeApp().changeAttribute(
				role,nowBuffer.append(preBuffer.reverse()));
		role.getBehavior().notifyAttribute();
	}
	
	/**
	 * 技能对属性的影响、主动技能的同步更新（受被动技能影响的主动技能的修正值）
	 * @param role
	 */
	public void skillLevelChanged(AbstractRole role,AttriBuffer preBuffer){
		if(SkillApplyType.active == this.getSkillApplyType()){
			return ;
		}
		if(null == role || role.getRoleType() != RoleType.PLAYER){
			return ;
		}
		//先算属性修改
		AttriBuffer nowBuffer = null ;
		if(role.hasLearnSkill(this.getSkillId())){
			nowBuffer = this.getAttriBuffer(role);
		}
		this.changeRoleAttribute(role, preBuffer, nowBuffer);
		
		//计算对主动技能的影响
		if(Util.isEmpty(this.getAffectSkills())){
			return ;
		}
		//影响全部主动技能
		if(this.getAffectSkills().equals("-1")){
			sendSkillUpdateMessage(role, role.getSkillMap().keySet().toArray());
			return;
		}
		//影响部分主动技能
		sendSkillUpdateMessage(role, this.getAffectSkills().split(Cat.comma));
	}
	
	/**
	 * 技能更新通知消息（受被动技能影响的主动技能）
	 * @param role
	 * @param skills
	 */
	public static void sendSkillUpdateMessage(AbstractRole role, Object[] skills){
		if(null == role || null == skills || 0 == skills.length){
			return ;
		}
		List<SkillUpdateItem> skillResetList = new ArrayList<SkillUpdateItem>();
		for(Object item : skills){
			RoleSkillStat stat = role.getSkillMap().get(Short.valueOf(item.toString()));
			if(null == stat){
				continue;
			}
			Skill skill = GameContext.getSkillApp().getSkill(stat.getSkillId());
			if(null == skill || SkillApplyType.passive == skill.getSkillApplyType()){
				continue;
			}
			SkillUpdateItem i = buildSkillUpdateItem(role, stat);
			if(null != i){
				skillResetList.add(i);
			}
		}
		if(0 == skillResetList.size()){
			return ;
		}
		C0318_SkillUpdateRespMessage resp = new C0318_SkillUpdateRespMessage();
		resp.setSkillResetList(skillResetList);
		role.getBehavior().sendMessage(resp);
	}
	
	private static SkillUpdateItem buildSkillUpdateItem(AbstractRole role, RoleSkillStat stat){
		Skill skill = GameContext.getSkillApp().getSkill(stat.getSkillId());
		if(null == skill 
				|| SkillApplyType.passive == skill.getSkillApplyType()){
			return null;
		}
		SkillUpdateItem item = new SkillUpdateItem();
		item.setSkillId(skill.getSkillId());
		item.setSkillCD(skill.getCd(role));
		item.setConsumeHP(skill.getHp(role));
		item.setMinDistance((short) skill.getMinUseRange(role));
		item.setMaxDistance((short) skill.getMaxUseRange(role));
		SkillDetail detail = skill.getSkillDetail(stat.getSkillLevel());
		item.setPrepareArg(detail.getPrepareArg());
		return item;
	}
	
	public abstract int getSkillHurt(SkillContext context,int areaId) ;
	
	public abstract int getSkillHurtProb(SkillContext context,int areaId) ;
	
	public abstract int getSkillBuffProb(SkillContext context,int buffId,int areaId) ;
	
	public abstract Map<Integer, TargetScope> getTargetScopeMap(SkillContext context) ;
	
}
