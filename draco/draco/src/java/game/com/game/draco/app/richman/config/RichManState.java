package com.game.draco.app.richman.config;

import com.game.draco.app.richman.vo.RichManStateType;

import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;
import lombok.Data;

public @Data class RichManState implements KeySupport<Byte>{
	private byte id; //状态id
	private byte type; //类型
	private short imageId; //状态图片
	private String name; //状态名
	private short time; //状态持续时间
	private short effectId; //特效id
	
	//变量
	private RichManStateType stateType;
	
	public void init() {
		stateType = RichManStateType.get(this.type);
		if(null == stateType) {
			Log4jManager.CHECK.error("RichManState init error: id=" + this.id + ", type config error");
			Log4jManager.checkFail();
		}
	}
	
	@Override
	public Byte getKey() {
		return this.id;
	}
}
