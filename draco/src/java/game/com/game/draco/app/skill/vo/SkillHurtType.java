package com.game.draco.app.skill.vo;

import sacred.alliance.magic.base.AttributeType;

public enum SkillHurtType {
	//战斗属性
	ATK((byte)0, AttributeType.atk, AttributeType.rit),
	SACRED((byte)3, AttributeType.sacredAtk, AttributeType.sacredRit),
	
	//概率属性
	SLOW((byte)4, AttributeType.slowAddRate, AttributeType.slowRitRate),
	SUNDER((byte)5, AttributeType.sunderAddRate, AttributeType.sunderRitRate),
	WEAK((byte)6, AttributeType.weakAddRate, AttributeType.weakRitRate),
	FIXED((byte)7, AttributeType.fixedAddRate, AttributeType.fixedRitRate),
	BLOW_FLY((byte)8, AttributeType.blowFlyAddRate, AttributeType.blowFlyRitRate),
	REPEL((byte)9, AttributeType.repelAddRate, AttributeType.repelRitRate),
	LULL((byte)10, AttributeType.lullAddRate, AttributeType.lullRitRate),
	TIRED((byte)11, AttributeType.tiredAddRate, AttributeType.tiredRitRate),
	MUM((byte)12, AttributeType.mumAddRate, AttributeType.mumRitRate),
	BLOOD((byte)13, AttributeType.bloodAddRate, AttributeType.bloodRitRate),
	CHARM((byte)14, AttributeType.charmAddRate, AttributeType.charmRitRate),
	COMA((byte)15, AttributeType.comaAddRate, AttributeType.comaRitRate),
	MUSS((byte)16, AttributeType.mussAddRate, AttributeType.mussRitRate),
	POISION((byte)17, AttributeType.poisionAddRate, AttributeType.poisionRitRate),
	LIGHT((byte)18, AttributeType.lightAddRate, AttributeType.lightRitRate),
	SACRED_HURT((byte)19, null, null),
	PARALYSIS((byte)20, AttributeType.paralysisAddRate, AttributeType.paralysisRitRate),
	;
	
	private final byte type;
	private final AttributeType atkType;
	private final AttributeType ritType;
	
	SkillHurtType(byte type, AttributeType atkType, AttributeType ritType) {
		this.type = type;
		this.atkType = atkType;
		this.ritType = ritType;
	}
	
	public byte getType() {
		return this.type;
	}
	
	public AttributeType getAtkType() {
		return this.atkType;
	}
	
	public AttributeType getRitType() {
		return this.ritType;
	}
	
	public static SkillHurtType getType(int type){
		for(SkillHurtType st : values()){
			if(type == st.getType()){
				return st ;
			}
		}
		return null ;
	}
	
}
