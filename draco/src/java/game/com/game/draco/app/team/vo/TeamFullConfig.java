package com.game.draco.app.team.vo;

import lombok.Data;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;

public @Data class TeamFullConfig implements KeySupport<String> {

	private byte targetType;
	private short targetId;
	private String info;

	public void init(String fileInfo) {
		TeamPanelTargetType type = TeamPanelTargetType.getTeamPanelTargetType(this.targetType);
		if (null == type) {
			this.checkFail(fileInfo + " : targetType is " + this.targetType +" : targetType config error!");
		}
	}
	
	private void checkFail(String info) {
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
	@Override
	public String getKey() {
		return this.targetType + Cat.underline + this.targetId;
	}

}
