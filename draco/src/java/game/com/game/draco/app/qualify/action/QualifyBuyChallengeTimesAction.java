package com.game.draco.app.qualify.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1754_QualifyBuyChallengeTimesMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C1754_QualifyBuyChallengeTimesRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class QualifyBuyChallengeTimesAction extends BaseAction<C1754_QualifyBuyChallengeTimesMessage> {

	@Override
	public Message execute(ActionContext context, C1754_QualifyBuyChallengeTimesMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		C1754_QualifyBuyChallengeTimesRespMessage resp = new C1754_QualifyBuyChallengeTimesRespMessage();
		Result result = GameContext.getQualifyApp().buyChallengeTimes(role, reqMsg.getParam());
		if (result.isIgnore()) {
			return null;
		}
		if (!result.isSuccess()) {
			C0002_ErrorRespMessage message = new C0002_ErrorRespMessage();
			message.setInfo(result.getInfo());
			return message;
		}
		resp.setRemChallengeTimes(GameContext.getQualifyApp().getRemainChallengeTimes(role));
		resp.setMaxChallengeTimes(GameContext.getQualifyApp().getMaxChallengeTimes(role));
		return resp;
	}

}
