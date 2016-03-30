package com.game.draco.app.qualify;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.util.Util;

import com.alibaba.fastjson.JSON;
import com.game.draco.GameContext;
import com.game.draco.app.qualify.domain.RoleQualifyRecord;
import com.game.draco.component.ssdb.SSDB;
import com.game.draco.component.ssdb.SSDBID;
import com.game.draco.component.ssdb.SSDBUtil;

public class QualifySSDBStorage implements QualifyStorage {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Getter
	@Setter
	private SSDBUtil ssdbUtil;

	@Override
	public RoleQualifyRecord getRoleQualifyRecord(String roleId) {
		SSDB ssdb = null;
		try {
			ssdb = ssdbUtil.getSSDB();
			String str = ssdb.hgetString(this.getRoleQualifyRecordName(), roleId);
			if (Util.isEmpty(str)) {
				return null;
			}
			return JSON.parseObject(str, RoleQualifyRecord.class);

		} catch (Exception ex) {
			logger.error("QualifySSDBStorage.getRoleQualifyRecord error!", ex);
			return null;
		} finally {
			ssdbUtil.returnSSDB(ssdb);
		}
	}
	
	private String getRoleQualifyRecordName() {
		return SSDBID.QUALIFY_RECORD + GameContext.getServerId();
	}

	@Override
	public void saveRoleQualifyRecord(RoleQualifyRecord record) {
		if (null == record) {
			return;
		}
		SSDB ssdb = null;
		try {
			ssdb = ssdbUtil.getSSDB();
			ssdb.hset(this.getRoleQualifyRecordName(), record.getRoleId(), JSON.toJSONString(record));
		} catch (Exception ex) {
			logger.error("QualifySSDBStorage.saveRoleQualifyRecord error!", ex);
		} finally {
			ssdbUtil.returnSSDB(ssdb);
		}
	}

}
