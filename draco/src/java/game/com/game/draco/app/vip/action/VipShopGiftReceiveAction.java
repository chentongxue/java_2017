package com.game.draco.app.vip.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2107_VipShopGiftReceiveReqMessage;
/**
 * 收取VIP商城礼包
 */
public class VipShopGiftReceiveAction  extends BaseAction<C2107_VipShopGiftReceiveReqMessage>{
	@Override
	public Message execute(ActionContext ct, C2107_VipShopGiftReceiveReqMessage req) {
		RoleInstance role = this.getCurrentRole(ct);
		if(role == null){
			return null;
		}
		byte vipLevel = req.getVipLevel();
		return GameContext.getVipApp().receiveShopVipGift(role, vipLevel);
	}
}
