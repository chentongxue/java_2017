package com.game.draco.app.goblin.action;

import com.game.draco.GameContext;
import com.game.draco.app.goblin.config.GoblinBaseConfig;
import com.game.draco.message.request.C3002_GoblinActiveInfoReqMessage;
import com.game.draco.message.response.C3002_GoblinActiveInfoRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;

public class GoblinInfoAction extends BaseAction<C3002_GoblinActiveInfoReqMessage> {

	@Override
	public Message execute(ActionContext context, C3002_GoblinActiveInfoReqMessage reqMsg) {
		C3002_GoblinActiveInfoRespMessage resp = new C3002_GoblinActiveInfoRespMessage();
		GoblinBaseConfig config = GameContext.getGoblinApp().getGoblinBaseConfig();
		if (null == config) {
			return null;
		}
		resp.setPanelStarTime(config.getPanelStartTime());
		resp.setPanelDesc(config.getPanelDesc());
		resp.setGoodsLiteList(GameContext.getGoblinApp().getPanelShowGoodsList());
		return resp;
	}

}
