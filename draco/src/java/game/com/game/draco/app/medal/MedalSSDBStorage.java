package com.game.draco.app.medal;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.util.Util;

import com.alibaba.fastjson.JSON;
import com.game.draco.GameContext;
import com.game.draco.app.medal.vo.MedalRoleData;
import com.game.draco.component.ssdb.SSDB;
import com.game.draco.component.ssdb.SSDBID;
import com.game.draco.component.ssdb.SSDBUtil;

/**
 * 
 * 存储离线数据
 * 用于查看其他玩家的数据
 *
 */
public class MedalSSDBStorage implements MedalStorage {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Getter @Setter private SSDBUtil ssdbUtil;
	
	private String getRoleHeroArenaRecordName(){
		return SSDBID.MEDAL_ROLE_DATA + GameContext.getServerId();
	}
	
	@Override
	public MedalRoleData getMedalRoleData(String roleId) {
		SSDB ssdb = null;
		try {
			ssdb = this.ssdbUtil.getSSDB();
			String str = ssdb.hgetString(this.getRoleHeroArenaRecordName(), roleId);
			if (Util.isEmpty(str)) {
				return null ;
			}
			return JSON.parseObject(str, MedalRoleData.class);
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".getMedalRoleData error: ", e);
			return null;
		} finally {
			this.ssdbUtil.returnSSDB(ssdb);
		}
	}

	@Override
	public void saveMedalRoleData(MedalRoleData data) {
		if (null == data) {
			return ;
		}
		SSDB ssdb = null;
		try {
			ssdb = this.ssdbUtil.getSSDB();
			ssdb.hset(this.getRoleHeroArenaRecordName(), data.getRoleId(), JSON.toJSONString(data));
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".saveMedalRoleData error: ", e);
		} finally {
			this.ssdbUtil.returnSSDB(ssdb);
		}
	}
	
}
