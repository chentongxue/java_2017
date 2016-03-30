//package com.game.draco.action.internal;
//
//import com.game.draco.GameContext;
//import com.game.draco.message.internal.C0076_FactionChangePostionInternalMessage;
//
//import sacred.alliance.magic.action.BaseAction;
//import sacred.alliance.magic.core.Message;
//import sacred.alliance.magic.core.action.ActionContext;
//import sacred.alliance.magic.domain.Faction;
//
//public class FactionChangePositionInternalAction extends BaseAction<C0076_FactionChangePostionInternalMessage> {
//
//	@Override
//	public Message execute(ActionContext context,C0076_FactionChangePostionInternalMessage reqMsg) {
//		Faction faction = reqMsg.getFaction();
//		GameContext.getFactionApp().changePosition(faction);
//		return null;
//	}
//}
