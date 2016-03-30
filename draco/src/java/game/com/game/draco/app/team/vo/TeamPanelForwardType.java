package com.game.draco.app.team.vo;

public enum TeamPanelForwardType {
	
	hint((byte) 1, "提示"),
	list((byte) 2, "列表"),
	forward((byte) 3, "前往"),
	
	;
	
	private byte type;
	private String name;
	
	TeamPanelForwardType(byte type, String name) {
		this.type = type;
		this.name = name;
	}
	
	public byte getType() {
		return this.type;
	}
	
	public String getName() {
		return this.name;
	}
	
	public static TeamPanelForwardType getTeamPanelForwardType(byte type) {
		for (TeamPanelForwardType teamType : TeamPanelForwardType.values()) {
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
