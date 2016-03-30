package com.game.draco.app.operate.payextra.domain;

import java.util.Date;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.KV;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

import lombok.Data;

public @Data class PayExtraStage implements KV<Integer, PayExtraStage> {
	
	@Protobuf(fieldType = FieldType.INT32, order = 1)
	private int rechargePoint;
	@Protobuf(fieldType = FieldType.DATE, order = 2)
	private Date payDate;
	
	@Override
	public Integer $key() {
		return rechargePoint;
	}
	@Override
	public void $key(Integer rechargePoint) {
		this.rechargePoint = rechargePoint;
	}
	
	@Override
	public PayExtraStage $value() {
		return this;
	}
	
	@Override
	public void $value(PayExtraStage arg0) {
	}

}
