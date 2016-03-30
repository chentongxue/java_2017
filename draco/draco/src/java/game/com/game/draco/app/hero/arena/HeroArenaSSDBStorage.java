package com.game.draco.app.hero.arena;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.util.Util;

import com.alibaba.fastjson.JSON;
import com.game.draco.app.hero.arena.domain.RoleHeroArenaRecord;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.component.ssdb.SSDB;
import com.game.draco.component.ssdb.SSDBID;
import com.game.draco.component.ssdb.SSDBUtil;

public class HeroArenaSSDBStorage implements HeroArenaStorage {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Getter @Setter private SSDBUtil ssdbUtil;
	
	private String getRoleHeroArenaRecordKey(String roleId){
		return SSDBID.HERO_ARENA_RECORD + roleId;
	}
	
	@Override
	public RoleHeroArenaRecord getRoleHeroArenaRecord(String roleId) {
		SSDB ssdb = null;
		try {
			ssdb = this.ssdbUtil.getSSDB();
			String str = ssdb.getString(this.getRoleHeroArenaRecordKey(roleId));
			if (Util.isEmpty(str)) {
				return null ;
			}
			return JSON.parseObject(str, RoleHeroArenaRecord.class);
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".getRoleHeroArenaRecord error: ", e);
			return null;
		} finally {
			this.ssdbUtil.returnSSDB(ssdb);
		}
	}

	@Override
	public void saveRoleHeroArenaRecord(RoleHeroArenaRecord record) {
		if (null == record) {
			return ;
		}
		SSDB ssdb = null;
		try {
			ssdb = this.ssdbUtil.getSSDB();
			ssdb.set(this.getRoleHeroArenaRecordKey(record.getRoleId()), JSON.toJSONString(record));
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".saveRoleHeroArenaRecord error: ", e);
		} finally {
			this.ssdbUtil.returnSSDB(ssdb);
		}
	}
	
	private String makeRoleArenaHerosKey(String roleId){
		return SSDBID.ROLE_ARENA_HEROS + roleId;
	}

	@Override
	public List<RoleHero> getRoleHeros(String roleId) {
		SSDB ssdb = null;
		try {
			ssdb = this.ssdbUtil.getSSDB();
			String str = ssdb.getString(this.makeRoleArenaHerosKey(roleId));
			if (Util.isEmpty(str)) {
				return null ;
			}
			return JSON.parseArray(str, RoleHero.class);
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".getRoleHeros error: ", e);
			return null;
		} finally {
			this.ssdbUtil.returnSSDB(ssdb);
		}
	}

	@Override
	public void saveRoleHeros(String roleId, List<RoleHero> heros) {
		if (Util.isEmpty(heros)) {
			return ;
		}
		SSDB ssdb = null;
		try {
			ssdb = this.ssdbUtil.getSSDB();
			ssdb.set(this.makeRoleArenaHerosKey(roleId), JSON.toJSONString(heros));
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".saveRoleHeros error: ", e);
		} finally {
			this.ssdbUtil.returnSSDB(ssdb);
		}
	}

	/*@Override
	public RoleHero getRoleHero(String roleId, int heroId) {
		SSDB ssdb = null;
		try {
			ssdb = this.ssdbUtil.getSSDB();
			String str = ssdb.hgetString(this.getRoleHeroDbName(roleId), this.getRoleHeroKey(heroId));
			if (Util.isEmpty(str)) {
				return null ;
			}
			return JSON.parseObject(str, RoleHero.class);
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".getRoleHeroArenaRecord error: ", e);
			return null;
		} finally {
			this.ssdbUtil.returnSSDB(ssdb);
		}
	}

	@Override
	public void saveRoleHero(RoleHero roleHero) {
		if (null == roleHero) {
			return ;
		}
		SSDB ssdb = null;
		try {
			ssdb = this.ssdbUtil.getSSDB();
			ssdb.hset(this.getRoleHeroDbName(roleHero.getRoleId()), this.getRoleHeroKey(roleHero.getHeroId()), JSON.toJSONString(roleHero));
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".saveRoleHeroArenaRecord error: ", e);
		} finally {
			this.ssdbUtil.returnSSDB(ssdb);
		}
	}*/
	
	/*private String getRoleHeroDbName(String roleId){
		return SSDBID.ROLE_HEROS + roleId;
	}
	
	private String getRoleHeroKey(int heroId){
		return String.valueOf(heroId);
	}
	
	private Map<String, String> hgetRoleHeroMap(String roleId) {
		SSDB ssdb = null;
		Map<String, String> map = Maps.newHashMap();
		try {
			ssdb = this.ssdbUtil.getSSDB();
			Response resp = ssdb.hgetAll(this.getRoleHeroDbName(roleId));
			if(resp.ok()){
				resp.buildStringMap();
				map.putAll(resp.stringItems);
			}
			return map;
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".saveRoleHeroArenaRecord error: ", e);
			return map;
		} finally {
			this.ssdbUtil.returnSSDB(ssdb);
		}
	}*/

	/*@Override
	public Map<Integer, RoleHero> getRoleHeroMap(String roleId) {
		Map<Integer, RoleHero> heroMap = Maps.newHashMap();
		try {
			Map<String, String> strMap = this.hgetRoleHeroMap(roleId);
			if(Util.isEmpty(strMap)){
				return heroMap;
			}
			for(String val : strMap.values()){
				if(null == val){
					continue;
				}
				RoleHero hero = JSON.parseObject(val, RoleHero.class);
				if(null == hero){
					continue;
				}
				heroMap.put(hero.getHeroId(), hero);
			}
			return heroMap;
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".saveRoleHeroArenaRecord error: ", e);
			return heroMap;
		}
	}*/
	
}
