package com.game.draco.app.union.battle.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2530_UnionBattlePanelReqMessage;

/**
 * 打开【公会战】活动
 */
public class UnionBattlePanelAction extends BaseAction<C2530_UnionBattlePanelReqMessage> {

	@Override
	public Message execute(ActionContext context, C2530_UnionBattlePanelReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(role == null){
			return null;
		}
		return GameContext.getUnionBattleApp().openPanel(role);
	}

}
