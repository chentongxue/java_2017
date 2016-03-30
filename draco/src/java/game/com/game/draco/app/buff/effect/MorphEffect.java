package com.game.draco.app.buff.effect;


public class MorphEffect extends AttributeEffect {
	static class SingletonHolder {
		static MorphEffect singleton = new MorphEffect();
	}
	
	public static MorphEffect getInstance(){
		return SingletonHolder.singleton ;
	}
}
