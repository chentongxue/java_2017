package sacred.alliance.magic.app.onlinecenter;
import java.util.Collection;

import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.core.channel.ChannelSession;
import sacred.alliance.magic.vo.RoleInstance;


public interface OnlineCenter extends Service {
	
	public void removeOnlineUser(String userId);
	
	public boolean isOnlineByUserId(String userId);
	
	public boolean isOnlineByRoleId(String roleId);
	
	public void addOnlineUser(RoleInstance roleInstance);
	
	public int onlineUserSize();
	
	RoleInstance getRoleInstanceByUserId(String userId);
	
	RoleInstance getRoleInstanceByRoleName(String roleName);
	
	RoleInstance getRoleInstanceByRoleId(String roleId);
	
	public Collection<RoleInstance> getAllOnlineRole();
	
	
	public void offline(ChannelSession session);
	
	public void offlineWithNetIO(ChannelSession session);
	
	public void saveOnline(RoleInstance roleInstance);
	
	public Long removeIoId(String passportId);
	
	public Long getIoId(String passportId);
	
	public void addIoId(String passportId,long ioId); 
	
	public void offline(RoleInstance role, boolean withIO);
	
	/**
	 * 发送定时入库消息
	 */
	public void timingWriteDBSendMsg(RoleInstance role);
	
	/**
	 * 定时入库操作，需入库的：role对象（顺带时长奖励，邮件补偿），goods入库，折扣活动，金条记录数
	 */
	public void timingWriteDB(RoleInstance role);
	
	public void closeIoSession(long ioId) ;

	public void kickAllRole();
}

