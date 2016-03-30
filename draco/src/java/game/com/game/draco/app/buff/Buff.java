package com.game.draco.app.buff;

import lombok.Data;
import sacred.alliance.magic.base.StateType;
import sacred.alliance.magic.vo.AbstractRole;

public @Data abstract class Buff {
	public Buff(short buffId){
		this.buffId = buffId;
	}
	
	protected short buffId;
	protected String buffName;
	/**buff类型 0自己 1地图*/
	protected byte buffType;
	/**buff效果类型[吸收|状态|属性]*/
	protected EffectType effectType = EffectType.attribute ;
	/**buff状态 */
	protected byte stateType;
	/**替换类型 1 直接替换 2 延长时间 3 重置时间  4不叠加 */
	protected int replaceType;
	/**小图标*/
	protected byte iconId;
	protected short effectId ;
	/**buff时效类型[单次|持久]*/
	protected BuffTimeType timeType = BuffTimeType.continued ;
	/**buff分类*/
	protected BuffCategoryType categoryType = BuffCategoryType.buff ;
	/**变身是否不清除*/
	protected boolean transNoClean ;
	/**间隔时间*/
	protected int intervalTime;
	/**持续时间*/
	protected int persistTime;
	/**死亡是否消失*/
	protected boolean dieLost ; 
	/**下线是否消失*/
	protected boolean offlineLost ;
	/**下线是否计时*/
	protected boolean offlineTiming ;
	/**切换英雄是否保留*/
	protected boolean switchOn ;
	/**buff作用范围类型 0：团队BUFF（唯一）1：个人BUFF（共存 */
	protected int beingType;
	/**互斥系列id*/
	protected int groupId;
	/**不能替换时，提示语*/
	protected String notReplaceDesc;
	/**伤血造成仇恨百分比*/
	protected int hatredPercent ;
	/**获得造成仇恨值*/
	protected int hatredAdd ;
	/** 抗性类型 */
	protected byte hurtType;
	/** 外形缩放 */
	protected byte zoom;
	/** 外形变色 */
	protected int discolor;
	/**描述*/
	protected String des;
	/**最大等级*/
	protected int maxLevel;
	/**持续技能ID*/
	protected short skillContinue;
	/**加伤害*/
	protected boolean stack;
	/**是否击飞*/
	protected boolean blowfly;
	/**是否随机*/
	protected boolean random;
	/**喊话*/
	protected String shout;
	/**过图是否消失*/
	protected boolean transLost;
	/**退出副本是否消失*/
	protected boolean exitInsLost;
	/**最大叠层数*/
	protected int maxLayer;
	
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
    public abstract int getIntervalTime(int buffLevel) ;
	public abstract String getBuffDesc(int buffLevel);
	public abstract boolean isDebuff();
	public abstract BuffAddResult getReplaceResult(AbstractRole player,
			AbstractRole caster,int currAddBuffLevel,boolean mustSuccess) ;
	/**buff被抵抗(加buff失败)*/
	public abstract void buffResist(AbstractRole player,AbstractRole caster);
	
	public abstract void getBuffHurt(BuffContext context) ;
	
	public abstract void addAttr(BuffContext context) ;
	
	public abstract BuffDetail getBuffDetail(int buffLevel);

}
