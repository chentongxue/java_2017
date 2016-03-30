package com.game.draco.app.skill.vo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.base.SkillApplyResult;
import sacred.alliance.magic.vo.AbstractRole;

import com.game.draco.app.skill.config.SkillApplyType;
import com.game.draco.app.skill.config.SkillDetail;
import com.game.draco.app.skill.config.SkillPassiveType;
import com.game.draco.app.skill.config.SkillScope;
import com.game.draco.app.skill.domain.SAttrC;
import com.game.draco.app.skill.vo.SkillContext.AttrSource;
import com.game.draco.app.skill.vo.scope.AreaType;
import com.game.draco.app.skill.vo.scope.EffectTarget;
import com.game.draco.app.skill.vo.scope.Radian;
import com.game.draco.app.skill.vo.scope.TArea;
import com.game.draco.app.skill.vo.scope.TargetScope;
import com.game.draco.app.skill.vo.scope.TargetScopeType;
import com.game.draco.message.item.SkillApplyItem;
import com.game.draco.message.response.C0301_SkillApplyAttackerMessage;
import com.google.common.collect.Maps;

/**
 * 
 * 被动技能
 *
 */
public class SkillReformPassive extends SkillPassive {
	protected static final Logger logger = LoggerFactory.getLogger(SkillReformPassive.class);
	
	private static final float  TEN_THOUSAND_F = SkillFormula.TEN_THOUSAND_F ;
	
	private Set<SkillPassiveType> skillPassiveTypeSet;//被动技能的触发方式(当skillAppType为被动技能时有效) 
	public SkillReformPassive(short skillId) {
		super(skillId);
		this.setSkillApplyType(SkillApplyType.passive);
	}

	@Override
	public AttriBuffer getAttriBuffer(AbstractRole role) {
		try {
			if (!this.hasPassiveType(SkillPassiveType.attribute)) {
				return null;
			}
			int lv = role.getSkillEffectLevel(skillId);
			if (lv <= 0) {
				return null;
			}
			SkillContext context = new SkillContext(this);
			context.setAttacker(role);
			context.setSkillLevel(lv);
			context.setSkillPassiveType(SkillPassiveType.attribute);
			this.getSkillEffect(context);
			AttriBuffer buffer = context.getAttriBuffer();
			if (null == buffer || buffer.isEmpty()) {
				return null;
			}
			AttriBuffer value = AttriBuffer.createAttriBuffer();
			value.append(buffer);
			context.release();
			context = null;
			return value;
		} catch (Exception ex) {
			logger.error("getAttriBuffer error,skillId=" + this.skillId,ex);
			return null;
		}
	}
	
	@Override
	protected  boolean hasPassiveType(SkillPassiveType skillPassiveType){
		if(null == this.skillPassiveTypeSet || null == skillPassiveType){
			return false ;
		}
		return skillPassiveTypeSet.contains(skillPassiveType) ;
	}

	
	@Override
	protected void notifyMessage(AbstractRole role, Set<Integer> targetIdSet, SkillContext context){
		super.notifyMessage(role, targetIdSet, context);
	}

	@Override
	protected void passiveEffect(SkillContext context,SkillPassiveType passiveType){
		//空实现
		//被动技能不能触发被动技能
	}

	public void setSkillPassiveTypeSet(Set<SkillPassiveType> skillPassiveTypeSet) {
		this.skillPassiveTypeSet = skillPassiveTypeSet;
	}

	
	@Override
	protected AbstractRole getSkillOwner(SkillContext context){
		return context.getSkillPassiveType().isAttack()?context.getAttacker():context.getDefender() ;
	}

	@Override
	public Map<Integer, TargetScope> getTargetScopeMap(SkillContext context) {
		return super.getTargetScopeMap(context);
	}

	@Override
	protected void getSkillEffect(SkillContext context) {
		SkillDetail detail = this.getSkillDetail(context.getSkillLevel());		
		List<SAttrC> attrList = detail.getSkillAttriMap().get(context.getAreaId());
		AttrSource source = null;
		SkillAdjust adjust = null;
		int attrValue = 0;
		if(!Util.isEmpty(attrList)){
			for(SAttrC attr : attrList){
				if(attr.getTargetType() == 0){
					attrValue = context.getAttacker().get(attr.getAttrType()) ; 
					source = AttrSource.attacker;
				}else{
					attrValue = context.getDefender().get(attr.getAttrType()) ; 
					source = AttrSource.defender;
				}
				int value = getAbc(attr.getA(),attr.getB(),attr.getC(),attr.getD(),attrValue,false);
				if(attr.getReduce() == 0){
					adjust = new SkillAdjust(SkillAdjust.Type.add,-value);
				}else{
					adjust = new SkillAdjust(SkillAdjust.Type.add,value);
				}
				context.appendAdjust(AttributeType.get(attr.getModifyTargetAttr()),adjust,source);
			}
		}
		
	}
	
	private int getAbc(int a,int b,int c,int d,int value,boolean prob){
		float rate = ((a / TEN_THOUSAND_F * value) + ((b / TEN_THOUSAND_F) * (c / TEN_THOUSAND_F)) + (d / TEN_THOUSAND_F));
		if(prob){
			rate *= 10000;
		}
		BigDecimal decimal = new BigDecimal(rate);
		rate = decimal.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
		return (int)rate;
	}

	@Override
	protected void getAfterAttackEffect(SkillContext context) {
		
	}

	@Override
	protected void getAttackerEffect(SkillContext context) {
		
	}

	@Override
	protected Map<Integer, TargetScope> getTargetScope(SkillContext context) {
		return getTargetScopeMap(context);
	}

}
