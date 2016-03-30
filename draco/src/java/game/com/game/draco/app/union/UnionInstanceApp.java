package com.game.draco.app.union;

import java.util.List;
import java.util.Map;
import java.util.Set;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.union.domain.instance.RoleDps;


public interface UnionInstanceApp{

	/**
	 * 设置BOSS状态
	 * @param unionId
	 * @param insId
	 * @param bossId
	 * @param state
	 */
	void setBossState(String unionId, byte insId, byte groupId, byte state,int maxHp);

	/**
	 * 添加DPS
	 * @param role
	 * @param activityId
	 * @param bossId
	 * @param dps
	 */
	void addDps(int roleId, byte activityId, byte groupId, int dps);

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
	void resetActivity(byte activityId,byte week);

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
	void addUnionKillBossRecord(String unionId,String bossId);

	/**
	 *  初始化BOSS击杀记录
	 */
	void initKillBossRecord();
	
	/**
	 * 角色登录初始化数据
	 * @param role
	 */
	void onJoinGame(RoleInstance role);

	/**
	 * 进入副本
	 */
	Result enterInstance(RoleInstance role,byte activityId);

	/**
	 * 击杀公会副本世界广播
	 * @param attacker
	 * @param victim
	 */
	void broadcast(RoleInstance attacker, NpcInstance victim);
}