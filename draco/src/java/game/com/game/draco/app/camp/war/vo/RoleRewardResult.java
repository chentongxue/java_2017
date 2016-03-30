package com.game.draco.app.camp.war.vo;

import lombok.Data;

public @Data class RoleRewardResult {

	private String roleId ;
	private int gameMoney ;
	private int effectAddPrestige ;
	private int AddPrestige ;
	private byte pkStatus ;
	private int winTimes ;
}
