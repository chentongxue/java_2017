package com.game.draco.app.copy.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.copy.vo.CopyRaidsResult;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C0259_CopyRaidsReqMessage;
import com.game.draco.message.response.C0259_CopyRaidsRespMessage;

public class CopyRaidsAction extends BaseAction<C0259_CopyRaidsReqMessage> {

	@Override
	public Message execute(ActionContext context, C0259_CopyRaidsReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		CopyRaidsResult result = GameContext.getCopyLogicApp().raidsCopy(role, reqMsg.getCopyId());
		if (!result.isSuccess()) {
			C0003_TipNotifyMessage message = new C0003_TipNotifyMessage();
			message.setMsgContext(result.getInfo());
			return message;
		}
		C0259_CopyRaidsRespMessage resp = new C0259_CopyRaidsRespMessage();
		resp.setCopyId(reqMsg.getCopyId());
		resp.setCopyRemCount(result.getCopyRemCount());
		resp.setTypeRemCount(result.getTypeRemCount());
		resp.setGoodsLiteList(result.getGoodsLiteList());
		resp.setAttriTypeList(result.getAttriItemList());
		return resp;
	}

}
