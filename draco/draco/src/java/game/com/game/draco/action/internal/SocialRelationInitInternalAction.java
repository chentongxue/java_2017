package com.game.draco.action.internal;

import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.message.internal.C0058_SocialRelationInitInternalMessage;
import com.game.draco.message.push.C0003_TipNotifyMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.RoleSocialRelation;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

public class SocialRelationInitInternalAction extends BaseAction<C0058_SocialRelationInitInternalMessage>{

	@Override
	public Message execute(ActionContext context, C0058_SocialRelationInitInternalMessage reqMsg) {
		RoleInstance role = reqMsg.getRole();
		String roleId = role.getRoleId();
		GameContext.getSocialApp().initRoleSocialRelation(roleId, reqMsg.getRelationList());
		List<RoleSocialRelation> friendList = GameContext.getSocialApp().getFriendList(role);
		if(!Util.isEmpty(friendList)){
			C0003_TipNotifyMessage message = new C0003_TipNotifyMessage();
			message.setMsgContext(role.getRoleName() + " 上线了");
			for(RoleSocialRelation relation : friendList){
				String targetRoleId = relation.getOtherRoleId(roleId);
				GameContext.getMessageCenter().sendByRoleId(null, targetRoleId, message);
			}
		}
		return null;
	}

}
