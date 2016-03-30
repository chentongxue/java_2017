package sacred.alliance.magic.app.recall;

import java.util.Date;

import sacred.alliance.magic.vo.RoleInstance;

public interface RecallApp {
	/**
	 * 为了不占用登录线程异步处理领奖
	 * @param lastLoginTime
	 */
	public void sendRecallAwardMsg(RoleInstance role, Date lastLoginTime);
	/**
	 * 发放回归玩家奖励
	 * @param role
	 */
	public void sendRecallAward(int roleId, long lastLoginTime);
}
