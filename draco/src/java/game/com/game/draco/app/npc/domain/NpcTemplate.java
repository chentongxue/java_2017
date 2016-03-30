package com.game.draco.app.npc.domain;

import lombok.Data;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.util.KeySupport;

import com.game.draco.GameContext;
import com.game.draco.app.skill.vo.SkillFormula;
import com.game.draco.app.speciallogic.SpecialLogicType;
import com.game.draco.app.speciallogic.config.WorldLevelGroupLogic;
import com.game.draco.app.unionbattle.domain.UnionIntegralInfo;


public @Data class NpcTemplate implements KeySupport<String>{
	protected String npcid;
	protected String npcname;
	protected int npctype;
	protected int level;
	protected int resid;
	protected int forceId;
	protected int exp;
	protected int minMoney;
	protected int maxMoney;
	protected int dkp ;
	protected int potential ;
	protected byte seriesId ; //系id
	protected byte gearId ; //档位id
	
	protected int lootNpc;
	protected int lootWorld;
	
	protected int maxHP;
	protected int atk; //攻击
	protected int rit; //防御
	protected int speed; //速度
	protected int dodge; //闪避
	protected int hit; //命中
	protected int critAtk; //暴击
	protected int critRit; //韧性
	protected int slowRitRate; //抵抗减速
	protected int exposeArmorRitRate; //抵抗破甲
	protected int weakRitRate; //抵抗虚弱
	protected int fixedRitRate; //抵抗定身
	protected int blowFlyRitRate; //抵抗击飞
	protected int lullRitRate; //抵抗麻痹
	protected int tiredRitRate; //抵抗疲劳
	protected int silenceRitRate; //抵抗沉默
	protected int bloodRitRate; //抵抗流血
	protected int charmRitRate; //抵抗魅惑
	protected int comaRitRate; //抵抗昏迷
	protected int poisionRitRate; //抵抗中毒
	protected int lightRitRate; //抵抗点燃
	protected int paralysisRitRate; //抵抗瘫痪
	protected int breakDefense ;//破防
	
	protected String gossip = "";
	protected byte lockType;
	protected String function;
	protected short funcResId ;
	protected String desc;
	protected byte resRate = 10 ;
	protected byte boss ;
	protected byte musicId ;
	/**
	 * 血条类型
	 * 0 默认
	 * 1 出生即显示
	 */
	protected byte hpStrip ;
	/**
	 * 是否任务仇恨共享
	 */
	protected byte questHatred ;
	protected byte shieldFunc = 0 ;
	protected byte showName = 0; //是否显示npc名字，0：不显示，1：显示
	protected int lifeTime = 0; //该种npc实例的生存期(毫秒)
	protected String color = "" ;
	/**
	 * 是否是藏宝图怪
	 * 此类型怪物击杀后将激活召唤者的日常数+1
	 */
	protected byte treasureBoss ;
	
	//是否读取特殊逻辑
	protected byte loadSpecialLogic;
	
	public byte getNpcProps(){
		int value = 0 ;
		if(1== lockType){
			value = (value | (1<<0));
		}
		if(1== hpStrip){
			value = (value | (1<<1));
		}
		return (byte)value ;
	}

    public int getAtk(){
        return this.getAttriValue(AttributeType.atk) ;
    }

    public int getRit(){
        return this.getAttriValue(AttributeType.rit) ;
    }

    public int getBreakDefense(){
        return this.getAttriValue(AttributeType.breakDefense) ;
    }

    public int getMaxHP(){
        return this.getAttriValue(AttributeType.maxHP) ;
    }

    public int getDodge(){
        return this.getAttriValue(AttributeType.dodge) ;
    }

    public int getHit(){
        return this.getAttriValue(AttributeType.hit) ;
    }

    public int getCritAtk(){
        return this.getAttriValue(AttributeType.critAtk) ;
    }

    public int getCritRit(){
        return this.getAttriValue(AttributeType.critRit) ;
    }

    public int getLevel(){
        return this.getAttriValue(AttributeType.level) ;
    }


    public int getAttriValue(byte byteAttriType){
        AttributeType attriType = AttributeType.get(byteAttriType);
        return this.getAttriValue(attriType) ;
    }
	
	public int getAttriValue(AttributeType attriType){
        if(null == attriType){
            return 0 ;
        }
		if(loadSpecialLogic == SpecialLogicType.worldLevel.getType()){
			WorldLevelGroupLogic baseData = GameContext.getSpecialLogicApp().getWorldLevelGroupLogic(getNpcid());
			if(baseData != null){
				int value = baseData.getAttriValue(attriType) ;
                if(value != Integer.MIN_VALUE){
                    return value ;
                }
			}
		}
		if(attriType == AttributeType.level){
			return this.level;
		}else if(attriType == AttributeType.exp){
			return this.exp;
		}else if(attriType == AttributeType.maxHP){
			return this.maxHP;
		}else if(attriType == AttributeType.atk){
			return this.atk;
		}else if(attriType == AttributeType.rit){
			return this.rit;
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
		}else if(attriType == AttributeType.poisionRitRate){
			return this.poisionRitRate;
		}else if(attriType == AttributeType.paralysisRitRate){
			return this.paralysisRitRate;
		}else if(attriType == AttributeType.cdRate){
			return SkillFormula.TEN_THOUSAND ;
		}else if(attriType == AttributeType.healRate) {
			return SkillFormula.TEN_THOUSAND ;
		}else if(attriType == AttributeType.mpConsumeRate) {
			return SkillFormula.TEN_THOUSAND ;
		}else if(attriType == AttributeType.breakDefense){
			return this.breakDefense ;
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

	public boolean isPureNpc(){
		return true ;
	}

	@Override
	public String getKey() {
		return this.npcid;
	}
	

	public String getNpcname(){
		if(loadSpecialLogic == SpecialLogicType.unionIntegral.getType()){
			return GameContext.getSpecialLogicApp().getNpcName(npcname,loadSpecialLogic);
		}
		return npcname;
	}
}
