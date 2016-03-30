package sacred.alliance.magic.component.executor;

import com.game.draco.GameContext;
import sacred.alliance.magic.channel.ChannelEvent;
import sacred.alliance.magic.core.channel.ChannelSession;
import sacred.alliance.magic.util.SessionUtil;
import sacred.alliance.magic.vo.RoleInstance;

public class FactionChannelEventExecutor extends ChannelEventExecutor{

	protected String __getKey(ChannelEvent entry){
		ChannelSession session = entry.getSession();
		if(null == session){
			return "" ;
		}
		String userId = SessionUtil.getUserId(session);
		if(null == userId){
			return "" ;
		}
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