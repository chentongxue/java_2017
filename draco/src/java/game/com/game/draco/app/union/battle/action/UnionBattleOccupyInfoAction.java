package com.game.draco.app.union.battle.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2532_UnionBattleOccupyInfoReqMessage;
import com.game.draco.message.response.C2532_UnionBattleOccupyInfoRespMessage;

/**
 * 【公会战】占领信息（玩家登录的时候，公会地图被占领的时候发送）
 */
public class UnionBattleOccupyInfoAction extends BaseAction<C2532_UnionBattleOccupyInfoReqMessage> {

	@Override
	public Message execute(ActionContext context, C2532_UnionBattleOccupyInfoReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(role == null){
			return null;
		}
		C2532_UnionBattleOccupyInfoRespMessage resp = GameContext.getUnionBattleApp().getUnionBattleOccupyInfoMessageByMapIndex(req.getMapIndex());
		return resp;
	}
}
