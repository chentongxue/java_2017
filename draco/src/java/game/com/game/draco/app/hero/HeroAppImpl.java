package com.game.draco.app.hero;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.game.draco.message.item.*;
import com.game.draco.message.response.*;
import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.HeroEquipBackpack;
import sacred.alliance.magic.app.goods.RoleGoodsHelper;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.app.goods.behavior.result.UseResult;
import sacred.alliance.magic.app.map.MapProperty;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.QualityType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.StateType;
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.ParasConstant;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsHero;
import sacred.alliance.magic.domain.GoodsHeroAid;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.MapHeroOnBattleEvent;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.app.hero.config.AttributeHeroLevelRate;
import com.game.draco.app.hero.config.AttributeHeroRate;
import com.game.draco.app.hero.config.AttributeQualityRate;
import com.game.draco.app.hero.config.AttributeTypeRate;
import com.game.draco.app.hero.config.HeroAttribute;
import com.game.draco.app.hero.config.HeroBaseConfig;
import com.game.draco.app.hero.config.HeroLevelup;
import com.game.draco.app.hero.config.HeroLove;
import com.game.draco.app.hero.config.HeroLoveAttribute;
import com.game.draco.app.hero.config.HeroNumRoleLevel;
import com.game.draco.app.hero.config.HeroQualityUpgrade;
import com.game.draco.app.hero.domain.HeroEquip;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.app.hero.domain.RoleHeroStatus;
import com.game.draco.app.hero.vo.HeroQualityUpgradeResult;
import com.game.draco.app.hero.vo.HeroSwallowResult;
import com.game.draco.app.hero.vo.HeroSwitchResult;
import com.game.draco.app.hint.HintAppImpl;
import com.game.draco.app.skill.config.SkillDetail;
import com.game.draco.app.skill.domain.RoleSkillStat;
import com.game.draco.app.skill.vo.Skill;
import com.game.draco.app.target.cond.TargetCondType;
import com.game.draco.base.QualityStarSupport;
import com.game.draco.message.push.C1273_HeroOnBattleNotifyMessage;
import com.game.draco.message.request.C1251_HeroGoodsToShadowReqMessage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class HeroAppImpl implements HeroApp {

	private final byte ON_BATTLE = 1 ;
	private final byte SUCCESS_NOT_INCR_LEVEL = (byte)1 ;
	private final byte SUCCESS_AND_INCR_LEVEL = (byte)2 ;
	private final short HERO_GOODS_TO_SHADOW_CMDID = new C1251_HeroGoodsToShadowReqMessage().getCommandId();
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Getter
	@Setter
	private List<AttributeType> attributeTypeList = Lists.newArrayList(
			AttributeType.atk,	AttributeType.maxHP,
			AttributeType.rit,	AttributeType.breakDefense,
			AttributeType.critAtk, AttributeType.critRit,
			AttributeType.dodge,AttributeType.hit);
	
	@Getter @Setter private HeroBaseConfig heroBaseConfig ;
	@Getter @Setter private AttributeTypeRate attributeTypeRate ;
	@Getter @Setter private Map<String,HeroLevelup> heroLevelupMap = null ;
	@Getter @Setter private Map<String,Integer> maxLevelMap = null ;
	@Getter @Setter private List<Integer> heroIdentifyList = null ;
	@Getter @Setter private Map<String,HeroLove> heroLoveMap = null ;
	@Getter @Setter private Map<String,List<HeroLove>> heroLoveListMap = null ;
	@Getter @Setter private Map<String,AttributeHeroRate> attributeHeroRateMap = null ;
	@Getter @Setter private Map<String,AttributeQualityRate> attributeQualityRateMap = null ;
	@Getter @Setter private Map<String,AttributeQualityRate> attributeBornRateMap = null ;
	@Getter @Setter private Map<String,AttributeHeroLevelRate> attributeHeroLevelRateMap = null ;
	@Getter @Setter private Map<String,HeroQualityUpgrade> heroQualityUpgradeMap = null ;
	@Getter @Setter private Map<Integer,HeroNumRoleLevel> heroNumRoleLevelMap = null ;
	/**
	 * 每品质最大的星
	 */
	private Map<String,Byte> qualityMaxStarMap = Maps.newHashMap() ;
	@Getter @Setter private Map<String,HeroLoveAttribute> heroLoveAttributeMap = null ;
	//上阵英雄的开启等级
	@Getter @Setter private byte[] switchOpenLevel ;
	//助威英雄的开启等级
	@Getter @Setter private byte[] helpOpenLevel ;

	@Override
	public byte getMaxStar(int quality){
		Byte value = this.qualityMaxStarMap.get(String.valueOf(quality));
		return (null == value)?0:value ;
	}
	
	@Override
	public HeroQualityUpgrade getHeroQualityUpgrade(int quality,int star){
		String key = quality + "_" + star ;
		return this.fromMap(heroQualityUpgradeMap, key);
	}
	
	private AttributeQualityRate getAttributeQualityRate(int quality,int star){
		String key = quality + "_" + star ;
		return this.fromMap(attributeQualityRateMap, key);
	}
	
	private AttributeQualityRate getAttributeBornRate(int quality,int star){
		String key = quality + "_" + star ;
		return this.fromMap(attributeBornRateMap, key);
	}
	
	private AttributeHeroLevelRate getAttributeHeroLevelRate(int heroLevel){
		String key = String.valueOf(heroLevel) ;
		return this.fromMap(this.attributeHeroLevelRateMap, key);
	}
	
	private AttributeHeroRate getAttributeHeroRate(int heroId){
		String key = String.valueOf(heroId) ;
		return this.fromMap(this.attributeHeroRateMap, key);
	}
	
	private <T> T fromMap(Map<String,T> map,String key){
		if(null == map){
			return null ;
		}
		return map.get(key);
	}
	
	public int getMaxLevel(int heroQuality){
		String key = String.valueOf(heroQuality) ;
		return this.fromMap(this.maxLevelMap, key);
	}
	
	@Override
	public HeroBaseConfig getHeroBaseConfig() {
		return this.heroBaseConfig;
	}


	@Override
	public HeroLevelup getHeroLevelup(int heroQuality, int level) {
		String key = heroQuality + Cat.underline + level ;
		return this.fromMap(this.heroLevelupMap, key);
	}


	@Override
	public void syncBattleScore(RoleInstance role,int heroId,boolean notify){
		if(null == role){
			return ;
		}
		RoleHero roleHero = GameContext.getUserHeroApp().getRoleHero(role.getRoleId(), heroId);
		if(null == roleHero){
			return ;
		}
		int score = this.getBattleScore(roleHero);
		roleHero.setScore(score);

        C1260_HeroBattleScoreNotifyMessage notifyMsg = new C1260_HeroBattleScoreNotifyMessage();
        notifyMsg.setHeroId(heroId);
        notifyMsg.setBattleScore(score);

        role.getBehavior().sendMessage(notifyMsg);
		if(notify){
			//同步战斗力
			role.syncBattleScore();
			role.getBehavior().notifyAttribute();
		}
	}
	
	@Override
	public int getBattleScore(RoleHero roleHero){
		boolean isOnline = GameContext.getOnlineCenter().isOnlineByRoleId(roleHero.getRoleId());
		if(!isOnline){
			return roleHero.getScore() ;
		}
		int battleScore =  GameContext.getAttriApp().getAttriBattleScore(
				this.getHeroAttriBuffer(roleHero));
		//技能
		battleScore += GameContext.getSkillApp().getSkillBattleScore(roleHero.getSkillMap());
		roleHero.setScore(battleScore);
		return battleScore ;
	}
	
	@Override
	public int getNotOnBattleScore(String roleId){
		int battleScore = 0 ;
		RoleHeroStatus status = GameContext.getUserHeroApp().getRoleHeroStatus(
				roleId);
		for (int heroId : status.getSwitchHeroSet()) {
			RoleHero rh = GameContext.getUserHeroApp().getRoleHero(roleId,
					heroId);
			if (null == rh || heroId == status.getBattleHeroId() ) {
				continue;
			}
			battleScore += rh.getScore() ;
		}
		return battleScore ;
	
	}
	
	@Override
	public void saveRoleHero(RoleHero roleHero){
		//!!!
		this.preToStore(roleHero);
		GameContext.getBaseDAO().update(roleHero);
		//标识未未修改
		roleHero.setModify(false);
	}
	
	private void heroLevelupEffect(RoleInstance role,int oldLevel,RoleHero roleHero){
		try {
			if (oldLevel == roleHero.getLevel()) {
				// 等级未变化
				return;
			}
			RoleHeroStatus status = GameContext.getUserHeroApp().getRoleHeroStatus(role.getRoleId());
			int heroId = roleHero.getHeroId() ;
			if(!status.getSwitchHeroSet().contains(heroId)){
				return ;
			}
			//未出战
			boolean isOnBattle = (ON_BATTLE == roleHero.getOnBattle()) ;
			if(isOnBattle){
				AttriBuffer buffer = this.getHeroAttriBuffer(roleHero).append(
						this.getHeroGivenAttriBuffer(roleHero, oldLevel,roleHero.getQuality(),roleHero.getStar()).reverse());
				GameContext.getUserAttributeApp().changeAttribute(role, buffer);
				//给角色满hp
				role.setCurHP(role.getMaxHP());
				role.getBehavior().notifyAttribute();
			}
			GameContext.getHeroApp().syncBattleScore(role, heroId,!isOnBattle);
		}catch(Exception ex){
			logger.error("",ex);
		}
	}
	
	private void loadAttributeTypeRate(){
		String fileName = XlsSheetNameType.attribute_type_rate.getXlsName();
		String sheetName = XlsSheetNameType.attribute_type_rate.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		attributeTypeRate = XlsPojoUtil.getEntity(sourceFile, sheetName, AttributeTypeRate.class);
		if(null == attributeTypeRate){
			Log4jManager.CHECK.error("not config the attributeTypeRate,file=" + sourceFile + " sheet=" + sheetName);
			Log4jManager.checkFail();
			return ;
		}
		attributeTypeRate.init();
	}
	
	

	/**
	 * 加载基本配置
	 */
	private void loadHeroBaseConfig(){
		String fileName = XlsSheetNameType.hero_base_config.getXlsName();
		String sheetName = XlsSheetNameType.hero_base_config.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		heroBaseConfig = XlsPojoUtil.getEntity(sourceFile, sheetName, HeroBaseConfig.class);
		if(null == heroBaseConfig){
			Log4jManager.CHECK.error("not config the heroBaseConfig,file=" + sourceFile + " sheet=" + sheetName);
			Log4jManager.checkFail();
		}
	}
	
	private List<Integer> loadHeroIdList(XlsSheetNameType xls){
		List<Integer> goodsList = Lists.newArrayList() ;
		String sourceFile = GameContext.getPathConfig().getXlsPath() + xls.getXlsName();
		List<String> list = XlsPojoUtil.sheetToStringList(sourceFile, xls.getSheetName()) ;
		if(null == list){
			Log4jManager.CHECK.error("hero id confg error ,file=" + sourceFile + " sheet=" + xls.getSheetName());
			Log4jManager.checkFail();
			return goodsList;
		}
		for(String str : list){
			if(Util.isEmpty(str)){
				continue ;
			}
			if(!Util.isNumber(str)){
				Log4jManager.CHECK.error("hero id confg error ,goodsId=" + str 
						+ " not number ,file=" + sourceFile + " sheet=" + xls.getSheetName());
				Log4jManager.checkFail();
				continue ;
			}
			int goodsId = Integer.parseInt(str);
			GoodsHero hero = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class, goodsId);
			if(null == hero){
				Log4jManager.CHECK.error("hero id confg error ,goodsId=" + goodsId 
						+ " not goodshero,file=" + sourceFile + " sheet=" + xls.getSheetName());
				Log4jManager.checkFail();
				continue ;
			}
			goodsList.add(goodsId);
		}
		return goodsList ;
	}
	
	private void loadHeroIdentify(){
		this.heroIdentifyList = this.loadHeroIdList(XlsSheetNameType.hero_identify) ;
	}
	
	
	private void loadHeroLoveAttribute(){
		String fileName = XlsSheetNameType.hero_love_attribute.getXlsName();
		String sheetName = XlsSheetNameType.hero_love_attribute.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		Map<String,HeroLoveAttribute> map = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, HeroLoveAttribute.class);
		if(Util.isEmpty(map)){
			Log4jManager.CHECK.error("not config the hero_love_attribute,file="
					+ sourceFile + " sheet=" + sheetName);
			Log4jManager.checkFail();
			return;
		}
		for(HeroLoveAttribute config : map.values()){
			config.init(); 
			HeroLove love = this.getHeroLoveById(config.getLoveId());
			if(null == love){
				Log4jManager.CHECK.error("not config the herolove in attribute,loveId=" + config.getLoveId());
				Log4jManager.checkFail();
				continue ;
			}
			//拼接描述
			config.setDesc(love.getDesc() + " " + config.getDesc());
			//设置love的最小品星
			love.resetMinQualityStar(config.getQuality(), config.getStar());
		}
		this.heroLoveAttributeMap = map ;
	}
	
	private void loadHeroLove(){
		String fileName = XlsSheetNameType.hero_love.getXlsName();
		String sheetName = XlsSheetNameType.hero_love.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		Map<String,HeroLove> map = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, HeroLove.class);
		if(null != map){
			for(HeroLove love : map.values()){
				love.init(); 
			}
		}
		//构建每个英雄的情缘列表
		this.heroLoveMap = map ;
		Map<String,List<HeroLove>> heroLoveListMap = Maps.newHashMap();
		for(HeroLove config : map.values()){
			String heroId = String.valueOf(config.getHeroId());
			List<HeroLove> list = heroLoveListMap.get(heroId);
			if(null == list){
				list = Lists.newArrayList();
				heroLoveListMap.put(heroId, list);
			}
			list.add(config);
		}
		this.heroLoveListMap = heroLoveListMap ;
	}
	
	private void loadHeroNumRoleLevel() {
		String fileName = XlsSheetNameType.hero_rolelevel_hero_num.getXlsName();
		String sheetName = XlsSheetNameType.hero_rolelevel_hero_num
				.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		this.heroNumRoleLevelMap = XlsPojoUtil.sheetToGenericMap(sourceFile,
				sheetName, HeroNumRoleLevel.class);
		if (Util.isEmpty(heroNumRoleLevelMap)) {
			Log4jManager.CHECK.error("not config the heroNumRoleLevelMap,file="
					+ sourceFile + " sheet=" + sheetName);
			Log4jManager.checkFail();
			return;
		}
		// 判断是否都配置
		Map<Byte,Integer> switchMinMap = Maps.newHashMap() ;
		Map<Byte,Integer> helpMinMap = Maps.newHashMap() ;
		for (int i = 1; i <= GameContext.getAreaServerNotifyApp().getMaxLevel(); i++) {
			HeroNumRoleLevel config = heroNumRoleLevelMap.get(i);
			if (null == config) {
				Log4jManager.CHECK
						.error("config heroNumRoleLevelMap error,file="
								+ sourceFile + " sheet=" + sheetName
								+ " roleLevel=" + i);
				Log4jManager.checkFail();
				continue ;
			}
			//构建各英雄孔位开启
			this.min(switchMinMap, config.getHeroNum(), i);
			this.min(helpMinMap, config.getHelpHeroNum(), i);
		}
		this.switchOpenLevel = this.mapToArr(switchMinMap);
		this.helpOpenLevel = this.mapToArr(helpMinMap) ;
	}
	
	private byte[] mapToArr(Map<Byte,Integer> map){
		//此处map key是连续的
		if(Util.isEmpty(map)){
			return null ;
		}
		byte[] ret = new byte[map.size()];
		//必须是 <= map.size()
		int index = 0 ;
		for(int i=0;i <= map.size(); i++){
			Integer value = map.get((byte)i);
			if(null == value || value < 0 ){
				continue ;
			}
			ret[index++] = value.byteValue() ;
		}
		return ret ;
	}
	
	private void min(Map<Byte,Integer> map,byte num ,int level){
		if(0 == num){
			return ;
		}
		if(!map.containsKey(num)){
			map.put(num, level);
		}
		int oldLevel = map.get(num);
		if(oldLevel <= level){
			return ;
		}
		map.put(num, level);
	}
	
	private void loadHeroQualityUpgrade(){
		String fileName = XlsSheetNameType.hero_quality_upgrade.getXlsName();
		String sheetName = XlsSheetNameType.hero_quality_upgrade.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		this.heroQualityUpgradeMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, HeroQualityUpgrade.class);
		if(Util.isEmpty(heroQualityUpgradeMap)){
			Log4jManager.CHECK.error("not config the heroQualityUpgradeMap,file=" + sourceFile + " sheet=" + sheetName);
			Log4jManager.checkFail();
			return ;
		}
		List<HeroQualityUpgrade> list = Lists.newArrayList();
		list.addAll(this.heroQualityUpgradeMap.values());
		//排序
		Comparator<HeroQualityUpgrade> comparator = new Comparator<HeroQualityUpgrade>(){
			@Override
			public int compare(HeroQualityUpgrade r1, HeroQualityUpgrade r2) {
				if(r1.getQuality() < r2.getQuality()){
					return 1 ;
				}
				if(r1.getQuality() > r2.getQuality()){
					return -1 ;
				}
				if(r1.getStar() < r2.getStar()){
					return 1 ;
				}
				if(r1.getStar() > r2.getStar()){
					return -1 ;
				}
				return 0 ;
			}
		};
		Collections.sort(list, comparator);
		for(int i= list.size()-1;i>0;i--){
			HeroQualityUpgrade curr = list.get(i);
			curr.setNextConf(list.get(i-1));
			this.initQualityMaxStar(curr.getQuality(), curr.getStar());
		}
		//第一行记录
		this.initQualityMaxStar(list.get(0).getQuality(), list.get(0).getStar());
		list.clear();
		list = null ;
	}
	
	private void initQualityMaxStar(byte quality,byte star){
		byte value = this.getMaxStar(quality);
		if(value > star){
			return ;
		}
		this.qualityMaxStarMap.put(String.valueOf(quality), star);
	}
	
	private void loadAttributeHeroRate(){
		String fileName = XlsSheetNameType.attribute_hero_rate.getXlsName();
		String sheetName = XlsSheetNameType.attribute_hero_rate.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		attributeHeroRateMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, AttributeHeroRate.class);
		if(Util.isEmpty(attributeHeroRateMap)){
			Log4jManager.CHECK.error("not config the attributeHeroRateMap,file=" + sourceFile + " sheet=" + sheetName);
			Log4jManager.checkFail();
			return ;
		}
		for(AttributeHeroRate rate : attributeHeroRateMap.values()){
			rate.init();
		}
	}
	
	
	private void loadAttributeBornRate(){
		String fileName = XlsSheetNameType.attribute_born_rate.getXlsName();
		String sheetName = XlsSheetNameType.attribute_born_rate.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		this.attributeBornRateMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, AttributeQualityRate.class);
		if(Util.isEmpty(attributeBornRateMap)){
			Log4jManager.CHECK.error("not config the loadAttributeBornRate,file=" + sourceFile + " sheet=" + sheetName);
			Log4jManager.checkFail();
			return ;
		}
		for(AttributeQualityRate rate : attributeBornRateMap.values()){
			rate.init();
		}
	}
	
	private void loadAttributeQualityRate(){
		String fileName = XlsSheetNameType.attribute_quality_rate.getXlsName();
		String sheetName = XlsSheetNameType.attribute_quality_rate.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		this.attributeQualityRateMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, AttributeQualityRate.class);
		if(Util.isEmpty(attributeQualityRateMap)){
			Log4jManager.CHECK.error("not config the attributeQualityRateMap,file=" + sourceFile + " sheet=" + sheetName);
			Log4jManager.checkFail();
			return ;
		}
		for(AttributeQualityRate rate : attributeQualityRateMap.values()){
			rate.init();
		}
	}
	
	private void loadAttributeHeroLevelRate(){
		String fileName = XlsSheetNameType.attribute_herolevel_rate.getXlsName();
		String sheetName = XlsSheetNameType.attribute_herolevel_rate.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		this.attributeHeroLevelRateMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, AttributeHeroLevelRate.class);
		if(Util.isEmpty(attributeHeroLevelRateMap)){
			Log4jManager.CHECK.error("not config the attributeHeroLevelRateMap,file=" + sourceFile + " sheet=" + sheetName);
			Log4jManager.checkFail();
			return ;
		}
		for(AttributeHeroLevelRate rate : attributeHeroLevelRateMap.values()){
			rate.init();
		}
	}
	
	
	private void loadHeroLevelup(){
		String fileName = XlsSheetNameType.hero_levelup.getXlsName();
		String sheetName = XlsSheetNameType.hero_levelup.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		List<HeroLevelup> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, HeroLevelup.class) ;
		if(Util.isEmpty(list)){
			Log4jManager.CHECK.error("not config the HeroLevelup,file=" + sourceFile + " sheet=" + sheetName);
			Log4jManager.checkFail();
			return ;
		}
		//按照品质,等级正序排序
		Collections.sort(list, new Comparator<HeroLevelup>(){
			@Override
			public int compare(HeroLevelup l1, HeroLevelup l2) {
				if(l1.getHeroQuality() > l2.getHeroQuality()){
					return 1 ;
				}
				if(l1.getHeroQuality() < l2.getHeroQuality()){
					return -1 ;
				}
				if(l1.getLevel() > l2.getLevel()){
					return 1 ;
				}
				if(l1.getLevel() < l2.getLevel()){
					return -1 ;
				}
				return 0 ;
			}
			
		});
		Map<String,HeroLevelup> levelupMap = Maps.newHashMap() ;
		Map<String,Integer> maxLvMap = Maps.newHashMap() ;
		int preQuality = list.get(0).getHeroQuality() ;
		int totalExp = 0 ;
		for(HeroLevelup levelup : list){
			int thisQuality = levelup.getHeroQuality() ;
			if(thisQuality != preQuality){
				totalExp = 0 ;
			}
			levelup.setReachTotalExp(totalExp);
			totalExp += levelup.getMaxExp();
			levelupMap.put(levelup.getKey(), levelup) ;
			maxLvMap.put(String.valueOf(levelup.getHeroQuality()), levelup.getLevel());
		}
		if(levelupMap.size() != list.size()){
			Log4jManager.CHECK.error("HeroLevelup config error,file=" + sourceFile + " sheet=" + sheetName);
			Log4jManager.checkFail();
			return ;
		}
		this.heroLevelupMap = levelupMap ;
		this.maxLevelMap = maxLvMap ;
	}
	
	
	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void start() {
		this.loadHeroBaseConfig();
		this.loadAttributeHeroRate();
		this.loadAttributeTypeRate();
		this.loadAttributeQualityRate();
		this.loadAttributeHeroLevelRate();
		this.loadAttributeBornRate();
		this.loadHeroLevelup();
		this.loadHeroIdentify();
		//先加载HeroLove后加载HeroLoveAttribute
		this.loadHeroLove();
		this.loadHeroLoveAttribute();
		this.loadHeroQualityUpgrade();
		this.loadHeroNumRoleLevel();
	}

	@Override
	public void stop() {
		
	}

	@Override
	public int getSwallowExp(RoleHero roleHero) {
		HeroLevelup lu = this.getHeroLevelup(roleHero.getQuality(), roleHero.getLevel());
		int total = (lu.getReachTotalExp() + roleHero.getExp() );
		//模板上能获得的基本经验
		GoodsHero hero = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class, roleHero.getHeroId());
		if(null != hero){
			total += hero.getSwallowExp() ;
		}
		return total ;
	}
	
	
	private void resetRoleHeroBaseInfo(RoleInstance role,GoodsHero goodsHero){
		role.setClothesResId(goodsHero.getResId());
		role.setHeroHeadId(goodsHero.getHeadId());
		role.setHeroGearId(goodsHero.getGearId());
		role.setHeroSeriesId(goodsHero.getSeriesId());
	}
	
	private Result onBattle(RoleInstance role, RoleHero hero,RoleHero oldOnHero,boolean userUse) {
		if(null != oldOnHero){
			float f = role.getCurHP()/(float)role.getMaxHP() ;
			oldOnHero.setHpRate((short)(f*RoleHero.HP_RATE_FULL)) ;
		}
		//处理buff
		GameContext.getUserBuffApp().delBuffOnSwitchHero(role);
		
		Result result = new Result();
		AttriBuffer oldHeroBuffer = null;
		if (null != oldOnHero) {
			// 删除此英雄技能
			for (Short skillId : oldOnHero.getSkillMap().keySet()) {
				role.delSkillStat(skillId);
			}
			oldHeroBuffer = this.getHeroAttriBuffer(oldOnHero);
		}
		// 添加新英雄的技能
		role.getSkillMap().putAll(hero.getSkillMap());
		//表示
		GameContext.getUserHeroApp()
				.setOnBattleRoleHero(role.getRoleId(), hero,this.getMaxSwitchableHeroNum(role));
		
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(hero.getHeroId());
		String name = (null == gb) ? "" : gb.getName();
		result.setInfo(GameContext.getI18n().messageFormat(
				TextId.Hero_on_battle_success, name));
	
		C1273_HeroOnBattleNotifyMessage notifyMsg = null ;
		if (null != gb) {
			GoodsHero goodsHero  = (GoodsHero) gb;
			//设置到角色对象上
			this.resetRoleHeroBaseInfo(role, goodsHero);
			
			notifyMsg = new C1273_HeroOnBattleNotifyMessage();
			notifyMsg.setRoleId(role.getIntRoleId());
			notifyMsg.setResId((short)goodsHero.getResId());
			notifyMsg.setResRate(goodsHero.getResRate());
			notifyMsg.setHeroHeadId(goodsHero.getHeadId());
			notifyMsg.setGearId(goodsHero.getGearId());
			notifyMsg.setSeriesId(goodsHero.getSeriesId());
		}
		result.success();
		try {
			if(userUse){
				//设置CD
				this.setOnBattleCd(role.getRoleId());
			}
			//通知客户端ui变化
			GameContext.getMessageCenter().sendSysMsg(role, this.getHeroMainUiMessage(role));
			// 计算属性差异
			AttriBuffer nowHeroBuffer = this.getHeroAttriBuffer(hero);
			if (null != oldHeroBuffer) {
				nowHeroBuffer.append(oldHeroBuffer.reverse());
			}
			GameContext.getUserAttributeApp().changeAttribute(role,
					nowHeroBuffer);
			//修改切换后血百分比
			float hpRate = hero.getHpRate()/(float)RoleHero.HP_RATE_FULL;
			int hp = (int)(role.getMaxHP()*hpRate) ;
			role.setCurHP(hp);
			role.getBehavior().notifyAttribute();
			//给同地图内其他玩家广播变化
			MapInstance map = role.getMapInstance();
			if(null != notifyMsg && null != map){
				map.broadcastMap(role, notifyMsg);
			}
			MapInstance mapInstance = role.getMapInstance();
			if(null != mapInstance){
				mapInstance.doEvent(role, 
						new MapHeroOnBattleEvent(hero,oldOnHero));
			}
		} catch (Exception ex) {
			logger.error("", ex);
		}
		return result;
	}
	
	@Override
	public Result systemAutoOnBattle(RoleInstance role,int heroId){
		return this.onBattle(role, heroId, false);
	}
	
	@Override
	public Result onBattle(RoleInstance role, int heroId){
		return this.onBattle(role, heroId, true);
	}

	
	private Result onBattle(RoleInstance role, int heroId,boolean userUse) {
		Result result = new Result();
		RoleHero hero = GameContext.getUserHeroApp().getRoleHero(
				role.getRoleId(), heroId);
		if (null == hero) {
			// 提示参数错误
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result;
		}
		//判断是否死亡
		if(userUse && role.isDeath()){
			result.setInfo(GameContext.getI18n().getText(TextId.DO_THIS_THING_WHEN_ALIVE));
			return result;
		}
		// 判断cd
		if (userUse && this.getOnBattleCd(role) > 0) {
			result.setInfo(GameContext.getI18n().getText(
					TextId.Hero_On_Battle_Cd_Limit));
			return result;
		}
		//判断当前地图是否允许
		if (userUse
				&& !GameContext.getMapApp().canMapProperty(role,
						MapProperty.canSwitchHero.getType())) {
			result.setInfo(GameContext.getI18n().getText(
					TextId.Hero_On_Battle_Cur_Map_Canot));
			return result;
		}
		//用户使用判断状态
		if(userUse){
			if(role.inState(StateType.mum) 
					|| role.inState(StateType.coma)){
				//当前状态不允许
				result.setInfo(GameContext.getI18n().getText(
						TextId.Hero_On_Battle_Canot_At_Non_health));
				return result;
			}
		}
		RoleHero oldOnHero = GameContext.getUserHeroApp().getOnBattleRoleHero(
				role.getRoleId());
		// 判断当前英雄是否已经出战
		if (null != oldOnHero && hero.getHeroId() == oldOnHero.getHeroId()) {
			// 提示已经出战
			result.setInfo(GameContext.getI18n().getText(
					TextId.Hero_On_Battle_Now));
			return result;
		}
		return this.onBattle(role, hero, oldOnHero,userUse);
	}
	
	
	private String skillIdLevelString(Map<Short,RoleSkillStat> map,RoleHero hero){
		if(Util.isEmpty(map)){
			return "" ;
		}
		GoodsHero goodsHero = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class, hero.getHeroId());
		String cat = "" ;
		StringBuffer buffer = new StringBuffer();
		for(Iterator<Map.Entry<Short, RoleSkillStat>> it = map.entrySet().iterator();it.hasNext();){
			Map.Entry<Short, RoleSkillStat> entry = it.next() ;
			if(null != goodsHero && !goodsHero.isHeroSkill(entry.getKey())){
				continue ;
			}
			buffer.append(cat);
			buffer.append(entry.getKey());
			buffer.append(":");
			buffer.append(entry.getValue().getSkillLevel());
			cat = "," ;
		}
		return buffer.toString() ;
	}
	
	private long getLastProcessTimeFromStore(RoleSkillStat stat){
		return GameContext.getUserSkillApp().getLastProcessTimeFromCache(stat.getRoleId(), 
				stat.getSkillId());
	}
	
	private void postFromStore(RoleHero hero){
		//解析技能MAP
		Map<Short,Integer> map = Util.parseShortIntMap(hero.getSkills());
		if(!Util.isEmpty(map)){
			Map<Short,RoleSkillStat> skillMap = Maps.newHashMap() ;
			for(Iterator<Map.Entry<Short, Integer>> it = map.entrySet().iterator();it.hasNext();){
				Map.Entry<Short, Integer> entry = it.next() ;
				RoleSkillStat stat = new RoleSkillStat() ;
				stat.setSkillId(entry.getKey());
				stat.setSkillLevel(entry.getValue());
				stat.setRoleId("HERO_" + hero.getHeroId());
				stat.setLastProcessTime(this.getLastProcessTimeFromStore(stat));
				skillMap.put(stat.getSkillId(), stat) ;
			}
			hero.setSkillMap(skillMap);
		}
		this.initSkill(hero);
	}
	
	@Override
	public void initSkill(RoleHero hero) {
		// 普通攻击
		GoodsHero template = GameContext.getGoodsApp().getGoodsTemplate(
				GoodsHero.class, hero.getHeroId()) ;
		if(null == template){
			logger.error("hero template not exist,heroId=" + hero.getHeroId());
			return  ;
		}
		this.initSkill(hero, template.getCommonSkill());
		for(short skillId : template.getSkillIdList() ){
			this.initSkill(hero, skillId);
		}
	}
	
	private void initSkill(RoleHero hero,short skillId){
		RoleSkillStat stat = hero.getSkillMap().get(skillId);
		if(null != stat){
			return ;
		}
		stat = new RoleSkillStat(); 
		stat.setSkillId(skillId);
		stat.setSkillLevel(1);
		stat.setRoleId("HERO_" + hero.getHeroId());
		stat.setLastProcessTime(0);
		hero.getSkillMap().put(skillId, stat);
	}
	
	@Override
	public void preToStore(RoleHero hero){
		//存储技能MAP
		hero.setSkills(this.skillIdLevelString(hero.getSkillMap(),hero));
	}
	
	@Override
	public int onLogin(RoleInstance role, Object context) {
		//加载英雄
		List<RoleHero> heros = GameContext.getBaseDAO().selectList(RoleHero.class, 
				RoleHero.ROLE_ID, role.getRoleId());
		for(RoleHero hero : heros){
			this.postFromStore(hero);
			hero.setModify(false);
		}
		//获得状态
		RoleHeroStatus stauts = GameContext.getBaseDAO().selectEntity(RoleHeroStatus.class, 
				RoleHero.ROLE_ID, role.getRoleId()) ;
		this.heroListLoginAction(role, heros,stauts);
		//获得英雄的装备
		Map<String,List<RoleGoods>> equipMap = this.buildHeroEquipMap(role.getRoleId());
		for (RoleHero hero : heros) {
			this.initHeroEquipBackpack(role, hero.getHeroId(),
					equipMap.get(String.valueOf(hero.getHeroId())));
		}
		RoleHero onBattleHero = GameContext.getUserHeroApp().getOnBattleRoleHero(role.getRoleId());
		if(null == onBattleHero){
			return 1 ;
		}
		GoodsHero goodsHero = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class, onBattleHero.getHeroId());
		if(null == goodsHero){
			return 1 ;
		}
		//初始外观
		this.resetRoleHeroBaseInfo(role, goodsHero);
		return 1;
	}
	
	private void initHeroEquipBackpack(RoleInstance role,int heroId,List<RoleGoods> list){
		HeroEquipBackpack equippack = new HeroEquipBackpack(role,
				ParasConstant.HERO_EQUIP_MAX_NUM,heroId);
		equippack.initGoods(list);
		GameContext.getUserHeroApp().initHeroEquipBackpack(
				role.getRoleId(), equippack);
	}
	@Override
	public Map<String,List<RoleGoods>> buildHeroEquipMap(String roleId){
		Map<String,List<RoleGoods>> value = Maps.newHashMap() ;
		List<RoleGoods> equipList = this.selectFromStorage(roleId);
		if(Util.isEmpty(equipList)){
			return value ;
		}
		for(RoleGoods rg : equipList){
			String heroId = rg.getOtherParm() ;
			List<RoleGoods> heroGoods = value.get(heroId);
			if(null == heroGoods){
				heroGoods = Lists.newArrayList();
				value.put(heroId, heroGoods);
			}
			heroGoods.add(rg);
		}
		return value ;
	}
	
	
	private List<RoleGoods> selectFromStorage(String roleId){
		 return GameContext.getBaseDAO().selectList(RoleGoods.class, RoleGoods.ROLE_ID,roleId, 
				 RoleGoods.STORAGE_TYPE,StorageType.hero.getType());
	}

	private void saveRoleHeroStatus(RoleHeroStatus status){
		if(null == status){
			return ;
		}
		status.preToStore();
		if(status.isInStore()){
			GameContext.getBaseDAO().update(status);
		}else{
			GameContext.getBaseDAO().insert(status);
			status.setInStore(true);
		}
	}
	
	private void heroListLoginAction(RoleInstance role,List<RoleHero> heros,RoleHeroStatus status){
		if(null != status){
			//已经持久
			status.setInStore(true);
		}else {
			status = new RoleHeroStatus();
			status.setRoleId(role.getRoleId());
			status.setInStore(false);
		}
		GameContext.getUserHeroApp().initRoleHeroStatus(status);
		if(Util.isEmpty(heros)){
			return ;
		}

		int battleHeroId = status.getBattleHeroId() ;
		//如果拥有英雄，但没有出战，系统自动设置为出战
		boolean haveOnBattle = false ;
		for(RoleHero hero: heros){
			GameContext.getUserHeroApp().addRoleHero(role.getRoleId(), hero);
			if(battleHeroId==hero.getHeroId()){
				haveOnBattle = true ;
				hero.setOnBattle((byte)1);
				status.getSwitchHeroSet().add(battleHeroId);
			}else{
				hero.setOnBattle((byte)0);
			}
		}
		if(!haveOnBattle){
			RoleHero hero = heros.get(0);
			battleHeroId = hero.getHeroId();
			hero.setOnBattle((byte)1);
			status.setBattleHeroId(hero.getHeroId());
			status.getSwitchHeroSet().add(battleHeroId);
		}
		//自动对可出战英雄进行动态处理
		this.switchHeroExtend(role, false);
		this.helpHeroExtend(role, status);
		
	}
	
	private void helpHeroExtend(RoleInstance role,RoleHeroStatus status){
		for(int switchId : status.getSwitchHeroSet()){
			//移除在助威中并且在出战中的英雄(容错)
			 status.getHelpHeroSet().remove(switchId);
		}
		int max = this.getMaxHelpHeroNum(role.getLevel());
		int removeSize =  status.getHelpHeroSet().size() - max ;
		if(removeSize <=0){
			return ;
		}
		//删除多余的
		Set<Integer> old = Sets.newLinkedHashSet() ;
		old.addAll(status.getHelpHeroSet());
		
		status.getHelpHeroSet().clear();
		int num = 0 ;
		for(int id : old){
			if(num >= max){
				break ;
			}
			status.getHelpHeroSet().add(id);
			num ++ ;
		}
	}
	
	private boolean switchHeroExtend(RoleInstance role,boolean notifyClient) {
		String roleId = role.getRoleId() ;
		RoleHeroStatus status = GameContext.getUserHeroApp().getRoleHeroStatus(roleId);
		// 判断当前可出战的英雄是否超过上限
		int maxSwitch = this.getMaxSwitchableHeroNum(role);
		int switchSize = status.getSwitchHeroSet().size();
		if (maxSwitch == switchSize) {
			return false;
		}
		boolean needNotify = false ;
		if (switchSize < maxSwitch) {
			// 自动补充
			Collection<RoleHero> heros = GameContext.getUserHeroApp().getAllRoleHero(roleId);
			int addSize = maxSwitch - switchSize;
			for (RoleHero hero : heros) {
				int heroId = hero.getHeroId();
				if (status.getSwitchHeroSet().contains(heroId)) {
					continue;
				}
				if (status.getHelpHeroSet().contains(heroId)) {
					//在助威中需过滤
					continue;
				}
				needNotify = true ;
				status.getSwitchHeroSet().add(heroId);
				addSize--;
				if (0 >= addSize) {
					break;
				}
			}
		}else{
			// 删除多余的
			int battleHeroId = status.getBattleHeroId() ;
			int removeSize = switchSize - maxSwitch;
			for (Iterator<Integer> it = status.getSwitchHeroSet().iterator(); it
					.hasNext();) {
				int curHeroId = it.next();
				if (curHeroId == battleHeroId) {
					continue;
				}
				needNotify = true ;
				it.remove();
				removeSize--;
				if (0 >= removeSize) {
					break;
				}
			}
		}
		if(needNotify && notifyClient){
			GameContext.getMessageCenter().sendSysMsg(role, this.getHeroSwitchUiMessage(role));
		}
		return needNotify ;
	}

	@Override
	public int onLogout(RoleInstance role, Object conetxt) {
		try {
			RoleHeroStatus status = GameContext.getUserHeroApp()
					.getRoleHeroStatus(role.getRoleId());
			this.saveRoleHeroStatus(status);
		}catch(Exception ex){
			logger.error("hero app saveRoleHeroStatus error,roleId=" + role.getRoleId(),ex);
		}
		String roleId = role.getRoleId() ;
		
		RoleHero battleHero = GameContext.getUserHeroApp().getOnBattleRoleHero(role.getRoleId());
		//将修改过的英雄入库
		Collection<RoleHero> allHeros = GameContext.getUserHeroApp().getAllRoleHero(roleId) ;
		if(!Util.isEmpty(allHeros)){
			for(RoleHero roleHero : allHeros){
				if(battleHero != null){
					if(battleHero.getHeroId() == roleHero.getHeroId()){
						float f = role.getCurHP()/(float)role.getMaxHP();
						short hpRate = (short)(f*(RoleHero.HP_RATE_FULL));
						roleHero.setHpRate(hpRate);
					}
				}
				if(!roleHero.isModify() || roleHero.isFromSystem()){
					continue ;
				}
				
				this.saveRoleHero(roleHero);
			}
		}
		//将英雄放入ssdbcache
		try {
			// 可战斗英雄信息保存
			List<RoleHero> heros = this.onlineSwitchHeros(roleId);
			for(RoleHero hero : heros){
				if(battleHero != null){
					if(battleHero.getHeroId() == hero.getHeroId()){
						float f = role.getCurHP()/(float)role.getMaxHP();
						short hpRate = (short)(f*(RoleHero.HP_RATE_FULL));
						hero.setHpRate(hpRate);
					}
				}
			}
			GameContext.getHeroStorage().saveRoleHeros(roleId, heros);
		}catch(Exception ex){
			logger.error("hero app herostorage save heros error,roleId=" + role.getRoleId(),ex);
		}
		HeroEquip he = new HeroEquip();
		he.setRoleId(roleId);
		
		//装备
		Collection<HeroEquipBackpack> packList = GameContext.getUserHeroApp().getEquipBackpack(roleId);
		if(null != packList){
			for(HeroEquipBackpack pack  : packList){
				try {
					pack.offline();
					he.getEquipMap().put(pack.getHeroId(), pack.getAllGoods());
				} catch (Exception ex) {
					logger.error(
							"hero app save equip error,roleId="
									+ role.getRoleId() + " heroId="
									+ pack.getHeroId(), ex);
				}
			}
		}
		//装备存入ssdbcache
		try{
			GameContext.getHeroStorage().saveHeroEquip(he);
		}catch(Exception ex){
			logger.error("hero app herostorage save hero equip error,roleId=" + roleId,ex);
		}
		
		//情况内存中数据
		GameContext.getUserHeroApp().cleanHeroData(role.getRoleId());
		return 1;
	}
	
	@Override
	public int onCleanup(String roleId, Object context) {
		//情况内存中数据
		GameContext.getUserHeroApp().cleanHeroData(roleId);
		return 0;
	}
	
	private Result heroGoodsToShadow(RoleInstance role, 
			RoleGoods roleGoods,boolean confirm,GoodsHero goodsHero){
		int shadowNum = goodsHero.getRecycleShadowNum() ;
		if(confirm){
			//直接转换为影子
			List<GoodsOperateBean> addList = Lists.newArrayList(
					new GoodsOperateBean(goodsHero.getShadowId(),shadowNum,
							roleGoods.getBind()));
			GoodsResult gr = GameContext.getUserGoodsApp().addDelGoodsForBag(role, addList, 
					OutputConsumeType.hero_goods_to_shadow,
					roleGoods,roleGoods.getCurrOverlapCount(), null, OutputConsumeType.goods_use);
			if(!gr.isSuccess()){
				return gr ;
			}
			Result result = new Result();
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsHero.getShadowId());
			String tips = GameContext.getI18n().messageFormat(TextId.Hero_goods_to_shadow_success_tips,
					goodsHero.getName(),gb.getName(),String.valueOf(shadowNum));
			result.setInfo(tips);
			result.success();
			return result ;
		}
		//二次确认提示用户是否转换为影子
		UseResult result = new UseResult();
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsHero.getShadowId());
		String tips = GameContext.getI18n().messageFormat(TextId.Hero_goods_to_shadow_confirm_tips,
				goodsHero.getName(),gb.getName(),String.valueOf(shadowNum));
		result.success();
		result.setMustConfirm(true);
		result.setInfo(tips);
		result.setConfirmCmdId(HERO_GOODS_TO_SHADOW_CMDID);
		//物品实例ID
		result.setConfirmInfo(roleGoods.getId());
		return result;
	}

	@Override
	public Result useHeroGoods(RoleInstance role, RoleGoods roleGoods,
			boolean confirm) throws ServiceException {
		try {
			Result result = new Result();
			GoodsHero goodsHero = GameContext.getGoodsApp().getGoodsTemplate(
					GoodsHero.class, roleGoods.getGoodsId());
			if (null == goodsHero) {
				result.setInfo(GameContext.getI18n().getText(
						TextId.ERROR_INPUT));
				return result;
			}
			//判断是否已经拥有了此英雄
			//如果有则走英雄物品与影子的兑换逻辑
			RoleHero hero = GameContext.getUserHeroApp().getRoleHero(role.getRoleId(), goodsHero.getId());
			if(null != hero){
				return this.heroGoodsToShadow(role, roleGoods, confirm, goodsHero) ;
			}
			//删除物品，添加英雄
			GoodsResult gr = GameContext.getUserGoodsApp()
					.deleteForBagByInstanceId(role, roleGoods.getId(), 1,
							OutputConsumeType.goods_use);
			if (!gr.isSuccess()) {
				return gr;
			}
			result = this.useHeroTemplate(role, goodsHero);
			if(result.isSuccess()){
				String tips = GameContext.getI18n().messageFormat(TextId.Hero_GOODS_USE_SUCCESS_TIPS,
						goodsHero.getName());
				result.setInfo(tips);
			}
			return result ;
		}catch(Exception ex){
			throw new ServiceException("useHeroGoods error",ex) ;
		}
	}
	
	private AttriBuffer getHeroLoveAttriBuffer(RoleHero hero){
		if(null == hero){
			return null ;
		}
		List<HeroLove> loveList = this.getHeroLoveList(hero.getHeroId());
		if(Util.isEmpty(loveList)){
			return null ;
		}
		AttriBuffer buffer = AttriBuffer.createAttriBuffer();
		for(HeroLove love : loveList){
			HeroLoveStatus  status = this.getHeroLoveStatus(hero, love);
			//是否激活
			if(HeroLoveStatus.activated != status.getStatus()){
				continue ;
			}
			HeroLoveAttribute hla = this.getHeroLoveAttribute(love.getLoveId(),status.getQuality(), status.getStar());
			if(null != hla){
				buffer.append(hla.getAttriItemList());
			}
		}
		return buffer ;
	}
	
	
	private RoleHero createRoleHero(String roleId,GoodsHero goodsHero,boolean fromSystem){
		RoleHero hero = new RoleHero();
		hero.setHeroId(goodsHero.getId());
		hero.setRoleId(roleId);
		hero.setQuality(goodsHero.getQualityType());
		hero.setStar(goodsHero.getStar());
		hero.setFromSystem(fromSystem);
		//添加技能
		this.initSkill(hero);
		return hero ;
	}
	
	@Override
	public RoleHero insertHeroDb(String roleId,GoodsHero goodsHero){
		RoleHero hero = this.createRoleHero(roleId, goodsHero, false);
		//实时入库
		GameContext.getBaseDAO().insert(hero);
		return hero ;
	}
	
	
	private Result useHeroGoods(RoleInstance role,int goodsId,boolean system){
		Result result = new Result() ;
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsId);
		if(null == gb || GoodsType.GoodsHero.getType() != gb.getGoodsType()){
			return result ;
		}
		GoodsHero goodsHero = (GoodsHero)gb ;
		//判断玩家是否已经拥有
		RoleHero roleHero  = GameContext.getUserHeroApp().getRoleHero(role.getRoleId(), goodsId);
		if(null != roleHero){
			return result ;
		}
		return this.useHeroTemplate(role, goodsHero,system) ;
	}
	
	
	@Override
	public Result useHeroGoods(RoleInstance role,int goodsId){
		return this.useHeroGoods(role, goodsId, false) ;
	}
	
	@Override
	public Result useHeroBySystem(RoleInstance role,int heroId) {
		Result result = this.useHeroGoods(role, heroId, true) ;
		if(!result.isSuccess()){
			return result ;
		}
		//同时放入可切换列表
		RoleHeroStatus status = GameContext.getUserHeroApp().getRoleHeroStatus(role.getRoleId());
		if(null == status){
			return result ;
		}
		status.getSwitchHeroSet().add(heroId);
		role.getBehavior().sendMessage(this.getHeroSwitchUiMessage(role));
		return result ;
	}
	
	
	private RoleHero getFirstUnOnBattleHero(String roleId){
		Collection<RoleHero> allHero = GameContext.getUserHeroApp().getAllRoleHero(roleId);
		if(Util.isEmpty(allHero)){
			return null ;
		}
		for(RoleHero roleHero : allHero){
			if(ON_BATTLE == roleHero.getOnBattle()){
				continue ;
			}
			return roleHero ;
		}
		return null ;
	}
	
	@Override
	public Result deleteHeroBySystem(RoleInstance role,int heroId) {
		Result result = new Result() ;
		String roleId = role.getRoleId() ;
		//获得当前出战的英雄
		RoleHero onBattleHero = GameContext.getUserHeroApp().getOnBattleRoleHero(roleId);
		boolean isOnBattle = (null != onBattleHero && heroId == onBattleHero.getHeroId() ) ;
		if(isOnBattle){
			RoleHero newOnBattleHero = this.getFirstUnOnBattleHero(roleId);
			this.onBattle(role, newOnBattleHero, onBattleHero,false) ;
		}
		boolean isOnSwitch = GameContext.getUserHeroApp().deleteRoleHero(roleId, heroId);
		//成功
		result.success();
		
		//通知删除
		C1257_HeroDeleteNotifyMessage deleteMsg = new C1257_HeroDeleteNotifyMessage() ;
		deleteMsg.setHeroId(heroId);
		role.getBehavior().sendMessage(deleteMsg);
		
		if(isOnSwitch){
			//刷新可切换ui
			role.getBehavior().sendMessage(this.getHeroSwitchUiMessage(role));
		}
		return result ;
	}
	
	private Result useHeroTemplate(RoleInstance role, GoodsHero goodsHero,boolean system){
		RoleHero hero = null ;
		if(system){
			hero = this.createRoleHero(role.getRoleId(), goodsHero,true) ;
		}else {
			hero = this.insertHeroDb(role.getRoleId(), goodsHero) ;
		}
		GameContext.getUserHeroApp().addRoleHero(role.getRoleId(), hero);
		//创建装备容器,没有装备
		this.initHeroEquipBackpack(role, hero.getHeroId(), null);
		//没有出战主动出战
		/*RoleHero onBattleHero = GameContext.getUserHeroApp().getOnBattleRoleHero(role.getRoleId());
		if(null == onBattleHero){
			this.onBattle(role, goodsHero.getId());
		}*/
		//push信息给客户端
		int heroId = goodsHero.getId() ;
		C1255_HeroAddRespMessage respMsg = new C1255_HeroAddRespMessage();
		respMsg.setHeroItem(this.getHeroInfoItem(hero));
		//配方
		HeroEquipFormulaListItem formula = new HeroEquipFormulaListItem();
		formula.setHeroId(heroId);
		formula.setFormulaList(GameContext.getEquipApp().getHeroEquipFormula(role, hero));
		respMsg.setFormula(formula);
		GameContext.getMessageCenter().sendSysMsg(role, respMsg);
		
		if(!system){
			//目标系统
			GameContext.getTargetApp().updateTarget(role, TargetCondType.HeroNum);
			// 红点提示规则
			this.pushHintRulesChange(role, hero);
			this.pushHintSkillRulesChange(role, hero);
			// 世界走马灯广播
			this.broadcast(role, goodsHero);
		}
		return new Result().success();
	}
	
	private void broadcast(RoleInstance role, GoodsHero goodsHero) {
		try {
			// 如果获得的英雄品质低于紫色，不予广播
			if (QualityType.purple.getType() > goodsHero.getQualityType()) {
				return ;
			}
			String broadcastInfo = GameContext.getI18n().getText(TextId.BROAD_CAST_HERO_GAIN).replace(Wildcard.Role_Name,
					Util.getColorRoleName(role, ChannelType.Publicize_Personal)).replace(Wildcard.GoodsName,
					Wildcard.getChatGoodsName(goodsHero.getId(), ChannelType.Publicize_Personal));
			GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Publicize_Personal, broadcastInfo, null, null);
		} catch (Exception e) {
			logger.error("HeroAppImpl.broadcast error!", e);
		}
	}
	
	@Override
	public Result useHeroTemplate(RoleInstance role, GoodsHero goodsHero) {
		return this.useHeroTemplate(role, goodsHero, false) ;
	}
	
	// 红点提示规则变化
	private void pushHintRulesChange(RoleInstance role, RoleHero roleHero) {
		GoodsHero goodsHero = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class, roleHero.getHeroId());
		if (null == goodsHero) {
			return;
		}
		HintRulesItem hintRulesItem = new HintRulesItem();
		hintRulesItem.setType(HintAppImpl.HINT_HERO);
		hintRulesItem.setTargetId(roleHero.getHeroId());
		List<HintGoodsTermItem> hintGoodsList = Lists.newArrayList();
		short needNum = (short) this.getStarShadowNumber(roleHero);
		// 如果升星到最高等级，则消耗物品数量为最大值
		if (this.isReachMaxQuality(roleHero)) {
			needNum = Short.MAX_VALUE;
		}
		hintGoodsList.add(new HintGoodsTermItem(goodsHero.getShadowId(), needNum));
		hintRulesItem.setHintGoodsTermList(hintGoodsList);
		GameContext.getHintApp().pushHintRulesChange(role, hintRulesItem);
	}
	
	private void pushHintSkillRulesChange(RoleInstance role, RoleHero roleHero) {
		GoodsHero goodsHero = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class, roleHero.getHeroId());
		if (null == goodsHero) {
			return;
		}
		// 技能规则变化
		List<HintSkillRulesItem> skillRulesList = Lists.newArrayList();
		for (short skillId : goodsHero.getSkillIdList()) {
			HintSkillRulesItem item = new HintSkillRulesItem();
			item.setSkillId(skillId);
			Skill skill = GameContext.getSkillApp().getSkill(skillId);
			int skillLevel = 0;
			RoleSkillStat skillStat = roleHero.getSkillMap().get(skillId);
			if (null != skillStat) {
				skillLevel = skillStat.getSkillLevel();
			}
			SkillDetail skillDetail = skill.getSkillDetail(skillLevel + 1);
			// 如果技能到达最大等级
			if (null == skillDetail) {
				item.setLevel(Short.MAX_VALUE);
				skillRulesList.add(item);
				continue;
			}
			item.setLevel((short) skillDetail.getLevel());
			skillRulesList.add(item);
		}
		HintHeroRulesItem item = new HintHeroRulesItem();
		item.setHeroId(roleHero.getHeroId());
		item.setHintSkillRulesList(skillRulesList);
		GameContext.getHintApp().pushHintSkillChange(role, item);
	}
	
	@Override
	public AttriBuffer getBaseAttriBuffer(int heroId,int heroLevel,int quality,int star){
		HeroAttribute levelRate = this.getAttributeHeroLevelRate(heroLevel);
		HeroAttribute heroRate = this.getAttributeHeroRate(heroId) ;
		HeroAttribute qualityRate = this.getAttributeQualityRate(quality, star);
		
		HeroAttribute bornRate = null ;
		GoodsHero hero = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class, heroId);
		if(null != hero){
			bornRate = this.getAttributeBornRate(hero.getQualityType(), hero.getStar());
		}
		
		AttriBuffer buffer = AttriBuffer.createAttriBuffer();
		for(AttributeType at : this.getAttributeTypeList()){
			byte attriType = at.getType();
			//成长
			float value = this.getAttributeValue(attriType, this.attributeTypeRate)
					* this.getAttributeValue(attriType, levelRate)
					* this.getAttributeValue(attriType, heroRate)
					* this.getAttributeValue(attriType, qualityRate);
			//初始
			float bornValue = this.getAttributeValue(attriType, this.attributeTypeRate)
					* this.getAttributeValue(attriType, heroRate)
					* this.getAttributeValue(attriType, bornRate);
			int totalValue = Math.max(0,(int)value) + Math.max(0,(int)bornValue);
			buffer.append(at,totalValue,false);
		}
		return buffer; 
	}
	
	private float getAttributeValue(byte attriType,HeroAttribute ha){
		if(null == ha){
			return 1 ;
		}
		return ha.getValue(attriType);
	}
	
	@Override
	public AttriBuffer getHeroGivenAttriBuffer(RoleHero hero, int givenHeroLevel,int givenQuality, int givenStar) {
		AttriBuffer buffer = AttriBuffer.createAttriBuffer();
		if(null == hero){
			return buffer ;
		}
		// 基本
		buffer.append(this.getBaseAttriBuffer(hero.getHeroId(),
				givenHeroLevel, givenQuality, givenStar));
		
		//情缘
		buffer.append(this.getHeroLoveAttriBuffer(hero));
		//!!!!!!!
		//处理加百分比的情况
		//!!!!!!!
		buffer.precToValue();
		// 装备(装备不算百分比)
		HeroEquipBackpack pack = GameContext.getUserHeroApp().getEquipBackpack(hero.getRoleId(),
				hero.getHeroId());
		if(null != pack){
			RoleGoods[] goodsList = pack.getGrids();
			for (RoleGoods rg : goodsList) {
				if (null == rg || RoleGoodsHelper.isExpired(rg)) {
					continue;
				}
				buffer.append(RoleGoodsHelper.getAttriBuffer(rg));
			}
		}
		return buffer;
	}

	@Override
	public AttriBuffer getHeroAttriBuffer(RoleHero hero) {
		return this.getHeroGivenAttriBuffer(hero, hero.getLevel(),hero.getQuality(),hero.getStar());
	}
	
	
	@Override
	public AttriBuffer getAttriBuffer(RoleInstance role) {
		RoleHero onHero = GameContext.getUserHeroApp().getOnBattleRoleHero(role.getRoleId());
		if(null == onHero){
			//没有出战的英雄
			return null ;
		}
		return this.getHeroAttriBuffer(onHero);
	}
	
	
	private HeroSwallowResult heroSwallowCond(RoleInstance role,int sourceHeroId,List<HeroSwallowItem> swallowList){
		HeroSwallowResult result = new HeroSwallowResult();
		RoleHero roleHero = GameContext.getUserHeroApp().getRoleHero(role.getRoleId(), sourceHeroId) ;
		if(null == roleHero){
			//提示参数错误
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result ;
		}
		if(Util.isEmpty(swallowList)){
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result ;
		}
		//不能超过角色等级
		int oldLevel = roleHero.getLevel() ;
		if(oldLevel >= role.getLevel()){
			result.setInfo(GameContext.getI18n().getText(TextId.Hero_level_canot_gt_role_level));
			return result ;
		}
		int heroQuality = roleHero.getQuality() ;
		int maxLevel = GameContext.getHeroApp().getMaxLevel(heroQuality);
		
		//判断是否到达了最大等级
		if( oldLevel >= maxLevel){
			result.setInfo(GameContext.getI18n().getText(TextId.Hero_have_reach_max_level));
			return result ;
		}
		//判断要吞噬的物品或英雄是否存在
		for(HeroSwallowItem item : swallowList ){
			String id = item.getId() ;
			if(Util.isEmpty(id)){
				result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
				return result ;
			}
			if(Constant.HERO_SWALLOW_GOODS_TYPE == item.getType()){
				RoleGoods rg = GameContext.getUserGoodsApp().getRoleGoods(role, StorageType.bag, id,0);
				if(null == rg){
					result.setInfo(GameContext.getI18n().getText(TextId.Hero_swallow_target_not_exist));
					return result ;
				}
				int singleExp = this.getSingleSwallowExp(rg);
				if(0 >= singleExp){
					//此物品不支持吞噬
					result.setInfo(GameContext.getI18n().messageFormat(
							TextId.Hero_goods_canot_swallow,this.getGoodsName(rg)));
					return result ;
				}
				//store 
				result.getSingleExpMap().put(rg.getId(), singleExp);
				result.getGoodsList().add(rg);
				continue ;
			}
			if(!Util.isNumber(id)){
				result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
				return result ;
			}
			int heroId = Integer.parseInt(id);
			//英雄
			RoleHero swallowHero = GameContext.getUserHeroApp().getRoleHero(role.getRoleId(), heroId);
			if(null == swallowHero){
				result.setInfo(GameContext.getI18n().getText(TextId.Hero_swallow_target_not_exist));
				return result ;
			}
			if(swallowHero.getHeroId() == roleHero.getHeroId()){
				//不能为自己
				result.setInfo(GameContext.getI18n().getText(TextId.Hero_swallow_target_canot_self));
				return result ;
			}
			//不能吞噬目前已经出战未的英雄
			RoleHero onHero = GameContext.getUserHeroApp().getOnBattleRoleHero(role.getRoleId());
			if(null != onHero && onHero.getHeroId() == swallowHero.getHeroId()){
				//当前出战的英雄不能吞噬
				result.setInfo(GameContext.getI18n().getText(TextId.Hero_swallow_onbattle_canot_self));
				return result ;
			}
			RoleHeroStatus status = GameContext.getUserHeroApp().getRoleHeroStatus(role.getRoleId());
			if(status.getSwitchHeroSet().contains(swallowHero.getHeroId())
					|| status.getHelpHeroSet().contains(swallowHero.getHeroId())){
				//待切换/助威的英雄不能吞噬
				result.setInfo(GameContext.getI18n().getText(TextId.Hero_swallow_onbattle_canot_self));
				return result ;
			}
			result.getHeroList().add(swallowHero);
		}
		result.setRoleHero(roleHero);
		result.success();
		return result ;
	}
	
	
	private void flagSuccessHeroSwallowResult(HeroSwallowResult result,RoleHero roleHero,int oldLevel){
		result.success();
		result.setStatus((oldLevel == roleHero.getLevel())?
				SUCCESS_NOT_INCR_LEVEL:SUCCESS_AND_INCR_LEVEL);
	}
	
	@Override
	public void addHeroExp(RoleInstance role,int exp){
		if(exp <=0){
			return ;
		}
		List<RoleHero> heroList = this.onlineSwitchHeros(role.getRoleId());
		if(Util.isEmpty(heroList)){
			return ;
		}
		for(RoleHero hero: heroList){
			int remain = exp ;
			//可能升多级
			while(remain >0){
				remain = this.addHeroExp(role, remain, hero);
			}
		}
	}

	private int addHeroExp(RoleInstance role,int exp,RoleHero roleHero){
		if(exp <=0){
			return 0 ;
		}
		//判断是否可以升级
		int currLv = roleHero.getLevel() ;
		int heroQuality = roleHero.getQuality() ;
		int maxLevel = this.getMaxLevel(heroQuality);
		//不能超过角色等级
		maxLevel = Math.min(maxLevel, role.getLevel()) ;
		if(currLv > maxLevel){
			return 0;
		}
		//获得当前级最大经验
		HeroLevelup lup = this.getHeroLevelup(heroQuality, currLv) ;
		if(null == lup){
			return 0;
		}
		int heroExp = roleHero.getExp() + exp ;
		
		if(currLv < maxLevel){
			//标识为修改
			roleHero.setModify(true);
			int maxExp = lup.getMaxExp() ;
			if(heroExp < maxExp){
				//未升级
				roleHero.setExp(heroExp);
				return 0;
			}
			//升级
			int oldLevel = roleHero.getLevel() ;
			roleHero.setLevel(oldLevel+ 1);
			roleHero.setExp(0);
			//升级触发效果
			this.onHeroLevelUpgraded(role, oldLevel, roleHero);
			return heroExp-maxExp;
		}
		//currLv == maxLevel
		//经验最多到上限制
		heroExp = Math.min(heroExp, lup.getMaxExp()-1);
		if(heroExp != roleHero.getExp()){
			//标识为修改
			roleHero.setModify(true);
			roleHero.setExp(heroExp);
		}
		return 0 ;
	}
	
	@Override
	public HeroSwallowResult heroSwallow(RoleInstance role,int sourceHeroId,List<HeroSwallowItem> swallowList){
		HeroSwallowResult result = this.heroSwallowCond(role, sourceHeroId, swallowList);
		if(!result.isSuccess()){
			return result ;
		}
		RoleHero roleHero = result.getRoleHero() ;
		List<RoleGoods> goodsList = result.getGoodsList() ;
		Map<String,Integer> singleExpMap = result.getSingleExpMap() ;
		List<RoleHero> heroList = result.getHeroList() ;
		int oldLevel = roleHero.getLevel() ;
		int heroQuality = roleHero.getQuality() ;
		int maxLevel = this.getMaxLevel(heroQuality);
		//不能超过角色等级
		maxLevel = Math.min(maxLevel, role.getLevel()) ;
		//将结果设置为失败
		result.failure();
		try {
			// 计算到达最大等级需要的经验
			int needMaxExp = this.reachMaxLevelNeedExp(roleHero,maxLevel) ;
			int addExp = 0;
			// 先消耗物品
			for (RoleGoods goods : goodsList) {
				int needExp = needMaxExp - addExp;
				int singleExp = singleExpMap.get(goods.getId());
				int n = needExp / singleExp;
				int needNum = (needExp % singleExp) == 0 ? n : n + 1;
				int currOverlapCount = goods.getCurrOverlapCount() ;
				if ( currOverlapCount >= needNum) {
					// 删除物品
					GameContext.getUserGoodsApp().deleteForBagByInstanceId(
							role, goods.getId(), needNum,
							OutputConsumeType.hero_swallow_consume);
					// 已经满级可以提前返回
					this.swallowToMaxLevel(roleHero, maxLevel);
					this.flagSuccessHeroSwallowResult(result, roleHero, oldLevel);
					return result;
				}
				// 删除物品
				GameContext.getUserGoodsApp().deleteForBagByInstanceId(role,
						goods.getId(), OutputConsumeType.hero_swallow_consume);
				//!!! 此处的物品数量必须在物品删除前获得，物品删除后会将物品数量设置为0
				addExp += singleExp * currOverlapCount;
			}
			// 消耗英雄
			for (RoleHero rh : heroList) {
				int needExp = needMaxExp - addExp;
				int singleExp = this.getSwallowExp(rh);
				if (singleExp >= needExp) {
					// 删除英雄
					this.deleteSwallowHero(rh);
					// 收集已经删除的英雄返回给客户端
					result.getSwallowHeroList().add(rh.getHeroId());
					// 已经满级可以提前返回
					this.swallowToMaxLevel(roleHero, maxLevel);
					this.flagSuccessHeroSwallowResult(result, roleHero, oldLevel);
					return result;
				}
				// 收集已经删除的英雄返回给客户端
				result.getSwallowHeroList().add(rh.getHeroId());
				// 删除英雄
				this.deleteSwallowHero(rh);
				addExp += singleExp;
			}
			// 计算addExp可以升级
			//因为开始等级为影响当前等级，所以需要将影响的当前经验+进去
			addExp += roleHero.getExp();
			int targetLv =  roleHero.getLevel();
			for (int lv = roleHero.getLevel(); lv < maxLevel; lv++) {
				HeroLevelup lup = this.getHeroLevelup(heroQuality, lv);
				int thisLvMaxExp = lup.getMaxExp();
				if (addExp < thisLvMaxExp) {
					roleHero.setExp(addExp);
					if (targetLv == maxLevel) {
						// 满级当前经验为0
						roleHero.setExp(0);
					}
					roleHero.setLevel(targetLv);
					this.saveRoleHero(roleHero);
					this.flagSuccessHeroSwallowResult(result, roleHero, oldLevel);
					return result;
				}else{
					// 升级
					targetLv ++;
					addExp -= thisLvMaxExp;
				}
			}
			this.flagSuccessHeroSwallowResult(result, roleHero, oldLevel);
			return result;
		}finally{
			//计算效果
			if(result.isSuccess()){
				this.onHeroLevelUpgraded(role, oldLevel, roleHero);
				if(!Util.isEmpty(result.getSwallowHeroList())){
					//有装备删除
					GameContext.getTargetApp().updateTarget(role, TargetCondType.HeroEquipMosaic);
					GameContext.getTargetApp().updateTarget(role, TargetCondType.HeroEquipQuality);
					GameContext.getTargetApp().updateTarget(role, TargetCondType.HeroEquipStrength);
				}
			}
		}
	}
	
	private void onHeroLevelUpgraded(RoleInstance role,int oldLevel, RoleHero roleHero){
		this.heroLevelupEffect(role,oldLevel, roleHero);
		//目标系统
		GameContext.getTargetApp().updateTarget(role, TargetCondType.HeroLevel);
		//通知客户端
		C1259_HeroLevelNotifyMessage levelChangedMsg = new C1259_HeroLevelNotifyMessage() ;
		levelChangedMsg.setHeroId(roleHero.getHeroId());
		levelChangedMsg.setLevel((byte)roleHero.getLevel());
		role.getBehavior().sendMessage(levelChangedMsg);
	}
	
	private void deleteSwallowHero(RoleHero roleHero){
		//删除数据库
		GameContext.getBaseDAO().delete(RoleHero.class, RoleHero.HERO_ID, roleHero.getHeroId(),
				RoleHero.ROLE_ID,roleHero.getRoleId());
		//先获得英雄的背包
		HeroEquipBackpack pack = GameContext.getUserHeroApp().getEquipBackpack(roleHero.getRoleId(),
				roleHero.getHeroId());
		//删除内存
		GameContext.getUserHeroApp().deleteRoleHero(roleHero.getRoleId(), roleHero.getHeroId());
		//如果英雄有装备将装备自动放入邮箱
		if(null == pack){
			return ;
		}
		List<RoleGoods> goodsList = pack.getAllGoods();
		if(Util.isEmpty(goodsList)){
			return ;
		}
		GameContext.getMailApp().sendMailAsync(
				roleHero.getRoleId(), 
				this.getText(TextId.Hero_swallow_equip_mail_title), 
				this.getText(TextId.Hero_swallow_equip_mail_content), 
				this.getText(TextId.SYSTEM), 
				OutputConsumeType.hero_swallow_equip_mail.getType(), null,goodsList);
	}
	
	
	private void swallowToMaxLevel(RoleHero roleHero,int maxLevel){
		roleHero.setLevel(maxLevel);
		roleHero.setExp(0);
		this.saveRoleHero(roleHero);
	}
	
	private String getGoodsName(RoleGoods goods){
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goods.getGoodsId());
		if(null == gb){
			return "" ;
		}
		return gb.getName() ;
	}
	
	private int getSingleSwallowExp(RoleGoods goods){
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goods.getGoodsId());
		if(null == gb){
			return 0 ;
		}
		if(GoodsType.GoodsHero.getType() == gb.getGoodsType()){
			return ((GoodsHero)gb).getSwallowExp() ;
		}
		if(GoodsType.GoodsHeroAid.getType() == gb.getGoodsType()){
			return ((GoodsHeroAid)gb).getSwallowExp() ;
		}
		return 0 ;
	}
	
	/**
	 * 到达最高等级需要的经验
	 * @param roleHero
	 * @return
	 */
	private int reachMaxLevelNeedExp(RoleHero roleHero,int maxLv){
		int quality = roleHero.getQuality() ;
		int currLv = roleHero.getLevel() ;
		if(currLv >= maxLv){
			return 0 ;
		}
		HeroLevelup lup = this.getHeroLevelup(quality, currLv) ;
		int total = Math.max(0, lup.getMaxExp() - roleHero.getExp()) ;
		for(int lv=roleHero.getLevel()+1;lv <maxLv;lv++){
			lup = this.getHeroLevelup(quality, lv) ;
			total += lup.getMaxExp() ;
		}
		return total ;
	}
	
	
	private boolean isOnBattleHero(RoleHero roleHero){
		return (null != roleHero && 1 == roleHero.getOnBattle()) ;
	}
	
	@Override
	public boolean isOnBattleHero(String roleId,int heroId){
		RoleHero roleHero = GameContext.getUserHeroApp().getOnBattleRoleHero(roleId);
		if(null == roleHero){
			return false ;
		}
		return heroId == roleHero.getHeroId() ;
	}
	
	@Override
	public void onRoleLevelUp(RoleInstance role){
		try {
			//将未出战的英雄也满血
			this.fullHp(role);
			//可上阵英雄数目改变
			this.switchableNumChange(role);
		}catch(Exception ex){
			logger.error("onRoleLevelUp error",ex);
		}
	}
	
	private void fullHp(RoleInstance role){
		List<RoleHero> heroList = this.onlineSwitchHeros(role.getRoleId());
		if(Util.isEmpty(heroList)){
			return ;
		}
		for(RoleHero hero : heroList){
			hero.setHpRate(RoleHero.HP_RATE_FULL);
		}
		//当前英雄的hpate是靠 curHP/maxHP 计算的
		role.setCurHP(role.getMaxHP());
		role.getBehavior().sendMessage(this.getHeroSwitchUiMessage(role));
	}
	
	
	private void switchableNumChange(RoleInstance role){
		int level = role.getLevel();
		int oldLevel = level - 1;
		byte num = this.getMaxSwitchableHeroNum(level);
		byte oldNum = this.getMaxSwitchableHeroNum(oldLevel);
		byte helpNum = this.getMaxHelpHeroNum(level);
		byte helpOldNum = this.getMaxHelpHeroNum(oldLevel);
		if (num == oldNum && helpNum == helpOldNum) {
			return;
		}
		//通知可切换数目变大
		//C1270_HeroSwitchableNumRespMessage notifyMsg = new C1270_HeroSwitchableNumRespMessage();
		//notifyMsg.setMaxNum( num);
		//notifyMsg.setHelpMaxNum(helpNum);
		//GameContext.getMessageCenter().sendSysMsg(role, notifyMsg);
		this.switchHeroExtend(role, true);
	}
	
	@Override
	public Result heroExchange(RoleInstance role,int heroId){
		Result result = new Result();
		GoodsHero hero = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class, heroId);
		if(null == hero || null == this.heroIdentifyList 
				|| !this.heroIdentifyList.contains(heroId)){
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result ;
		}
		if(hero.getShadowId()<=0 || hero.getShadowNum()<=0){
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_DATA));
			return result ;
		}
		RoleHero roleHero = GameContext.getUserHeroApp().getRoleHero(role.getRoleId(), heroId);
		if(null != roleHero){
			//已经拥有不用兑换
			result.setInfo(GameContext.getI18n().getText(TextId.Hero_exchange_had_this_hero));
			return result ;
		}
		//删除相关物品
		GoodsResult gr = GameContext.getUserGoodsApp().deleteForBag(role, hero.getShadowId(), 
				hero.getShadowNum(), OutputConsumeType.hero_exchange_consume);
		if(!gr.isSuccess()){
			return gr ;
		}
		try {
			result = this.useHeroTemplate(role, hero) ;
		} catch (Exception e) {
			logger.error("heroExchange error,heroId=" + heroId + " roleId=" + role.getRoleId(),e);
			result.setInfo(GameContext.getI18n().getText(TextId.SYSTEM_ERROR));
		}
		result.setInfo(GameContext.getI18n().messageFormat(TextId.Hero_exchange_success,hero.getName()));
		result.success();
		return result ;
	}
	
	private HeroLove getHeroLoveById(int loveId) {
		return this.fromMap(this.heroLoveMap, String.valueOf(loveId));
	}
	
	private List<HeroLove> getHeroLoveList(int heroId) {
		return this.fromMap(this.heroLoveListMap, String.valueOf(heroId));
	}
	
	private HeroLoveAttribute getHeroLoveAttribute(int loveId,int quality,int star){
		String key = loveId + "_" + quality + "_" + star ; 
		return this.fromMap(heroLoveAttributeMap, key);
	}
	
	
	private void dealLoveStatus(HeroLoveStatus ret,int quality,int star,int targetId){
		ret.setStatus(HeroLoveStatus.activated);
		if(ret.isNull()){
			ret.reset(quality,star,targetId);
			return ;
		}
		//取最小的品质
		if(ret.lessThan(quality,star)){
			//小于
			ret.reset(quality,star,targetId);
			return ;
		}
		if(ret.equal(quality,star)){
			//多次符合条件
			ret.incrReachNum(targetId);
			return ;
		}
	}
	
	private HeroLoveStatus getHeroLoveStatus(RoleHero hero,HeroLove love){
		return getHeroLoveStatus(hero,love,null,null,null);
	}
	
	private QualityStarSupport getTarget(HeroLoveType loveType,String roleId,int targetId){
		switch(loveType){
		case horse :
			 return GameContext.getRoleHorseApp().getRoleHorse(
						Integer.parseInt(roleId), targetId) ;
		case pet :
			return GameContext.getUserPetApp().getRolePet(
					roleId,targetId) ;
		case godWeapon :
			break ;
		case hero:
			return this.getLoveEffectHero(roleId, targetId) ;
		}
		return null ;
	}
	
	/**
	 * 获得对情缘有效的RoleHero
	 * @param roleId
	 * @param heroId
	 * @return
	 */
	private RoleHero getLoveEffectHero(String roleId,int heroId){
		RoleHero hero = GameContext.getUserHeroApp().getRoleHero(roleId, heroId) ;
		if(null == hero){
			return null ;
		}
		RoleHeroStatus status = GameContext.getUserHeroApp().getRoleHeroStatus(roleId);
		if(Util.inSet(heroId, status.getSwitchHeroSet(),
				status.getHelpHeroSet())){
			return hero ;
		}
		return null ;
	}
	
	private boolean isLoveEffectHero(String roleId,int heroId){
		RoleHero roleHero = this.getLoveEffectHero(roleId, heroId);
		return (null != roleHero);
	}
	
	private HeroLoveStatus getHeroLoveStatus(RoleHero hero, HeroLove heroLove, 
			HeroLoveTarget added, HeroLoveTarget removed,HeroLoveTarget modifyed) {
		HeroLoveStatus ret = new HeroLoveStatus();
		ret.unActivated();
		byte loveType = heroLove.getLoveType() ;
		HeroLoveType hlt = HeroLoveType.get(loveType);
		if (null == hlt) {
			return ret;
		}
		if(Util.isEmpty(heroLove.getIdSet())){
			ret.notOpen(); 
			return ret ;
		}
		for(int id : heroLove.getIdSet()){
			if(null != added && added.getId() == id){
				//添加
				this.dealLoveStatus(ret, added.getQuality(),
						added.getStar(), id);
				continue ;
			}
			boolean mustRemove = (null != removed && id == removed.getId()) ;
			QualityStarSupport target = null ;
			if(mustRemove || 
					(null == (target = this.getTarget(hlt, hero.getRoleId(), id)))){
				//未激活
				ret.unActivated();
				return ret ;
			}
			if(null != modifyed && modifyed.getId() == id){
				//用修改者的品质
				this.dealLoveStatus(ret, modifyed.getQuality(),
						modifyed.getStar(), id);
			}else{
				//用原来的品质
				this.dealLoveStatus(ret, target.getQuality(),
						target.getStar(), id);
			}
		}
		return ret ;
	}
	
	private byte getHeroAstaff(int quality,int star){
		HeroQualityUpgrade v = this.getHeroQualityUpgrade(quality, star);
		return (null == v)?0:v.getAstaff() ;
	}
	
	@Override
	public HeroInfoItem getHeroInfoItem(RoleHero rh){
		GoodsHero hero = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class, rh.getHeroId());
		if(null == hero){
			return null ;
		}
		HeroInfoItem item = new HeroInfoItem();
		item.setName(hero.getName());
		item.setHeroId(rh.getHeroId());
		item.setLevel((byte)rh.getLevel());
		item.setQuality(rh.getQuality());
		item.setStar(rh.getStar());
		item.setMaxStar(this.getMaxStar(rh.getQuality()));
		item.setOnBattle(rh.getOnBattle());
		item.setExp(rh.getExp());
		HeroLevelup lu = this.getHeroLevelup(rh.getQuality(), rh.getLevel()) ;
		item.setMaxExp(lu.getMaxExp());
		item.setSwallowExp(this.getSwallowExp(rh));
		//战斗力
		item.setBattleScore(this.getBattleScore(rh));
		item.setResId((short)hero.getResId());
		item.setShadowId(hero.getShadowId());
		item.setImageId(hero.getImageId());
		item.setGearId(hero.getGearId());
		item.setSeriesId(hero.getSeriesId());
		item.setAstaff(this.getHeroAstaff(rh.getQuality(), rh.getStar()));
		return item ;
	}
	
	@Override
	public boolean isReachMaxQuality(RoleHero hero) {
		
		HeroBaseConfig baseConf = this.getHeroBaseConfig();
		
		if(hero.getQuality() >= baseConf.getMaxQuality() && hero.getStar() >= baseConf.getMaxStar()){
			return true;
		}
		
		HeroQualityUpgrade conf = this.getHeroQualityUpgrade(hero.getQuality(),
				hero.getStar());
		
		if (null == conf || null == conf.getNextConf()) {
			return true;
		}
		return false;
	}
	
	public HeroQualityUpgradeResult heroQualityUpgrade(RoleInstance role,int heroId){
		HeroQualityUpgradeResult result = new HeroQualityUpgradeResult();
		RoleHero hero = GameContext.getUserHeroApp().getRoleHero(role.getRoleId(), heroId);
		if (null == hero) {
			// 提示参数错误
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result;
		}
		if(this.isReachMaxQuality(hero)){
			result.setInfo(GameContext.getI18n().getText(TextId.Hero_reach_max_quality_notto_upgrade));
			return result ;
		}
		
		//不考虑当前已经溢出的情况,担心配置错误出现刷的bug
		GoodsHero goodsHero = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class,heroId);
		int shadowId  = goodsHero.getShadowId() ;
		int haveShadowNum = role.getRoleBackpack().countByGoodsId(shadowId);
		if(haveShadowNum <=0){
			//提示物品不足
			result.setInfo(GameContext.getI18n().getText(TextId.GOODS_NO_ENOUGH));
			return result ;
		}
		int needShadowNum = this.starUpNeedShadowNum(hero);
		int delShadowNum = Math.min(needShadowNum, haveShadowNum) ;
		if(delShadowNum >0){
			Result gr = GameContext.getUserGoodsApp().deleteForBag(role, shadowId, delShadowNum, 
					OutputConsumeType.hero_quality_upgrade_consume);
			if(!gr.isSuccess()){
				result.setInfo(gr.getInfo());
				return result ;
			}
		}
		
		int progress = Math.max(0, hero.getQualityProgress()) + delShadowNum ;
		HeroQualityUpgrade conf = this.getHeroQualityUpgrade(hero.getQuality(), hero.getStar());
		if(haveShadowNum >= needShadowNum){
			//升级成功
			int preQuality = hero.getQuality();
			int preStar = hero.getStar() ;
			HeroQualityUpgrade nextConf = conf.getNextConf() ;
			hero.setQualityProgress(0);
			hero.setQuality(nextConf.getQuality());
			hero.setStar(nextConf.getStar());
			try {
				// 计算属性
				boolean isOnBattle = (ON_BATTLE == hero.getOnBattle());
				if (isOnBattle) {
					AttriBuffer buffer = this.getHeroGivenAttriBuffer(hero,
							hero.getLevel(), preQuality, preStar);
					buffer.reverse();
					buffer.append(this.getHeroAttriBuffer(hero));
					GameContext.getUserAttributeApp().changeAttribute(role,
							buffer);
					role.getBehavior().notifyAttribute();
				} else {
					if(this.isLoveEffectHero(hero.getRoleId(), heroId)){
						//情缘
						this.onTargetStarChanged(role.getIntRoleId(), heroId,
								hero.getQuality(), hero.getStar(), preQuality,
								preStar, HeroLoveType.hero.getType());
					}
					//判断是否在出战的3英雄里面
					RoleHeroStatus status = GameContext.getUserHeroApp().getRoleHeroStatus(role.getRoleId());
					if(status.getSwitchHeroSet().contains(heroId)){
						this.syncBattleScore(role, heroId, true);
					}
				}
				// 目标系统
				GameContext.getTargetApp().updateTarget(role,
						TargetCondType.HeroQualityStar);
			}catch(Exception ex){
				logger.error("",ex);
			}
			// 通知红点提示规则变化
			this.pushHintRulesChange(role, hero);
			result.setStatus(SUCCESS_AND_INCR_LEVEL);
		}else{
			hero.setQualityProgress(progress);
			result.setStatus(SUCCESS_NOT_INCR_LEVEL);
		}
		this.saveRoleHero(hero);
		result.success();
		return result ;
	}
	
	private int starUpNeedShadowNum(RoleHero roleHero) {
		int progress = roleHero.getQualityProgress();
		int maxShadow = this.getStarShadowNumber(roleHero);
		return Math.max(maxShadow - progress,0);
	}
	
	private int getStarShadowNumber(RoleHero roleHero) {
		HeroQualityUpgrade starConfig = this.getHeroQualityUpgrade(roleHero.getQuality(), roleHero.getStar());
		if (null == starConfig) {
			return 0;
		}
		return starConfig.getNextShadowNum();
	}
	
	@Override
	public C1268_HeroQualityInfoRespMessage buildHeroQualityInfoMessage(RoleHero hero){
		C1268_HeroQualityInfoRespMessage respMsg = new C1268_HeroQualityInfoRespMessage();
		int heroId = hero.getHeroId() ;
		respMsg.setHeroId(heroId);
		GoodsHero goodsHero = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class, heroId);
		GoodsBase goodsShadow = GameContext.getGoodsApp().getGoodsBase(goodsHero.getShadowId());
		//升品需要的物品
		respMsg.setGoodsItem(goodsShadow.getGoodsLiteNamedItem());
		if(GameContext.getHeroApp().isReachMaxQuality(hero)){
			respMsg.setFull((byte)1);
			respMsg.setFullProgress((short)1);
			//升星满级后显示当前属性
			//计算各部分属性
			List<AttriTypeStrValueItem> attriList = Lists.newArrayList();
			AttriBuffer buffer = GameContext.getHeroApp().getHeroAttriBuffer(hero);
			java.util.Map<Byte, AttriItem> attriMap = buffer.getMap();
			for (AttriItem ai : attriMap.values()) {
				if (ai.getValue() <= 0) {
					continue;
				}
				AttriTypeStrValueItem item = new AttriTypeStrValueItem();
				item.setType(ai.getAttriTypeValue());
				item.setValue(AttributeType.formatValue(ai.getAttriTypeValue(),
						ai.getValue()));
				attriList.add(item);
			}
			respMsg.setAttriList(attriList);
			return respMsg ;
		}
		HeroQualityUpgrade conf = GameContext.getHeroApp().getHeroQualityUpgrade(hero.getQuality(), hero.getStar());
		
		respMsg.setFull((byte)0);
		respMsg.setProgress((short)hero.getQualityProgress());
		respMsg.setFullProgress((short)conf.getNextShadowNum());
		//获得升品质后的
		HeroQualityUpgrade nextConf = conf.getNextConf() ;
		AttriBuffer givenBuffer = GameContext.getHeroApp().getHeroGivenAttriBuffer(
				hero, hero.getLevel(),nextConf.getQuality(), nextConf.getStar());
		AttriBuffer nowBuffer = GameContext.getHeroApp().getHeroGivenAttriBuffer(
				hero, hero.getLevel(),conf.getQuality(), conf.getStar());
		givenBuffer.append(nowBuffer.reverse());
		
		List<AttriTypeStrValueItem> attriList = Lists.newArrayList();
		for(AttributeType at :GameContext.getHeroApp().getAttributeTypeList()){
			AttriTypeStrValueItem item = new AttriTypeStrValueItem();
			item.setType(at.getType());
			float value = 0 ;
			AttriItem ai = givenBuffer.getAttriItem(at);
			if(null != ai){
				value = ai.getValue() ;
			}
			item.setValue(AttributeType.formatValue(at.getType(), value));
			attriList.add(item);
		}
		respMsg.setAttriList(attriList);
		return respMsg;
	}
	
	public short getHeroHeadId(int heroId ){
		GoodsHero gh = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class,heroId);
		if(null == gh){
			return RespTypeStatus.DEFAULT_HERO_HEAD_ID ;
		}
		return gh.getHeadId() ;
	}
	
	public short getHeroResId(int heroId ){
		GoodsHero gh = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class,heroId);
		if(null == gh){
			return RespTypeStatus.DEFAULT_HERO_HEAD_ID ;
		}
		return (short) gh.getResId();
	}
	
	public short getRoleHeroHeadId(String roleId) {
		RoleHero rh = GameContext.getUserHeroApp().getOnBattleRoleHero(roleId);
		if(null == rh){
			return RespTypeStatus.DEFAULT_HERO_HEAD_ID ;
		}
		return this.getHeroHeadId(rh.getHeroId()) ;
	}
	
	@Override
	public RoleHero getRoleHero(String roleId,int heroId) {
		return GameContext.getUserHeroApp().getRoleHero(roleId, heroId);
	}
	
	public short getRoleHeroResId(String roleId) {
		RoleHero rh = GameContext.getUserHeroApp().getOnBattleRoleHero(roleId);
		if(null == rh){
			return RespTypeStatus.DEFAULT_HERO_HEAD_ID ;
		}
		return this.getHeroResId(rh.getHeroId());
	}
	
	private HeroSwitchableInfoItem buildHeroSwitchableInfoItem(RoleHero roleHero){
		HeroSwitchableInfoItem item = new HeroSwitchableInfoItem() ;
		short hpRate = roleHero.getHpRate();
//		RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleHero.getRoleId());
//		RoleHero hero = GameContext.getUserHeroApp().getOnBattleRoleHero(roleHero.getRoleId());
//		if(hero != null){
//			if(hero.getHeroId() == roleHero.getHeroId()){
//				float f = role.getCurHP()/(float)role.getMaxHP();
//				hpRate = (short)(f*(RoleHero.HP_RATE_FULL));
//			}
//		}
		item.setHeroId(roleHero.getHeroId());
		item.setHpRate(hpRate);
		return item ;
	}
	
	
	private List<HeroSwitchableInfoItem> getSwitchableHeroInfoList(String roleId){
		List<RoleHero> heroList = this.onlineSwitchHeros(roleId);
		List<HeroSwitchableInfoItem> list = Lists.newArrayList() ;
		for(RoleHero hero : heroList){
			list.add(this.buildHeroSwitchableInfoItem(hero)) ;
		}
		return list ;
	}
	
	private List<HeroSwitchableInfoItem> getHelpHeroInfoList(String roleId){
		List<RoleHero> heroList = this.helpHeros(roleId);
		List<HeroSwitchableInfoItem> list = Lists.newArrayList() ;
		for(RoleHero hero : heroList){
			list.add(this.buildHeroSwitchableInfoItem(hero)) ;
		}
		return list ;
	}
	
	/*@Override
	public List<HeroSwitchableInfoItem> getNonSwitchableHeroInfoList(String roleId){
		List<HeroSwitchableInfoItem> list = Lists.newArrayList() ;
		//获得所有英雄
		Collection<RoleHero> heroList = GameContext.getUserHeroApp().getAllRoleHero(roleId);
		if(Util.isEmpty(heroList)){
			return list ;
		}
		RoleHeroStatus roleHeroStatus = GameContext.getUserHeroApp().getRoleHeroStatus(roleId);
		for(RoleHero hero : heroList){
			if(null != roleHeroStatus && 
					roleHeroStatus.getSwitchHeroSet().contains(hero.getHeroId())){
				//已经出战
				continue ;
			}
			list.add(this.buildHeroSwitchableInfoItem(hero));
		}
		return list ;
	}*/
	
	private String getText(String textId){
		return GameContext.getI18n().getText(textId);
	}
	
	
	@Override
	public Result updateSwitchableHero(RoleInstance role,int[] heroList,int[] helpHeroList){
		return this.updateSwitchableHero(role, heroList, helpHeroList, false);
	}
	
	@Override
	public Result systemUpdateSwitchableHero(RoleInstance role,int[] heroList,int[] helpHeroList){
		return this.updateSwitchableHero(role, heroList, helpHeroList, true);
	}
	
	private HeroSwitchResult checkHeroList(RoleInstance role,int[] heros,boolean isHelpHero){
		HeroSwitchResult result = new HeroSwitchResult();
		boolean isEmpty = (null == heros || 0 == heros.length) ;
		//助威的英雄是可以为空的
		if(!isHelpHero && isEmpty){
			result.setInfo(this.getText(TextId.ERROR_INPUT));
			return result ;
		}
		if(isEmpty){
			result.success();
			return result ;
		}
		//判断是否有重复
		List<Integer> heroList = Lists.newArrayList();
		for(int heroId : heros){
			if(0 == heroId){
				continue ;
			}
			heroList.add(heroId);
			//判断英雄是否存在
			RoleHero hero = GameContext.getUserHeroApp().getRoleHero(role.getRoleId(), heroId);
			if(null == hero){
				result.setInfo(this.getText(TextId.ERROR_INPUT));
				return result ;
			}
		}
		if(!isHelpHero && 0 == heroList.size()){
			result.setInfo(this.getText(TextId.ERROR_INPUT));
			return result ;
		}
		//判断上限
		int max = isHelpHero?this.getMaxHelpHeroNum(role.getLevel()):this.getMaxSwitchableHeroNum(role);
		if( heroList.size() > max){
			//出战超过上限
			result.setInfo(this.getText(TextId.ERROR_INPUT));
			return result ;
		}
		result.setHeroList(heroList);
		result.success();
		return result ;
	}
	
	private Result updateSwitchableHero(RoleInstance role,int[] heros,
										int[] helpHeros,
										boolean isSystemUse){
		Result result = new Result();
		if(!isSystemUse && !GameContext.getMapApp().canMapProperty(role,
				MapProperty.canChange3Hero.getType())){
			//系统调用不判断
			result.setInfo(this.getText(TextId.MAP_CANOT_DO_THIS_THING));
			return result ;
		}
		//检测英雄列表
		HeroSwitchResult checkResult = this.checkHeroList(role, heros, false);
		if(!checkResult.isSuccess()){
			return checkResult ;
		}
		//检查助威英雄
		HeroSwitchResult checkHelpResult = this.checkHeroList(role, helpHeros, true);
		if(!checkHelpResult.isSuccess()){
			return checkHelpResult ;
		}
		List<Integer> heroList = checkResult.getHeroList() ;
		List<Integer> helpHeroList = checkHelpResult.getHeroList() ;
		//判断2个lsit是否有相同的元素存在
		if(null != heroList && null != helpHeroList &&
				!Collections.disjoint(heroList,helpHeroList)){
			result.setInfo(this.getText(TextId.ERROR_INPUT));
			return result ;
		}

		String roleId = role.getRoleId() ;
		RoleHeroStatus status = GameContext.getUserHeroApp().getRoleHeroStatus(roleId);
		//是否切换了第一英雄
		boolean isChangeOnBattle = isSystemUse? status.getBattleHeroId() != heros[0]:!heroList.contains(status.getBattleHeroId());
        int cd = isSystemUse ? 0:this.getOnBattleCd(role) ;
		if(isChangeOnBattle && cd > 0){
			//判断cd
			result.setInfo(this.getText(TextId.Hero_On_Battle_Cd_Limit));
			return result ;
		}
		
		RoleHero battleHero = GameContext.getUserHeroApp().getOnBattleRoleHero(roleId);
		//情愿有可能改变
		AttriBuffer preBuffer = this.getHeroAttriBuffer(battleHero) ;
		//更新切换列表
		int preBattleIndex = 0 ;
		if(isChangeOnBattle && !isSystemUse){
			for(int heroId : status.getSwitchHeroSet()){
				if(heroId == status.getBattleHeroId()){
					break ;
				}
				preBattleIndex++ ;
			}
		}
		Set<Integer> preHeroSet = Sets.newHashSet();
		preHeroSet.addAll(status.getSwitchHeroSet());

		Set<Integer> preHelpHeroSet = Sets.newHashSet();
		preHelpHeroSet.addAll(status.getHelpHeroSet());

		//出战英雄
		status.getSwitchHeroSet().clear();
		for(int heroId : heroList){
			status.getSwitchHeroSet().add(heroId);
		}

		//协助英雄
		status.getHelpHeroSet().clear();
		if(null != helpHeroList){
			for(int heroId : helpHeroList){
				status.getHelpHeroSet().add(heroId);
			}
		}
		
		AttriBuffer postBuffer = this.getHeroAttriBuffer(battleHero) ;
		postBuffer.append(preBuffer.reverse());
		GameContext.getUserAttributeApp().changeAttribute(role, postBuffer);
		
		if(isChangeOnBattle){
			//修改出战英雄的时候会同步属性
			preBattleIndex = Math.min(heroList.size()-1, preBattleIndex);
			RoleHero onHero = GameContext.getUserHeroApp().getRoleHero(roleId, heroList.get(preBattleIndex));
			this.onBattle(role,onHero,battleHero,!isSystemUse);
            if(!Util.isElementSame(preHelpHeroSet,status.getHelpHeroSet())){
                //助威英雄发生了变化
                GameContext.getMessageCenter().sendSysMsg(role, this.getHeroSwitchUiMessage(role));
            }
		}else{
			role.getBehavior().notifyAttribute(); 
			//告知客户端更新切换ui栏
			GameContext.getMessageCenter().sendSysMsg(role, this.getHeroSwitchUiMessage(role));
			this.onHeroSwitchListChanged(role, preHeroSet, status.getSwitchHeroSet(),
					preHelpHeroSet,status.getHelpHeroSet());
		}
		
		result.success();
		return result ;
	}
	
	@Override
	public Message getHeroMainUiMessage(RoleInstance role) {
		C1271_HeroMainUiRespMessage respMsg = new C1271_HeroMainUiRespMessage();
		RoleHero onBattleHero = GameContext.getUserHeroApp()
				.getOnBattleRoleHero(role.getRoleId());
		// 外形资源
		GoodsHero gb = GameContext.getGoodsApp().getGoodsTemplate(
				GoodsHero.class, onBattleHero.getHeroId());
		respMsg.setResId((short) gb.getResId());
		respMsg.setHeroHeadId(gb.getHeadId());
		// 技能
		respMsg.setSkillItems(GameContext.getSkillApp().getRoleSkillItem(role, onBattleHero.getSkillMap().values()));
		respMsg.setHeroId(onBattleHero.getHeroId());
		respMsg.setResRate(gb.getResRate());
 		respMsg.setSwitchHeroList(this.getSwitchableHeroInfoList(role.getRoleId()));
		//respMsg.setMaxSwitchNum(this.getMaxSwitchableHeroNum(role));
		//respMsg.setMaxHelpNum(this.getMaxHelpHeroNum(role.getLevel()));
		respMsg.setHpHealth(this.heroBaseConfig.getHpHealthRate());
		return respMsg;
	}
	
	/**
	 * 出战英雄全满血满蓝
	 * @param role
	 */
	@Override
	public void switchableHeroPerfectBody(RoleInstance role){
		List<RoleHero> heroList = this.onlineSwitchHeros(role.getRoleId()) ;
		if(Util.isEmpty(heroList)){
			return ;
		}
		for(RoleHero hero : heroList){
			hero.setHpRate(RoleHero.HP_RATE_FULL);
		}
		role.getBehavior().sendMessage(this.getHeroSwitchUiMessage(role));
	}
	
	@Override
	public Message getHeroSwitchUiMessage(RoleInstance role) {
		C1272_HeroSwitchUiRespMessage respMsg = new C1272_HeroSwitchUiRespMessage();
		respMsg.setSwitchHeroList(this.getSwitchableHeroInfoList(role.getRoleId()));
		respMsg.setHelpHeroList(this.getHelpHeroInfoList(role.getRoleId()));
		return respMsg ;
	}
	
	@Override
	public short getOnBattleCd(RoleInstance role){
		RoleHeroStatus status = GameContext.getUserHeroApp().getRoleHeroStatus(role.getRoleId());
		if(0 >= status.getLastSwitchTime()){
			return 0 ;
		}
		int time = (int)((System.currentTimeMillis()-status.getLastSwitchTime())/1000);
		int cd = this.heroBaseConfig.getSwitchCd() ;
		if(time < 0 || time >= cd){
			return 0 ;
		}
		return (short)(cd-time) ;
	}
	
	
	private void setOnBattleCd(String roleId){
		RoleHeroStatus status = GameContext.getUserHeroApp().getRoleHeroStatus(roleId);
		status.setLastSwitchTime(System.currentTimeMillis());
	}
	
	@Override
	public byte getMaxSwitchableHeroNum(RoleInstance role){
		return this.getMaxSwitchableHeroNum(role.getLevel());
	}
	
	private byte getMaxSwitchableHeroNum(int level){
		HeroNumRoleLevel config = this.heroNumRoleLevelMap.get(level);
		if(null != config){
			return config.getHeroNum() ;
		}
		int size = this.heroNumRoleLevelMap.size();
		if(level < size){
			return 1 ;
		}
		config = this.heroNumRoleLevelMap.get(size);
		return (null == config)? 1 : config.getHeroNum();
	}

	private byte getMaxHelpHeroNum(int level){
		HeroNumRoleLevel config = this.heroNumRoleLevelMap.get(level);
		if(null != config){
			return config.getHelpHeroNum();
		}
		int size = this.heroNumRoleLevelMap.size();
		if(level < size){
			return 0 ;
		}
		config = this.heroNumRoleLevelMap.get(size);
		return (null == config)? 0 : config.getHelpHeroNum() ;
	}
	
	@Override
	public List<HeroLoveItem> getHeroLoveItemList(String roleId,int heroId) {
		RoleHero hero = GameContext.getUserHeroApp().getRoleHero(roleId, heroId);
		if(null == hero){
			return this.getHeroLoveItemList(heroId);
		}
		List<HeroLoveItem> list = Lists.newArrayList();
		List<HeroLove> loveList = this.getHeroLoveList(heroId) ;
		if(Util.isEmpty(loveList)){
			return list ;
		}
		for(HeroLove love : loveList){
			HeroLoveItem item = new HeroLoveItem();
			HeroLoveStatus status = this.getHeroLoveStatus(hero, love) ;
			item.setLoveStatus(status.getStatus());
			item.setLoveName(love.getLoveName());
			HeroLoveAttribute att = this.getHeroLoveAttribute(love.getLoveId(), status.getQuality(), 
					status.getStar()) ;
			if(null == att){
				att = this.getHeroLoveAttribute(love.getLoveId(), love.getMinQuality(), 
						love.getMinStar()); 
			}
			if(null == att){
				item.setDesc(love.getDesc());
			}else{
				item.setDesc(att.getDesc());
			}
			list.add(item);
		}
		return list ;
	}
	
	@Override
	public List<HeroLoveItem> getHeroLoveItemList(int heroId) {
		List<HeroLoveItem> list = Lists.newArrayList();
		List<HeroLove> loveList = this.getHeroLoveList(heroId) ;
		if(Util.isEmpty(loveList)){
			return list ;
		}
		for(HeroLove love : loveList){
			HeroLoveItem item = new HeroLoveItem();
			item.setLoveStatus(HeroLoveStatus.un_activated);
			item.setLoveName(love.getLoveName());
			HeroLoveAttribute att = this.getHeroLoveAttribute(love.getLoveId(), 
					love.getMinQuality(), love.getMinStar()) ;
			if(null != att){
				item.setDesc(att.getDesc());
			}else{
				item.setDesc(love.getDesc());
			}
			list.add(item);
		}
		return list ;
	}
	
	@Override
	public List<RoleHero> getRoleSwitchableHeroList(String roleId) {
		boolean isOnline = GameContext.getOnlineCenter().isOnlineByRoleId(roleId);
		if(isOnline){
			return this.onlineSwitchHeros(roleId);
		}
		//不在线情况
		return GameContext.getHeroStorage().getRoleHeros(roleId);
	}
	
	@Override
	public List<RoleHero> helpHeros(String roleId) {
		List<RoleHero> list = Lists.newArrayList();
		RoleHeroStatus status = GameContext.getUserHeroApp().getRoleHeroStatus(
				roleId);
		for (int heroId : status.getHelpHeroSet()) {
			RoleHero rh = GameContext.getUserHeroApp().getRoleHero(roleId,
					heroId);
			if (null == rh) {
				continue;
			}
			list.add(rh);
		}
		return list;
	}
	
	
	private List<RoleHero> onlineSwitchHeros(String roleId) {
		List<RoleHero> list = Lists.newArrayList();
		RoleHeroStatus status = GameContext.getUserHeroApp().getRoleHeroStatus(
				roleId);
		for (int heroId : status.getSwitchHeroSet()) {
			RoleHero rh = GameContext.getUserHeroApp().getRoleHero(roleId,
					heroId);
			if (null == rh) {
				continue;
			}
			list.add(rh);
			/*if (heroId != status.getBattleHeroId() || list.isEmpty()) {
				list.add(rh);
				continue;
			}
			// 当前出战的排第一位置
			list.add(0, rh);*/
		}
		return list;
	}
	
	@Override
	public boolean isSwitchableHero(String roleId,int heroId){
		RoleHeroStatus status = GameContext.getUserHeroApp().getRoleHeroStatus(roleId);
		if(null == status){
			return false ;
		}
		return status.getSwitchHeroSet().contains(heroId);
	}

	@Override
	public int getOnBattleHeroBattleScore(String roleId) {
		RoleHero onHero = GameContext.getUserHeroApp().getOnBattleRoleHero(roleId);
		if(null == onHero) {
			return 0;
		}
		return this.getBattleScore(onHero);
	}

	@Override
	public int getHeroLevelNum(String roleId, int level) {
		Collection<RoleHero> heros = GameContext.getUserHeroApp().getAllRoleHero(roleId);
		if(Util.isEmpty(heros)) {
			return 0;
		}
		int num = 0;
		for(RoleHero hero : heros) {
			if(hero.getLevel() < level) {
				continue;
			}
			num++;
		}
		return num;
	}

	@Override
	public int getRoleHeroNum(String roleId) {
		Collection<RoleHero> heros = GameContext.getUserHeroApp().getAllRoleHero(roleId);
		if(Util.isEmpty(heros)) {
			return 0;
		}
		return heros.size();
	}

	@Override
	public int getHeroQualityStarNum(String roleId, byte quality, byte star) {
		Collection<RoleHero> heros = GameContext.getUserHeroApp().getAllRoleHero(roleId);
		if(Util.isEmpty(heros)) {
			return 0;
		}
		int num = 0;
		for(RoleHero hero : heros) {
			if(hero.getQuality() < quality) {
				continue;
			}
			if(hero.getQuality() == quality && hero.getStar() < star) {
				continue;
			}
			num++;
		}
		return num;
	}
	
	@Override
	public int getEquipStrengthenLevel(String roleId,int level){
		Collection<HeroEquipBackpack> list = GameContext.getUserHeroApp().getEquipBackpack(roleId);
		if(Util.isEmpty(list)) {
			return 0;
		}
		int total = 0 ;
		for(HeroEquipBackpack pack : list){
			total += pack.totalEffectStrengthenLevel(level);
		}
		return total ;
	}
	
	@Override
	public int getEquipMosaicLevel(String roleId,int level){
		Collection<HeroEquipBackpack> list = GameContext.getUserHeroApp().getEquipBackpack(roleId);
		if(Util.isEmpty(list)) {
			return 0;
		}
		int total = 0 ;
		for(HeroEquipBackpack pack : list){
			total += pack.totalEffectMosaicLevel(level) ;
		}
		return total ;
	}
	
	@Override
	public int getEquipQualityNum(String roleId,int quality){
		Collection<HeroEquipBackpack> list = GameContext.getUserHeroApp().getEquipBackpack(roleId);
		if(Util.isEmpty(list)) {
			return 0;
		}
		int total = 0 ;
		for(HeroEquipBackpack pack : list){
			total += pack.totalEquipQuality(quality) ;
		}
		return total ;
	}
	
	@Override
	public int getSkillLevelNum(String roleId, int level) {
		Collection<RoleHero> heros = GameContext.getUserHeroApp()
				.getAllRoleHero(roleId);
		if (Util.isEmpty(heros)) {
			return 0;
		}
		int num = 0;
		for (RoleHero hero : heros) {
			for (RoleSkillStat skillStat : hero.getSkillMap().values()) {
				if (skillStat.getSkillLevel() < level) {
					continue;
				}
				num++;
			}
		}
		return num;
	}

	@Override
	public HeroEquip getHeroEquipCache(String roleId) {
		return GameContext.getHeroStorage().getHeroEquip(roleId);
	}
	
	@Override
	public List<RoleGoods> getAllHeroEquipList(String roleId){
		List<RoleGoods> list = Lists.newArrayList() ;
		Collection<HeroEquipBackpack> packList = GameContext.getUserHeroApp().getEquipBackpack(roleId);
		if(Util.isEmpty(packList)){
			return list ;
		}
		for(HeroEquipBackpack pack : packList){
			for(RoleGoods rg : pack.getGrids()){
				if(null == rg){
					continue ;
				}
				list.add(rg);
			}
		}
		return list ;
	}
	

	@Override
	public void onHorseStarChanged(int roleId, int horseId,int quality,int star,int preQuality,int preStar) {
		this.onTargetStarChanged(roleId, horseId, quality, star, preQuality,preStar,HeroLoveType.horse.getType());
	}
	
	@Override
	public void onPetStarChanged(int roleId, int petId,int quality,int star,int preQuality,int preStar) {
		this.onTargetStarChanged(roleId, petId, quality, star,preQuality,preStar, HeroLoveType.pet.getType());
	}
	
	private void onHeroSwitchListChanged(RoleInstance role,Set<Integer> preHeroSet,
										 Set<Integer> currentHeroSet,Set<Integer> preHelpHeroSet,
										 Set<Integer> currentHelpHeroSet) {
		try {
			RoleHero hero = GameContext.getUserHeroApp().getOnBattleRoleHero(
					role.getRoleId());
			if (null == hero) {
				return;
			}
			List<HeroLove> loveList = this.getHeroLoveList(hero.getHeroId());
			if(Util.isEmpty(loveList)){
				return ;
			}
			for(HeroLove love : loveList){
				if(love.getLoveType() != HeroLoveType.hero.getType()){
					continue ;
				}
				HeroLoveStatus preStatus = this.getHeroLoveStatus4Hero(
						role.getRoleId(), love, preHeroSet,preHelpHeroSet);
				HeroLoveStatus currentStatus = this.getHeroLoveStatus4Hero(
						role.getRoleId(), love, currentHeroSet,currentHelpHeroSet);
				if (preStatus.getStatus() == currentStatus.getStatus()
						&& preStatus.equal(currentStatus.getQuality(),
								currentStatus.getStar())) {
					// 属性没有变化
					return;
				}
				HeroLoveAttribute pre = null;
				HeroLoveAttribute current = null;
				if (HeroLoveStatus.activated == preStatus.getStatus()) {
					pre = this.getHeroLoveAttribute(love.getLoveId(),
							preStatus.getQuality(), preStatus.getStar());
				}
				if (HeroLoveStatus.activated == currentStatus.getStatus()) {
					current = this.getHeroLoveAttribute(love.getLoveId(),
							currentStatus.getQuality(), currentStatus.getStar());
				}
				this.changeAttribute(role, pre, current);
			}
			
		} catch (Exception ex) {
			logger.error("", ex);
		}
	}
	
	private HeroLoveStatus getHeroLoveStatus4Hero(String roleId,
			HeroLove heroLove, Set<Integer>... sets) {
		HeroLoveStatus ret = new HeroLoveStatus();
		ret.unActivated();
		for (int id : heroLove.getIdSet()) {
			if (!Util.inSet(id, sets)) {
				// 没有激活
				ret.unActivated();
				return ret;
			}
			RoleHero hero = GameContext.getUserHeroApp()
					.getRoleHero(roleId, id);
			if (null == hero) {
				continue;
			}
			this.dealLoveStatus(ret, hero.getQuality(), hero.getStar(),
					hero.getHeroId());
		}
		return ret;
	}

	

	private void onTargetStarChanged(int roleId,int targetId, int quality, 
			int star,int preQuality,int preStar,byte loveType) {
		RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(String.valueOf(roleId));
		if(null == role){
			return ;
		}
		// 获得当前出战英雄
		RoleHero hero = GameContext.getUserHeroApp().getOnBattleRoleHero(
				role.getRoleId());
		if (null == hero) {
			return;
		}
		int heroId = hero.getHeroId();
		// 获得此英雄的情缘
		List<HeroLove> loveList = this.getHeroLoveList(heroId);
		if (Util.isEmpty(loveList)) {
			return;
		}
		for (HeroLove love : loveList) {
			if (loveType != love.getLoveType() || !love.containId(targetId)) {
				continue;
			}
			HeroLoveStatus curr = this.getHeroLoveStatus(hero, love);
			if(HeroLoveStatus.activated != curr.getStatus()){
				//当前未激活
				continue ;
			}
			HeroLoveStatus pre = this.getHeroLoveStatus(hero, love,
					null,null,new HeroLoveTarget(targetId,preQuality,preStar));
			HeroLoveAttribute preAttr = null ;
			if(HeroLoveStatus.activated != pre.getStatus()){
				preAttr = this.getHeroLoveAttribute(love.getLoveId(), pre.getQuality(), pre.getStar());
			}
			this.changeAttribute(role, 
					preAttr,
					this.getHeroLoveAttribute(love.getLoveId(), curr.getQuality(), curr.getStar()));
		}
	}
	
	@Override
	public void onHorseAdded(int roleId, int horseId, int quality, int star) {
		this.onTargetAdded(roleId, horseId, quality, star, HeroLoveType.horse.getType());
	}

	@Override
	public void onPetAdded(int roleId, int petId, int quality, int star) {
		this.onTargetAdded(roleId, petId, quality, star, HeroLoveType.pet.getType());
	}

	@Override
	public void onPetRemoved(int roleId, int petId,int quality,int star) {
		RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(String.valueOf(roleId));
		if(null == role){
			return ;
		}
		// 获得当前出战英雄
		RoleHero hero = GameContext.getUserHeroApp().getOnBattleRoleHero(
				role.getRoleId());
		if (null == hero) {
			return;
		}
		int heroId = hero.getHeroId();
		//获得此英雄的情缘
		List<HeroLove> loveList = this.getHeroLoveList(heroId);
		if(Util.isEmpty(loveList)){
			return ;
		}
		for(HeroLove love : loveList){
			if(HeroLoveType.pet.getType() != love.getLoveType()
					|| !love.containId(petId)){
				continue ;
			}
			HeroLoveStatus pre = this.getHeroLoveStatus(hero, love,
					new HeroLoveTarget(petId,quality,star),null,null);
			if(HeroLoveStatus.activated != pre.getStatus()){
				//删除前就没有激活
				return ;
			}
			HeroLoveStatus curr = this.getHeroLoveStatus(hero, love);
			if(HeroLoveStatus.activated != curr.getStatus()){
				//当前也未激活
				return ;
			}
			this.changeAttribute(role, 
					this.getHeroLoveAttribute(love.getLoveId(), pre.getQuality(), pre.getStar()),
					this.getHeroLoveAttribute(love.getLoveId(), curr.getQuality(), curr.getStar()));
		}
	}
	
	private void onTargetAdded(int roleId,int targetId,int quality,int star,byte loveType){
		RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(String.valueOf(roleId));
		if(null == role){
			return ;
		}
		// 获得当前出战英雄
		RoleHero hero = GameContext.getUserHeroApp().getOnBattleRoleHero(
				role.getRoleId());
		if (null == hero) {
			return;
		}
		int heroId = hero.getHeroId();
		//获得此英雄的情缘
		List<HeroLove> loveList = this.getHeroLoveList(heroId);
		if(Util.isEmpty(loveList)){
			return ;
		}
		for(HeroLove love : loveList){
			if(loveType != love.getLoveType() 
					|| !love.containId(targetId)){
				continue ;
			}
			HeroLoveStatus curr = this.getHeroLoveStatus(hero, love);
			if(HeroLoveStatus.activated != curr.getStatus()){
				//当前未激活
				continue ;
			}
			HeroLoveStatus pre = this.getHeroLoveStatus(hero, love,
					null,new HeroLoveTarget(targetId,quality,star),null);
			HeroLoveAttribute preAttr = null ;
			if(HeroLoveStatus.activated != pre.getStatus()){
				preAttr = this.getHeroLoveAttribute(love.getLoveId(), pre.getQuality(), pre.getStar());
			}
			this.changeAttribute(role, 
					preAttr,
					this.getHeroLoveAttribute(love.getLoveId(), curr.getQuality(), curr.getStar()));
		}
	}
	
	private void changeAttribute(RoleInstance role,HeroLoveAttribute pre,HeroLoveAttribute current){
		AttriBuffer buffer = AttriBuffer.createAttriBuffer();
		if(null != pre){
			buffer.append(pre.getAttriItemList());
			buffer.reverse();
		}
		if(null != current){
			buffer.append(current.getAttriItemList());
		}
		if (buffer.isEmpty()) {
			return;
		}
		GameContext.getUserAttributeApp().changeAttribute(role, buffer);
		role.getBehavior().notifyAttribute();
	}
	
	@Override
	public int getAstaff(String roleId,int heroId){
		RoleHero hero = GameContext.getUserHeroApp().getRoleHero(roleId, heroId);
		if(null == hero){
			return 0 ;
		}
		HeroQualityUpgrade upgrade = GameContext.getHeroApp().getHeroQualityUpgrade(hero.getQuality(), hero.getStar());
		if(null == upgrade){
			return 0 ;
		}
		return upgrade.getAstaff() ;
	}

	@Override
	public void hpHealth(RoleInstance role) {
		if (null == role || null == this.heroBaseConfig) {
			return;
		}
		RoleHeroStatus status = GameContext.getUserHeroApp().getRoleHeroStatus(
				role.getRoleId());
		for (int heroId : status.getSwitchHeroSet()) {
			RoleHero rh = GameContext.getUserHeroApp().getRoleHero(
					role.getRoleId(), heroId);
			if (null == rh) {
				continue;
			}
			if (heroId == status.getBattleHeroId()
					|| rh.getOnBattle() == ON_BATTLE) {
				// 当前出战的不恢复hp
				continue;
			}
			rh.setHpRate((short) Math.min(
					rh.getHpRate() + this.heroBaseConfig.getHpHealthRate(),
					RoleHero.HP_RATE_FULL));
		}
	}

	@Override
	public void pushHeroEquipOpenCond(RoleInstance role){
		C1281_HeroEquipOpenCondRespMessage respMsg = new C1281_HeroEquipOpenCondRespMessage();
		List<HeroEquipOpenCondItem> list = Lists.newArrayList() ;
		byte index = 0 ;
		for(Integer q : GameContext.getEquipApp().getEquipOpenCondList()){
			HeroEquipOpenCondItem item = new HeroEquipOpenCondItem() ;
			item.setEquipPos(index++);
			item.setQuality(q.byteValue());
			list.add(item);
		}
		respMsg.setCondList(list);
		role.getBehavior().sendMessage(respMsg);
	}
	
	@Override
	public void pushHeroMusicList(RoleInstance role) {
		List<Integer> heroIdList = this.getHeroIdentifyList() ;
		if(Util.isEmpty(heroIdList)){
			return ;
		}
		
		List<HeroMusicItem> musicList = Lists.newArrayList() ;
		for(int heroId : heroIdList){
			GoodsHero hero = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class, heroId);
			if(null == hero){
				continue ;
			}
			HeroMusicItem musicItem = new HeroMusicItem();
			musicItem.setHeroId(heroId);
			musicItem.setMusicId(hero.getMusicId());
			musicList.add(musicItem);
		}
		C1253_HeroMusicListRespMessage respMsg = new C1253_HeroMusicListRespMessage();
		respMsg.setMusicList(musicList);
		role.getBehavior().sendMessage(respMsg);
	}
	
	
}
