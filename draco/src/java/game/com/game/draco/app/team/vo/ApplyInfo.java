package com.game.draco.app.team.vo;

import java.util.List;

import lombok.Data;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.vo.AbstractRole;

import com.game.draco.app.team.PlayerTeam;
import com.game.draco.app.team.Team;
import com.google.common.collect.Lists;

public @Data class ApplyInfo {

	private byte number;// 最大人数
	private short targetId;// 目标ID
	private byte targetType;// 目标类型 
	private Team team ;
	private long applyTime;
	private String teamId = "";
	private List<AbstractRole> applyRoles = Lists.newArrayList();
	
	public ApplyInfo() {
		super();
	}
	
	public ApplyInfo(PlayerTeam team) {
		this.targetId = team.getTargetId();
		this.targetType = team.getTargetType();
		this.number = (byte) team.getMaxPlayerNum();
		this.team = team;
		this.teamId = team.getTeamId();
		this.applyRoles.addAll(team.getMembers());
		// 防止出现异常
		this.applyRoles.addAll(team.getOfflineMembers().values());
		this.setApplyTime(System.currentTimeMillis());
	}
	
	/**
	 * 目标
	 * @return
	 */
	public String getTarget() {
		return this.targetType + Cat.underline + this.targetId;
	}
	
}
