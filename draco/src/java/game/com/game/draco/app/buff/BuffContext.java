package com.game.draco.app.buff;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.base.AttriLevelType;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.StateType;
import sacred.alliance.magic.vo.AbstractRole;

import com.game.draco.GameContext;
import com.game.draco.app.buff.stat.BuffStat;
import com.game.draco.app.skill.vo.SkillFormula;
import com.game.draco.app.skill.vo.SkillHurtType;

public class BuffContext {
	public static final float HURT_RATE = 0.02f ;
	private static final SecureRandom random = new SecureRandom();
	private static final int  TEN_THOUSAND = SkillFormula.TEN_THOUSAND  ;
	private static final float  TEN_THOUSAND_F = SkillFormula.TEN_THOUSAND_F  ;
	private BuffStat buffStat;
	//策划脚本返回效果
	/**属性修改 目标*/ 
	private AttriBuffer ownerAttriBuffer = AttriBuffer.createAttriBuffer();
	/**属性修改*/
	private AttriBuffer casterAttriBuffer = AttriBuffer.createAttriBuffer();
	/**buff效果*/
	private List<BuffEffect> ownerBuffEffects = new ArrayList<BuffEffect>();
	private List<BuffEffect> casterBuffEffects = new ArrayList<BuffEffect>();
	/**buff所用者触发的技能*/
	private Map<Short,SkillInfo> useSkillsOfOwner = new HashMap<Short,SkillInfo>();
	/**buff施加者触发的技能*/
	private Map<Short,SkillInfo> useSkillsOfCaster = new HashMap<Short,SkillInfo>();
	/**吸收量*/
	private int absorb = 0 ;
	/**状态量*/
	private StateType stateType = null ;
	private BuffFuncPoint buffFuncPoint ;
	/**buff是否删除*/
	private boolean remove = false ;
	
	/**提供吸收使用,传入伤害总量*/
	private int inputHurts = 0 ;
	/**提供吸收使用,吸收量*/
	private int absorbed = 0;
	/**
	 * 是否实时同步属性
	 */
	private boolean realTimeNotifyAttri = true;
	
	/** 是否是通道技能buff */
	@Getter @Setter private boolean channelSkillBuff = false;
	
	public BuffFuncPoint getBuffFuncPoint() {
		return buffFuncPoint;
	}

	public void setBuffFuncPoint(BuffFuncPoint buffFuncPoint) {
		this.buffFuncPoint = buffFuncPoint;
	}
	
	public static int random(){
		return Math.abs(random.nextInt()) % TEN_THOUSAND + 1;
	}
	
	public static boolean on(int probability){
		if(probability >= TEN_THOUSAND){
			return true ;
		}else if(probability<=0){
			return false;
		}
		return (Math.abs(random.nextInt()) % TEN_THOUSAND) < probability ;
	}
	
	public int getBuffLevel(){
		return buffStat.getBuffLevel();
	}
	
	public int getBuffLayer(){
		return buffStat.getLayer();
	}
	
	public void appendAbsorb(int absorb){
		this.absorb += absorb ;
	}
	
	public void appendState(StateType stateType){
		this.stateType = stateType ;
	}
	
	/**添加技能效果*/
	public BuffContext appendBuff(short buffId,int level,int effectTime, int probability){
		if(on(probability)){
			this.appendBuff(new BuffEffect(buffId,level,effectTime));
		}
		return this ;
	}
	
	/**添加技能效果*/
	public void appendUserBuff(AbstractRole role,AbstractRole caster,short buffId,int buffLevel,int probability){
		if(on(probability)){
			GameContext.getUserBuffApp().addBuffStat(role, caster, buffId, buffLevel);
		}
	}
	
	/**Caster添加技能效果*/
	public BuffContext appendCasterBuff(short buffId,int level,int effectTime ,int probability){
		if(on(probability)){
			casterBuffEffects.add(new BuffEffect(buffId,level,effectTime));
		}
		return this ;
	}
	
	/**添加技能效果*/
	private BuffContext appendBuff(BuffEffect effect){
		if(null != effect){
			ownerBuffEffects.add(effect);
		}
		return this ;
	}
	
	public BuffContext appendAttriValue(AttributeType attriType,int changeValue){
		return this.appendAttri(attriType, changeValue,0);
	}
	
	public BuffContext appendAttriValue(AttributeType attriType,int changeValue,int percen){
		return this.appendAttri(attriType, changeValue,percen);
	}
	
	public BuffContext appendAttri(AttributeType attriType,int changeValue,int changePercen){
		this.ownerAttriBuffer.append(attriType, changeValue,(float)changePercen/TEN_THOUSAND );
		return this ;
	}
	
	public BuffContext appendHurt(SkillHurtType type, int value, int percent) {
		if(null == this.buffStat) {
			return this;
		}
		AbstractRole caster = this.buffStat.getCaster();
		AbstractRole owner = this.buffStat.getOwner();
		if(null == caster || null == owner) {
			return this;
		}
//		SkillHurtRemit skillHurtRemit = GameContext.getSkillApp().getSkillHurtRemit(owner.getLevel());
//		if(null == skillHurtRemit) {
//			return this;
//		}
		
		
//		int hurtRemit = skillHurtRemit.getHurtRemit();
		AttributeType atkType = type.getAtkType();
		AttributeType ritType = type.getRitType();
		//防御减伤=防守方xx防御a/(防守方xx防御a+A)
		//某系伤害=(A%*角色物攻/冰攻/火攻+B)*(1-防御减伤)
		
		float atk = caster.get(atkType);
		int rit = owner.get(ritType);
		float hurt = ((float)percent/TEN_THOUSAND_F) * atk+ value; 
//		int hurt = (int)((atk * percent / TEN_THOUSAND + value) * hurtRemit / (rit + hurtRemit));
		if(hurt == 0) {
			return this;
		}
		
		//减伤=防御方防御/(攻击方破防+防御方防御)
		float defenderHurtRemit = (float)rit/(caster.get(AttributeType.breakDefense) + rit);
		float damage = hurt * (1-defenderHurtRemit);
		
		if(type == SkillHurtType.SACRED_HURT){
			hurt = Math.max(0, (int)hurt);
			this.ownerAttriBuffer.append(AttributeType.curHP, -hurt,0);
			return this;
		}
		
		float restrictRate = GameContext.getAttriApp().getAttriRestrictHurtRate(caster, owner);
		if(restrictRate <= 0.0) {
			this.ownerAttriBuffer.append(AttributeType.curHP, -damage,0);
			return this;
		}
		int attHurt = Math.max(0, (int)(restrictRate * damage));
		
		this.ownerAttriBuffer.append(AttributeType.curHP, -attHurt,0);
		return this;
	}
	
	public BuffContext appendAttriPercen(AttributeType attriType,int changePercen){
		return this.appendAttri(attriType, 0,changePercen);
	}
	
	public BuffContext appendCasterAttriValue(AttributeType attriType,int changeValue){
		return this.appendCasterAttri(attriType, changeValue,0);
	}
	
	public BuffContext appendCasterAttri(AttributeType attriType,int changeValue,int changePercen){
		if(this.buffFuncPoint != BuffFuncPoint.begin){
			//process,timeover,remove只允许修改积累属性
			if(!AttriLevelType.orig.verify(attriType)){
				return this ;
			}
			
		}
		
		this.casterAttriBuffer.append(attriType, changeValue,(float)changePercen/TEN_THOUSAND );
		return this ;
	}
	
	public BuffContext appendCasterAttriPercen(AttributeType attriType,int changePercen){
		return this.appendCasterAttri(attriType, 0,changePercen);
	}
	
	/**
	 * 使用技能
	 * @param skillId 技能id
	 * @param level 技能级别
	 * @param role 技能使用者
	 * @return
	 */
	public BuffContext appendUseSkill(short skillId,int level,AbstractRole role,Object info){
		if(level<=0 || null == role){
			return this ;
		}
		if (null != this.getOwner()
				&& this.getOwner().getIntRoleId() == role.getIntRoleId()) {
			this.useSkillsOfOwner.put(skillId, new SkillInfo(level,info));
			return this ;
		}
		if (null != this.getCaster()
				&& this.getCaster().getIntRoleId() == role.getIntRoleId()) {
			this.useSkillsOfCaster.put(skillId, new SkillInfo(level,info));
		}
		return this ;
	}
	
	public BuffContext appendUseSkill(short skillId,int level,AbstractRole role){
		return this.appendUseSkill(skillId, level, role, null);
	}
	
	public void release(){
		this.buffStat = null ;
		this.ownerAttriBuffer.clear();
		this.ownerBuffEffects.clear();
		this.absorb=0;
		this.stateType = null ;
		this.buffFuncPoint = null ;
		this.inputHurts = 0 ;
		this.absorbed = 0 ;
		this.useSkillsOfOwner.clear();
		this.useSkillsOfCaster.clear();
		this.remove = false ;
		this.realTimeNotifyAttri = true ;
	}

	public BuffStat getBuffStat() {
		return buffStat;
	}

	public void setBuffStat(BuffStat buffStat) {
		this.buffStat = buffStat;
	}

	public AttriBuffer getOwnerAttriBuffer() {
		return ownerAttriBuffer;
	}
	
	public AttriBuffer getCasterAttriBuffer() {
		return this.casterAttriBuffer;
	}

	public List<BuffEffect> getOwnerBuffEffects() {
		return ownerBuffEffects;
	}
	
	public List<BuffEffect> getCasterBuffEffects() {
		return this.casterBuffEffects;
	}
	
	public int getAbsorb() {
		return absorb;
	}

	public AbstractRole getOwner(){
		if(null == buffStat){
			return null ;
		}
		return buffStat.getOwner();
	}
	
	public AbstractRole getCaster(){
		if(null == this.buffStat){
			return null ;
		}
		return buffStat.getCaster();
	}
	
	public Buff getBuff(){
		if(null == buffStat){
			return null ;
		}
		return buffStat.getBuff();
	}

	public StateType getStateType() {
		return stateType;
	}

	public int getInputHurts() {
		return inputHurts;
	}

	public void setInputHurts(int inputHurts) {
		this.inputHurts = inputHurts;
	}

	public int getAbsorbed() {
		return absorbed;
	}

	public void setAbsorbed(int absorbed) {
		this.absorbed = absorbed;
	}

	public boolean isRemove() {
		return remove;
	}

	public void setRemove(boolean remove) {
		this.remove = remove;
	}

	public Map<Short, SkillInfo> getUseSkillsOfOwner() {
		return useSkillsOfOwner;
	}

	public Map<Short, SkillInfo> getUseSkillsOfCaster() {
		return useSkillsOfCaster;
	}
	
	public @Data class SkillInfo{
		private int lv ;
		private Object info ;
		public SkillInfo(int lv,Object info){
			this.lv = lv ;
			this.info = info ;
		}
	}

	public boolean isRealTimeNotifyAttri() {
		return realTimeNotifyAttri;
	}

	public void setRealTimeNotifyAttri(boolean realTimeNotifyAttri) {
		this.realTimeNotifyAttri = realTimeNotifyAttri;
	}
	
	public void getBuffHurt(){
		getBuff().getBuffHurt(this);
	}
	
	public void addAttr(){
		getBuff().addAttr(this);
	}
	
}
