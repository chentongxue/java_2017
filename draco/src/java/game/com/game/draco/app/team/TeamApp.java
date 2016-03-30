package com.game.draco.app.team;

import java.util.List;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.AppSupport;
import com.game.draco.app.copy.team.vo.TeamResult;
import com.game.draco.app.team.vo.ApplyInfo;
import com.game.draco.app.team.vo.TeamFullConfig;
import com.game.draco.app.team.vo.TeamTargetConfig;
import com.game.draco.message.item.TeamPanelTargetDetailItem;
import com.game.draco.message.item.TeamPanelTargetTypeItem;

public interface TeamApp extends Service, AppSupport{
	
	/**
	 * 判断是否满足组队条件，给目标发送弹板
	 * @param role 邀请人
	 * @param targRole 被邀请人
	 * @return
	 */
	public Status canBuildTeam(RoleInstance role, RoleInstance targRole);
	
	/**
	 * 组队
	 * @param role 被邀请人
	 * @param invitorRole 邀请人
	 * @return
	 */
	public Status buildTeam(RoleInstance role, RoleInstance invitorRole);
	
	/**
	 * 是否在同一队伍
	 * @param role1
	 * @param role2
	 * @return
	 */
	public boolean isInSameTeam(AbstractRole role1, AbstractRole role2); 
	
	/**
	 * 获取同一地图的活着的组队玩家
	 * @param role
	 * @return
	 */
	public List<AbstractRole> getTeamMembersInSameMap(RoleInstance role);
	
	/**
	 * 组队系数
	 * @param teamMembers
	 * @return
	 */
	public double teamCoefficient(int teamMembers);
	
	/** 
	 * 加入下线队伍缓存
	 **/
	public void addOfflineCache(AbstractRole role);
	
	/**
	 * 获取队伍目标类型列表
	 * @return
	 */
	public List<TeamPanelTargetTypeItem> getTeamPanelTargetTypeList(RoleInstance role);
	
	/**
	 * 获取匹配信息
	 * @param teamId
	 * @return
	 */
	public ApplyInfo getMatchApplyInfo(String teamId);
	
	/**
	 * 发布组队信息
	 * @param role
	 * @param applyInfo
	 * @return
	 */
	public TeamResult teamPublish(RoleInstance role, byte targetType, short targetId, byte number);
	
	/**
	 * 取消组队发布
	 * @param role
	 * @param applyInfo
	 * @return
	 */
	public Result cancelTeamPublish(RoleInstance role);
	
	/**
	 * 申请加入发布队伍
	 * @param role
	 * @return
	 */
	public Result teamPanelPublishApply(RoleInstance role, PlayerTeam team);
	
	/**
	 * 系统匹配
	 */
	public void systemMatch();
	
	/**
	 * 移除报名信息
	 */
	public void removeApplyInfo(String teamId);
	
	/**
	 * 报名匹配
	 * @param role
	 * @param applyInfo
	 * @return
	 */
	public TeamResult teamApply(RoleInstance role, byte targetType, short targetId, byte number);
	
	/**
	 * 取消匹配
	 * @param role
	 * @return
	 */
	public Result cancelTeamApply(RoleInstance role);
	
	/**
	 * 移除发布信息
	 * @param teamId
	 */
	public void removePublishInfo(String teamId);
	
	/**
	 * 获取发布队伍
	 * @param teamId
	 * @return
	 */
	public PlayerTeam getPublishTeam(String teamId);
	
	/**
	 * 获取目标的名称
	 * @param targetType
	 * @param targetId
	 * @return
	 */
	public String getTargetName(byte targetType, short targetId);
	
	/**
	 * 获取队伍满员喊话
	 * @param targetType
	 * @param target
	 * @return
	 */
	public TeamFullConfig getTeamFullConfig(byte targetType, short targetId);
	
	/**
	 * 获取该类目标列表
	 * @param role
	 * @param targetType
	 * @return
	 */
	public List<TeamPanelTargetDetailItem> getTeamPanelTargetDetailList(byte targetType);
	
	/**
	 * 组队副本开始
	 * @param playerTeam
	 * @return
	 */
	public Message targetForward(RoleInstance role, PlayerTeam playerTeam);
	
	/**
	 * 获取目标配置
	 * @param targetType
	 * @param targetId
	 * @return
	 */
	public TeamTargetConfig getTeamTargetConfig(byte targetType, short targetId);
	
//	/**
//	 * 队内亲密读等级
//	 * @param role
//	 * @return
//	 */
//	public List<TeamPanelIntimateItem> getTeamPanelIntimateItemList(RoleInstance role);
	
}
