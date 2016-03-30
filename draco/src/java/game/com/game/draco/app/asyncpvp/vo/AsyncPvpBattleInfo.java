package com.game.draco.app.asyncpvp.vo;

import lombok.Data;

public @Data class AsyncPvpBattleInfo {
	private String roleId;//挑战者ID
	private String targetRoleId;//被挑战者ID
	private String roleName;//挑战者名字
	private String targetRoleName;//被挑战者名字
	private byte opType; //0:rob 1:revenge
	private int petId;
	private String petName = "";
}
