package com.game.draco.app.forward.logic;

import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.forward.config.ForwardConfig;
import com.game.draco.app.union.domain.Union;
import com.game.draco.message.push.C0003_TipNotifyMessage;

public class UnionDonateLogic  implements ForwardLogic{

	@Override
	public void forward(RoleInstance role, ForwardConfig config) {
		Union union = GameContext.getUnionApp().getUnion(role) ;
		Message msg = null ;
		if(null == union){
			msg = new C0003_TipNotifyMessage(GameContext.getI18n().getText(TextId.UNION_NOT));
		}else {
			msg = GameContext.getUnionApp().sendC2753_UnionInfoRespMessage(role);
		}
		role.getBehavior().sendMessage(msg);
	}

	@Override
	public ForwardLogicType getForwardLogicType() {
		return ForwardLogicType.union_donate ;
	}

}
