package com.game.draco.app.target.cond;

public enum TargetCondType {
	RoleLevel((byte)1, "人物等级"),
	//CopyPass((byte)2, "x副本通关x次"),
	QuestFinish((byte)3, "完成x任务"),
	HeroNum((byte)4, "英雄x个"),
	HeroLevel((byte)5, "英雄x等级x个"),
	HeroQualityStar((byte)6, "英雄x品质和x星级x个"),
	HeroEquipStrength((byte)7, "英雄装备强化x级x个"),
	HeroEquipQuality((byte)8, "x品质x件英雄装备"),
	HeroEquipMosaic((byte)9, "英雄装备x符文镶嵌x个"),
	HorseNum((byte)10, "坐骑x个"),
	RoleHeroSkillLevel((byte)12, "主角英雄技能x等级x个"),
	//AttributeTotal((byte)13, "x属性累积获得x值"),
	//NpcKill((byte)14, "杀死x npc x次"),
	RoleBattleScore((byte)15, "出战英雄战斗力"),
	PetNum((byte)16, "宠物x个"),
	PetLevel((byte)17, "宠物x等级x个"),
	PetMosaic((byte)18, "宠物x符文镶嵌x个"),
	;
	
	private final byte type;
	private final String name;
	
	TargetCondType(byte type, String name) {
		this.type = type;
		this.name = name;
	}
	
	public byte getType() {
		return this.type;
	}
	
	public String getName() {
		return this.name;
	}
	
	public static TargetCondType get(byte type) {
		for(TargetCondType v : values()) {
			if(type == v.getType()){
				return v;
			}
		}
		return null;
	}
	
}
