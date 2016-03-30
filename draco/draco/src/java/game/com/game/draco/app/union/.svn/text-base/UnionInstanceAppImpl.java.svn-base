package com.game.draco.app.union;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import org.python.google.common.collect.Lists;
import org.python.google.common.collect.Maps;
import org.python.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.union.config.UnionActivityInfo;
import com.game.draco.app.union.config.UnionDpsGroupRank;
import com.game.draco.app.union.config.UnionDpsResult;
import com.game.draco.app.union.config.UnionDropConf;
import com.game.draco.app.union.config.UnionDropGroup;
import com.game.draco.app.union.config.instance.UnionInstance;
import com.game.draco.app.union.domain.Union;
import com.game.draco.app.union.domain.UnionMember;
import com.game.draco.app.union.domain.auction.Auction;
import com.game.draco.app.union.domain.auction.GoodsItem;
import com.game.draco.app.union.domain.instance.RoleDps;
import com.game.draco.app.union.domain.instance.UnionActivityBossRecord;
import com.game.draco.app.union.domain.instance.UnionBoss;
import com.game.draco.app.union.domain.instance.UnionKillBossRecord;
import com.game.draco.app.union.domain.instance.UnionRoleDpsRecord;

public class UnionInstanceAppImpl implements UnionInstanceApp{
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Getter @Setter //公会副本boss计算 <公会ID，<活动ID,Map<groupId,Map<角色ID,副本数据>>>>
	private Map<String,Map<Byte,Map<Byte,Map<Integer,RoleDps>>>> roleDpsRecordMap = Maps.newConcurrentMap();
	
	@Getter @Setter  //<公会ID，<活动ID,Map<groupId,BOSS数据>>>
	private Map<String,Map<Byte,Map<Byte,UnionActivityBossRecord>>> bossRecordMap = Maps.newConcurrentMap();
	
	@Getter @Setter //<公会ID，Set<bossId>>
	private Map<String,Set<String>> killBossMap = Maps.newConcurrentMap();
	
	public final static float TEN_THOUSAD_F = 10000.f;
	
	/**
	 * 初始化副本状态
	 */
	@Override
	public void initEvolve(){
		try{
			List<UnionActivityBossRecord> bossRecordList = GameContext.getBaseDAO().selectAll(UnionActivityBossRecord.class);
			List<UnionRoleDpsRecord> roleDpsRecordList = GameContext.getBaseDAO().selectAll(UnionRoleDpsRecord.class);
			if(bossRecordList != null && !bossRecordList.isEmpty()){
				for(UnionActivityBossRecord record : bossRecordList){
					//添加BOSS数据
					addBossRecordMap(record);
				}
			}
			
			if(roleDpsRecordList != null && !roleDpsRecordList.isEmpty()){
				for(UnionRoleDpsRecord record : roleDpsRecordList){
					//添加角色DPS数据
					List<RoleDps> roleDpsList = record.parseRoleDpsData();
					for(RoleDps roleDps : roleDpsList){
						addRoleDpsRecordMap(roleDps);
					}
				}
			}
		}catch(Exception e){
			logger.error("initEvolve",e);
		}
	}
	
	/**
	 * 初始化BOSS击杀记录
	 */
	@Override
	public void initKillBossRecord(){
		List<UnionKillBossRecord> killBossList = GameContext.getBaseDAO().selectAll(UnionKillBossRecord.class);
		if(killBossList != null && !killBossList.isEmpty()){
			for(UnionKillBossRecord record : killBossList){
				addKillBossRecord(record.getUnionId(),record.parseKillBossData());
			}
		}
	}
	
	
	/**
	 * 添加BOSS数据
	 * @param record
	 */
	private void addBossRecordMap(UnionActivityBossRecord record){
		try{
			Map<Byte,Map<Byte,UnionActivityBossRecord>> insMap = null;
			Map<Byte,UnionActivityBossRecord> bossMap = null;
			if(record.getUnionId() != null && !"".equals(record.getUnionId())){
				if(bossRecordMap.containsKey(record.getUnionId())){
					insMap = bossRecordMap.get(record.getUnionId());
					if(insMap.containsKey(record.getActivityId())){
						bossMap = insMap.get(record.getActivityId());
					}else{
						bossMap = Maps.newConcurrentMap();
						insMap.put(record.getActivityId(), bossMap);
					}
					bossMap.put(record.getGroupId(), record);
				}else{
					insMap = Maps.newConcurrentMap();
					bossMap = Maps.newConcurrentMap();
					bossMap.put(record.getGroupId(), record);
					insMap.put(record.getActivityId(), bossMap);
					bossRecordMap.put(record.getUnionId(), insMap);
				}
			}
		}catch(Exception e){
			logger.error("addBossRecordMap",e);
		}
	}
	
	/**
	 * 添加角色DPS数据
	 * @param record
	 */
	private void addRoleDpsRecordMap(RoleDps record){
		Map<Byte,Map<Byte,Map<Integer,RoleDps>>> insMap = null;
		Map<Byte,Map<Integer,RoleDps>> groupMap = null;
		Map<Integer,RoleDps> dpsMap = null;
		if(record.getUnionId() != null && !"".equals(record.getUnionId())){
			if(roleDpsRecordMap.containsKey(record.getUnionId())){
				insMap = roleDpsRecordMap.get(record.getUnionId());
				if(insMap.containsKey(record.getActivityId())){
					groupMap = insMap.get(record.getActivityId());
					if(groupMap.containsKey(record.getGroupId())){
						dpsMap = groupMap.get(record.getGroupId());
					}else{
						dpsMap = Maps.newConcurrentMap();
						groupMap.put(record.getGroupId(), dpsMap);
					}
					dpsMap.put(record.getRoleId(),record);
				}else{
					dpsMap = Maps.newConcurrentMap();
					groupMap = Maps.newConcurrentMap();
					dpsMap.put(record.getRoleId(), record);
					groupMap.put(record.getGroupId(), dpsMap);
					insMap.put(record.getActivityId(), groupMap);
				}
			}else{
				dpsMap = Maps.newConcurrentMap();
				groupMap = Maps.newConcurrentMap();
				insMap = Maps.newConcurrentMap();
				dpsMap.put(record.getRoleId(), record);
				groupMap.put(record.getGroupId(), dpsMap);
				insMap.put(record.getActivityId(), groupMap);
				roleDpsRecordMap.put(record.getUnionId(), insMap);
			}
		}
	}
	
	/**
	 * 添加DPS
	 * @param roleId
	 * @param bossId
	 * @param dps
	 */
	@Override
	public void addDps(int roleId,String unionId,byte activityId,byte groupId,int dps){
		UnionMember unionMember = GameContext.getUnionApp().getUnionMember(unionId, roleId);
		if(unionMember != null){
			RoleDps record = new RoleDps();
			record.setGroupId(groupId);
			record.setDps(dps);
			record.setActivityId(activityId);
			record.setRoleId(roleId);
			record.setRoleName(unionMember.getRoleName());
			record.setUnionId(unionId);
			addRoleDpsRecordMap(record);
		}
	}
	
	/**
	 * 重置活动
	 * @param activityId
	 */
	@Override
	public void resetActivity(byte activityId){
		try{
			boolean roleDpsFlag = false,bossFlag = false;
			
			//<公会ID，<活动ID,Map<groupId,Map<角色ID,副本数据>>>>
			for(Entry<String,Map<Byte,Map<Byte,Map<Integer,RoleDps>>>> roleDpsMap : roleDpsRecordMap.entrySet()){
				for(Entry<Byte,Map<Byte,Map<Integer,RoleDps>>> activityMap : roleDpsMap.getValue().entrySet()){
					if(activityMap.getKey() == activityId){
						Map<Byte,Map<Integer,RoleDps>> groupMap = Maps.newConcurrentMap();
						activityMap.setValue(groupMap);
						roleDpsFlag = true;
					}
				}
				if(roleDpsFlag){
					//清表
					GameContext.getBaseDAO().delete(UnionActivityBossRecord.class, "unionId", roleDpsMap.getKey(),"activityId",activityId);
				}
			}
	
			//<公会ID，<活动ID,Map<groupId,BOSS数据>>>
			for(Entry<String,Map<Byte,Map<Byte,UnionActivityBossRecord>>> bossMap : bossRecordMap.entrySet()){
				for(Entry<Byte,Map<Byte,UnionActivityBossRecord>> activityMap : bossMap.getValue().entrySet()){
					if(activityMap.getKey() == activityId){
						Map<Byte,UnionActivityBossRecord> groupMap = Maps.newConcurrentMap();
						activityMap.setValue(groupMap);
						bossFlag = true;
					}
				}
				if(bossFlag){
					//清表
					GameContext.getBaseDAO().delete(UnionActivityBossRecord.class, "unionId", bossMap.getKey(),"activityId",activityId);
				}
			}
		}catch(Exception e){
			logger.error("resetActivity",e);
		}
	}
	
	/**
	 * 获得角色dps
	 */
	private int getRoleDps(String unionId,int roleId,int activityId,byte groupId){
		Map<Byte,Map<Byte,Map<Integer,RoleDps>>> insMap = null;
		Map<Byte,Map<Integer,RoleDps>> groupMap = null;
		Map<Integer,RoleDps> dpsMap = null;
		if(roleDpsRecordMap.containsKey(unionId)){
			insMap = roleDpsRecordMap.get(unionId);
			if(insMap.containsKey(activityId)){
				groupMap = insMap.get(activityId);
				if(groupMap.containsKey(groupId)){
					dpsMap = groupMap.get(groupId);
					if(dpsMap.containsKey(roleId)){
						return dpsMap.get(roleId).getDps();
					}
				}
			}
		}
		return 0;
	}
	
	/**
	 * 设置Boss死亡状态 发奖
	 * @param unionId
	 * @param bossId
	 * @param state
	 */
	@Override
	public void setBossState(String unionId,byte activityId,byte groupId,byte state,int maxHp){
		UnionActivityBossRecord record = initBossRecord(unionId,activityId,groupId,state,maxHp);
		addBossRecordMap(record);
		GameContext.getBaseDAO().saveOrUpdate(record);
		saveRoleDps(unionId,activityId,groupId);
		calculateReward(unionId,groupId,activityId);
	}
	
	/**
	 * 设置Boss死亡状态 发奖
	 * @param unionId
	 * @param bossId
	 * @param state
	 */
	@Override
	public void setTeamBossState(String unionId,byte activityId,byte groupId,byte state,int maxHp,Map<Integer,RoleDps> roleDpsMap){
		UnionActivityBossRecord record = initBossRecord(unionId,activityId,groupId,state,maxHp);
		addBossRecordMap(record);
		GameContext.getBaseDAO().saveOrUpdate(record);
		calculateTeamReward(unionId,groupId,activityId,roleDpsMap);
	}
	
	/**
	 * 保存角色DPS数据
	 * @param unionId
	 * @param activity
	 * @param bossId
	 */
	private void saveRoleDps(String unionId,byte activityId,byte groupId){
		Map<Integer, RoleDps> roleDpsRecordMap = getUnionRoleDpsMap(unionId, activityId, groupId);
		if(roleDpsRecordMap != null && !roleDpsRecordMap.isEmpty()){
			UnionRoleDpsRecord record = new UnionRoleDpsRecord();
			record.buildRoelDpsData(roleDpsRecordMap);
			record.setActivityId(activityId);
			record.setGroupId(groupId);
			record.setUnionId(unionId);
			GameContext.getBaseDAO().saveOrUpdate(record);
		}
	}
	
	/**
	 * 获得Boss状态
	 */
	@Override
	public byte getInsBossState(String unionId,byte activity,byte groupId){
		Map<Byte,Map<Byte,UnionActivityBossRecord>> insMap = null;
		Map<Byte,UnionActivityBossRecord> bossMap = null;
		if(bossRecordMap.containsKey(unionId)){
			insMap = bossRecordMap.get(unionId);
			if(insMap.containsKey(activity)){
				bossMap = insMap.get(activity);
				if(bossMap.containsKey(groupId)){
					return bossMap.get(groupId).getState();
				}
			}
		}
		return 0;
	}
	
	/**
	 * 初始化boss数据
	 * @param unionId
	 * @param activityId
	 * @param bossId
	 * @param state
	 * @return
	 */
	private UnionActivityBossRecord initBossRecord(String unionId,byte acticityId,byte groupId,byte state,int maxHp){
		UnionActivityBossRecord record = new UnionActivityBossRecord();
		record.setGroupId(groupId);
		record.setActivityId(acticityId);
		record.setState(state);
		record.setUnionId(unionId);
		record.setBossHp(maxHp);
		return record;
	}
	
	@Override
	public List<RoleDps> getBossDpsRank(String unionId,byte activityId,byte groupId){
		
		Map<Byte,Map<Byte,Map<Integer,RoleDps>>> insMap = null;
		Map<Byte,Map<Integer,RoleDps>> groupMap = null;
		Map<Integer,RoleDps> dpsMap = null;
		if(roleDpsRecordMap.containsKey(unionId)){
			insMap = roleDpsRecordMap.get(unionId);
			if(insMap.containsKey(activityId)){
				groupMap = insMap.get(activityId);
				if(groupMap.containsKey(groupId)){
					dpsMap = groupMap.get(groupId);
				}
			}
		}
		
		if(dpsMap != null && !dpsMap.isEmpty()){
			List<RoleDps> list = Lists.newArrayList();
			list.addAll(dpsMap.values());
			if(Util.isEmpty(list)){
				return null;
			}
			sortRoleDps(list);
			return list;
		}
		return null;
	}
	
	@Override
	public void sortRoleDps(List<RoleDps> list){
		Collections.sort(list, new Comparator<RoleDps>() {
			public int compare(RoleDps info1, RoleDps info2) {
				if(info1.getDps() > info2.getDps()){
					return -1;
				}
				return 0;
			}
		});
	}
	
	@Override
	public Map<Integer, RoleDps> getUnionRoleDpsMap(String unionId,
			byte activityId, byte groupId) {
		Map<Byte,Map<Byte,Map<Integer,RoleDps>>> insMap = null;
		Map<Byte,Map<Integer,RoleDps>> groupMap = null;
		Map<Integer,RoleDps> dpsMap = null;
		if(roleDpsRecordMap.containsKey(unionId)){
			insMap = roleDpsRecordMap.get(unionId);
			if(insMap.containsKey(activityId)){
				groupMap = insMap.get(activityId);
				if(groupMap.containsKey(groupId)){
					dpsMap = groupMap.get(groupId);
				}
			}
		}
		return dpsMap;
	}

	/**
	 * 发送奖励
	 * @param groupId
	 * @param activityId
	 * @param dpsList
	 * @param type 0公会 1组队
	 */
	@Override
	public void calculateReward(String unionId,byte groupId,byte activityId){
		Set<String> bossArr = GameContext.getUnionDataApp().getUnionDpsResult(groupId);
		int rank = 0;
		int rewDkp = 0;
		for(String bossId : bossArr){
			List<RoleDps> dpsList = getBossDpsRank(unionId,activityId,groupId);
			UnionInstance unionInstance = GameContext.getUnionDataApp().getUnionInstance(activityId);
			List<UnionDpsGroupRank> dpsGroupList = GameContext.getUnionDataApp().getUnionDpsGroupRank(unionInstance.getRankGroupId());
			UnionDpsResult dpsResult = GameContext.getUnionDataApp().getUnionDpsResult(bossId);
			
			if(unionInstance != null){
				if(dpsList != null && !dpsList.isEmpty()){
					int pNum = dpsList.size();
					Set<Integer> roleSet = Sets.newHashSet();
					for(RoleDps roleDps : dpsList){
						roleSet.add(roleDps.getRoleId());
						UnionActivityBossRecord boss = getBossRecord(unionId,groupId,activityId);
						rank++;
						if(boss != null){
							long bossHp = (long)(boss.getBossHp() * dpsResult.getHarmPercent() / TEN_THOUSAD_F);
							if(roleDps.getDps() >= bossHp){
								rewDkp += dpsResult.getKillBossDkp(); 
								for(UnionDpsGroupRank dpsRank : dpsGroupList){
									if(rank >= dpsRank.getRankBefore() && rank <= dpsRank.getRankEnd()){
										rewDkp+= dpsRank.getRewardDkp();
										break;
									}
								}
							}
						}
						UnionMember member = GameContext.getUnionApp().getUnionMember(unionId,roleDps.getRoleId());
						if(member != null){
							member.setDkp(member.getDkp() + rewDkp);
							GameContext.getUnionApp().saveOrUpdUnionMember(member);
						}
					}
					
					int goodsNum = getRewardGoodsNum(pNum);
					if(goodsNum > 0){
						
						List<GoodsItem> itemList = Lists.newArrayList();
						UnionDropGroup dropGroupProb;
						if(!Util.isEmpty(dpsResult.getDropgroupId())){
							String [] gId = dpsResult.getDropgroupId().split(",");
							for(String id : gId){
								List<UnionDropGroup> dropGroupElement = GameContext.getUnionDataApp().getUnionDropGroup(Integer.parseInt(id));
								int table  [] = new int[dropGroupElement.size()] ; 
								for(int i =0 ; i <dropGroupElement.size() ;i++ ){
									dropGroupProb = dropGroupElement.get(i);
									table[i] = dropGroupProb.getProbability();
								}
								int index = Util.getProbabilityIndexByTable(table);
								UnionDropGroup dropGroup = dropGroupElement.get(index);
								
								for(int z=0;z<dropGroup.getGoodsNum();z++){
									for(int k=0;k<goodsNum;k++){
										GoodsItem item = new GoodsItem();
										item.setUuid(getUuid());
										item.setGoodsId(dropGroup.getGoodsId());
										item.setGoodsType(dropGroup.getGoodsType());
										item.setGoodsBinded(dropGroup.getGoodsBind());
										item.setGoodsNum((byte)1);
										itemList.add(item);
									}
								}
							}
							Auction auction = GameContext.getUnionAuctionApp().packagingAuction(unionId,activityId, groupId, itemList,roleSet);
							GameContext.getUnionAuctionApp().addAuctionGoods(unionId, auction);
						}
					}
				}
			}
		}
	}
	
	/**
	 * 发送奖励
	 * @param groupId
	 * @param activityId
	 * @param dpsList
	 */
	@Override
	public void calculateTeamReward(String unionId,byte groupId,byte activityId,Map<Integer,RoleDps> roleDpsMap){
		Set<String> bossArr = GameContext.getUnionDataApp().getUnionDpsResult(groupId);
		int rank = 0;
		int rewDkp = 0;
		
		List<RoleDps> list = Lists.newArrayList();
		if(roleDpsMap != null && !roleDpsMap.isEmpty()){
			
			list.addAll(roleDpsMap.values());
			
			Iterator<RoleDps> iter = list.iterator();
			
			while(iter.hasNext()){
				RoleDps roleDps = iter.next();
				int dps = getRoleDps(unionId,roleDps.getRoleId(), activityId, groupId);
				if(dps > 0){
					iter.remove();
				}
			}
			sortRoleDps(list);
		}
		
		for(String bossId : bossArr){
			
			UnionInstance unionInstance = GameContext.getUnionDataApp().getUnionInstance(activityId);
			List<UnionDpsGroupRank> dpsGroupList = GameContext.getUnionDataApp().getUnionDpsGroupRank(unionInstance.getRankGroupId());
			UnionDpsResult dpsResult = GameContext.getUnionDataApp().getUnionDpsResult(bossId);
			
			if(unionInstance != null){
				if(list != null && !list.isEmpty()){
					Set<Integer> roleSet = Sets.newHashSet();
					for(RoleDps roleDps : list){
						roleSet.add(roleDps.getRoleId());
						UnionActivityBossRecord boss = getBossRecord(unionId,groupId,activityId);
						rank++;
						if(boss != null){
							long bossHp = (long)(boss.getBossHp() * dpsResult.getHarmPercent() / TEN_THOUSAD_F);
							if(roleDps.getDps() >= bossHp){
								rewDkp += dpsResult.getKillBossDkp(); 
								for(UnionDpsGroupRank dpsRank : dpsGroupList){
									if(rank >= dpsRank.getRankBefore() && rank <= dpsRank.getRankEnd()){
										rewDkp+= dpsRank.getRewardDkp();
										break;
									}
								}
							}
						}
						UnionMember member = GameContext.getUnionApp().getUnionMember(unionId,roleDps.getRoleId());
						if(member != null){
							member.setDkp(member.getDkp() + rewDkp);
							GameContext.getUnionApp().saveOrUpdUnionMember(member);
						}
						addDps(roleDps.getRoleId(),roleDps.getUnionId(),activityId, groupId, roleDps.getDps());
					}
					saveRoleDps(unionId,activityId,groupId);
				}
			}
		}
	}
	
	private String getUuid(){
		return java.util.UUID.randomUUID().toString();
	}
	
	/**
	 * 计算人数获得物品个数
	 * @return
	 */
	private int getRewardGoodsNum(int pNum){
		List<UnionDropConf> unionDropConfList = GameContext.getUnionDataApp().getUnionDropConfList();
		int goodsNum = 0;
		if(unionDropConfList != null && !unionDropConfList.isEmpty()){
			for(UnionDropConf dropConf : unionDropConfList){
				if(pNum >= dropConf.getMin() && pNum <= dropConf.getMax()){
					int prob = (int)(dropConf.getProb() / TEN_THOUSAD_F);
					if(prob > 100){
						goodsNum += prob / 100;
						prob = prob%100;
					}
					Random rand = new Random();
					int randInt = rand.nextInt(101);
					if(randInt > prob){
						goodsNum++;
					}
				}
			}
		}
		return goodsNum;
	}
	
	/**
	 * 获得某个活动中的boss数据
	 * @param unionId
	 * @param groupId
	 * @param activityId
	 * @return
	 */
	private UnionActivityBossRecord getBossRecord(String unionId,byte groupId,byte activityId){
		Map<Byte,Map<Byte,UnionActivityBossRecord>> insMap = null;
		Map<Byte,UnionActivityBossRecord> bossMap = null;
		if(bossRecordMap.containsKey(unionId)){
			insMap = bossRecordMap.get(unionId);
			if(insMap.containsKey(activityId)){
				bossMap = insMap.get(activityId);
				if(bossMap.containsKey(groupId)){
					return bossMap.get(groupId);
				}
			}
		}
		return null;
	}
	
	/**
	 * 添加BOSS击杀记录
	 * @param record
	 */
	@Override
	public void saveOrUpdUnionKillBossRecord(String unionId,String bossId){
		
		Set<String> setBoss = Sets.newHashSet();
		UnionKillBossRecord record = new UnionKillBossRecord();
		if(killBossMap.containsKey(unionId)){
			setBoss = killBossMap.get(unionId);
			setBoss.add(bossId);
		}else{
			setBoss.add(bossId);
			killBossMap.put(unionId, setBoss);
		}
		record.setUnionId(unionId);
		record.buildKillBossData(setBoss);
		GameContext.getBaseDAO().saveOrUpdate(record);
	}
	
	/**
	 * 添加BOSS击杀记录
	 * @param record
	 */
	private void addKillBossRecord(String unionId,Set<String> setBoss){
		killBossMap.put(unionId, setBoss);
	}
	
	
	
	/**
	 * 获得击杀BOSS记录
	 * @param unionId
	 * @return
	 */
	@Override
	public Set<String> getUnionKillBossRecord(String unionId){
		Set<String> setBoss = Sets.newHashSet();
		if(killBossMap.containsKey(unionId)){
			setBoss = killBossMap.get(unionId);
		}
		return setBoss;
	}

	@Override
	public Result enterInstance(RoleInstance role, byte activityId) {
		Result result = new Result();
		try{
			Map<Byte,UnionActivityInfo> activityInfoMap = GameContext.getUnionDataApp().getUnionActivityMap();
			UnionActivityInfo activityInfo = null;
			
			Union union = GameContext.getUnionApp().getUnion(role);
			if(union == null){
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_NULL));
			}
			
			if(activityInfoMap.containsKey(activityId)){
				activityInfo = activityInfoMap.get(activityId);
			}else{
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_ACTIVITY_ERR));
			}
			
			if(GameContext.getUnionApp().isOverActivity(activityId)){
				return result.setInfo(GameContext.getI18n().messageFormat(TextId.UNION_ACTIVITY_OVERTIME, activityInfo.getActivityName()));
			}
			
			byte state = GameContext.getUnionApp().getActivityState(role.getUnionId(), activityId);
			if(state == 0){
				return result.setInfo(GameContext.getI18n().messageFormat(TextId.UNION_ACTIVITY_CLOSE, activityInfo.getActivityName()));
			}
			
			UnionInstance instance = GameContext.getUnionDataApp().getUnionInstance(activityId);
			if(instance == null){
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_ACTIVITY_ERR));
			}
			
			Point targetPoint = new Point(instance.getMapId(),instance.getMapX(),instance.getMapY());
			GameContext.getUserMapApp().changeMap(role, targetPoint);
			result.success();
		}catch(Exception e){
			logger.error("enterInstance",e);
		}
		return result;
	}
	
	
}
