package com.game.draco.app.asyncarena.action;

import java.util.Collections;
import java.util.Map;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.asyncarena.domain.AsyncBattleInfo;
import com.game.draco.app.asyncarena.vo.RoleAsyncRefResult;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C2625_RoleAsyncArenaRefItemReqMessage;
import com.game.draco.message.response.C2625_RoleAsyncArenaRefItemRespMessage;

public class RoleAsyncArenaRefItemInfoAction extends BaseAction<C2625_RoleAsyncArenaRefItemReqMessage> {

	@Override
	public Message execute(ActionContext context, C2625_RoleAsyncArenaRefItemReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		
		//对战数据
		Map<Integer,AsyncBattleInfo> asyncBattleInfoMap = GameContext.getRoleAsyncArenaApp().getRoleAsyncBattleInfo(role);
		
		if(asyncBattleInfoMap  == null || asyncBattleInfoMap .isEmpty()) {
			return new C0003_TipNotifyMessage(this.getText(TextId.ASYNC_ARENA_ROLE_NO_BATTLE_INFO));
		}
		
		C2625_RoleAsyncArenaRefItemRespMessage respMsg = new C2625_RoleAsyncArenaRefItemRespMessage();
		
		RoleAsyncRefResult result = GameContext.getRoleAsyncArenaApp().refValidator(role);
		if(result.isIgnore()){
			return null;
		}
		if(result.getTargetItem() != null){
			Collections.sort(result.getTargetItem());
		}
		respMsg.setPrice(result.getPrice());
		respMsg.setTargetItem(result.getTargetItem());
		respMsg.setFlag(result.getResult());
		respMsg.setMsg(result.getInfo());
		return respMsg;
	}

}
