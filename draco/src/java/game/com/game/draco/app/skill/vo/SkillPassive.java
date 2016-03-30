package com.game.draco.app.skill.vo;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.AttrFontInfo;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.skill.config.SkillApplyType;
import com.game.draco.app.skill.config.SkillAttackType;
import com.game.draco.app.skill.config.SkillDetail;
import com.game.draco.app.skill.config.SkillPassiveType;
import com.game.draco.message.item.SkillApplyItem;
import com.game.draco.message.item.SkillApplyTargetItem;
import com.game.draco.message.item.SkillRoleAttrFontItem;
import com.game.draco.message.push.C0216_WalkTeleportNotifyMessage;
import com.game.draco.message.push.C0320_SkillDashNotifyMessage;
import com.game.draco.message.response.C0301_SkillApplyAttackerMessage;
import com.game.draco.message.response.C0306_SkillApplyOtherMessage;
import com.google.common.collect.Lists;

/**
 * 
 * 被动技能
 *
 */
public abstract class SkillPassive extends SkillAdaptor {
	protected static final Logger logger = LoggerFactory.getLogger(SkillPassive.class);
	private Set<SkillPassiveType> skillPassiveTypeSet;//被动技能的触发方式(当skillAppType为被动技能时有效) 
	public SkillPassive(short skillId) {
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
		if(!context.isChannelSkill()) {
			return ;
		}
		if(role.getRoleType() != RoleType.PLAYER){
			return ;
		}
		SkillDetail detail = this.getSkillDetail(role);
		short effectId = detail.getEffectId();
		if(0 == effectId){
			//被动技能没有特效无需广播
			return ;
		}
		//给自己发一个使用了被动技能的协议
		SkillApplyItem applyItem = this.createSkillApplyItem(0, role, detail, null, false);
		C0301_SkillApplyAttackerMessage skillPushMsg = new C0301_SkillApplyAttackerMessage();
		skillPushMsg.setSkillApplyItem(applyItem);
		//连击数
		RoleInstance player = (RoleInstance)role;
		skillPushMsg.setHitCombo((short)player.getHitCombo().get());
		role.getBehavior().sendMessage(skillPushMsg);
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
	
}
