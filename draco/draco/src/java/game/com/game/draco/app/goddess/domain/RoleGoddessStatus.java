package com.game.draco.app.goddess.domain;

import java.util.Date;
import java.util.Set;

import lombok.Data;
import sacred.alliance.magic.util.DateUtil;

import com.game.draco.app.goddess.GoddessAppImpl;
import com.google.common.collect.Sets;

public @Data class RoleGoddessStatus {
	private String roleId;
	private int battleGoddessId;
	private byte robNum;
	private byte revengeNum;
	private Date operateDate;
	private Set<String> roberRoleIdSet = Sets.newHashSet();
	
	public void reset() {
		Date now = new Date();
		if(DateUtil.sameDay(now, operateDate)) {
			return ;
		}
		
		this.robNum = 0;
		this.revengeNum = 0;
	}
	
	public void updateNum(byte opType) {
		this.reset();
		if(opType == GoddessAppImpl.PVP_TYPE_ROB) {
			this.robNum++;
			
		}else if(opType == GoddessAppImpl.PVP_TYPE_REVENGE) {
			this.revengeNum++;
		}
		
		this.operateDate = new Date();
	}
}
