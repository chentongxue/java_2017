package com.game.draco.app.pet.domain;

import java.util.Date;
import java.util.Set;

import lombok.Data;
import sacred.alliance.magic.util.DateUtil;

import com.google.common.collect.Sets;

public @Data class RolePetStatus {

	public static final String ROLE_ID = "roleId";
	public static final String BATTLE_PET_ID = "petId";

	private String roleId;// 角色Id
	private int battlePetId;// 出战宠物Id
	private byte robNum = 0;// 抢夺次数
	private byte revengeNum = 0;// 被抢夺次数
	private Date operateDate = new Date();
	private Set<String> roberRoleIdSet = Sets.newHashSet();

	public void reset() {
		Date now = new Date();
		if (DateUtil.sameDay(now, operateDate)) {
			return;
		}
		this.robNum = 0;
		this.revengeNum = 0;
	}

	public void updateNum(byte opType) {
		this.reset();
		if (opType == (byte) 0) {
			this.robNum++;
		} else if (opType == (byte) 1) {
			this.revengeNum++;
		}
		this.operateDate = new Date();
	}

}
