package com.game.draco.app.forward.logic;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.forward.config.ForwardConfig;

public class ChoiceCardLogic implements ForwardLogic{

	@Override
	public void forward(RoleInstance role, ForwardConfig config) {
		Message msg1 = GameContext.getRoleChoiceCardApp().sendC2810_GoldCardRespMessage(role) ;
		Message msg2 = GameContext.getRoleChoiceCardApp().sendC2811_GemCardRespMessage(role); 
		Message msg3 = GameContext.getRoleChoiceCardApp().sendC2812_ActivityCardRespMessage(role); 
		GameContext.getMessageCenter().sendSysMsg(role, msg1);
		GameContext.getMessageCenter().sendSysMsg(role, msg2);
		GameContext.getMessageCenter().sendSysMsg(role, msg3);
	}

	@Override
	public ForwardLogicType getForwardLogicType() {
		return ForwardLogicType.choice_card ;
	}

}
