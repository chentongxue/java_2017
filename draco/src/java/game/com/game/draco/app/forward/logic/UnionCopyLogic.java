package com.game.draco.app.forward.logic;

import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.forward.config.ForwardConfig;
import com.game.draco.app.union.domain.Union;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C2754_UnionActivityListReqMessage;

public class UnionCopyLogic implements ForwardLogic {

	@Override
	public void forward(RoleInstance role, ForwardConfig config) {
		Union union = GameContext.getUnionApp().getUnion(role) ;
		if(null == union){
			Message msg = new C0003_TipNotifyMessage(GameContext.getI18n().getText(TextId.UNION_NOT));
			role.getBehavior().sendMessage(msg);
			return ;
		}
		C2754_UnionActivityListReqMessage reqMsg = new C2754_UnionActivityListReqMessage();
		role.getBehavior().addEvent(reqMsg);
	}

	@Override
	public ForwardLogicType getForwardLogicType() {
		return ForwardLogicType.union_copy ;
	}

}
