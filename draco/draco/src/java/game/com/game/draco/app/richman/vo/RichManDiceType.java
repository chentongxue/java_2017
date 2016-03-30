package com.game.draco.app.richman.vo;

import sacred.alliance.magic.constant.TextId;

public enum RichManDiceType {
	
	Normal((byte)0, TextId.Richman_dice_type_normal), //普通骰子
	Double((byte)1, TextId.Richman_dice_type_double), //双倍骰子
	Remote((byte)2, TextId.Richman_dice_type_remote), //遥控骰子
	;
	
	private final byte type; 
	private final String name;
	
	RichManDiceType(byte type, String name) {
		this.type = type;
		this.name = name;
	}
	
	public byte getType() {
		return this.type;
	}
	
	public String getName() {
		return this.name;
	}
	
	
	public static RichManDiceType get(byte type) {
		for(RichManDiceType rmdt : RichManDiceType.values()) {
			if(rmdt.getType() != type) {
				continue;
			}
			return rmdt;
		}
		return null;
	}
}
