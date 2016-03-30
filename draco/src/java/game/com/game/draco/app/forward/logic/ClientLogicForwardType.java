package com.game.draco.app.forward.logic;

public enum ClientLogicForwardType {
	HERO((byte)1),//英雄
	HeroEquipStrength((byte)2),//英雄装备强化
	HeroEquipMosaic((byte)3),//英雄装备镶嵌
	HeroEquipQuality((byte)4),//英雄装备品质
	Backpack((byte)5),//打开背包
	HeroArenaReward((byte)6),//英雄试炼奖励
	HeroChoiceCardReward((byte)7),//英雄抽卡奖励
	HeroSkillUpgrade((byte)8),//英雄技能升级
	HeroUpgradeLevel((byte)9),
	HeroUpgradeStar((byte)10),
	;
	
	private final byte type;
	
	ClientLogicForwardType(byte type) {
		this.type = type;
	}
	
	public byte getType() {
		return this.type;
	}
}
