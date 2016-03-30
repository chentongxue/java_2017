package com.game.draco.app.pet;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.util.Util;

import com.alibaba.fastjson.JSON;
import com.game.draco.GameContext;
import com.game.draco.app.pet.domain.RolePetBattleList;
import com.game.draco.app.pet.domain.RolePetShow;
import com.game.draco.app.pet.domain.RolePetStatus;
import com.game.draco.component.ssdb.Response;
import com.game.draco.component.ssdb.SSDB;
import com.game.draco.component.ssdb.SSDBID;
import com.game.draco.component.ssdb.SSDBUtil;
import com.google.common.collect.Maps;

public class PetSSDBStorage implements PetStorage {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Getter
	@Setter
	private SSDBUtil ssdbUtil;

	@Override
	public RolePetStatus getRolePetStatus(String roleId) {
		SSDB ssdb = null;
		try {
			ssdb = ssdbUtil.getSSDB();
			String str = ssdb.hgetString(this.getRolePetStatusName(), roleId);
			if (Util.isEmpty(str)) {
				return null;
			}
			return JSON.parseObject(str, RolePetStatus.class);

		} catch (Exception ex) {
			logger.error("PetSSDBStorage.getRolePetStatus error ", ex);
			return null;
		} finally {
			ssdbUtil.returnSSDB(ssdb);
		}
	}

	private String getRolePetStatusName() {
		return SSDBID.PET_STATUS + GameContext.getServerId();
	}

	@Override
	public void saveRolePetStatus(RolePetStatus record) {
		if (null == record) {
			return;
		}
		SSDB ssdb = null;
		try {
			ssdb = ssdbUtil.getSSDB();
			ssdb.hset(this.getRolePetStatusName(), record.getRoleId(), JSON.toJSONString(record));
		} catch (Exception ex) {
			logger.error("PetSSDBStorage.saveRolePetStatus error ", ex);
		} finally {
			ssdbUtil.returnSSDB(ssdb);
		}
	}

	@Override
	public RolePetShow getShowRolePet(String roleId) {
		SSDB ssdb = null;
		try {
			ssdb = ssdbUtil.getSSDB();
			String str = ssdb.hgetString(this.getPetShowName(), roleId);
			if (Util.isEmpty(str)) {
				return null;
			}
			return JSON.parseObject(str, RolePetShow.class);
		} catch (Exception ex) {
			logger.error("SSDBStorage.getRolePetShow() error ", ex);
			return null;
		} finally {
			ssdbUtil.returnSSDB(ssdb);
		}

	}

	private String getPetShowName() {
		return SSDBID.PET_SHOW + GameContext.getServerId();
	}

	@Override
	public void saveShowRolePet(String roleId, RolePetShow petShow) {
		SSDB ssdb = null;
		try {
			ssdb = ssdbUtil.getSSDB();
			String name = this.getPetShowName();
			if (null == petShow) {
				ssdb.hdel(name, roleId);
				return;
			}
			ssdb.hset(name, roleId, JSON.toJSONString(petShow));
		} catch (Exception ex) {
			logger.error("SSDBStorage.saveRolePetShow() error ", ex);
		} finally {
			ssdbUtil.returnSSDB(ssdb);
		}
	}

	private String getRoleBattleScoreDbName() {
		return SSDBID.PET_BATTLESORE + GameContext.getServerId();
	}

	@Override
	public void saveRolePetBattle(String roleId, int battleScore) {
		if (Util.isEmpty(roleId)) {
			return;
		}
		SSDB ssdb = null;
		try {
			ssdb = ssdbUtil.getSSDB();
			ssdb.zset(this.getRoleBattleScoreDbName(), roleId, battleScore);
		} catch (Exception ex) {
			logger.error("SSDBStorage.saveRolePetBattle error ", ex);
		} finally {
			ssdbUtil.returnSSDB(ssdb);
		}
	}

	@Override
	public Map<String, String> getRolePetBattleScores(String key, int battleScore, int limit) {
		SSDB ssdb = null;
		Map<String, String> bsMap = Maps.newLinkedHashMap();
		try {
			ssdb = ssdbUtil.getSSDB();
			String dbName = this.getRoleBattleScoreDbName();
			Response zr = ssdb.zscan(dbName, key, String.valueOf(battleScore), "", limit);
			if (zr.ok()) {
				zr.buildStringMap();
				bsMap.putAll(zr.stringItems);
			}
			zr = ssdb.zrscan(dbName, key, String.valueOf(battleScore), "", limit);
			if (zr.ok()) {
				zr.buildStringMap();
				bsMap.putAll(zr.stringItems);
			}
			return bsMap;
		} catch (Exception ex) {
			logger.error("SSDBStorage.getRolePetBattleScores error ", ex);
			return null;
		} finally {
			ssdbUtil.returnSSDB(ssdb);
		}
	}

	private String getRolePetBattleName() {
		return SSDBID.ROLE_PET_BATTLE + GameContext.getServerId();
	}

	@Override
	public RolePetBattleList getRolePetBattleList(String roleId) {
		SSDB ssdb = null;
		try {
			ssdb = ssdbUtil.getSSDB();
			String name = this.getRolePetBattleName();
			String str = ssdb.hgetString(name, roleId);
			if (Util.isEmpty(str)) {
				return null;
			}
			return JSON.parseObject(str, RolePetBattleList.class);
		} catch (Exception ex) {
			logger.error("SSDBStorage.getRolePetShow() error ", ex);
			return null;
		} finally {
			ssdbUtil.returnSSDB(ssdb);
		}
	}

	@Override
	public void saveRolePetBattleList(String roleId, RolePetBattleList petBattleList) {
		SSDB ssdb = null;
		try {
			ssdb = ssdbUtil.getSSDB();
			String name = this.getRolePetBattleName();
			if (null == petBattleList) {
				ssdb.hdel(name, roleId);
				return;
			}
			ssdb.hset(name, roleId, JSON.toJSONString(petBattleList));
		} catch (Exception ex) {
			logger.error("SSDBStorage.saveRolePetBattleList error ", ex);
		} finally {
			ssdbUtil.returnSSDB(ssdb);
		}
	}

}
