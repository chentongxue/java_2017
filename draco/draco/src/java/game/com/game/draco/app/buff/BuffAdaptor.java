package com.game.draco.app.buff;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.base.AttrFontColorType;
import sacred.alliance.magic.base.AttrFontSizeType;
import sacred.alliance.magic.base.AttrFontSpecialState;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.vo.AbstractRole;

import com.game.draco.GameContext;
import com.game.draco.app.buff.BuffContext.SkillInfo;
import com.game.draco.app.buff.stat.BuffStat;
import com.game.draco.app.skill.config.SkillApplyType;
import com.game.draco.app.skill.vo.Skill;
import com.game.draco.app.skill.vo.SkillAdaptor;
import com.game.draco.app.skill.vo.SkillContext;
import com.google.common.collect.Maps;


public abstract  class BuffAdaptor extends Buff{
	protected final static Logger logger = LoggerFactory.getLogger(BuffAdaptor.class);
	private final static int DEFAULT_INTERVAL = 1000;
	public BuffAdaptor(short buffId) {
		super(buffId);
	}
	private boolean transClean = true;
	protected Map<Integer,BuffDetail> buffDetails = Maps.newHashMap();
	
	/**buff添加时刻效果,策划接口*/
	public void beginEffect(BuffContext context){
	}

	/**buff执行效果,策划接口*/
	public void processEffect(BuffContext context) {
	}

	/**buff中断效果,策划接口*/
	public void removeEffect(BuffContext context){
	}

	/**buff超时效果,策划接口*/
	public void timeOverEffect(BuffContext context){
	}

	protected abstract void store(BuffContext context) ;

	protected abstract void resume(BuffContext context);
	
	protected abstract void execute(BuffContext context,BuffFuncPoint fp);
	
	protected abstract boolean hasProcess(BuffContext context);
	
	
	@Override
	public String getBuffDesc(int buffLevel) {
		BuffDetail detail =  this.getBuffDetail(buffLevel);
		return detail.getDesc();
	}
	
	@Override
	public String getName(int buffLevel) {
		BuffDetail detail =  this.getBuffDetail(buffLevel);
		if(null == detail){
			return this.getBuffName();
		}
		return detail.getName();
	}

	@Override
	public int getIntervalTime(int buffLevel) {
		BuffDetail detail =  this.getBuffDetail(buffLevel);
		int value = detail.getIntervalTime();
		//判断buff是否属性效果,如果是属性效果间隔<=0时,默认为1000ms(因为策划常配置错误)
		if(value <=0 && EffectType.attribute == this.effectType
				&& BuffTimeType.continued == this.getTimeType() ){
			return DEFAULT_INTERVAL ;
		}
		return value ;
	}

	@Override
	public int getPersistTime(int buffLevel) {
		BuffDetail detail = this.getBuffDetail(buffLevel);
		if(null == detail){
			return 0 ;
		}
		return detail.getPersistTime();
	}
	
	protected void putBuffDetail(Map<Integer,BuffDetail> details){
		this.buffDetails.clear();
		this.buffDetails.putAll(details);
		BuffDetail detail = this.buffDetails.get(1);
		this.init(detail);
	}
	
	/**
	 * 根据buff配置表信息初始化buff实例
	 * @param detail
	 */
	public void init(BuffDetail detail){
		if(null == detail){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("buff detail is exist,buffId=" + this.buffId);
			return ;
		}
		this.setTimeType(BuffTimeType.get(detail.getTimeType()));
		this.setCategoryType(BuffCategoryType.get(detail.getCategoryType()));
		this.iconId = detail.getIconId();
		this.effectId = detail.getEffectId();
		this.dieLost = detail.isDieLost();
		this.offlineLost = detail.isOfflineLost();
		this.offlineTiming = detail.isOfflineTiming();
		this.hatredPercent = detail.getHatredPercent();
		this.hatredAdd = detail.getHatredAdd();
		this.beingType = detail.getBeingType();
		this.replaceType = detail.getReplaceType();
		this.notReplaceDesc = detail.getNotReplaceDesc();
		//互斥系列
		this.groupId = detail.getGroupId();
		//变身是否清除
		this.transClean = !detail.isTransNoClean();
		this.hurtType = detail.getHurtType();
		this.zoom = detail.getZoom();
		String discolor = detail.getDiscolor();
		if(!Util.isEmpty(discolor)) {
			this.discolor = (int)Long.parseLong(detail.getDiscolor(), 16);
		}
	}

	@Override
	public void activeAttack(BuffContext context) {
		
	}


	@Override
	public void begin(BuffContext context) {
		context.setBuffFuncPoint(BuffFuncPoint.begin);
		try {
			this.beginEffect(context);
		} catch (Exception e) {
			logger.error("beginEffect error",e);
		}
		this.store(context);
		this.execute(context, BuffFuncPoint.begin);
		this.ifDeath(context);
	}
	

	@Override
	public void process(BuffContext context) {
		if(!this.hasProcess(context)){
			return ;
		}
		context.setBuffFuncPoint(BuffFuncPoint.process);
		try {
			this.processEffect(context);
		} catch (Exception e) {
			logger.error("processEffect error",e);
		}
		this.execute(context, BuffFuncPoint.process);
		this.ifDeath(context);
	}
	
	@Override
	public void remove(BuffContext context) {
		context.setBuffFuncPoint(BuffFuncPoint.remove);
		try {
			this.removeEffect(context);
		} catch (Exception e) {
			logger.error("removeEffect error",e);
		}
		this.execute(context, BuffFuncPoint.remove);
		this.resume(context);
		this.ifDeath(context);
	}
	
	
	
	@Override
	public void timeOver(BuffContext context) {
		context.setBuffFuncPoint(BuffFuncPoint.timeover);
		try {
			this.timeOverEffect(context);
		} catch (Exception e) {
			logger.error("timeOverEffect error",e);
		}
		this.execute(context, BuffFuncPoint.timeover);
		this.resume(context);
		this.ifDeath(context); 
	}
	
	private void ifDeath(BuffContext context){
		if(this.effectType == EffectType.flag){
			return ;
		}
		AbstractRole owner = context.getOwner();
		if(null == owner || !owner.isDeath()){
			return ;
		}
		try {
			GameContext.getBattleApplication().killedRole(
					context.getCaster(), owner);
		} catch (ServiceException e) {
			logger.error("buff exec jude death", e);
		}
	}

	
	@Override
	public boolean isDebuff() {
		return categoryType == BuffCategoryType.debuff ;
	}

	protected void execSkills(BuffContext context){
		this.useSkills(context.getOwner(), context.getUseSkillsOfOwner(), context.isSetEffectTime());
		this.useSkills(context.getCaster(), context.getUseSkillsOfCaster(), context.isSetEffectTime());
	}
	
	private void useSkills(AbstractRole role,Map<Short,SkillInfo> skills, boolean setEffectTime){
		if(null == role || Util.isEmpty(skills)){
			return ;
		}
		for(Iterator<Map.Entry<Short,SkillInfo>> it = skills.entrySet().iterator();it.hasNext();){
			Map.Entry<Short,SkillInfo> entry = it.next();
			SkillInfo skillInfo = entry.getValue();
			if(skillInfo.getLv()<=0){
				continue ;
			}
			short skillId = entry.getKey();
			Skill skill = GameContext.getSkillApp().getSkill(skillId);
			if(null == skill
					|| skill.getSkillApplyType() != SkillApplyType.active){
				continue ;
			}
			SkillContext context = new SkillContext(skill);
			context.setAttacker(role);
			context.setSkillLevel(skillInfo.getLv());
			context.setInfo(skillInfo.getInfo());
			context.setDefender(role.getTarget());
			//buff触发的技能不需要判断使用条件
			context.appendSkillState(true, false);
			//buff触发的技能是否需要设置效果时间
			context.setSetEffectTime(setEffectTime);
			//使用技能
			((SkillAdaptor)skill).use(context);
			context.release();
			context = null ;
		}
	}


	public BuffDetail getBuffDetail(int buffLv) {
		if(buffLv<=0 || 0 == this.buffDetails.size()){
			return null ;
		}
		BuffDetail detail = this.buffDetails.get(buffLv);
		if(null != detail){
			return detail ;
		}
		if(null == detail){
			logger.error("--- buff error: buffId=" + this.buffId + "level=" + buffLv + ",the detail of this level is null.");
		}
		return this.buffDetails.get(this.buffDetails.size());
	}

	@Override
	public int getMaxLevel(){
		if(null == this.buffDetails){
			return 0 ;
		}
		return this.buffDetails.size();
	}
	
	@Override
	public void buffResist(AbstractRole player,AbstractRole caster){
		//技能buff失败时,弹出战斗数字“抵抗”
		if(null != player){
			player.getBehavior().addSelfFont(AttrFontSizeType.Cycle, AttrFontColorType.Special_State, 
					AttrFontSpecialState.Resist.getType());
			player.getBehavior().notifyAttrFont();
		}
		if(null != caster){
			caster.getBehavior().addTargetFont(AttrFontSizeType.Cycle, AttrFontColorType.Special_State, 
					AttrFontSpecialState.Resist.getType(), player);
			caster.getBehavior().notifyAttrFont();
		}
	}

	@Override
	public BuffAddResult getReplaceResult(AbstractRole player,
			AbstractRole caster,int currAddBuffLevel,boolean mustSuccess) {
		if(this.getTimeType() != BuffTimeType.continued){
			//单次buff直接加上
			BuffAddResult result = new BuffAddResult();
			result.setReplaceType(BuffReplaceType.replace.getType());
			return result;
		}
		Collection<BuffStat> list = player.getReceiveBuffCopy();
		//判断buff作用范围类型： 0：团队buff（唯一）1：个人（共存）
		boolean isUnique = (BuffBeingType.unique.getType() == this.getBeingType());
		for (BuffStat buffStat : list) {
			if(groupId == buffStat.getBuff().getGroupId()
					 && (isUnique || (buffStat.getCasterRoleId().equals(caster.getRoleId())))){
					return this.buildReplaceResult(buffStat, currAddBuffLevel);
			}
		}
		BuffAddResult result = new BuffAddResult();
		result.setReplaceType(BuffReplaceType.replace.getType());
		return result;
	}
	
	private BuffAddResult buildReplaceResult(BuffStat replaceBuffStat,int currAddBuffLevel){
		BuffAddResult result = new BuffAddResult();
		//新buff等级小于等于旧buff等级
		if (currAddBuffLevel < replaceBuffStat.getBuffLevel()) {
			result.setReplaceType(BuffReplaceType.failure.getType());
			return result ;
		}
		result.setReplaceBuffStat(replaceBuffStat);
		result.setReplaceType(replaceType);
		return result ;
	}
	
	@Override
	public boolean isTransClean() {
		return this.transClean ;
	}
}
