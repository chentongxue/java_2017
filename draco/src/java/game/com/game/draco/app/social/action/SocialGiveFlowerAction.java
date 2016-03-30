package com.game.draco.app.social.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.social.domain.DracoSocialRelation;
import com.game.draco.message.request.C1211_SocialGiveFlowerReqMessage;
import com.game.draco.message.response.C1211_SocialGiveFlowerRespMessage;

public class SocialGiveFlowerAction extends BaseAction<C1211_SocialGiveFlowerReqMessage> {

	@Override
	public Message execute(ActionContext context, C1211_SocialGiveFlowerReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		Result result = GameContext.getSocialApp().giveFlower(role, reqMsg.getRoleId(), reqMsg.getFlowerId());
		if(result.isIgnore()){
			return null;
		}
		C1211_SocialGiveFlowerRespMessage resp = new C1211_SocialGiveFlowerRespMessage();
		resp.setType(result.getResult());
		resp.setInfo(result.getInfo());
		DracoSocialRelation relation = GameContext.getSocialApp().getFriendRelation(role.getRoleId(), String.valueOf(reqMsg.getRoleId()));
		if (null != relation) {
			resp.setIntimateLevel((byte) relation.getIntimateLevel());
			resp.setIntimate(relation.getIntimate());
			resp.setMaxIntimate(relation.getIntimateConfig().getMaxIntimate());
		}
		resp.setRoleId(reqMsg.getRoleId());
		return resp;
	}
	
}
