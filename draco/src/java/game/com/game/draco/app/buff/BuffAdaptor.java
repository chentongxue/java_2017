package com.game.draco.app.buff;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.base.AttrFontColorType;
import sacred.alliance.magic.base.AttrFontSizeType;
import sacred.alliance.magic.base.AttrFontSpecialState;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;

import com.game.draco.GameContext;
import com.game.draco.app.buff.BuffContext.SkillInfo;
import com.game.draco.app.buff.domain.BHurtC;
import com.game.draco.app.buff.stat.BuffStat;
import com.game.draco.app.skill.config.SkillApplyType;
import com.game.draco.app.skill.vo.Skill;
import com.game.draco.app.skill.vo.SkillAdaptor;
import com.game.draco.app.skill.vo.SkillContext;
import com.game.draco.app.skill.vo.SkillHurtType;
import com.google.common.collect.Maps;


public abstract  class BuffAdaptor extends Buff{
	protected final static Logger logger = LoggerFactory.getLogger(BuffAdaptor.class);
	private final static int DEFAULT_INTERVAL = 1000;
	public BuffAdaptor(short buffId) {
		super(buffId);
	}
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
	public int getIntervalTime(int buffLevel) {
		int value = this.getIntervalTime();
		//判断buff是否属性效果,如果是属性效果间隔<=0时,默认为1000ms(因为策划常配置错误)
		if(value <=0 && EffectType.attribute == this.effectType
				&& BuffTimeType.continued == this.getTimeType() ){
			return DEFAULT_INTERVAL ;
		}
		return value ;
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
			GameContext.getBattleApp().killedRole(
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
		this.useSkills(context.getOwner(), context.getUseSkillsOfOwner(), 
				context.isChannelSkillBuff(), context.getBuffStat());
		this.useSkills(context.getCaster(), context.getUseSkillsOfCaster(), 
				context.isChannelSkillBuff(), context.getBuffStat());
	}
	
	private void useSkills(AbstractRole role,Map<Short,SkillInfo> skills,
			boolean channelSkillBuff, BuffStat buffStat){
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
			//通道技能buff产生通道技能
			context.setChannelSkill(channelSkillBuff);
			//技能使用次数
			buffStat.addExecSkillTimes();
			context.setExecSkillTimes(buffStat.getExecSkillTimes());
			//使用技能
			((SkillAdaptor)skill).use(context);
			context.release();
			context = null ;
		}
	}

	@Override
	public BuffDetail getBuffDetail(int buffLv) {
		if(buffLv<=0 || 0 == this.buffDetails.size()){
			return null ;
		}
		BuffDetail detail = this.buffDetails.get(buffLv);
		if(null != detail){
			return detail ;
		}
		if(null == detail){
			logger.error("--- buff error: buffId=" + this.buffId + ", level=" + buffLv + ",the detail of this level is null.");
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
	public void getBuffHurt(BuffContext context) {
		if(null == this.buffDetails){
			return ;
		}
		BuffDetail detail = buffDetails.get(context.getBuffLevel());
		if(null == detail){
			return ;
		}
		if(this.getSkillContinue() > 0){
			return;
		}
		List<BHurtC> bHurtList = detail.getBHurtList();
		if(Util.isEmpty(bHurtList)){
			return ;
		}
		for(BHurtC hurt : bHurtList){
			if(hurt == null){
				return ;
			}
			
			if(hurt.getDamage() == 1){
				int attrValue = 0;
				if(hurt.getTargetType() == 0){
					attrValue = context.getCaster().get(hurt.getAttrType()) ; 
				}else{
					attrValue = context.getOwner().get(hurt.getAttrType()) ; 
				}
				int value = Util.getAbc(hurt.getA(),hurt.getB(),hurt.getC(),hurt.getD(),attrValue,false,true);
				
				value = calcStack(context,value);
				if(value != 0){
					if(hurt.getModifyTargetType() == 1){
						context.appendHurt(SkillHurtType.getType(hurt.getHurtType()), value, hurt.getA());
					}
				}
			}
			
			if(hurt.getDamage() == 2){
				addAttr(context);
			}
		}
	}
	
	@Override
	public void addAttr(BuffContext context) {
		if(null == this.buffDetails){
			return ;
		}
		BuffDetail detail = buffDetails.get(context.getBuffLevel());
		if(detail == null){
			return;
		}
		
		List<BHurtC> bHurtCList = detail.getBHurtList();
		for(BHurtC hurt : bHurtCList){
			
			if(hurt == null){
				continue;
			}
			
			if(hurt.getDamage() == 1){
				return;
			}
			
			int attrValue = 0;
			if(hurt.getTargetType() == 0){
				attrValue = context.getCaster().get(hurt.getAttrType()) ; 
			}else{
				attrValue = context.getOwner().get(hurt.getAttrType()) ; 
			}
			
			int value = Util.getAbc(hurt.getA(),hurt.getB(),hurt.getC(),hurt.getD(),attrValue,false,true);
			
			if(this.getEffectType() == EffectType.absorb){
				context.appendAbsorb(value);
				continue;
			}
			
			if(value != 0){
			
				if(hurt.getReduce() ==0){
					value = -value;
				}
				
				value = calcStack(context,value);
				
				context.appendAttri(AttributeType.get(hurt.getModifyTargetAttr()), value,0);
			}
		}
	}
	
	/**
	 * 计算叠层
	 * @param context
	 * @param value
	 */
	private int calcStack(BuffContext context,int value){
		if(context.getBuff().isStack()){
			boolean isStack = true;
			if(context.getBuff().getMaxLayer() != -1){
				if(context.getBuffLayer() > context.getBuff().getMaxLayer()){
					isStack = false;
				}
			}
			if(isStack){
				value *= context.getBuffLayer();
			}
		}
		return value;
	}
}
