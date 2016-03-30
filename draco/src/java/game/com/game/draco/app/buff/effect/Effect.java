package com.game.draco.app.buff.effect;

import com.game.draco.app.buff.BuffContext;
import com.game.draco.app.buff.stat.BuffStat;

import sacred.alliance.magic.app.attri.AttriBuffer;

public interface Effect {

	void store(BuffContext context);
	void resume(BuffContext context) ;
	boolean canRemoveNow(BuffContext context) ;
	AttriBuffer getAttriBuffer(BuffStat stat);
}
