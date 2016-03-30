package com.game.draco.app.skill.vo;

public class SkillAdjust {
	private static final int  TEN_THOUSAND = 10000 ;
	public enum Type{
		add,
		percent,
		min,
		max,
		;
	}
	private Type type;
	private int value ;
	public SkillAdjust(Type type,int value){
		this.type = type ;
		this.value = value ;
	}
	
	public int getValue(int original){
		if(null == type){
			return original ;
		}
		if(Type.add == type){
			return original + value ;
		}
		if(Type.percent == type){
			return (int)(original*(1+ (float)value/TEN_THOUSAND));
		}
		if(Type.max == type){
			return Math.max(original, value);
		}
		return Math.min(original, value);
	}

}
