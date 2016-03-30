package com.game.draco.app.dailyplay.domain;

import lombok.Data;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.KV;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

public @Data class DailyItemData implements KV<String,DailyItemData> {
		
	@Protobuf(fieldType=FieldType.STRING,order=1)
	private String dailyId ;
	/**
	 * 当天次数
	 */
	@Protobuf(fieldType=FieldType.INT32,order=2)
	private short curr ;
	/**
	 * 领奖状态
	 * 0 未领取
	 * 1 已领取
	 */
	@Protobuf(fieldType=FieldType.INT32,order=3)
	private byte state ;
	
	
	@Override
	public String $key() {
		return this.dailyId;
	}
	@Override
	public void $key(String v) {
		this.dailyId = v ;
	}
	
	@Override
	public DailyItemData $value() {
		return this;
	}
	
	@Override
	public void $value(DailyItemData arg0) {
	}
	
}
