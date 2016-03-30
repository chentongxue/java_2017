package com.game.draco.app.asyncarena;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.util.Util;

import com.game.draco.GameContext;
import com.game.draco.component.ssdb.Response;
import com.game.draco.component.ssdb.SSDB;
import com.game.draco.component.ssdb.SSDBID;
import com.game.draco.component.ssdb.SSDBUtil;
import com.google.common.collect.Maps;

public class RoleAsyncArenaSSDBStorage implements RoleAsyncArenaStorage {
	private Logger logger = LoggerFactory.getLogger(this.getClass()); 
	private SSDBUtil ssdbUtil;
	
	public SSDBUtil getSsdbUtil() {
		return ssdbUtil;
	}

	public void setSsdbUtil(SSDBUtil ssdbUtil) {
		this.ssdbUtil = ssdbUtil;
	}
	
	@Override
	public Map<String, String> getRoleBattleScores(String key, String startScore,String endScore,int limit) {
		SSDB ssdb = null;
		Map<String, String> bsMap = Maps.newLinkedHashMap();
		try {
			ssdb = ssdbUtil.getSSDB();
			String dbName = this.getRoleBattleScoreDbName() ;
			Response zr = ssdb.zrandom(dbName, key, startScore, endScore,limit) ;
			if(zr.ok()){
				zr.buildStringMap();
				bsMap.putAll(zr.stringItems);
			}
			return bsMap;
		}catch (Exception ex) {
			logger.error("SSDBStorage.getRoleBattleScores error ", ex);
			return null;
		}finally {
			ssdbUtil.returnSSDB(ssdb);
		}
	}
	
	/**
	 * 获得角色排名
	 * 按照score 降序的排名 如果一共N条记录,分数最高的返回0,分数最低的返回 N-1 返回数值[0-N),-1表示不存在
	 */
	@Override
	public int getRoleAsyncArenaRanking(String roleId) {
		SSDB ssdb = null;
		try {
			ssdb = ssdbUtil.getSSDB();
			String dbName = this.getRoleAsyncArenaDbName() ;
			int total = ssdb.zrrank(dbName, roleId);
			if(total == 0) { 
				total = 1;
			}else if(total != -1){
				total +=1;
			}else{
				total = (int)getTotalRanking();
			}
			return total;
		}catch (Exception ex) {
			logger.error("SSDBStorage.getRoleAsyncArenaRanking error ", ex);
			return 0;
		}finally {
			ssdbUtil.returnSSDB(ssdb);
		}
	}
	
	/**
	 * 获得总人数
	 */
	@Override
	public long getTotalRanking() {
		SSDB ssdb = null;
		try {
			ssdb = ssdbUtil.getSSDB();
			String dbName = this.getRoleBattleScoreDbName() ;
			long total = ssdb.zsize(dbName);
			return total;
		}catch (Exception ex) {
			logger.error("SSDBStorage.getTotalRanking error ", ex);
			return 0;
		}finally {
			ssdbUtil.returnSSDB(ssdb);
		}
	}
	
	@Override
	public void saveRoleAsyncArena(String roleId, int honor) {
		if(Util.isEmpty(roleId)) {
			return ;
		}
		SSDB ssdb = null;
		try {
			ssdb = ssdbUtil.getSSDB();
			ssdb.zset(this.getRoleAsyncArenaDbName(), roleId, honor);
		}catch (Exception ex) {
			logger.error("SSDBStorage.saveRoleBattleScore error ", ex);
		}finally {
			ssdbUtil.returnSSDB(ssdb);
		}
	}
	
	private String getRoleBattleScoreDbName() {
		return SSDBID.ROLE_BATTLESCORE + GameContext.getServerId();
	}
	
	private String getRoleAsyncArenaDbName() {
		return SSDBID.ROLE_ASYNCARENA + GameContext.getServerId();
	}

}
