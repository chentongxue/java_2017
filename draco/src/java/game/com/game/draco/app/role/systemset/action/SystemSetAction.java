package com.game.draco.app.role.systemset.action;

import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.message.item.SystemSetItem;
import com.game.draco.message.request.C3100_SystemSetReqMessage;
import com.game.draco.message.response.C3100_SystemSetRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class SystemSetAction extends BaseAction<C3100_SystemSetReqMessage>{
	
	public Message execute(ActionContext context, C3100_SystemSetReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		List<SystemSetItem> sysSetList = reqMsg.getSysSetList();
		boolean flag = GameContext.getSystemSetApp().modifyRoleSysSet(role, sysSetList);
		C3100_SystemSetRespMessage resp = new C3100_SystemSetRespMessage();
		if(!flag){
			resp.setType((byte) 0);
			resp.setInfo(Status.SYS_SystemSet_Fail.getTips());
			return resp;
		}
		resp.setType((byte) 1);
		return resp;
	}
}
