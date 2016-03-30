package com.game.draco.app.copy.team;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;
import com.game.draco.message.response.C0221_CopyTeamCancelRespMessage;

import sacred.alliance.magic.app.team.Team;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.RoleInstance;

public @Data class ApplyInfo {
	private final static Logger logger = LoggerFactory.getLogger(ApplyInfo.class);
	private short copyId ;
	private long applyTime ;
	private List<AbstractRole> applyRoles ;
	private Team team ;
	private String teamId = "";
	
	public ApplyInfo(Team team,short copyId){
		this.team = team ;
		this.teamId = team.getTeamId();
		this.applyRoles = new ArrayList<AbstractRole>();
		applyRoles.addAll(team.getMembers());
		this.setCopyId(copyId);
		this.setApplyTime(System.currentTimeMillis());
	}
	
	
	public void notifyLeave(){
		try {
			if (null == this.applyRoles || this.applyRoles.size() < 2) {
				return;
			}
			C0221_CopyTeamCancelRespMessage msg = new C0221_CopyTeamCancelRespMessage();
			msg.setStatus((byte) 1);
			for (AbstractRole role : this.applyRoles) {
				RoleInstance player = (RoleInstance)role;
				Team team = player.getTeam();
				if(null != team 
						&& !team.getTeamId().equals(this.teamId)
						&& GameContext.getCopyTeamApp().inApplyStatus(player)){
					//排除新队伍也报名了的情况
					continue ;
				}
				role.getBehavior().sendMessage(msg);
			}
		}catch(Exception ex){
			logger.error("",ex);
		}
	}
}
