package com.game.draco.message.internal;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import sacred.alliance.magic.domain.RoleSocialRelation;
import sacred.alliance.magic.vo.RoleInstance;


public @Data class C0058_SocialRelationInitInternalMessage extends InternalMessage{

	public C0058_SocialRelationInitInternalMessage(){
		this.commandId = 58;
	}
	
	private RoleInstance role;
	private List<RoleSocialRelation> relationList = new ArrayList<RoleSocialRelation>();
	
}
