package com.game.draco.app.accumulatelogin;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.AppSupport;
import com.game.draco.app.hint.HintSupport;

/**
 */
public interface AccumulateLoginApp extends Service, AppSupport, HintSupport {

	public Message openAccumulateLoginPanel(RoleInstance role);

	public Message receiveAccumulateLoginAwards(RoleInstance role, byte day);

	public Message getAccumulateLoginAwardDetail(RoleInstance role, byte day);

	public boolean autoPushUI(RoleInstance role);

	public boolean hasAward(RoleInstance role);

}
