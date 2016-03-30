package com.game.draco.app.skill.vo;

import java.security.SecureRandom;

import com.game.draco.app.skill.config.SkillDetail;

import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.vo.AbstractRole;

public class SkillFormula {

	private static final SecureRandom random = new SecureRandom();
	public static final int TEN_THOUSAND = 10000 ;
	public static final float TEN_THOUSAND_F = 10000.0f ;
	
	/**
	 * 获得攻击类型	
	 * @param context
	 * @param detail
	 * @return
	 */
	public static AttackType getAttackType(SkillContext context,SkillDetail detail){
		AbstractRole attacker = context.getAttacker();
		/*if(attacker.inState(StateType.crit)){
			return AttackType.CRIT ;
		}*/
		//attacker 暴击值修正
		int critChange = detail.getCritChange() + context.getCritChange();
		if(critChange >= TEN_THOUSAND) {
			return AttackType.CRIT;
		}
		//attacker 命中值修正
		int hitChange = detail.getHitChange() + context.getHitChange() ;
		if(hitChange >= TEN_THOUSAND) {
			return AttackType.ONHIT;
		}
		AbstractRole defender = context.getDefender();
		//攻击者暴击 - 防御者韧性
		float critAtkDiff = Math.max(attacker.get(AttributeType.critAtk) + critChange
				- defender.get(AttributeType.critRit), 0);
		//防御者闪避 - 攻击者命中
		float dodgeDiff = Math.max(defender.get(AttributeType.dodge)
				- attacker.get(AttributeType.hit) - hitChange, 0);
		float critProp = 0, dodgeProp = 0, hitProp = 0; 
		float param = 0, param1 = 0;
		int judgeParam = 0;
		//暴击>=闪避
		if(critAtkDiff >= dodgeDiff) {
			//(暴击-闪避)/10000
			param = (float)(critAtkDiff - dodgeDiff) / TEN_THOUSAND;
			//暴击+10000
			param1 = dodgeDiff + TEN_THOUSAND;
			
			judgeParam = (int)(2*dodgeDiff / param1 + param);
			if(judgeParam <= 1) {
				//情况1
				float param2 = dodgeDiff / param1;
				critProp = (param2	+ param);
				dodgeProp = param2;
				hitProp = 1 - critProp - dodgeProp;
				return getAttactType(critProp, dodgeProp, hitProp);
			}
			//情况2
			critProp = (critAtkDiff) / (critAtkDiff + dodgeDiff);
			dodgeProp = 1 - critProp;
			return getAttactType(critProp, dodgeProp, hitProp);
		}
		
		
		param = (TEN_THOUSAND * critAtkDiff)
				/ ((dodgeDiff - critAtkDiff + TEN_THOUSAND) * (dodgeDiff + TEN_THOUSAND));
		param1 = dodgeDiff / (dodgeDiff + TEN_THOUSAND);
		judgeParam = (int)(param + param1);
		if(judgeParam <= 1) {
			//情况3
			critProp = param;
			dodgeProp = param1;
			hitProp = 1 - critProp - dodgeProp;
			return getAttactType(critProp, dodgeProp, hitProp);
		}
		
		//情况4
		critProp = critAtkDiff / (critAtkDiff + dodgeDiff);
		dodgeProp = 1 - critProp;
		return getAttactType(critProp, dodgeProp, hitProp);
	}
	
	/**
	 * 
	 * @param critProp 暴击
	 * @param dodgeProp 闪避
	 * @param hitProp 命中
	 * @return
	 */
	private static AttackType getAttactType(float critProp, float dodgeProp, float hitProp) {
		int zoomCirtProp = (int)(critProp * TEN_THOUSAND);
		int zoomDodgeProp = (int)(dodgeProp * TEN_THOUSAND);
		int rd = Math.abs(random.nextInt()) % TEN_THOUSAND + 1;
		if(rd <= zoomCirtProp) {
			return AttackType.CRIT;
		}
		int curProp = zoomCirtProp + zoomDodgeProp;
		if(rd <= curProp) {
			return AttackType.MISS;
		}
		
		return AttackType.ONHIT;
	}
}
