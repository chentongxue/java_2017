package com.game.draco.app.union.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1728_UnionDonateReqMessage;
import com.game.draco.message.response.C1728_UnionDonateRespMessage;

/**
 * 捐献
 * @author mofun030602
 *
 */
public class UnionDonateAction extends BaseAction<C1728_UnionDonateReqMessage> {

	@Override
	public Message execute(ActionContext context, C1728_UnionDonateReqMessage reqMsg) {
		C1728_UnionDonateRespMessage resp = new C1728_UnionDonateRespMessage();
		resp.setType((byte) 0);
		try {
			RoleInstance role = this.getCurrentRole(context);
			resp = GameContext.getUnionApp().donate(role);
			return resp;
		} catch (Exception e) {
			this.logger.error("FactionDemiseAction", e);
			resp.setInfo(Status.Faction_FAILURE.getTips());
			return resp;
		}
	}
}
