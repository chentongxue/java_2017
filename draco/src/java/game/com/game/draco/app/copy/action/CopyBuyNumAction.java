package com.game.draco.app.copy.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.copy.vo.CopyBuyNumResult;
import com.game.draco.message.request.C0258_CopyBuyNumReqMessage;
import com.game.draco.message.response.C0258_CopyBuyNumRespMessage;

public class CopyBuyNumAction extends BaseAction<C0258_CopyBuyNumReqMessage> {

	@Override
	public Message execute(ActionContext context, C0258_CopyBuyNumReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (role == null) {
			return null;
		}
		if (Util.isEmpty(reqMsg.getParam())) {
			return null;
		}
		String[] param = reqMsg.getParam().split(",");
		byte confirm = 0;
		if (param.length > 1) {
			confirm = Byte.valueOf(param[1]);
		}
		CopyBuyNumResult result = GameContext.getCopyLogicApp().copyBuyNum(role, Short.valueOf(param[0]), confirm);
		if (result.isIgnore()) {
			return null;
		}
		C0258_CopyBuyNumRespMessage resp = new C0258_CopyBuyNumRespMessage();
		resp.setSuccess(result.getResult());
		resp.setInfo(result.getInfo());
		resp.setCopyId(Short.valueOf(param[0]));
		resp.setCopyRemCount(result.getCopyRemCount());
		resp.setCopyMaxCount(result.getCopyMaxCount());
		resp.setTypeMaxCount(result.getTypeMaxCount());
		resp.setTypeRemCount(result.getTypeRemCount());
		return resp;
	}

}
