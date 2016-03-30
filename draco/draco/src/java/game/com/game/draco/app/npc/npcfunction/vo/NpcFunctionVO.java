package com.game.draco.app.npc.npcfunction.vo;

import sacred.alliance.magic.vo.RoleInstance;
import lombok.Data;

public @Data class NpcFunctionVO {
	
	private String npcId;//npc模板ID
	private String functionName;//功能名称
	private short commandId;//命令ID
	private String param;//参数
	private byte cdtFaction;//限制条件-公会[0:不受该条件限制 1:要求角色没有公会 2:要求角色拥有公会]
	
	/**
	 * 是否满足限制条件而显示
	 * @param role
	 * @return
	 */
	public boolean canDisplay(RoleInstance role){
		if(1 == this.cdtFaction && role.hasUnion()){
			return false;
		}else if(2 == this.cdtFaction && !role.hasUnion()){
			return false;
		}
		return true;
	}
	
}
