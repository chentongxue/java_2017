package com.game.draco.app.richman.vo.event;

import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.vo.RoleInstance;
import sacred.alliance.magic.vo.map.MapRichManInstance;

import com.game.draco.GameContext;
import com.game.draco.app.richman.config.RichManEvent;
import com.game.draco.app.richman.vo.RichManRoleBehavior;
import com.game.draco.app.richman.vo.RichManRoleStat;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.response.C2653_RichManRoleEventNoticeMessage;

public class RichManEventTrun extends RichManEventLogic {
	private static RichManEventTrun instance = new RichManEventTrun();
	private RichManEventTrun() {
		
	}
	
	public static RichManEventTrun getInstance() {
		return instance;
	}

	@Override
	public void execute(MapRichManInstance mapInstance, RoleInstance role, 
			RichManRoleBehavior behavior) {
		RichManEvent event = behavior.getEvent();
		if(null == event) {
			return ;
		}
		RichManRoleStat roleStat = mapInstance.getRoleStat(role.getIntRoleId());
		if(null == roleStat) {
			return ;
		}
		//设置新方向
		byte oldFace = roleStat.getFace();
		byte newFace = (oldFace == RichManRoleStat.FACE_FORWARD ? 
				RichManRoleStat.FACE_BACK : RichManRoleStat.FACE_FORWARD);
		roleStat.setFace(newFace);
		event.setEventValue(newFace);
		//提示
		C0003_TipNotifyMessage tipMsg = new C0003_TipNotifyMessage();
		tipMsg.setMsgContext(GameContext.getI18n().getText(TextId.Richman_event_trun));
		role.getBehavior().sendMessage(tipMsg);
		//广播
		C2653_RichManRoleEventNoticeMessage respMsg = getRoleEventNoticeMessage(behavior);
		mapInstance.broadcastMap(null, respMsg);
	}

}
