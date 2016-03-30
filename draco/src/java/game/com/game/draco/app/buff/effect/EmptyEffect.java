package com.game.draco.app.buff.effect;

import com.game.draco.app.buff.BuffContext;
import com.game.draco.app.buff.stat.BuffStat;

import sacred.alliance.magic.app.attri.AttriBuffer;

public class EmptyEffect implements Effect{

	static class SingletonHolder {
		static EmptyEffect singleton = new EmptyEffect();
	}
	
	public static EmptyEffect getInstance(){
		return SingletonHolder.singleton ;
	}
	
	@Override
	public void resume(BuffContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void store(BuffContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AttriBuffer getAttriBuffer(BuffStat stat) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canRemoveNow(BuffContext context) {
		// TODO Auto-generated method stub
		return false;
	}

}
