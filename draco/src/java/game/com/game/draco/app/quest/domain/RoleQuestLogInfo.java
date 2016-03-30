package com.game.draco.app.quest.domain;

import java.util.Date;

import lombok.Data;

public @Data class RoleQuestLogInfo {

	private String roleId;
	private int questId;
	private int phase;//阶段
	private int status;//状态
	private int data1;
	private int data2;
	private int data3;
	private int awardId;//奖励ID
	private Date createTime = new Date();
	private Date updateTime;
	private boolean inDatabase = false;//表示库中是否存在
	
	public int[] getDataValues(){
		int[] values = new int[3];
		values[0] = this.data1;
		values[1] = this.data2;
		values[2] = this.data3;
		return values;
	}
	
}
