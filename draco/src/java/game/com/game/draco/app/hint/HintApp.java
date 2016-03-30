package com.game.draco.app.hint;

import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.hint.vo.HintType;
import com.game.draco.message.item.HintHeroRulesItem;
import com.game.draco.message.item.HintRulesItem;
import com.game.draco.message.item.HintTimeNotifyItem;

public interface HintApp extends Service {
	
	/**
	 * 可领取提示特效的列表
	 * @param role
	 * @return
	 */
	public void pushHintListMessage(RoleInstance role);
	
	/**
	 * 可领取提示列表（带时间）
	 * @param role
	 */
	public void pushHintTimeListMessage(RoleInstance role);
	
	/**
	 * 可提示变化（带时间）
	 * @param role
	 */
	public void pushHintTimeChangeMessage(RoleInstance role, HintTimeNotifyItem hintTimeNotifyItem);
	
	/**
	 * 提示特效规则
	 * @param role
	 */
	public void pushHintRulesMessage(RoleInstance role);
	
	/**
	 * 提示特效对应的UI
	 * @param role
	 */
	public void pushHintUITreeMessage(RoleInstance role);
	
	/**
	 * 提示特效变化
	 * @param role 角色
	 * @param hintId 特效ID
	 * @param hasHint 是否有特效
	 */
	public void hintChange(RoleInstance role, HintType hintType, boolean hasHint);
	
	/**
	 * 获得新的英雄、宠物、坐骑或英雄升星、宠物升星、坐骑升星通知客户端规则变化
	 * @param role
	 * @param item
	 */
	public void pushHintRulesChange(RoleInstance role, HintRulesItem hintRulesItem);
	
	/**
	 * 英雄技能变化
	 * @param role
	 * @param hintSkillRulesItem
	 */
	public void pushHintSkillChange(RoleInstance role, HintHeroRulesItem hintHeroRulesItem);
	
	/**
	 * 系统重置可领取特效列表
	 * 定时任务触发
	 */
	public void sysPushHintMsg();
	
}
