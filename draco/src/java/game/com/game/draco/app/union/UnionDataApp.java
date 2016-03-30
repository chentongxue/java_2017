package com.game.draco.app.union;

import java.util.List;
import java.util.Map;
import java.util.Set;

import sacred.alliance.magic.core.Service;

import com.game.draco.app.union.config.UnionActivityInfo;
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

public interface UnionDataApp extends Service{
	
	/**
	 * 获得公会基础数据
	 * @return
	 */
	UnionBase getUnionBase();
	
	/**
	 * 获得公会等级数据
	 * @param unionLevel
	 * @return
	 */
	UnionUpgrade getUnionUpgrade(int unionLevel);
	
	/**
	 * 公会权限数据
	 * @param positionType
	 * @return
	 */
	Set<UnionPowerType> getPowerTypeSet(UnionPositionType positionType);
	
	/**
	 * 获得公会描述数据
	 * @return
	 */
	Map<Byte, UnionDes> getDescribeMap();
	
	/**
	 * 获得公会活动数据
	 * @param activityId
	 * @return
	 */
	UnionInstance getUnionInstance(byte activityId); 
	
	/**
	 * 获得DPS排行奖励数据
	 * @param groupId
	 * @return
	 */
	List<UnionDpsGroupRank> getUnionDpsGroupRank(byte groupId);
	
	/**
	 * 获得BOSS数据
	 * @param bossId
	 * @return
	 */
	UnionDpsResult getUnionDpsResultByBossId(String bossId);
	
	/**
	 * 获得BOSS数据
	 * @param groupId
	 * @return
	 */
	Set<UnionDpsResult> getUnionDpsResult(byte groupId);
	
	/**
	 * 获得某一项捐赠数据
	 * @param id
	 * @return
	 */
	List<UnionDonate> getUnionDonateList();
	
	/**
	 * 获得某一项钻石捐赠数据
	 * @param id
	 * @return
	 */
	List<UnionGemDonate> getUnionGemDonateList();
	
	/**
	 * 获得当前等级活动数据
	 * @param level
	 * @return
	 */
	Set<Byte> getActivityGroupMap(int level);
	
	/**
	 * 获得活动数据
	 * @return
	 */
	Map<Byte,UnionActivityInfo> getUnionActivityMap();
	
	/**
	 * 获得公会活动副本中BOSS数据
	 * @return
	 */
	UnionInsBoss getUnionInsBossMap(byte activityId);

	/**
	 * 根据BOSSID获得掉落组
	 * @param bossId
	 * @return
	 */
	Set<Integer> getDropMap(String bossId);

	/**
	 * 根据掉落组获得掉落物品
	 * @param groupId
	 * @return
	 */
	List<UnionDropGroup> getUnionDropGroup(int groupId);
	
	/**
	 * 根据BOSS获得组ID
	 * 
	 */
	byte getGroupId(String bossId);
	
	/**
	 * 获得人数概率
	 * @return
	 */
	List<UnionDropConf> getUnionDropConfList();
	
	/**
	 * 获得活动消耗
	 * @param num
	 * @return
	 */
	UnionActivityConsume getUnionActivityConsume(byte num);
	
	/**
	 * 获得活动bosss数
	 */
	int getActivityMaxBossNum();
	
//	/**
//	 * 获得开启活动时间
//	 */
//	long getActivityOpenTime();

	/**
	 * 获得物品底价
	 * @param goodsId
	 * @return
	 */
	int getGoodsBasePrice(int goodsId);
	
	UnionVipReward getUnionVipReward(byte vipLevel);
	
	UnionMail getUnionMail(byte type);
	
	UnionSummon getUnionSummon(int groupId);
	
}