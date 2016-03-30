package com.game.draco.app.union.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.union.vo.ChangeActivityResult;
import com.game.draco.message.request.C2756_UnionChangeActivityReqMessage;
import com.game.draco.message.response.C2756_UnionChangeActivityRespMessage;

/**
 * 公会活动开关
 * @author zhb
 *
 */
public class UnionChangeActivityAction extends BaseAction<C2756_UnionChangeActivityReqMessage> {

	@Override
	public Message execute(ActionContext context, C2756_UnionChangeActivityReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		C2756_UnionChangeActivityRespMessage respMsg = new C2756_UnionChangeActivityRespMessage();
		
		ChangeActivityResult result  = GameContext.getUnionApp().changeActivity(role,reqMsg.getActivityId(),reqMsg.getState(),reqMsg.getConsumeType());
		if(result.isIgnore()){
			return null;
		}
		respMsg.setFlag(result.getResult());
		respMsg.setMsg(result.getInfo());
		respMsg.setState(result.getState());
		respMsg.setGem(result.getGem());
		respMsg.setActyivityId(reqMsg.getActivityId());
		respMsg.setPopularity(result.getPopularity());
		return respMsg;
	}

}
