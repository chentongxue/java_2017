package com.game.draco.app.npc.config;

import com.game.draco.base.CampType;

import lombok.Data;
import sacred.alliance.magic.base.ForceRelation;
import sacred.alliance.magic.util.KeySupport;
/**
 * 势力
 */
public @Data class ForceConfig implements KeySupport<Integer>{

	private int forceId ;
	private byte human	;
	private byte genius ;
	private byte orc ;
	private byte undead ;
	private byte unknow ;
	
	
	
	public ForceRelation getForceRelation(byte campType){
		byte relation = this.getRelation(campType);
		return ForceRelation.getByType(relation);
	}
	
	public byte getRelation(byte campType){
		if(campType == CampType.human.getType()){
			return human ;
		}else if(campType == CampType.genius.getType()){
			return genius ;
		}else if(campType == CampType.orc.getType()){
			return orc ;
		}else if(campType == CampType.undead.getType()){
			return undead ;
		}else if(campType == CampType.unknow.getType()){
			return unknow ;
		}
		return 0 ;
	}
	
	@Override
	public Integer getKey(){
		return this.forceId ;
	}
}
