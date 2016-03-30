package com.game.draco.app.alchemy.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1913_AlchemyDisplayReqMessage;
/**
 * 
 * @author gaibaoning@moogame.cn
 * @date 2014-4-4 下午01:54:14
 */
public class AlchemyDisplayAction  extends BaseAction<C1913_AlchemyDisplayReqMessage>{

	@Override
	public Message execute(ActionContext context, C1913_AlchemyDisplayReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		return GameContext.getAlchemyApp().openAlchemyPanel(role);
	}
	
}
