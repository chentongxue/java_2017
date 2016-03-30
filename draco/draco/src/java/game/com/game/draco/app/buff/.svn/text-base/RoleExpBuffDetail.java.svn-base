package com.game.draco.app.buff;

import lombok.Data;

public @Data class RoleExpBuffDetail extends BuffDetail{

	private boolean replace ;
	private int add ; //冪桄樓楊捷薹
	private int mult ;//冪桄傚楊捷薹
	
	
	@Override
	public void init(){
		
	}
	
	@Override
	public void check(){
		
	}
	
	
	public Buff newBuff(){
		RoleExpBuff buff = new RoleExpBuff(this.getBuffId());
		buff.setBuffName(this.getName());
		buff.setEffectType(EffectType.attribute);
		buff.setTimeType(BuffTimeType.continued);
		return buff;
	}
}
