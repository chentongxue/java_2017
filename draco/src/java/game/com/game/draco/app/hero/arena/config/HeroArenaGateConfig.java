package com.game.draco.app.hero.arena.config;

import lombok.Data;
import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.app.map.data.MapConfig;
import sacred.alliance.magic.base.MapLogicType;
import sacred.alliance.magic.util.Log4jManager;

import com.game.draco.GameContext;

@Data
public class HeroArenaGateConfig {
	
	//关卡ID
	private int gateId;
	private String mapId;
	private short mapX1;
	private short mapY1;
	private short mapX2;
	private short mapY2;
	
	public void checkInit(String fileInfo){
		Map map = GameContext.getMapApp().getMap(this.mapId);
		if(null == map){
			this.checkFail(fileInfo + " mapId is empty.");
		}
		MapConfig mapConfig = map.getMapConfig();
		if(!mapConfig.changeLogicType(MapLogicType.heroArena)){
			this.checkFail(fileInfo + " map's LogicType is not heroArena or defaultLogic.");
		}
		if(this.mapX1 <= 0 || this.mapY1 <= 0){
			this.checkFail(fileInfo + " mapX1 or mapY1 is error.");
		}
		if(this.mapX2 <= 0 || this.mapY2 <= 0){
			this.checkFail(fileInfo + " mapX2 or mapY2 is error.");
		}
	}
	
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
}
