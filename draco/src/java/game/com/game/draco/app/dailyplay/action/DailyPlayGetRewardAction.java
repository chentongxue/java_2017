package com.game.draco.app.dailyplay.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1921_DailyPlayGetRewardReqMessage;
import com.game.draco.message.response.C1921_DailyPlayGetRewardRespMessage;

public class DailyPlayGetRewardAction extends BaseAction<C1921_DailyPlayGetRewardReqMessage>{

	@Override
	public Message execute(ActionContext context,
			C1921_DailyPlayGetRewardReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		Result result = GameContext.getDailyPlayApp().recvReward(role, reqMsg.getPlayId());
		C1921_DailyPlayGetRewardRespMessage  respMsg = new C1921_DailyPlayGetRewardRespMessage();
		respMsg.setFlag(result.getResult());
		respMsg.setInfo(result.getInfo());
		respMsg.setPlayId(reqMsg.getPlayId());
		return respMsg;
	}

}
