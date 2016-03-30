package com.game.draco.app.buff.effect;

import com.game.draco.app.buff.BuffContext;
import com.game.draco.app.buff.stat.BuffStat;

import sacred.alliance.magic.app.attri.AttriBuffer;

public class AbsorbEffect implements Effect{
	static class SingletonHolder {
		static AbsorbEffect singleton = new AbsorbEffect();
	}
	
	public static AbsorbEffect getInstance(){
		return SingletonHolder.singleton ;
	}
	/**吸收效果*/
	@Override
	public void resume(BuffContext context) {
		
	}

	@Override
	public void store(BuffContext context) {
		//将剩余吸收量放入buffInfo字段
		context.getBuffStat().setBuffInfo(String.valueOf(context.getAbsorb()));
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
