package com.game.draco.app.buff;

import java.util.List;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.base.AttrFontColorType;
import sacred.alliance.magic.base.AttrFontSizeType;
import sacred.alliance.magic.base.AttrFontSpecialState;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;

import com.game.draco.GameContext;
import com.game.draco.app.skill.vo.SkillFormula;

public class RoleBuff extends BuffAdaptor{

	
	@Override
	public String getNotReplaceDesc(){
		//roleBuff没有提示
		return "" ;
	}
	
	protected boolean doHealRate(){
		return true ;
	}
	
	public RoleBuff(short buffId) {
		super(buffId);
	}

	@Override
	protected void store(BuffContext context){
		//到此处buffstat已经添加到角色上
		EffectType effectType = context.getBuff().getEffectType();
		effectType.getEffect().store(context);
	}

	@Override
	protected void resume(BuffContext context){
		//到此处buffstat已经删除
		EffectType effectType = context.getBuff().getEffectType();
		effectType.getEffect().resume(context);
	}
	
	@Override
	protected void execute(BuffContext context, BuffFuncPoint fp) {
		if (null == context || null == fp || this.effectType == EffectType.flag) {
			return;
		}
		try {
			boolean[] v1 = this.execAttribute(context, fp);
			boolean[] v2 = this.execBuff(context, fp);
			if(context.isRealTimeNotifyAttri()){
				if (v1[0] || v2[0]) {
					// 说明属性有变化
					context.getOwner().getBehavior().notifyAttribute();
				}
				if (v1[1] || v2[1]) {
					// 说明属性有变化
					context.getCaster().getBehavior().notifyAttribute();
				}
			}
			this.execSkills(context);
		} catch (Exception ex) {
			logger.error("", ex);
		}
	}
	
	/**执行buff效果*/
	private boolean[] execBuff(BuffContext context,BuffFuncPoint fp){
		boolean v1 = this.execBuff(context.getOwner(),context.getCaster(),context.getOwnerBuffEffects(),fp);
		boolean v2 = this.execBuff(context.getCaster(),context.getCaster(),context.getCasterBuffEffects(),fp);
		return new boolean[]{v1,v2} ;
	}
	
	private boolean execBuff (AbstractRole buffOwner,AbstractRole buffCaster,
			List<BuffEffect> effects,BuffFuncPoint fp){
		if(null == buffOwner || Util.isEmpty(effects)){
			return false;
		}
		for(BuffEffect effect : effects){
			if(effect.getBuffLv() > 0){
				GameContext.getUserBuffApp().addBuffStat(buffOwner,
						buffCaster, effect.getBuffId(), effect.getBuffLv());
			}else{
				GameContext.getUserBuffApp().delBuffStat(buffOwner,
						effect.getBuffId(), fp == BuffFuncPoint.timeover);
			}
		}
		return true ;
	}
	
	private boolean execAttribute(AbstractRole role,AttriBuffer buffer,BuffFuncPoint fp, AbstractRole caster){
		if(null == role || null == buffer || buffer.isEmpty() /*|| null == caster*/){
			return false ;
		}
		boolean isSendToCaster = false;
		if(null != caster && role.getIntRoleId() != caster.getIntRoleId()){
			isSendToCaster = true;
		}
		AttriItem hpItem = buffer.removeAttriItem(AttributeType.curHP);
//		AttriItem mpItem = buffer.removeAttriItem(AttributeType.curMP);
//		AttriItem hurtItem = buffer.removeAttriItem(AttributeType.sacredAtk);
		
		int hp = this.getBuffHurt(hpItem, role) ;
//		int mp = this.getBuffHurt(mpItem, role);
//		int hurt = this.getBuffHurt(hurtItem, role);
		
//		hp = Util.safeIntAdd(hp, hurt);
		AttrFontColorType colorType = AttrFontColorType.Be_Hurt;
		if(hp > 0){
			colorType = AttrFontColorType.HP_Revert;
			if(this.doHealRate()){
				//治疗系数
				int healRate = role.get(AttributeType.healRate);
				hp = (int)(healRate/(float)SkillFormula.TEN_THOUSAND*hp);
			}
		}
		if(hp<0){
			//-hp,考虑吸收
			int absorbValue = GameContext.getUserBuffApp().hurtAbsorb(role,(int)-hp);
			if(absorbValue >0){
				 //标识吸收
				hp = hp + absorbValue ;
				role.getBehavior().addSelfFont(AttrFontSizeType.Common, AttrFontColorType.Special_State, AttrFontSpecialState.Absorb.getType());
				if(isSendToCaster){
					caster.getBehavior().addTargetFont(AttrFontSizeType.Common, AttrFontColorType.Special_State, AttrFontSpecialState.Absorb.getType(), role);
				}
			}
		}
		if(0 == hp && buffer.isEmpty()){
			role.getBehavior().notifyAttrFont();
			if(isSendToCaster){
				caster.getBehavior().notifyAttrFont();
			}
			return false ;
		}
		buffer.append(AttributeType.curHP, hp);
//		buffer.append(AttributeType.curMP, mp);
		GameContext.getUserAttributeApp().changeAttribute(role, buffer, false);
		//发送飘字通知
		AttrFontSizeType fontSizeType = AttrFontSizeType.Cycle;
		if(BuffFuncPoint.begin == fp){
			fontSizeType = AttrFontSizeType.Common;
		}
		role.getBehavior().addSelfFont(fontSizeType, colorType, hp);
//		role.getBehavior().addSelfFont(fontSizeType, AttrFontColorType.MP_Change, mp);
		role.getBehavior().notifyAttrFont();
		if(isSendToCaster){
			if(hp < 0){
				colorType = AttrFontColorType.Skill_Attack;
			}
			caster.getBehavior().addTargetFont(fontSizeType, colorType, hp, role);
//			caster.getBehavior().addTargetFont(fontSizeType, AttrFontColorType.MP_Change, mp, role);
			caster.getBehavior().notifyAttrFont();
		}
		return true ;
	}
	
	private int getBuffHurt(AttriItem item,AbstractRole role){
		if(null == item){
			return 0 ;
		}
		return (int)item.getValue() ;
	}
	
	/**执行属性效果*/
	private boolean[] execAttribute(BuffContext context,BuffFuncPoint fp){
		boolean v1 = this.execAttribute(context.getOwner(), context.getOwnerAttriBuffer(), fp, context.getCaster());
		boolean v2 = this.execAttribute(context.getCaster(), context.getCasterAttriBuffer(), fp, context.getCaster());
		//这里的AttriBuffer要做处理[已经在BuffContext中处理]
		//process,remove,timeover中只能修改累积属性
		return new boolean[]{v1,v2} ;
	}

	@Override
	public void attacked(BuffContext context) {
		// 被攻击接口考虑吸收
		if(EffectType.absorb != this.effectType){
			return ;
		}
		//获得当前吸收剩余总量
		int remain = Integer.parseInt(context.getBuffStat().getBuffInfo());
		if(remain >0){
			context.setAbsorbed(Math.min(remain, context.getInputHurts()));
			remain -= context.getAbsorbed();
			context.appendAbsorb(Math.max(remain,0));
			this.store(context);
		}
		if(remain<=0){
			GameContext.getUserBuffApp().delBuffStat(
					context.getOwner(), context.getBuffStat(), false);
		}
	}

	@Override
	protected boolean hasProcess(BuffContext context) {
		//获得process效果
		//吸收,状态buff无需处理process
		if(null == this.effectType){
			return true ;
		}
		return this.effectType.isDoProcess() ;
	}

}
