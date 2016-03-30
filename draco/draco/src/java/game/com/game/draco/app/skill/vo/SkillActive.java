package com.game.draco.app.skill.vo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.base.SkillApplyResult;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.vo.AbstractRole;

import com.game.draco.GameContext;
import com.game.draco.app.skill.config.SkillApplyType;
import com.game.draco.app.skill.config.SkillAttackType;
import com.game.draco.app.skill.config.SkillDetail;
import com.game.draco.app.skill.config.SkillPassiveType;
import com.game.draco.app.skill.domain.RoleSkillStat;
import com.game.draco.message.push.C0216_WalkTeleportNotifyMessage;
import com.game.draco.message.push.C0320_SkillDashNotifyMessage;
import com.game.draco.message.response.C0300_SkillApplyRespMessage;
import com.game.draco.message.response.C0301_SkillApplyBroadcastMessage;

public abstract class SkillActive extends SkillAdaptor{

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	public SkillActive(short skillId) {
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
	protected void notifyMessage(AbstractRole role, int[] targetIds, boolean setEffectTime, boolean skillActiveApply){
		SkillDetail detail = this.getSkillDetail(role);
		//用户使用技能消息
		C0300_SkillApplyRespMessage respMsg = new C0300_SkillApplyRespMessage();
		respMsg.setType((byte)SkillApplyResult.SUCCESS.getType());
		
		respMsg.setSkillId(this.getSkillId());
		respMsg.setActionId(detail.getActionId());
		respMsg.setEffectId(detail.getEffectId());
		
		if(null != role.getTarget()){
			respMsg.setTargetRoleId(role.getTarget().getIntRoleId());
		}
		respMsg.setTargetIds(targetIds);
		respMsg.setTargetEffectId(detail.getTargetEffectId());
		if(setEffectTime) {
			respMsg.setEffectTime(detail.getEffectTime());
		}
		else {
			respMsg.setEffectTime(-1);
		}
		role.getBehavior().sendMessage(respMsg);
		
		//广播用户使用技能
		C0301_SkillApplyBroadcastMessage skillPushMsg = new C0301_SkillApplyBroadcastMessage();
		skillPushMsg.setRoleId(role.getIntRoleId());
		skillPushMsg.setSkillId(this.getSkillId());
		skillPushMsg.setActionId(detail.getActionId());
		skillPushMsg.setEffectId(detail.getEffectId());
		if(null != role.getTarget()){
			skillPushMsg.setTargetRoleId(role.getTarget().getIntRoleId());
		}
		skillPushMsg.setTargetIds(targetIds);
		skillPushMsg.setTargetEffectId(detail.getTargetEffectId());
		if(setEffectTime || skillActiveApply) {
			skillPushMsg.setEffectTime(detail.getEffectTime());
		}
		else {
			skillPushMsg.setEffectTime(-1);
		}
		
		Message affixMessage = null ;
		if(SkillAttackType.Telesport == getSkillAttackType()){
			//闪现通知其他用户坐标
			C0216_WalkTeleportNotifyMessage notifyMessage = new C0216_WalkTeleportNotifyMessage();
			notifyMessage.setRoleId(role.getIntRoleId());
			notifyMessage.setX((short)role.getMapX());
			notifyMessage.setY((short)role.getMapY());
			affixMessage = notifyMessage ;
		}else if(SkillAttackType.Dash == getSkillAttackType()){
			//冲锋
			C0320_SkillDashNotifyMessage message = new C0320_SkillDashNotifyMessage();
			message.setRoleId(role.getIntRoleId());
			affixMessage = message ;
		}
		try {
			role.getBehavior().notifySkillBuff(skillPushMsg,affixMessage,
					String.valueOf(skillPushMsg.getTargetRoleId()), false);
		} catch (ServiceException e) {
			logger.error("",e);
		}
	}
	//触发  防御后 技能
	/*private void passiveAfterDefend(SkillContext context){
		AbstractRole defender = context.getDefender();
		//TODO:boss以后会有被动技能 ，，到时候需要修改条件（添加是否为boss   ，，，不是则return）
		if(null == defender 
				|| defender.getRoleType() != RoleType.PLAYER 
				|| context.getDefender() == context.getAttacker()){
			return ;
		}
		for (SkillStat stat : defender.getSkillMap().values()) {
			short skillId = stat.getSkillId();
			Skill skill = GameContext.getSkillApp().getSkill(
					skillId);
			if(!this.isTriggerPassive(skill, SkillPassiveType.afterDefend,defender)){
				continue ;
			}
			SkillContext c = new SkillContext(skill);
			c.setAttacker(defender);
			c.setSkillPassiveType(SkillPassiveType.afterDefend);
			c.setSkillLevel(defender.getSkillLevel(skill.getSkillId()));
			((SkillAdaptor) skill).use(c);
		}
	}*/
	
	private boolean isTriggerPassive(Skill skill,SkillPassiveType passiveType,AbstractRole abstRole){
		if(null == skill 
				|| skill.getSkillId() == this.getSkillId()
				|| null == passiveType){
			return false ;
		}
		if(!skill.hasPassiveType(passiveType)){
			return false ;
		}
		/*if (abstRole instanceof RoleInstance) {
			RoleInstance role = (RoleInstance) abstRole;
			if(!role.isSoulState() || role.getCurrentSoul().getSoulAttriTemplate().isPersonSkill()){
				return skill.getSkillLearnType().isHuman();
			}
			return !skill.getSkillLearnType().isHuman();
		}*/
		return true ;
	}
	
	@Override
	protected void passiveEffect(SkillContext context, SkillPassiveType passiveType) {
		if(context.isSystemTrigger()) {
			//如果是系统触发的技能则不会触发被动技能
			return ;
		}
		if(null == passiveType ){
			return ;
		}
		/*if(passiveType ==SkillPassiveType.afterDefend ){
			this.passiveAfterDefend(context);
			return ;
		}*/
		int skillLevel = context.getSkillLevel();
		Object info = context.getInfo();
		AbstractRole attacker = context.getAttacker();
		AbstractRole defender = context.getDefender();
		try {
			if(!this.isTriggerPassive() || null == passiveType){
				return ;
			}
			AbstractRole role = passiveType.isAttack()?attacker:defender;
			if (this.getSkillApplyType() != SkillApplyType.active
					|| null == role || role.isDeath()) {
				return;
			}
			/*if(!passiveType.isAttack()){
				//被动方的被动技能,攻击方,防御方需要调换
				context.setAttacker(defender);
				context.setDefender(attacker);
			}*/
			for (RoleSkillStat stat : role.getSkillMap().values()) {
				short skillId = stat.getSkillId();
				Skill skill = GameContext.getSkillApp().getSkill(
						skillId);
				if(!this.isTriggerPassive(skill, passiveType,role)){
					continue ;
				}
				context.setSkillPassiveType(passiveType);
				context.setSkill(skill);
				context.setSkillLevel(role.getSkillLevel(skill.getSkillId()));
				((SkillAdaptor) skill).use(context);
			}
		} catch (Exception ex) {
			logger.error("",ex);
		}finally{
			context.setAttacker(attacker);
			context.setDefender(defender);
			//主动技能没有被动触发方式
			context.setSkillPassiveType(null);
			//将skill设置回来
			context.setSkill(this);
			//context.setSkillLevel(context.getAttacker().getSkillLevel(skillId));
			//上面语句错误,因为buff中使用的技能从角色身上将无法取到
			context.setSkillLevel(skillLevel);
			context.setInfo(info);
		}
	}
	
	@Override
	protected AbstractRole getSkillOwner(SkillContext context){
		return context.getAttacker() ;
	}
}
