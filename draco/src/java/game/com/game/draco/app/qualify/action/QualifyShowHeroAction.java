package com.game.draco.app.qualify.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.qualify.domain.QualifyRank;
import com.game.draco.message.request.C1758_QualifyShowHeroReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C1758_QualifyShowHeroRespMessage;

public class QualifyShowHeroAction extends BaseAction<C1758_QualifyShowHeroReqMessage> {

	@Override
	public Message execute(ActionContext context, C1758_QualifyShowHeroReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		QualifyRank roleQualify = GameContext.getQualifyApp().getQualifyRank(String.valueOf(reqMsg.getRoleId()));
		if (null == roleQualify) {
			C0002_ErrorRespMessage message = new C0002_ErrorRespMessage();
			message.setInfo(GameContext.getI18n().getText(TextId.Sys_Error));
			return message;
		}
		C1758_QualifyShowHeroRespMessage resp = new C1758_QualifyShowHeroRespMessage();
		resp.setLevel((byte) roleQualify.getRoleLevel());
		resp.setHeroBattleList(GameContext.getQualifyApp().getQualifyHeroList(roleQualify));
		return resp;
	}
	
}
