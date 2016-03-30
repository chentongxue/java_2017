package com.game.draco.app.skill.vo;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.base.AttrFontColorType;
import sacred.alliance.magic.base.AttrFontSizeType;
import sacred.alliance.magic.base.AttrFontSpecialState;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.ForceRelation;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;

import com.game.draco.GameContext;
import com.game.draco.app.buff.Buff;
import com.game.draco.app.buff.BuffEffect;
import com.game.draco.app.buff.MapBuffEffect;
import com.game.draco.app.skill.config.SkillApplyType;
import com.game.draco.app.skill.config.SkillHurtRemit;
import com.game.draco.app.skill.config.SkillPassiveType;
import com.game.draco.app.skill.domain.RoleSkillStat;
import com.google.common.collect.Lists;

public class SkillContext {
	public SkillContext(Skill skill){
		this.skill = skill ;
	}
	public static enum AttrSource {
		attacker,
		defender,
		skill,
		;
	}
	private static final SecureRandom random = new SecureRandom();
	private static final int  TEN_THOUSAND = SkillFormula.TEN_THOUSAND ;
	//不破防系数
	private static final float NO_DAMAGE_RIT = 0.1f ;
	/**攻击者*/
	@Setter @Getter private AbstractRole attacker ;
	/**被攻击者*/
	@Setter @Getter private AbstractRole defender ;
	/**攻击方式*/
	@Setter @Getter private AttackType attackType ;
	/**目标范围ID*/
	@Setter @Getter private int areaId ;
	/**当前技能等级*/
	@Setter @Getter private int skillLevel ;
	@Setter private Skill skill ;
	///**目标差异*/
	//@Setter private boolean difference = true ;
	@Setter @Getter private SkillPassiveType skillPassiveType ;
	/**附加信息*/
	@Setter @Getter private Object info ;
	
	@Setter @Getter private int clientDelay = 0 ;
	/**
	 * 必须活着
	 */
	@Setter @Getter private boolean mustLive = true ;
	//###############
	//技能效果
	/**包含的伤害列表*/
	
	private int attackerHpChange = 0 ;
	private int attackerMpChange = 0 ;
	private int defenderHpChange = 0;
	private int defenderMpChange = 0 ;
	@Getter private int hurtFactorChange = 0;//伤害系数修正 
	@Getter private int hitChange;//命中修正 
	@Getter private int critChange;//暴击修正 
	@Setter @Getter private int inputHurts = 0 ;
	/**技能参数修正*/
	private Map<AttributeType,List<SkillAdjust>> attackerAdjusts = new HashMap<AttributeType,List<SkillAdjust>>();
	private Map<AttributeType,List<SkillAdjust>> defenderAdjusts = new HashMap<AttributeType,List<SkillAdjust>>();
	private Map<AttributeType,List<SkillAdjust>> skillAdjusts = new HashMap<AttributeType,List<SkillAdjust>>();
	/**buff效果列表*/
	@Getter private List<BuffEffect> defenderBuffEffects = new ArrayList<BuffEffect>();
	/**自己buff效果列表*/
	@Getter private List<BuffEffect> attackerBuffEffects = new ArrayList<BuffEffect>();
	/**地图buff效果*/
	@Getter private List<MapBuffEffect> mapBuffEffects = new ArrayList<MapBuffEffect>();
	/**持久效果类被动技能修改的attriBuffer*/
	@Getter private AttriBuffer attriBuffer = AttriBuffer.createAttriBuffer();
	/** 飘字效果 */
	private List<SkillFontType> fontList = new ArrayList<SkillFontType>();
	
	/** 技能中各系伤害 */
	private List<SkillHurt> skillHurtList = Lists.newArrayList();
	/** 是否触发二次攻击 */
	@Getter @Setter private boolean trigger2Attack;
	/** 是否是系统触发 */
	@Getter @Setter private boolean systemTrigger = false;
	/** 是否判断使用条件 */
	@Getter @Setter private boolean judgeUseCond = true;
	/** 是否设置特效时间 */
	@Getter @Setter private boolean setEffectTime = true;
	/** 是否是客户端主动请求 */
	@Getter @Setter private boolean skillActiveApply = false;
	
	public boolean isMustTargetScope(){
		if(this.skill.getSkillApplyType()== SkillApplyType.active
				|| null == this.skillPassiveType){
			//主动技能
			return true ;
		}
		return this.skillPassiveType.isMustTargetScope();
	}
	
	public boolean isSkillPassiveType(SkillPassiveType skillPassiveType){
		if(null == skillPassiveType || null == this.skillPassiveType){
			return false ;
		}
		return this.skillPassiveType == skillPassiveType ;
	}
	
	private int calc(AttributeType attrType,AttrSource source,int originalValue){
		if(null == attrType || null == source){
			return originalValue ;
		}
		Map<AttributeType,List<SkillAdjust>> adjusts = this.getAdjusts(source);
		if(Util.isEmpty(adjusts)){
			return originalValue;
		}
		List<SkillAdjust> list = adjusts.get(attrType);
		if(Util.isEmpty(list)){
			return originalValue;
		}
		int value = originalValue ;
		for(SkillAdjust sa:list){
			value = sa.getValue(value);
		}
		return value ;
	}
	
	
	private int get(AttributeType attriType,AttrSource source){
		if(source == AttrSource.skill){
			return 0 ;
		}
		AbstractRole actor = (source == AttrSource.attacker)?this.attacker:defender ;
		return this.calc(attriType, source, actor.get(attriType));
	}
	
	//攻击方暴击倍率=系统暴击倍率 + 技能调整
	private float getCritMultiple(){
		if(this.attackType != AttackType.CRIT){
			return 1 ;
		}
		float value = (this.get(AttributeType.critAtkProb, AttrSource.attacker))/Float.valueOf(TEN_THOUSAND);
		return Math.max(1, value);
	}
	
	public int getAttackerHpResult(){
		return this.attackerHpChange ;
	}
	public int getAttackerMpResult (){
		 return this.attackerMpChange ;
	 }
	public int getDefenderHpResult(){
		return this.defenderHpChange ;
	}
	public int getDefenderMpResult(){
		return this.defenderMpChange ;
	}
	
	public void result(){
		if(Util.isEmpty(this.skillHurtList)) {
			this.defenderHpChange = 0;
			return;
		}
		
		SkillHurtRemit skillHurtRemit = GameContext.getSkillApp()
				.getSkillHurtRemit(this.defender.getLevel());
		if(null == skillHurtRemit) {
			this.defenderHpChange = 0;
			return ;
		}
		float hp = 0;
		short hurtRemit = skillHurtRemit.getHurtRemit();
		for(SkillHurt skillHurt : this.skillHurtList) {
			hp += calcSkillHurt(skillHurt, hurtRemit);
		}
		//基础伤害
		hp = hp * (TEN_THOUSAND - this.get(AttributeType.hurtRemitRate, AttrSource.defender))
        		* this.getCritMultiple() / TEN_THOUSAND;
		//最终伤害=基础伤害 * 伤害系数调整
		this.defenderHpChange = -(int)(hp * (this.getHurtFactorChange() + TEN_THOUSAND) / TEN_THOUSAND);
	}
	
	/** 某系技能伤害计算 */
	private int calcSkillHurt(SkillHurt skillHurt, short hurtRemit) {
		SkillHurtType hurtType = skillHurt.getType();
		AttributeType atkType = hurtType.getAtkType();
		AttributeType ritType = hurtType.getRitType();
		//防御减伤=防守方xx防御a/(防守方xx防御a+A)
		//某系伤害=(A%*角色物攻/冰攻/火攻+B)*(1-防御减伤)
		float atk = this.get(AttributeType.atk, AttrSource.attacker)
		        + this.get(atkType, AttrSource.attacker);
		int rit = this.get(AttributeType.rit, AttrSource.defender)
        		+ this.get(ritType, AttrSource.defender);
		int hurt = (int)((atk * skillHurt.getPercent() / TEN_THOUSAND + skillHurt.getValue()) * hurtRemit / (rit + hurtRemit));
		
		return Math.max(0, hurt);
	}
	
	/**
	 * 产生一个[1-10000]的随机数
	 * @return
	 */
	public static int random(){
		return Math.abs(random.nextInt()) % TEN_THOUSAND + 1;
	}
	
	public static boolean on(int probability){
		if(probability >= TEN_THOUSAND){
			return true ;
		}
		if(probability<=0){
			return false ;
		}
		return (Math.abs(random.nextInt()) % TEN_THOUSAND) < probability ;
	}
	
	/**
	 * 获得技能使用者与传入角色的势力关系
	 * @param targetRole 目标角色
	 * @return neutral((byte)0,"中立"),
			   friend((byte)1,"友好"),
			   enemy((byte)2,"敌对"),
	 */
	public ForceRelation relation(AbstractRole targetRole){
		return attacker.getForceRelation(targetRole);
	} 
	
	public void release(){
		this.attacker = null ;
		this.defender = null ;
		this.areaId = 0 ;
		this.attackType = null ;
		this.skillLevel = 1 ;
		this.defenderBuffEffects.clear();
		this.attackerBuffEffects.clear();
		this.attackerAdjusts.clear();
		this.defenderAdjusts.clear();
		this.skillAdjusts.clear();
		this.attackerHpChange = 0 ;
		this.attackerMpChange = 0 ;
		this.defenderHpChange = 0 ;
		this.defenderMpChange = 0 ;
		this.attriBuffer.clear();
		this.mapBuffEffects.clear();
		this.inputHurts = 0 ;
		this.skill = null ;
		this.hitChange = 0 ;
		this.critChange = 0 ;
		this.hurtFactorChange = 0 ;
		this.fontList.clear();
		this.skillHurtList.clear();
		//this.systemTrigger = false;
		//this.judgeUseCond = true;
		//this.setEffectTime = true;
		//this.channelBuffId = 0;
		//this.attackerHpResult = 0  ;
		//this.attackerMpResult = 0  ;
		//this.defenderHpResult = 0  ;
		//this.defenderMpResult = 0  ;
	}
	
	/**
	 * 附加hp修改量
	 * @param hp hp修改量
	 * @param source attacker 攻击者 defender 防御者
	 * @return
	 */
	public SkillContext appendHpChange(int hp,AttrSource source){
		if(null == source){
			return this;
		}
		if(source == AttrSource.attacker){
			this.attackerHpChange += hp ;
		}else{
			this.defenderHpChange += hp ;
		}
		return this ;
	}
	
	/**
	 * 附加mp修改量
	 * @param mp mp修改量
	 * @param source attacker 攻击者 defender 防御者
	 * @return
	 */
	public SkillContext appendMpChange(int mp,AttrSource source){
		if(null == source){
			return this;
		}
		if(source == AttrSource.attacker){
			this.attackerMpChange += mp ;
		}else{
			this.defenderMpChange += mp ;
		}
		return this ;
	}
	
	
	public SkillContext appendHitChange(int hitChange){
		this.hitChange += hitChange ;
		return this ;
	}
	
	public SkillContext appendCritChange(int critChange){
		this.critChange += critChange ;
		return this ;
	}
	
	public SkillContext appendHurtFactorChange(int hurtFactor) {
		this.hurtFactorChange += hurtFactor;
		return this;
	}

	/**
	 * 附加buff效果
	 * @param buffId buff效果id
	 * @param lv buff级别(如果lv<=0则为删除buff)
	 * @param probability 几率[0-10000] 100%=10000
	 * @param source attacker 攻击者 defender 防御者
	 * @return
	 */
	public SkillContext appendBuff(short buffId,int lv,int probability,AttrSource source){
		if(null == source){
			return this;
			
		}
		if(on(probability)){
			this.appendBuff(new BuffEffect(buffId,lv),source == AttrSource.attacker);
			return this ;
		}
		return this ;
	}
	
	/**
	 * 给攻击者自己加buff
	 * @param buffId
	 * @param lv
	 * @param probability
	 * @return
	 */
	public SkillContext appendAttackerBuff(short buffId, int lv, int probability){
		if(null == attacker) {
			return this;
		}
		Buff buff = GameContext.getBuffApplication().getBuff(buffId);
		if(null == buff) {
			return this;
		}
		if(!on(probability)) {
			return this;
		}
		this.appendBuff(new BuffEffect(buffId,lv),true);
		return this;
	}
	
	/**
	 * 战斗飘字中加入特殊状态通知(击飞,击退)
	 * @param owner
	 * @param 
	 * @param colorType
	 * @param state
	 * @return
	 */
	public SkillContext addStateFont(AbstractRole attacker, AbstractRole defender, AttrFontSizeType sizeType,
			AttrFontSpecialState state) {
		if(null == attacker || null == defender) {
			return this;
		}
		attacker.getBehavior().addTargetFont(sizeType, AttrFontColorType.Skill_Attack, state.getType(), defender);
		defender.getBehavior().addSelfFont(sizeType, AttrFontColorType.Skill_Attack, state.getType(), attacker);
		return this;
	}
	
	/**
	 * 删除buff
	 * @param buffId
	 * @param probability
	 * @param source
	 * @return
	 */
	public SkillContext removeBuff(short buffId,int probability,AttrSource source){
		return appendBuff(buffId,0,probability,source);
	}
	
	/**
	 * 删除目标身上自己附加的buff-
	 * @param buffId
	 * @param probability
	 * @param source
	 * @return
	 */
	
	public SkillContext removeSelfAppendBuff(short buffId,int probability,AttrSource source){
		//lv = -1 标识自己附加的buff
		return appendBuff(buffId,-1,probability,source);
	}
	
	/**
	 * 给防御者附加buff效果
	 * 如果攻击者的hurtType属性值-防御者的hurtType属性值  > probability 则给防御者施加buff
	 * @param buffId buff效果id
	 * @param lv buff级别(如果lv<=0则为删除buff)
	 * @param probability 几率[0-10000] 100%=10000
	 * @return
	 */
	public SkillContext appendBuff(short buffId, int lv, int probability){
		Buff buff = GameContext.getBuffApplication().getBuff(buffId);
		if(null == buff) {
			return this;
		}
		SkillHurtType hurtType = SkillHurtType.getType(buff.getHurtType());
		if(null == hurtType) {
			return this;
		}
		//判断是否能产生buff
		AttributeType atkType = hurtType.getAtkType();
		AttributeType ritType = hurtType.getRitType();
		int hitRate = probability + this.get(atkType, AttrSource.attacker)
				- this.get(ritType, AttrSource.defender);
		return appendBuff(buffId,lv,hitRate,AttrSource.defender);
	}
	
	/**
	 * 添加地图buff
	 * @param buffId buff效果id
	 * @param lv buff级别(如果lv<=0则为删除buff)
	 * @param probability 几率[0-10000] 100%=10000
	 * @param x 地图buff中心点x坐标
	 * @param y 地图buff中心点y坐标
	 * @return
	 */
	public SkillContext appendMapBuff(short buffId,int lv,int probability,int x,int y){
		if(on(probability)){
			this.mapBuffEffects.add(new MapBuffEffect(buffId,lv,x,y));
		}
		return this ;
	}
	
	/**
	 * 删除目标角色所有的debuff
	 * @param probability 几率[0-10000] 100%=10000
	 * @param source attacker 攻击者 defender 防御者
	 * @return
	 */
	public SkillContext removeDebuff(int probability,AttrSource source){
		if(null != source && on(probability)){
			this.appendBuff(new BuffEffect((short)-1,-1),source == AttrSource.attacker);
		}
		return this ;
	}
	
	/**
	 * 设置当前技能的状态值
	 * @param systemTrigger 是否是系统触发
	 * @param judgeUseCond 是否需要判断使用条件(消耗,cd时间)
	 * @return
	 */
	public SkillContext appendSkillState(boolean systemTrigger, boolean judgeUseCond) {
		this.systemTrigger = systemTrigger;
		this.judgeUseCond = judgeUseCond;
		return this;
	}
	
	
	private Map<AttributeType,List<SkillAdjust>> getAdjusts(AttrSource source){
		if(AttrSource.attacker == source){
			return this.attackerAdjusts;
		}
		if(AttrSource.defender == source){
			return this.defenderAdjusts;
		}
		return this.skillAdjusts ;
	}
	
	/**
	 * 附加技能相关参数修正
	 * @param type 属性类型,具体参考AttributeType枚举
	 * @param adjust 调整类型,具体参考类SkillAdjust
	 * @param source 调整目标
	 *               role,
		             targetRole,
		             skill,
	 * @return
	 */
	public SkillContext appendAdjust(AttributeType type,SkillAdjust adjust,AttrSource source){
		if(null == type || null == adjust || null == source){
			return this ;
		}
		Map<AttributeType,List<SkillAdjust>> adjusts = this.getAdjusts(source);
		if(!adjusts.containsKey(type)){
			adjusts.put(type, new ArrayList<SkillAdjust>());
		}
		adjusts.get(type).add(adjust);
		return  this ;
	}
	
	public SkillContext appendAttribute(AttributeType type,int value,int precValue){
		if(null == type || (0 == value && 0 == precValue)){
			return this ;
		}
		this.attriBuffer.append(type, value, (float)precValue/TEN_THOUSAND);
		return this ;
	}
	
	/** 附加技能某系伤害 */
	public SkillContext appendSkillHurt(SkillHurtType type, int percent, int value) {
		if(null == type || (percent == 0 && value == 0)) {
			return this ;
		}
		this.skillHurtList.add(new SkillHurt(type, percent, value));
		return this ;
	}
	
	
	/**添加技能效果*/
	private SkillContext appendBuff(BuffEffect effect,boolean isAttacker){
		if(null == effect){
			return this;
		}
		if(isAttacker){
			attackerBuffEffects.add(effect);
		}else{
			defenderBuffEffects.add(effect);
		}
		return this ;
	}
	
	/**
	 * 设置抵抗
	 * @param attacker
	 * @param defender
	 */
	public void resist(AbstractRole attacker, AbstractRole defender){
		if(null == attacker || null == defender
				|| attacker.getRoleId().equals(defender.getRoleId())){
			return;
		}
		this.fontList.add(new SkillFontType(attacker, defender, AttrFontSpecialState.Resist));
	}
	
	/**
	 * 设置格挡
	 * @param attacker
	 * @param defender
	 */
	public void block(AbstractRole attacker, AbstractRole defender){
		if(null == attacker || null == defender
				|| attacker.getRoleId().equals(defender.getRoleId())){
			return;
		}
		this.fontList.add(new SkillFontType(attacker, defender, AttrFontSpecialState.Block));
	}
	
	/** 飘字效果 */
	public List<SkillFontType> getFontList(){
		return this.fontList;
	}
	
	
	/**
	 * 在helper技能中使用
	 */
	public static boolean compareAndSetCd(AbstractRole role,short skillId){
		if(null == role ){
			return false ;
		}
		RoleSkillStat stat = role.getSkillStat(skillId);
		if(null == stat){
			return false ;
		}
		Skill skill = GameContext.getSkillApp().getSkill(skillId);
		if(null == skill){
			return false ;
		}
		long now = System.currentTimeMillis();
		// 判断冷却时间
		long t = now - stat.getLastProcessTime() ;
		if (t >= 0 && t < skill.getCd(role) - SkillAdaptor.CD_OFFSET) {
			return false ;
		}
		stat.setLastProcessTime(now);
		return true ;
	}
}
