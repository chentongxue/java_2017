package com.game.draco.app.luckybox.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1915_LuckyBoxDisplayReqMessage;
/**
 * 打开幸运转盘（原海盗宝箱），或刷新转盘
 */
public class LuckyBoxDisplayAction  extends BaseAction<C1915_LuckyBoxDisplayReqMessage>{

	@Override
	public Message execute(ActionContext context,
			C1915_LuckyBoxDisplayReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		byte refreshFlag = reqMsg.getRefreshFlag();
		return GameContext.getLuckyBoxApp().openLuckyBoxPanel(role,refreshFlag);
	}

	
}
