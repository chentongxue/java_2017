package com.game.draco.app.union.battle.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2531_UnionBattleJoinReqMessage;
import com.game.draco.message.response.C2531_UnionBattleJoinRespMessage;

/**
 * 加入【公会战】
 */
public class UnionBattleJoinAction extends BaseAction<C2531_UnionBattleJoinReqMessage> {

	@Override
	public Message execute(ActionContext context, C2531_UnionBattleJoinReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(role == null){
			return null;
		}
		Result result = GameContext.getUnionBattleApp().joinBattle(role, req.getMapIndex());
		if(result.isIgnore()){
			return null;
		}
//		return new C0003_TipNotifyMessage(this.getText(TextId.FACTION_APPLY_JOIN_SEND_SUCCESS));
		C2531_UnionBattleJoinRespMessage resp = new C2531_UnionBattleJoinRespMessage();
		resp.setType(result.getResult());
		resp.setInfo(result.getInfo());
		return resp;
	}

}
