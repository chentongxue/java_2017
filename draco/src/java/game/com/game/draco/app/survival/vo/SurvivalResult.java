package com.game.draco.app.survival.vo;

import lombok.Data;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.vo.AbstractRole;

import com.game.draco.GameContext;
import com.game.draco.app.copy.team.vo.ApplyInfo;
import com.game.draco.app.team.LeaveTeam;
import com.game.draco.app.team.Team;

public @Data class SurvivalResult extends Result{

	private boolean notifyTeam = false ;
	
	/**
	 * 保证先清除进度,后移除报名信息
	 * @param master
	 * @param slaves
	 */
	private void memberChange(ApplyInfo master,ApplyInfo... slaves){
		Team masterTeam = master.getTeam();
		for(ApplyInfo slave : slaves){
			Team slaveTeam = slave.getTeam();
			for (AbstractRole role : slave.getApplyRoles()) {
				slaveTeam.memberLeave(role, LeaveTeam.system);
				masterTeam.memberJoin(role);
			}
		}
		//移除报名信息
		GameContext.getCopyTeamApp().removeApplyInfo(master.getTeamId());
		for(ApplyInfo slave : slaves){
			GameContext.getCopyTeamApp().removeApplyInfo(slave.getTeamId());
		}
	}
	
}
