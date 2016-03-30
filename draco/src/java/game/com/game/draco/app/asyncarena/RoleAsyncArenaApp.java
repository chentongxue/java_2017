package com.game.draco.app.asyncarena;

import java.util.List;
import java.util.Map;

import sacred.alliance.magic.base.ChallengeResultType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.AppSupport;
import com.game.draco.app.asyncarena.config.AsyncRankReward;
import com.game.draco.app.asyncarena.domain.AsyncArenaRole;
import com.game.draco.app.asyncarena.domain.AsyncBattleInfo;
import com.game.draco.app.asyncarena.vo.RoleAsyncRankRewardResult;
import com.game.draco.app.asyncarena.vo.RoleAsyncRefResult;
import com.game.draco.app.asyncpvp.vo.AsyncPvpBattleInfo;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.message.item.AsyncArenaRoleBuyChallengeItem;
import com.game.draco.message.item.AsyncArenaTargetItem;
import com.game.draco.message.item.TargetItem;
import com.game.draco.message.response.C2627_AsyncArenaBuyNumRespMessage;

public interface RoleAsyncArenaApp extends AppSupport{
	
	/**
	 * 按战力筛选组数据
	 */
	void initAutoRoleAsyncArena(RoleInstance role,AsyncArenaRole asyncArenaRole);
	
	/**
	 * 按规则挑选对战方数据16人 VIP花费刷
	 */
	void refAsyncArenaFilterBattleVip(RoleInstance role,boolean isVip,boolean isDel);
	
	/**
	 * 刷新对手校验 VIP花费刷
	 */
	RoleAsyncRefResult refValidator(RoleInstance role);
	
//	/**
//	 * 创建对手
//	 * @param role
//	 * @param mapInstance
//	 * @return
//	 */
//	NpcInstance createAsyncArenaNpc(RoleInstance role, int targetRoleId,MapInstance mapInstance);
	
	/**
	 * 获得角色对战分组数据
	 */
	Map<Integer,AsyncBattleInfo> getRoleAsyncBattleInfo(RoleInstance role);
	
	/**
	 * 获得角色异步竞技场数据
	 */
	AsyncArenaRole getRoleAsyncArenaInfo(RoleInstance role);

	/**
	 * 保存或更新角色异步竞技场数据
	 * @param asyncArenaRole
	 */
	void saveOrUpdRoleAsyncArena(RoleInstance role,AsyncArenaRole asyncArenaRole);

	/**
	 * 初始化角色竞技场数据
	 * @param role
	 * @return
	 */
	void initRoleAsyncArena(RoleInstance role);

	/**
	 * 战斗结果数据
	 * @param role
	 * @param battleInfo
	 * @param type
	 */
	void challengeOver(RoleInstance role, AsyncPvpBattleInfo battleInfo,
			ChallengeResultType type);
	
	/**
	 * 挑战
	 */
	void challenge(RoleInstance role, int targetRoleId);

	/**
	 * 判断是否可以挑战
	 * @param role
	 * @param targetRoleId
	 * @param result
	 * @return
	 */
	Result isChallenge(RoleInstance role, int targetRoleId);
	
	/**
	 * 购买挑战次数
	 */
	C2627_AsyncArenaBuyNumRespMessage buyChallengeNum(RoleInstance role);
	
	/**
	 * 获得角色排名
	 * @return
	 */
	int getRoleBattleScoreRanking(RoleInstance role);
	
	/**
	 * 日更新全部
	 */
	void dailyUpdateAsyncArenaAll();
	
	/**
	 * 日更新角色
	 */
	void dailyUpdateAsyncArenaByRole(AsyncArenaRole asyncArenaRole);
	
	/**
	 * 领取排行奖励
	 */
	RoleAsyncRankRewardResult rewardRank(RoleInstance role);

	/**
	 * 获得排行奖励
	 * @param rank
	 * @return
	 */
	AsyncRankReward getAsyncRankReward(int rank);

	/**
	 * 获得对战数据
	 * @param role
	 * @return
	 */
	List<AsyncArenaTargetItem> getAsyncArenaTargetItemList(RoleInstance role);
	
	/**
	 * 检查人数
	 */
	void validRoleBattltNum(RoleInstance role);

	List<TargetItem> getTargetItemList(int targetRoleId);
}