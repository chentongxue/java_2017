package com.game.draco.app.npc.action;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C1606_NpcTransferReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class NpcTransferAction extends BaseAction<C1606_NpcTransferReqMessage>{

	@Override
	public Message execute(ActionContext context, C1606_NpcTransferReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null;
		}
		String param = req.getParam();
		Result result = GameContext.getNpcTransferApp().transferRole(role, param);
		if(result.isIgnore()){
			return null;
		}
		if(!result.isSuccess()){
			C0002_ErrorRespMessage errorMsg = new C0002_ErrorRespMessage();
			errorMsg.setReqCmdId(req.getCommandId());
			errorMsg.setInfo(result.getInfo());
			return errorMsg;
		}
		C0003_TipNotifyMessage notifyMsg = new C0003_TipNotifyMessage();
		notifyMsg.setMsgContext(Status.Npc_Trans_Success.getTips());
		return notifyMsg;
	}
}
