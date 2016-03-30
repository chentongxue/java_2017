package com.game.draco.app.asyncpvp.domain;

import java.util.Map;

import lombok.Data;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.domain.RoleLevelup;
import sacred.alliance.magic.vo.RoleInstance;

import com.alibaba.fastjson.annotation.JSONField;
import com.game.draco.GameContext;
import com.game.draco.app.npc.domain.NpcTemplate;
import com.game.draco.app.skill.domain.RoleSkillStat;
import com.google.common.collect.Maps;

public @Data class AsyncPvpRoleAttr extends NpcTemplate {
	private String roleId;
	private String roleName;
	private int speed;
	private int level;
	private byte sex;
	private byte camp;
	//属性
	//private int atk;
	//private int rit;
	private int sacredAtk ;
	private int sacredRit;
	//private int hit;
	//private int dodge;
	//private int critAtk;
	//private int critRit;
	private int critAtkProb;
	private int hurtRemitRate;
	//private int breakDefense;//破防属性
	
	//概率属性
	//private int slowRitRate; //抵抗减速概率
	private int sunderRitRate; //抵抗破甲概率
	//private int weakRitRate; //抵抗虚弱概率
	//private int fixedRitRate; //抵抗定身概率
	//private int blowFlyRitRate; //抵抗击飞概率
	private int repelRitRate; //抵抗击退概率
	//private int lullRitRate; //抵抗麻痹概率
	//private int tiredRitRate; //抵抗疲劳概率
	private int mumRitRate; //抵抗沉默概率
	//private int bloodRitRate; //抵抗流血概率
	//private int charmRitRate; //抵抗魅惑概率
	//private int comaRitRate; //抵抗昏迷概率
	private int mussRitRate; //抵抗混乱概率
	//private int agedRitRate; //抵抗老化概率
	//private int lightRitRate; //抵抗点燃概率

	private int slowAddRate; //加成减速概率
	private int sunderAddRate; //加成破甲概率
	private int weakAddRate; //加成虚弱概率
	private int fixedAddRate; //加成定身概率
	private int blowFlyAddRate; //加成击飞概率
	private int repelAddRate; //加成击退概率
	private int lullAddRate; //加成麻痹概率
	private int tiredAddRate; //加成疲劳概率
	private int mumAddRate; //加成沉默概率
	private int bloodAddRate; //加成流血概率
	private int charmAddRate; //加成魅惑概率
	private int comaAddRate; //加成昏迷概率
	private int mussAddRate; //加成混乱概率
	private int poisionAddRate; //加成中毒概率
	private int lightAddRate; //加成点燃概率
	private int paralysisAddRate;//加成瘫痪概率
	
	private int healRate;
	private int mpConsumeRate;
	private int cdRate;
	private int clothesResId;
	private byte vipLevel;
	
	private int battleScore;
	
	private int horseResId;
	private int horseId;
	private byte horseQuality;
	private byte horseStar;
	
	private int heroId;
	private int heroLevel;
	private byte heroQuality;
	private byte heroStar;
	private short heroHeadId;
	
	private int petId;
	private byte petLevel;
	private byte petQuality;
	private byte petStar;
	
	
	
	//<skillId, skillLv>
	private Map<Short, Integer> skillInfoMap = Maps.newHashMap();
	
	public AsyncPvpRoleAttr(){
	}
	
	public AsyncPvpRoleAttr(RoleInstance role){
		if(null == role) {
			return;
		}
		this.roleId = role.getRoleId();
		this.roleName = role.getRoleName();
		this.sex = role.getSex();
		this.camp = role.getCampId();
		this.maxHP = role.getMaxHP();
		this.speed = role.getSpeed();
		this.level = role.getLevel();
		//属性
		this.atk = role.get(AttributeType.atk);
		this.rit = role.get(AttributeType.rit);
		this.sacredAtk  = role.get(AttributeType.sacredAtk);
		this.sacredRit = role.get(AttributeType.sacredRit);
		this.hit = role.get(AttributeType.hit);
		this.dodge = role.get(AttributeType.dodge);
		this.critAtk = role.get(AttributeType.critAtk);
		this.critRit = role.get(AttributeType.critRit);
		this.critAtkProb = role.get(AttributeType.critAtkProb);
		this.hurtRemitRate = role.get(AttributeType.hurtRemitRate);
		this.breakDefense = role.get(AttributeType.breakDefense);
		
		//概率属性
		this.slowRitRate = role.get(AttributeType.slowRitRate); 
		this.sunderRitRate = role.get(AttributeType.sunderRitRate); 
		this.weakRitRate = role.get(AttributeType.weakRitRate); 
		this.fixedRitRate = role.get(AttributeType.fixedRitRate); 
		this.blowFlyRitRate = role.get(AttributeType.blowFlyRitRate); 
		this.repelRitRate = role.get(AttributeType.repelRitRate); 
		this.lullRitRate = role.get(AttributeType.lullRitRate); 
		this.tiredRitRate = role.get(AttributeType.tiredRitRate); 
		this.mumRitRate = role.get(AttributeType.mumRitRate); 
		this.bloodRitRate = role.get(AttributeType.bloodRitRate); 
		this.charmRitRate = role.get(AttributeType.charmRitRate); 
		this.comaRitRate = role.get(AttributeType.comaRitRate); 
		this.mussRitRate = role.get(AttributeType.mussRitRate); 
		this.poisionRitRate = role.get(AttributeType.poisionRitRate); 
		this.lightRitRate = role.get(AttributeType.lightRitRate); 
		this.paralysisRitRate = role.get(AttributeType.paralysisRitRate);

		this.slowAddRate = role.get(AttributeType.slowAddRate); 
		this.sunderAddRate = role.get(AttributeType.sunderAddRate); 
		this.weakAddRate = role.get(AttributeType.weakAddRate); 
		this.fixedAddRate = role.get(AttributeType.fixedAddRate); 
		this.blowFlyAddRate = role.get(AttributeType.blowFlyAddRate); 
		this.repelAddRate = role.get(AttributeType.repelAddRate); 
		this.lullAddRate = role.get(AttributeType.lullAddRate); 
		this.tiredAddRate = role.get(AttributeType.tiredAddRate); 
		this.mumAddRate = role.get(AttributeType.mumAddRate); 
		this.bloodAddRate = role.get(AttributeType.bloodAddRate); 
		this.charmAddRate = role.get(AttributeType.charmAddRate); 
		this.comaAddRate = role.get(AttributeType.comaAddRate); 
		this.mussAddRate = role.get(AttributeType.mussAddRate); 
		this.poisionAddRate = role.get(AttributeType.poisionAddRate); 
		this.lightAddRate = role.get(AttributeType.lightAddRate); 
		this.paralysisAddRate = role.get(AttributeType.paralysisAddRate);
		
		this.healRate = role.get(AttributeType.healRate);
		this.mpConsumeRate = role.get(AttributeType.mpConsumeRate);
		this.cdRate = role.get(AttributeType.cdRate); 
		this.clothesResId = role.getClothesResId();
		this.battleScore = role.getBattleScore();
		//设置技能
		for(RoleSkillStat stat : role.getSkillMap().values()){
			skillInfoMap.put(stat.getSkillId(), stat.getSkillLevel());
		}
	}
	
	
	@JSONField(serialize=false)
	public int getAttriValue(byte attrType){
		AttributeType attriType = AttributeType.get(attrType);
		switch(attriType){
		case level:
			return this.level;
		case maxHP:
			return this.maxHP;
		case cdRate:
			return this.cdRate ;
		case speed:
			return this.speed;
		case atk:
			return this.atk ;
		case rit:
			return this.rit ;
		case sacredAtk:
			return this.sacredAtk ;
		case sacredRit:
			return this.sacredRit;
		case hit:
			return this.hit;
		case dodge:
			return this.dodge ;
		case critAtk:
			return this.critAtk;
		case critRit:
			return this.critRit;
		case critAtkProb:
			return this.critAtkProb ;
		case hurtRemitRate:
			return this.hurtRemitRate ;
		case slowRitRate:
			return this.slowRitRate ;
		case sunderRitRate:
			return this.sunderRitRate;
		case weakRitRate:
			return this.weakRitRate;
		case fixedRitRate:
			return this.fixedRitRate;
		case blowFlyRitRate:
			return this.blowFlyRitRate;
		case repelRitRate:
			return this.repelRitRate ;
		case lullRitRate:
			return this.lullRitRate;
		case tiredRitRate:
			return this.tiredRitRate;
		case mumRitRate:
			return this.mumRitRate;
		case bloodRitRate:
			return this.bloodRitRate ;
		case charmRitRate:
			return this.charmRitRate ;
		case comaRitRate:
			return this.comaRitRate ;
		case mussRitRate:
			return this.mussRitRate;
		case poisionRitRate:
			return this.poisionRitRate;
		case paralysisRitRate:
			return this.paralysisRitRate;
		case lightRitRate:
			return this.lightRitRate;
		case slowAddRate:
			return this.slowAddRate ;
		case sunderAddRate:
			return this.sunderAddRate;
		case weakAddRate:
			return this.weakAddRate;
		case fixedAddRate:
			return this.fixedAddRate;
		case blowFlyAddRate:
			return this.blowFlyAddRate;
		case repelAddRate:
			return this.repelAddRate ;
		case lullAddRate:
			return this.lullAddRate;
		case tiredAddRate:
			return this.tiredAddRate;
		case mumAddRate:
			return this.mumAddRate;
		case bloodAddRate:
			return this.bloodAddRate ;
		case charmAddRate:
			return this.charmAddRate ;
		case comaAddRate:
			return this.comaAddRate ;
		case mussAddRate:
			return this.mussAddRate;
		case poisionAddRate:
			return this.poisionAddRate;
		case lightAddRate:
			return this.lightAddRate;
		case healRate:
			return this.healRate;
		case mpConsumeRate:
			return this.mpConsumeRate;
		case paralysisAddRate:
			return this.paralysisAddRate;
		case breakDefense:
			if(this.breakDefense == 0){
				RoleLevelup levelUp= GameContext.getAttriApp().getLevelup(level);
				if(levelUp != null){
					return levelUp.getBreakDefense();
				}
			}
			return this.breakDefense ;
		}
		return 0;
	}
	
	public boolean setAttriValue(byte attrType, int value){
		AttributeType attriType = AttributeType.get(attrType);
		switch(attriType){
		case level:
			this.level = value; return true;
		case maxHP:
			this.maxHP = value; return true;
		case cdRate:
			this.cdRate = value; return true;
		case speed:
			this.speed = value; return true;
		case atk:
			this.atk = value; return true;
		case rit:
			this.rit = value; return true;
		case sacredAtk:
			this.sacredAtk = value; return true;
		case sacredRit:
			this.sacredRit = value; return true;
		case hit:
			this.hit = value; return true;
		case dodge:
			this.dodge = value; return true;
		case critAtk:
			this.critAtk = value; return true;
		case critRit:
			this.critRit = value; return true;
		case critAtkProb:
			this.critAtkProb = value; return true;
		case hurtRemitRate:
			this.hurtRemitRate  = value; return true;
		case slowRitRate:
			this.slowRitRate = value; return true;
		case sunderRitRate:
			this.sunderRitRate = value; return true;
		case weakRitRate:
			this.weakRitRate = value; return true;
		case fixedRitRate:
			this.fixedRitRate = value; return true;
		case blowFlyRitRate:
			this.blowFlyRitRate = value; return true;
		case repelRitRate:
			this.repelRitRate = value; return true;
		case lullRitRate:
			this.lullRitRate = value; return true;
		case tiredRitRate:
			this.tiredRitRate = value; return true;
		case mumRitRate:
			this.mumRitRate = value; return true;
		case bloodRitRate:
			this.bloodRitRate = value; return true;
		case charmRitRate:
			this.charmRitRate = value; return true;
		case comaRitRate:
			this.comaRitRate = value; return true;
		case mussRitRate:
			this.mussRitRate = value; return true;
		case poisionRitRate:
			this.poisionRitRate = value; return true;
		case paralysisRitRate:
			this.paralysisRitRate = value; return true;
		case lightRitRate:
			this.lightRitRate = value; return true;
		case slowAddRate:
			this.slowAddRate = value; return true;
		case sunderAddRate:
			this.sunderAddRate = value; return true;
		case weakAddRate:
			this.weakAddRate = value; return true;
		case fixedAddRate:
			this.fixedAddRate = value; return true;
		case blowFlyAddRate:
			this.blowFlyAddRate = value; return true;
		case repelAddRate:
			this.repelAddRate  = value; return true;
		case lullAddRate:
			this.lullAddRate = value; return true;
		case tiredAddRate:
			this.tiredAddRate = value; return true;
		case mumAddRate:
			this.mumAddRate = value; return true;
		case bloodAddRate:
			this.bloodAddRate = value; return true;
		case charmAddRate:
			this.charmAddRate  = value; return true;
		case comaAddRate:
			this.comaAddRate = value; return true;
		case mussAddRate:
			this.mussAddRate = value; return true;
		case poisionAddRate:
			this.poisionAddRate = value; return true;
		case paralysisAddRate:
			this.paralysisAddRate = value; return true;
		case lightAddRate:
			this.lightAddRate = value; return true;
		case healRate:
			this.healRate = value; return true;
		case mpConsumeRate:
			this.mpConsumeRate = value; return true;
		case breakDefense :
			this.breakDefense = value ; return true ;
		}
		return false ;
	}


	public boolean isPureNpc(){
		return false ;
	}

}
