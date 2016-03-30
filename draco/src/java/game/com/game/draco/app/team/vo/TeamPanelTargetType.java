package com.game.draco.app.team.vo;

public enum TeamPanelTargetType {
	
	every((byte) 1, "任意目标"),
	field((byte) 2, "野外杀怪"),
	copy((byte) 3, "组队副本"),
	active((byte) 4, "参加活动"),
	;
	
	private byte type;
	private String name;
	
	TeamPanelTargetType(byte type, String name) {
		this.type = type;
		this.name = name;
	}
	
	public byte getType() {
		return this.type;
	}
	
	public String getName() {
		return this.name;
	}
	
	public static TeamPanelTargetType getTeamPanelTargetType(byte type) {
		for (TeamPanelTargetType teamType : TeamPanelTargetType.values()) {
			if (null == teamType) {
				continue ;
			}
			if (teamType.getType() == type) {
				return teamType;
			}
		}
		return null;
	}

}
