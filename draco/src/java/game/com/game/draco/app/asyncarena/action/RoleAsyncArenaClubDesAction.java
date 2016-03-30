package com.game.draco.app.asyncarena.action;

import java.util.List;
import java.util.Map;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.asyncarena.config.AsyncClubDes;
import com.game.draco.app.asyncarena.domain.AsyncBattleInfo;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C2624_AsyncArenaClubDesReqMessage;
import com.game.draco.message.response.C2624_AsyncArenaClubDesRespMessage;

public class RoleAsyncArenaClubDesAction extends BaseAction<C2624_AsyncArenaClubDesReqMessage> {

	@Override
	public Message execute(ActionContext context, C2624_AsyncArenaClubDesReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		
		Map<Integer,AsyncBattleInfo> asyncBattleInfoMap = GameContext.getRoleAsyncArenaApp().getRoleAsyncBattleInfo(role);
		
		if(asyncBattleInfoMap  == null || asyncBattleInfoMap .isEmpty()) {
			return new C0003_TipNotifyMessage(this.getText(TextId.ASYNC_ARENA_ROLE_NO_BATTLE_INFO));
		}
		C2624_AsyncArenaClubDesRespMessage respMsg = new C2624_AsyncArenaClubDesRespMessage();
		List<AsyncClubDes> clubDesList = GameContext.getAsyncArenaApp().getAsyncClubDesList();
		StringBuilder sb = new StringBuilder();
		for(AsyncClubDes des : clubDesList){
			sb.append(des.getClubDes() + "\n");
		}
		respMsg.setDes(sb.toString());		
		return respMsg;
	}

}
