package com.game.draco.app.camp.war.vo;

import java.util.concurrent.atomic.AtomicInteger;

import lombok.Data;

public @Data class ApplyInfo {
	private final static AtomicInteger idGen = new AtomicInteger(0);
	
	public ApplyInfo(String roleId){
		this.roleId = roleId ;
		this.id = idGen.incrementAndGet();
	}
	
	private int id  ;
	private String roleId ;
	//便于广播时判断
	private byte campId ;
	//报名时间
	private long createDate ;
	private boolean cancel = false ;
	//是否已经匹配
	private MatchInfo match = null ; 
}
