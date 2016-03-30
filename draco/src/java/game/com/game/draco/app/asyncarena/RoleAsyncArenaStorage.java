package com.game.draco.app.asyncarena;

import java.util.Map;

public interface RoleAsyncArenaStorage {
	Map<String, String> getRoleBattleScores(String key, String startScore,String endScore,int limit);
	void saveRoleAsyncArena(String roleId, int honor);
	int getRoleAsyncArenaRanking(String roleId);
	long getTotalRanking();
}
