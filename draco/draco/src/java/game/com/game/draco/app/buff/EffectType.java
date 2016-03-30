package com.game.draco.app.buff;

import com.game.draco.app.buff.effect.AbsorbEffect;
import com.game.draco.app.buff.effect.AttributeEffect;
import com.game.draco.app.buff.effect.Effect;
import com.game.draco.app.buff.effect.EmptyEffect;
import com.game.draco.app.buff.effect.StateEffect;

public enum EffectType {
	absorb,
	state,
	attribute,
	flag,
	;
	
	public Effect getEffect(){
		if(this == absorb){
			return AbsorbEffect.getInstance();
		}
		if(this == state){
			return  StateEffect.getInstance();
		}
		if(this == attribute){
			return  AttributeEffect.getInstance();
		}
		return EmptyEffect.getInstance();
	}
}
