package com.game.draco.app.compass.domain;
/**
 * 淘宝类型，未使用
 * @author gaibaoning@moogame.cn
 * @date 2014-3-28 上午10:09:44
 */
public enum CompassTaobaoType {
	
	di_gong((byte)0,"地宫淘宝"),
	yao_gong((byte)1,"妖宫淘宝"),
	tian_gong((byte)2,"天宫淘宝"),
	
	;
	
	private final byte type;
	private final String name;
	
	CompassTaobaoType(byte type, String name){
		this.type = type;
		this.name = name;
	}

	public byte getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}

	public static CompassTaobaoType get(byte type){
		for(CompassTaobaoType item : CompassTaobaoType.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}
	
}
