package com.game.draco.app.goddess.vo;

import sacred.alliance.magic.base.AttrFontColorType;
import sacred.alliance.magic.base.AttrFontSizeType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.AbstractRoleBehavior;
import sacred.alliance.magic.vo.Point;

import com.game.draco.app.goddess.domain.RoleGoddess;
import com.game.draco.app.npc.domain.NpcInstance;

public class RoleGoddessBehavior extends AbstractRoleBehavior<RoleGoddess> {

	public RoleGoddessBehavior(RoleGoddess t) {
		super(t);
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
			AttrFontColorType colorType, int value, AbstractRole attacker) {
		
	}

	@Override
	public void addTargetFont(AttrFontSizeType sizeType,
			AttrFontColorType colorType, int value, AbstractRole targetRole) {
		if(/*AttrFontColorType.Common_Attack == colorType
				|| */AttrFontColorType.Skill_Attack == colorType){
			//法宝的攻击飘字用特殊的颜色
			colorType = AttrFontColorType.Goddess_Attack ;
		}
		this.role.getRole().getBehavior().addTargetFont(sizeType, colorType, value, targetRole);
	}
	
	@Override
	public boolean inSelfEyes(AbstractRole abstractRole) {
		return role.getRole().getBehavior().inSelfEyes(abstractRole);
	}

	@Override
	public void notifyAttrFont() {
		role.getRole().getBehavior().notifyAttrFont();
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
	public void sendMessage(Message message,int awaitMillis) {
		
	}

	@Override
	public void stopMove() {
		
	}

	@Override
	public void update() throws ServiceException {
		
	}

}
