package com.game.draco.action.internal;

import com.game.draco.GameContext;
import com.game.draco.message.internal.C0071_RoleWarehouseMailInternalMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;

public class RoleWarehouseMailInternalAction extends BaseAction<C0071_RoleWarehouseMailInternalMessage> {

	@Override
	public Message execute(ActionContext context,C0071_RoleWarehouseMailInternalMessage reqMsg) {
		
		GameContext.getUserWarehouseApp().clearWarehouseAndMail(reqMsg.getRoleId());
		return null;
	}
}
