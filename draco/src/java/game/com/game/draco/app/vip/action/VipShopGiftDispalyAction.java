package com.game.draco.app.vip.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2106_VipShopGiftReqMessage;
/**
 * 购买VIP商城礼包
 */
public class VipShopGiftDispalyAction  extends BaseAction<C2106_VipShopGiftReqMessage>{
	@Override
	public Message execute(ActionContext ct, C2106_VipShopGiftReqMessage req) {
		RoleInstance role = this.getCurrentRole(ct);
		if(role == null){
			return null;
		}
		return GameContext.getVipApp().vipShopGiftDispaly(role);
	}
}
