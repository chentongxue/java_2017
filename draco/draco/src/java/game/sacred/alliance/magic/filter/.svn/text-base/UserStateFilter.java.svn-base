package sacred.alliance.magic.filter;

import java.util.concurrent.atomic.AtomicBoolean;

import com.game.draco.GameContext;
import com.game.draco.message.response.C0002_ErrorRespMessage;

import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.channel.ChannelSession;
import sacred.alliance.magic.core.filter.Filter;
import sacred.alliance.magic.core.filter.FilterChain;
import sacred.alliance.magic.util.SessionUtil;
import sacred.alliance.magic.vo.RoleInstance;

public class UserStateFilter implements Filter{
	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ActionContext context, FilterChain chain)
			throws Exception {
		AtomicBoolean lock = this.getLock(context);
		if(null == lock){
			chain.doFilter(context);
			return ;
		}
		if(!lock.compareAndSet(false, true)){
			C0002_ErrorRespMessage msg = new C0002_ErrorRespMessage();
			msg.setType((byte)0);
			msg.setInfo(GameContext.getI18n().getText(TextId.USER_STATE_ERROR));
			context.getSession().write(msg);
			return ;
		}
		try{
			chain.doFilter(context);
		}finally{
			lock.compareAndSet(true, false);
		}
	}
	
	private AtomicBoolean getLock(ActionContext context) {
		try {
			int commandId = context.getMessage().getCommandId();
			String strCmd = "," + GameContext.getParasConfig().getUserStateCommandId() + "," ;
			if(strCmd.indexOf("," + commandId + ",")<0){
				return null ;
			}
			ChannelSession session = context.getSession();
			if (null == session) {
				return null;
			}
			String userId = SessionUtil.getUserId(session);
			if(null == userId){
				return null ;
			}
			RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByUserId(userId);
			if(null == role){
				return null ;
			}
			return role.getStateLock();
		} catch (Exception ex) {
			return null;
		}
	}

	@Override
	public void init() {
		
	}

}
