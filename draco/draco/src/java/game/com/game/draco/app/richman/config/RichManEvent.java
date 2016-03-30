package com.game.draco.app.richman.config;

import com.game.draco.app.richman.vo.RichManEventType;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data class RichManEvent implements KeySupport<Integer>{
	private int id; //事件id
	private byte type; //事件类型
	private short animId; //外形动画
	private float eventValue; //改变值
	private short effectId; //特效
	
	//变量
	private RichManEventType eventType;
	
	@Override
	public Integer getKey() {
		return this.id;
	}
	
	public Result init() {
		Result result = new Result();
		eventType = RichManEventType.get(this.type);
		if(null == eventType) {
			result.setInfo("RichManEvent.init(), id=" + this.id + ", type= " + this.type + "not exist");
			return result;
		}
		result.success();
		return result;
	}
}
