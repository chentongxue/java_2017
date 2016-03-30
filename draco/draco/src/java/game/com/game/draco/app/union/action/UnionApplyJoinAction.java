package com.game.draco.app.union.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C1703_UnionApplyJoinReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;

/**
 * 申请加入公会
 * @author mofun030602
 *
 */
public class UnionApplyJoinAction extends BaseAction<C1703_UnionApplyJoinReqMessage> {

	@Override
	public Message execute(ActionContext context, C1703_UnionApplyJoinReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		Result result = GameContext.getUnionApp().applyJoinUnion(role, reqMsg.getUnionId());
		if(result.isSuccess()){
			return new C0003_TipNotifyMessage(this.getText(TextId.FACTION_APPLY_JOIN_SEND_SUCCESS));
		}
		return new C0002_ErrorRespMessage(reqMsg.getCommandId(), result.getInfo());
	}

}
