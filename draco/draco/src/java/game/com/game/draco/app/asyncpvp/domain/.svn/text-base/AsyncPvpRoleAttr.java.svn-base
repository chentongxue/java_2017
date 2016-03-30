package com.game.draco.app.asyncpvp.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.alibaba.fastjson.annotation.JSONField;
import com.game.draco.GameContext;
import com.game.draco.app.npc.domain.NpcTemplate;
import com.game.draco.app.skill.config.SkillDetail;
import com.game.draco.app.skill.domain.RoleSkillStat;
import com.game.draco.app.skill.vo.Skill;
import com.google.common.collect.Maps;

public @Data class AsyncPvpRoleAttr extends NpcTemplate {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private String roleId;
	private String roleName;
	private int maxHp;
	private int maxMp;
	private int speed;
	private int level;
	private byte sex;
	private byte camp;
	//属性
	private int atk;
	private int rit;
	private int sacredAtk ;
	private int phyAtk;
	private int iceAtk;
	private int fireAtk;
	private int phyRit;
	private int iceRit;
	private int fireRit;
	private int sacredRit;
	private int hit;
	private int dodge;
	private int critAtk;
	private int critRit;
	private int critAtkProb;
	private int hurtRemitRate;
	
	//概率属性
	private int slowRitRate; //抵抗减速概率
	private int sunderRitRate; //抵抗破甲概率
	private int weakRitRate; //抵抗虚弱概率
	private int fixedRitRate; //抵抗定身概率
	private int blowFlyRitRate; //抵抗击飞概率
	private int repelRitRate; //抵抗击退概率
	private int lullRitRate; //抵抗麻痹概率
	private int tiredRitRate; //抵抗疲劳概率
	private int mumRitRate; //抵抗沉默概率
	private int bloodRitRate; //抵抗流血概率
	private int charmRitRate; //抵抗魅惑概率
	private int comaRitRate; //抵抗昏迷概率
	private int mussRitRate; //抵抗混乱概率
	private int agedRitRate; //抵抗老化概率
	private int lightRitRate; //抵抗点燃概率

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
	private int agedAddRate; //加成老化概率
	private int lightAddRate; //加成点燃概率
	
	private int healRate;
	private int mpConsumeRate;
	private int cdRate;
	private int clothesResId;
	private int battleScore;
	private int mountsResId;
	private int goddessId;
	private int horseId;
	private int heroId;
	private int horseLevel;
	private int goddessLevel;
	private int heroLevel;
	private byte horseQuality;
	private byte goddessQuality;
	private byte heroQuality;
	private short equipResId;
	private short heroResId;
	
	//<skillId, skillLv>
	private Map<Short, Integer> skillInfoMap = Maps.newHashMap();
	
	//变量
	private Map<Short,RoleSkillStat> skillMap = new HashMap<Short,RoleSkillStat>();
	private List<RoleSkillStat> skillList = new ArrayList<RoleSkillStat>();
	private List<RoleSkillStat> soulSkillList = new ArrayList<RoleSkillStat>();
	private int selfSoulHpRate;//变身血量百分比
    private int targetSoulHpRate;//目标血量少于这个值变身
    
    @JSONField(serialize=false)
    public Map<Short,RoleSkillStat> getSkillMap() {
    	return this.skillMap;
    }
    
    @JSONField(serialize=false)
    public List<RoleSkillStat> getSkillList() {
    	return this.skillList;
    }
    
    @JSONField(serialize=false)
    public List<RoleSkillStat> getSoulSkillList() {
    	return this.soulSkillList;
    }
    
    @JSONField(serialize=false)
    public int getSelfSoulHpRate() {
    	return this.selfSoulHpRate;
    }
    
    @JSONField(serialize=false)
    public int getTargetSoulHpRate() {
    	return this.targetSoulHpRate;
    }
    
    @JSONField(serialize=false)
    public Logger getLogger() {
    	return this.logger;
    }
	
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
		this.maxHp = role.getMaxHP();
		this.maxMp = role.getMaxMP();
		this.speed = role.getSpeed();
		this.level = role.getLevel();
		//属性
		this.atk = role.get(AttributeType.atk);
		this.rit = role.get(AttributeType.rit);
		this.sacredAtk  = role.get(AttributeType.sacredAtk);
		this.phyAtk = role.get(AttributeType.phyAtk);
		this.iceAtk = role.get(AttributeType.iceAtk);
		this.fireAtk = role.get(AttributeType.fireAtk);
		this.phyRit = role.get(AttributeType.phyRit);
		this.iceRit = role.get(AttributeType.iceRit);
		this.fireRit = role.get(AttributeType.fireRit);
		this.sacredRit = role.get(AttributeType.sacredRit);
		this.hit = role.get(AttributeType.hit);
		this.dodge = role.get(AttributeType.dodge);
		this.critAtk = role.get(AttributeType.critAtk);
		this.critRit = role.get(AttributeType.critRit);
		this.critAtkProb = role.get(AttributeType.critAtkProb);
		this.hurtRemitRate = role.get(AttributeType.hurtRemitRate);

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
		this.agedRitRate = role.get(AttributeType.agedRitRate); 
		this.lightRitRate = role.get(AttributeType.lightRitRate); 

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
		this.agedAddRate = role.get(AttributeType.agedAddRate); 
		this.lightAddRate = role.get(AttributeType.lightAddRate); 
		
		this.healRate = role.get(AttributeType.healRate);
		this.mpConsumeRate = role.get(AttributeType.mpConsumeRate);
		this.cdRate = role.get(AttributeType.cdRate); 
		this.clothesResId = role.getClothesResId();
		this.battleScore = role.getBattleScore();
		skillMap = role.getSkillMap();
		/*RoleMount roleMount = role.getRoleMount();
		if(null != roleMount){
			this.mountsResId = roleMount.getMountResId();
		}*/
	}
	
	public void preToStore(RoleInstance role) {
		
		if(Util.isEmpty(skillMap)) {
			return ;
		}
		for(RoleSkillStat skillStat : skillMap.values()) {
			if(null == skillStat) {
				continue;
			}
			skillInfoMap.put(skillStat.getSkillId(), skillStat.getSkillLevel());
		}
	}
	
	public void postFromStore() {
		if(Util.isEmpty(skillInfoMap)) {
			return ;
		}
		
		for(Entry<Short, Integer> entry : skillInfoMap.entrySet()) {
			short skillId = entry.getKey();
			int skillLv = entry.getValue();
			Skill skill = GameContext.getSkillApp().getSkill(skillId);
			if(null == skill){
				continue ;
			}
			RoleSkillStat stat = new RoleSkillStat();
			stat.setSkillId(skillId);
			stat.setSkillLevel(skillLv);
			skillMap.put(skillId, stat);
		}
	}
	
	
	public void initSkill() {
		RoleSkillStat newSkillStat = null;
		if(Util.isEmpty(skillMap)) {
			return;
		}
		for(RoleSkillStat skillStat : skillMap.values()) {
			if(null == skillStat) {
				continue;
			}
			short skillId = skillStat.getSkillId();
			Skill skill = GameContext.getSkillApp().getSkill(skillId);
			if(null == skill) {
				continue;
			}
			if(!skill.isActiveSkill()){
				continue;
			}
			SkillDetail skillDetail = skill.getSkillDetail(skillStat.getSkillLevel());
			if(null == skillDetail) {
				continue;
			}
			newSkillStat = new RoleSkillStat();
			newSkillStat.setSkillId(skillId);
			newSkillStat.setSkillLevel(skillStat.getSkillLevel());
			//TODO:newSkillStat.setSkillLearnType(skill.getSkillLearnType());
			/*if(skill.getSkillType() == SkillType.Soul) {
				soulSkillList.add(newSkillStat);
			}else{
				skillList.add(newSkillStat);
			}*/
			skillList.add(newSkillStat);
		}
		this.sortList(skillList);
		this.sortList(soulSkillList);
	}
	
	private void sortList(List<RoleSkillStat> skillList){
		 Util.sortSkillCdDesc(skillList);
	}
	
	@JSONField(serialize=false)
	public int getAttriValue(byte attrType){
		AttributeType attriType = AttributeType.get(attrType);
		/*if(attriType == AttributeType.level){
			return this.level;
		}else if(attriType == AttributeType.maxHP){
			return this.maxHp;
		}else if(attriType == AttributeType.maxMP){
			return this.maxMp;
		}else if(attriType == AttributeType.cdRate){
			return this.cdRate ;
		}else if(attriType == AttributeType.speed){
			return this.speed;
		}else if(attriType == AttributeType.atk){
			return this.atk ;
		}else if(attriType == AttributeType.rit){
			return this.rit ;
		}else if(attriType == AttributeType.sacredAtk){
			return this.sacredAtk ;
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
		}else if(attriType == AttributeType.sacredRit){
			return this.sacredRit;
		}else if(attriType == AttributeType.hit){
			return this.hit;
		}else if(attriType == AttributeType.dodge){
			return this.dodge ;
		}else if(attriType == AttributeType.critAtk){
			return this.critAtk;
		}else if(attriType == AttributeType.critRit){
			return this.critRit;
		}else if(attriType == AttributeType.critAtkProb){
			return this.critAtkProb ;
		}else if(attriType == AttributeType.hurtRemitRate){
			return this.hurtRemitRate ;
		}else if(attriType == AttributeType.slowRitRate){
			return this.slowRitRate ;
		}else if(attriType == AttributeType.sunderRitRate){
			return this.sunderRitRate;
		}else if(attriType == AttributeType.weakRitRate){
			return this.weakRitRate;
		}else if(attriType == AttributeType.fixedRitRate){
			return this.fixedRitRate;
		}else if(attriType == AttributeType.blowFlyRitRate){
			return this.blowFlyRitRate;
		}else if(attriType == AttributeType.repelRitRate){
			return this.repelRitRate ;
		}else if(attriType == AttributeType.lullRitRate){
			return this.lullRitRate;
		}else if(attriType == AttributeType.tiredRitRate){
			return this.tiredRitRate;
		}else if(attriType == AttributeType.mumRitRate) {
			return this.mumRitRate;
		}else if(attriType == AttributeType.bloodRitRate){
			return this.bloodRitRate ;
		}else if(attriType == AttributeType.charmRitRate){
			return this.charmRitRate ;
		}else if(attriType == AttributeType.comaRitRate){
			return this.comaRitRate ;
		}else if(attriType == AttributeType.mussRitRate){
			return this.mussRitRate;
		}else if(attriType == AttributeType.agedRitRate){
			return this.agedRitRate;
		}else if(attriType == AttributeType.lightRitRate){
			return this.lightRitRate;
		}else if(attriType == AttributeType.slowAddRate){
			return this.slowAddRate ;
		}else if(attriType == AttributeType.sunderAddRate){
			return this.sunderAddRate;
		}else if(attriType == AttributeType.weakAddRate){
			return this.weakAddRate;
		}else if(attriType == AttributeType.fixedAddRate){
			return this.fixedAddRate;
		}else if(attriType == AttributeType.blowFlyAddRate){
			return this.blowFlyAddRate;
		}else if(attriType == AttributeType.repelAddRate){
			return this.repelAddRate ;
		}else if(attriType == AttributeType.lullAddRate){
			return this.lullAddRate;
		}else if(attriType == AttributeType.tiredAddRate){
			return this.tiredAddRate;
		}else if(attriType == AttributeType.mumAddRate) {
			return this.mumAddRate;
		}else if(attriType == AttributeType.bloodAddRate){
			return this.bloodAddRate ;
		}else if(attriType == AttributeType.charmAddRate){
			return this.charmAddRate ;
		}else if(attriType == AttributeType.comaAddRate){
			return this.comaAddRate ;
		}else if(attriType == AttributeType.mussAddRate){
			return this.mussAddRate;
		}else if(attriType == AttributeType.agedAddRate){
			return this.agedAddRate;
		}else if(attriType == AttributeType.lightAddRate){
			return this.lightAddRate;
		}else if(attriType == AttributeType.healRate){
			return this.healRate;
		}else if(attriType == AttributeType.mpConsumeRate) {
			return this.mpConsumeRate;
		}*/
		switch(attriType){
		case level:
			return this.level;
		case maxHP:
			return this.maxHp;
		case maxMP:
			return this.maxMp;
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
		case phyAtk:
			return this.phyAtk ;
		case iceAtk:
			return this.iceAtk ;
		case fireAtk:
			return this.fireAtk ;
		case phyRit:
			return this.phyRit;
		case iceRit:
			return this.iceRit;
		case fireRit:
			return this.fireRit;
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
		case agedRitRate:
			return this.agedRitRate;
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
		case agedAddRate:
			return this.agedAddRate;
		case lightAddRate:
			return this.lightAddRate;
		case healRate:
			return this.healRate;
		case mpConsumeRate:
			return this.mpConsumeRate;
		}
		return 0;
	}
	
	public boolean setAttriValue(byte attrType, int value){
		AttributeType attriType = AttributeType.get(attrType);
		switch(attriType){
		case level:
			this.level = value; return true;
		case maxHP:
			this.maxHp = value; return true;
		case maxMP:
			this.maxMp = value; return true;
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
		case phyAtk:
			this.phyAtk = value; return true;
		case iceAtk:
			this.iceAtk = value; return true;
		case fireAtk:
			this.fireAtk = value; return true;
		case phyRit:
			this.phyRit = value; return true;
		case iceRit:
			this.iceRit = value; return true;
		case fireRit:
			this.fireRit = value; return true;
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
		case agedRitRate:
			this.agedRitRate = value; return true;
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
		case agedAddRate:
			this.agedAddRate = value; return true;
		case lightAddRate:
			this.lightAddRate = value; return true;
		case healRate:
			this.healRate = value; return true;
		case mpConsumeRate:
			this.mpConsumeRate = value; return true;
		}
		return false;
	}
	
}
