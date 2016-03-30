package com.game.draco.app.skill.vo;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;

import com.game.draco.GameContext;
import com.game.draco.app.buff.Buff;
import com.game.draco.app.buff.EffectType;
import com.game.draco.app.buff.RoleBuffReform;
import com.game.draco.app.skill.config.SkillApplyType;
import com.game.draco.app.skill.config.SkillDetail;
import com.game.draco.app.skill.config.SkillPassiveType;
import com.game.draco.app.skill.domain.SAttrC;
import com.game.draco.app.skill.domain.SBuffC;
import com.game.draco.app.skill.domain.SHurt;
import com.game.draco.app.skill.vo.SkillContext.AttrSource;
import com.game.draco.app.skill.vo.scope.TargetScope;

public class SkillReformActive extends SkillActive{

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public SkillReformActive(short skillId) {
		super(skillId);
		this.setSkillApplyType(SkillApplyType.active);
	}

	
	@Override
	public AttriBuffer getAttriBuffer(AbstractRole role){
		return null ;
	}
	
	@Override
	protected  boolean hasPassiveType(SkillPassiveType skillPassiveType){
		//主动技能没有被动效果
		return false ;
	}
	
	@Override
	protected void notifyMessage(AbstractRole role,  Set<Integer> targetIdSet, SkillContext context){
		super.notifyMessage(role, targetIdSet, context);
	}
	
	@Override
	protected void passiveEffect(SkillContext context, SkillPassiveType passiveType) {
		super.passiveEffect(context, passiveType);
	}
	
	@Override
	protected AbstractRole getSkillOwner(SkillContext context){
		return context.getAttacker() ;
	}

	@Override
	public Map<Integer, TargetScope> getTargetScopeMap(SkillContext context) {
		return super.getTargetScopeMap(context);
	}

	@Override
	protected void getSkillEffect(SkillContext context) {
		SkillDetail detail = this.getSkillDetail(context.getSkillLevel());		
		AttrSource source = null;
		SkillAdjust adjust = null;
		int attrValue = 0;
		if(!Util.isEmpty(detail.getSkillAttriMap())){
			List<SAttrC> attrList = detail.getSkillAttriMap().get(context.getAreaId());
			if(!Util.isEmpty(attrList)){
				for(SAttrC attr : attrList){
					if(attr.getTargetType() == 0){
						attrValue = context.getAttacker().get(attr.getAttrType()) ; 
						source = AttrSource.attacker;
					}else{
						attrValue = context.getDefender().get(attr.getAttrType()) ; 
						source = AttrSource.defender;
					}
					int value = Util.getAbc(attr.getA(),attr.getB(),attr.getC(),attr.getD(),attrValue,false,true);
					if(attr.getReduce() == 0){
						adjust = new SkillAdjust(SkillAdjust.Type.add,-value);
					}else{
						adjust = new SkillAdjust(SkillAdjust.Type.add,value);
					}
					if(context.getAttackType() == AttackType.CRIT){
						context.appendcritAtkProbChange(value);
					}
					context.appendAdjust(AttributeType.get(attr.getModifyTargetAttr()),adjust,source);
				}
			}
		}
		if(!Util.isEmpty(detail.getSHurtMap())){
			SHurt skillHurt = detail.getSHurtMap().get(context.getAreaId());
			if(skillHurt != null){
					
				if(skillHurt.getTargetType() == 0){
					source = AttrSource.attacker;
					attrValue = context.getAttacker().get(skillHurt.getAttrType()) ; 
				}else{
					source = AttrSource.defender;
					attrValue = context.getDefender().get(skillHurt.getAttrType()) ; 
				}
				int value = Util.getAbc(skillHurt.getA(),skillHurt.getB(),skillHurt.getC(),skillHurt.getD(),attrValue,false,true);
				//如果是减
				if(skillHurt.getReduce() == 0){
					context.appendSkillHurt(SkillHurtType.getType(skillHurt.getHurtType()), skillHurt.getA(), value,source,AttributeType.get(skillHurt.getAttrType()));
				}else{
					if(skillHurt.getModifyTargetType() == 0){
						source = AttrSource.attacker;
					}else{
						source = AttrSource.defender;
					}
					context.appendHpChange(-value, source);
				}
			}
		}
		
		int execSkillTimes = context.getExecSkillTimes();
		if(detail.isGuideSkill()){
			if(execSkillTimes > 0){
				return;
			}
		}
		
		if(!Util.isEmpty(detail.getSkillBuffMap())){
			List<SBuffC> skillBuffList = detail.getSkillBuffMap().get(context.getAreaId());
			if(!Util.isEmpty(skillBuffList)){
				int random = 0;
				for(SBuffC skillBuff : skillBuffList){
					Buff buff = GameContext.getBuffApp().getBuff(skillBuff.getBuffId());
					if(buff == null){
						continue;
					}
					
					int probability = Util.getAbc(0,skillBuff.getB(),skillBuff.getC(),skillBuff.getD(),0,true,true);
					
					if(buff.isRandom()){
						if(random == 0){
							random = context.random();
						}
					}
					if(random > probability){
						return;
					}
					
					if(buff.getBuffType() == 0){
						if(buff.getEffectType() != EffectType.guideSkill && buff.getSkillContinue() == 0){
							if(skillBuff.getTargetType() == 1){
								context.appendBuff(skillBuff.getBuffId(), context.getSkillLevel(), 0, probability);
							}
						}
					}
						
					if(buff.getBuffType() == 1){
						int mapX = 0, mapY = 0;
						
						if(AttrSource.attacker == source){
							mapX = context.getAttacker().getMapX();
							mapY = context.getAttacker().getMapY();
						}else{
							mapX = context.getDefender().getMapX();
							mapY = context.getDefender().getMapY();
						}
						context.appendMapBuff(skillBuff.getBuffId(), context.getSkillLevel(),probability,mapX,mapY);
					}
				}
			}
		}
	}
	
	@Override
	protected void getAfterAttackEffect(SkillContext context) {
	}

	@Override
	protected void getAttackerEffect(SkillContext context) {
		if (context.isSystemTrigger()) {
			return;
		}
		SkillDetail detail = this.getSkillDetail(context.getSkillLevel());
		if (Util.isEmpty(detail.getSkillBuffMap())) {
			return;
		}
		Map<Integer, TargetScope> targetMap = getTargetScope(context);
		if (Util.isEmpty(targetMap)) {
			return;
		}

		for (Entry<Integer, TargetScope> target : targetMap.entrySet()) {
			List<SBuffC> skillBuffList = detail.getSkillBuffMap().get(
					target.getKey());

			if (Util.isEmpty(skillBuffList)) {
				continue;
			}
			for (SBuffC skillBuff : skillBuffList) {
				RoleBuffReform buff = (RoleBuffReform) GameContext.getBuffApp()
						.getBuff(skillBuff.getBuffId());
				if (buff == null) {
					continue;
				}
				if (context.isSystemTrigger() 
						|| 0 != buff.getBuffType()
						|| 0 != skillBuff.getTargetType()) {
					continue;
				}
				if (buff.getEffectType() == EffectType.guideSkill
						|| buff.getSkillContinue() != 0
						|| buff.getEffectType() == EffectType.state
						|| buff.getEffectType() == EffectType.attribute
						|| buff.getEffectType() == EffectType.absorb
						|| buff.getEffectType() == EffectType.color
						|| buff.getEffectType() == EffectType.zoom) {
					int probability = Util.getAbc(0, skillBuff.getB(),
							skillBuff.getC(), skillBuff.getD(), 0, true, true);
					context.appendAttackerBuff(skillBuff.getBuffId(),
							context.getSkillLevel(),
							buff.getPersistTime(), probability,
							context.getInfo());
				}
			}
		}
	}

	@Override
	protected Map<Integer, TargetScope> getTargetScope(SkillContext context) {
		return getTargetScopeMap(context);
	}

}
