package com.game.draco.app.union.battle.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2533_UnionBattleInfoReqMessage;

/**
 * 打开世界地图点击【公会战】详情
 */
public class UnionBattleInfoAction extends BaseAction<C2533_UnionBattleInfoReqMessage> {

	@Override
	public Message execute(ActionContext context, C2533_UnionBattleInfoReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(role == null){
			return null;
		}
		return GameContext.getUnionBattleApp().getUnionBattleInfo(role, req.getMapIndex());
	}
}
