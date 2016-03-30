package com.game.draco.app.exchange.domain;

import java.util.Date;

import lombok.Data;

public @Data class ExchangeDbInfo {
	
	public ExchangeDbInfo(){
		
	}
	
	public ExchangeDbInfo(int id, String roleId, byte times, Date lastExTime, Date expiredTime){
		this.id = id;
		this.roleId = roleId;
		this.times = times;
		this.lastExTime = lastExTime;
		this.expiredTime = expiredTime;
	}
	
	private int id;
	private String roleId;
	private byte times; //已兑换次数
	private Date lastExTime;
	private Date expiredTime;
	
	private boolean existRecord;
}
