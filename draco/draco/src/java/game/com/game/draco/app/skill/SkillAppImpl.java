package com.game.draco.app.skill;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.config.PathConfig;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.component.script.ScriptSupport;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.skill.config.SkillApplyType;
import com.game.draco.app.skill.config.SkillDetail;
import com.game.draco.app.skill.config.SkillHurtRemit;
import com.game.draco.app.skill.config.SkillLearnConfig;
import com.game.draco.app.skill.config.SkillSourceType;
import com.game.draco.app.skill.domain.RoleSkillStat;
import com.game.draco.app.skill.func.SkillLearnFunc;
import com.game.draco.app.skill.vo.Skill;
import com.game.draco.app.skill.vo.SkillAdaptor;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.item.SkillShowItem;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class SkillAppImpl extends SkillApp {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private PathConfig pathConfig;
	private ScriptSupport scriptSupport;
	private Map<Short,Map<Integer,SkillDetail>> skillDetailMap = Maps.newHashMap();
	private List<Short> learnSkillList = Lists.newArrayList();
	private Map<Integer, SkillHurtRemit> hurtRemitMap = Maps.newHashMap();
	/**
	 * 自动学习第1级的主动技能列表
	 * MAP:  key: roleLevel  value: skillId lists
	 */
	private Map<Integer,List<Short>> autoLearnActiveSkills = Maps.newHashMap();
	
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
		this.initSkillDetail();
		this.loadHurtRemit();
		scriptSupport.loadScript(pathConfig.getSkillPath());
		this.build();	
		this.skillDetailMap.clear();
	}
	
	@Override
	public void start() {
		this.load();
		this.verifyRoleCommonSkill();
	}
	
	private void verifyRoleCommonSkill(){
		try {
			short skillId = GameContext.getSkillConfig().getRoleCommonSkillId();
			Skill skill = this.getSkill(skillId);
			if(null == skill){
				this.checkFail("please check role common attack skill. skillId = " + skillId + ", skill is not exist.");
			}
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".verifyRoleCommonSkill error: please check the skillId of common attack.");
		}
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
			logger.error("reload buff error",ex);
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
	
	/**
	 * 载入技能配置表
	 */
	private void initSkillDetail() {
		String fileName = XlsSheetNameType.skill.getXlsName();
		String sheetName = XlsSheetNameType.skill.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		String info = "load excel error : fileName=" + fileName + ",sheetName=" + sheetName + ".";
		List<SkillDetail> skillDetailList = XlsPojoUtil.sheetToList(sourceFile, sheetName, SkillDetail.class);
		for(SkillDetail detail : skillDetailList){
			if(null == detail){
				continue;
			}
			short skillId = detail.getSkillId();
			if(skillId <= 0){
				this.checkFail(info + ", skillId = " + skillId + "The skillId is error!");
				continue ;
			}
			SkillSourceType skillSourceType = SkillSourceType.get(detail.getSourceType());
			if(null == skillSourceType){
				this.checkFail(info + ", skillId = " + skillId + "The sourceType is error.");
			}
			if (!skillDetailMap.containsKey(skillId)) {
				skillDetailMap.put(skillId, new HashMap<Integer, SkillDetail>());
			}
			skillDetailMap.get(skillId).put(detail.getLevel(), detail);
		}
	}
	
	private void checkFail(String info){
		Log4jManager.checkFail();
		Log4jManager.CHECK.error(info);
	}
	
	private void loadHurtRemit() {
		String fileName = XlsSheetNameType.hurtRemit.getXlsName();
		String sheetName = XlsSheetNameType.hurtRemit.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		List<SkillHurtRemit> hurtRemitList = XlsPojoUtil.sheetToList(sourceFile, sheetName, SkillHurtRemit.class);
		for(SkillHurtRemit hurtRemit : hurtRemitList){
			if(null == hurtRemit){
				continue;
			}
			int level = hurtRemit.getLevel();
			if(level <= 0){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("load excel error : fileName=" + fileName + ",sheetName=" + sheetName + ",has level=0!");
				continue ;
			}
			if (hurtRemitMap.containsKey(level)) {
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("load excel error : fileName=" + fileName + ",sheetName=" + sheetName + ",level=" + level + "more than one");
				continue ;
			}
			hurtRemitMap.put(level, hurtRemit);
		}
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
			Map<Integer,SkillDetail> details = this.skillDetailMap.remove(skillId);
			if(Util.isEmpty(details)){
				this.checkFail(info + "---The skill does not config levels and parameters!");
				continue ;
			}
			((SkillAdaptor)skill).putSkillDetail(details);
		}
		//xls中配置的技能没有技能脚本
		if(!this.skillDetailMap.isEmpty()){
			for(short id : this.skillDetailMap.keySet()){
				this.checkFail("initSkill error : ---skillId=" + id + "---The skill in the script does not exist!");
			}
		}
		this.buildSkillLearnDetail();
		//验证技能学习配置（SkillDetail是否都配置了学习条件）
		//策划确认不需要验证
		/*for(Skill skill : skillMap.values()){
			if(null == skill){
				continue;
			}
			for(int i=1;i<=skill.getMaxLevel();i++){
				SkillDetail detail = skill.getSkillDetail(i);
				if(detail.getRoleLevel() <= 0){
					this.checkFail("skillLearn config error : ---skillId=" + skill.getSkillId() + ",level=" + i + "---This level of skill does not config learn condition!");
				}
			}
		}*/
	}
	
	/**
	 * 加载技能学习/升级配置
	 */
	private void buildSkillLearnDetail(){
		String fileName = XlsSheetNameType.skill_role_learn.getXlsName();
		String sheetName = XlsSheetNameType.skill_role_learn.getSheetName();
		String info = "load excel error: fileName = " + fileName + ", sheetName = " + sheetName + ".";
		try {
			String xlsPath = GameContext.getPathConfig().getXlsPath();
			//加载角色可学习技能配置
			List<String> lcList = XlsPojoUtil.sheetToStringList(xlsPath + fileName, sheetName);
			for(String id : lcList){
				if(Util.isEmpty(id)){
					continue;
				}
				short skillId = Short.valueOf(id);
				Skill skill = this.getSkill(skillId);
				if(null == skill){
					this.checkFail(info + ", skill is not exist.");
				}
				this.learnSkillList.add(skillId);
			}
			//加载技能学习配置
			fileName = XlsSheetNameType.skill_Learn.getXlsName();
			sheetName = XlsSheetNameType.skill_Learn.getSheetName();
			info = "load excel error: fileName = " + fileName + ", sheetName = " + sheetName + ".";
			List<SkillLearnConfig> learnList = XlsPojoUtil.sheetToList(xlsPath + fileName, sheetName, SkillLearnConfig.class);
			for(SkillLearnConfig config : learnList){
				if(null == config){
					continue;
				}
				config.checkInit(info);//验证并初始化配置
				Skill skill = this.getSkill(config.getSkillId());
				if(null == skill){
					this.checkFail("initDefaultSkill error : ---skillId:" + config.getSkillId() + "---The skill does not exist!");
					continue;
				}
				skill.setCanLearnFromSystem(true);
				SkillDetail detail = skill.getSkillDetail(config.getLevel());
				if(null == detail){
					continue;
				}
				
				detail.setRoleLevel(config.getRoleLevel());
				detail.setInnerLevel(config.getInnerLevel());
				detail.setConsumeGoodsId(config.getGoodsId());
				detail.setConsumeGoodsNum(config.getGoodsNum());
				detail.setConsumeAttributeList(config.getConsumeAttributeList());
				detail.setRelyAttrType(config.getRelyAttributeType());
				detail.setRelyAttrValue(config.getRelyAttrValue());
				detail.setRelySkillId(config.getRelySkillId());
				detail.setRelySkillLevel(config.getRelySkillLevel());
				this.initAutoLearn(detail, skill);
			}
		} catch (Exception e) {
			this.checkFail(info);
		}
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
		int roleLevel = detail.getRoleLevel();
		List<Short> skills = this.autoLearnActiveSkills.get(roleLevel);
		if(null == skills){
			skills = Lists.newArrayList();
			this.autoLearnActiveSkills.put(roleLevel, skills);
		}
		skills.add(skill.getSkillId());
	}
	
	@Override
	public List<Short> getRoleLearnSkillList(){
		return this.learnSkillList;
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
	public SkillHurtRemit getSkillHurtRemit(int roleLevel) {
		return this.hurtRemitMap.get(roleLevel);
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
		item.setSkillIco(skill.getIconId());
		item.setSkillType(skill.getSkillApplyType().getType());
		int currLevel = learnFunc.getSkillLevel(role, skillId, parameter);
		item.setMaxLevel((byte)skill.getMaxLevel());
		item.setCurrLevel((byte)currLevel);
		if(currLevel < skill.getMaxLevel()){
			SkillDetail nextDetail = skill.getSkillDetail(currLevel + 1);
			item.setRoleLevel((byte)nextDetail.getRoleLevel());
			short relySkillId = nextDetail.getRelySkillId();
			item.setRelySkillId(relySkillId);
			item.setRelySkillLevel(nextDetail.getRelySkillLevel());
			Skill relySkill = GameContext.getSkillApp().getSkill(relySkillId);
			if(null != relySkill){
				item.setRelySkillName(relySkill.getName());
			}
			GoodsLiteNamedItem goodsLiteNamedItem = nextDetail.getConsumeGoodsLiteNamedItem();
			if(null != goodsLiteNamedItem){
				item.setGoodsLiteNamedItem(goodsLiteNamedItem);
			}
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
	
}
