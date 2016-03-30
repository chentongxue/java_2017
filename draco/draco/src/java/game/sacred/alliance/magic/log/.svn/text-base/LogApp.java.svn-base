package sacred.alliance.magic.log;

import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

public interface LogApp extends Service{
	/**
	 * 角色首次创建角色日志
	 * @param role
	 */
	public void activeLog(RoleInstance role, int createdRoleNum, String ip);
	
	/**
	 * 角色等级变化日志
	 * @param role
	 */
	public void userGradeLog(RoleInstance role);
	
	/**
	 * 角色登陆日志
	 * @param role
	 */
	public void loginLog(RoleInstance role);
	
	/**
	 * 在线人数日志
	 */
	public void onlineLog();
	
	/**
	 * 跨月定时任务
	 */
	public void createNewLogger();
}
