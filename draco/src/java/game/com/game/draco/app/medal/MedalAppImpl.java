package com.game.draco.app.medal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.goods.HeroEquipBackpack;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.EquipslotType;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.domain.GoodsEquipment;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.medal.config.MedalConfig;
import com.game.draco.app.medal.config.MedalNameIconConfig;
import com.game.draco.app.medal.vo.MedalRoleData;
import com.game.draco.message.item.MedalIconItem;
import com.game.draco.message.push.C0518_EquipEffectNotifyMessage;
import com.game.draco.message.response.C0521_MedalListRespMessage;
import com.google.common.collect.Maps;

public class MedalAppImpl implements MedalApp{
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private TreeMap<Integer, List<MedalConfig>> medalConfigMap = Maps.newTreeMap();
	private Map<Integer, MedalNameIconConfig> defaultIcons = Maps.newHashMap();
	private Map<AttributeType, MedalType> attributeMedalTypeMap = Maps.newHashMap();
	private int equipslotEffectNum = 0;
	private Map<String, MedalRoleData> roleMedalMap = Maps.newHashMap();
	
	@Autowired private MedalStorage medalStorage;
	
	@Override
	public void start() {
		this.loadMedalConfig();
		this.loadMedalNameIconConfig();
		this.initEquipslotEffectNum();
	}
	
	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void stop() {
		
	}
	
	private void initEquipslotEffectNum(){
		this.equipslotEffectNum = EquipslotType.getEffectSlotNum();
	}
	
	@Override
	public short getDefaultIcon(MedalType medalType){
		if(null == medalType){
			return 0;
		}
		MedalNameIconConfig config = this.getMedalNameIconConfig(medalType.getType());
		if(null == config){
			return 0;
		}
		return config.getIconId();
	}
	
	private void loadMedalNameIconConfig(){
		String fileName = XlsSheetNameType.medal_default_icon.getXlsName();
		String sheetName = XlsSheetNameType.medal_default_icon.getSheetName();
		String info = "load excel error: fileName = " + fileName + ", sheetName = " + sheetName + ".";
		try {
			this.defaultIcons.clear();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<MedalNameIconConfig> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, MedalNameIconConfig.class);
			for(MedalNameIconConfig config : list){
				if(null == config){
					continue;
				}
				int type = config.getType();
				if(null == MedalType.get(type)){
					this.checkFail(info + "type = " + type + ", it's not exist.");
				}
				this.defaultIcons.put(type, config);
			}
		} catch (Exception e) {
			this.checkFail(info);
		}
	}
	private void loadMedalConfig(){
		this.medalConfigMap.clear();
		//加载特殊规则的勋章配置
		this.loadSpecialMedalConfig(MedalType.QiangHua, XlsSheetNameType.medal_qianghua);
		this.loadSpecialMedalConfig(MedalType.XiangQian, XlsSheetNameType.medal_xiangqian);
		this.loadSpecialMedalConfig(MedalType.XiLian, XlsSheetNameType.medal_xilian);
		//加载属性勋章配置
		this.loadAttrMedalConfig();
		//排序并初始化index
		for(List<MedalConfig> list : this.medalConfigMap.values()){
			if(Util.isEmpty(list)){
				continue;
			}
			//排序
			this.sortMedalConfigList(list);
			//初始化index
			this.initMedalConfigIndex(list);
		}
	}
	
	private void sortMedalConfigList(List<MedalConfig> medalConfiglist){
		Collections.sort(medalConfiglist, new Comparator<MedalConfig>() {
			@Override
			public int compare(MedalConfig c1, MedalConfig c2) {
				if(c1.getLevel() < c2.getLevel()){
					return -1;
				}
				if(c1.getLevel() > c2.getLevel()){
					return 1;
				}
				if(c1.getNum() < c2.getNum()){
					return -1;
				}
				if(c1.getNum() > c2.getNum()){
					return 1;
				}
				return 0;
			}
		});
	}
	
	private void initMedalConfigIndex(List<MedalConfig> configList){
		if(Util.isEmpty(configList)){
			return ;
		}
		int index = -1 ;
		for(MedalConfig config : configList){
			index ++ ;
			config.setIndex((byte)index);
		}
	}
	
	private void loadSpecialMedalConfig(MedalType medalType, XlsSheetNameType xlsSheetNameType){
		String fileName = xlsSheetNameType.getXlsName();
		String sheetName = xlsSheetNameType.getSheetName();
		String info = "load excel error: fileName = " + fileName + ", sheetName = " + sheetName + ".";
		try {
			int type = medalType.getType();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<MedalConfig> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, MedalConfig.class);
			for(MedalConfig config : list){
				if(null == config){
					continue;
				}
				config.initMedalType(medalType);
				config.checkInit(info);
				if(!this.medalConfigMap.containsKey(type)){
					this.medalConfigMap.put(type, new ArrayList<MedalConfig>());
				}
				this.medalConfigMap.get(type).add(config);
			}
		} catch (Exception e) {
			this.checkFail(info);
		}
	}
	
	private void loadAttrMedalConfig(){
		String fileName = XlsSheetNameType.medal_attribute.getXlsName();
		String sheetName = XlsSheetNameType.medal_attribute.getSheetName();
		String info = "load excel error: fileName = " + fileName + ", sheetName = " + sheetName + ".";
		try {
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<MedalConfig> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, MedalConfig.class);
			for(MedalConfig config : list){
				if(null == config){
					continue;
				}
				config.checkInit(info);
				int type = config.getType();
				if(!this.medalConfigMap.containsKey(type)){
					this.medalConfigMap.put(type, new ArrayList<MedalConfig>());
				}
				this.medalConfigMap.get(type).add(config);
				this.attributeMedalTypeMap.put(AttributeType.get(config.getRelyAttrType()), config.getMedalType());
			}
		} catch (Exception e) {
			this.checkFail(info);
		}
	}
	
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
	@Override
	public MedalConfig getMedalConfig(MedalType medalType, int index){
		List<MedalConfig> list = this.getMedalConfigList(medalType);
		if(Util.isEmpty(list)){
			return null;
		}
		//这种情况不存在
		if(index < 0 || index >= list.size()){
			return null;
		}
		return list.get(index);
	}
	
	private List<MedalConfig> getMedalConfigList(MedalType medalType){
		return this.getMedalConfigList(medalType.getType());
	}
	
	private List<MedalConfig> getMedalConfigList(int type){
		return this.medalConfigMap.get(type);
	}
	
	private short getEffectId(MedalType medalType, int index){
		if(null == medalType || index < 0){
			return 0 ;
		}
		MedalConfig config = this.getMedalConfig(medalType, index);
		if(null == config){
			return 0 ;
		}
		return config.getEffectId();
	}
	
	@Override
	public short[] getRoleMedalEffects(RoleInstance role){
		int size = 0;
		try{
			MedalRoleData rd = this.getMedalRoleData(role.getRoleId());
			size = rd.getRoleMedalCount();
			short[] effects = new short[size];
			int i = 0;
			for(Map.Entry<Integer, Integer> entry : rd.getRoleMedalMap().entrySet()){
				if(null == entry){
					continue;
				}
				MedalType medalType = MedalType.get(entry.getKey());
				int index = entry.getValue();
				effects[i] = this.getEffectId(medalType, index);
				i++;
			}
			return effects;
		}catch(Exception e){
			logger.error(this.getClass().getName() + " getRoleEquipEffect error: ", e);
		}
		return new short[size];
	}
	
	@Override
	public int onLogin(RoleInstance role, Object context){
		try{
			String roleId = role.getRoleId();
			MedalRoleData rd = new MedalRoleData();
			rd.setRoleId(roleId);
			
			//强化
			MedalConfig strengthenCfg = this.getStrengthenConfig(role);
			rd.addMedalInfo(MedalType.QiangHua, strengthenCfg);
			//镶嵌
			MedalConfig mosaicConfig = this.getMosaicConfig(role);
			rd.addMedalInfo(MedalType.XiangQian, mosaicConfig);
			//洗练
			MedalConfig recastingConfig = this.getRecastingConfig(role);
			rd.addMedalInfo(MedalType.XiLian, recastingConfig);
			//属性
			for(MedalType medalType : MedalType.values()){
				if(!medalType.isAttribute()){
					continue;
				}
				MedalConfig attrConfig = this.getAttributeConfig(role, medalType);
				rd.addMedalInfo(medalType, attrConfig);
			}
			this.roleMedalMap.put(roleId, rd);
		}catch(Exception e){
			this.logger.error(this.getClass().getName() + " login error: ", e);
			return 0;
		}
		
		return 1;
	}
	
	private MedalConfig getStrengthenConfig(RoleInstance role){
		List<MedalConfig> list = this.getMedalConfigList(MedalType.QiangHua);
		if(Util.isEmpty(list)){
			return null;
		}
		int size = list.size();
		for(int i = size-1 ;i >= 0 ;i--){
			MedalConfig config  = list.get(i);
			int roleNum = this.totalEffectNum(role, config.getLevel(),config.getSlaveLevel(), MedalType.QiangHua) ;
			if(roleNum >= config.getNum()){
				return config ;
			}
		}
		return null ;
	}
	
	private MedalConfig getMosaicConfig(RoleInstance role){
		List<MedalConfig> list = this.getMedalConfigList(MedalType.XiangQian);
		if(Util.isEmpty(list)){
			return null;
		}
		int size = list.size();
		for(int i = size-1 ;i >= 0 ;i--){
			MedalConfig config  = list.get(i);
			int roleNum = this.totalEffectNum(role, config.getLevel(), config.getSlaveLevel(),MedalType.XiangQian) ;
			if(roleNum >= config.getNum()){
				return config ;
			}
		}
		return null ;
	}
	
	private MedalConfig getRecastingConfig(RoleInstance role){
		List<MedalConfig> list = this.getMedalConfigList(MedalType.XiLian);
		if(Util.isEmpty(list)){
			return null;
		}
		int size = list.size();
		for(int i = size-1 ;i >= 0 ;i--){
			MedalConfig config  = list.get(i);
			int roleNum = this.totalEffectNum(role, config.getLevel(),config.getSlaveLevel(), MedalType.XiLian) ;
			if(roleNum >= config.getNum()){
				return config ;
			}
		}
		return null ;
	}
	
	private int totalEffectNum(RoleInstance role, int lv,int slaveLv,MedalType medalType){
		Collection<HeroEquipBackpack> list = GameContext.getUserHeroApp().getEquipBackpack(role.getRoleId());
		if(Util.isEmpty(list)){
			return 0 ;
		}
		int total = 0 ;
		for(HeroEquipBackpack pack : list){
			switch (medalType) {
				case QiangHua:
					total += pack.totalEffectStrengthenLevel(lv);
					break ;
				case XiLian:
					//total += pack.totalEffectRecastingQualityNum(lv);
					total += pack.totalEffectStar(lv, slaveLv) ;
					break ;
				case XiangQian:
					total += pack.totalEffectMosaicLevel(lv) ;
					break ;
			}
		}
		return total ;
	}
	
	private MedalConfig getAttributeConfig(RoleInstance role, MedalType medalType){
		List<MedalConfig> list = this.getMedalConfigList(medalType);
		if(Util.isEmpty(list)){
			return null;
		}
		int size = list.size();
		for(int i=size-1; i>=0; i--){
			MedalConfig config  = list.get(i);
			if(role.get(config.getRelyAttrType()) >= config.getRelyAttrValue()){
				return config;
			}
		}
		return null;
	}
	
	private int getIndex(MedalConfig config){
		if(null == config){
			return -1 ;
		}
		return config.getIndex() ;
	}
	
	/**
	 * 同步装备特效
	 * @param role
	 * 
	 */
	private void sendEquipEffectNotify(RoleInstance role){
		C0518_EquipEffectNotifyMessage notify = new C0518_EquipEffectNotifyMessage();
		notify.setRoleId(role.getIntRoleId());
		notify.setEffectId(this.getRoleMedalEffects(role));
		role.getBehavior().sendMessage(notify);
		
		//广播给周围玩家
		MapInstance mapInstance = role.getMapInstance();
		if(mapInstance == null){
			return ;
		}
		mapInstance.broadcastMap(role, notify);
	}
	
	private void doUpdateMedal(RoleInstance role, MedalType medalType){
		try{
			if(null == medalType){
				return ;
			}
			MedalRoleData roleData = this.getMedalRoleData(role.getRoleId());
			int oldIndex = roleData.getMedalIndex(medalType);
			MedalConfig nowConfig = null ;
			if(MedalType.QiangHua == medalType){//强化等级
				nowConfig = this.getStrengthenConfig(role);
			}else if(MedalType.XiangQian == medalType){//镶嵌
				nowConfig = this.getMosaicConfig(role);
			}else if(MedalType.XiLian == medalType){//洗练
				nowConfig = this.getRecastingConfig(role);
			}else if(medalType.isAttribute()){
				nowConfig = this.getAttributeConfig(role, medalType);
			}
			int nowIndex = this.getIndex(nowConfig);
			//新旧相同没有改变
			if(nowIndex == oldIndex){
				return;
			}
			//!!!! 更新角色特效index
			roleData.update(medalType, nowIndex);
			//1.特效 
			this.sendEquipEffectNotify(role);
			
			//2.属性效果
			MedalConfig oldConfig = this.getMedalConfig(medalType, oldIndex);
			AttriBuffer buffer = new AttriBuffer();
			if(null != oldConfig){
				buffer.append(oldConfig.getAttriList()).reverse();
			}
			if(null != nowConfig){
				buffer.append(nowConfig.getAttriList());
			}
			GameContext.getUserAttributeApp().changeAttribute(role, buffer);
			role.getBehavior().notifyAttribute();
			
			//发送勋章通知消息
			C0521_MedalListRespMessage message = this.getC0521_MedalListRespMessage(role);
			role.getBehavior().sendMessage(message);
		}catch(Exception e){
			this.logger.error(this.getClass().getName() + ".doUpdateMedal error: ", e);
		}
	}
	
	@Override
	public C0521_MedalListRespMessage getC0521_MedalListRespMessage(RoleInstance role){
		C0521_MedalListRespMessage message = new C0521_MedalListRespMessage();
		message.setMedalList(this.getMedalList(role));
		return message;
	}
	
	private List<MedalIconItem> getMedalList(RoleInstance role) {
		MedalRoleData rd = this.getMedalRoleData(role.getRoleId());
		return this.buildMedalList(rd);
	}
	
	private List<MedalIconItem> buildMedalList(MedalRoleData rd){
		List<MedalIconItem> medalList = new ArrayList<MedalIconItem>();
		if(null == rd){
			return medalList;
		}
		for(Map.Entry<Integer, Integer> entry : rd.getRoleMedalMap().entrySet()){
			if(null == entry){
				continue;
			}
			MedalType medalType = MedalType.get(entry.getKey());
			int index = entry.getValue();
			MedalConfig config = this.getMedalConfig(medalType, index);
			MedalIconItem item = new MedalIconItem();
			short icon; 
			if(null == config){
				icon = this.getDefaultIcon(medalType);
			}else{
				icon = config.getIconId();
				item.setShowLv(config.getShowLv());
			}
			item.setType((byte) medalType.getType());
			item.setIcon(icon);
			medalList.add(item);
		}
		return medalList;
	}
	
	@Override
	public List<MedalIconItem> getMedalList(String roleId) {
		RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleId);
		if(null != role){
			return this.getMedalList(role);
		}
		MedalRoleData rd = this.medalStorage.getMedalRoleData(roleId);
		return this.buildMedalList(rd);
	}
	
	@Override
	public void updateMedal(RoleInstance role, MedalType medalType, RoleGoods roleGoods){
		try {
			if(null != roleGoods){
				GoodsEquipment ge = GameContext.getGoodsApp().getGoodsTemplate(GoodsEquipment.class, roleGoods.getGoodsId());
				if(null == ge ){
					return;
				}
			}
			this.doUpdateMedal(role, medalType);
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".updateMedal error: ", e);
		}
	}
	
	@Override
	public AttriBuffer getAttriBuffer(AbstractRole player) {
		AttriBuffer buffer = AttriBuffer.createAttriBuffer();
		try{
			RoleInstance role = (RoleInstance) player;
			//强化等级
			MedalConfig strengConfig = this.getStrengthenConfig(role);
			if(null != strengConfig) {
				buffer.append(strengConfig.getAttriList());
			}
			//镶嵌
			MedalConfig mosaicConfig = this.getMosaicConfig(role);
			if(null != mosaicConfig) {
				buffer.append(mosaicConfig.getAttriList());
			}
			//洗练
			MedalConfig recastingConfig = this.getRecastingConfig(role);
			if(null != recastingConfig) {
				buffer.append(recastingConfig.getAttriList());
			}
			//属性勋章
			for(MedalType medalType : MedalType.values()){
				if(!medalType.isAttribute()){
					continue;
				}
				MedalConfig config = this.getAttributeConfig(role, medalType);
				if(null != config){
					buffer.append(config.getAttriList());
				}
			}
		}catch(Exception e){
			logger.error("EquipEffectApp.getAttriBuffer error:",e);
		}
		return buffer;
	}

	@Override
	public MedalRoleData getMedalRoleData(String roleId) {
		return this.roleMedalMap.get(roleId);
	}

	@Override
	public void updateMedal(RoleInstance role, AttributeType attrType) {
		try {
			if(null == role || null == attrType){
				return;
			}
			MedalType medalType = this.attributeMedalTypeMap.get(attrType);
			if(null == medalType){
				return;
			}
			this.doUpdateMedal(role, medalType);
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".updateMedal error: ", e);
		}
		
	}

	@Override
	public int getEquipslotEffectNum() {
		return this.equipslotEffectNum;
	}

	@Override
	public String getMedalName(MedalType medalType) {
		if(null == medalType){
			return "";
		}
		MedalNameIconConfig config = this.getMedalNameIconConfig(medalType.getType());
		if(null == config){
			return "";
		}
		return config.getMedalName();
	}
	
	private MedalNameIconConfig getMedalNameIconConfig(int type){
		return this.defaultIcons.get(type);
	}

	@Override
	public int onLogout(RoleInstance role, Object context) {
		try {
			String roleId = role.getRoleId();
			this.roleMedalMap.remove(roleId);
			//下线保存到SSDB中
			this.medalStorage.saveMedalRoleData(this.getMedalRoleData(roleId));
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".logout error: ", e);
			return 0;
		}
		
		return 1;
	}
	
	@Override
	public int onCleanup(String roleId, Object context) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
