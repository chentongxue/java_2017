package com.game.draco.app.union.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2759_UnionAuctionListReqMessage;
import com.game.draco.message.response.C2759_UnionAuctionListRespMessage;

/**
 * 拍卖行列表
 * @author zhb
 *
 */
public class UnionAuctionListAction extends BaseAction<C2759_UnionAuctionListReqMessage> {

	@Override
	public Message execute(ActionContext context, C2759_UnionAuctionListReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		C2759_UnionAuctionListRespMessage respMsg = GameContext.getUnionAuctionApp().sendC2759_UnionAuctionListRespMessage(role.getUnionId(), role.getIntRoleId());
		return  respMsg;
	}

}
