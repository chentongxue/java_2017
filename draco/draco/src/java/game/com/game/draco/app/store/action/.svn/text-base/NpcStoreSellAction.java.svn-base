package com.game.draco.app.store.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1603_NpcStoreSellReqMessage;
import com.game.draco.message.response.C1603_NpcStoreSellRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class NpcStoreSellAction extends BaseAction<C1603_NpcStoreSellReqMessage> {

	@Override
	public Message execute(ActionContext context, C1603_NpcStoreSellReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null;
		}
		int sellNum = req.getSellNum() & 0xffff ;
		Status status = GameContext.getNpcStoreApp().sell(role, req.getNpcTemplateId(),
				req.getGoodsInstanceId(),sellNum);
		if(!status.isSuccess()){
			C1603_NpcStoreSellRespMessage resp = new C1603_NpcStoreSellRespMessage();
			resp.setInfo(status.getTips());
			return resp;
		}
		return null;
	}
}
