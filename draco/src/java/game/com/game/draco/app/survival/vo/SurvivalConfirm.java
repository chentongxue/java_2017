package com.game.draco.app.survival.vo;

import java.util.Set;

import com.google.common.collect.Sets;

import lombok.Data;

public @Data class SurvivalConfirm {
	public static final String AFFIRM = "1";
	public static final String CANCEL = "0";

	private int roleId;// 队长
	private int memberNum;// 队伍数量
	private Set<Integer> roleIdSet = Sets.newHashSet();
	
	/**
	 * 成员进入确认（如果都确认了，返回true）
	 * @param roleId
	 * @return
	 */
	public boolean memberConfirm(int roleId) {
		this.roleIdSet.add(roleId);
		return this.membersAllConfirm();
	}
	
	/**
	 * 是否全部成员都确认（不包括队长）
	 * @return
	 */
	public boolean membersAllConfirm() {
		return (this.roleIdSet.size() >= memberNum - 1);
	}
	
	/**
	 * 是否确认过
	 * @param roleId
	 * @return
	 */
	public boolean haveConfirm(int roleId) {
		return roleIdSet.contains(roleId);
	}
	
}
