package com.game.draco.app.forward.logic;

import sacred.alliance.magic.app.map.data.MapConfig;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.forward.config.ForwardConfig;
import com.game.draco.message.push.C0215_PathToPointSearchNotifyMessage;

public class PathToPointLogic implements ForwardLogic {

	@Override
	public void forward(RoleInstance role, ForwardConfig config) {
		String mapId = config.getParameter() ;
		if(Util.isEmpty(mapId)){
			return ;
		}
		MapConfig mapConfig = GameContext.getMapApp().getMapConfig(mapId);
		if(null == mapConfig){
			return ;
		}
		C0215_PathToPointSearchNotifyMessage notifyMsg = new C0215_PathToPointSearchNotifyMessage();
		notifyMsg.setMapId(mapId);
		notifyMsg.setMapX((short)mapConfig.getMaporiginx());
		notifyMsg.setMapY((short)mapConfig.getMaporiginy());
		role.getBehavior().sendMessage(notifyMsg);
	}

	@Override
	public ForwardLogicType getForwardLogicType() {
		return ForwardLogicType.path_to_point ;
	}

}
