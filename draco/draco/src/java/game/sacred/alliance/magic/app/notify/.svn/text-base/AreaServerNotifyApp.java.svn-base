package sacred.alliance.magic.app.notify;

import sacred.alliance.magic.base.OsType;
import sacred.alliance.magic.core.Service;

public interface AreaServerNotifyApp extends Service{
	
	public String getServerId();
	
	public int getMaxLevel();
	
	/**
	 * 是否允许登录
	 * 根据分区服务器配置，判断是否允许模拟器登录
	 * @param osType 系统类型
	 * @return
	 */
	public boolean isAllowLogin(OsType osType);
	
	/**
	 * 是否允许渠道登录
	 * @param channelId 渠道ID
	 * @return
	 */
	public boolean isAllowChannelLogin(int channelId);
	
}
