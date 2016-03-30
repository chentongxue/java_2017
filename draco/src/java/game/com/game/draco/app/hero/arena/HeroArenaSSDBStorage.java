package com.game.draco.app.hero.arena;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.util.Util;

import com.alibaba.fastjson.JSON;
import com.game.draco.GameContext;
import com.game.draco.app.hero.arena.domain.RoleHeroArenaRecord;
import com.game.draco.component.ssdb.SSDB;
import com.game.draco.component.ssdb.SSDBID;
import com.game.draco.component.ssdb.SSDBUtil;

public class HeroArenaSSDBStorage implements HeroArenaStorage {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Getter @Setter private SSDBUtil ssdbUtil;
	
	private String getRoleHeroArenaRecordName(){
		return SSDBID.HERO_ARENA_RECORD + GameContext.getServerId();
	}
	
	@Override
	public RoleHeroArenaRecord getRoleHeroArenaRecord(String roleId) {
		SSDB ssdb = null;
		try {
			ssdb = this.ssdbUtil.getSSDB();
			String str = ssdb.hgetString(this.getRoleHeroArenaRecordName(), roleId);
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
			ssdb.hset(this.getRoleHeroArenaRecordName(), record.getRoleId(), JSON.toJSONString(record));
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".saveRoleHeroArenaRecord error: ", e);
		} finally {
			this.ssdbUtil.returnSSDB(ssdb);
		}
	}
	
}
