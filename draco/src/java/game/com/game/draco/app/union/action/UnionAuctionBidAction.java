package com.game.draco.app.union.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.union.vo.RoleAuctionResult;
import com.game.draco.message.request.C2760_UnionAuctionBidReqMessage;
import com.game.draco.message.response.C2760_UnionAuctionBidRespMessage;

/**
 * 拍卖行出价
 * @author zhb
 *
 */
public class UnionAuctionBidAction extends BaseAction<C2760_UnionAuctionBidReqMessage> {

	@Override
	public Message execute(ActionContext context, C2760_UnionAuctionBidReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		RoleAuctionResult result = GameContext.getUnionAuctionApp().addRoleAuction(role, reqMsg.getUuid(), reqMsg.getBidPrice());
		C2760_UnionAuctionBidRespMessage respMsg = new C2760_UnionAuctionBidRespMessage();
		respMsg.setBidPrice(result.getBidPrice());
		respMsg.setUuid(reqMsg.getUuid());
		respMsg.setType(result.getResult());
		respMsg.setInfo(result.getInfo());
		return  respMsg;
	}
	
}
