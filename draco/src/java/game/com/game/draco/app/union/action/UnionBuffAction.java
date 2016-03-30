package com.game.draco.app.union.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C2765_UnionBuffReqMessage;

/**
 * 公会活动领取BUFF
 * @author zhb
 *
 */
public class UnionBuffAction extends BaseAction<C2765_UnionBuffReqMessage> {

	@Override
	public Message execute(ActionContext context, C2765_UnionBuffReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		C0003_TipNotifyMessage respMsg = new C0003_TipNotifyMessage();
		if(!role.hasUnion()){
			respMsg.setMsgContext(GameContext.getI18n().getText(TextId.UNION_NOT));
			return respMsg;
		}
		Result result = GameContext.getUnionApp().receiveBuff(role);
		respMsg.setMsgContext(result.getInfo());
		return respMsg;
	}

}
