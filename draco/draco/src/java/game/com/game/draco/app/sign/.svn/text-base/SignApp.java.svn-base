package com.game.draco.app.sign;

import java.util.Collection;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.sign.config.SignConfig;

public interface SignApp {
	
	public int getMonthSignValue(RoleInstance role) ;
	
	public int getCurrSignRecv(RoleInstance role) ;
	
	public boolean isSigned(int signValue,int day) ;
	
	public int getCurrSignTimes(RoleInstance role) ;
	
	public byte getRecvState(int awardValue,int totalSignTimes,int times) ;

	
	public Result recvAward(RoleInstance role,int times) ;
	
	public Result signRepair(RoleInstance role);
	
	public Result sign(RoleInstance role) ;
	
	public Collection<SignConfig> getAllSignConfig();
}
