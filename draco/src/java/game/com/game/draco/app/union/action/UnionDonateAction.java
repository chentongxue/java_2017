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
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null;
		}
		try {
			if(reqMsg.getType() == 0){
				return GameContext.getUnionApp().donate(role);
			}else{
				return GameContext.getUnionApp().gemDonate(role);
			}
		} catch (Exception e) {
			this.logger.error("FactionDemiseAction", e);
			C1728_UnionDonateRespMessage resp = new C1728_UnionDonateRespMessage();
			resp.setType((byte) 0);
			resp.setInfo(Status.Faction_FAILURE.getTips());
			return resp;
		}
	}
}
