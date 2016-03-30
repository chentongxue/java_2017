package com.game.draco.app.target.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;

import com.game.draco.app.target.cond.TargetCondType;

public @Data class TargetCond implements KeySupport<Short>{
	private short id;	//条件id
	private byte type;	//条件类型
	private int value;	//英雄xx个
	private String param1;	//xx品质
	private String param2;	//xx星
	private String desc;	//描述
	private String buttonText;	//未完成显示文本
	/**
	 * 前往ID
	 */
	private short forwardId ;
	
	//变量
	private TargetCondType condType;
	
	@Override
	public Short getKey() {
		return this.id;
	}
	
	public void init(String fileInfo) {
		TargetCondType condType = TargetCondType.get(this.type);
		if(null == condType) {
			Log4jManager.CHECK.error(fileInfo + ", type=" + this.type + " config error");
			Log4jManager.checkFail();
		}
		this.condType = condType;
	}

	
}
