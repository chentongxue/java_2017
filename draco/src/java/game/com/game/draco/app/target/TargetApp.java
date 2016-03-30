package com.game.draco.app.target;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.AppSupport;
import com.game.draco.app.hint.HintSupport;
import com.game.draco.app.target.cond.TargetCondType;
import com.game.draco.app.target.cond.TargetLogic;
import com.game.draco.app.target.config.TargetConfig;
import com.game.draco.app.target.domain.RoleTarget;
import com.game.draco.app.target.vo.TargetRewardResult;
import com.game.draco.message.item.TargetDetailItem;

public interface TargetApp extends Service, AppSupport,HintSupport {
	void pushTargetHintMessage(RoleInstance role);
	/**
	 * 构建目标面板协议
	 * @param role
	 * @return
	 */
	Message createTargetPanelMessage(RoleInstance role);
	/**
	 * 目标面板领奖
	 * @param role
	 * @param targetId
	 * @return
	 */
	TargetRewardResult reward(RoleInstance role, short targetId);
	/**
	 * 构建目标详细信息
	 * @param role
	 * @param roleTarget
	 * @param targetId
	 * @return
	 */
	TargetDetailItem createTargetDetailItem(RoleInstance role, 
			RoleTarget roleTarget, short targetId);
	
	/**
	 * 构建默认目标详细信息
	 * @param line
	 * @return
	 */
	TargetDetailItem createDefaultTargetDetailItem(byte line);
	
	/**
	 * 返回目标条件对于的逻辑类
	 * @param condType
	 * @return
	 */
	TargetLogic getTargetLogic(TargetCondType condType);
	/**
	 * 其他目标目标统一入口
	 * @param role
	 * @param condType
	 * @param type 小类型eg：杀死xx npc xx次,type=npcid
	 * @param value
	 */
	void updateTarget(RoleInstance role, TargetCondType condType, String type, int value);
	/**
	 * 其他目标目标统一入口
	 * @param role
	 * @param condType
	 */
	void updateTarget(RoleInstance role, TargetCondType condType);
	
	
	TargetConfig getTargetConfig(short targetId);
	
	void onRoleLevelUp(RoleInstance role);
	
	/**
	 * 是否有红点提示
	 * @param role
	 * @return
	 */
	public boolean isHavaHint(RoleInstance role);
	
}
