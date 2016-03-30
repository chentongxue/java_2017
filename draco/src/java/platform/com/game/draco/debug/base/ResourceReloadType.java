package com.game.draco.debug.base;

import com.game.draco.GameContext;

import sacred.alliance.magic.constant.TextId;

public enum ResourceReloadType {
	
	Shop(1,TextId.Res_Reload_Shop),
	Rate(2,TextId.Res_Reload_Rate),
	Config(3,TextId.Res_Reload_Config),
	Announce(4,TextId.Res_Reload_Announce),
	Skill(6,TextId.Res_Reload_Skill),
	Buff(7,TextId.Res_Reload_Buff),
	Discount(8,TextId.Res_Reload_Discount),
	DoorDogBlackIp(10,TextId.Res_Reload_DoorDogBlackIp),
	ShopSecret(11,TextId.Res_Reload_Shop),
	CardChoice(12,TextId.Res_Reload_Card_Hero),
	SimpleActive(13, TextId.Res_Reload_Simple_Active),// 运营活动
	MonthCard(14, TextId.Res_Reload_Month_Card),// 月卡
	GrowFund(15, TextId.Res_Reload_Grow_Fund),// 成长基金
	FirstPay(16, TextId.Res_Reload_First_Pay),// 首冲
	PayExtra(17, TextId.Res_Reload_Pay_Extra),// 充值赠送
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
