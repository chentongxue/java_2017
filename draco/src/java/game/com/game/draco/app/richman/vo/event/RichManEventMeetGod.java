package com.game.draco.app.richman.vo.event;

import java.util.Map;

import sacred.alliance.magic.vo.RoleInstance;
import sacred.alliance.magic.vo.map.MapRichManInstance;

import com.game.draco.GameContext;
import com.game.draco.app.richman.config.RichManConfig;
import com.game.draco.app.richman.config.RichManEvent;
import com.game.draco.app.richman.domain.RoleRichMan;
import com.game.draco.app.richman.vo.RichManRoleBehavior;
import com.game.draco.message.item.RichManEventItem;
import com.game.draco.message.response.C2653_RichManRoleEventNoticeMessage;
import com.game.draco.message.response.C2659_RichManRoleHeadAnimAddRespMessage;

public class RichManEventMeetGod extends RichManEventLogic {
	private static RichManEventMeetGod instance = new RichManEventMeetGod();
	private RichManEventMeetGod() {
		
	}
	
	public static RichManEventMeetGod getInstance() {
		return instance;
	}
	
	@Override
	public void execute(MapRichManInstance mapInstance, RoleInstance role, 
			RichManRoleBehavior behavior) {
		RoleRichMan rrm = GameContext.getUserRichManApp().getRoleRichMan(role.getIntRoleId());
		if(null == rrm) {
			return ;
		}
		RichManEvent event = behavior.getEvent();
		if(null == event) {
			return ;
		}
		RichManConfig config = GameContext.getRichManApp().getRichManConfig();
		Map<Integer, Byte> randomEventMap = rrm.getRandomEventMap();
		randomEventMap.put(event.getId(), config.getGodMoveNum());
		//广播
		C2653_RichManRoleEventNoticeMessage respMsg = getRoleEventNoticeMessage(behavior);
		mapInstance.broadcastMap(null, respMsg);
		//玩家头顶动画广播
		C2659_RichManRoleHeadAnimAddRespMessage animAddRespMsg = new C2659_RichManRoleHeadAnimAddRespMessage();
		animAddRespMsg.setRoleId(role.getIntRoleId());
		RichManEventItem eventItem = new RichManEventItem();
		eventItem.setId((short)event.getId());
		eventItem.setAnimId(event.getAnimId());
		animAddRespMsg.setEventItem(eventItem);
		mapInstance.broadcastMap(null, animAddRespMsg);
	}

}
