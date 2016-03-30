package com.game.draco.app.qualify.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C1753_QualifyChallengeReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;

public class QualifyChallengeAction extends BaseAction<C1753_QualifyChallengeReqMessage> {
	
	private static final byte INFO_LENGTH = 2;
	private static final String HAS_CONFIRM = "1";

	@Override
	public Message execute(ActionContext context, C1753_QualifyChallengeReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		String[] infos = this.getHeroInfos(reqMsg.getInfo());
		if (null == infos || infos.length < INFO_LENGTH) {
			C0002_ErrorRespMessage message = new C0002_ErrorRespMessage();
			message.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return message;
		}
		Result result = GameContext.getQualifyApp().qualifyChallenge(role, infos[0], this.isConfirm(infos[1]));
		if (result.isIgnore()) {
			return null;
		}
		if (!result.isSuccess()) {
			C0002_ErrorRespMessage message = new C0002_ErrorRespMessage();
			message.setInfo(result.getInfo());
			return message;
		}
		C0003_TipNotifyMessage message = new C0003_TipNotifyMessage();
		message.setMsgContext(result.getInfo());
		return message;
	}
	
	private String[] getHeroInfos(String heroInfo) {
		return Util.splitStr(heroInfo, Cat.colon);
	}
	
	private boolean isConfirm(String info) {
		return HAS_CONFIRM.equals(info);
	}
	
}
