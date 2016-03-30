package com.game.draco.app.operate.payextra.domain;

import java.util.Date;
import java.util.Map;

import lombok.Data;

import sacred.alliance.magic.util.Util;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufKV;
import com.game.draco.app.operate.domain.RoleOperateActive;
import com.google.common.collect.Maps;

public @Data class RolePayExtra extends RoleOperateActive {
	
	@Protobuf(fieldType = FieldType.KV, order = 10)
	@ProtobufKV(clazz = PayExtraStage.class)
	private Map<Integer, PayExtraStage> payExtraStageMap = Maps.newHashMap();
	
	/**
	 * 创建首冲赠送数据
	 * @return
	 */
	public static RolePayExtra createRoleGrowFund() {
		RolePayExtra extra = new RolePayExtra();
		extra.setInsertDB(true);
		return extra;
	}
	
	/**
	 * 是否已领取档位奖励
	 * @param rechargePoint
	 * @return
	 */
	public boolean isReward(int rechargePoint) {
		if (Util.isEmpty(this.payExtraStageMap)) {
			return false;
		}
		return this.payExtraStageMap.containsKey(rechargePoint);
	}
	
	/**
	 * 领取档位奖励
	 * @param rechargePoint
	 */
	public void reward(int rechargePoint) {
		PayExtraStage stage = new PayExtraStage();
		stage.setPayDate(new Date());
		stage.setRechargePoint(rechargePoint);
		this.payExtraStageMap.put(rechargePoint, stage);
		this.setUpdateDB(true);
	}

}
