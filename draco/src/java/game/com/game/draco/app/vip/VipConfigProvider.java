package com.game.draco.app.vip;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;

import com.game.draco.GameContext;
import com.game.draco.app.vip.config.VipConfig;
import com.game.draco.app.vip.config.VipGiftConfig;
import com.game.draco.app.vip.config.VipLevelAwardConfig;
import com.game.draco.app.vip.config.VipLevelFunctionConfig;
import com.game.draco.app.vip.config.VipLevelUpConfig;
import com.game.draco.app.vip.config.VipPrivilegeConfig;
import com.game.draco.app.vip.type.VipPrivilegeType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public @Data
class VipConfigProvider {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private VipConfig vipConfig;
	private Map<String, VipLevelUpConfig> levelUpConfigMap = Maps.newHashMap();
	private Map<String, List<VipPrivilegeConfig>> privilegeConfigEntryMap = Maps.newHashMap();//展示VIP特权信息用，如果showFlag为0，则不添加
	private Map<String, VipPrivilegeConfig> privilegeConfigMap = Maps.newHashMap();
	private Map<String, Integer> vipPrivilegeTimesMap = Maps.newHashMap();//其他模块从这里取得各自的次数，上限百分比等
	public byte maxVipLevel;

	//VIP function
	private Map<String, VipLevelFunctionConfig> vipLevelFunctionConfigMap = Maps.newHashMap();
	
	
	//【运营】商城礼包
	private Map<String, VipGiftConfig> vipGiftMap = Maps.newHashMap();
	//VIP等级奖励 viplevel:cf
	private Map<Byte, List<VipLevelAwardConfig>> vipLevelAwardConfigMapList = Maps.newHashMap();
	
	protected List<VipLevelAwardConfig> getVipLevelAwardConfigList(byte vipLevel){
		return vipLevelAwardConfigMapList.get(vipLevel);
	}
	
	protected ArrayList<VipLevelFunctionConfig> getVipLevelFunctionList(byte vipLevel){
		ArrayList<VipLevelFunctionConfig> rtList = Lists.newArrayList();
		for(VipLevelFunctionConfig conf : vipLevelFunctionConfigMap.values()){
			if(vipLevel >= conf.getVipLevel()){
				rtList.add(conf);
			}
		}
		return rtList;
	}
	protected VipLevelFunctionConfig getVipLevelFunctionConfig(String functionId){
		return vipLevelFunctionConfigMap.get(functionId);
	}
	
	private List<VipPrivilegeConfig> getPrivilegeConfigs(byte vipLevel) {
		String key = vipLevel + "";
		return privilegeConfigEntryMap.get(key);
	}
	/**
	 * 
	 * @param vipLevel
	 * @param vipPriType
	 * @param param
	 * @return 可能为空
	 * @date 2014-8-27 上午10:02:40
	 */
	public VipPrivilegeConfig getVipPrivilegeInfo(byte vipLevel, int vipPriType, String param){
		String key = vipLevel + Cat.underline + vipPriType + Cat.underline + param;
		return privilegeConfigMap.get(key);
	}
	/**
	 * splited by \n
	 * 
	 * @param vipLevel
	 */
	protected String getPrivilegeInfo(byte vipLevel) {
		List<VipPrivilegeConfig> list = getPrivilegeConfigs(vipLevel);
		int size = list.size();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < size - 1; i++) {
			if(list.get(i).getVipPriType()==0){
				sb.append(getFormat(list.get(i).getVipPriIntroduction(),(vipLevel-1))).append("\n");
			}else{
				sb.append(list.get(i).getVipPriIntroduction()).append("\n");
			}
		}
		sb.append(list.get(size - 1).getVipPriIntroduction());
		return sb.toString();
	}
	protected String[] getPrivilegeInfos(byte vipLevel) {
		List<VipPrivilegeConfig> list = getPrivilegeConfigs(vipLevel);
		List<String> infos = Lists.newArrayList();
		int size = list.size();
		for (int i = 0; i < size - 1; i++) {
			if(list.get(i).getVipPriType()==0){
				infos.add(getFormat(list.get(i).getVipPriIntroduction(),(vipLevel-1)));
			}else{
				infos.add(list.get(i).getVipPriIntroduction());
			}
		}
		infos.add(list.get(size - 1).getVipPriIntroduction());
		return infos.toArray(new String[infos.size()]);
	}
	public static String getFormat(String pattern, Object ... arguments) {
		if(pattern==null){
			return "";
		}
	    return MessageFormat.format(pattern, arguments);
	}
	protected VipLevelUpConfig getVipLevelUpConfig(byte vipLevel) {
		String key = vipLevel + "";
		VipLevelUpConfig conf = levelUpConfigMap.get(key);
		return conf;
	}

	protected String getMaxLevelHeaderInfo() {
		return vipConfig.getMaxLevelHeaderInfo();
	}

	protected String getLevelUpHeaderInfo() {
		return vipConfig.getLevelUpHeaderInfo();//
	}

	protected Collection<VipLevelUpConfig> getLevelUpConfigs() {
		return levelUpConfigMap.values();
	}

	public VipGiftConfig getVipGiftConfig(byte vipLevel){
		String key = String.valueOf(vipLevel);
		return vipGiftMap.get(key);
	}
	protected VipConfigProvider init() {
		loadAllConfig();
		return this;
	}

	private void loadAllConfig() {
		loadVipConfig();
		loadVipLevelUpConfig();
		loadVipPrivilegeConfig();
		loadVipPrivilegeConfigMap();//
		loadVipLevelFunctionConfigMap();
		
		loadVipGiftConfigMap();
		loadVipLevelAwardMap();
	}
	
	private void loadVipLevelAwardMap() {
		try {
			String fileName = XlsSheetNameType.vipLevelAwardConfig.getXlsName();
			String sheetName = XlsSheetNameType.vipLevelAwardConfig.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			vipLevelAwardConfigMapList = XlsPojoUtil.sheetToMapList(sourceFile, sheetName, VipLevelAwardConfig.class, true);
			if (Util.isEmpty(vipLevelAwardConfigMapList)) {
				Log4jManager.CHECK.error("load loadVipLevelAwardMap fail: file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
			for(List<VipLevelAwardConfig> cfs:vipLevelAwardConfigMapList.values()){
				for (VipLevelAwardConfig cf : cfs) {
					cf.init();
				}
			}
		} catch (Exception e) {
			Log4jManager.CHECK.error("vip config load buildVipGiftConfigMap err," + e.toString());
			Log4jManager.checkFail();
		}
	}
	private void loadVipGiftConfigMap() {
		try {
			vipGiftMap =  loadConfigMap(XlsSheetNameType.VipGiftConfig, VipGiftConfig.class, true);
			for(VipGiftConfig cf:vipGiftMap.values()){
				cf.init();
			}
		} catch (Exception e) {
			Log4jManager.CHECK.error("vip config load buildVipGiftConfigMap err," + e.toString());
			Log4jManager.checkFail();
		}
	}
	private void loadVipLevelFunctionConfigMap() {
		try {
			vipLevelFunctionConfigMap = loadConfigMap(XlsSheetNameType.VipLevelFunctionConfig, VipLevelFunctionConfig.class, true);
		} catch (Exception e) {
			Log4jManager.CHECK.error("vip config load vipLevelFunctionConfigMap err," + e.toString());
			Log4jManager.checkFail();
		}
	}
	private void loadVipPrivilegeConfigMap() {
		try {
			privilegeConfigMap = loadConfigMap(XlsSheetNameType.VipPrivilegeConfig, VipPrivilegeConfig.class, true);
		} catch (Exception e) {
			Log4jManager.CHECK.error("vip config load privilegeConfigMap err," + e.toString());
			Log4jManager.checkFail();
		}
	}

	private void loadVipConfig() {
		String fileName = XlsSheetNameType.VipConfig.getXlsName();
		String sheetName = XlsSheetNameType.VipConfig.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		vipConfig = XlsPojoUtil.getEntity(sourceFile, sheetName, VipConfig.class);
		if (vipConfig == null) {
			Log4jManager.CHECK.error("not config the vipConfig,file=" + sourceFile + " sheet=" + sheetName);
			Log4jManager.checkFail();
		}
	}

	private void loadVipLevelUpConfig() {
		try {
			levelUpConfigMap = loadConfigMap(XlsSheetNameType.VipLevelUpConfig, VipLevelUpConfig.class, false);
			verifyVipLevel(levelUpConfigMap);
		} catch (Exception e) {
			Log4jManager.CHECK.error("vip config load err," + e.toString());
			Log4jManager.checkFail();
		}

	}

	private void verifyVipLevel(Map<String, VipLevelUpConfig> map) {
		byte size = (byte) map.size();
		byte max = -1;
		byte min = 2 << 3;
		for (Map.Entry<String, VipLevelUpConfig> entry : map.entrySet()) {
			String key = entry.getKey();
			byte i = Byte.parseByte(key);
			max = max > i ? max : i;
			min = min < i ? min : i;
		}
		maxVipLevel = max;
		if (size < maxVipLevel || min != 1 || max != size) {
			Log4jManager.CHECK.error("vip config not enough,vip size in .xls = " + size);
			Log4jManager.checkFail();
		}
	}

	private void loadVipPrivilegeConfig() {

		List<VipPrivilegeConfig> list = loadConfigList(XlsSheetNameType.VipPrivilegeConfig, VipPrivilegeConfig.class);
		List<VipPrivilegeConfig> lst = null;
		for (VipPrivilegeConfig config : list) {
			String vipLevel = config.getVipLevel() + "";
			if (!privilegeConfigEntryMap.containsKey(vipLevel)) {
				lst = Lists.newArrayList();
				privilegeConfigEntryMap.put(vipLevel, lst);
			} else {
				lst = privilegeConfigEntryMap.get(vipLevel);
			}
			if(config.isShow()){//根据showFlag确定是否展示给客户端
				lst.add(config);
			}
			//加载特权及参数
			String key = config.getKey();
			if (!vipPrivilegeTimesMap.containsKey(key)) {
				vipPrivilegeTimesMap.put(key, config.getVipPriPram());
			}
			VipPrivilegeType pt = VipPrivilegeType.getPrivilegeType(config.getVipPriType());
			if (null == pt){
				Log4jManager.CHECK.error("vip privilege config is error,privilege type = " + config.getVipPriType());
				Log4jManager.checkFail();
			}
		}
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
}
