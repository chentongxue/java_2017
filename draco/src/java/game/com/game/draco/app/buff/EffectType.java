package com.game.draco.app.buff;

import com.game.draco.app.buff.effect.AbsorbEffect;
import com.game.draco.app.buff.effect.AttributeEffect;
import com.game.draco.app.buff.effect.ColorEffect;
import com.game.draco.app.buff.effect.Effect;
import com.game.draco.app.buff.effect.EmptyEffect;
import com.game.draco.app.buff.effect.GuideSkillEffect;
import com.game.draco.app.buff.effect.StateEffect;
import com.game.draco.app.buff.effect.UseSkillRemoveEffect;
import com.game.draco.app.buff.effect.ZoomEffect;

public enum EffectType {
	absorb(0,false),
	state(1,false),
	attribute(2,true),
	flag(3,false),
	skill(4,false), //改变技能
	shape(5,false), //改变外形
	color(6,false), //颜色
	zoom(7,false), //缩放
	UseSkillRemove(8,true),
	guideSkill(9,true),//引导
	;

	private int type = 0;
	private boolean doProcess = false ;
	
	private EffectType(int type,boolean doProcess){
		this.type = type;
		this.doProcess = doProcess ;
	}
	
	public static EffectType get(int type){
		for(EffectType tt : values()){
			if(tt.getType() == type){
				return tt ;
			}
		}
		return null ;
	}
	
	public boolean isDoProcess() {
		return doProcess;
	}

	public int getType() {
		return type;
	}

	public Effect getEffect(){
		if(this == absorb){
			return AbsorbEffect.getInstance();
		}
		if(this == color){
			return ColorEffect.getInstance();
		}
		if(this == zoom){
			return ZoomEffect.getInstance();
		}
		if(this == state){
			return StateEffect.getInstance();
		}
		if(this == attribute){
			return AttributeEffect.getInstance();
		}
		if(this == UseSkillRemove){
			return UseSkillRemoveEffect.getInstance();
		}
		if(this == guideSkill){
			return GuideSkillEffect.getInstance();
		}
		return EmptyEffect.getInstance();
	}
	
	
}
