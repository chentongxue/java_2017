package com.game.draco.app.union.battle.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2533_UnionBattleInfoReqMessage;
import com.game.draco.message.request.C2535_UnionBattleWinInfoReqMessage;
import com.game.draco.message.response.C2535_UnionBattleWinInfoRespMessage;

/**
 * 【公会战】结束可查询
 */
public class UnionBattleWinInfoAction extends BaseAction<C2535_UnionBattleWinInfoReqMessage> {

	@Override
	public Message execute(ActionContext context, C2535_UnionBattleWinInfoReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(role == null){
			return null;
		}
		return GameContext.getUnionBattleApp().getUnionBattleWinInfoRespMessage();
	}

}
