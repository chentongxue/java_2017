package com.game.draco.app.survival.vo;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.survival.config.SurvivalBase;
import com.game.draco.app.team.Team;
import com.game.draco.message.response.C0280_SurvivalRespMessage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public @Data class SurvivalApplyInfo {
	private final static Logger logger = LoggerFactory.getLogger(SurvivalApplyInfo.class);
	private volatile boolean status = false;
	private volatile List<Team> teamList = Lists.newArrayList();
	private long unixId = 0;
	private Map<String,Byte> teamSizeMap = Maps.newHashMap();
	
	public SurvivalApplyInfo(long unixId){
		this.unixId = unixId;
	}
	
	public void addSurvivalApplyInfo(Team team){
		teamList.add(team);
		teamSizeMap.put(team.getTeamId(), (byte)team.getMembers().size());
	}
	
	public void removeSurvivalApplyInfo(Team team){
		Iterator<Team> iter = teamList.iterator();
		while(iter.hasNext()){
			Team t = iter.next();
			if(t.getTeamId().equals(team.getTeamId())){
				iter.remove();
			}
		}
		teamSizeMap.remove(team.getTeamId());
	}
	
	
	/**
	 * 系统踢出还是队长取消 0系统 1队长
	 * @param flag
	 */
	public void notifyLeave(boolean flag,Team team){
		try {
			C0280_SurvivalRespMessage msg = new C0280_SurvivalRespMessage();
			for (AbstractRole role : team.getMembers()) {
				RoleInstance player = (RoleInstance)role;
				Team roleTeam = player.getTeam();
				if(roleTeam == null){
					break;
				}
				if(!team.getTeamId().equals(roleTeam.getTeamId()) 
						&& GameContext.getSurvivalBattleApp().isApplyStatus(player)){
					break ;
				}
				if(!flag){
					msg.setStatus((byte)1);
					role.getBehavior().sendMessage(msg);
				}
			}
		}catch(Exception ex){
			logger.error("",ex);
		}
	}
	
	/**
	 *	判断人数是否达到上限
	 */
	public boolean isTeamMaxMember(){
		SurvivalBase base = GameContext.getSurvivalApp().getSurvivalBase();
		int num = 0;
		for(Team team : teamList){
			num += team.getPlayerNum();
		}
		if(num >= base.getMaximum()){
			return true;
		}
		return false;
	}
	
	public boolean isStatus(){
		return status;
	}
	
	public void changeStatus(){
		status = true;
	}
	
}
