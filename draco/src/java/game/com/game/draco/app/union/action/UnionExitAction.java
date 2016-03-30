package com.game.draco.app.union.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C1709_UnionExitReqMessage;
import com.game.draco.message.response.C1709_UnionExitRespMessage;

/**
 * 退出公会
 * @author mofun030602
 *
 */
public class UnionExitAction extends BaseAction<C1709_UnionExitReqMessage> {

	@Override
	public Message execute(ActionContext context, C1709_UnionExitReqMessage reqMsg) {
		C1709_UnionExitRespMessage resp = new C1709_UnionExitRespMessage();
		resp.setType((byte) 0);
		try {
			RoleInstance role = this.getCurrentRole(context);
			
			boolean isInActive = GameContext.getUnionIntegralBattleApp().inIntegtalActive(role.getUnionId());
			if(isInActive){
				return new C0003_TipNotifyMessage(GameContext.getI18n().getText(TextId.UNION_INTEGRAL_IN_ACTIVE_ERR));
			}
			
			Result result = GameContext.getUnionApp().exitUnion(role);
			if(!result.isSuccess()){
				resp.setInfo(result.getInfo());
				return resp;
			}
			resp.setType((byte) 1);
			return resp;
		} catch (ServiceException e) {
			this.logger.error("UnionExitAction", e);
			resp.setInfo(this.getText(TextId.SYSTEM_ERROR));
			return resp;
		}
	}

}
