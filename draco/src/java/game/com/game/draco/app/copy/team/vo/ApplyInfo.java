package com.game.draco.app.copy.team.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;
import com.game.draco.app.team.Team;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.response.C0221_CopyTeamCancelRespMessage;

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
	
	
	public void notifyLeave(String msgContext) {
		try {
			if (null == this.applyRoles) {
				return;
			}
			C0221_CopyTeamCancelRespMessage msg = new C0221_CopyTeamCancelRespMessage();
			msg.setStatus((byte) 1);
			C0003_TipNotifyMessage message = new C0003_TipNotifyMessage();
			message.setMsgContext(msgContext);
			for (AbstractRole role : this.applyRoles) {
				RoleInstance player = (RoleInstance) role;
				Team team = player.getTeam();
				if (null != team && !team.getTeamId().equals(this.teamId) && GameContext.getCopyTeamApp().inApplyStatus(player)) {
					//排除新队伍也报名了的情况
					continue;
				}
				role.getBehavior().sendMessage(msg);
				// 飘字通知
				role.getBehavior().sendMessage(message);
			}
		} catch (Exception ex) {
			logger.error("ApplyInfo.notifyLeave error!", ex);
		}
	}
	
}
