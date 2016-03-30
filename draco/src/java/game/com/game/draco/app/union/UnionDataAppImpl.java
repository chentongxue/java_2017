package com.game.draco.app.union;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.base.MapLogicType;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;

import com.game.draco.GameContext;
import com.game.draco.app.buff.Buff;
import com.game.draco.app.union.config.UnionActivityInfo;
import com.game.draco.app.union.config.UnionAuthority;
import com.game.draco.app.union.config.UnionBase;
import com.game.draco.app.union.config.UnionDes;
import com.game.draco.app.union.config.UnionDonate;
import com.game.draco.app.union.config.UnionDpsGroupRank;
import com.game.draco.app.union.config.UnionDpsResult;
import com.game.draco.app.union.config.UnionDropConf;
import com.game.draco.app.union.config.UnionDropGroup;
import com.game.draco.app.union.config.UnionGemDonate;
import com.game.draco.app.union.config.UnionMail;
import com.game.draco.app.union.config.UnionSummon;
import com.game.draco.app.union.config.UnionUpgrade;
import com.game.draco.app.union.config.UnionVipReward;
import com.game.draco.app.union.config.instance.UnionActivityConsume;
import com.game.draco.app.union.config.instance.UnionInsBoss;
import com.game.draco.app.union.config.instance.UnionInstance;
import com.game.draco.app.union.type.UnionPositionType;
import com.game.draco.app.union.type.UnionPowerType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class UnionDataAppImpl implements UnionDataApp{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	//公会基础数据
	@Getter @Setter private UnionBase unionBase = null;
	
	//公会升级数据
	@Getter @Setter private Map<Integer,UnionUpgrade> unionUpgradeMap = null;
	
	//公会权限数据
	@Getter @Setter private Map<UnionPositionType,Set<UnionPowerType>> unionAuthorityMap = null;
	
	//公会描述数据
	@Getter @Setter private Map<Byte,UnionDes> describeMap = null;
	
	//公会副本数据
	@Getter @Setter private Map<Byte,UnionInstance> unionInstanceMap = null;
	
	//公会DPS排行奖励组数据
	@Getter @Setter private Map<Byte,List<UnionDpsGroupRank>> unionDpsGroupRankMap = null;
	
	//公会DPS结果数据
	@Getter @Setter private Map<String,UnionDpsResult> unionDpsResultMap = null;
	
	//公会DPS结果数据
	@Getter @Setter private Map<Byte,Set<UnionDpsResult>> unionGroupDpsResultMap = null;
	
	//公会捐献数据
	@Getter @Setter private List<UnionDonate> unionDonateList = null;
	
	//公会钻石捐献数据
	@Getter @Setter private List<UnionGemDonate> unionGemDonateList = null;
	
	//公会掉落配置
	@Getter @Setter private List<UnionDropConf> unionDropConfList = null;
	
	//公会功能配置
	@Getter @Setter private Map<Byte,UnionActivityInfo> unionActivityMap = null;
	
	//公会掉落组配置
	@Getter @Setter private Map<Integer,List<UnionDropGroup>> unionDropGroupMap = null;
	
	//公会等级对应开启活动
	@Getter @Setter private Map<Integer,Set<Byte>> activityGroupMap = Maps.newHashMap();
	
	//BOSS掉落组<BossId,组ID>
	@Getter @Setter private Map<String,Set<Integer>> dropMap = Maps.newHashMap();
	
	//活动中BOSS数据
	@Getter @Setter private Map<Byte,UnionInsBoss> insBossMap = null;
	
	@Getter @Setter private List<UnionActivityConsume> activityConsumeList= null;
	
//	@Getter @Setter private List<UnionActivityConfig> activityConfigList= null;
	
	//物品价格对应表
	@Getter @Setter private Map<Integer,Integer> goodsPriceMap = Maps.newConcurrentMap();
	
	//VIP奖励
	@Getter @Setter private Map<Byte,UnionVipReward> vipRewardMap = null;
	
	//邮件
	@Getter @Setter private Map<Byte,UnionMail> unionMailMap = null;
	
	//召唤
	@Getter @Setter private Map<Integer,UnionSummon> unionSummonMap = null;
	
	//最大boss总数
	@Getter @Setter private static int bossNum = 0;
	
	//公会活动开启时间
	@Getter @Setter private static long openTime = 0;
	
	@Override
	public void start() {
		try{
			//加载公会基础数据
			loadUnionBaseConfig();
			//加载公会升级数据
			loadUnionUpgradeConfig();
			//加载公会权限数据
			loadUnionAuthorityConfig();
			//加载公会描述数据
			loadUnionDesConfig();
			//加载公会副本数据
			loadUnionInstanceConfig();
			//加载公会DPS排行奖励数据
			loadUnionDpsGroupRankConfig();
			//加载公会DPS计算结果数据
			loadUnionDpsResultConfig();
			//加载公会掉落配置
			loadUnionDropConfConfig();
			//加载公会捐献数据
			loadUnionDonateConfig();
			//加载公会钻石捐献数据
			loadUnionGemDonateConfig();
			//加载公会掉落组数据
			loadUnionDropGroupConfig();
			//加载公会功能数据
			loadUnionActivityConfig();
			//加载公会活动BOSS数据
			loadUnionActivityInsBossConfig();
			//加载公会活动消耗
			loadUnionActivityConsumeConfig();
//			//加载公会活动开启时间
//			loadUnionActivityOpenTimeConfig();
			
			//加载公会VIP奖励
			loadUnionVipRewardConfig();
			
			//加载公会召唤
			loadUnionSummonConfig();
			
			//加载公会邮件
			loadUnionMailConfig();
			
			//初始化公会数据
			GameContext.getUnionApp().initUnion();
			
			//初始化BOSS击杀记录
			GameContext.getUnionInstanceApp().initKillBossRecord();
			
			//初始化公会副本数据
			GameContext.getUnionInstanceApp().initEvolve();
			
			//初始化公会拍卖行数据
			GameContext.getUnionAuctionApp().initAuction();
			
		
			String fileName = XlsSheetNameType.union_instance_config.getXlsName();
			String sheetName = XlsSheetNameType.union_instance_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			//循环添加活动对应地图
			for(Entry<Byte,UnionInstance> instance : unionInstanceMap.entrySet()){
				sacred.alliance.magic.app.map.Map map = GameContext.getMapApp().getMap(instance.getValue().getMapId());
				if(map == null){
					Log4jManager.CHECK.error("err config the unionInstance,file=" + sourceFile + " sheet=" + sheetName);
					Log4jManager.checkFail();
					continue;
				}
				boolean flag = map.getMapConfig().changeLogicType(MapLogicType.unionInstanceLogic);
				map.getMapConfig().setCopyId(instance.getKey());
				if(!flag){
					Log4jManager.CHECK.error("mapId=" + map.getMapId() + "err config the unionInstance,file=" +  sourceFile + " sheet=" + sheetName);
					Log4jManager.checkFail();
				}
			}
		
			
		}catch(Exception e){
			logger.error("start is error",e);
		}
	}

	@Override
	public void stop() {
		
	}

	@Override
	public void setArgs(Object args) {
		
	}

	/**
	 * 加载公会基础数据
	 */
	private void loadUnionBaseConfig(){
		try{
			String fileName = XlsSheetNameType.union_base_config.getXlsName();
			String sheetName = XlsSheetNameType.union_base_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			unionBase = XlsPojoUtil.getEntity(sourceFile, sheetName, UnionBase.class);
			if(unionBase == null){
				Log4jManager.CHECK.error("not config the unionBaseList,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
				return;
			}
			
			sacred.alliance.magic.app.map.Map map = GameContext.getMapApp().getMap(unionBase.getMapId());
			if(map == null){
				return;
			}
			if(!map.getMapConfig().changeLogicType(MapLogicType.unionTerritoryLogic)) {
				Log4jManager.CHECK.error("unionBase The map logic type config error. mapId= "	+ fileName);
				Log4jManager.checkFail();
			}
		}catch(Exception e){
			logger.error("loadUnionBaseConfig is error",e);
		}
	}
	
	/**
	 * 加载公会召唤数据
	 */
	private void loadUnionSummonConfig(){
		try{
			String fileName = XlsSheetNameType.union_summon_config.getXlsName();
			String sheetName = XlsSheetNameType.union_summon_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			unionSummonMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, UnionSummon.class);
			if(Util.isEmpty(unionSummonMap)){
				Log4jManager.CHECK.error("not config the unionSummon,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
		}catch(Exception e){
			logger.error("loadUnionSummonConfig is error",e);
		}
	}
	
	/**
	 * 加载公会升级数据
	 */
	private void loadUnionUpgradeConfig(){
		try{
			String fileName = XlsSheetNameType.union_upgrade_config.getXlsName();
			String sheetName = XlsSheetNameType.union_upgrade_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			unionUpgradeMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, UnionUpgrade.class);
			if(unionUpgradeMap == null || unionUpgradeMap.isEmpty()){
				Log4jManager.CHECK.error("not config the unionUpgradeMap,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}else{
				for(Entry<Integer,UnionUpgrade> upgrade : unionUpgradeMap.entrySet()){
					Buff buff = GameContext.getBuffApp().getBuff(upgrade.getValue().getBuffId());
					if(buff == null){
						Log4jManager.CHECK.error("not config the unionUpgradeMap BUFF IS NULL,file=" + sourceFile + " sheet=" + sheetName);
						Log4jManager.checkFail();
					}
					String [] funId = upgrade.getValue().getActivityId().split(",");
					if(funId != null && funId.length > 0){
						Set<Byte> funSet = Sets.newHashSet();
						for(String id : funId){
							if("".equals(id)){
								continue;
							}
							funSet.add(Byte.parseByte(id));
						}
						activityGroupMap.put(upgrade.getKey(), funSet);
					}
				}
			}
			
		}catch(Exception e){
			logger.error("loadUnionUpgradeConfig is error",e);
		}
	}
	
	/**
	 * 加载公会权限数据
	 */
	private void loadUnionAuthorityConfig(){
		try{
			String fileName = XlsSheetNameType.union_authority_config.getXlsName();
			String sheetName = XlsSheetNameType.union_authority_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<UnionAuthority> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, UnionAuthority.class);
			unionAuthorityMap = Maps.newHashMap();
			//职位拥有的权限
			for(UnionPositionType position : UnionPositionType.values()){
				this.unionAuthorityMap.put(position, new HashSet<UnionPowerType>());
			}
			if(list == null || list.isEmpty()){
				Log4jManager.CHECK.error("not config the unionUpgradeMap,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}else{
				for(UnionAuthority authority : list){
					if(null == authority){
						continue;
					}
					byte type = authority.getFunId();
					UnionPowerType powerType = UnionPowerType.get(type);
					if(null == powerType){
						Log4jManager.checkFail();
						Log4jManager.CHECK.error("loadExel error : sourceFile = "+fileName +" sheetName ="+sheetName
								+",type="+type+",this type is not exist!");
						continue;
					}
					//职位拥有的权限
					if(authority.isLeaderHold()){
						this.unionAuthorityMap.get(UnionPositionType.Leader).add(powerType);
					}
					if(authority.isDeputyHold()){
						this.unionAuthorityMap.get(UnionPositionType.Deputy).add(powerType);
					}
					if(authority.isEliteHold()){
						this.unionAuthorityMap.get(UnionPositionType.Elite).add(powerType);
					}
					if(authority.isMemberHold()){
						this.unionAuthorityMap.get(UnionPositionType.Member).add(powerType);
					}
				}
			}
		}catch(Exception e){
			logger.error("loadUnionAuthorityConfig is error",e);
		}
	}
	
	/**
	 * 加载公会描述数据
	 */
	private void loadUnionDesConfig(){
		try{
			String fileName = XlsSheetNameType.union_des_config.getXlsName();
			String sheetName = XlsSheetNameType.union_des_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			describeMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, UnionDes.class);
			if(describeMap == null || describeMap.isEmpty()){
				Log4jManager.CHECK.error("not config the describeMap,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
		}catch(Exception e){
			logger.error("loadUnionDesConfig is error",e);
		}
	}
	
	/**
	 * 加载公会掉落配置
	 */
	private void loadUnionDropConfConfig(){
		try{
			String fileName = XlsSheetNameType.union_dropconf_config.getXlsName();
			String sheetName = XlsSheetNameType.union_dropconf_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			unionDropConfList = XlsPojoUtil.sheetToList(sourceFile, sheetName, UnionDropConf.class);
			if(unionDropConfList == null || unionDropConfList.isEmpty()){
				Log4jManager.CHECK.error("not config the unionDropConfList,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
		}catch(Exception e){
			logger.error("loadUnionDropConfConfig is error",e);
		}
	}
	
	/**
	 * 加载开启公会副本消耗配置
	 */
	private void loadUnionActivityConsumeConfig(){
		try{
			String fileName = XlsSheetNameType.union_activity_consume_config.getXlsName();
			String sheetName = XlsSheetNameType.union_activity_consume_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			activityConsumeList = XlsPojoUtil.sheetToList(sourceFile, sheetName, UnionActivityConsume.class);
			if(activityConsumeList == null || activityConsumeList.isEmpty()){
				Log4jManager.CHECK.error("not config the activityConsumeList,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
		}catch(Exception e){
			logger.error("loadUnionActivityConsumeConfig is error",e);
		}
	}
	
	/**
	 * 加载公会副本数据
	 */
	private void loadUnionInstanceConfig(){
		try{
			String fileName = XlsSheetNameType.union_instance_config.getXlsName();
			String sheetName = XlsSheetNameType.union_instance_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			unionInstanceMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, UnionInstance.class);
			if(unionInstanceMap == null || unionInstanceMap.isEmpty()){
				Log4jManager.CHECK.error("not config the unionFunctionMap,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
		}catch(Exception e){
			logger.error("loadUnionInstanceConfig is error",e);
		}
	}
	
	/**
	 * 加载公会DPS排行奖励数据
	 */
	private void loadUnionDpsGroupRankConfig(){
		try{
			String fileName = XlsSheetNameType.union_dpsgrouprank_config.getXlsName();
			String sheetName = XlsSheetNameType.union_dpsgrouprank_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<UnionDpsGroupRank> unionDpsGroupRankList = XlsPojoUtil.sheetToList(sourceFile, sheetName, UnionDpsGroupRank.class);
			if(unionDpsGroupRankList == null || unionDpsGroupRankList.isEmpty()){
				Log4jManager.CHECK.error("not config the unionDpsGroupRankList,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}else{
				unionDpsGroupRankMap = Maps.newHashMap();
				List<UnionDpsGroupRank> list = null;
				for(UnionDpsGroupRank groupRank : unionDpsGroupRankList){
					if(unionDpsGroupRankMap.containsKey(groupRank.getGroupId())){
						list = unionDpsGroupRankMap.get(groupRank.getGroupId());
						list.add(groupRank);
					}else{
						list = Lists.newArrayList();
						list.add(groupRank);
						unionDpsGroupRankMap.put(groupRank.getGroupId(), list);
					}
				}
			}
		}catch(Exception e){
			logger.error("loadUnionDpsGroupRankConfig is error",e);
		}
	}
	
	/**
	 * 加载公会DPS计算结果数据
	 */
	private void loadUnionDpsResultConfig(){
		try{
			String fileName = XlsSheetNameType.union_dpsresult_config.getXlsName();
			String sheetName = XlsSheetNameType.union_dpsresult_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			unionDpsResultMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, UnionDpsResult.class);
			if(unionDpsResultMap == null || unionDpsResultMap.isEmpty()){
				Log4jManager.CHECK.error("not config the unionDpsResultMap,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}else{
				unionGroupDpsResultMap = Maps.newHashMap();
				for(Entry<String,UnionDpsResult> result: unionDpsResultMap.entrySet()){
					Set<UnionDpsResult> boss = null;
					if(unionGroupDpsResultMap.containsKey(result.getValue().getGroupId())){
						boss = unionGroupDpsResultMap.get(result.getValue().getGroupId());
						boss.add(result.getValue());
					}else{
						boss = Sets.newHashSet();
						boss.add(result.getValue());
						unionGroupDpsResultMap.put(result.getValue().getGroupId(), boss);
					}
					
					String [] dropGroupId = result.getValue().getDropgroupId().split(",");
					if(dropGroupId != null && dropGroupId.length > 0){
						Set<Integer> groupSet = Sets.newHashSet();
						for(String id : dropGroupId){
							if("".equals(id)){
								continue;
							}
							groupSet.add(Integer.parseInt(id));
						}
						dropMap.put(result.getKey(),groupSet);
					}
				}
			}
		}catch(Exception e){
			logger.error("loadUnionDpsResultConfig is error",e);
		}
	}
	
	@Override
	public Set<Integer> getDropMap(String bossId){
		return dropMap.get(bossId);
	}
	
	/**
	 * 加载公会捐献数据
	 */
	private void loadUnionDonateConfig(){
		try{
			String fileName = XlsSheetNameType.union_donate_config.getXlsName();
			String sheetName = XlsSheetNameType.union_donate_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			unionDonateList = XlsPojoUtil.sheetToList(sourceFile, sheetName, UnionDonate.class);
			if(Util.isEmpty(unionDonateList)){
				Log4jManager.CHECK.error("not config the unionDonateList,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
		}catch(Exception e){
			logger.error("loadUnionDonateConfig is error",e);
		}
	}
	
	/**
	 * 加载公会钻石捐献数据
	 */
	private void loadUnionGemDonateConfig(){
		try{
			String fileName = XlsSheetNameType.union_gem_donate_config.getXlsName();
			String sheetName = XlsSheetNameType.union_gem_donate_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			unionGemDonateList = XlsPojoUtil.sheetToList(sourceFile, sheetName, UnionGemDonate.class);
			if(Util.isEmpty(unionGemDonateList)){
				Log4jManager.CHECK.error("not config the unionGemDonateList,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
				return;
			}
			
			int gemDonateMaxCount = 0;
			for(UnionGemDonate gem : unionGemDonateList){
				if(gem.getMax() > gemDonateMaxCount){
					gemDonateMaxCount = gem.getMax();
				}
			}
			
			for(UnionGemDonate gem : unionGemDonateList){
				gem.setMaxCount((byte)gemDonateMaxCount);
			}
			
		}catch(Exception e){
			logger.error("loadUnionGemDonateConfig is error",e);
		}
	}
	
	/**
	 * 加载公会掉落组数据
	 */
	private void loadUnionDropGroupConfig(){
		try{
			String fileName = XlsSheetNameType.union_dropgroup_config.getXlsName();
			String sheetName = XlsSheetNameType.union_dropgroup_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<UnionDropGroup> dropGroupList = XlsPojoUtil.sheetToList(sourceFile, sheetName, UnionDropGroup.class);
			if(dropGroupList == null || dropGroupList.isEmpty()){
				Log4jManager.CHECK.error("not config the unionDropGroupList,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}else{
				unionDropGroupMap = Maps.newHashMap();
				List<UnionDropGroup> list = null;
				for(UnionDropGroup drop : dropGroupList){
					if(unionDropGroupMap.containsKey(drop.getDropGroupId())){
						list = unionDropGroupMap.get(drop.getDropGroupId());
						list.add(drop);
					}else{
						list = Lists.newArrayList();
						list.add(drop);
						unionDropGroupMap.put(drop.getDropGroupId(), list);
					}
					goodsPriceMap.put(drop.getGoodsId(), drop.getBasePrice());
				}
			}
		}catch(Exception e){
			logger.error("loadUnionDropGroupConfig is error",e);
		}
	}
	
	/**
	 * 加载公会功能数据
	 */
	private void loadUnionActivityConfig(){
		try{
			String fileName = XlsSheetNameType.union_activity_config.getXlsName();
			String sheetName = XlsSheetNameType.union_activity_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			unionActivityMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, UnionActivityInfo.class);
			if(Util.isEmpty(unionActivityMap)){
				Log4jManager.CHECK.error("not config the unionFunctionMap,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
		}catch(Exception e){
			logger.error("loadUnionFunctionConfig is error",e);
		}
	}

	@Override
	public UnionUpgrade getUnionUpgrade(int unionLevel) {
		return unionUpgradeMap.get(unionLevel);
	}

	@Override
	public Set<UnionPowerType> getPowerTypeSet(UnionPositionType positionType) {
		return unionAuthorityMap.get(positionType);
	}

	@Override
	public UnionInstance getUnionInstance(byte funId) {
		return unionInstanceMap.get(funId);
	}

	@Override
	public List<UnionDpsGroupRank> getUnionDpsGroupRank(byte groupId) {
		return unionDpsGroupRankMap.get(groupId);
	}

	@Override
	public UnionDpsResult getUnionDpsResultByBossId(String bossId) {
		return unionDpsResultMap.get(bossId);
	}

	@Override
	public Set<Byte> getActivityGroupMap(int level) {
			if(activityGroupMap.containsKey(level)){
				return activityGroupMap.get(level);
			}
		return null;
	}

	/**
	 * 加载公会活动BOSS数据
	 */
	private void loadUnionActivityInsBossConfig(){
		try{
			String fileName = XlsSheetNameType.union_boss_config.getXlsName();
			String sheetName = XlsSheetNameType.union_boss_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			insBossMap = XlsPojoUtil.sheetToGenericLinkedMap(sourceFile, sheetName, UnionInsBoss.class);
			if(insBossMap == null || insBossMap.isEmpty()){
				Log4jManager.CHECK.error("not config the insBossMap,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}else{
				for(Entry<Byte,UnionInsBoss> boss : insBossMap.entrySet()){
					bossNum += boss.getValue().getBossNum();
				}
			}
		}catch(Exception e){
			logger.error("loadUnionActivityInsBossConfig is error",e);
		}
	}
	
	@Override
	public UnionInsBoss getUnionInsBossMap(byte activityId) {
		return insBossMap.get(activityId);
	}

	@Override
	public List<UnionDropGroup> getUnionDropGroup(int groupId){
		return unionDropGroupMap.get(groupId);
	}

	@Override
	public Set<UnionDpsResult> getUnionDpsResult(byte groupId) {
		return unionGroupDpsResultMap.get(groupId);
	}

	@Override
	public byte getGroupId(String bossId) {
		for(Entry<Byte,Set<UnionDpsResult>> boss : unionGroupDpsResultMap.entrySet()){
			for(UnionDpsResult bossResult : boss.getValue()){
				if(bossResult.getBossId().equals(bossId)){
					return boss.getKey();
				}
			}
		}
		return -1;
	}

	@Override
	public UnionActivityConsume getUnionActivityConsume(byte num) {
		if(activityConsumeList != null && !activityConsumeList.isEmpty()){
			for(UnionActivityConsume activityConsume : activityConsumeList){
				if(num >= activityConsume.getBeginNum() && num <= activityConsume.getEndNum()){
					return activityConsume;
				}
			}
		}
		return null;
	}

	@Override
	public int getActivityMaxBossNum() {
		return bossNum;
	}

	
	@Override
	public int getGoodsBasePrice(int goodsId){
		if(goodsPriceMap.containsKey(goodsId)){
			return goodsPriceMap.get(goodsId);
		}
		return 100;
	}
	
	/**
	 * 加载公会vip奖励
	 */
	private void loadUnionVipRewardConfig(){
		try{
			String fileName = XlsSheetNameType.union_activity_vip_config.getXlsName();
			String sheetName = XlsSheetNameType.union_activity_vip_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			vipRewardMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, UnionVipReward.class);
			if(Util.isEmpty(vipRewardMap)){
				Log4jManager.CHECK.error("not config the vipRewardMap,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
		}catch(Exception e){
			logger.error("loadUnionVipRewardConfig is error",e);
		}
	}
	
	/**
	 * 加载公会vip奖励
	 */
	private void loadUnionMailConfig(){
		try{
			String fileName = XlsSheetNameType.union_activity_mail_config.getXlsName();
			String sheetName = XlsSheetNameType.union_activity_mail_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			unionMailMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, UnionMail.class);
			if(Util.isEmpty(unionMailMap)){
				Log4jManager.CHECK.error("not config the vipRewardMap,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
		}catch(Exception e){
			logger.error("loadUnionVipRewardConfig is error",e);
		}
	}

	@Override
	public UnionVipReward getUnionVipReward(byte vipLevel) {
		return vipRewardMap.get(vipLevel);
	}

	@Override
	public UnionMail getUnionMail(byte type) {
		return unionMailMap.get(type);
	}

	@Override
	public UnionSummon getUnionSummon(int groupId) {
		return unionSummonMap.get(groupId);
	}

}