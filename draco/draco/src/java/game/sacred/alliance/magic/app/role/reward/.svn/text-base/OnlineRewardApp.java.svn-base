package sacred.alliance.magic.app.role.reward;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

public interface OnlineRewardApp extends Service {
	
	/**
	 * 角色登录时初始化奖励信息
	 * @param role
	 */
	public void login(RoleInstance role);
	
	/**
	 * 角色下线
	 * @param role
	 */
	public void logout(RoleInstance role);
	
	
	public void roleLevelUpgrade(RoleInstance role) ;
	
	/**
	 * 领取奖励
	 * @param role
	 * @return
	 */
	public Result takeReward(RoleInstance role);
	
	/**
	 * 系统刷新在线领奖
	 */
	public void systemRefresh();
	
}
