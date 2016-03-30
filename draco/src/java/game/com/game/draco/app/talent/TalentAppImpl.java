package com.game.draco.app.talent;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.AttributeOperateLevelBean;

import com.game.draco.GameContext;
import com.game.draco.app.talent.config.TalentAttr;
import com.game.draco.app.talent.config.TalentBase;
import com.game.draco.app.talent.config.TalentCondition;
import com.game.draco.app.talent.config.TalentConsume;
import com.game.draco.app.talent.config.TalentConsumeInfo;
import com.game.draco.app.talent.config.TalentDes;
import com.game.draco.app.talent.config.TalentGoods;
import com.game.draco.app.talent.config.TalentGroup;
import com.game.draco.app.talent.config.TalentInfo;
import com.game.draco.app.talent.config.TalentLevelUp;
import com.game.draco.app.talent.config.TalentRank;
import com.game.draco.app.talent.config.TalentShop;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class TalentAppImpl implements TalentApp {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	//天赋基础数据
	@Getter @Setter private Map<Integer,List<TalentBase>> talentBaseMap = Maps.newHashMap();
	
	//天赋基础数据
	@Getter @Setter private Map<Integer,TalentInfo> talentInfoMap = Maps.newHashMap();
	
	//天赋升级数据
	@Getter @Setter private Map<Integer,TalentLevelUp> talentLevelUpMap = Maps.newHashMap();
	
	//天赋排行数据
	@Getter @Setter private Map<Integer,Integer> talentRankMap = Maps.newHashMap();
	
	//天赋消耗数据
	@Getter @Setter private Map<Byte,TalentConsume> talentConsumeMap = Maps.newHashMap();
	
	//天赋消耗属性数据
	@Getter @Setter private Map<Byte,List<TalentAttr>> talentAttrMap = Maps.newHashMap();
	
	//天赋消耗物品数据
	@Getter @Setter private Map<Byte,TalentGoods> talentGoodsMap = Maps.newHashMap();
	
	//天赋消耗属性物品数据
	@Getter @Setter private Map<Byte,TalentConsumeInfo> talentConsumeInfoMap = Maps.newHashMap();
	
	//天赋描述数据
	@Getter @Setter private Map<Integer,TalentDes> talentDesMap = Maps.newHashMap();
	
	//天赋条件数据
	@Getter @Setter private Map<Byte,TalentCondition> talentConditionMap = Maps.newHashMap();
	
	//天赋组数据
	@Getter @Setter private Map<Byte,List<TalentGroup>> talentGroupMap = Maps.newHashMap();
	
	//商店ID
	@Getter @Setter private TalentShop talentShop = null; 
	
	@Getter @Setter private int initTalentPoint ;
	
	/**
	 * 加载天赋基础数据
	 */
	private void loadTalentBaseConfig(){
		try{
			String fileName = XlsSheetNameType.talent_base_config.getXlsName();
			String sheetName = XlsSheetNameType.talent_base_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<TalentBase> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, TalentBase.class);
			if(Util.isEmpty(list)){
				Log4jManager.CHECK.error("not config the list,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
				return;
			}
			
			for(TalentBase base : list){
				List<TalentBase> baseList = null;
				if(talentBaseMap.containsKey(base.getTalentId())){
					baseList = talentBaseMap.get(base.getTalentId());
				}else{
					baseList = Lists.newArrayList();
					talentBaseMap.put(base.getTalentId(), baseList);
				}
				baseList.add(base);
			}
			
		}catch(Exception e){
			logger.error("loadTalentBaseConfig is error",e);
		}
	}
	
	/**
	 * 加载天赋基础数据
	 */
	private void loadTalentInfoConfig(){
		try{
			String fileName = XlsSheetNameType.talent_info_config.getXlsName();
			String sheetName = XlsSheetNameType.talent_info_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			talentInfoMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, TalentInfo.class);
			if(Util.isEmpty(talentInfoMap)){
				Log4jManager.CHECK.error("not config the list,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
				return;
			}
			for(TalentInfo info : this.talentInfoMap.values()){
				this.initTalentPoint += info.getAttrValue() ;
			}
		}catch(Exception e){
			logger.error("loadTalentInfoConfig is error",e);
		}
	}
	
	/**
	 * 加载天赋商店数据
	 */
	private void loadTalentShopConfig(){
		try{
			String fileName = XlsSheetNameType.talent_shop_config.getXlsName();
			String sheetName = XlsSheetNameType.talent_shop_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			talentShop = XlsPojoUtil.getEntity(sourceFile, sheetName, TalentShop.class);
			if(Util.isEmpty(talentShop.getShopId())){
				Log4jManager.CHECK.error("not config the list,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
				return;
			}
			
		}catch(Exception e){
			logger.error("loadTalentShopConfig is error",e);
		}
	}
	
	/**
	 * 加载天赋升级数据
	 */
	private void loadTalentLevelUpConfig(){
		try{
			String fileName = XlsSheetNameType.talent_levelup_config.getXlsName();
			String sheetName = XlsSheetNameType.talent_levelup_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			talentLevelUpMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, TalentLevelUp.class);
			if(Util.isEmpty(talentLevelUpMap)){
				Log4jManager.CHECK.error("not config the talentLevelUpMap,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
		}catch(Exception e){
			logger.error("loadTalentLevelUpConfig is error",e);
		}
	}
	
	/**
	 * 加载天赋描述数据
	 */
	private void loadTalentDesConfig(){
		try{
			String fileName = XlsSheetNameType.talent_des_config.getXlsName();
			String sheetName = XlsSheetNameType.talent_des_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			talentDesMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, TalentDes.class);
			if(Util.isEmpty(talentDesMap)){
				Log4jManager.CHECK.error("not config the talentDesMap,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
		}catch(Exception e){
			logger.error("loadTalentDesConfig is error",e);
		}
	}
	
	/**
	 * 加载天赋排行数据
	 */
	private void loadTalentRankConfig(){
		try{
			String fileName = XlsSheetNameType.talent_rank_config.getXlsName();
			String sheetName = XlsSheetNameType.talent_rank_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<TalentRank> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, TalentRank.class);
			if(Util.isEmpty(list)){
				Log4jManager.CHECK.error("not config the list,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
				return;
			}
			
			for(TalentRank rank : list){
				talentRankMap.put(rank.getRank(), rank.getProb());
			}
			
		}catch(Exception e){
			logger.error("loadTalentRankConfig is error",e);
		}
	}
	
	/**
	 * 加载天赋消耗数据
	 */
	private void loadTalentConsumeConfig(){
		try{
			String fileName = XlsSheetNameType.talent_consume_config.getXlsName();
			String sheetName = XlsSheetNameType.talent_consume_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			talentConsumeMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, TalentConsume.class);
			if(Util.isEmpty(talentConsumeMap)){
				Log4jManager.CHECK.error("not config the talentConsumeMap,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
		}catch(Exception e){
			logger.error("loadTalentConsumeConfig is error",e);
		}
	}
	
	/**
	 * 加载天赋消耗属性数据
	 */
	private void loadTalentAttrConfig(){
		try{
			String fileName = XlsSheetNameType.talent_attr_config.getXlsName();
			String sheetName = XlsSheetNameType.talent_attr_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<TalentAttr> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, TalentAttr.class);
			if(Util.isEmpty(list)){
				Log4jManager.CHECK.error("not config the List<TalentAttr> list,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
				return;
			}
			List<TalentAttr> talentAttrList = null;
			for(TalentAttr talentAttr : list){
				if(talentAttrMap.containsKey(talentAttr.getGroupId())){
					talentAttrList = talentAttrMap.get(talentAttr.getGroupId());
				}else{
					talentAttrList = Lists.newArrayList();
					talentAttrMap.put(talentAttr.getGroupId(),talentAttrList);
				}
				talentAttrList.add(talentAttr);
			}
			
		}catch(Exception e){
			logger.error("loadTalentAttrConfig is error",e);
		}
	}
	
	/**
	 * 加载天赋消耗物品数据
	 */
	private void loadTalentGoodsConfig(){
		try{
			String fileName = XlsSheetNameType.talent_goods_config.getXlsName();
			String sheetName = XlsSheetNameType.talent_goods_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			talentGoodsMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, TalentGoods.class);
			if(Util.isEmpty(talentGoodsMap)){
				Log4jManager.CHECK.error("not config the talentGoodsMap,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
		}catch(Exception e){
			logger.error("loadTalentGoodsConfig is error",e);
		}
	}
	
	/**
	 * 天赋条件数据
	 */
	private void loadTalentConditionConfig(){
		try{
			String fileName = XlsSheetNameType.talent_condition_config.getXlsName();
			String sheetName = XlsSheetNameType.talent_condition_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			talentConditionMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, TalentCondition.class);
			if(Util.isEmpty(talentConditionMap)){
				Log4jManager.CHECK.error("not config the talentConditionMap,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
		}catch(Exception e){
			logger.error("loadTalentConditionConfig is error",e);
		}
	}
	
	/**
	 * 天赋组数据
	 */
	private void loadTalentGroupConfig(){
		try{
			String fileName = XlsSheetNameType.talent_group_config.getXlsName();
			String sheetName = XlsSheetNameType.talent_group_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<TalentGroup> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, TalentGroup.class);
			if(Util.isEmpty(list)){
				Log4jManager.CHECK.error("not config the list,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
				return;
			}
			
			for(TalentGroup group : list){
				if(talentGroupMap.containsKey(group.getType())){
					talentGroupMap.get(group.getType()).add(group);
				}else{
					List<TalentGroup> groupList = Lists.newArrayList();
					groupList.add(group);
					talentGroupMap.put(group.getType(),groupList);
				}
			}
			
		}catch(Exception e){
			logger.error("loadTalentConditionConfig is error",e);
		}
	}
	
	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void start() {
		try{
			//加载天赋基本数据
			loadTalentBaseConfig();
			//加载天赋升级数据
			loadTalentLevelUpConfig();
			//加载天赋排行数据
			loadTalentRankConfig();
			//加载天赋消耗数据
			loadTalentConsumeConfig();
			//加载天赋消耗属性数据
			loadTalentAttrConfig();
			//加载天赋消耗物品数据
			loadTalentGoodsConfig();
			//加载天赋描述数据
			loadTalentDesConfig();
			//加载天赋条件数据
			loadTalentConditionConfig();
			//封装天赋升级消耗数据（属性、物品）
			initConsumeInfo();
			//加载商店数据
			loadTalentShopConfig();
			
			loadTalentGroupConfig();
			loadTalentInfoConfig();
		}catch(Exception e){
			logger.error("start is error",e);
		}
	}

	@Override
	public void stop() {
		
	}

	@Override
	public TalentLevelUp getTalentLevelUp(int level) {
		return talentLevelUpMap.get(level);
	}
	
	private void initConsumeInfo(){
		for(Entry<Byte,TalentConsume> consume : talentConsumeMap.entrySet()){
			TalentConsumeInfo info = new TalentConsumeInfo();
			if(talentAttrMap.containsKey(consume.getValue().getAttrGroup())){
				List<TalentAttr> attrList = talentAttrMap.get(consume.getValue().getAttrGroup());
				for(TalentAttr talentAttr : attrList){
					AttributeOperateLevelBean attrBean = new AttributeOperateLevelBean(AttributeType.get(talentAttr.getAttrType()),talentAttr.getAttrValue(),talentAttr.getMinLevel(),talentAttr.getMaxLevel());
					info.getAttrList().add(attrBean);
				}
			}
			
			if(talentGoodsMap.containsKey(consume.getValue().getGoodsGroup())){
				TalentGoods goods = talentGoodsMap.get(consume.getValue().getGoodsGroup());
				GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goods.getGoodsId());
				GoodsLiteNamedItem goodsItem = goodsBase.getGoodsLiteNamedItem();
				goodsItem.setNum(goods.getGoodsNum());
				info.getGoodsGroup().add(goodsItem);
			}
			info.setType(consume.getKey());
			talentConsumeInfoMap.put(consume.getKey() , info);
		}
		
	}

	@Override
	public TalentConsumeInfo getTalentConsumeInfo(byte type) {
		return talentConsumeInfoMap.get(type);
	}

	@Override
	public TalentDes getTalentDes(int talentId) {
		return talentDesMap.get(talentId);
	}

	@Override
	public TalentCondition getTalentCondition(byte id) {
		return talentConditionMap.get(id);
	}

	@Override
	public List<TalentGroup> getTalentGroupList(byte type) {
		return talentGroupMap.get(type);
	}

	@Override
	public Map<Integer,TalentInfo> getTalentInfoMap() {
		return talentInfoMap;
	}


}