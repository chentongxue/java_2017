package com.game.draco.app.team.vo;

import java.util.List;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.team.PlayerTeam;
import com.game.draco.message.item.TeamPanelTargetDetailItem;

public interface TargetTypeLogic {
	
	/**
	 * 获取该类目标列表
	 * @return
	 */
	public List<TeamPanelTargetDetailItem> getTeamPanelTargetDetailItemList();
	
	/**
	 * 是否有足够的次数
	 * @param role
	 * @return
	 */
	public boolean countEnough(RoleInstance role, short targetId);
	
	/**
	 * 获取目标类型
	 * @return
	 */
	public TeamPanelTargetType getTeamPanelTargetType();
	
	/**
	 * 组队目标开始
	 * @param role
	 * @param team
	 * @return
	 */
	public Message targetForword(RoleInstance role, PlayerTeam team);
	
	/**
	 * 获取目标名称
	 * @param targetId
	 * @return
	 */
	public String getTargetName(short targetId);
	
}
