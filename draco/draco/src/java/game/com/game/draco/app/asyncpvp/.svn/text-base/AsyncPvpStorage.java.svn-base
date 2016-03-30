package com.game.draco.app.asyncpvp;

import java.util.List;
import java.util.Map;

import com.game.draco.app.asyncpvp.domain.AsyncPvpRoleAttr;

public interface AsyncPvpStorage {
	AsyncPvpRoleAttr getAsyncPvpRoleAttr(String roleId);
	void saveAsyncPvpRoleAttr(AsyncPvpRoleAttr roleAttr);
	List<AsyncPvpRoleAttr> getAsyncPvpRoleAttrList(List<String> ids);
	void saveRoleBattleScore(String roleId, int battleScore);
	Map<String, String> getRoleBattleScores(String key, String score, int limit);
	Map<String, String> getRoleBattleScores(String key, String startScore,String endScore,int limit);
	long getTotalRanking();
	int getRoleBattleScoreRanking(String roleId);
	void saveRoleAsyncArena(String roleId, int honor);
	int getRoleAsyncArenaRanking(String roleId);
}
