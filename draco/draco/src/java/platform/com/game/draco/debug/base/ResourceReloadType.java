package com.game.draco.debug.base;

import com.game.draco.GameContext;

import sacred.alliance.magic.constant.TextId;

public enum ResourceReloadType {
	
	Shop(1,TextId.Res_Reload_Shop),
	Rate(2,TextId.Res_Reload_Rate),
	Config(3,TextId.Res_Reload_Config),
	Announce(4,TextId.Res_Reload_Announce),
	PublicNotice(5,TextId.Res_Reload_PublicNotice),
	Skill(6,TextId.Res_Reload_Skill),
	Buff(7,TextId.Res_Reload_Buff),
	Discount(8,TextId.Res_Reload_Discount),
	Carnival(9,TextId.Res_Reload_Carnival),
	DoorDogBlackIp(10,TextId.Res_Reload_DoorDogBlackIp),
	;
	
	private final int type;//活动类型
	private final String name;//活动名称
	
	private ResourceReloadType(int type, String name) {
		this.type = type;
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public String getName() {
		return GameContext.getI18n().getText(this.name);
	}
	
	public static ResourceReloadType get(int type){
		for(ResourceReloadType actType:ResourceReloadType.values()){
			if(actType.getType()==type){
				return actType;
			}
		}
		return null;
	}
}
