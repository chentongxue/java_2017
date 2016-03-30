package com.game.draco.app.asyncpvp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.util.Util;

import com.alibaba.fastjson.JSON;
import com.game.draco.GameContext;
import com.game.draco.app.asyncpvp.domain.AsyncPvpRoleAttr;
import com.game.draco.component.ssdb.Response;
import com.game.draco.component.ssdb.SSDB;
import com.game.draco.component.ssdb.SSDBUtil;
import com.google.common.collect.Maps;

public class AsyncPvpSSDBStorage implements AsyncPvpStorage {
	private Logger logger = LoggerFactory.getLogger(this.getClass()); 
	private SSDBUtil ssdbUtil;
	
	public SSDBUtil getSsdbUtil() {
		return ssdbUtil;
	}

	public void setSsdbUtil(SSDBUtil ssdbUtil) {
		this.ssdbUtil = ssdbUtil;
	}
	
	private String getAsyncPvpRoleAttrKey(String roleId) {
		return "ASYNCPVP_ROLEATTR:" + roleId;
	}
	
	@Override
	public void saveAsyncPvpRoleAttr(AsyncPvpRoleAttr roleAttr){
		if(null == roleAttr) {
			return ;
		}
		SSDB ssdb = null;
		try {
			ssdb = ssdbUtil.getSSDB();
			ssdb.set(this.getAsyncPvpRoleAttrKey(roleAttr.getRoleId()), 
					JSON.toJSONString(roleAttr, false));
		} catch (Exception ex) {
			logger.error("SSDBStorage.saveRoleAttr error ", ex);
		}finally {
			ssdbUtil.returnSSDB(ssdb);
		}
	}
	
	@Override
	public AsyncPvpRoleAttr getAsyncPvpRoleAttr(String roleId) {
		SSDB ssdb = null;
		try {
			ssdb = ssdbUtil.getSSDB();
			String str = ssdb.getString(this.getAsyncPvpRoleAttrKey(roleId));
			if(Util.isEmpty(str)) {
				return null;
			}
			return JSON.parseObject(str, AsyncPvpRoleAttr.class);
		}catch (Exception ex) {
			logger.error("SSDBStorage.getAsyncPvpRoleAttr error ", ex);
			return null;
		}finally {
			ssdbUtil.returnSSDB(ssdb);
		}
	}
	
	@Override
	public List<AsyncPvpRoleAttr> getAsyncPvpRoleAttrList(List<String> ids) {
		SSDB ssdb = null ;
		List<String> formatIds = new ArrayList<String>();
		try {
			for(String id : ids){
				formatIds.add(this.getAsyncPvpRoleAttrKey(id));
			}
			ssdb = ssdbUtil.getSSDB();
			Response kr = ssdb.multi_get(formatIds);
			if(!kr.ok()){
				return null ;
			}
			kr.buildStringMap();
			List<AsyncPvpRoleAttr> list = new ArrayList<AsyncPvpRoleAttr>();
			for(int index = 0 ;index <kr.stringKeys.size();index ++){
				String value = kr.stringItems.get(kr.stringKeys.get(index));
				if(Util.isEmpty(value)){
					continue ;
				}
				AsyncPvpRoleAttr r = JSON.parseObject(value, AsyncPvpRoleAttr.class);
				if(null == r){
					continue ;
				}
				list.add(r);
			}
			return list ;
		} catch (Exception ex) {
			logger.error("storage.getAsyncPvpRoleAttrList error ", ex);
			return null;
		}finally{
			ssdbUtil.returnSSDB(ssdb);
		}
	}
	
	
	private String getRoleBattleScoreDbName() {
		return "ROLE_BATTLESCORE:S" + GameContext.getServerId();
	}
	
	private String getRoleAsyncArenaDbName() {
		return "ROLE_ASYNCARENA:S" + GameContext.getServerId();
	}
	
	@Override
	public void saveRoleBattleScore(String roleId, int battleScore) {
		if(Util.isEmpty(roleId)) {
			return ;
		}
		SSDB ssdb = null;
		try {
			ssdb = ssdbUtil.getSSDB();
			ssdb.zset(this.getRoleBattleScoreDbName(), roleId, battleScore);
		}catch (Exception ex) {
			logger.error("SSDBStorage.saveRoleBattleScore error ", ex);
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
	
	@Override
	public Map<String, String> getRoleBattleScores(String key, String score, int limit) {
		SSDB ssdb = null;
		Map<String, String> bsMap = Maps.newLinkedHashMap();
		try {
			ssdb = ssdbUtil.getSSDB();
			String dbName = this.getRoleBattleScoreDbName() ;
			Response zr = ssdb.zscan(dbName, key, score, "", limit) ;
			if(zr.ok()){
				zr.buildStringMap();
				bsMap.putAll(zr.stringItems);
			}
			zr = ssdb.zrscan(dbName, key, score, "", limit);
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
	
	@Override
	public Map<String, String> getRoleBattleScores(String key, String startScore,String endScore,int limit) {
		SSDB ssdb = null;
		Map<String, String> bsMap = Maps.newLinkedHashMap();
		try {
			ssdb = ssdbUtil.getSSDB();
			String dbName = this.getRoleBattleScoreDbName() ;
			Response zr = ssdb.zscan(dbName, key, startScore, endScore,limit) ;
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
	
	/**
	 * 获得角色排名
	 * 按照score 降序的排名 如果一共N条记录,分数最高的返回0,分数最低的返回 N-1 返回数值[0-N),-1表示不存在
	 */
	@Override
	public int getRoleBattleScoreRanking(String roleId) {
		SSDB ssdb = null;
		try {
			ssdb = ssdbUtil.getSSDB();
			String dbName = this.getRoleBattleScoreDbName() ;
			int total = ssdb.zrrank(dbName, roleId);
			return total;
		}catch (Exception ex) {
			logger.error("SSDBStorage.getRoleAsyncArenaRanking error ", ex);
			return 0;
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
}
