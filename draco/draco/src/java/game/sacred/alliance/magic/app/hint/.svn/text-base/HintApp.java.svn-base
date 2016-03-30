package sacred.alliance.magic.app.hint;

import sacred.alliance.magic.vo.RoleInstance;

public interface HintApp {
	
	/**
	 * 可领取提示特效的列表
	 * @param role
	 * @return
	 */
	public void pushHintListMessage(RoleInstance role);
	
	/**
	 * 提示特效变化
	 * @param role 角色
	 * @param hintId 特效ID
	 * @param hasHint 是否有特效
	 */
	public void hintChange(RoleInstance role, HintId hintId, boolean hasHint);
	
	/**
	 * 系统重置可领取特效列表
	 * 定时任务触发
	 */
	public void sysPushHintMsg();
	
}
