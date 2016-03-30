package com.game.draco.app.npc.domain;

import com.game.draco.app.skill.vo.SkillFormula;

import lombok.Data;
import sacred.alliance.magic.base.AttributeType;


public @Data class NpcTemplate{
	private String npcid;
	private String npcname;
	private int npctype;
	private int level;
	private int resid;
	private int forceId;
	private int exp;
	private int minMoney;
	private int maxMoney;
	private int bindMoney ;
	private int zp ;
	private int magicSoul ;
	private int totalMagicSoul ;
	
	private int lootNpc;
	private int lootWorld;
	
	private int maxHP;
	private int maxMP;
	private int phyAtk; //物攻
	private int iceAtk; //冰攻
	private int fireAtk; //火攻
	private int phyRit; //物防
	private int iceRit; //冰抗
	private int fireRit; //火抗
	private int speed; //速度
	private int dodge; //闪避
	private int hit; //命中
	private int critAtk; //暴击
	private int critRit; //韧性
	private int slowRitRate; //抵抗减速
	private int exposeArmorRitRate; //抵抗破甲
	private int weakRitRate; //抵抗虚弱
	private int fixedRitRate; //抵抗定身
	private int blowFlyRitRate; //抵抗击飞
	private int lullRitRate; //抵抗麻痹
	private int tiredRitRate; //抵抗疲劳
	private int silenceRitRate; //抵抗沉默
	private int bloodRitRate; //抵抗流血
	private int charmRitRate; //抵抗魅惑
	private int comaRitRate; //抵抗昏迷
	private int agedRitRate; //抵抗老化
	private int lightRitRate; //抵抗点燃
	
	private String gossip = "";
	private byte lockType;
	private String function;
	private short funcResId ;
	private String desc;
	private byte resRate = 10 ;
//	private short headResId ;
//	private short bodyResId ;
	private byte boss ;
	private byte musicId ;
	/**
	 * 是否任务仇恨共享
	 */
	private byte questHatred ;
	private byte xp ;
	private byte shieldFunc = 0 ;
	private byte showName = 0; //是否显示npc名字，0：不显示，1：显示
	
	public int getAttriValue(byte attriValue){
		AttributeType attriType = AttributeType.get(attriValue);
		if(attriType == AttributeType.level){
			return this.level;
		}else if(attriType == AttributeType.exp){
			return this.exp;
		}else if(attriType == AttributeType.maxHP){
			return this.maxHP;
		}else if(attriType == AttributeType.maxMP){
			return this.maxMP;
		}else if(attriType == AttributeType.phyAtk){
			return this.phyAtk ;
		}else if(attriType == AttributeType.iceAtk){
			return this.iceAtk ;
		}else if(attriType == AttributeType.fireAtk){
			return this.fireAtk ;
		}else if(attriType == AttributeType.phyRit){
			return this.phyRit;
		}else if(attriType == AttributeType.iceRit){
			return this.iceRit;
		}else if(attriType == AttributeType.fireRit){
			return this.fireRit;
		}else if(attriType == AttributeType.speed){
			return this.speed;
		}else if(attriType == AttributeType.dodge){
			return this.dodge;
		}else if(attriType == AttributeType.hit){
			return this.hit;
		}else if(attriType == AttributeType.critAtk){
			return this.critAtk;
		}else if(attriType == AttributeType.critRit){
			return this.critRit;
		}else if(attriType == AttributeType.slowRitRate){
			return this.slowRitRate;
		}else if(attriType == AttributeType.sunderRitRate){
			return this.exposeArmorRitRate;
		}else if(attriType == AttributeType.weakRitRate){
			return this.weakRitRate;
		}else if(attriType == AttributeType.fixedRitRate){
			return this.fixedRitRate;
		}else if(attriType == AttributeType.blowFlyRitRate){
			return this.blowFlyRitRate;
		}else if(attriType == AttributeType.lullRitRate){
			return this.lullRitRate;
		}else if(attriType == AttributeType.tiredRitRate){
			return this.tiredRitRate;
		}else if(attriType == AttributeType.mumRitRate){
			return this.silenceRitRate;
		}else if(attriType == AttributeType.bloodRitRate){
			return this.bloodRitRate;
		}else if(attriType == AttributeType.lightRitRate){
			return this.lightRitRate;
		}else if(attriType == AttributeType.charmRitRate){
			return this.charmRitRate;
		}else if(attriType == AttributeType.comaRitRate){
			return this.comaRitRate;
		}else if(attriType == AttributeType.agedRitRate){
			return this.agedRitRate;
		}else if(attriType == AttributeType.cdRate){
			return SkillFormula.TEN_THOUSAND ;
		}else if(attriType == AttributeType.healRate) {
			return SkillFormula.TEN_THOUSAND ;
		}else if(attriType == AttributeType.mpConsumeRate) {
			return SkillFormula.TEN_THOUSAND ;
		}
		return 0;
	}
	
	public boolean npcIsBoss(){
		return this.boss == (byte)1;
	}
	
	/**
	 * 利用已经有的boss字段来拼接一下标识
	 * 0位:是否boss 0:否,1:是;
	 * 1位:是否显示名字 0:不显示,1:显示
	 * @return
	 */
	public byte buildFlags(){
		//是否显示名字
		int result = this.showName << 1;
		//是否是boss
		result += this.boss;
		return (byte)result;
	}
}
