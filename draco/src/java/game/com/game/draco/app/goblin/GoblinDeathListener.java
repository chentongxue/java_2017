package com.game.draco.app.goblin;

import com.game.draco.GameContext;
import com.game.draco.app.npc.domain.NpcInstance;

import sacred.alliance.magic.app.ai.AiMessageListener;
import sacred.alliance.magic.app.ai.MessageType;
import sacred.alliance.magic.vo.AbstractRole;

public class GoblinDeathListener implements AiMessageListener {

	@Override
	public Object onEvent(MessageType messageType, AbstractRole who) {
		if (MessageType.JUSTDIE == messageType) {
			GameContext.getGoblinApp().goblinDeath((NpcInstance) who);
		}
		return null;
	}

}
