package com.game.draco.app.compass.action;
import com.game.draco.GameContext;
import com.game.draco.message.request.C1907_CompassListReqMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
/**
 * 当转盘信息具有页签的情况使用
 * @author gaibaoning@moogame.cn
 * @date 2014-3-21 上午11:20:48
 */
public class CompassListAction extends BaseAction<C1907_CompassListReqMessage> {

	@Override
	public Message execute(ActionContext context, C1907_CompassListReqMessage reqMsg) {
		return GameContext.getCompassApp().getCompassListMessage(this.getCurrentRole(context));
	}

}
