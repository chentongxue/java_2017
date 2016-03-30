package com.game.draco.app.camp.war;

import java.util.List;

import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.AppSupport;
import com.game.draco.app.camp.war.config.RoleBattleConfig;
import com.game.draco.app.camp.war.vo.ApplyInfo;
import com.game.draco.app.camp.war.vo.MatchInfo;
import com.game.draco.app.camp.war.vo.RoleRewardResult;

public interface CampWarApp extends AppSupport{

	public Active getCampWarActive() ;
	
	public RoleBattleConfig getRoleBattleConfig() ;
	
	public void removeApplyInfo(String roleId);
	
	public Result apply(RoleInstance role);
	
	public Result cancel(RoleInstance role) ;
	
	public Float getHeroHpRate(String roleId,int heroId) ;
	
	public void addHeroHpRate(String roleId,int heroId,Float rate) ;
	
	public void clearAllHeroHpRate(String roleId) ;
	
	public ApplyInfo getApplyInfo(String roleId) ;
	
	public Message getCampWarPanelMessage(RoleInstance role,boolean autoApply);
	
	public void initCampMatchGroup() ;
	
	public int getLeaderConfigMaxHp() ;
	
	public List<RoleRewardResult> roleBattleEnd(MatchInfo match,AbstractRole winRole,AbstractRole failRole);
	
	public void doApplyMatch();
	
	public void removePanelRole(RoleInstance role) ;
	
	
}
