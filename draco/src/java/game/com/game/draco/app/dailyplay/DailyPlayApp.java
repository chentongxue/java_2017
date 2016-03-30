package com.game.draco.app.dailyplay;

import java.util.Collection;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.AppSupport;
import com.game.draco.app.dailyplay.config.DailyPlayReward;
import com.game.draco.app.dailyplay.config.DailyPlayRule;
import com.game.draco.app.hint.HintSupport;

public interface DailyPlayApp  extends AppSupport,HintSupport {

	public Collection<DailyPlayRule> getAllDailyPlayRule() ;
	
	public byte getStatus(DailyPlayRule daily,RoleInstance role) ;
	
	public short getCompleteTimes(int dailyId,RoleInstance role) ;
	
	public DailyPlayReward getDailyPlayReward(int dailyId,RoleInstance role);
	
	public Result recvReward(RoleInstance role,int dailyId) ;
	
	/**
	 * 
	 * @param role
	 * @param times
	 * @param dailyType
	 * @param ext 类型为副本时：副本id
	 */
	public void incrCompleteTimes(RoleInstance role,int times,
			DailyPlayType dailyType,String ext) ;
}
