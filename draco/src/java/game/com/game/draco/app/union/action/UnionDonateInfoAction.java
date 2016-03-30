package com.game.draco.app.union.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1727_UnionDonateInfoReqMessage;

/**
 * 获取捐献消息
 * @author mofun030602
 *
 */
public class UnionDonateInfoAction extends BaseAction<C1727_UnionDonateInfoReqMessage> {

	@Override
	public Message execute(ActionContext context, C1727_UnionDonateInfoReqMessage reqMsg) {
		try {
			RoleInstance role = this.getCurrentRole(context);
			return GameContext.getUnionApp().getUnionDonateInfo(role);
		} catch (Exception e) {
			this.logger.error("UnionDonateInfoAction", e);
			return null;
		}
	}
}
