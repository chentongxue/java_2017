package com.game.draco.app.richman.vo;

import java.util.Map;
import java.util.Map.Entry;

import lombok.Data;
import sacred.alliance.magic.util.Util;

import com.google.common.collect.Maps;

public @Data class RichManRoleStat {
	public final static byte FACE_FORWARD = 1; //正向
	public final static byte FACE_BACK = -1; //反向
	
	private String roleId;
	private byte gridId;
	private byte face; //朝向
	private Map<Byte, Long> stateOverTimeMap = Maps.newHashMap(); 
	
	public void initStateTime(byte stateId, short stateTime) {
		stateOverTimeMap.put(stateId, System.currentTimeMillis() + stateTime * 1000);
	}
	
	public boolean needNotifyTime() {
		if(Util.isEmpty(stateOverTimeMap)) {
			return false;
		}
		for(Entry<Byte, Long> entry : stateOverTimeMap.entrySet()) {
			Long time = entry.getValue();
			if(time > 0) {
				return true;
			}
		}
		return false;
	}
	
}
