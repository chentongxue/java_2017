package com.game.draco.app.operate.vo;

import lombok.Data;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.Result;

public @Data class OperateRewardAttributeConfig {

	private byte type;
	private int value;
	private AttributeType attriType;
	
	public Result init(String info) {
		Result result = new Result();
		boolean flag = false;
		attriType = AttributeType.get(type);
		if (null == attriType) {
			flag = true;
			result.setInfo(info + " : type " + this.type + " not exist!");
		}
		if (this.value < 0) {
			flag = true;
			result.setInfo(info + " : type " + this.type + " value config error!");
		}
		if (flag) {
			return result;
		}
		return result.success();
	}
	
}
