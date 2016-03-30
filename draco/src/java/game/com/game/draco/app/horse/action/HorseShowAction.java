package com.game.draco.app.horse.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;

import com.game.draco.GameContext;
import com.game.draco.app.horse.domain.RoleHorseCache;
import com.game.draco.message.request.C1105_HorseShowReqMessage;
import com.game.draco.message.response.C1105_HorseShowRespMessage;

public class HorseShowAction extends BaseAction<C1105_HorseShowReqMessage> {

	@Override
	public Message execute(ActionContext context,
			C1105_HorseShowReqMessage reqMsg) {
		C1105_HorseShowRespMessage respMsg = new C1105_HorseShowRespMessage();
		RoleHorseCache cache = null;
		if (!GameContext.getOnlineCenter().isOnlineByRoleId(
				String.valueOf(reqMsg.getRoleId()))) {
			cache = GameContext.getRoleHorseStorage().getRoleHorseOnBattle(
					reqMsg.getRoleId());
		} else {
			cache = GameContext.getRoleHorseApp().getRoleHorseBattleCache(
					reqMsg.getRoleId());
		}
		if (null == cache) {
			respMsg.setFlag(false);
			return respMsg;
		}
		respMsg.setFlag(true);
		respMsg.setBattleScore(cache.getBattleScore());
		respMsg.setName(cache.getName());
		respMsg.setQuality(cache.getQuality());
		respMsg.setResId(cache.getResId());
		respMsg.setRoleId(reqMsg.getRoleId());
		respMsg.setIconId(cache.getIconId());
		respMsg.setHorsePropItem(cache.getHorsePropItem());

		return respMsg;
	}
	
}
