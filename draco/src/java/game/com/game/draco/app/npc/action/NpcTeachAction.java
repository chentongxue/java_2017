package com.game.draco.app.npc.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1605_TeachNpcFunctionReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class NpcTeachAction extends BaseAction<C1605_TeachNpcFunctionReqMessage>{

	@Override
	public Message execute(ActionContext context, C1605_TeachNpcFunctionReqMessage req) {
		try{
			RoleInstance role = this.getCurrentRole(context);
			if(null == role){
				return null ;
			}
			return GameContext.getNpcTeachApp().getChooseMenuMessage(role, req.getParam());
		}catch(Exception e){
			logger.error("",e);
			C0002_ErrorRespMessage erm = new C0002_ErrorRespMessage();
			erm.setReqCmdId(req.getCommandId());
			erm.setInfo(this.getText(TextId.SYSTEM_ERROR));
			return erm;
		}
	}
}
