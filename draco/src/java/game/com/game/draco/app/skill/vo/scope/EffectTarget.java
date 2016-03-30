package com.game.draco.app.skill.vo.scope;

import com.game.draco.app.skill.vo.scope.target.EffectTargetEnemy;
import com.game.draco.app.skill.vo.scope.target.EffectTargetFriend;
import com.game.draco.app.skill.vo.scope.target.EffectTargetLogic;
import com.game.draco.app.skill.vo.scope.target.EffectTargetMaster;
import com.game.draco.app.skill.vo.scope.target.EffectTargetSelf;
import com.game.draco.app.skill.vo.scope.target.EffectTargetSelfTeam;
import com.game.draco.app.skill.vo.scope.target.EffectTargetTarget;

/**
 * 技能效果目标类型
 * @author tiefengKuang 
 * @date 2009-11-16 
 * @version 0.0.0.1
 * @changeLog
 *  1) create: 2009-11-16
 */
public enum EffectTarget {

	self(0),//自己
	target(1),//目标
	enemy(2),//敌人
    friend(3),//友好关系
	selfTeam(4),//队伍
	master(5),//主人
	;
	EffectTarget(int type){
		this.type = type;
	}
	
	private final int type;

	public int getType() {
		return type;
	}
	
	public static EffectTarget getType(int type){
		for(EffectTarget st : values()){
			if(type == st.getType()){
				return st ;
			}
		}
		return null ;
	}
	
	public static EffectTargetLogic getEffectTargetLogic(EffectTarget effectTarget) {
		switch (effectTarget) {
		case self:
			return EffectTargetSelf.getInstance();
		case target:
			return EffectTargetTarget.getInstance();
		case enemy:
			return EffectTargetEnemy.getInstance();
		case friend:
			return EffectTargetFriend.getInstance();
		case selfTeam:
			return EffectTargetSelfTeam.getInstance();
		case master:
			return EffectTargetMaster.getInstance();
		default:
			return null;
		}
	}
}
