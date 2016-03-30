package com.game.draco.app.asyncarena.action;

import java.util.List;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.item.TargetItem;
import com.game.draco.message.request.C2621_RoleAsyncArenaInfoReqMessage;
import com.game.draco.message.response.C2621_RoleAsyncArenaInfoRespMessage;

public class RoleAsyncArenaBattleInfoAction extends BaseAction<C2621_RoleAsyncArenaInfoReqMessage> {

	@Override
	public Message execute(ActionContext context, C2621_RoleAsyncArenaInfoReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		
		C2621_RoleAsyncArenaInfoRespMessage respMsg = new C2621_RoleAsyncArenaInfoRespMessage();
		List<TargetItem> targetList = GameContext.getRoleAsyncArenaApp().getTargetItemList(reqMsg.getTargetId());
		respMsg.setTargetList(targetList);
		
		return respMsg;
	}
	

}
