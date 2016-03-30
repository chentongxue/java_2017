package com.game.draco.app.goblin.map;

import sacred.alliance.magic.app.map.MapLineInstanceCreatedEvent;
import sacred.alliance.magic.vo.MapLineInstance;

import com.game.draco.GameContext;
import com.google.common.eventbus.Subscribe;

public class MapLineInstanceCreatedListener {

	@Subscribe
	public void onMapLineInstanceCreated(MapLineInstanceCreatedEvent event){
		if(null == event){
			return ;
		}
		MapLineInstance mapInstance = event.getMapInstance() ;
		if(null == mapInstance || 1 != mapInstance.getLineId()){
			return ;
		}
		if (GameContext.getGoblinApp().isOnGoblinActive()) {
			GameContext.getGoblinApp().refreshSignGoblinOrJumpPoint(event.getMapInstance());
		}
	}
}
