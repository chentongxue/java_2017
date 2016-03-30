package com.game.draco.app.survival;

import sacred.alliance.magic.base.ChallengeResultType;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.survival.vo.SurvivalApplyInfo;
import com.game.draco.app.survival.vo.SurvivalResult;
import com.game.draco.app.team.Team;
import com.game.draco.message.response.C0282_SurvivalInfoRespMessage;

public interface SurvivalBattleApp{
	
	public void start();
	
	/**
	 * 报名
	 * @param role
	 * @param type
	 * @return
	 */
	public SurvivalResult apply(RoleInstance role,byte type);
	
	/**
	 * 取消报名
	 * @param role
	 * @return
	 */
	public SurvivalResult cancel(RoleInstance role) ;

	/**
	 * 系统检查
	 */
	public void systemMatch();
	
	public SurvivalApplyInfo getSurvivalApplyInfo(RoleInstance role);
	
	/**
	 * 生存战场队友确认
	 * @param role
	 * @param confirm
	 */
	public void survivalTeamConfirm(RoleInstance role, String confirm);
	
	/**
	 * 是否在报名状态
	 * @param role
	 * @return
	 */
	public boolean isApplyStatus(RoleInstance role);
	
	/**
	 * 生存战场信息
	 */
	public C0282_SurvivalInfoRespMessage sendC0282_SurvivalInfoRespMessage(RoleInstance role);
	
	/**
	 * 生存战场奖励
	 * @param type
	 */
	
	public void sendSurvivalBattleReward(Team team,ChallengeResultType result,String instanceId);
	
	/**
	 * 生存战场结束面板
	 */
	public void gameOver(Team team,ChallengeResultType result,String instanceId);
	
	/**
	 * 判断是否在活动内
	 */
	public boolean isTimeOpen(boolean flag);
	
	/**
	 * 删除已结束的队列
	 */
	public void pollApplyInfo(SurvivalApplyInfo info);

}
