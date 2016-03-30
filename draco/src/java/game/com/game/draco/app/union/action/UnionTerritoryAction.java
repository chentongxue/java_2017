package com.game.draco.app.union.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C1720_UnionTerritoryReqMessage;

/**
 * 进入公会领地
 */
public class UnionTerritoryAction extends BaseAction<C1720_UnionTerritoryReqMessage> {

	@Override
	public Message execute(ActionContext context, C1720_UnionTerritoryReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(role == null){
			return null;
		}
		Result result = GameContext.getUnionApp().joinUnionTerritory(role);
		if(result.isIgnore()){
			return null;
		}
		if(!result.isSuccess()){
			return new C0003_TipNotifyMessage(result.getInfo());
		}
		return null;
	}

}
