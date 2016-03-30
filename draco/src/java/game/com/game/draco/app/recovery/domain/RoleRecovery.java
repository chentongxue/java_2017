package com.game.draco.app.recovery.domain;

import java.util.Date;

import lombok.Data;
import sacred.alliance.magic.constant.Cat;
/**
 * 一键追回
 * roleLevel:今天第一次登陆的角色等级
 * maxNum:   昨天可追回数目
 * data:     如果是离线经验，记录“未获得的挂机经验”
 */
public @Data class RoleRecovery implements Comparable<RoleRecovery>{
	
	public final static String ROLE_ID = "roleId" ;
	public final static String RECOVER_ID = "recoveryId" ;
	private String roleId;        //角色Id
	private String recoveryId;    //追回Id = recoveryId_pram
	
	private int roleLevel;
	private int num;       //已经追回的数目
	private int maxNum;    //总的追回数目
	
	private String data;   //如果是离线经验，记录“未获得的挂机经验”
	
	private Date updateTime = new Date();
	
	public String getKey(){
		return roleId + Cat.underline + recoveryId;
	}
	public RoleRecovery incrementNum(){
		this.num ++;
		return this;
	}
	public RoleRecovery incrementNum(int num){
		this.num += num;
		return this;
	}
	//获得可以追回的项目
	public int getRecoveryNum(){
		if(num >= maxNum){
			return 0;
		}
		return maxNum - num;
	}
	@Override
	public int compareTo(RoleRecovery o) {
		try{
			return Integer.parseInt(recoveryId) - Integer.parseInt(o.recoveryId);
		}catch (Exception e) {
			return 0;
		}
	}
	@Override
	public boolean equals(Object o){
		if (this == o) {
			return true;
		}
		if (o instanceof RoleRecovery) {
			RoleRecovery other = (RoleRecovery) o;
			return this.roleId.equals(other.getRoleId()) && this.recoveryId.equals(other.getRecoveryId());
		}
		return false;
	}
}
