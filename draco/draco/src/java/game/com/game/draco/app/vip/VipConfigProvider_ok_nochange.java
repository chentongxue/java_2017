//package com.game.draco.app.vip;
//
//import java.util.Collection;
//import java.util.List;
//import java.util.Map;
//
//import lombok.Data;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import sacred.alliance.magic.base.XlsSheetNameType;
//import sacred.alliance.magic.constant.Cat;
//import sacred.alliance.magic.util.KeySupport;
//import sacred.alliance.magic.util.Log4jManager;
//import sacred.alliance.magic.util.Util;
//import sacred.alliance.magic.util.XlsPojoUtil;
//
//import com.game.draco.GameContext;
//import com.game.draco.app.luckybox.config.LuckyBoxRewardPoolConfig;
//import com.game.draco.app.vip.config.VipConfig;
//import com.game.draco.app.vip.config.VipLevelUpConfig;
//import com.game.draco.app.vip.config.VipPrivilegeConfig;
//import com.game.draco.message.item.VipPrivilegeItem;
//import com.google.common.collect.Maps;
//import com.google.common.collect.Lists;
//import baoutil.LogB;
//public @Data class VipConfigProvider_ok_nochange {
//	
//	private Logger logger = LoggerFactory.getLogger(this.getClass());
//	private VipConfig vipConfig;
//	private Map<String, VipLevelUpConfig> levelUpConfigMap = Maps.newHashMap();
//	private Map<String, VipPrivilegeConfig> privilegeConfigMap = Maps
//			.newHashMap();
//	private Map<String, List<VipPrivilegeConfig>> privilegeConfigEntryMap = Maps.newHashMap();
//	
//	@Deprecated public List<VipPrivilegeConfig> getPrivilegeConfigs0(byte vipLevel){
//		List<VipPrivilegeConfig> cfList = Lists.newArrayList();
//		String key = vipLevel + Cat.underline;
//		for (Map.Entry<String, VipPrivilegeConfig> entry : getPrivilegeConfigMap()
//				.entrySet()) {
//			String awardKey = entry.getKey();
//			if (awardKey.startsWith(key)) {
//				VipPrivilegeConfig conf = entry.getValue();
//				cfList.add(conf);
//			}
//		}
//		return cfList;
//	}
//	public List<VipPrivilegeConfig> getPrivilegeConfigs(byte vipLevel){
//		String key = vipLevel + "";
//		return privilegeConfigEntryMap.get(key);
//	}
//	
//	public VipLevelUpConfig getVipLevelUpConfig(byte vipLevel){
//		String key = vipLevel + "";
//		VipLevelUpConfig conf = levelUpConfigMap.get(key);
//		return conf;
//	}
//	public String getMaxLevelHeaderInfo(){
//		return vipConfig.getMaxLevelHeaderInfo();
//	}
//	public String getLevelUpHeaderInfo(){
//		return vipConfig.getLevelUpHeaderInfo();//
//	}
//	public Collection<VipLevelUpConfig>  getLevelUpConfigs(){
//		return levelUpConfigMap.values();
//	}
//	public VipConfigProvider_ok_nochange init(){
//		loadAllConfig();
//		return this;
//	}; 
//	
//	private void loadAllConfig() {
//		loadVipConfig();
//		loadVipLevelUpConfig();
//		loadVipPrivilegeConfig();
//	}
//
//	private void loadVipConfig() {
//		String fileName = XlsSheetNameType.VipConfig.getXlsName();
//		String sheetName = XlsSheetNameType.VipConfig.getSheetName();
//		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
//		vipConfig = XlsPojoUtil.getEntity(sourceFile, sheetName,
//				VipConfig.class);
//		if (vipConfig == null) {
//			Log4jManager.CHECK.error("not config the vipConfig,file="
//					+ sourceFile + " sheet=" + sheetName);
//			Log4jManager.checkFail();
//		}
//	}
//	private void loadVipLevelUpConfig() {
//		levelUpConfigMap = loadConfigMap(XlsSheetNameType.VipLevelUpConfig,
//				VipLevelUpConfig.class, false);
//	}
//
//	private void loadVipPrivilegeConfig() {
//		privilegeConfigMap = loadConfigMap(XlsSheetNameType.VipPrivilegeConfig,
//				VipPrivilegeConfig.class, false);
//		
//		List<VipPrivilegeConfig> list = loadConfigList(
//				XlsSheetNameType.VipPrivilegeConfig,VipPrivilegeConfig.class);
//		List<VipPrivilegeConfig> lst = null;
//		for (VipPrivilegeConfig config : list) {
//			String vipLevel = config.getVipLevel()+"";
//			if(!privilegeConfigEntryMap.containsKey(vipLevel)){
//				lst = Lists.newArrayList();
//				privilegeConfigEntryMap.put(vipLevel, lst);
//			}else{
//				lst = privilegeConfigEntryMap.get(vipLevel);
//			}
//			lst.add(config);
//		}
//		  String s = privilegeConfigEntryMap.toString();
//		  LogB.s(s);
//		  s = s.replaceAll("\\], ", "\\]          ,\n\n\n");
//		  LogB.s(s);
//	}
//	private <K, V extends KeySupport<K>> Map<K, V> loadConfigMap(
//			XlsSheetNameType xls, Class<V> clazz, boolean linked) {
//		String fileName = xls.getXlsName();
//		String sheetName = xls.getSheetName();
//		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
//		Map<K, V> map = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName,
//				clazz, linked);
//		if (Util.isEmpty(map)) {
//			Log4jManager.CHECK.error("not config the " + clazz.getSimpleName()
//					+ " ,file=" + sourceFile + " sheet=" + sheetName);
//			Log4jManager.checkFail();
//		}
//		return map;
//	}
//	/**
//	 * List<LuckyBoxRewardPoolConfig> viplist = loadConfigList(XlsSheetNameType.LuckyBoxRewardPoolConfig,LuckyBoxRewardPoolConfig.class);
//		for (LuckyBoxRewardPoolConfig rewardConfig : viplist) {
//			String vipLevel = rewardConfig.getVipLevel();//vipLevel∈{0,1,2,...12,normal}
//			Map<String, Integer> map = getLuckyBoxLuckyOddsMap(vipLevel);
//			addLuckyBoxLuckyOddsMap(map,rewardConfig);
//		}
//	 */
//	private <T> List<T>  loadConfigList(XlsSheetNameType xls,Class<T> t){
//		List<T> list = null;
//		String fileName = xls.getXlsName();
//		String sheetName = xls.getSheetName();
//		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
//		try {
//			list = XlsPojoUtil.sheetToList(sourceFile, sheetName,t);
//		} catch (Exception e) {
//			Log4jManager.CHECK.error("load "+t.getSimpleName()+" error:fileName=" + fileName+ ",sheetName=" + sheetName);
//			Log4jManager.checkFail();
//			
//		}
//		if(list == null){
//			Log4jManager.CHECK.error("load "+t.getSimpleName()+" error: result is null fileName=" + fileName+ ",sheetName=" + sheetName);
//			Log4jManager.checkFail();
//		}
//		return list;
//	}
//}
