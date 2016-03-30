package com.game.draco.app.camp.balance.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1530_CampBalanceReqMessage;

public class CampBalanceAction extends BaseAction<C1530_CampBalanceReqMessage> {

	@Override
	public Message execute(ActionContext context, C1530_CampBalanceReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null;
		}
		if(GameContext.getCampBalanceApp().pushToSelectCampMessage(role)){
			//已经给用户发送了选择阵营的界面
			return null ;
		}
		boolean isOpen = GameContext.getCampBalanceApp().isChangeOpen();
		if(isOpen){
			return GameContext.getCampBalanceApp().getCampBalanceOpenMessage(role);
		}
		return GameContext.getCampBalanceApp().getCampBalanceMessage(role);
	}

}
