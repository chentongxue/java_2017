package com.game.draco.app.buff.effect;

import sacred.alliance.magic.app.attri.AttriBuffer;

import com.game.draco.GameContext;
import com.game.draco.app.buff.BuffContext;
import com.game.draco.app.buff.EffectType;
import com.game.draco.app.buff.stat.BuffStat;

public class ZoomEffect implements Effect{
	static class SingletonHolder {
		static ZoomEffect singleton = new ZoomEffect();
	}
	
	public static ZoomEffect getInstance(){
		return SingletonHolder.singleton ;
	}
	
	@Override
	public void resume(BuffContext context) {
		if(EffectType.zoom == context.getBuff().getEffectType()){
			BuffStat buffStat = context.getBuffStat();
			if(buffStat == null){
				return;
			}
			GameContext.getSkillApp().roleRecoverZoom(buffStat.getOwner());
		}
	}

	@Override
	public void store(BuffContext context) {
		GameContext.getSkillApp().roleChangeZoom(context.getOwner(),
				context.getBuff().getZoom(),	context.getBuff().getPersistTime());
	}
	@Override
	public AttriBuffer getAttriBuffer(BuffStat stat) {
		return null;
	}
	
	@Override
	public boolean canRemoveNow(BuffContext context) {
		return Integer.parseInt(context.getBuffStat().getBuffInfo()) <=0;
	}

}
