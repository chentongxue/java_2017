package com.game.draco.app.asyncpvp;

import java.util.List;
import java.util.Map;

import com.game.draco.app.AppSupport;
import com.game.draco.app.asyncpvp.domain.AsyncPvpRoleAttr;
import com.game.draco.app.asyncpvp.vo.AsyncPvpBattleInfo;

import sacred.alliance.magic.vo.RoleInstance;
import sacred.alliance.magic.vo.map.MapAsyncPvpContainer;

public interface AsyncPvpApp extends AppSupport{
	void addAsyncPvpBattleInfo(AsyncPvpBattleInfo battleInfo);
	AsyncPvpBattleInfo getAsyncPvpBattleInfo(String roleId);
	AsyncPvpRoleAttr getAsyncPvpRoleAttr(String roleId); 
	void resetRoleSkill(RoleInstance role);
	MapAsyncPvpContainer getMapAsyncPvpContainer();
	List<AsyncPvpRoleAttr> getAsyncPvpRoleAttrList(List<String> ids);
	Map<String, String> getRoleBattleScores(String roleId, String score, int limit);
	Map<String, String> randomRoleBattleScores(String roleId, String startScore, String endScore, int limit);
	int getRoleBattleScoreRanking(String roleId);
}
