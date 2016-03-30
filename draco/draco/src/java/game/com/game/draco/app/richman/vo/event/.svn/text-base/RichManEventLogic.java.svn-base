package com.game.draco.app.richman.vo.event;

import sacred.alliance.magic.vo.RoleInstance;
import sacred.alliance.magic.vo.map.MapRichManInstance;

import com.game.draco.app.richman.config.RichManEvent;
import com.game.draco.app.richman.vo.RichManRoleBehavior;
import com.game.draco.message.response.C2653_RichManRoleEventNoticeMessage;

public abstract class RichManEventLogic {
	public abstract void execute(MapRichManInstance mapInstance, RoleInstance role, 
			RichManRoleBehavior behavior);
	
	protected C2653_RichManRoleEventNoticeMessage getRoleEventNoticeMessage(
			RichManRoleBehavior behavior) {
		C2653_RichManRoleEventNoticeMessage respMsg = new C2653_RichManRoleEventNoticeMessage();
		respMsg.setRoleId(behavior.getRoleId());
		RichManEvent event = behavior.getEvent();
		respMsg.setEventType(event.getType());
		respMsg.setEventValue((short)event.getEventValue());
		respMsg.setEffectId(event.getEffectId());
		return respMsg;
	}
	
	protected C2653_RichManRoleEventNoticeMessage getRoleEventNoticeMessage(
			RichManRoleBehavior behavior, String info) {
		C2653_RichManRoleEventNoticeMessage respMsg = this.getRoleEventNoticeMessage(behavior);
		respMsg.setInfo(info);
		return respMsg;
	}
	
}
