package com.game.draco.app.union.domain.instance;

import lombok.Data;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.KV;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

public @Data class RoleDps implements KV<Integer,RoleDps> {
	
	//角色Id
	@Protobuf(fieldType=FieldType.INT32,order=1)
	private int roleId;
	
	//角色名称
	@Protobuf(fieldType=FieldType.STRING,order=2)
	private String roleName;
	
	//角色DPS
	@Protobuf(fieldType=FieldType.INT32,order=3)
	private int dps;

	@Override
	public Integer $key() {
		return roleId;
	}

	@Override
	public void $key(Integer v) {
		this.roleId = v ;		
	}

	@Override
	public RoleDps $value() {
		return this;
	}

	@Override
	public void $value(RoleDps arg0) {
	}

}
