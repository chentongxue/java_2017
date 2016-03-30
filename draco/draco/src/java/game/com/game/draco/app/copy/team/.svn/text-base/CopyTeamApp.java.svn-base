package com.game.draco.app.copy.team;

import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

public interface CopyTeamApp extends Service{

	/**
	 * 是否在报名状态中
	 * @param role
	 * @return
	 */
	public boolean inApplyStatus(RoleInstance role);
	
	/**
	 * 报名
	 * @param role
	 * @return
	 */
	public CopyTeamResult apply(RoleInstance role,short copyId);
	
	/**
	 * 取消报名
	 * @param role
	 * @return
	 */
	public CopyTeamResult cancel(RoleInstance role) ;
	
	/**
	 * 获得报名信息
	 * @param role
	 * @return
	 */
	public ApplyInfo getApplyInfo(RoleInstance role);
	
	public void systemMatch();
	
	public void removeApplyInfo(String teamId);
	
}
