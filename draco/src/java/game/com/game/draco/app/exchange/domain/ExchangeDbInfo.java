package com.game.draco.app.exchange.domain;

import java.util.Date;

import lombok.Data;

public @Data class ExchangeDbInfo {
	public static final String ROLE_ID = "roleId" ;
	public static final String ID = "id" ;
	
	public ExchangeDbInfo(){
		
	}
	
	public ExchangeDbInfo(int id, String roleId, int times, Date lastExTime){
		this.id = id;
		this.roleId = roleId;
		this.times = times;
		this.lastExTime = lastExTime;
	}
	
	private int id;
	private String roleId;
	private int times; //已兑换次数
	private Date lastExTime;
	
	private Date expiredTime ;
	private boolean existRecord;
	
}
