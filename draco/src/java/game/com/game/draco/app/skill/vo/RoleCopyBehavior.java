package com.game.draco.app.skill.vo;

import sacred.alliance.magic.app.ai.MessageType;
import sacred.alliance.magic.app.ai.Telegram;
import sacred.alliance.magic.base.AttrFontColorType;
import sacred.alliance.magic.base.AttrFontSizeType;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.constant.LoopConstant;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.scheduler.job.LoopCount;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.AbstractRoleBehavior;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.Point;

import com.game.draco.app.npc.domain.NpcInstance;

public class RoleCopyBehavior extends AbstractRoleBehavior<NpcInstance> {
	private NpcInstance role;
	private LoopCount defaultLoopCount = new LoopCount(LoopConstant.NPC_DEFAULT_CYCLE);
	public RoleCopyBehavior(NpcInstance t) {
		super(t);
		this.role = t;
	}
	
	@Override
	public void addCumulateEvent(Message message) {
		
	}
	@Override
	public void addEvent(Message message) {
		
	}
	@Override
	public void addSelfFont(AttrFontSizeType sizeType,
			AttrFontColorType colorType, int value) {
		
	}
	@Override
	public void addSelfFont(AttrFontSizeType sizeType,
			AttrFontColorType colorType, int value, SkillContext context) {
		
	}
	@Override
	public void addTargetFont(AttrFontSizeType sizeType,
			AttrFontColorType colorType, int value, AbstractRole targetRole) {
		
	}
	@Override
	public void addTargetFont(AttrFontSizeType sizeType,
			AttrFontColorType colorType, int value, AbstractRole targetRole,
			SkillContext context) {
		AbstractRole role = this.role.getMasterRole();
		if(null == role) {
			return ;
		}
		role.getBehavior().addTargetFont(sizeType, colorType, value, targetRole, context);
	}
	@Override
	public void autoLevelup() {
		
	}
	@Override
	public void changeMap(Point targetPoint) throws ServiceException {
		
	}
	@Override
	public void closeNetLink() {
		
	}
	@Override
	public void death(AbstractRole attacker) {
		
	}
	@Override
	public void enterMap() throws ServiceException {
		
	}
	@Override
	public void exitMap() throws ServiceException {
		
	}
	@Override
	public boolean inSelfEyes(AbstractRole abstractRole) {
		AbstractRole role = ((NpcInstance)this.role).getMasterRole();
		if(null == role) {
			return false;
		}
		return role.getBehavior().inSelfEyes(abstractRole);
	}
	@Override
	public void notifyAttrFont() {
		AbstractRole role = this.role.getMasterRole();
		if(null == role) {
			return ;
		}
		role.getBehavior().notifyAttrFont();
	}
	@Override
	public void notifyBattleAttrIncome(NpcInstance dieNpc) {
		
	}
	@Override
	public void notifyForceType() {
		
	}
	@Override
	public void notifyNpcMsg(String content) {
		
	}
	@Override
	public void notifyPosition(Message resp) throws ServiceException {
		
	}
	@Override
	public void sendMessage(Message message, int awaitMillis) {
		if(this.role.getRoleType() != RoleType.COPY) {
			return ;
		}
		this.role.getMasterRole().getBehavior().sendMessage(message, awaitMillis);
	}
	@Override
	public void stopMove() {
		
	}
	@Override
	public void update() throws ServiceException {
		if(null != role.getAi()){
			role.getAi().updateAI();
		}
		if(!this.defaultLoopCount.isReachCycle()){
			return ;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              
		}
		 MapInstance mapInstance = role.getMapInstance();
	     if(null == mapInstance){
	        	return;
	     }
		//NPC消失
        long disappearTime = role.getDisappearTime();
        if(disappearTime > 0 && System.currentTimeMillis() > disappearTime){
        	mapInstance.removeAbstractRole(role);
        	mapInstance.notifyNpcDeath(role);
        }
	}
	
	public void switchBattleState() {
		role.getMapInstance().getMessageDispatcher()
			.dispatch(new Telegram(role, role, MessageType.ATTACK));
	}

	@Override
	public void addSelfFont(AttrFontSizeType sizeType,
			AttrFontColorType colorType, int value, SkillContext context,
			AbstractRole attack) {
		// TODO Auto-generated method stub
		
	}
}
