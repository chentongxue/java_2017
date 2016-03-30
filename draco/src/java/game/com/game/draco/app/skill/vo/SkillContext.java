package com.game.draco.app.skill.vo;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.map.data.NpcBorn;
import sacred.alliance.magic.base.AttrFontColorType;
import sacred.alliance.magic.base.AttrFontSizeType;
import sacred.alliance.magic.base.AttrFontSpecialState;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.Direction;
import sacred.alliance.magic.base.ForceRelation;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.AttrFontInfo;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.buff.Buff;
import com.game.draco.app.buff.BuffEffect;
import com.game.draco.app.buff.MapBuffEffect;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.npc.domain.NpcTemplate;
import com.game.draco.app.skill.config.SkillApplyType;
import com.game.draco.app.skill.config.SkillEffectType;
import com.game.draco.app.skill.config.SkillPassiveType;
import com.game.draco.app.skill.domain.RoleSkillStat;
import com.game.draco.app.skill.vo.scope.TargetScope;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class SkillContext {
	public SkillContext(Skill skill){
		this.skill = skill ;
	}
	public static enum AttrSource {
		attacker,
		defender,
		summoner, //召唤者(eg:宠物拥有者)
		;
	}
	private static final SecureRandom random = new SecureRandom();
	private static final int  TEN_THOUSAND = SkillFormula.TEN_THOUSAND ;
	private static final float  TEN_THOUSAND_F = SkillFormula.TEN_THOUSAND_F ;
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
	@Setter @Getter private Skill skill ;
	///**目标差异*/
	//@Setter private boolean difference = true ;
	@Setter @Getter private SkillPassiveType skillPassiveType ;
	/**附加信息*/
	@Setter @Getter private Object info ;
	@Getter private SkillEffectType skillEffectType ;//技能效果类型
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
	private int defenderTempHpChange = 0;
	private int defenderMpChange = 0 ;
	private int critAtkProbChange = 0; //暴击率修正
	@Getter private int hurtFactorChange = 0;//伤害系数修正 
	@Getter private int hitChange;//命中修正 
	@Getter private int critChange;//暴击修正 
	@Setter @Getter private int inputHurts = 0 ;
	private int defenderAttHurt = 0;
	/**技能参数修正*/
	private Map<AttributeType,List<SkillAdjust>> attackerAdjusts = new HashMap<AttributeType,List<SkillAdjust>>();
	private Map<AttributeType,List<SkillAdjust>> defenderAdjusts = new HashMap<AttributeType,List<SkillAdjust>>();
	private Map<AttributeType,List<SkillAdjust>> skillAdjusts = new HashMap<AttributeType,List<SkillAdjust>>();
	/**buff效果列表*/
	@Getter private List<BuffEffect> defenderBuffEffects = new ArrayList<BuffEffect>();
	/**自己buff效果列表*/
	@Getter private List<BuffEffect> attackerBuffEffects = new ArrayList<BuffEffect>();
	/**召唤者buff效果列表*/
	@Getter private List<BuffEffect> summonerBuffEffects = new ArrayList<BuffEffect>();
	/**地图buff效果*/
	@Getter private List<MapBuffEffect> mapBuffEffects = new ArrayList<MapBuffEffect>();
	/**持久效果类被动技能修改的attriBuffer*/
	@Getter private AttriBuffer attriBuffer = AttriBuffer.createAttriBuffer();
	/** 技能特效状态飘字效果 */
	private List<SkillFontType> fontList = new ArrayList<SkillFontType>();
	
	/** 技能中各系伤害 */
	private List<SkillHurt> skillHurtList = Lists.newArrayList();
	/** 是否触发二次攻击 */
	@Getter @Setter private boolean trigger2Attack;
	/** 是否是系统触发 */
	@Getter @Setter private boolean systemTrigger = false;
	/** 是否判断使用条件 */
	@Getter @Setter private boolean judgeUseCond = true;
	/** 是否是客户端主动请求 */
	@Getter @Setter private boolean skillActiveApply = false;
	/** 是否通知客户端使用技能 */
	@Getter @Setter private boolean channelSkill = false;
	/** 战斗中属性飘字效果 */
	@Getter private Map<Integer, List<AttrFontInfo>> attrFontInfoMap = Maps.newHashMap();
	/** 战斗中特殊状态(击退,击飞)飘字 只有在-306协议中用到这部分数据*/
	@Getter private List<AttrFontInfo> stateFontInfoList = Lists.newArrayList();
	/** 执行技能当前次数 */
	@Getter @Setter private int execSkillTimes = 0;
	/*** 触发技能ID***/
	@Setter @Getter private short triggerSkillId;
	
	
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
	
	public boolean isBoss(AbstractRole npc){
		if(npc.getRoleType() != RoleType.NPC){
			return true;
		}
		NpcInstance n = (NpcInstance)npc;
		if(n.getNpc().npcIsBoss()){
			return false;
		}
		return true;
	}
	
	private int get(AttributeType attriType,AttrSource source){
		AbstractRole actor = (source == AttrSource.attacker)?this.attacker:defender ;
		return this.calc(attriType, source, actor.get(attriType));
	}
	
	//攻击方暴击倍率=系统暴击倍率 + 技能调整
	private float getCritMultiple(){
		if(this.attackType != AttackType.CRIT){
			return 1 ;
		}
		int critAtkProb = this.get(AttributeType.critAtkProb, AttrSource.attacker);
		/*if (this.critAtkProbChange == 0) {
			return Math.max(1, critAtkProb/TEN_THOUSAND_F);
		}*/
		return Math.max(1, (critAtkProb + this.critAtkProbChange)/TEN_THOUSAND_F);
		//return Math.max(1, critAtkProb * (TEN_THOUSAND + this.critAtkProbChange)/(TEN_THOUSAND_F * TEN_THOUSAND_F));
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
	public int getDefenderTempHpResult(){
		return this.defenderTempHpChange ;
	}
	public void setDefenderTempHpResult(){
		this.defenderTempHpChange = defenderHpChange;
	}
	public int getDefenderMpResult(){
		return this.defenderMpChange ;
	}
	
	public void result(){
		if(this.skillEffectType == SkillEffectType.ASSISTANCE) {
			//辅助技能利用appendHpChange来实现，这里不需要计算伤害
			return ;
		}
		if(Util.isEmpty(this.skillHurtList)) {
			this.defenderHpChange = 0;
			return;
		}
		
//		SkillHurtRemit skillHurtRemit = GameContext.getSkillApp()
//				.getSkillHurtRemit(this.attacker.getLevel());
//		if(null == skillHurtRemit) {
//			this.defenderHpChange = 0;
//			return ;
//		}
		float hp = 0;
		float attrHurt = 0;
//		int hurtRemit = skillHurtRemit.getHurtRemit();
		for(SkillHurt skillHurt : this.skillHurtList) {
			hp += calcSkillHurt(skillHurt,false);
			attrHurt += calcSkillHurt(skillHurt,true);
		}
//		hp = Math.max(1, hp);
		//基础伤害
//		hp = (TEN_THOUSAND_F - this.get(AttributeType.hurtRemitRate, AttrSource.defender)) /TEN_THOUSAND_F 
//				* hp 
//        		* this.getCritMultiple();
		//最终伤害=基础伤害 * 伤害系数调整
//		this.defenderHpChange = -(int)((this.getHurtFactorChange() + TEN_THOUSAND_F)/TEN_THOUSAND_F * hp);
		this.defenderHpChange = (int)hurt(hp);
		this.defenderAttHurt = (int)hurt(attrHurt);
	}
	
	private float hurt(float hp){
		if(hp == 0){
			return 0;
		}
		hp = Math.max(1, hp);
		//基础伤害
		hp = (TEN_THOUSAND_F - this.get(AttributeType.hurtRemitRate, AttrSource.defender)) /TEN_THOUSAND_F 
				* hp 
        		* this.getCritMultiple();
		//基础伤害 * 伤害系数调整
		return -((this.getHurtFactorChange() + TEN_THOUSAND_F)/TEN_THOUSAND_F * hp);
	}
	
	/** 某系技能伤害计算 */
	private int calcSkillHurt(SkillHurt skillHurt,boolean flag) {
		//防御减伤=防守方xx防御a/(防守方xx防御a+A)
		//某系伤害=(A%*角色攻击+B点)*(1-防御减伤)
//		float atk = this.get(skillHurt.getAttributeType(), skillHurt.getSource());
		int rit = this.get(AttributeType.rit, AttrSource.defender);
		float hurt = skillHurt.getValue();//(skillHurt.getPercent()/TEN_THOUSAND_F * atk+ skillHurt.getValue()); 
		//减伤=防御方防御/(攻击方破防+防御方防御)
		float defenderHurtRemit = (float)rit/(attacker.get(AttributeType.breakDefense) + rit);
		float damage = hurt * (1-defenderHurtRemit);//(float)hurtRemit/(rit + hurtRemit + defenderHurtRemit));
		
		if(!flag){
			if(skillHurt.getType() == SkillHurtType.SACRED_HURT){
				return Math.max(0, (int)hurt);
			}
			return Math.max(0, (int)damage);
		}
		
		float restrictRate = GameContext.getAttriApp().getAttriRestrictHurtRate(attacker, defender);
		if(restrictRate <= 0.0) {
			return 0;
		}
		int attHurt = Math.max(0, (int)(restrictRate * damage));
		return attHurt;
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
	
	/**
	 * 循环中攻击者对目标使用技能
	 */
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
		this.critAtkProbChange = 0;
//		this.trigger2Attack = false;
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
	 * 攻击者对所有目标使用完技能
	 */
	public void clearAttrFontInfo() {
		if(!Util.isEmpty(attrFontInfoMap)){
			this.attrFontInfoMap.clear();
		}
	
		if(!Util.isEmpty(stateFontInfoList)){
			this.stateFontInfoList.clear();
		}
		
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
	
	public SkillContext appendcritAtkProbChange(int change) {
		this.critAtkProbChange += change;
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
	private SkillContext appendBuff(short buffId,int lv, int effectTime,int probability,AttrSource source){
		if(null == source){
			return this;
			
		}
		if(on(probability)){
			Buff buff = GameContext.getBuffApp().getBuff(buffId);
			if(buff == null){
				return this;
			}
			if(buff.isBlowfly()){
				addStateFont(AttrFontSpecialState.BlowFly);
			}
			this.appendBuff(new BuffEffect(buffId,lv, effectTime),source);
			return this ;
		}
		return this ;
	}
	
	public SkillContext appendBuff(short buffId,int lv, int effectTime,int probability,AttrSource source,Object info){
		if(null == source){
			return this;
			
		}
		if(on(probability)){
			if(info == null){
				this.appendBuff(new BuffEffect(buffId,lv, effectTime),source);
			}else{
				this.appendBuff(new BuffEffect(buffId,lv, effectTime,info),source);
			}
			
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
	public SkillContext appendAttackerBuff(short buffId, int lv, int effectTime, 
			int probability,Object info){
		if(null == attacker) {
			return this;
		}
		Buff buff = GameContext.getBuffApp().getBuff(buffId);
		if(null == buff) {
			return this;
		}
		if(!on(probability)) {
			return this;
		}
		if(info == null){
			this.appendBuff(new BuffEffect(buffId,lv,effectTime),AttrSource.attacker);
		}else{
			this.appendBuff(new BuffEffect(buffId,lv,effectTime,info),AttrSource.attacker);
		}
		return this;
	}
	
	public SkillContext appendAttackerBuff(short buffId, int lv, int effectTime, 
			int probability){
		return this.appendAttackerBuff(buffId, lv, effectTime, probability,null);
	}
	
	/**
	 * 给召唤者加buff
	 * @param buffId
	 * @param lv
	 * @param effectTime
	 * @param probability
	 * @return
	 */
	public SkillContext appendSummonerBuff(short buffId, int lv, int effectTime,	int probability){
		Buff buff = GameContext.getBuffApp().getBuff(buffId);
		if(null == buff) {
			return this;
		}
		if(!on(probability)) {
			return this;
		}
		this.appendBuff(new BuffEffect(buffId,lv,effectTime),AttrSource.summoner);
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
	public SkillContext addStateFont(AttrFontSpecialState state) {
		if(null == attacker || null == defender) {
			return this;
		}
		attacker.getBehavior().addTargetFont(AttrFontSizeType.Common, AttrFontColorType.Special_State, state.getType(), defender, this);
		defender.getBehavior().addSelfFont(AttrFontSizeType.Common, AttrFontColorType.Special_State, state.getType(), this);
		//if(sizeType == AttrFontSizeType.State) {
			//如果是特殊状态类型,则需要保存-306协议中会用到
			int ownerId = defender.getIntRoleId();
			AttrFontInfo fontInfo = GameContext.getBattleApp().creatAttrFontInfo(AttrFontSizeType.Common, 
					AttrFontColorType.Special_State, state.getType(), ownerId, 0);
			this.stateFontInfoList.add(fontInfo);
		//}
		return this;
	}
	
	/**
	 * 删除buff
	 * @param buffId
	 * @param probability
	 * @param source
	 * @return
	 */
	public SkillContext removeBuff(short buffId,int probability,int effectTime,AttrSource source){
		return appendBuff(buffId,0,effectTime,probability,source);
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
		return appendBuff(buffId,-1,0,probability,source);
	}
	
	/**
	 * 给防御者附加buff效果
	 * 如果攻击者的hurtType属性值-防御者的hurtType属性值  > probability 则给防御者施加buff
	 * @param buffId buff效果id
	 * @param lv buff级别(如果lv<=0则为删除buff)
	 * @param probability 几率[0-10000] 100%=10000
	 * @return
	 */
	public SkillContext appendBuff(short buffId, int lv, int effectTime, int probability){
		Buff buff = GameContext.getBuffApp().getBuff(buffId);
		if(null == buff) {
			return this;
		}
		
		if(buff.getHurtType() <= 0) {
			return appendBuff(buffId,lv,effectTime,probability,AttrSource.defender);
		}
		
		SkillHurtType hurtType = SkillHurtType.getType(buff.getHurtType());
		if(null == hurtType) {
			return appendBuff(buffId,lv,effectTime,probability,AttrSource.defender);
		}
		//附加抗性计算
		AttributeType atkType = hurtType.getAtkType();
		AttributeType ritType = hurtType.getRitType();
		int hitRate = probability + this.get(atkType, AttrSource.attacker)
				- this.get(ritType, AttrSource.defender);
		return appendBuff(buffId,lv,effectTime,hitRate,AttrSource.defender);
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
			this.appendBuff(new BuffEffect((short)-1,-1,0),source);
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
	
	/** 附加技能某系伤害 */
	public SkillContext appendSkillHurt(SkillHurtType type, int percent, int value,AttrSource source, AttributeType attributeType) {
		if(null == type || (percent == 0 && value == 0)) {
			return this ;
		}
		this.skillHurtList.add(new SkillHurt(type, percent, value, source, attributeType));
		return this ;
	}
	
	
	/**添加技能效果*/
	private SkillContext appendBuff(BuffEffect effect, AttrSource source){
		if(null == effect){
			return this;
		}
		if(AttrSource.attacker == source){
			attackerBuffEffects.add(effect);
		}else if(AttrSource.defender == source){
			defenderBuffEffects.add(effect);
		}else {
			summonerBuffEffects.add(effect);
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
	 * 添加飘字信息
	 * @param receiveRoleId
	 * @param fontInfo
	 */
	public void addAttrFontInfo(int receiveRoleId, AttrFontInfo fontInfo) {
		List<AttrFontInfo> fontInfoList = this.attrFontInfoMap.get(receiveRoleId);
		if(null == fontInfoList) {
			fontInfoList = Lists.newArrayList();
			this.attrFontInfoMap.put(receiveRoleId, fontInfoList);
		}
		fontInfoList.add(fontInfo);
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
	
	public int getSkillHurt(int areaId){
		return getSkill().getSkillHurt(this,areaId);
	}
	
	public int getSkillHurtProb(int areaId){
		return getSkill().getSkillHurtProb(this,areaId);
	}
	
	public int getSkillBuffProb(int buffId,int areaId){
		return getSkill().getSkillBuffProb(this,buffId,areaId);
	}
	
	public Map<Integer, TargetScope> getTargetScopeMap(){
		return getSkill().getTargetScopeMap(this);
	}
	
	public void delAllRoleBuff(){
		Collection<RoleInstance> list = attacker.getMapInstance().getRoleList();
		for(RoleInstance role : list){
			GameContext.getUserBuffApp().cleanBuffById(role);
		}
	}
	
	public void summonNpc(String npcId,String roleId){
		int mapX = this.getDefender().getMapX();
		int mapY = this.getDefender().getMapY();
		
		NpcBorn npcBorn = new NpcBorn();
		NpcTemplate npcTemplate = GameContext.getNpcApp().getNpcTemplate(npcId);
		if (npcTemplate == null) {
			return;
		}
		npcBorn.setBornmapgxbegin(mapX);
		npcBorn.setBornmapgybegin(mapY+10);
		npcBorn.setBornmapgxend(mapX);
		npcBorn.setBornmapgyend(mapY+10);
		npcBorn.setBornnpccount(1);
		npcBorn.setBornnpcid(npcId);
		npcBorn.setBornNpcDir(Direction.DOWN.getType());
		this.getDefender().getMapInstance().summonCreateNpc(npcBorn,roleId);
	}
	
	public void delRoleBuff(short buffId,int radius){
		Collection<RoleInstance> roleList = attacker.getMapInstance().getRoleList();
		for(RoleInstance target : roleList){
			boolean isArea = Util.inCircle(attacker.getMapX(), attacker.getMapY(), target.getMapX(), target.getMapY(), radius);
			if(isArea){
				GameContext.getUserBuffApp().delBuffStat(target, buffId,false);
			}
		}
	}

	public int getDefenderAttHurtResult() {
		return defenderAttHurt;
	}
	
	public void addFontState(AttrFontSpecialState state){
		attacker.getBehavior().addTargetFont(
				AttrFontSizeType.Common,
				AttrFontColorType.Special_State,
				state.getType(), defender, this);
		
	}

}
