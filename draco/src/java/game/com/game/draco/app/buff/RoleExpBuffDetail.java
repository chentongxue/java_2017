package com.game.draco.app.buff;

import java.util.List;

import lombok.Data;

import com.game.draco.app.buff.config.BuffBase;
import com.game.draco.app.buff.domain.BHurtC;

public @Data class RoleExpBuffDetail extends BuffBase implements BuffDetail{

	private boolean replace ;
	private int add ; 
	private int mult ;
	private int level ;
	private String desc ;
	
	
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
		buff.setIconId(this.getIconId());
		buff.setPersistTime(this.getPersistTime());
		buff.setIntervalTime(this.getIntervalTime());
		buff.setGroupId(this.getGroupId());
		buff.setReplaceType(this.getReplaceType());
		buff.setDieLost(this.isDieLost());
		buff.setOfflineLost(this.isOfflineLost());
		buff.setOfflineTiming(this.isOfflineTiming());
		buff.setSwitchOn(true);
		buff.setExitInsLost(this.isExitInsLost());
		buff.setTransLost(this.isTransLost());
		return buff;
	}


	@Override
	public List<BHurtC> getBHurtList() {
		// TODO Auto-generated method stub
		return null;
	}
}
