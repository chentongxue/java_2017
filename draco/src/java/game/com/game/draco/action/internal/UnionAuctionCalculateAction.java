package com.game.draco.action.internal;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;

import com.game.draco.GameContext;
import com.game.draco.message.internal.C0088_UnionAuctionCalculateMessage;

public class UnionAuctionCalculateAction extends BaseAction<C0088_UnionAuctionCalculateMessage>{

	@Override
	public Message execute(ActionContext context,
			C0088_UnionAuctionCalculateMessage reqMsg) {
		GameContext.getUnionAuctionApp().calculateRoleAuction(reqMsg.getAuctionId(),
				reqMsg.getUnionId());
		return null;
	}
}
