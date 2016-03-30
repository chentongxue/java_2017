package com.game.draco.app.levelgift.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2403_LevelGiftReceiveReqMessage;
import com.game.draco.message.response.C2403_LevelGiftReceiveRespMessage;

public class LevelGiftReceiveAction extends BaseAction<C2403_LevelGiftReceiveReqMessage>{

	@Override
	public Message execute(ActionContext context, C2403_LevelGiftReceiveReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null;
		}
		int level = (reqMsg.getLevel() & 0xff);
		Result result = GameContext.getLevelGiftApp().takeReward(role, level);
		C2403_LevelGiftReceiveRespMessage respMsg = new C2403_LevelGiftReceiveRespMessage();
		respMsg.setStatus(result.getResult());
		respMsg.setInfo(result.getInfo());
		respMsg.setLevel((byte)level);
		return respMsg;
	}

}
