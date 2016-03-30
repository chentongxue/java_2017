package com.game.draco.app.union;

import java.util.List;
import java.util.Map;
import java.util.Set;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.union.domain.instance.RoleDps;


public interface UnionInstanceApp{

	/**
	 * 设置BOSS状态
	 * @param unionId
	 * @param insId
	 * @param bossId
	 * @param state
	 * @param type  0公会  1组队
	 */
	void setBossState(String unionId, byte insId, byte groupId, byte state,int maxHp);

	/**
	 * 添加DPS
	 * @param role
	 * @param activityId
	 * @param bossId
	 * @param dps
	 */
	void addDps(int roleId,String unionId, byte activityId, byte groupId, int dps);

	/**
	 * 初始化公会副本数据
	 */
	void initEvolve();

	/**
	 * 获得BOSS状态
	 * @param unionId
	 * @param insId
	 * @param bossId
	 * @return
	 */
	byte getInsBossState(String unionId, byte insId, byte groupId);

	/**
	 * RoleDps排序
	 * @param list
	 */
	void sortRoleDps(List<RoleDps> list);
	
	/**
	 * 获得公会活动对应BOSS的角色DPS数据
	 */
	Map<Integer,RoleDps> getUnionRoleDpsMap(String unionId,byte insId,byte groupId);

	/**
	 * 计算奖励
	 * @param unionId
	 * @param groupId
	 * @param activityId
	 */
	void calculateReward(String unionId, byte groupId, byte activityId);
	
	/**
	 * 计算小队奖励
	 * @param unionId
	 * @param groupId
	 * @param activityId
	 * @param roleDpsMap
	 */
	void calculateTeamReward(String unionId, byte groupId, byte activityId,Map<Integer,RoleDps> roleDpsMap);

	/**
	 * 获得排行 多个BOSS计算
 	 * @param unionId
	 * @param insId
	 * @param bossId
	 * @return
	 */
	List<RoleDps> getBossDpsRank(String unionId, byte activityId,byte groupId);

	/**
	 * 重置活动
	 * @param activityId
	 */
	void resetActivity(byte activityId);

	/**
	 * 获得击杀BOSS记录
	 * @param unionId
	 * @return
	 */
	Set<String> getUnionKillBossRecord(String unionId);

	/**
	 * 记录boss被击杀
	 * @param unionId
	 * @param bossId
	 */
	void saveOrUpdUnionKillBossRecord(String unionId,String bossId);

	/**
	 *  初始化BOSS击杀记录
	 */
	void initKillBossRecord();

	/**
	 * 设置小队组队击杀boss状态
	 * @param unionId
	 * @param activityId
	 * @param groupId
	 * @param state
	 * @param maxHp
	 * @param roleDpsMap
	 */
	void setTeamBossState(String unionId, byte activityId, byte groupId,byte state, int maxHp,Map<Integer,RoleDps> roleDpsMap);
	
	/**
	 * 进入副本
	 */
	Result enterInstance(RoleInstance role,byte activityId);
}