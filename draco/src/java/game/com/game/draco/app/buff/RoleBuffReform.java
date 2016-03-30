package com.game.draco.app.buff;

import sacred.alliance.magic.base.StateType;
import sacred.alliance.magic.util.Util;

import com.game.draco.GameContext;

public class RoleBuffReform extends RoleBuff{

	@Override
	public String getNotReplaceDesc(){
		//roleBuff没有提示
		return null;
	}
	
	protected boolean doHealRate(){
		return true ;
	}
	
	public RoleBuffReform(short buffId) {
		super(buffId);
	}
	
	@Override
	public void attacked(BuffContext context) {
		// 被攻击接口考虑吸收
		boolean flag = false;
		if(EffectType.absorb == this.effectType){
			if(!Util.isEmpty(context.getBuffStat().getBuffInfo())){
				//获得当前吸收剩余总量
				int remain = Integer.parseInt(context.getBuffStat().getBuffInfo());
				if(remain >0){
					context.setAbsorbed(Math.min(remain, context.getInputHurts()));
					remain -= context.getAbsorbed();
					context.appendAbsorb(Math.max(remain,0));
					this.store(context);
				}
				if(remain<=0){
					flag = true;
				}
			}
		}
		if(context.getOwner() != null && StateType.getCode(context.getOwner().getState()) != null){
			if(StateType.paralysis.getType() == StateType.getCode(context.getOwner().getState()).getType()){
				context.getOwner().removeState(StateType.paralysis);
				//通知状态变化
				context.getOwner().getBehavior().notifyAttribute();
				flag = true;
			}
		}
		if(flag){
			GameContext.getUserBuffApp().delBuffStat(
					context.getOwner(), context.getBuffStat(), false);
		}
		
	}
	
	@Override
	protected void store(BuffContext context){
		super.store(context);
	}

	@Override
	protected void resume(BuffContext context){
		super.resume(context);
	}

	@Override
	public void beginEffect(BuffContext context){
		BuffDetail detail = this.getBuffDetail(context.getBuffLevel());
		if(detail == null){
			return;
		}
		
		if(getEffectType() == EffectType.state){
			if(getStateType() != -1){
				context.appendState(StateType.getType(getStateType()));
			}
		}
		addAttr(context);
	}

	@Override
	public void processEffect(BuffContext context) {
		
		if(getStateType() != -1){
			return;
		}
		
		if(getTimeType() != BuffTimeType.continued){
			return;
		}
		
		getBuffHurt(context);
		
		if(getEffectType() == EffectType.guideSkill){
			context.setChannelSkillBuff(true);
			context.appendUseSkill(getSkillContinue(), context.getBuffLevel(), context.getOwner(),context.getBuffStat().getContextInfo());
		}
		
		if(getEffectType() == EffectType.UseSkillRemove){
			if (effectType.getEffect().canRemoveNow(context)){
				context.setRemove(true);
			}
		}
	}
	
	@Override
	protected void execute(BuffContext context, BuffFuncPoint fp) {
		super.execute(context, fp);
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
	
	@Override
	public void removeEffect(BuffContext context){
//		recoverColor(context);
//		recoverZoom(context);
	}
	
	@Override
	public void timeOverEffect(BuffContext context){
//		recoverColor(context);
//		recoverZoom(context);
	}
	
}
