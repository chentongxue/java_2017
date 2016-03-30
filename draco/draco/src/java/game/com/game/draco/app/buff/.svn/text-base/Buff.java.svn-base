package com.game.draco.app.buff;

import lombok.Data;
import sacred.alliance.magic.vo.AbstractRole;

public @Data abstract class Buff {
	public Buff(short buffId){
		this.buffId = buffId;
	}
	
	protected short buffId;
	protected String buffName;
	/**buff时效类型[单次|持久]*/
	protected BuffTimeType timeType = BuffTimeType.continued ;
	/**buff效果类型[吸收|状态|属性]*/
	protected EffectType effectType = EffectType.attribute ;
	/**buff分类*/
	protected BuffCategoryType categoryType = BuffCategoryType.buff ;
	/**小图标*/
	protected byte iconId;
	protected short effectId ;
	/**死亡是否消失*/
	protected boolean dieLost ; 
	/**下线是否消失*/
	protected boolean offlineLost ;
	/**下线是否计时*/
	protected boolean offlineTiming ;
	protected int hatredPercent ;
	protected int hatredAdd ;
	
	/**buff作用范围类型 0：团队BUFF（唯一）1：个人BUFF（共存 */
	protected int beingType;
	/**替换类型 1 直接替换 2 延长时间 3 重置时间 */
	protected int replaceType;
	/**不能替换时，提示语*/
	protected String notReplaceDesc;
	/**互斥系列id*/
	protected int groupId;
	/** 抗性类型 */
	protected byte hurtType;
	/** 外形缩放 */
	protected byte zoom;
	/** 外形变色 */
	protected int discolor;
	
	public abstract int getMaxLevel() ;
	
	/**buff添加上时触发*/
	public abstract void begin(BuffContext context);
	/**buff间隔触发*/
	public abstract void process(BuffContext context);
	/**buff超时触发*/
	public abstract void timeOver(BuffContext context);
	/**buff非超时中断触发*/
	public abstract void remove(BuffContext context);
	/**主动攻击时触发*/
	public abstract void activeAttack(BuffContext context);
	/**攻击时触发*/
	public abstract void attacked(BuffContext context);
    /**获得buff的间隔时间*/
    public abstract int getIntervalTime(int buffLevel) ;
    public abstract int getPersistTime(int buffLevel);
	public abstract String getBuffDesc(int buffLevel);
	public abstract String getName(int buffLevel);
	public abstract boolean isDebuff();
	public abstract BuffAddResult getReplaceResult(AbstractRole player,
			AbstractRole caster,int currAddBuffLevel,boolean mustSuccess) ;
	public abstract boolean isTransClean();
	/**buff被抵抗(加buff失败)*/
	public abstract void buffResist(AbstractRole player,AbstractRole caster);
}
