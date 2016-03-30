package com.game.draco.app.copy.team;

import com.game.draco.app.copy.team.vo.ApplyInfo;
import com.game.draco.app.copy.team.vo.TeamResult;
import com.game.draco.app.team.Team;

import sacred.alliance.magic.base.Result;
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
	public TeamResult apply(RoleInstance role, short copyId, byte type);
	
	/**
	 * 取消报名
	 * @param role
	 * @return
	 */
	public TeamResult cancel(RoleInstance role, String enterCopyId) ;
	
	/**
	 * 获得报名信息
	 * @param role
	 * @return
	 */
	public ApplyInfo getApplyInfo(RoleInstance role);
	
	/**
	 * 系统匹配
	 */
	public void systemMatch();
	
	/**
	 * 删除匹配信息
	 * @param teamId
	 */
	public void removeApplyInfo(String teamId);
	
	/**
	 * 给队长发进入副本提示消息
	 * @param team
	 * @param copyId
	 */
	public void matchSuccess(Team team,short copyId);
	
	/**
	 * 组队副本确认
	 * @param role
	 * @param confirm
	 */
	public void copyTeamConfirm(RoleInstance role, String confirm);
	
	/**
	 * 是否满足副本进入条件（个人）
	 * @param role
	 * @param copyId
	 * @return
	 */
	public Result canEnterCopy(RoleInstance role, short copyId);
	
	/**
	 * 是否满足发布副本条件
	 * @param role
	 * @param copyId
	 * @return
	 */
	public TeamResult canApplyCopy(RoleInstance role, short copyId);
	
}
