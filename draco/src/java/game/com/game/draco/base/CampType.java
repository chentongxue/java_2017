package com.game.draco.base;

import com.game.draco.GameContext;

import sacred.alliance.magic.constant.TextId;

public enum CampType {
	unknow((byte)-1,"unknow", false, "FFffffff"),
	human((byte)0,TextId.camp_human_name, true, "ffffc000"),  //人类
	genius((byte)1,TextId.camp_genius_name, true, "ffffffff"), //精灵
	orc((byte)2,TextId.camp_orc_name, true, "ff03da43"),//兽人
	undead((byte)3,TextId.camp_undead_name, true, "ffffc000"),//亡灵
	;
	
	private final byte type;
	private final String name;
	private final boolean realCamp;//是否是真实的阵营关系
	private final String color;// 对应颜色
	
	
	private CampType(byte type, String name, boolean realCamp, String color) {
		this.type = type;
		this.name = name;
		this.realCamp = realCamp;
		this.color = color;
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
		if(this == unknow){
			return "" ;
		}
		return GameContext.getI18n().getText(this.name);
	}
	
	public boolean isRealCamp() {
		return realCamp;
	}
	
	public String getColor() {
		return color;
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
