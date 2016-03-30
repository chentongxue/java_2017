package com.game.draco.app.union.config;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data class UnionDonate implements KeySupport<Byte>{
	
	//id
	private byte id;
	
	//金钱类型
	private byte moneyType;
	
	//消耗的金钱
	private int money;
	
	//公会获得人气
	private int contribute;
	
	//角色获得DKP
	private int addDkp;
	
	//每日最大捐献次数
	private byte maxCount;

	@Override
	public Byte getKey() {
		return getId();
	}
}
