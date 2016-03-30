package com.game.draco.app.union.battle.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C2536_UnionBattleRenameReqMessage;

/**
 * 连胜四次后会长可改名
 */
public class UnionBattleRenameAction extends BaseAction<C2536_UnionBattleRenameReqMessage> {

	@Override
	public Message execute(ActionContext context, C2536_UnionBattleRenameReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(role == null){
			return null;
		}
		Result result = GameContext.getUnionBattleApp().renameMap(role, req.getMapIndex(), req.getNewMapName());
		if(!result.isSuccess()){
			return new C0003_TipNotifyMessage(result.getInfo());
		}
		return null;
	}

}
