package com.game.draco.app.enhanceoption;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.enhanceoption.config.EnhanceOptionBase;
import com.game.draco.app.enhanceoption.config.EnhanceOptionConfig;
import com.game.draco.app.enhanceoption.config.LevelupEnhanceConfig;
import com.game.draco.app.enhanceoption.type.EnhanceOptionType;
import com.game.draco.message.item.EnhanceOptionItem;
import com.game.draco.message.response.C0620_LevelUpEnhanceOptionRespMessage;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;


public class EnhanceOptionAppImpl implements EnhanceOptionApp {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	//init
	private Map<Short, EnhanceOptionBase> optionBaseMap = Maps. newHashMap();

	
	//roleLevel
	private Multimap<Integer, EnhanceOptionItem> deathOptionItemMap = ArrayListMultimap.create();  
	private Multimap<Integer, EnhanceOptionItem> levelupOptionItemMap = ArrayListMultimap.create(); 
	
	private LevelupEnhanceConfig levelUpEnhanceConfig; 
	
	@Override
	public Message getEnhanceOptionLevelUpMessage(RoleInstance role) {
		C0620_LevelUpEnhanceOptionRespMessage msg = new C0620_LevelUpEnhanceOptionRespMessage();
		LevelupEnhanceConfig cf = getLevelupEnhanceConfig();
		if(cf==null){
			msg.setType(Result.FAIL);
			msg.setInfo(getText(TextId.ENHANCE_OPTION_CONFIG_ERR));
			logger.error("getEnhanceOptionLevelUpMessage error:roleId = "+role.getRoleId()+", LevelupEnhanceConfig == null");
		}
		msg.setNpcName(cf.getNpcName());
		msg.setNpcRes(cf.getNpcRes());
		msg.setNpcTalk(cf.getNpcTalk());
		List<EnhanceOptionItem> options = getEnhanceOptionItems(role, EnhanceOptionType.LEVEL_OPTION);
		msg.setOptions(options);
		msg.setType(Result.SUCCESS);
		return msg;
	}
	//根据玩家过滤
	@Override
	public List<EnhanceOptionItem> getEnhanceOptionItems(RoleInstance role, EnhanceOptionType tp) {
		if(role == null){
			return null;
		}
		int roleLevel = role.getLevel();
		switch (tp){
		case DEATH_OPTION:
			return Lists.newArrayList(deathOptionItemMap.get(roleLevel));
		case LEVEL_OPTION:
			return Lists.newArrayList(levelupOptionItemMap.get(roleLevel));
		}
		return null;
	}
	
	@Override
	public LevelupEnhanceConfig getLevelupEnhanceConfig(){
		return levelUpEnhanceConfig;
	}
	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void start() {
		initOptionBaseMap();
		
		initDeathOptionItemMap();
		initLevelupOptionMap();
		
		initLevelupEnhanceConfig();
	}

	private void initOptionBaseMap() {
		try {
			optionBaseMap = loadConfigMap(XlsSheetNameType.enhance_option_base_config, EnhanceOptionBase.class, false);
		} catch (Exception e) {
			Log4jManager.CHECK.error("initOptionBaseMap  err," + e.toString());
			Log4jManager.checkFail();
		}
	}
	@Override
	public void stop() {
		
	}
	private void initDeathOptionItemMap(){
		try {
			List<EnhanceOptionConfig> optionList = loadConfigList(XlsSheetNameType.enhance_option_death_config, EnhanceOptionConfig.class);
			initEnhanceOptionItemMap(deathOptionItemMap, optionBaseMap, optionList);
		} catch (Exception e) {
			Log4jManager.CHECK.error("enhance_options initDeathOptionItemMap err," + e.toString());
			Log4jManager.checkFail();
		}
	}
	private void initEnhanceOptionItemMap(Multimap<Integer, EnhanceOptionItem> itemMap, final Map<Short, EnhanceOptionBase> baseMap, final List<EnhanceOptionConfig> list) {
		if(itemMap == null){//tar
			itemMap = ArrayListMultimap.create();  
		}
		for (EnhanceOptionConfig cf : list) {
			if(cf == null){
				continue;
			}
			cf.add2OptionItemMap(itemMap, baseMap);
		}
	}

	private void initLevelupOptionMap(){
		try {
			List<EnhanceOptionConfig> optionList = loadConfigList(XlsSheetNameType.enhance_option_levelup_config, EnhanceOptionConfig.class);
			initEnhanceOptionItemMap(levelupOptionItemMap, optionBaseMap, optionList);
		} catch (Exception e) {
			Log4jManager.CHECK.error("enhance_options initLevelupOptionMap err," + e.toString());
			Log4jManager.checkFail();
		}
	}

	private void initLevelupEnhanceConfig(){
		try {
			this.levelUpEnhanceConfig = loadConfigEntity(XlsSheetNameType.enhance_option_levelup_npc_config, LevelupEnhanceConfig.class);
		} catch (Exception e) {
			Log4jManager.CHECK.error("enhance_options initLevelupEnhanceConfig err," + e.toString());
			Log4jManager.checkFail();
		}
	}
	private <T> T loadConfigEntity(XlsSheetNameType xls, Class<T> t) {
		String fileName = xls.getXlsName();
		String sheetName = xls.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		T en = XlsPojoUtil.getEntity(sourceFile, sheetName, t);
		if (en == null) {
			Log4jManager.CHECK.error("not config the vipConfig,file=" + sourceFile + " sheet=" + sheetName);
			Log4jManager.checkFail();
		}
		return en;
	}
	private <K, V extends KeySupport<K>> Map<K, V> loadConfigMap(XlsSheetNameType xls, Class<V> clazz, boolean linked) {
		String fileName = xls.getXlsName();
		String sheetName = xls.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		Map<K, V> map = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, clazz, linked);
		if (Util.isEmpty(map)) {
			Log4jManager.CHECK.error("not config the " + clazz.getSimpleName() + " ,file=" + sourceFile + " sheet=" + sheetName);
			Log4jManager.checkFail();
		}
		return map;
	}

	private <T> List<T> loadConfigList(XlsSheetNameType xls, Class<T> t) {
		List<T> list = null;
		String fileName = xls.getXlsName();
		String sheetName = xls.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		try {
			list = XlsPojoUtil.sheetToList(sourceFile, sheetName, t);
		} catch (Exception e) {
			Log4jManager.CHECK.error("load " + t.getSimpleName() + " error:fileName=" + fileName + ",sheetName=" + sheetName);
			Log4jManager.checkFail();

		}
		if (list == null) {
			Log4jManager.CHECK.error("load " + t.getSimpleName() + " error: result is null fileName=" + fileName + ",sheetName=" + sheetName);
			Log4jManager.checkFail();
		}
		return list;
	}
	private String getText(String textId) {
		return GameContext.getI18n().getText(textId);
	}

}
