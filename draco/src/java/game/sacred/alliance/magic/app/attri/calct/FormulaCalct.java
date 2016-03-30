package sacred.alliance.magic.app.attri.calct;

import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.vo.AbstractRole;

public abstract class FormulaCalct<T extends AbstractRole> {
	/**
	 * 默认暴击伤害倍率15000
	 */
	public final static int DEFAULT_CRIT_ATK_PROB= 20000;
	
	/**
	 * 速度默认值130
	 */
	public final static int DEFAULT_SPEED_VALUE = 180;
	
	/**
	 * 仇恨比例系数 默认10000
	 */
	public final static int DEFAULT_HATRED_RATE = 10000;
	/**
	 * 治疗比例系数 默认10000
	 */
	public final static int DEFAULT_HEAL_RATE = 10000;
	
	/**
	 * 耗mp比例系数 默认10000
	 */
	public final static int DEFAULT_MP_CONSUME_RATE = 10000;
	/**
	 * cd比例系数 默认10000
	 */
	public final static int DEFAULT_CD_RATE = 10000;
	
	/**
	 * 普攻cd比例系数 默认10000
	 */
	public final static int DEFAULT_NORMAL_ATK_CD_RATE = 10000;
	
	/**
	 * 默认命中
	 */
	public final static int DEFAULT_HIT_VALUE = 10000 ;
	
	/**
	 * 默认闪避值
	 */
	public final static int DEFAULT_DODGE_VALUE = 200 ;
	
	/**
	 * 默认暴击
	 */
	public final static int DEFAULT_CRIT_ATK_VALUE = 200 ;
	
	
	/**
	 * 得到基础值
	 * @param role
	 * @param attriType
	 */
	protected abstract int getBaseValue(AttributeType attriType);
	

}
