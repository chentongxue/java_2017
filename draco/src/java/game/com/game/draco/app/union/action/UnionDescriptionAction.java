package com.game.draco.app.union.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;

import com.game.draco.GameContext;
import com.game.draco.app.union.config.UnionDes;
import com.game.draco.message.request.C2761_UnionDescriptionReqMessage;
import com.game.draco.message.response.C2761_UnionDescriptionRespMessage;

/**
 * 公会描述
 * @author zhb
 *
 */
public class UnionDescriptionAction extends BaseAction<C2761_UnionDescriptionReqMessage> {

	@Override
	public Message execute(ActionContext context, C2761_UnionDescriptionReqMessage reqMsg) {
		C2761_UnionDescriptionRespMessage respMsg = new C2761_UnionDescriptionRespMessage();
		if(GameContext.getUnionDataApp().getDescribeMap().containsKey(reqMsg.getType())){
			UnionDes des = GameContext.getUnionDataApp().getDescribeMap().get(reqMsg.getType());
			respMsg.setInfo(des.getDescribe());
		}
		return  respMsg;
	}

}
