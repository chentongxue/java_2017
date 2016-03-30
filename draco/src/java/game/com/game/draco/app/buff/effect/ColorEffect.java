package com.game.draco.app.buff.effect;

import com.game.draco.GameContext;
import com.game.draco.app.buff.BuffContext;
import com.game.draco.app.buff.BuffDetail;
import com.game.draco.app.buff.EffectType;
import com.game.draco.app.buff.stat.BuffStat;

import sacred.alliance.magic.app.attri.AttriBuffer;

public class ColorEffect implements Effect{
	static class SingletonHolder {
		static ColorEffect singleton = new ColorEffect();
	}
	
	public static ColorEffect getInstance(){
		return SingletonHolder.singleton ;
	}
	
	@Override
	public void resume(BuffContext context) {
		if(EffectType.color == context.getBuff().getEffectType()){
			BuffStat buffStat = context.getBuffStat();
			if(buffStat == null){
				return;
			}
        	GameContext.getSkillApp().roleRecoverColor(buffStat.getOwner());
		}
	}

	@Override
	public void store(BuffContext context) {
			GameContext.getSkillApp().roleChangeColor(context.getOwner(),context.getBuff().getDiscolor(), context.getBuff().getPersistTime());
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
