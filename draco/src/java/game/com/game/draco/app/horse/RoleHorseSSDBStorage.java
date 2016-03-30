package com.game.draco.app.horse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.util.Util;

import com.alibaba.fastjson.JSON;
import com.game.draco.GameContext;
import com.game.draco.app.horse.domain.RoleHorseCache;
import com.game.draco.component.ssdb.SSDB;
import com.game.draco.component.ssdb.SSDBID;
import com.game.draco.component.ssdb.SSDBUtil;

public class RoleHorseSSDBStorage implements RoleHorseStorage {
	private Logger logger = LoggerFactory.getLogger(this.getClass()); 
	private SSDBUtil ssdbUtil;
	
	public SSDBUtil getSsdbUtil() {
		return ssdbUtil;
	}

	public void setSsdbUtil(SSDBUtil ssdbUtil) {
		this.ssdbUtil = ssdbUtil;
	}
	
	/**
	 * 获得骑乘坐骑KEY
	 * @return
	 */
	private String getRoleHorseOnBattleName(){
		return SSDBID.ROLEHORSE_ONBATTLE + GameContext.getServerId();
	}
	
	@Override
	public RoleHorseCache getRoleHorseOnBattle(int roleId)
	{
		SSDB ssdb = null;
		try {
			ssdb = ssdbUtil.getSSDB();
			String str = ssdb.hgetString(this.getRoleHorseOnBattleName(), String.valueOf(roleId));
			if(Util.isEmpty(str)){
				return null ;
			}
			return JSON.parseObject(str, RoleHorseCache.class);
		}catch (Exception ex) {
			logger.error("RoleHorseSSDBStorage.getRoleHorseOnBattle error ", ex);
			return null;
		}finally {
			ssdbUtil.returnSSDB(ssdb);
		}
	}

	@Override
	public void saveRoleHorseOnBattle(RoleHorseCache record)
	{
		if(null == record) {
			return ;
		}
		SSDB ssdb = null;
		try {
			ssdb = ssdbUtil.getSSDB();
			ssdb.hset(this.getRoleHorseOnBattleName(), String.valueOf(record.getRoleId()), JSON.toJSONString(record));
		}catch (Exception ex) {
			logger.error("RoleHorseSSDBStorage.saveRoleHorseOnBattle error ", ex);
		}finally {
			ssdbUtil.returnSSDB(ssdb);
		}
	}

}
