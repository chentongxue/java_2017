package com.game.draco.app.asyncarena.action;

import java.util.List;
import java.util.Map;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.asyncarena.config.AsyncRankDes;
import com.game.draco.app.asyncarena.domain.AsyncBattleInfo;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C2623_AsyncArenaRankDesReqMessage;
import com.game.draco.message.response.C2623_AsyncArenaRankDesRespMessage;

public class RoleAsyncArenaRankDesAction extends BaseAction<C2623_AsyncArenaRankDesReqMessage> {

	@Override
	public Message execute(ActionContext context, C2623_AsyncArenaRankDesReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		
		//对战数据
		Map<Integer,AsyncBattleInfo> asyncBattleInfoMap = GameContext.getRoleAsyncArenaApp().getRoleAsyncBattleInfo(role);
		
		if(asyncBattleInfoMap  == null || asyncBattleInfoMap .isEmpty()) {
			return new C0003_TipNotifyMessage(this.getText(TextId.ASYNC_ARENA_ROLE_NO_BATTLE_INFO));
		}
		C2623_AsyncArenaRankDesRespMessage respMsg = new C2623_AsyncArenaRankDesRespMessage();
		List<AsyncRankDes> rankDesList = GameContext.getAsyncArenaApp().getAsyncRankDesList();
		StringBuilder sb = new StringBuilder();
		for(AsyncRankDes des : rankDesList){
			sb.append(des.getRankDes() + "\n");
		}
		respMsg.setDes(sb.toString());		
		return respMsg;
	}

}
