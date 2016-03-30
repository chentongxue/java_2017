package com.game.draco.app.store.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1601_NpcStoreListReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;

public class NpcStoreListAction extends BaseAction<C1601_NpcStoreListReqMessage> {

	@Override
	public Message execute(ActionContext context, C1601_NpcStoreListReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if (role == null) {
			return null;
		}
		String param = req.getParam();
		if (Util.isEmpty(param)) {
			return new C0002_ErrorRespMessage(req.getCommandId(),this.getText(TextId.ERROR_INPUT));
		}
		String[] params = param.split(Cat.comma);
		String npcTemplateId = params[0];
		int showType = Integer.valueOf(params[1]);
		return GameContext.getNpcStoreApp().getNpcStoreMessage(npcTemplateId, showType);
		
	}
}
