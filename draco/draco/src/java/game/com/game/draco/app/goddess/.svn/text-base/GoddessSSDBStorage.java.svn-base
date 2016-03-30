package com.game.draco.app.goddess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.Util;

import com.alibaba.fastjson.JSON;
import com.game.draco.app.goddess.domain.GoddessWeakTime;
import com.game.draco.app.goddess.domain.RoleGoddessStatus;
import com.game.draco.component.ssdb.SSDB;
import com.game.draco.component.ssdb.SSDBUtil;

public class GoddessSSDBStorage implements GoddessStorage {
	private Logger logger = LoggerFactory.getLogger(this.getClass()); 
	private SSDBUtil ssdbUtil;
	
	public SSDBUtil getSsdbUtil() {
		return ssdbUtil;
	}

	public void setSsdbUtil(SSDBUtil ssdbUtil) {
		this.ssdbUtil = ssdbUtil;
	}
	
	/**
	 * 获得女神对象keyName
	 * @return
	 */
	private String getRoleGoddessStatusKey(String roleId){
		return "GODDESS_STATUS:" + roleId ;
	}
	
	private String getGoddessWeakTimeKey(String roleId, int goddessId) {
		return "GODDESS_WEAKTIME:" + roleId + Cat.underline + goddessId;
	}

	@Override
	public RoleGoddessStatus getRoleGoddessStatus(String roleId)
	{
		SSDB ssdb = null;
		try {
			ssdb = ssdbUtil.getSSDB();
			String str = ssdb.getString(this.getRoleGoddessStatusKey(roleId));
			if(Util.isEmpty(str)){
				return null ;
			}
			return JSON.parseObject(str, RoleGoddessStatus.class);
			
		}catch (Exception ex) {
			logger.error("SSDBStorage.getRoleGoddessStatus error ", ex);
			return null;
		}finally {
			ssdbUtil.returnSSDB(ssdb);
		}
	}

	@Override
	public void saveRoleGoddessStatus(RoleGoddessStatus record)
	{
		if(null == record) {
			return ;
		}
		SSDB ssdb = null;
		try {
			ssdb = ssdbUtil.getSSDB();
			ssdb.set(this.getRoleGoddessStatusKey(record.getRoleId()),
					JSON.toJSONString(record, false));
		}catch (Exception ex) {
			logger.error("SSDBStorage.saveRoleGoddessStatus error ", ex);
		}finally {
			ssdbUtil.returnSSDB(ssdb);
		}
	}

	@Override
	public GoddessWeakTime getGoddessWeakTime(String roleId, int goddessId){
		SSDB ssdb = null;
		try {
			ssdb = ssdbUtil.getSSDB();
			String str = ssdb.getString(this.getGoddessWeakTimeKey(roleId, goddessId));
			if(Util.isEmpty(str)) {
				return null;
			}
			return JSON.parseObject(str, GoddessWeakTime.class);
		}catch (Exception ex) {
			logger.error("SSDBStorage.getGoddessWeakTime error ", ex);
			return null;
		}finally {
			ssdbUtil.returnSSDB(ssdb);
		}
	}

	@Override
	public void saveGoddessWeakTime(GoddessWeakTime record){
		if(null == record) {
			return ;
		}
		SSDB ssdb = null;
		try {
			ssdb = ssdbUtil.getSSDB();
			ssdb.set(this.getGoddessWeakTimeKey(record.getRoleId(), record.getGoddessId()),
					JSON.toJSONString(record, false));
		}catch (Exception ex) {
			logger.error("SSDBStorage.saveGoddessWeakTime error ", ex);
		}finally {
			ssdbUtil.returnSSDB(ssdb);
		}
	}

}
