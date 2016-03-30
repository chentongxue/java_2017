package com.game.draco.action.internal;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;

import com.game.draco.GameContext;
import com.game.draco.message.internal.C0083_RichManGetGoodsInternalMessage;

public class RichManGetGoodsInternalAction extends BaseAction<C0083_RichManGetGoodsInternalMessage> {

	@Override
	public Message execute(ActionContext context, C0083_RichManGetGoodsInternalMessage reqMsg) {
		GameContext.getRichManApp().roleGetGoods(reqMsg.getRoleId(), reqMsg.getGoodsId());
		return null;
	}

}
