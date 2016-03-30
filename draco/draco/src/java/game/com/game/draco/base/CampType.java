package com.game.draco.base;

import com.game.draco.GameContext;

import sacred.alliance.magic.constant.TextId;

public enum CampType {
	unknow((byte)-1,"unknow", false),
	human((byte)0,TextId.camp_human_name, true),  //人类
	genius((byte)1,TextId.camp_genius_name, true), //精灵
	orc((byte)2,TextId.camp_orc_name, true),//兽人
	undead((byte)3,TextId.camp_undead_name, true),//亡灵
	;
	
	private final byte type;
	private final String name;
	private final boolean realCamp;//是否是真实的阵营关系
	
	
	private CampType(byte type, String name, boolean realCamp) {
		this.type = type;
		this.name = name;
		this.realCamp = realCamp;
	}
	
	public static int getRealCampNum(){
		int value = 0 ;
		for(CampType camp : CampType.values()){
			if(camp.isRealCamp()){
				value ++ ;
			}
		}
		return value ;
	}

	public byte getType() {
		return type;
	}

	public String getName() {
		return GameContext.getI18n().getText(this.name);
	}
	
	public boolean isRealCamp() {
		return realCamp;
	}

	public static CampType get(byte type){
		for(CampType camp : CampType.values()){
			if(camp.getType() == type){
				return camp;
			}
		}
		return null ;
	}
}
