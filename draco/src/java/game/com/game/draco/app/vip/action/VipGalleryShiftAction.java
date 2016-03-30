package com.game.draco.app.vip.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2511_VipGalleryShiftReqMessage;
/**
 * 
 */
public class VipGalleryShiftAction  extends BaseAction<C2511_VipGalleryShiftReqMessage>{
	@Override
	public Message execute(ActionContext ct, C2511_VipGalleryShiftReqMessage msg) {
		RoleInstance role = this.getCurrentRole(ct);
		if(role == null){
			return null;
		}
		byte vipLevel = msg.getVipLevel();
		return GameContext.getVipApp().vipGalleryShift(role, vipLevel);
	}
}
