package com.game.draco.app.union.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C1725_UnionImpeachReqMessage;
import com.game.draco.message.response.C1725_FactionImpeachRespMessage;

/**
 * 弹劾
 * @author mofun030602
 *
 */
public class UnionImpeachAction extends BaseAction<C1725_UnionImpeachReqMessage> {

	@Override
	public Message execute(ActionContext context, C1725_UnionImpeachReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		C1725_FactionImpeachRespMessage resp = new C1725_FactionImpeachRespMessage();
		
		boolean isInActive = GameContext.getUnionIntegralBattleApp().inIntegtalActive(role.getUnionId());
		if(isInActive){
			return new C0003_TipNotifyMessage(GameContext.getI18n().getText(TextId.UNION_INTEGRAL_IN_ACTIVE_ERR));
		}
		
		
		Result result = GameContext.getUnionApp().impeach(role);
		if(result.isIgnore()){
			return null;
		}
		if(!result.isSuccess()){
			resp.setType((byte) 0);
			resp.setInfo(result.getInfo());
			return resp;
		}
		resp.setType((byte) 1);
		resp.setInfo(this.getText(TextId.SYSTEM_SUCCESS));
		return resp;
	}

}
