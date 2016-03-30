package com.game.draco.app.buff.effect;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.vo.AbstractRole;

import com.game.draco.app.buff.BuffContext;
import com.game.draco.app.buff.stat.BuffStat;

public class UseSkillRemoveEffect implements Effect{

	static class SingletonHolder {
		static UseSkillRemoveEffect singleton = new UseSkillRemoveEffect();
	}
	
	public static UseSkillRemoveEffect getInstance(){
		return SingletonHolder.singleton ;
	}
	
	@Override
	public void store(BuffContext context) {
		AbstractRole owner = context.getOwner();
		//将最后使用技能放入buffInfo字段
		context.getBuffStat().setBuffInfo(String.valueOf(owner.getLastSkillProcessTime()));
	}

	@Override
	public void resume(BuffContext context) {
		
	}

	@Override
	public AttriBuffer getAttriBuffer(BuffStat stat) {
		return null;
	}

	@Override
	public boolean canRemoveNow(BuffContext context) {
		AbstractRole owner = context.getOwner();
		return !context.getBuffStat().getBuffInfo()
				.equals(String.valueOf(owner.getLastSkillProcessTime()));
	}

}
