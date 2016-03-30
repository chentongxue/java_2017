package sacred.alliance.magic.app.attri.calct;

import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.vo.RoleInstance;


public class RoleFormulaCalct extends FormulaCalct<RoleInstance>{
	
	/**
	 * µÃµ½»ù´¡Öµ
	 * @param role
	 * @param attriType
	 */
	public int getBaseValue(AttributeType attriType){
		if(null == attriType){
			return 0 ;
		}
		if(attriType == AttributeType.speed){
			return DEFAULT_SPEED_VALUE;
		}
		if(attriType == AttributeType.critAtkProb){
			return DEFAULT_CRIT_ATK_PROB;
		}
		if(attriType == AttributeType.hatredRate){
			return DEFAULT_HATRED_RATE ;
		}
		if(attriType == AttributeType.healRate){
			return DEFAULT_HEAL_RATE ;
		}
		if(attriType == AttributeType.mpConsumeRate){
			return DEFAULT_MP_CONSUME_RATE ;
		}
		if(attriType == AttributeType.cdRate){
			return DEFAULT_CD_RATE ;
		}
		if(attriType == AttributeType.normalAtkCdRate) {
			return DEFAULT_NORMAL_ATK_CD_RATE;
		}
		/*if(attriType == AttributeType.hit){
			return DEFAULT_HIT_VALUE ;
		}
		if(attriType == AttributeType.critAtk){
			return DEFAULT_CRIT_ATK_VALUE ;
		}
		if(attriType == AttributeType.dodge){
			return DEFAULT_DODGE_VALUE ;
		}*/
		return 0;
	}
}
