package com.game.draco.app.store.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;

import com.game.draco.GameContext;
import com.game.draco.app.store.config.NpcStoreAnytime;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C1604_NpcStoreAnytimeReqMessage;

public class NpcStoreAnytimeAction extends BaseAction<C1604_NpcStoreAnytimeReqMessage>{

	@Override
	public Message execute(ActionContext context,
			C1604_NpcStoreAnytimeReqMessage reqMsg) {
		NpcStoreAnytime config = GameContext.getNpcStoreApp().getNpcStoreAnytime() ;
		if(null == config){
			return new C0003_TipNotifyMessage(GameContext.getI18n().getText(TextId.ERROR_DATA));
		}
		return GameContext.getNpcStoreApp().getNpcStoreMessage(config.getNpcTemplateId(), config.getShowType());
	}

}
