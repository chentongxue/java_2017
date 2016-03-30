package sacred.alliance.magic.component.executor;

import com.game.draco.GameContext;

import sacred.alliance.magic.channel.ChannelEvent;
import sacred.alliance.magic.core.channel.ChannelSession;
import sacred.alliance.magic.vo.RoleInstance;

public class FactionChannelEventExecutor extends ChannelEventExecutor{
	private final static String USER_KEY = "userid" ;

	protected String __getKey(ChannelEvent entry){
		//´ÓChannelSessionÖÐ»ñµÃ
		ChannelSession session = entry.getSession();
		if(null == session){
			return "" ;
		}
		Object o = session.getAttribute(USER_KEY);
		if(null == o){
			return "" ;
		}
		String userId = o.toString();
		RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByUserId(userId);
		if(null == role){
			return "" ;
		}
		if(!role.hasUnion()) {
			return "" ;
		}
		return role.getUnionId();
	}
}