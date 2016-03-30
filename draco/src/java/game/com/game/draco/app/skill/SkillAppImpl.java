package com.game.draco.app.skill;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.config.PathConfig;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.component.script.ScriptSupport;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.buff.BuffAddResult;
import com.game.draco.app.buff.BuffContext;
import com.game.draco.app.buff.BuffDetail;
import com.game.draco.app.buff.stat.BuffStat;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.npc.domain.NpcTemplate;
import com.game.draco.app.npc.type.NpcType;
import com.game.draco.app.skill.config.SkillApplyType;
import com.game.draco.app.skill.config.SkillAttackType;
import com.game.draco.app.skill.config.SkillAttr;
import com.game.draco.app.skill.config.SkillAttrC;
import com.game.draco.app.skill.config.SkillBase;
import com.game.draco.app.skill.config.SkillBattleScore;
import com.game.draco.app.skill.config.SkillBattleScoreC;
import com.game.draco.app.skill.config.SkillBuff;
import com.game.draco.app.skill.config.SkillBuffC;
import com.game.draco.app.skill.config.SkillDetail;
import com.game.draco.app.skill.config.SkillEffectType;
import com.game.draco.app.skill.config.SkillHurt;
import com.game.draco.app.skill.config.SkillHurtC;
import com.game.draco.app.skill.config.SkillLearnBaseMoney;
import com.game.draco.app.skill.config.SkillLearnBasePotential;
import com.game.draco.app.skill.config.SkillLearnConfig;
import com.game.draco.app.skill.config.SkillLearnConsume;
import com.game.draco.app.skill.config.SkillScope;
import com.game.draco.app.skill.config.SkillSourceType;
import com.game.draco.app.skill.config.SkillTargetType;
import com.game.draco.app.skill.domain.RoleSkillStat;
import com.game.draco.app.skill.domain.SAttrC;
import com.game.draco.app.skill.domain.SBuffC;
import com.game.draco.app.skill.domain.SHurt;
import com.game.draco.app.skill.func.SkillLearnFunc;
import com.game.draco.app.skill.vo.Skill;
import com.game.draco.app.skill.vo.SkillAdaptor;
import com.game.draco.app.skill.vo.SkillFormula;
import com.game.draco.app.skill.vo.SkillReformActive;
import com.game.draco.app.skill.vo.SkillReformPassive;
import com.game.draco.message.item.RoleSkillItem;
import com.game.draco.message.item.SkillShowItem;
import com.game.draco.message.push.C1110_HeroSkillNotifyMessage;
import com.game.draco.message.push.C1111_RoleShapeNotifyMessage;
import com.game.draco.message.push.C1112_RoleColorNotifyMessage;
import com.game.draco.message.push.C1113_RoleZoomNotifyMessage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class SkillAppImpl extends SkillApp {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final float TEN_THOUSAND_F = SkillFormula.TEN_THOUSAND;

	private PathConfig pathConfig;
	private ScriptSupport scriptSupport;
	private Map<Short,Map<Integer,SkillDetail>> skillDetailMap = Maps.newHashMap();
	// 新技能加载
	private Map<Short, SkillBase> skillBaseMap = Maps.newLinkedHashMap();
	private Map<Short, List<SkillAttr>> skillAttrMap = Maps.newHashMap();
	private Map<Integer, Map<Byte,SkillAttrC>> skillAttrCMap = Maps.newHashMap();
//	private Map<Short, SkillMp> skillMpMap = Maps.newHashMap();
//	private Map<Integer, SkillMpC> skillMpCMap = Maps.newHashMap();
	private Map<Short, List<SkillHurt>> skillHurtMap = Maps.newHashMap();
	private Map<String, SkillHurtC> skillHurtCMap = Maps.newHashMap();
	private Map<Short, SkillBattleScore> skillBattleScoreMap = Maps.newHashMap();
	private Map<Integer, SkillBattleScoreC> skillBattleScoreCMap = Maps.newHashMap();
	private Map<Short, List<SkillBuff>> skillBuffMap = Maps.newHashMap();
	private Map<String, SkillBuffC> skillBuffCMap = Maps.newHashMap();
	private Map<Short,List<SkillScope>> skillScopeMap = Maps.newHashMap();
	
	/**
	 * 自动学习第1级的主动技能列表
	 * MAP:  key: roleLevel  value: skillId lists
	 */
	private Map<Integer,List<Short>> autoLearnActiveSkills = Maps.newHashMap();
	private Map<Short,short []> skillMusicMap = Maps.newHashMap() ;
	
	
	public Skill getSkill(short skillId) {
		if(skillId <=0){
			return null ;
		}
		return skillMap.get(skillId);
	}

	public void setPathConfig(PathConfig pathConfig) {
		this.pathConfig = pathConfig;
	}

	public void setScriptSupport(ScriptSupport scriptSupport) {
		this.scriptSupport = scriptSupport;
	}
	
	private void load(){
		initSkillDetail();
		scriptSupport.loadScript(pathConfig.getSkillPath());
		this.build();	
		skillDetailMap.clear();
	}
	
	/**
	 * 载入技能配置表
	 */
	private void initSkillDetail() {
		
		// 技能基础数据
		loadSkillBase();

		// 技能属性数据
		loadSkillAttr();
		loadSkillAttrC();

//		 技能消耗蓝数据
//		loadSkillMp();
//		loadSkillMpC();

		// 技能伤害数据
		loadSkillHurt();
		loadSkillHurtC();

		// 技能战力数据
		loadSkillBattleScore();
		loadSkillBattleScoreC();

		// 技能挂buff数据
		loadSkillBuff();
		loadSkillBuffC();
		
		//技能区域
		loadSkillScope();

		initSkillMap();
		
	}
	
	private void initSkillMap(){
		for (Entry<Short, SkillBase> skillBase : skillBaseMap.entrySet()) {
			Skill skill = null;
			if (skillBase.getValue().getSkillApplyType() == SkillApplyType.active
					.getType()) {
				skill = new SkillReformActive(skillBase.getKey());
			} else {
				skill = new SkillReformPassive(skillBase.getKey());
			}
			skill.setName(skillBase.getValue().getName());
			skill.setIconId(skillBase.getValue().getIconId());
			skill.setSkillApplyType(SkillApplyType.get(skillBase.getValue()
					.getSkillApplyType()));
			skill.setSkillAttackType(SkillAttackType.get(skillBase.getValue()
					.getAttackType()));
			skill.setSkillEffectType(SkillEffectType.get(skillBase.getValue()
					.getSkillEffectType()));
			skill.setSkillSourceType(SkillSourceType.get(skillBase.getValue()
					.getSourceType()));
			skill.setSkillTargetType(SkillTargetType.get(skillBase.getValue()
					.getServerTargetType()));
			skill.setTriggerPassive(skillBase.getValue().isTriggerPassive());
			skill.setUseGlobalCd(skillBase.getValue().isUseGlobalCd());
			skillMap.put(skillBase.getKey(), skill);

			// 循环等级
			for (int i = 1; i <= skillBase.getValue().getMaxLevel(); i++) {
				// 初始化
				SkillDetail detail = new SkillDetail();
				detail.setSkillId(skillBase.getKey());
				detail.setLevel(i);
				detail.setName(skillBase.getValue().getName());
				detail.setSourceType(skillBase.getValue().getSourceType());
				detail.setIconId(skillBase.getValue().getIconId());
				detail.setEffectId(skillBase.getValue().getEffectId());
				detail.setActionId(skillBase.getValue().getActionId());
				detail.setMusicId(skillBase.getValue().getMusicId());
				detail.setSkillApplyType(skillBase.getValue()
						.getSkillApplyType());
				detail.setCd(skillBase.getValue().getCd());
				detail.setMinUseRange(skillBase.getValue().getMinUseRange());
				detail.setMaxUseRange(skillBase.getValue().getMaxUseRange());
				detail.setServerTargetType(skillBase.getValue()
						.getServerTargetType());
				detail.setClientTargetType(skillBase.getValue()
						.getClientTargetType());
				detail.setTriggerPassive(skillBase.getValue()
						.isTriggerPassive());
				detail.setUseGlobalCd(skillBase.getValue().isUseGlobalCd());
				detail.setHitChange(skillBase.getValue().getHitChange());
				detail.setCritChange(skillBase.getValue().getCritChange());
				detail.setAttackType(skillBase.getValue().getAttackType());
				detail.setPrepareArg(skillBase.getValue().getPrepareArg());
				detail.setHatredPercent(skillBase.getValue().getHatredPercent());
				detail.setHatredAdd(skillBase.getValue().getHatredAdd());
				detail.setAffectSkills(skillBase.getValue().getAffectSkills());
				detail.setTargetAnimId(skillBase.getValue().getTargetAnimId());
				detail.setTargetEffectId(skillBase.getValue().getTargetEffectId());
				detail.setDesc(skillBase.getValue().getDes());
				detail.setFixedXy(skillBase.getValue().isFixedXy());
				detail.setGuideSkill(skillBase.getValue().isGuideSkill());
				detail.setSkillEffectType(skillBase.getValue().getSkillEffectType());
				detail.setSelectionRules(skillBase.getValue().isSelectionRules());
				detail.setBlackGround(skillBase.getValue().isBlackGround());
				detail.setContinueEffectId(skillBase.getValue().isContinueEffectId());
				
				if (skillAttrMap.containsKey(skillBase.getKey())) {
					List<SkillAttr> skillAttrList = skillAttrMap.get(skillBase
							.getKey());
					List<SAttrC> acList = Lists.newArrayList();
					if (skillAttrCMap.containsKey(i)) {
						Map<Byte,SkillAttrC> attrCMap = skillAttrCMap.get(i);
						String skillAttr = "{attr";
						for (int ai = 0; ai < skillAttrList.size(); ai++) {
							String des = detail.getDesc();
							SkillAttr attr = skillAttrList.get(ai);
							SkillAttrC attrC = attrCMap.get(attr.getAttrType());
							SAttrC ac = new SAttrC();
							ac.setA(attr.getA());
							ac.setAttrType(attr.getAttrType());
							ac.setB(attr.getB());
							ac.setC(attrC.getC());
							ac.setD(attr.getD());
							ac.setReduce(attr.getReduce());
							ac.setTargetType(attr.getTargetType());
							ac.setModifyTargetAttr(attr.getModifyTargetAttr());
							ac.setAreaId(attr.getAreaId());
							acList.add(ac);
							
							String desc = attr.getDes();
							desc = Util.replaceDes(0, attr.getA(), attr.getB(),
									attrC.getC(), attr.getD(), desc);
							detail.setDesc(des.replace(skillAttr + ai + "}",
									desc));
						}
					}
					if(!Util.isEmpty(acList)){
						for(SAttrC attr : acList){
							if(detail.getSkillAttriMap().containsKey(attr.getAreaId())){
								detail.getSkillAttriMap().get(attr.getAreaId()).add(attr);
							}else{
								List<SAttrC> acl = Lists.newArrayList();
								acl.add(attr);
								detail.getSkillAttriMap().put(attr.getAreaId(), acl);
							}
						}
					}
				}

				if (skillHurtMap.containsKey(skillBase.getKey())) {
					List<SkillHurt> skillHurtList = skillHurtMap.get(skillBase.getKey());
					List<SHurt> sHList = Lists.newArrayList();
					for(SkillHurt skillHurt : skillHurtList){
						String key = skillHurt.getPlanId() + Cat.underline + i;
						if (skillHurtCMap.containsKey(key)) {
							SHurt sHurt = new SHurt();
							sHurt.setA(skillHurt.getA());
							sHurt.setAttrType(skillHurt.getAttrType());
							sHurt.setB(skillHurt.getB());
							sHurt.setD(skillHurt.getD());
							sHurt.setHurtType(skillHurt.getHurtType());
							sHurt.setReduce(skillHurt.getReduce());
							sHurt.setTargetType(skillHurt.getTargetType());
							sHurt.setModifyTargetType(skillHurt.getModifyTargetType());
							sHurt.setAreaId(skillHurt.getAreaId());
							SkillHurtC hurtC = skillHurtCMap.get(key);
							sHurt.setC(hurtC.getC());
							sHList.add(sHurt);
							
							String hurtDes = skillHurt.getDes();
							hurtDes = Util.replaceDes(0, skillHurt.getA(),
									skillHurt.getB(), hurtC.getC(),
									skillHurt.getD(), hurtDes);
							String des = detail.getDesc();
							String damage = "{damage0}";
							detail.setDesc(des.replace(damage, hurtDes));
						}
					}
					if(!Util.isEmpty(sHList)){
						for(SHurt hurt : sHList){
							detail.getSHurtMap().put(hurt.getAreaId(),hurt);
						}
					}
				}

				if (skillBuffMap.containsKey(skillBase.getKey())) {
					List<SkillBuff> skillBuffList = skillBuffMap.get(skillBase.getKey());
					List<SBuffC> list = Lists.newArrayList(); 
					String buffRate = "{rate";
					String buffR = "{buff";
					for (int bi = 0; bi < skillBuffList.size(); bi++) {
						SkillBuff buff = skillBuffList.get(bi);
						String key = buff.getPlanId() + Cat.underline + i;
						if (skillBuffCMap.containsKey(key)) {
							String des = detail.getDesc();
							SkillBuffC buffC = skillBuffCMap.get(key);
							//上buff 是自己还是对方
							SBuffC bc = new SBuffC();
							bc.setB(buff.getB());
							bc.setBuffId(buff.getBuffId());
							bc.setC(buffC.getC());
							bc.setD(buff.getD());
							bc.setTargetType(buff.getTargetType());
							bc.setAreaId(buff.getAreaId());
							list.add(bc);
							String buffDes = buff.getDes();
							buffDes = Util.replaceDes(0, 0, buff.getB(),
									buffC.getC(), buff.getD(), buffDes);
							detail.setDesc(des.replace(buffRate + bi + "}",
									buffDes));
							BuffDetail buffDetail = GameContext.getBuffApp().getBuffDetail(buff.getBuffId(), i);
							if(buffDetail != null){
								des = detail.getDesc();
								detail.setDesc(des.replace(buffR + bi + "}",
										buffDetail.getDesc()));
							}
						}
					}
					if(!Util.isEmpty(list)){
						for(SBuffC buff : list){
							if(detail.getSkillBuffMap().containsKey(buff.getAreaId())){
								detail.getSkillBuffMap().get(buff.getAreaId()).add(buff);
							}else{
								List<SBuffC> acl = Lists.newArrayList();
								acl.add(buff);
								detail.getSkillBuffMap().put(buff.getAreaId(), acl);
							}
						}
					}
				}
				
				if (skillScopeMap.containsKey(skillBase.getKey())) {
					List<SkillScope> skillScopeList = skillScopeMap.get(skillBase
							.getKey());
					detail.setSkillScopeList(skillScopeList);
				}

//				SkillMp skillMp = skillMpMap.get(skillBase.getKey());
//				// 消耗MP
//				if (skillMp != null) {
//					if (skillMpCMap.containsKey(i)) {
//						int mp = getBcd(skillMp.getB(),skillMpCMap.get(i).getC(),skillMp.getD()); 
//						detail.setMp(mp);
//					}
//				}

				// 技能战力
				SkillBattleScore skillBattleScore = skillBattleScoreMap.get(skillBase.getKey());
				if (skillBattleScore != null) {
					if (skillBattleScoreCMap.containsKey(i)) {
						int battleScore = getBcd(skillBattleScore.getB(),skillBattleScoreCMap.get(i).getC(),skillBattleScore.getD());
						detail.setBattleScore(battleScore);
					}
				}
				
				Map<Integer,SkillDetail> map = null;
				if(skillDetailMap.containsKey(skillBase.getKey())){
					map = skillDetailMap.get(skillBase.getKey());
					map.put(i, detail);
				}else{
					map = Maps.newHashMap();
					map.put(i, detail);
					skillDetailMap.put(skillBase.getKey(), map);
				}
			}
			//被击音效
			if(null != skillBase.getValue().getMusicIds()){
				skillMusicMap.put(skillBase.getKey(), skillBase.getValue().getMusicIds());
			}
		}
	}
	
	@Override
	public void start() {
		this.load();
	}
	
	@Override
	public  boolean reLoad(){
		if(GameContext.isOfficialServer()){
			//正式服务器不运行此操作,此操作只是便于调试加载
			return false;
		}
		try {
			logger.info("reload skill start");
			this.load();
			logger.info("reload skill end");
			return true ;
		}catch(Exception ex){
			logger.error("reload skill error",ex);
		}
		return false ;
	}
	
	@Override
	public void stop() {
	}

	@Override
	public Collection<Skill> getAllSkill() {
		return skillMap.values();
	}
	
	protected void checkFail(String info){
		Log4jManager.checkFail();
		Log4jManager.CHECK.error(info);
	}
	
	/**
	 * 构建技能配置 Skill--SkillDetail
	 */
	private void build(){
		for(Skill skill : skillMap.values()){
			if(null == skill){
				continue;
			}
			short skillId = skill.getSkillId();
			String info = "initSkill error : ---skillId=" + skillId;
			if(skillId <= 0){
				this.checkFail(info + "---The skillId is error!");
				continue;
			}
			Map<Integer,SkillDetail> details = skillDetailMap.remove(skillId);
			if(Util.isEmpty(details)){
				this.checkFail(info + "---The skill does not config levels and parameters!");
				continue ;
			}
			((SkillAdaptor)skill).putSkillDetail(details);
		}
		//xls中配置的技能没有技能脚本
		if(!skillDetailMap.isEmpty()){
			for(short id : skillDetailMap.keySet()){
				this.checkFail("initSkill error : ---skillId=" + id + "---The skill in the script does not exist!");
			}
		}
		loadSkillLearnDetail();
	}
	
	/**
	 * 初始化自动学习技能列表
	 * @param careerType
	 * @param detail
	 * @param skill
	 */
	private void initAutoLearn(SkillDetail detail, Skill skill){
		//只第一级
		if(detail.getLevel() != 1){
			return ;
		}
		//只需要主动技能
		if(skill.getSkillApplyType() != SkillApplyType.active){
			return ;
		}
		int roleLevel = detail.getLevel();
		List<Short> skills = this.autoLearnActiveSkills.get(roleLevel);
		if(null == skills){
			skills = Lists.newArrayList();
			this.autoLearnActiveSkills.put(roleLevel, skills);
		}
		skills.add(skill.getSkillId());
	}
	
	
	@Override
	public void setArgs(Object args) {
		
	}

	@Override
	public Map<Short, Skill> getSkillMap() {
		return skillMap;
	}

	@Override
	public List<Short> getAutoLearnSkills(int roleLevel) {
		return this.autoLearnActiveSkills.get(roleLevel);
	}

	@Override
	public List<SkillShowItem> getSkillShowItemList(RoleInstance role, 
			SkillLearnFunc learnFunc, String parameter) {
		List<Short> skillList = learnFunc.getSkillList(role, parameter);
		if(Util.isEmpty(skillList)) {
			return null;
		}
		List<SkillShowItem> items = new ArrayList<SkillShowItem>();
		for(Short skillId : skillList){
			SkillShowItem item = this.getSkillShowItem(role, learnFunc, parameter, skillId);
			if(null == item){
				continue;
			}
			items.add(item);
		}
		return items;
	}
	
	@Override
	public SkillShowItem getSkillShowItem(RoleInstance role, SkillLearnFunc learnFunc, String parameter, short skillId){
		Skill skill = GameContext.getSkillApp().getSkill(skillId);
		if(null == skill){
			return null;
		}
		SkillShowItem item = new SkillShowItem();
		item.setSkillId(skillId);
		item.setSkillName(skill.getName());
		item.setSkillIcon(skill.getIconId());
		item.setSkillType(skill.getSkillApplyType().getType());
		int currLevel = learnFunc.getSkillLevel(role, skillId, parameter);
		item.setMaxLevel((byte)skill.getMaxLevel());
		item.setCurrLevel((byte)currLevel);
		if(currLevel < skill.getMaxLevel()){
			SkillDetail nextDetail = skill.getSkillDetail(currLevel + 1);
			item.setRoleLevel((byte)nextDetail.getLevel());
//			if(SkillSourceType.Hero == learnFunc.getSkillSourceType()
//					|| SkillSourceType.Pet == learnFunc.getSkillSourceType()){
//				//内部等级
//				item.setRoleLevel((byte)nextDetail.getInnerLevel());
//			}
//			short relySkillId = nextDetail.getRelySkillId();
//			item.setRelySkillId(relySkillId);
//			item.setRelySkillLevel(nextDetail.getRelySkillLevel());
//			Skill relySkill = GameContext.getSkillApp().getSkill(relySkillId);
//			if(null != relySkill){
//				item.setRelySkillName(relySkill.getName());
//			}
//			GoodsLiteNamedItem goodsLiteNamedItem = nextDetail.getConsumeGoodsLiteNamedItem();
//			if(null != goodsLiteNamedItem){
//				item.setGoodsLiteNamedItem(goodsLiteNamedItem);
//			}
			item.setExpendAttrList(nextDetail.getConsumeAttrTypeValueList());
		}
		return item;
	}

	@Override
	public String skillIdLevelString(Map<Short, RoleSkillStat> map) {
		if(Util.isEmpty(map)){
			return "" ;
		}
		String cat = "" ;
		StringBuffer buffer = new StringBuffer();
		for(Iterator<Map.Entry<Short, RoleSkillStat>> it = map.entrySet().iterator();it.hasNext();){
			Map.Entry<Short, RoleSkillStat> entry = it.next() ;
			buffer.append(cat);
			buffer.append(entry.getKey());
			buffer.append(":");
			buffer.append(entry.getValue().getSkillLevel());
			cat = "," ;
		}
		return buffer.toString() ;
	}
	
	@Override
	public int getSkillBattleScore(Map<Short,RoleSkillStat> map) {
		if(Util.isEmpty(map)) {
			return 0;
		}
		int score = 0;
		for(RoleSkillStat skillStat : map.values()) {
			if(null == skillStat) {
				continue ;
			}
			Skill skill = this.getSkill(skillStat.getSkillId());
			if(null == skill) {	
				continue;
			}
			SkillDetail skillDetail = skill.getSkillDetail(skillStat.getSkillLevel());
			if(null == skillDetail) {
				continue;
			}
			score += skillDetail.getBattleScore();
		}
		
		return score;
	}

	@Override
	public void heroMorphChangeSkill(BuffContext context, short[] skillIds) {
		try {
			if(null == skillIds || skillIds.length <= 0) {
				return ;
			}
			BuffStat buffStat = context.getBuffStat();
			if(null == buffStat) {
				return ;
			}
			AbstractRole owner = buffStat.getOwner();
			if(null == owner) {
				return ;
			}
			if(owner.getRoleType() != RoleType.PLAYER) {
				return ;
			}
			//取当前出战英雄
			RoleHero onBattle = GameContext.getUserHeroApp().getOnBattleRoleHero(
					owner.getRoleId());
			//保存出战英雄技能
			Map<Short, RoleSkillStat> skillMap = Maps.newHashMap();
			skillMap.putAll(onBattle.getSkillMap());
			buffStat.setSkillMap(skillMap);
			//删除技能
			for (Short skillId : onBattle.getSkillMap().keySet()) {
				owner.delSkillStat(skillId);
			}
			//添加变身后的技能
			Map<Short, RoleSkillStat> newSkillStatMap = Maps.newHashMap();
			for(Short skillId : skillIds) {
				RoleSkillStat stat = new RoleSkillStat(); 
				stat.setSkillId(skillId.shortValue());
				stat.setSkillLevel(buffStat.getBuffLevel());
				stat.setRoleId("HERO_" + onBattle.getHeroId());
				stat.setLastProcessTime(0);
				newSkillStatMap.put(skillId.shortValue(), stat);
			}
			owner.getSkillMap().putAll(newSkillStatMap);
			this.heroMorphSkillNotify(owner, newSkillStatMap);
		} catch (Exception ex) {
			this.logger.error("skillApp.roleMorphChangeSkill() error, ", ex);
		}
		
	}
	
	@Override
	public void heroMorphRecoverSkill(BuffContext context) {
		try {
			BuffStat buffStat = context.getBuffStat();
			if(null == buffStat) {
				return ;
			}
			AbstractRole owner = buffStat.getOwner();
			if(null == owner) {
				return ;
			}
			if(owner.getRoleType() != RoleType.PLAYER) {
				return ;
			}
			//取当前出战英雄
			RoleHero onBattle = GameContext.getUserHeroApp().getOnBattleRoleHero(
					owner.getRoleId());
			//删除技能
			for (Short skillId : onBattle.getSkillMap().keySet()) {
				owner.delSkillStat(skillId);
			}
			//取buffStat中存的变身前的技能
			owner.getSkillMap().putAll(buffStat.getSkillMap());
			this.heroMorphSkillNotify(owner, buffStat.getSkillMap());
		}catch (Exception ex) {
			this.logger.error("skillApp.roleMorphRecoverSkill() error, ", ex);
		}
	}
	
	private void heroMorphSkillNotify(AbstractRole role, Map<Short, RoleSkillStat> skillMap) {
		C1110_HeroSkillNotifyMessage respMsg = new C1110_HeroSkillNotifyMessage();
		respMsg.setSkillItems(GameContext.getSkillApp().getRoleSkillItem((RoleInstance)role, skillMap.values()));
		role.getBehavior().sendMessage(respMsg);
	}

	@Override
	public List<RoleSkillItem> getRoleSkillItem(RoleInstance role, Collection<RoleSkillStat> skillStats) {
		List<RoleSkillItem> skillItems = Lists.newArrayList();
		try {
			if (Util.isEmpty(skillStats)) {
				return skillItems;
			}
			for (RoleSkillStat stat : skillStats) {
				Skill skill = GameContext.getSkillApp().getSkill(
						stat.getSkillId());
				RoleSkillItem item = sacred.alliance.magic.util.Converter
						.getRoleSkillItem(role, skill, stat.getSkillLevel(),
								stat.getLastProcessTime());
				skillItems.add(item);
			}
			Collections.sort(skillItems, new Comparator<RoleSkillItem>() {
				@Override
				public int compare(RoleSkillItem o1, RoleSkillItem o2) {
					if (o1.getSkillId() < o2.getSkillId()) {
						return -1;
					}
					if (o1.getSkillId() > o2.getSkillId()) {
						return 1;
					}
					return 0;
				}
			});
			return skillItems;
		}catch(Exception ex){
			logger.error("",ex);
		}
		return skillItems ;
	}

	@Override
	public void roleRecoverShape(AbstractRole role) {
		if(null == role) {
			return ;
		}
		if(role.getRoleType() == RoleType.PLAYER) {
			//取当前出战英雄
			RoleHero onBattle = GameContext.getUserHeroApp().getOnBattleRoleHero(
					role.getRoleId());
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(onBattle.getHeroId());
			if(null == gb) {
				return ;
			}
			this.roleMorphShapeNotifyMessage(role, gb.getResId(),false);
			return ;
		}
		//如果是npc则取模版
		//如果是npc改变外形则需要恢复状态
		NpcInstance npc = ((NpcInstance)role);
		npc.setChangeShape(false);
		this.roleMorphShapeNotifyMessage(npc, npc.getNpc().getResid(),false);
	}

	@Override
	public void roleChangeShape(AbstractRole role, int resId, int effectTime) {
		if(null == role) {
			return ;
		}
		if(resId <= 0) {
			return ;
		}
		BuffAddResult result = GameContext.getUserBuffApp().addBuffStat(role, role, 
				GameContext.getSkillConfig().getShapeBuffId(), effectTime, 1);
		if(!result.isSuccess()){
			return ;
		}
		
		if(role.getRoleType() == RoleType.NPC) {
			//如果是npc改变外形则需要记录状态
			((NpcInstance)role).setChangeShape(true);
		}
		this.roleMorphShapeNotifyMessage(role, resId,true);
	}
	
	/**
	 * 角色或npc改变外形通知
	 * @param role
	 * @param resId
	 */
	private void roleMorphShapeNotifyMessage(AbstractRole role, int resId,boolean inShapeState) {
		C1111_RoleShapeNotifyMessage respMsg = new C1111_RoleShapeNotifyMessage();
		respMsg.setRoleId(role.getIntRoleId());
		respMsg.setClothesResId((short)resId);
		respMsg.setShapeState(inShapeState?(byte)1:(byte)0);
		//通知自己
		role.getBehavior().sendMessage(respMsg);
		//广播
		MapInstance map = role.getMapInstance();
		if(null == map) {
			return ;
		}
		map.broadcastMap(role, respMsg);
	}
	
	@Override
	public void roleChangeColor(AbstractRole role, int color, int effectTime) {
		if(null == role) {
			return ;
		}
		//-1为白色(原始颜色)
		if(color == -1) {
			return ;
		}
//		BuffAddResult result = GameContext.getUserBuffApp().addBuffStat(role, role, GameContext.getSkillConfig().getColorBuffId(), effectTime, 1);
//		if(!result.isSuccess()){
//			return ;
//		}
		role.setColor(color);
		this.roleMorphColorNotifyMessage(role, color);
		
	}
	
	@Override
	public void roleRecoverColor(AbstractRole role) {
		if(null == role) {
			return ;
		}
		role.setColor(-1);
		this.roleMorphColorNotifyMessage(role, -1);
		
	}
	
	/**
	 * 角色或npc改变颜色通知
	 * @param role
	 * @param resId
	 */
	private void roleMorphColorNotifyMessage(AbstractRole role, int color) {
		C1112_RoleColorNotifyMessage respMsg = new C1112_RoleColorNotifyMessage();
		respMsg.setRoleId(role.getIntRoleId());
		respMsg.setColor(color);
		//通知自己
		role.getBehavior().sendMessage(respMsg);
		//广播
		MapInstance map = role.getMapInstance();
		if(null == map) {
			return ;
		}
		map.broadcastMap(role, respMsg);
	}

	@Override
	public void roleChangeZoom(AbstractRole role, int zoom, int effectTime) {
		if(null == role) {
			return ;
		}
		//10标识100%(原始大小)
		if(zoom <= 0) {
			return ;
		}
//		BuffAddResult result = GameContext.getUserBuffApp().addBuffStat(role, role, 
//				GameContext.getSkillConfig().getZoomBuffId(), effectTime, 1);
//		if(!result.isSuccess()){
//			return ;
//		}
		this.roleMorphZoomNotifyMessage(role, zoom);
	}
	
	@Override
	public void roleRecoverZoom(AbstractRole role) {
		if(role == null) {
			return ;
		}
		this.roleMorphZoomNotifyMessage(role, -1);
	}
	
	/**
	 * 角色或npc缩放通知
	 * @param role
	 * @param resId
	 */
	private void roleMorphZoomNotifyMessage(AbstractRole role, int zoom) {
		C1113_RoleZoomNotifyMessage respMsg = new C1113_RoleZoomNotifyMessage();
		respMsg.setRoleId(role.getIntRoleId());
		respMsg.setZoom((byte)zoom);
		//通知自己
		role.getBehavior().sendMessage(respMsg);
		//广播
		MapInstance map = role.getMapInstance();
		if(null == map) {
			return ;
		}
		map.broadcastMap(role, respMsg);
	}

	@Override
	public void npcMorph(AbstractRole role, int resId, int color, int zoom,
			int effectTime) {
	}

	@Override
	public void roleCopySelf(RoleInstance role, int atkRate, int atkValue, int number,
			int radius, short skillId, int skillLv, int lifeTime, short buffId) {
		MapInstance mapInstance = role.getMapInstance();
		if(null == mapInstance) {
			return ;
		}
		mapInstance.roleCopySelf(role, atkRate, atkValue, number, radius, skillId, skillLv, lifeTime, buffId);
	}
	
	@Override
	public void roleCopyAttack(RoleInstance role, AbstractRole targetRole) {
		MapInstance mapInstance  = role.getMapInstance();
		if(null == mapInstance) {
			return ;
		}
		
		if(null == targetRole || targetRole.isDeath()) {
			return ;
		}
		mapInstance.roleCopyUseSkill(role, targetRole);
		
	}
	
	@Override
	public NpcTemplate createRoleCopyNpcTemplate(RoleInstance role, int attrRate, int atk, int resId, 
			byte seriesId, byte gearId, int lifeTime) {
		//构建模版
		NpcTemplate template = new NpcTemplate();
		template.setNpcid("-1");
		template.setNpcname(role.getRoleName());
		template.setNpctype(NpcType.rolecopy.getType());
		template.setLevel(role.getLevel());
		template.setResid(resId);
		template.setSeriesId(seriesId);
		template.setGearId(gearId);
		template.setLifeTime(lifeTime);
		template.setMaxHP(role.get(AttributeType.maxHP));
		//template.setMaxMP(role.get(AttributeType.maxMP));
		template.setAtk((int)(role.get(AttributeType.atk) * attrRate / SkillFormula.TEN_THOUSAND_F) + atk);
		template.setRit(role.get(AttributeType.rit));
		template.setSpeed(role.get(AttributeType.speed));
		template.setDodge(role.get(AttributeType.dodge));
		template.setHit(role.get(AttributeType.hit));
		template.setCritAtk(role.get(AttributeType.critAtk));
		template.setCritRit(role.get(AttributeType.critRit));
		template.setSlowRitRate(role.get(AttributeType.slowRitRate));
		template.setExposeArmorRitRate(role.get(AttributeType.sunderRitRate));
		template.setWeakRitRate(role.get(AttributeType.weakRitRate));
		template.setFixedRitRate(role.get(AttributeType.fixedRitRate));
		template.setBlowFlyRitRate(role.get(AttributeType.blowFlyRitRate));
		template.setLullRitRate(role.get(AttributeType.lullRitRate));
		template.setTiredRitRate(role.get(AttributeType.tiredRitRate));
		template.setSilenceRitRate(role.get(AttributeType.mumRitRate));
		template.setBloodRitRate(role.get(AttributeType.bloodRitRate));
		template.setCharmRitRate(role.get(AttributeType.charmRitRate));
		template.setComaRitRate(role.get(AttributeType.comaRitRate));
		template.setPoisionRitRate(role.get(AttributeType.poisionRitRate));
		template.setLightRitRate(role.get(AttributeType.lightRitRate));
		template.setParalysisRitRate(role.get(AttributeType.paralysisRitRate));
		return template;
	}
	
		// 替换
		private int getBcd(int b, int c, int d) {
			float rate = (b / TEN_THOUSAND_F) * (c / TEN_THOUSAND_F)
					+ (d / TEN_THOUSAND_F);
			BigDecimal decimal = new BigDecimal(rate);
			rate = decimal.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
			return (int)rate;
		}

		private void loadSkillBase() {
			String fileName = XlsSheetNameType.skill_reform_base.getXlsName();
			String sheetName = XlsSheetNameType.skill_reform_base.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			skillBaseMap = XlsPojoUtil.sheetToGenericLinkedMap(sourceFile,
					sheetName, SkillBase.class);
			if (Util.isEmpty(skillBaseMap)) {
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("load excel error : fileName=" + fileName
						+ ",sheetName=" + sheetName + ",skillBaseMap is null");
				return;
			}
			
			for(Entry<Short, SkillBase> skillBase : skillBaseMap.entrySet()){
				skillBase.getValue().init();
			}
		}

		private void loadSkillAttr() {
			String fileName = XlsSheetNameType.skill_reform_attr.getXlsName();
			String sheetName = XlsSheetNameType.skill_reform_attr.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<SkillAttr> list = XlsPojoUtil.sheetToList(sourceFile, sheetName,
					SkillAttr.class);
			if (Util.isEmpty(list)) {
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("load excel error : fileName=" + fileName
						+ ",sheetName=" + sheetName + ",skillAttrList is null");
			}

			for (SkillAttr attr : list) {
				if (skillAttrMap.containsKey(attr.getSkillId())) {
					skillAttrMap.get(attr.getSkillId()).add(attr);
				} else {
					List<SkillAttr> attrList = Lists.newArrayList();
					attrList.add(attr);
					skillAttrMap.put(attr.getSkillId(), attrList);
				}
			}
		}

		private void loadSkillAttrC() {
			String fileName = XlsSheetNameType.skill_reform_attr_c.getXlsName();
			String sheetName = XlsSheetNameType.skill_reform_attr_c.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<SkillAttrC> list= XlsPojoUtil.sheetToList(sourceFile, sheetName,
					SkillAttrC.class);
			if (Util.isEmpty(list)) {
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("load excel error : fileName=" + fileName
						+ ",sheetName=" + sheetName + ",skillAttrCMap is null");
				return;
			}
			
			for(SkillAttrC attr : list){
				if(skillAttrCMap.containsKey(attr.getSkillLevel())){
					skillAttrCMap.get(attr.getSkillLevel()).put(attr.getAttrType(), attr);
				}else{
					Map<Byte,SkillAttrC> attrMap = Maps.newHashMap();
					attrMap.put(attr.getAttrType(), attr);
					skillAttrCMap.put(attr.getSkillLevel(),attrMap);
				}
			}
			
		}

		private void loadSkillHurt() {
			String fileName = XlsSheetNameType.skill_reform_hurt.getXlsName();
			String sheetName = XlsSheetNameType.skill_reform_hurt.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<SkillHurt> skillHurtList = XlsPojoUtil.sheetToList(sourceFile, sheetName,
					SkillHurt.class);
			if (Util.isEmpty(skillHurtList)) {
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("load excel error : fileName=" + fileName
						+ ",sheetName=" + sheetName + ",skillHurtMap is null");
			}
			for (SkillHurt skillHurt : skillHurtList) {
				if (skillHurtMap.containsKey(skillHurt.getSkillId())) {
					skillHurtMap.get(skillHurt.getSkillId()).add(skillHurt);
				} else {
					List<SkillHurt> list = Lists.newArrayList();
					list.add(skillHurt);
					skillHurtMap.put(skillHurt.getSkillId(), list);
				}
			}
		}

		private void loadSkillHurtC() {
			String fileName = XlsSheetNameType.skill_reform_hurt_c.getXlsName();
			String sheetName = XlsSheetNameType.skill_reform_hurt_c.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			skillHurtCMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName,
					SkillHurtC.class);
			if (Util.isEmpty(skillHurtCMap)) {
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("load excel error : fileName=" + fileName
						+ ",sheetName=" + sheetName + ",skillHurtCMap is null");
			}
		}

		private void loadSkillBattleScore() {
			String fileName = XlsSheetNameType.skill_reform_battlescore
					.getXlsName();
			String sheetName = XlsSheetNameType.skill_reform_battlescore
					.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			skillBattleScoreMap = XlsPojoUtil.sheetToGenericMap(sourceFile,
					sheetName, SkillBattleScore.class);
			if (Util.isEmpty(skillBattleScoreMap)) {
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("load excel error : fileName=" + fileName
						+ ",sheetName=" + sheetName
						+ ",skillBattleScoreMap is null");
			}
		}

		private void loadSkillBattleScoreC() {
			String fileName = XlsSheetNameType.skill_reform_battlescore_c
					.getXlsName();
			String sheetName = XlsSheetNameType.skill_reform_battlescore_c
					.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			skillBattleScoreCMap = XlsPojoUtil.sheetToGenericMap(sourceFile,
					sheetName, SkillBattleScoreC.class);
			if (Util.isEmpty(skillBattleScoreCMap)) {
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("load excel error : fileName=" + fileName
						+ ",sheetName=" + sheetName
						+ ",skillBattleScoreCMap is null");
			}
		}

		private void loadSkillBuff() {
			String fileName = XlsSheetNameType.skill_reform_buff.getXlsName();
			String sheetName = XlsSheetNameType.skill_reform_buff.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<SkillBuff> list = XlsPojoUtil.sheetToList(sourceFile, sheetName,
					SkillBuff.class);
			if (Util.isEmpty(list)) {
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("load excel error : fileName=" + fileName
						+ ",sheetName=" + sheetName + ",skillBuffMap is null");
			}
			for (SkillBuff skillBuff : list) {
				if (skillBuffMap.containsKey(skillBuff.getSkillId())) {
					skillBuffMap.get(skillBuff.getSkillId()).add(skillBuff);
				} else {
					List<SkillBuff> skillBuffList = Lists.newArrayList();
					skillBuffList.add(skillBuff);
					skillBuffMap.put(skillBuff.getSkillId(), skillBuffList);
				}
			}
		}

		private void loadSkillBuffC() {
			String fileName = XlsSheetNameType.skill_reform_buff_c.getXlsName();
			String sheetName = XlsSheetNameType.skill_reform_buff_c.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			skillBuffCMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName,
					SkillBuffC.class);
			if (Util.isEmpty(skillBuffCMap)) {
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("load excel error : fileName=" + fileName
						+ ",sheetName=" + sheetName + ",skillBuffCMap is null");
			}
		}

		private void loadSkillScope(){
			String fileName = XlsSheetNameType.skill_reform_scope.getXlsName();
			String sheetName = XlsSheetNameType.skill_reform_scope.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<SkillScope> list = XlsPojoUtil.sheetToList(sourceFile, sheetName,
					SkillScope.class);
			if (Util.isEmpty(list)) {
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("load excel error : fileName=" + fileName
						+ ",sheetName=" + sheetName + ",SkillScopelist is null");
				return;
			}
			
			List<SkillScope> skillScopeList = null;
			for(SkillScope skillScope : list){
				if(skillScopeMap.containsKey(skillScope.getSkillId())){
					skillScopeList = skillScopeMap.get(skillScope.getSkillId());
					skillScopeList.add(skillScope);
				}else{
					skillScopeList = Lists.newArrayList();
					skillScopeList.add(skillScope);
					skillScopeMap.put(skillScope.getSkillId(),skillScopeList);
				}
			}
		}

		private void loadSkillLearnDetail(){
			
			//加载技能学习配置
			String info = "";
			String fileName = XlsSheetNameType.skill_reform_learn_base_money.getXlsName();
			String sheetName = XlsSheetNameType.skill_reform_learn_base_money.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			Map<Short,SkillLearnBaseMoney> learnBaseMoneyMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, SkillLearnBaseMoney.class);
			if(Util.isEmpty(learnBaseMoneyMap)){
				this.checkFail(info + ",learnBaseMoneyMap is null");
				return;
			}
			
			fileName = XlsSheetNameType.skill_reform_learn_base_potential.getXlsName();
			sheetName = XlsSheetNameType.skill_reform_learn_base_potential.getSheetName();
			sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			Map<Short,SkillLearnBasePotential> learnBasePotentialMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, SkillLearnBasePotential.class);
			if(Util.isEmpty(learnBasePotentialMap)){
				this.checkFail(info + ",learnBasePotentialMap is null");
				return;
			}
			
			fileName = XlsSheetNameType.skill_reform_learn_consume.getXlsName();
			sheetName = XlsSheetNameType.skill_reform_learn_consume.getSheetName();
			sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<SkillLearnConsume> learnConsumeList = XlsPojoUtil.sheetToList(sourceFile, sheetName, SkillLearnConsume.class);
			if(Util.isEmpty(learnConsumeList)){
				this.checkFail(info + ",learnConsumeList is null");
				return;
			}
			Map<Integer,Map<Byte,Integer>> consumeMap = Maps.newHashMap();
			for(SkillLearnConsume consume : learnConsumeList){
				if(null == consume){
					continue;
				}
				Map<Byte,Integer> map = null;
				if(consumeMap.containsKey(consume.getSkillLevel())){
					map = consumeMap.get(consume.getSkillLevel());
					map.put(consume.getAttrType(), consume.getC());
				}else{
					map = Maps.newHashMap();
					map.put(consume.getAttrType(), consume.getC());
					consumeMap.put(consume.getSkillLevel(), map);
				}
			}
			
			SkillLearnConfig config = null;
			if(Util.isEmpty(learnBaseMoneyMap)){
				return;
			}
			for(Entry<Short,SkillLearnBaseMoney> learnBase : learnBaseMoneyMap.entrySet()){
				if(null == learnBase){
					continue;
				}
				SkillBase base = skillBaseMap.get(learnBase.getKey());
				for(int i = 1;i <= base.getMaxLevel();i++){
					config = new SkillLearnConfig();
					config.setLevel(i);
					config.setSkillId(learnBase.getKey());
					Map<Byte,Integer> map = consumeMap.get(i);
					if(Util.isEmpty(map)){
						continue;
					}
					//游戏币
					int c = map.get(AttributeType.gameMoney.getType());
					SkillLearnBaseMoney bd = learnBaseMoneyMap.get(learnBase.getKey());
					int gameMoney = Util.getAbc(bd.getB(),c,bd.getD());
					config.setGameMoney(gameMoney);
					//潜能
					c = map.get(AttributeType.potential.getType());
					int potential = Util.getAbc(bd.getB(),c,bd.getD());
					config.setPotential(potential);
					//初始化消耗数据
					config.checkInit(info);
					Skill skill = this.getSkill(learnBase.getKey());
					if(null == skill){
						this.checkFail("initDefaultSkill error : ---skillId:" + config.getSkillId() + "---The skill does not exist!");
						continue;
					}
					skill.setCanLearnFromSystem(true);
					SkillDetail detail = skill.getSkillDetail(i);
					if(null == detail){
						continue;
					}
					detail.setConsumeAttributeList(config.getConsumeAttributeList());
					this.initAutoLearn(detail, skill);
				}
			}
		}
		
		@Override
		public Map<Short, short[]> getAllSkillMusicConfig() {
			return skillMusicMap;
		}
		
}
