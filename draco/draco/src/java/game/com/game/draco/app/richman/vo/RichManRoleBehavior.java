package com.game.draco.app.richman.vo;

import lombok.Data;

import com.game.draco.app.richman.config.RichManEvent;

/**
 * 大富翁角色事件 
 */
public @Data class RichManRoleBehavior {
	private int roleId;
	private int attackerId; //攻击者roleid
	private String attackerName;
	private RichManEvent event;
	
	public RichManRoleBehavior(int roleId, RichManEvent event) {
		this.roleId = roleId;
		this.event = event;
	}
	
	public RichManRoleBehavior(int roleId, int attackerId, String attackerName,
			RichManEvent event) {
		this.roleId = roleId;
		this.attackerId = attackerId;
		this.event = event;
		this.attackerName = attackerName;
	}
}
