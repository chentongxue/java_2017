package com.game.draco.app.team.vo;

import lombok.Data;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;

import com.game.draco.app.team.Team;

public @Data class TeamTargetConfig implements KeySupport<String> {
	private int targetType;
	private int targetId;
	private String targetName;
	private int minLevel;
	private int maxLevel;
	private int minMember;
	private int maxMember;
	private int forwardType;// 队员点击前往类型
	private String param;// 参数
	private String broadCast;// 队员提示

	@Override
	public String getKey() {
		return this.targetType + Cat.underline + this.targetId;
	}
	
	/**
	 * 初始化
	 * @param fileInfo
	 */
	public void init(String fileInfo) {
		TeamPanelTargetType targetType = TeamPanelTargetType.getTeamPanelTargetType((byte) this.targetType);
		if (null == targetType) {
			this.checkFail(fileInfo + " : targetType is " + this.targetType +" : targetType config error!");
		}
		TeamPanelForwardType forwardType = TeamPanelForwardType.getTeamPanelForwardType((byte) this.forwardType);
		if (null == forwardType) {
			this.checkFail(fileInfo + " : forwardType is " + this.forwardType +" : forwardType config error!");
		}
	}
	
	/**
	 * 错误日志
	 * @param info
	 */
	private void checkFail(String info) {
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
	/**
	 * 是否符合人数
	 * @param teamMember
	 * @return
	 */
	public boolean meetMember(Team team) {
		int teamMember = team.getPlayerNum();
		if (teamMember > this.maxMember || teamMember < this.minMember) {
			return false;
		}
		return true;
	}
	
}
