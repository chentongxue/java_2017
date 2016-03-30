package com.game.draco.app.operate.firstpay.domain;

import lombok.Data;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.game.draco.app.operate.domain.RoleOperateActive;
import com.game.draco.app.operate.vo.OperateActiveType;

public @Data class RoleFirstPay extends RoleOperateActive {
	
	@Protobuf(fieldType = FieldType.INT32,order = 10)
	private int status;// 领奖状态
	
	/**
	 * 创建RoleFirstPay对象，并赋值活动类型和充值时间
	 * @return
	 */
	public static RoleFirstPay createRoleFirstPay() {
		RoleFirstPay roleFirstPay = new RoleFirstPay();
		roleFirstPay.setActiveType(OperateActiveType.first_pay.getType());
		roleFirstPay.setInsertDB(true);
		return roleFirstPay;
	}

}
