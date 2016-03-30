package com.game.draco.app.richman.vo.event;

import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;
import sacred.alliance.magic.vo.map.MapRichManInstance;

import com.game.draco.GameContext;
import com.game.draco.app.richman.config.RichManBox;
import com.game.draco.app.richman.config.RichManEvent;
import com.game.draco.app.richman.vo.RichManRoleBehavior;
import com.game.draco.message.response.C2653_RichManRoleEventNoticeMessage;

public class RichManEventGetBox extends RichManEventLogic {
	private static RichManEventGetBox instance = new RichManEventGetBox();
	private RichManEventGetBox() {
		
	}
	
	public static RichManEventGetBox getInstance() {
		return instance;
	}

	@Override
	public void execute(MapRichManInstance mapInstance, RoleInstance role,
			RichManRoleBehavior behavior) {
		RichManEvent event = behavior.getEvent();
		if(null == event) {
			return ;
		}
		int boxId = (int)(event.getEventValue());
		RichManBox box = GameContext.getRichManApp().getRichManBox(boxId);
		if(null == box) {
			return ;
		}
		Integer eventId = Util.getWeightCalct(box.getWeightMap());
		if(null == eventId) {
			return ;
		}
		RichManEvent newEvent = GameContext.getRichManApp().getRichManEvent(eventId);
		if(null == newEvent) {
			return ;
		}
		mapInstance.addRoleBehavior(new RichManRoleBehavior(role.getIntRoleId(), newEvent));
		//通知客户端获得宝箱事件
		C2653_RichManRoleEventNoticeMessage respMsg = getRoleEventNoticeMessage(behavior);
		role.getBehavior().sendMessage(respMsg);
	}

}
