package com.game.draco.app.vip;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;

import com.game.draco.GameContext;
import com.game.draco.app.vip.config.VipConfig;
import com.game.draco.app.vip.config.VipLevelUpConfig;
import com.game.draco.app.vip.config.VipPrivilegeConfig;
import com.game.draco.app.vip.domain.VipConstant;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
public @Data class VipConfigProvider {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private VipConfig vipConfig;
	private Map<String, VipLevelUpConfig> levelUpConfigMap = Maps.newHashMap();
	private Map<String, List<VipPrivilegeConfig>> privilegeConfigEntryMap = Maps.newHashMap();
	public byte maxVipLevel;
	private List<VipPrivilegeConfig> getPrivilegeConfigs(byte vipLevel){
		String key = vipLevel + "";
		return privilegeConfigEntryMap.get(key);
	}
	/**
	 * splited by \n
	 * @param vipLevel
	 */
	protected String  getPrivilegeInfo(byte vipLevel){
		List<VipPrivilegeConfig> list = getPrivilegeConfigs(vipLevel);
		int size = list.size();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < size-1; i++) {
			sb.append(list.get(i).getVipPriIntroduction()).append("\n");
		}
		sb.append(list.get(size-1).getVipPriIntroduction());
		return sb.toString();
	}
	
	protected VipLevelUpConfig getVipLevelUpConfig(byte vipLevel){
		String key = vipLevel + "";
		VipLevelUpConfig conf = levelUpConfigMap.get(key);
		return conf;
	}
	protected String getMaxLevelHeaderInfo(){
		return vipConfig.getMaxLevelHeaderInfo();
	}
	protected String getLevelUpHeaderInfo(){
		return vipConfig.getLevelUpHeaderInfo();//
	}
	protected Collection<VipLevelUpConfig>  getLevelUpConfigs(){
		return levelUpConfigMap.values();
	}
	protected VipConfigProvider init(){
		loadAllConfig();
		return this;
	}
	
	private void loadAllConfig() {
		loadVipConfig();
		loadVipLevelUpConfig();
		loadVipPrivilegeConfig();
	}

	private void loadVipConfig() {
		String fileName = XlsSheetNameType.VipConfig.getXlsName();
		String sheetName = XlsSheetNameType.VipConfig.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		vipConfig = XlsPojoUtil.getEntity(sourceFile, sheetName,
				VipConfig.class);
		if (vipConfig == null) {
			Log4jManager.CHECK.error("not config the vipConfig,file="
					+ sourceFile + " sheet=" + sheetName);
			Log4jManager.checkFail();
		}
	}
	private void loadVipLevelUpConfig() {
		try{
			levelUpConfigMap = loadConfigMap(XlsSheetNameType.VipLevelUpConfig,VipLevelUpConfig.class, false);
			verifyVipLevel(levelUpConfigMap);
		}catch (Exception e) {
			Log4jManager.CHECK.error("vip config load err,"+e.toString());
			Log4jManager.checkFail();
		}
				
		
	}
	private void verifyVipLevel(Map<String, VipLevelUpConfig> map){
		byte size = (byte)map.size();
		byte max = -1;
		byte min = 2<<3;
	    for(Map.Entry<String, VipLevelUpConfig> entry:map.entrySet()){
	        String key = entry.getKey();
	        byte i = Byte.parseByte(key);
	        max = max>i?max:i;
	        min = min<i?min:i;
	    }
	    maxVipLevel = max;
		if(size<maxVipLevel||min!=1||max!=size){
			Log4jManager.CHECK.error("vip config not enough,vip size in .xls = "+size);
			Log4jManager.checkFail();
		}
	}

	private void loadVipPrivilegeConfig() {
		
		List<VipPrivilegeConfig> list = loadConfigList(
				XlsSheetNameType.VipPrivilegeConfig,VipPrivilegeConfig.class);
		List<VipPrivilegeConfig> lst = null;
		for (VipPrivilegeConfig config : list) {
			String vipLevel = config.getVipLevel()+"";
			if(!privilegeConfigEntryMap.containsKey(vipLevel)){
				lst = Lists.newArrayList();
				privilegeConfigEntryMap.put(vipLevel, lst);
			}else{
				lst = privilegeConfigEntryMap.get(vipLevel);
			}
			lst.add(config);
		}
	}
	private <K, V extends KeySupport<K>> Map<K, V> loadConfigMap(
			XlsSheetNameType xls, Class<V> clazz, boolean linked) {
		String fileName = xls.getXlsName();
		String sheetName = xls.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		Map<K, V> map = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName,
				clazz, linked);
		if (Util.isEmpty(map)) {
			Log4jManager.CHECK.error("not config the " + clazz.getSimpleName()
					+ " ,file=" + sourceFile + " sheet=" + sheetName);
			Log4jManager.checkFail();
		}
		return map;
	}
	private <T> List<T>  loadConfigList(XlsSheetNameType xls,Class<T> t){
		List<T> list = null;
		String fileName = xls.getXlsName();
		String sheetName = xls.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		try {
			list = XlsPojoUtil.sheetToList(sourceFile, sheetName,t);
		} catch (Exception e) {
			Log4jManager.CHECK.error("load "+t.getSimpleName()+" error:fileName=" + fileName+ ",sheetName=" + sheetName);
			Log4jManager.checkFail();
			
		}
		if(list == null){
			Log4jManager.CHECK.error("load "+t.getSimpleName()+" error: result is null fileName=" + fileName+ ",sheetName=" + sheetName);
			Log4jManager.checkFail();
		}
		return list;
	}
}
