package sacred.alliance.magic.util;

import sacred.alliance.magic.core.channel.ChannelSession;
import sacred.alliance.magic.vo.RoleEntity;

import com.game.draco.GameContext;
import com.game.draco.app.login.UserInfo;

public class SessionUtil {
	
	public static final String USER_INFO_KEY = "__USER_INFO__" ;
	
	public static RoleEntity getRoleEntity(ChannelSession session){
		String userId = getUserId(session);
		if(null == userId){
			return null ;
		}
		return GameContext.getOnlineCenter().getRoleInstanceByUserId(userId);
	}
	
	
	public static String getUserId(ChannelSession session){
		UserInfo userInfo = getUserInfo(session);
		return (null == userInfo)?null:userInfo.getUserId() ;
	}
	
	public static int getRoleId(ChannelSession session){
		UserInfo userInfo = getUserInfo(session);
		return (null == userInfo)?null:userInfo.getCurrRoleId() ;
	}
	
	public static UserInfo getUserInfo(ChannelSession session){
		Object obj = session.getAttribute(USER_INFO_KEY);
		if (null == obj) {
			return null;
		}
		return  (UserInfo)obj ;
	}
	
}
