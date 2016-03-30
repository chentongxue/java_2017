package com.game.draco.app.npc.domain;

public class NpcFunction {
	private String gateId;
	private String npcId;
	private int x;
	private int y;
	private int npcFuncType;
	private int mapindex;
	private String mapId;

	public String getMapId() {
		return mapId;
	}
	public void setMapId(String mapId) {
		this.mapId = mapId;
	}
	public String getNpcId() {
		return npcId;
	}
	public void setNpcId(String npcId) {
		this.npcId = npcId;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getNpcFuncType() {
		return npcFuncType;
	}
	public void setNpcFuncType(int npcFuncType) {
		this.npcFuncType = npcFuncType;
	}
	public String getGateId() {
		return gateId;
	}
	public void setGateId(String gateId) {
		this.gateId = gateId;
	}
	public int getMapindex() {
		return mapindex;
	}
	public void setMapindex(int mapindex) {
		this.mapindex = mapindex;
	}
}
