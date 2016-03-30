package com.game.draco.app.hero;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.app.goods.GoodsHelper;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.HeroEquipBackpack;
import sacred.alliance.magic.app.goods.RoleGoodsHelper;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.app.goods.behavior.result.UseResult;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.ParasConstant;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsHero;
import sacred.alliance.magic.domain.GoodsHeroAid;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.goddess.domain.RoleGoddess;
import com.game.draco.app.hero.config.AttributeHeroLevelRate;
import com.game.draco.app.hero.config.AttributeHeroRate;
import com.game.draco.app.hero.config.AttributeQualityRate;
import com.game.draco.app.hero.config.AttributeTypeRate;
import com.game.draco.app.hero.config.HeroAttribute;
import com.game.draco.app.hero.config.HeroBaseConfig;
import com.game.draco.app.hero.config.HeroEquipOpen;
import com.game.draco.app.hero.config.HeroLevelup;
import com.game.draco.app.hero.config.HeroLove;
import com.game.draco.app.hero.config.HeroLuck;
import com.game.draco.app.hero.config.HeroLuckGoods;
import com.game.draco.app.hero.config.HeroLuckGoodsConfig;
import com.game.draco.app.hero.config.HeroQualityUpgrade;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.app.hero.domain.RoleHeroStatus;
import com.game.draco.app.hero.vo.HeroQualityUpgradeResult;
import com.game.draco.app.hero.vo.HeroSwallowResult;
import com.game.draco.app.hero.vo.LuckLotteryResult;
import com.game.draco.app.horse.domain.RoleHorse;
import com.game.draco.app.mail.type.MailSendRoleType;
import com.game.draco.app.skill.domain.RoleSkillStat;
import com.game.draco.message.item.AttriTypeStrValueItem;
import com.game.draco.message.item.HeroInfoItem;
import com.game.draco.message.item.HeroLuckGoodsItem;
import com.game.draco.message.item.HeroLuckPanelItem;
import com.game.draco.message.item.HeroMarkingItem;
import com.game.draco.message.item.HeroSwallowItem;
import com.game.draco.message.request.C1251_HeroGoodsToShadowReqMessage;
import com.game.draco.message.response.C1264_HeroLuckPanelRespMessage;
import com.game.draco.message.response.C1268_HeroQualityInfoRespMessage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class HeroAppImpl implements HeroApp {

	private final byte SUCCESS_NOT_INCR_LEVEL = (byte)1 ;
	private final byte SUCCESS_AND_INCR_LEVEL = (byte)2 ;
	private final byte MAX_HERO_STAR = (byte)3 ;
	private final short HERO_GOODS_TO_SHADOW_CMDID = new C1251_HeroGoodsToShadowReqMessage().getCommandId();
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Getter
	@Setter
	private List<AttributeType> attributeTypeList = Lists.newArrayList(
			AttributeType.maxHP, AttributeType.maxMP, AttributeType.atk,
			AttributeType.rit, AttributeType.critAtk, AttributeType.critRit,
			AttributeType.hit, AttributeType.dodge);
	
	@Getter @Setter private HeroBaseConfig heroBaseConfig ;
	@Getter @Setter private AttributeTypeRate attributeTypeRate ;
	@Getter @Setter private Map<String,HeroLevelup> heroLevelupMap = null ;
	@Getter @Setter private Map<String,HeroEquipOpen> heroEquipOpenMap = null ;
	@Getter @Setter private Map<String,HeroLuck> heroLuckMap = null ;
	@Getter @Setter private Map<String,HeroLuckGoodsConfig> luckTypeConfigMap = null ;
	@Getter @Setter private Map<String,HeroLuckGoodsConfig> firstLuckTypeConfigMap = null ;
	@Getter @Setter private Map<String,Integer> maxLevelMap = null ;
	@Getter @Setter private List<Integer> heroExchangeList = null ;
	@Getter @Setter private List<Integer> heroLuckShowList = null ;
	@Getter @Setter private Map<String,HeroLove> heroLoveMap = null ;
	@Getter @Setter private Map<String,AttributeHeroRate> attributeHeroRateMap = null ;
	@Getter @Setter private Map<String,AttributeQualityRate> attributeQualityRateMap = null ;
	@Getter @Setter private Map<String,AttributeHeroLevelRate> attributeHeroLevelRateMap = null ;
	@Getter @Setter private Map<String,HeroQualityUpgrade> heroQualityUpgradeMap = null ;
	
	@Override
	public HeroQualityUpgrade getHeroQualityUpgrade(int quality,int star){
		String key = quality + "_" + star ;
		return this.fromMap(heroQualityUpgradeMap, key);
	}
	
	private AttributeQualityRate getAttributeQualityRate(int quality,int star){
		String key = quality + "_" + star ;
		return this.fromMap(attributeQualityRateMap, key);
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
	public HeroEquipOpen getHeroEquipOpen(int equipPosId) {
		String key = String.valueOf(equipPosId);
		return this.fromMap(this.heroEquipOpenMap, key) ;
	}

	@Override
	public HeroLuck getHeroLuck(int luckTypeId) {
		String key = String.valueOf(luckTypeId) ;
		return this.fromMap(this.heroLuckMap, key) ;
	}

	@Override
	public HeroLuckGoodsConfig getHeroLuckGoodsConfig(int luckTypeId,
			boolean first) {
		String key = String.valueOf(luckTypeId);
		return this.fromMap(first?this.firstLuckTypeConfigMap:this.luckTypeConfigMap, key) ;
	}
	
	@Override
	public int getBattleScore(RoleHero roleHero){
		return GameContext.getAttriApp().getAttriBattleScore(
				this.getHeroAttriBuffer(roleHero));
	}
	
	@Override
	public void saveRoleHero(RoleHero roleHero){
		//!!!
		this.preToStore(roleHero);
		GameContext.getBaseDAO().update(roleHero);
	}
	
	
	private void heroLevelupEffect(RoleInstance role,int oldLevel,RoleHero roleHero){
		try {
			if (1 != roleHero.getOnBattle() || oldLevel == roleHero.getLevel()) {
				// 未出战或等级未变化
				return;
			}
			AttriBuffer buffer = this.getHeroAttriBuffer(roleHero).append(
					this.getHeroGivenAttriBuffer(roleHero, oldLevel,null,
							0,roleHero.getQuality(),roleHero.getStar()).reverse());
			GameContext.getUserAttributeApp().changeAttribute(role, buffer);
			role.getBehavior().notifyAttribute();
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
			return ;
		}
		//初始化物品
		GoodsHeroAid valorGoodsBase = GameContext.getGoodsApp().getGoodsTemplate(
				GoodsHeroAid.class, heroBaseConfig.getValorGoodsId());
		GoodsHeroAid justiceGoodsBase = GameContext.getGoodsApp().getGoodsTemplate(
				GoodsHeroAid.class, heroBaseConfig.getJusticeGoodsId());
		heroBaseConfig.setValorGoodsBase(valorGoodsBase);
		heroBaseConfig.setJusticeGoodsBase(justiceGoodsBase);
		if(null == valorGoodsBase || null == justiceGoodsBase){
			Log4jManager.CHECK.error("the marking goodsId is error in the heroBaseConfig,file=" + sourceFile + " sheet=" + sheetName);
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
	
	private void loadHeroExchange(){
		this.heroExchangeList = this.loadHeroIdList(XlsSheetNameType.hero_exchange) ;
	}
	
	private void loadHeroLuckShow(){
		this.heroLuckShowList = this.loadHeroIdList(XlsSheetNameType.hero_luck_show) ;
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
		this.heroLoveMap = map ;
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
		}
		list.clear();
		list = null ;
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
	
	private void loadHeroEquipOpen(){
		String fileName = XlsSheetNameType.hero_equip_open.getXlsName();
		String sheetName = XlsSheetNameType.hero_equip_open.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		heroEquipOpenMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, HeroEquipOpen.class);
		if(Util.isEmpty(heroEquipOpenMap)){
			Log4jManager.CHECK.error("not config the heroEquipOpenMap,file=" + sourceFile + " sheet=" + sheetName);
			Log4jManager.checkFail();
			return ;
		}
		for(HeroEquipOpen heo : heroEquipOpenMap.values()){
			if(heo.isFree()){
				continue ;
			}
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(heo.getGoodsId());
			if (null == gb) {
				Log4jManager.CHECK
						.error("HeroEquipOpen config error,goods not exist,goodsId="
								+ heo.getGoodsId()
								+ ",file="
								+ sourceFile
								+ " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
		}
	}
	
	private void loadHeroLuck(){
		String fileName = XlsSheetNameType.hero_luck.getXlsName();
		String sheetName = XlsSheetNameType.hero_luck.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		this.heroLuckMap = XlsPojoUtil.sheetToGenericLinkedMap(sourceFile, sheetName, HeroLuck.class);
		if(Util.isEmpty(heroLuckMap)){
			Log4jManager.CHECK.error("not config the heroLuckMap,file=" + sourceFile + " sheet=" + sheetName);
			Log4jManager.checkFail();
		}
	}
	
	
	private void loadHeroLuckGoods(boolean first){
		if(first){
			this.firstLuckTypeConfigMap = this.loadHeroLuckGoods(XlsSheetNameType.hero_luck_firstfee_goods.getXlsName(),
					XlsSheetNameType.hero_luck_firstfee_goods.getSheetName());
			return ;
		}
		this.luckTypeConfigMap = this.loadHeroLuckGoods(XlsSheetNameType.hero_luck_goods.getXlsName(),
				XlsSheetNameType.hero_luck_goods.getSheetName());
	}
	
	private Map<String,HeroLuckGoodsConfig> loadHeroLuckGoods(String xlsName,String sheetName){
		String sourceFile = GameContext.getPathConfig().getXlsPath() + xlsName;
		List<HeroLuckGoods> allList = XlsPojoUtil.sheetToList(sourceFile, sheetName, HeroLuckGoods.class);
		if(Util.isEmpty(allList)){
			Log4jManager.CHECK.error("not config the HeroLuckGoods,file=" + sourceFile + " sheet=" + sheetName);
			Log4jManager.checkFail();
			return null ;
		}
		Map<String,HeroLuckGoodsConfig> retMap = Maps.newHashMap();
		for(HeroLuckGoods hlg: allList){
			String type = String.valueOf(hlg.getTypeId()) ;
			HeroLuckGoodsConfig config = retMap.get(type);
			if(null == config){
				config = new HeroLuckGoodsConfig();
				retMap.put(type, config);
			}
			if(config.exist(hlg)){
				Log4jManager.CHECK.error("HeroLuckGoods config error goods had exist,goodsId=" 
						+ hlg.getGoodsId() +" typeId=" + type +" file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
				continue ;
			}
			if(!GameContext.getGoodsApp().isExistGoods(hlg.getGoodsId())){
				Log4jManager.CHECK.error("HeroLuckGoods config error goods not exist ,goodsId=" 
						+ hlg.getGoodsId() +" typeId=" + type +" file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
			config.add(hlg);
		}
		for(HeroLuckGoodsConfig config : retMap.values()){
			config.init();
		}
		return retMap ;
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
		this.loadHeroLevelup();
		this.loadHeroEquipOpen();
		this.loadHeroLuck();
		this.loadHeroLuckGoods(false);
		this.loadHeroLuckGoods(true);
		this.loadHeroExchange();
		this.loadHeroLuckShow();
		this.loadHeroLove();
		this.loadHeroQualityUpgrade();
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

	@Override
	public Result onBattle(RoleInstance role, int heroId) {
		Result result = new Result();
		RoleHero hero = GameContext.getUserHeroApp().getRoleHero(role.getRoleId(), heroId);
		if(null == hero){
			//提示参数错误
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result ;
		}
		//判断当前英雄是否已经出战
		RoleHero oldOnHero = GameContext.getUserHeroApp().getOnBattleRoleHero(role.getRoleId());
		if(null != oldOnHero && hero.getHeroId() == oldOnHero.getHeroId()){
			//提示已经出战
			result.setInfo(GameContext.getI18n().getText(TextId.Hero_On_Battle_Now));
			return result ;
		}
		AttriBuffer oldHeroBuffer = null ;
		if(null != oldOnHero){
			//删除此英雄技能
			for(Short skillId : oldOnHero.getSkillMap().keySet()){
				role.delSkillStat(skillId);
			}
			oldHeroBuffer = this.getHeroAttriBuffer(oldOnHero);
		}
		//添加新英雄的技能
		role.getSkillMap().putAll(hero.getSkillMap());
		GameContext.getUserHeroApp().setOnBattleRoleHero(role.getRoleId(), hero);
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(hero.getHeroId());
		String name= (null== gb)?"":gb.getName();
		result.setInfo(GameContext.getI18n().messageFormat(TextId.Hero_on_battle_success,name));
		//修角色的武器资源ID
		//修改角色的外形资源ID
		if(null != gb){
			GoodsHero goodsHero = (GoodsHero)gb ;
			role.setEquipResId(goodsHero.getWeaponResId());
			role.setClothesResId(goodsHero.getResId());
		}
		
		result.success();
		try {
			// 计算属性差异
			AttriBuffer nowHeroBuffer = this.getHeroAttriBuffer(hero);
			if (null != oldHeroBuffer) {
				nowHeroBuffer.append(oldHeroBuffer.reverse());
			}
			GameContext.getUserAttributeApp().changeAttribute(role,
					nowHeroBuffer);
			role.getBehavior().notifyAttribute();
		}catch(Exception ex){
			logger.error("",ex);
		}
		return result;
	}
	
	
	private String skillIdLevelString(Map<Short,RoleSkillStat> map){
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
				stat.setRoleId(String.valueOf("HERO_") + hero.getHeroId());
				stat.setLastProcessTime(this.getLastProcessTimeFromStore(stat));
				skillMap.put(stat.getSkillId(), stat) ;
			}
			hero.setSkillMap(skillMap);
		}
		this.initSkill(hero);
	}
	
	private void initSkill(RoleHero hero) {
		// 普通攻击
		GoodsHero template = GameContext.getGoodsApp().getGoodsTemplate(
				GoodsHero.class, hero.getHeroId()) ;
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
	
	
	private void preToStore(RoleHero hero){
		//存储技能MAP
		hero.setSkills(this.skillIdLevelString(hero.getSkillMap()));
	}
	
	@Override
	public void login(RoleInstance role) {
		//加载英雄
		List<RoleHero> heros = GameContext.getBaseDAO().selectList(RoleHero.class, 
				RoleHero.ROLE_ID, role.getRoleId());
		for(RoleHero hero : heros){
			this.postFromStore(hero);
		}
		//获得状态
		RoleHeroStatus stauts = GameContext.getBaseDAO().selectEntity(RoleHeroStatus.class, 
				RoleHero.ROLE_ID, role.getRoleId()) ;
		this.heroListLoginAction(role, heros,stauts);
		//获得英雄的装备
		HeroEquipBackpack equippack = new HeroEquipBackpack(role,ParasConstant.HERO_EQUIP_MAX_NUM);
		GameContext.getUserHeroApp().initHeroEquipBackpack(role.getRoleId(), equippack);
	}
	
	

	private void saveRoleHeroStatus(RoleHeroStatus status){
		if(null == status){
			return ;
		}
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
		
		int battleHeroId = status.getBattleHeroId() ;
		
		if(!Util.isEmpty(heros)){
			//如果拥有英雄，但没有出战，系统自动设置为出战
			boolean haveOnBattle = false ;
			for(RoleHero hero: heros){
				GameContext.getUserHeroApp().addRoleHero(role.getRoleId(), hero);
				if(battleHeroId==hero.getHeroId()){
					haveOnBattle = true ;
					//出战的法宝
					GameContext.getUserHeroApp().setOnBattleRoleHero(role.getRoleId(), hero);
				}
			}
			if(!haveOnBattle){
				GameContext.getUserHeroApp().setOnBattleRoleHero(role.getRoleId(), heros.get(0));
			}
		}
		
	}

	@Override
	public void logout(RoleInstance role) {
		try {
			RoleHeroStatus status = GameContext.getUserHeroApp()
					.getRoleHeroStatus(role.getRoleId());
			this.saveRoleHeroStatus(status);
		}catch(Exception ex){
			logger.error("hero app saveRoleHeroStatus error,roleId=" + role.getRoleId(),ex);
		}
		//装备
		HeroEquipBackpack pack = GameContext.getUserHeroApp().getEquipBackpack(role.getRoleId());
		if(null != pack){
			try {
				pack.offline();
			} catch (Exception ex) {
				logger.error(
						"hero app save equip error,roleId="
								+ role.getRoleId() , ex);
			}
		}
		//情况内存中数据
		GameContext.getUserHeroApp().cleanHeroData(role.getRoleId());
	}
	
	
	private Result heroGoodsToShadow(RoleInstance role, 
			RoleGoods roleGoods,boolean confirm,GoodsHero goodsHero){
		if(confirm){
			//直接转换为影子
			List<GoodsOperateBean> addList = Lists.newArrayList(
					new GoodsOperateBean(goodsHero.getShadowId(),goodsHero.getShadowNum(),
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
					goodsHero.getName(),gb.getName(),String.valueOf(goodsHero.getShadowNum()));
			result.setInfo(tips);
			result.success();
			return result ;
		}
		//二次确认提示用户是否转换为影子
		UseResult result = new UseResult();
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsHero.getShadowId());
		String tips = GameContext.getI18n().messageFormat(TextId.Hero_goods_to_shadow_confirm_tips,
				goodsHero.getName(),gb.getName(),String.valueOf(goodsHero.getShadowNum()));
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
	
	private AttriBuffer getHeroLoveAttriBuffer(RoleHero hero,
			HeroLoveType givenLoveType,int givenTargetId){
		if(null == hero){
			return null ;
		}
		AttriBuffer buffer = AttriBuffer.createAttriBuffer();
		for(HeroLoveType hlt : HeroLoveType.values()){
			HeroLove love = this.getHeroLove(hero.getHeroId(), hlt.getType());
			if(null == love){
				continue ;
			}
			byte status = 0 ;
			if(null != givenLoveType && 
					hlt.getType() == givenLoveType.getType()){
				status = this.getHeroLoveStatus(hero, hlt.getType(),givenTargetId);
			}else{
				status = this.getHeroLoveStatus(hero, hlt.getType());
			}
			//是否激活
			if(HeroLoveStatus.activated.getType() != status){
				continue ;
			}
			buffer.append(love.getAttriItemList());
		}
		return buffer ;
	}

	@Override
	public RoleHero insertHeroDb(String roleId,GoodsHero goodsHero){
		RoleHero hero = new RoleHero();
		hero.setHeroId(goodsHero.getId());
		hero.setRoleId(roleId);
		hero.setQuality(goodsHero.getQualityType());
		hero.setStar(goodsHero.getBornStar());
		//添加技能
		this.initSkill(hero);
		//实时入库
		GameContext.getBaseDAO().insert(hero);
		return hero ;
	}
	
	@Override
	public Result useHeroTemplate(RoleInstance role, GoodsHero goodsHero) throws Exception {
		RoleHero hero = this.insertHeroDb(role.getRoleId(), goodsHero) ;
		GameContext.getUserHeroApp().addRoleHero(role.getRoleId(), hero);
		//没有出战主动出战
		/*RoleHero onBattleHero = GameContext.getUserHeroApp().getOnBattleRoleHero(role.getRoleId());
		if(null == onBattleHero){
			this.onBattle(role, goodsHero.getId());
		}*/
		return new Result().success();
	}
	
	@Override
	public AttriBuffer getBaseAttriBuffer(int heroId,int heroLevel,int quality,int star){
		HeroAttribute levelRate = this.getAttributeHeroLevelRate(heroLevel);
		HeroAttribute heroRate = this.getAttributeHeroRate(heroId) ;
		HeroAttribute qualityRate = this.getAttributeQualityRate(quality, star);
		AttriBuffer buffer = AttriBuffer.createAttriBuffer();
		for(AttributeType at : this.getAttributeTypeList()){
			byte attriType = at.getType();
			float value = this.getAttributeValue(attriType, this.attributeTypeRate)
					* this.getAttributeValue(attriType, levelRate)
					* this.getAttributeValue(attriType, heroRate)
					* this.getAttributeValue(attriType, qualityRate);
			buffer.append(at,Math.max(0, (int)value),false);
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
	public AttriBuffer getHeroGivenAttriBuffer(RoleHero hero, int givenHeroLevel,
			HeroLoveType givenLoveType, int givenLoveTargetId,
			int givenQuality, int givenStar) {
		AttriBuffer buffer = AttriBuffer.createAttriBuffer();
		// 基本
		buffer.append(this.getBaseAttriBuffer(hero.getHeroId(),
				givenHeroLevel, givenQuality, givenStar));
		
		// Marking
		buffer.append(this.getMarkingAttriBuffer(
				this.getMarkingNum(hero, MarkingType.valor.getType()),
				MarkingType.valor.getType()));
		buffer.append(this.getMarkingAttriBuffer(
				this.getMarkingNum(hero, MarkingType.justice.getType()),
				MarkingType.justice.getType()));
		//情缘
		buffer.append(this.getHeroLoveAttriBuffer(hero,givenLoveType,givenLoveTargetId));
		//!!!!!!!
		//处理加百分比的情况
		//!!!!!!!
		buffer.precToValue();
		// 装备(装备不算百分比)
		HeroEquipBackpack pack = GameContext.getUserHeroApp().getEquipBackpack(hero.getRoleId());
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
		return this.getHeroGivenAttriBuffer(hero, hero.getLevel(),null,0,hero.getQuality(),hero.getStar());
	}
	
	
	private AttriBuffer getMarkingAttriBuffer(int markingNum,byte markingType){
		if(markingNum <=0){
			return null ;
		}
		GoodsHeroAid goodsBase = this.getMarkingGoods(markingType);
		if(null == goodsBase){
			return null ;
		}
		AttriBuffer buffer = AttriBuffer.createAttriBuffer() ;
		buffer.append(goodsBase.getAttriItemList());
		if( 1 != markingNum){
			buffer.rate(markingNum);
		}
		return buffer ;
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

	@Override
	public boolean isEquipPosOpenOrFreeOpen(RoleInstance role,int pos){
		boolean isOpen = GameContext.getUserHeroApp().isOpenEquipStatus(role.getRoleId(), pos);
		if(isOpen){
			return true ;
		}
		//判断是否满足免费开启条件
		HeroEquipOpen heo = this.getHeroEquipOpen(pos);
		if(null == heo){
			//没有配置表示已经开启
			return true ;
		}
		if(role.getLevel() < heo.getRoleLevel()){
			return false ;
		}
		return heo.isFree() ;
	}
	
	@Override
	public Result openEquipPos(RoleInstance role,int pos) {
		Result result = new Result();
		if(pos <0 || pos > ParasConstant.HERO_EQUIP_MAX_NUM){
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result ;
		}
		if(this.isEquipPosOpenOrFreeOpen(role, pos)){
			//已开启
			result.setInfo(GameContext.getI18n().getText(TextId.Hero_equip_open_success));
			result.success();
			return result ;
		}
		HeroEquipOpen heo = this.getHeroEquipOpen(pos);
		if(role.getLevel() < heo.getRoleLevel()){
			result.setInfo(GameContext.getI18n().messageFormat(TextId.Hero_equip_open_lv_limit,
					String.valueOf(heo.getRoleLevel())));
			return result ;
		}
		//走快捷购买逻辑
		Result res = GameContext.getQuickBuyApp().doQuickBuy(role, heo.getGoodsId(),heo.getGoodsNum(),
				OutputConsumeType.hero_open_equip_pos, null);
		if(!res.isSuccess()){
			return res;
		}
		//设置开启状态
		RoleHeroStatus status = GameContext.getUserHeroApp().setOpenEquipStatus(role.getRoleId(), pos);
		//入库
		this.saveRoleHeroStatus(status);
		result.setInfo(GameContext.getI18n().getText(TextId.Hero_equip_open_success));
		result.success();
		return result ;
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
				RoleGoods rg = GameContext.getUserGoodsApp().getRoleGoods(role, StorageType.bag, id);
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
			
			RoleHero onHero = GameContext.getUserHeroApp().getOnBattleRoleHero(role.getRoleId());
			if(null != onHero && onHero.getHeroId() == swallowHero.getHeroId()){
				//当前出战的英雄不能吞噬
				result.setInfo(GameContext.getI18n().getText(TextId.Hero_swallow_onbattle_canot_self));
				return result ;
			}
			GoodsHero heroTemplate = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class,
					swallowHero.getHeroId());
			//判断英雄是否有印记
			if(swallowHero.getJusticeNum()>0 
					|| swallowHero.getValorNum()>0){
				String info = GameContext.getI18n().messageFormat(TextId.Hero_have_marking_canot_swallow,
						heroTemplate.getName(),String.valueOf(swallowHero.getLevel()));
				result.setInfo(info);
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
		int maxLevel = GameContext.getHeroApp().getMaxLevel(heroQuality);
		//将结果设置为失败
		result.failure();
		try {
			// 计算到达最大等级需要的经验
			int needMaxExp = this.reachMaxLevelNeedExp(roleHero) ;
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
				this.heroLevelupEffect(role,oldLevel, roleHero);
			}
		}
	}
	
	private void deleteSwallowHero(RoleHero roleHero){
		//删除数据库
		GameContext.getBaseDAO().delete(RoleHero.class, RoleHero.HERO_ID, roleHero.getHeroId(),
				RoleHero.ROLE_ID,roleHero.getRoleId());
		//删除内存
		GameContext.getUserHeroApp().deleteRoleHero(roleHero.getRoleId(), roleHero.getHeroId());
	}
	
	public HeroMarkingItem buildHeroMarkingItem(RoleHero hero,byte markingType) {
		boolean isValor = ( MarkingType.valor.getType() == markingType) ;
		HeroBaseConfig config = GameContext.getHeroApp().getHeroBaseConfig() ;
		
		int currNum = isValor?hero.getValorNum():hero.getJusticeNum() ;
		int goodsId = isValor?config.getValorGoodsId():config.getJusticeGoodsId();
		GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
		HeroLevelup lu = GameContext.getHeroApp().getHeroLevelup(goodsBase.getQualityType(), hero.getLevel());
		
		List<AttriTypeStrValueItem> retValueList = Lists.newArrayList();
		
		List<AttriItem> arriItemList = goodsBase.getAttriItemList();
		for(AttriItem ai : arriItemList){
			AttriTypeStrValueItem valueItem = new AttriTypeStrValueItem();
			valueItem.setType(ai.getAttriTypeValue());
			if(isValor){
				float value = ai.getValue()*currNum ;
				if(value <=0){
					continue ;
				}
				valueItem.setValue(String.valueOf((int)value));
			}else{
				double precValue = (double)(ai.getValue()*currNum*100);
				if(precValue <= 0){
					continue ;
				}
				valueItem.setValue(Util.doubleFormat(precValue)+ "%");  ;
			}
			retValueList.add(valueItem);
		}
		
		HeroMarkingItem item = new HeroMarkingItem();
		item.setMaxNum(isValor?(short)lu.getMaxValorNum():(short)lu.getMaxJusticeNum());
		item.setGoodsId(goodsId);
		item.setCurrNum((short)currNum);
		item.setMarkingType(markingType);
		item.setDesc(isValor?config.getValorDesc():config.getJusticeDesc());
		item.setArriList(retValueList);
		return item ;
	
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
	private int reachMaxLevelNeedExp(RoleHero roleHero){
		int quality = roleHero.getQuality() ;
		int currLv = roleHero.getLevel() ;
		int maxLv = this.getMaxLevel(quality);
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
	
	@Override
	public Result markingOn(RoleInstance role,int heroId,byte markingType){
		Result result = new Result() ;
		RoleHero roleHero = GameContext.getUserHeroApp().getRoleHero(role.getRoleId(), heroId);
		if(null == roleHero){
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result ;
		}
		// 获得模板ID
		HeroBaseConfig config = this.getHeroBaseConfig();
		if (null == config) {
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_DATA));
			return result;
		}
		int markingNum = this.getMarkingNum(roleHero, markingType);
		int maxNum = 0 ;
		//判断镶嵌数目是否达到上限
		HeroLevelup levelup = this.getHeroLevelup(roleHero.getQuality(), roleHero.getLevel());
		if(null != levelup){
			maxNum = (MarkingType.valor.getType() == markingType)?
					levelup.getMaxValorNum():levelup.getMaxJusticeNum() ;
		}
		if(markingNum >= maxNum){
			result.setInfo(GameContext.getI18n().getText(TextId.Hero_marking_have_max_num));
			return result;
		}
		//删除一印记
		int goodsId = this.getMarkingGoodsId(config, markingType);
		GoodsResult gr = GameContext.getUserGoodsApp().deleteForBag(role, goodsId, 1,
				OutputConsumeType.hero_marking_on_consume) ;
		if(!gr.isSuccess()){
			return gr ;
		}
		boolean onBattle = this.isOnBattleHero(roleHero) ;
		AttriBuffer preBuffer = null ;
		if(onBattle){
			preBuffer = this.getHeroAttriBuffer(roleHero);
		}
		this.incrMarkingNum(roleHero, markingType, 1);
		this.saveRoleHero(roleHero);
		if(onBattle){
			//重新算属性
			AttriBuffer buffer = this.getHeroAttriBuffer(roleHero);
			buffer.append(preBuffer.reverse());
			GameContext.getUserAttributeApp().changeAttribute(role, buffer);
			role.getBehavior().notifyAttribute();
		}
		result.success();
		return result ;
	}
	
	@Override
	public Result markingOff(RoleInstance role,int heroId,byte markingType){
		Result result = new Result() ;
		RoleHero roleHero = GameContext.getUserHeroApp().getRoleHero(role.getRoleId(), heroId);
		if(null == roleHero){
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result ;
		}
		// 获得模板ID
		HeroBaseConfig config = this.getHeroBaseConfig();
		if (null == config) {
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_DATA));
			return result ;
		}
		//获得当前印记数量
		int markingNum = this.getMarkingNum(roleHero, markingType);
		if(markingNum <=0){
			result.setInfo(GameContext.getI18n().getText(TextId.Hero_not_have_marking));
			return result ;
		}
		//先判断是否背包是否有空间
		GoodsResult gr = GameContext.getUserGoodsApp().addGoodsForBag(role, 
				this.getMarkingGoodsId(config, markingType), markingNum ,
				BindingType.already_binding, OutputConsumeType.hero_marking_off) ;
		if(!gr.isSuccess()){
			return gr ;
		}
		boolean onBattle = this.isOnBattleHero(roleHero) ;
		AttriBuffer preBuffer = null ;
		if(onBattle){
			preBuffer = this.getHeroAttriBuffer(roleHero);
		}
		this.cleanMarkingNum(roleHero, markingType);
		//保存
		this.saveRoleHero(roleHero);
		if(onBattle){
			//重新算属性
			AttriBuffer buffer = this.getHeroAttriBuffer(roleHero);
			buffer.append(preBuffer.reverse());
			GameContext.getUserAttributeApp().changeAttribute(role, buffer);
			role.getBehavior().notifyAttribute();
		}
		result.success();
		return result ;
	}
	
	private int getMarkingNum(RoleHero roleHero,byte markingType){
		if(MarkingType.valor.getType() == markingType){
			return roleHero.getValorNum() ;
		}
		return roleHero.getJusticeNum() ;
	}
	
	private void incrMarkingNum(RoleHero roleHero,byte markingType,int incrNum){
		if(MarkingType.valor.getType() == markingType){
			roleHero.setValorNum(roleHero.getValorNum()+incrNum);
			return ;
		}
		roleHero.setJusticeNum(roleHero.getJusticeNum()+incrNum);
	}
	
	private void cleanMarkingNum(RoleHero roleHero,byte markingType){
		if(MarkingType.valor.getType() == markingType){
			roleHero.setValorNum(0);
			return ;
		}
		roleHero.setJusticeNum(0);
	}
	
	private int getMarkingGoodsId(HeroBaseConfig config ,byte markingType){
		if(MarkingType.valor.getType() == markingType){
			return config.getValorGoodsId() ;
		}
		return config.getJusticeGoodsId() ;
	}
	
	private GoodsHeroAid getMarkingGoods(byte markingType){
		if(MarkingType.valor.getType() == markingType){
			return this.heroBaseConfig.getValorGoodsBase() ;
		}
		return this.heroBaseConfig.getJusticeGoodsBase() ;
	}
	
	private boolean isOnBattleHero(RoleHero roleHero){
		return (null != roleHero && 1 == roleHero.getOnBattle()) ;
	}
	
	@Override
	public Result heroExchange(RoleInstance role,int heroId){
		Result result = new Result();
		GoodsHero hero = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class, heroId);
		if(null == hero || null == this.heroExchangeList 
				|| !this.heroExchangeList.contains(heroId)){
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result ;
		}
		if(hero.getShadowId()<=0 || hero.getShadowNum()<=0){
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_DATA));
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
	
	private int getFreeCd(RoleHeroStatus status, HeroLuck luck,
			HeroLuckType luckType) {
		Date lastDate = this.getLastFreeDate(status, luckType);
		if (null == lastDate) {
			return 0;
		}
		Date now = new Date();
		int second = DateUtil.dateDiffSecond(lastDate, now);
		int cdSecond = luck.getCd() * 60;
		int cd = cdSecond - second;
		cd = Math.min(cdSecond, cd);
		cd = Math.max(0, cd);
		return cd;
	}
	
	private HeroLuckPanelItem getHeroLuckPanelItem(RoleInstance role,HeroLuck luck,
			RoleHeroStatus status){
		HeroLuckType luckType = HeroLuckType.get(luck.getTypeId());
		HeroLuckPanelItem luckItem = new HeroLuckPanelItem();
		luckItem.setLuckType(luck.getTypeId());
		luckItem.setImageQuality(luck.getImageQuality());
		luckItem.setTips(luck.getTips());
		luckItem.setGoldMoney(luck.getGoldMoney());
		luckItem.setFreeTotalTimes((byte)luck.getFreeTimes());
		//免费CD
		luckItem.setFreeCd(this.getFreeCd(status, luck, luckType));
		//今日免费次数
		luckItem.setFreeTimes((byte)this.getTodayFreeNum(status, luckType));
		return luckItem ;
	}
	
	@Override
	public C1264_HeroLuckPanelRespMessage buildHeroLuckPanel(RoleInstance role){
		C1264_HeroLuckPanelRespMessage respMsg = new C1264_HeroLuckPanelRespMessage();
		List<HeroLuckPanelItem> luckList = Lists.newArrayList();
		//各抽卡配置
		if(!Util.isEmpty(heroLuckMap)){
			RoleHeroStatus status = GameContext.getUserHeroApp().getRoleHeroStatus(role.getRoleId());
			for(HeroLuck luck : heroLuckMap.values()){
				HeroLuckType luckType = HeroLuckType.get(luck.getTypeId());
				if(null == luckType){
					continue ;
				}
				luckList.add(this.getHeroLuckPanelItem(role, luck, status));
			}
		}
		//展示的英雄
		List<Integer> heroIdList = this.heroLuckShowList;
		List<HeroLuckGoodsItem> showHeroList = Lists.newArrayList();
		if(!Util.isEmpty(heroIdList)){
			for(int goodsId : heroIdList){
				GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsId);
				if(null == gb){
					continue ;
				}
				HeroLuckGoodsItem goodsItem = new HeroLuckGoodsItem();
				goodsItem.setGoodsId(goodsId);
				goodsItem.setName(gb.getName());
				goodsItem.setQuality(gb.getQualityType());
				goodsItem.setResId((short)gb.getResId());
				showHeroList.add(goodsItem);
			}
		}
		respMsg.setLuckInfoList(luckList);
		respMsg.setShowHeroList(showHeroList);
		return respMsg;
	}
	
	@Override
	public HeroLove getHeroLove(int heroId,byte loveType) {
		String key = heroId + "_" + loveType ;
		return this.fromMap(this.heroLoveMap, key);
	}
	
	private byte getHeroLoveStatus(RoleHero hero,byte loveType,int targetId){
		HeroLove heroLove = this.getHeroLove(hero.getHeroId(),loveType);
		if(null == heroLove){
			//未开启
			return HeroLoveStatus.not_open.getType();
		}
		if(targetId <=0){
			return HeroLoveStatus.un_activated.getType() ;
		}
		if(heroLove.containId(String.valueOf(targetId))){
			return HeroLoveStatus.activated.getType();
		}
		return HeroLoveStatus.un_activated.getType() ;
	}
	
	@Override
	public byte getHeroLoveStatus(RoleHero hero,byte loveType) {
		HeroLoveType hlt = HeroLoveType.get(loveType);
		if(null == hlt){
			return HeroLoveStatus.not_open.getType();
		}
		if(null == hero){
			return HeroLoveStatus.un_activated.getType();
		}
		int targetId = 0 ;
		switch (hlt) {
		case horse:
			RoleHorse horse = GameContext.getRoleHorseApp()
					.getOnBattleRoleHorse(Integer.parseInt(hero.getRoleId()));
			if(null != horse){
				targetId = horse.getHorseId() ;
			}
		case goddess:
			RoleGoddess goddess = GameContext.getGoddessApp().getOnBattleGoddes(hero.getRoleId());
			if(null != goddess){
				targetId = goddess.getGoddessId();
			}
		case godWeapon:
			// TODO:等待其他模块接口
			break;
		}
		return this.getHeroLoveStatus(hero, loveType, targetId);
	}
	
	@Override
	public LuckLotteryResult heroLuckLottery(RoleInstance role,byte typeId){
		LuckLotteryResult result = new LuckLotteryResult() ;
		HeroLuckType luckType = HeroLuckType.get(typeId);
		if(null == luckType){
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result ;
		}
		HeroLuck luck = this.getHeroLuck(typeId);
		if(null == luck){
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result ;
		}
		RoleHeroStatus status = GameContext.getUserHeroApp().getRoleHeroStatus(role.getRoleId());
		int cd = this.getFreeCd(status, luck, luckType);
		//是否免费
		boolean free = (cd <=0 && this.getTodayFreeNum(status, luckType) < luck.getFreeTimes());
		if((!free) && (role.getGoldMoney() < luck.getGoldMoney())){
			//判断元宝是否足够
			result.setInfo(GameContext.getI18n().getText(TextId.NOT_ENOUGH_GOLD_MONEY));
			return result ;
		}
		//首次付费
		boolean fristPay = (!free && this.getPayLuckNum(status, luckType)<=0) ;
		HeroLuckGoodsConfig config = this.getHeroLuckGoodsConfig(typeId, fristPay);
		HeroLuckGoods goods = this.getHeroLuckGoods(role, config);
		if(null == goods){
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_DATA));
			return result ;
		}
		if(!free){
			//扣钱
			GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.goldMoney, OperatorType.Decrease, 
					luck.getGoldMoney(), OutputConsumeType.hero_luck_consume);
			role.getBehavior().notifyAttribute();
		}
		//修改抽奖状态
		this.updateRoleHeroStatus(status, luckType, free);
		this.saveRoleHeroStatus(status);
		
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goods.getGoodsId());
		boolean existRoleHero = false ;
		boolean isGoodsHero = (gb.getGoodsType() == GoodsType.GoodsHero.getType());
		if(isGoodsHero){
			existRoleHero = (null != GameContext.getUserHeroApp().getRoleHero(role.getRoleId(), gb.getId()));
		}
		//是英雄并且当前没有拥有次英雄时自动使用
		if(isGoodsHero && !existRoleHero){
			try {
				this.useHeroTemplate(role, (GoodsHero)gb);
			} catch (Exception e) {
				//TODO: 日志
				logger.error("useHeroTemplate error",e);
			}
		}else{
			//放入背包
			List<GoodsOperateBean> addList = Lists.newArrayList() ;
			GoodsOperateBean bean = new GoodsOperateBean();
			bean.setGoodsId(gb.getId());
			bean.setGoodsNum(goods.getGoodsNum());
			bean.setBindType(BindingType.already_binding);
			addList.add(bean);
			GoodsHelper.addGoodsForBagOrMail(role, addList,
					OutputConsumeType.hero_luck_output, MailSendRoleType.System);
		}
		result.setLuckItem(this.getHeroLuckPanelItem(role, luck, status));
		result.setGoodsBase(gb);
		result.setGoods(goods);
		result.setFree(free);
		result.success();
		return result ;
	}
	
	
	private int getTodayFreeNum(RoleHeroStatus status, HeroLuckType luckType) {
		switch (luckType) {
		case low:
			return status.getTodayLuck1Num();
		case mid:
			return status.getTodayLuck2Num();
		case hight:
			return status.getTodayLuck3Num();
		default:
			throw new java.lang.IllegalArgumentException("luckType error");
		}
	}
	
	private void updateRoleHeroStatus(RoleHeroStatus status, HeroLuckType luckType,boolean free){
		switch (luckType) {
		case low:
			if(free){
				status.setTodayLuck1Num(status.getTodayLuck1Num()+1);
				status.setLastLuck1Date(new Date());
			}else{
				status.setPayLuck1Num(status.getPayLuck1Num()+1);
			}
			break ;
		case mid:
			if(free){
				status.setTodayLuck2Num(status.getTodayLuck2Num()+1);
				status.setLastLuck2Date(new Date());
			}else{
				status.setPayLuck2Num(status.getPayLuck2Num()+1);
			}
			break ;
		case hight:
			if(free){
				status.setTodayLuck3Num(status.getTodayLuck3Num()+1);
				status.setLastLuck3Date(new Date());
			}else{
				status.setPayLuck3Num(status.getPayLuck3Num()+1);
			}
			break ;
		}
	}
	
	private int getPayLuckNum(RoleHeroStatus status, HeroLuckType luckType) {
		switch (luckType) {
		case low:
			return status.getPayLuck1Num() ;
		case mid:
			return status.getPayLuck2Num();
		case hight:
			return status.getPayLuck3Num();
		default:
			throw new java.lang.IllegalArgumentException("luckType error");
		}
	}
	
	private Date getLastFreeDate(RoleHeroStatus status, HeroLuckType luckType) {
		switch (luckType) {
		case low:
			return status.getLastLuck1Date() ;
		case mid:
			return status.getLastLuck2Date();
		case hight:
			return status.getLastLuck3Date();
		default:
			throw new java.lang.IllegalArgumentException("luckType error");
		}
	}
	
	private HeroLuckGoods getHeroLuckGoods(RoleInstance role,HeroLuckGoodsConfig config){
		Map<Integer,Integer> weightMap = this.getWeightMap(role, config);
		Integer goodsId = Util.getWeightCalct(weightMap);
		if(null == goodsId){
			return null ;
		}
		return config.getHeroLuckGoods(goodsId);
	}
	
	private Map<Integer,Integer> getWeightMap(RoleInstance role,HeroLuckGoodsConfig config){
		/*
		根据玩家手上的影子数量决定对应卡片出现的概率。
		假设该英雄影子的正常权值为A，当玩家的背包中有X个影子时
		则权值衰减为 A*(1- X/(X+B/2))
		其中B的含义是：B张影子可以兑换出该英雄 
		*/
		Map<Integer,Integer> weightMap = Maps.newHashMap();
		Map<Integer,Integer> canAddWeightMap = Maps.newHashMap();
		int reduWeight = 0 ;
		for(HeroLuckGoods lg : config.getGoodsMap().values()){
			if(1 == lg.getWeightsType()){
				//can add
				canAddWeightMap.put(lg.getGoodsId(), lg.getWeights());
				continue ;
			}
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(lg.getGoodsId());
			if(null == goodsBase || 
					goodsBase.getGoodsType() != GoodsType.GoodsHero.getType()){
				//非英雄
				weightMap.put(lg.getGoodsId(), lg.getWeights());
				continue ;
			}
			GoodsHero hero  = (GoodsHero)goodsBase ;
			//英雄计算权重衰减
			int shadowId = hero.getShadowId() ;
			int shadowNum = hero.getShadowNum() ;
			int haveNum = role.getRoleBackpack().countByGoodsId(shadowId);
			if(0 == haveNum){
				weightMap.put(lg.getGoodsId(), lg.getWeights());
				continue ;
			}
			int weight = lg.getWeights() ;
			int newWeight = (int)(weight*(1 - haveNum/(haveNum+ shadowNum/2.0f)));
			reduWeight += (weight-newWeight);
			weightMap.put(lg.getGoodsId(), newWeight);
		}
		int canAddSize = canAddWeightMap.size();
		if(reduWeight <=0 || 0 == canAddSize){
			weightMap.putAll(canAddWeightMap);
			return weightMap ;
		}
		int perAdd = reduWeight/canAddSize ;
		int remainAdd = reduWeight - perAdd*canAddSize ;
		boolean first = true ;
		for(int goodsId : canAddWeightMap.keySet()){
			int w = canAddWeightMap.get(goodsId);
			weightMap.put(goodsId,first?(w+perAdd+remainAdd):(w+perAdd));
			first = false ;
		}
		return weightMap ;
	}
	

	private void loveRelationChanged(String roleId,HeroLoveType loveType,
			int currentId,int preId){
		try {
			RoleHero hero = GameContext.getUserHeroApp().getOnBattleRoleHero(
					roleId);
			if (null == hero) {
				return;
			}
			HeroLove love = this.getHeroLove(hero.getHeroId(),loveType.getType());
			if (null == love) {
				return;
			}
			if (love.containId(String.valueOf(currentId)) == love
					.containId(String.valueOf(preId))) {
				return;
			}
			RoleInstance role = GameContext.getOnlineCenter()
					.getRoleInstanceByRoleId(roleId);
			if (null == role) {
				return;
			}
			AttriBuffer preBuffer = this.getHeroGivenAttriBuffer(hero,
					hero.getLevel(), loveType, preId,hero.getQuality(),hero.getStar());
			AttriBuffer nowBuffer = this.getHeroGivenAttriBuffer(hero,
					hero.getLevel(), loveType, currentId,hero.getQuality(),hero.getStar());
			nowBuffer.append(preBuffer.reverse());
			if (nowBuffer.isEmpty()) {
				return;
			}
			GameContext.getUserAttributeApp().changeAttribute(role, nowBuffer);
			role.getBehavior().notifyAttribute();
		}catch(Exception ex){
			logger.error("",ex);
		}
	}
	
	@Override
	public void onHorseChanged(int roleId, int currentHorseId, int preHorseId) {
		this.loveRelationChanged(String.valueOf(roleId), HeroLoveType.horse, 
				currentHorseId, preHorseId);
	}

	@Override
	public void onGoddessChanged(int roleId, int currentGoddessId,
			int preGoddessId) {
		this.loveRelationChanged(String.valueOf(roleId), HeroLoveType.goddess, 
				currentGoddessId, preGoddessId);
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
		item.setMaxStar(MAX_HERO_STAR);
		item.setOnBattle(rh.getOnBattle());
		item.setExp(rh.getExp());
		HeroLevelup lu = GameContext.getHeroApp().getHeroLevelup(rh.getQuality(), rh.getLevel()) ;
		item.setMaxExp(lu.getMaxExp());
		item.setSwallowExp(GameContext.getHeroApp().getSwallowExp(rh));
		//战斗力
		item.setBattleScore(GameContext.getHeroApp().getBattleScore(rh));
		item.setResId((short)hero.getResId());
		item.setShadowId(hero.getShadowId());
		item.setImageId(hero.getImageId());
		return item ;
	}
	
	@Override
	public boolean isReachMaxQuality(RoleHero hero) {
		int heroId = hero.getHeroId();
		GoodsHero goodsHero = GameContext.getGoodsApp().getGoodsTemplate(
				GoodsHero.class, heroId);
		if (hero.getQuality() > goodsHero.getMaxQuality()) {
			return true;
		}
		if (hero.getQuality() < goodsHero.getMaxQuality()) {
			return false;
		}
		if (hero.getStar() >= goodsHero.getMaxStar()) {
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
		GoodsResult gr = GameContext.getUserGoodsApp().deleteForBag(role, shadowId, 
				1, OutputConsumeType.hero_quality_upgrade_consume);
		if(!gr.isSuccess()){
			result.setInfo(gr.getInfo());
			return result ;
		}
		int progress = Math.max(0, hero.getQualityProgress()) + 1 ;
		HeroQualityUpgrade conf = this.getHeroQualityUpgrade(hero.getQuality(), hero.getStar());
		int max = conf.getNextShadowNum();
		if(progress >= max){
			//升级成功
			HeroQualityUpgrade nextConf = conf.getNextConf() ;
			hero.setQualityProgress(0);
			hero.setQuality(nextConf.getQuality());
			hero.setStar(nextConf.getStar());
			result.setStatus(SUCCESS_AND_INCR_LEVEL);
		}else{
			hero.setQualityProgress(progress);
			result.setStatus(SUCCESS_NOT_INCR_LEVEL);
		}
		this.saveRoleHero(hero);
		result.success();
		return result ;
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
			return respMsg ;
		}
		HeroQualityUpgrade conf = GameContext.getHeroApp().getHeroQualityUpgrade(hero.getQuality(), hero.getStar());
		
		respMsg.setFull((byte)0);
		respMsg.setProgress((short)hero.getQualityProgress());
		respMsg.setFullProgress((short)conf.getNextShadowNum());
		//获得升品质后的
		HeroQualityUpgrade nextConf = conf.getNextConf() ;
		AttriBuffer givenBuffer = GameContext.getHeroApp().getHeroGivenAttriBuffer(
				hero, hero.getLevel(),null, 0, nextConf.getQuality(), nextConf.getStar());
		AttriBuffer nowBuffer = GameContext.getHeroApp().getHeroGivenAttriBuffer(
				hero, hero.getLevel(),null, 0, conf.getQuality(), conf.getStar());
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
	
	public short getRoleHeroHeadId(String roleId) {
		RoleHero rh = GameContext.getUserHeroApp().getOnBattleRoleHero(roleId);
		if(null == rh){
			return RespTypeStatus.DEFAULT_HERO_HEAD_ID ;
		}
		GoodsHero gh = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class, rh.getHeroId());
		if(null == gh){
			return RespTypeStatus.DEFAULT_HERO_HEAD_ID ;
		}
		return gh.getHeadId() ;
	}
}
