package com.game.draco.message.internal;

import lombok.Data;
import sacred.alliance.magic.core.Message;

public @Data class InternalMessage extends Message{

	/**
	 * 是否允许拒绝
	 */
	protected boolean canRefuse = false ;
}
