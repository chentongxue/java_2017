package com.game.draco.app.buff.effect;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.base.StateType;

import com.game.draco.app.buff.BuffContext;
import com.game.draco.app.buff.stat.BuffStat;

public class GuideSkillEffect implements Effect{

	static class SingletonHolder {
		static GuideSkillEffect singleton = new GuideSkillEffect();
	}
	
	public static GuideSkillEffect getInstance(){
		return SingletonHolder.singleton ;
	}
	
	@Override
	public void resume(BuffContext context) {
//		if(context.getBuffStat().getBuffInfo() == null){
//			return;
//		}
		//移除状态
		context.getOwner().removeState(StateType.guideSkill);
		//通知状态变化
		context.getOwner().getBehavior().notifyAttribute();
	}

	@Override
	public void store(BuffContext context) {
		// 添加状态
		context.getOwner().addState(StateType.guideSkill);
		// 通知状态变化
		context.getOwner().getBehavior().notifyAttribute();
	}

	@Override
	public AttriBuffer getAttriBuffer(BuffStat stat) {
		return null;
	}

	@Override
	public boolean canRemoveNow(BuffContext context) {
		return false;
	}

}
