package com.game.draco.app.skill.vo;

import sacred.alliance.magic.base.AttrFontColorType;
import sacred.alliance.magic.base.AttrFontSizeType;
import sacred.alliance.magic.base.AttrFontSpecialState;
import sacred.alliance.magic.vo.AbstractRole;

/**
 * 
 * 技能特殊状态飘字效果
 * 比如：抵抗、格挡
 * @author dongrui
 *
 */
public class SkillFontType {
	
	/** 攻击者 */
	private AbstractRole attacker;
	
	/** 被攻击者 */
	private AbstractRole defender;
	
	/** 飘字特殊状态 */
	private AttrFontSpecialState specialState;
	
	public SkillFontType(){
		
	}
	
	public SkillFontType(AbstractRole attacker, AbstractRole defender, AttrFontSpecialState specialState){
		this.attacker = attacker;
		this.defender = defender;
		this.specialState = specialState;
	}
	
	/**
	 * 发送飘字效果通知
	 */
	public void notifyAttrFont(){
		this.defender.getBehavior().addSelfFont(AttrFontSizeType.Common, 
				AttrFontColorType.Special_State, this.specialState.getType());
		this.attacker.getBehavior().addTargetFont(AttrFontSizeType.Common, 
				AttrFontColorType.Special_State, this.specialState.getType(), this.defender);
		this.defender.getBehavior().notifyAttrFont();
		this.attacker.getBehavior().notifyAttrFont();
	}

	public AbstractRole getAttacker() {
		return attacker;
	}

	public void setAttacker(AbstractRole attacker) {
		this.attacker = attacker;
	}

	public AbstractRole getDefender() {
		return defender;
	}

	public void setDefender(AbstractRole defender) {
		this.defender = defender;
	}

	public AttrFontSpecialState getSpecialState() {
		return specialState;
	}

	public void setSpecialState(AttrFontSpecialState specialState) {
		this.specialState = specialState;
	}
	
}
