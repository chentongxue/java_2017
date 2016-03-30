package com.game.draco.action.internal;

import com.game.draco.GameContext;
import com.game.draco.message.internal.C0081_PkFallInternalMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class PkFallInternalAction extends BaseAction<C0081_PkFallInternalMessage>{

	@Override
	public Message execute(ActionContext context, C0081_PkFallInternalMessage reqMsg) {
		String roleId = reqMsg.getRoleId();
		RoleInstance role  = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleId);
		if(null == role){
			return null;
		}
		GameContext.getUserGoodsApp().deleteForBagByRoleGoods(role, reqMsg.getGoods(), reqMsg.getNum(), OutputConsumeType.pk_punish);
		return null;
	}
}
