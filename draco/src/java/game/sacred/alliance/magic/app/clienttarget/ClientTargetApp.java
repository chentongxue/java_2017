package sacred.alliance.magic.app.clienttarget;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.vo.RoleInstance;

public interface ClientTargetApp {
	
	/**
	 * 自动采样（定时任务触发）
	 */
	public void autoCollect();
	
	/**
	 * GM采样（调试平台触发）
	 * @param roleNum 采样人数
	 * @return
	 */
	public Result gmCollect(int roleNum);
	
	/**
	 * 采样指定角色的信息
	 * @param role
	 * @return
	 */
	public Result collectRole(RoleInstance role);
	
}
