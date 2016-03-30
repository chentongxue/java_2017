package com.game.draco.app.goblin.vo;

import lombok.Data;

import com.game.draco.app.goblin.map.GoblinJumpMapPoint;

public @Data class GoblinJumpPointInfo {

	private String mapInstanceId;// 哥布林密境地图实例Id
	private String mapId;// 野图地图Id
	private String npcName;
	private String jumpMapId;
	private int mapX;
	private int mapY;
	private String toMapId;
	
	public void saveGoblinJumpMapPoint(GoblinJumpMapPoint jumpPoint) {
		this.jumpMapId = jumpPoint.getMapid();
		this.mapX = jumpPoint.getX();
		this.mapY = jumpPoint.getY();
		this.toMapId = jumpPoint.getTomapid();
	}
	
	public GoblinJumpMapPoint createGoblinJumpMapPoint() {
		GoblinJumpMapPoint jumpPoint = new GoblinJumpMapPoint();
		jumpPoint.setMapid(jumpMapId);
		jumpPoint.setX(mapX);
		jumpPoint.setY(mapY);
		jumpPoint.setTomapid(toMapId);
		return jumpPoint;
	}
	
}
